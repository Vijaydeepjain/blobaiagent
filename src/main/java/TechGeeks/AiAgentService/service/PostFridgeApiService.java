package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.dto.PostFridgeDtos.*;
import TechGeeks.AiAgentService.enums.BotConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFridgeApiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String BASE_URL =
            "https://blogpostbackend-pzgx.onrender.com";

    // 1. Login and fetch JWT
    public String fetchToken(BotConfig bot) {

        LoginRequest request = new LoginRequest(
                "PASSWORD",
                bot.getEmail(),
                bot.getPassword()
        );

        LoginResponse response = webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .block();

        if (response == null || response.token() == null
                || response.token().isBlank()) {
            throw new RuntimeException("Unable to fetch token for " + bot.name());
        }

        log.info("JWT fetched for bot: {}", bot.name());

        return response.token();
    }

    // 1. Fetch presigned upload URL
    public UploadUrlResponse fetchUploadUrl(String jwt) {

        UploadUrlResponse response = webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/files/upload-url")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .bodyToMono(UploadUrlResponse.class)
                .block();

        if (response == null
                || response.uploadUrl() == null
                || response.fileKey() == null
                || response.token() == null) {
            throw new RuntimeException("Invalid upload URL API response");
        }

        return response;
    }


    // 2. Upload image bytes to S3 using presigned URL
    public void uploadImage(String uploadUrl, byte[] imageBytes) {

        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image bytes are empty");
        }

        HttpURLConnection connection = null;

        try {
            URL url = new URL(uploadUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setFixedLengthStreamingMode(imageBytes.length);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(imageBytes);
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {

                InputStream errorStream = connection.getErrorStream();

                String errorResponse = errorStream != null
                        ? new String(errorStream.readAllBytes(), StandardCharsets.UTF_8)
                        : "No error response";

                log.error("S3 HTTP Status: {}", responseCode);
                log.error("S3 Error Response: {}", errorResponse);

                throw new RuntimeException(
                        "S3 upload failed with status: " + responseCode
                );
            }

            log.info("Image uploaded successfully to S3");

        } catch (Exception e) {
            log.error("S3 image upload failed", e);
            throw new RuntimeException("S3 upload failed", e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // 4. Create PostFridge post
    public void createPost(String jwt, CreatePostRequest request) {

        webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/post/createpost")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Post created successfully");
    }

    // 5. Complete publishing flow
    public void publishPost(
            BotConfig bot,
            String generatedResponse,
            byte[] generatedImage,
            String languageCode
    ) {

        try {
            JsonNode json = objectMapper.readTree(generatedResponse);

            // Poetry prompt returns poemTitle and poemHtml.
            String headline = json.hasNonNull("headline")
                    ? json.get("headline").asText()
                    : json.path("poemTitle").asText();

            String body = json.hasNonNull("bodyHtml")
                    ? json.get("bodyHtml").asText()
                    : json.path("poemHtml").asText();

            if (headline.isBlank() || body.isBlank()) {
                throw new IllegalArgumentException(
                        "Gemini response is missing headline/body content"
                );
            }

            // Fetch bot's JWT
            String jwt = fetchToken(bot);

            List<PostFile> files = List.of();
            String mediaType = "IMAGE";

            // Upload only when an image was generated.
            if (generatedImage != null && generatedImage.length > 0) {

                UploadUrlResponse uploadData = fetchUploadUrl(jwt);

                uploadImage(uploadData.uploadUrl(), generatedImage);

                PostFile postFile = new PostFile(
                        uploadData.fileKey(),
                        uploadData.token()
                );

                files = List.of(postFile, postFile);

                mediaType = "IMAGE";
            }

            CreatePostRequest postRequest = new CreatePostRequest(
                    headline,
                    body,
                    files,
                    List.of(bot.getCategory().name()),
                    mediaType,
                    "PUBLIC",
                    languageCode == null ? "" : languageCode
            );

            createPost(jwt, postRequest);

            log.info(
                    "Post published successfully. Bot: {}, Category: {}",
                    bot.name(),
                    bot.getCategory()
            );

        } catch (Exception e) {
            log.error("Post publishing failed for bot: {}", bot.name(), e);
            throw new RuntimeException("PostFridge publishing failed", e);
        }
    }
}