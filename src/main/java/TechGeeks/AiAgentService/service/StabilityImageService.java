package TechGeeks.AiAgentService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class StabilityImageService {

    @Value("${stability.api.key}")
    private String apiKey;

    private static final String STABILITY_URL =
            "https://api.stability.ai/v2beta/stable-image/generate/sd3";

    public byte[] generateImage(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Image prompt cannot be empty");
        }

        HttpURLConnection conn = null;

        try {
            URL url = new URL(STABILITY_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30_000);
            conn.setReadTimeout(120_000);

            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Accept", "image/*");

            String boundary = "----Boundary" + System.currentTimeMillis();

            conn.setRequestProperty(
                    "Content-Type",
                    "multipart/form-data; boundary=" + boundary
            );

            try (OutputStream os = conn.getOutputStream()) {

                writeFormField(os, boundary, "prompt", prompt);
                writeFormField(os, boundary, "output_format", "jpeg");

                os.write(("--" + boundary + "--\r\n")
                        .getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {

                String errorMessage = "";

                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        errorMessage = new String(
                                errorStream.readAllBytes(),
                                StandardCharsets.UTF_8
                        );
                    }
                }

                throw new RuntimeException(
                        "Stability API failed. Status: "
                                + responseCode + ", Response: " + errorMessage
                );
            }

            try (InputStream inputStream = conn.getInputStream()) {
                byte[] imageBytes = inputStream.readAllBytes();

                log.info("Stability image generated successfully. Size: {} bytes",
                        imageBytes.length);

                return imageBytes;
            }

        } catch (Exception e) {
            log.error("Failed to generate image using Stability AI", e);
            throw new RuntimeException("Failed to generate image", e);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void writeFormField(
            OutputStream os,
            String boundary,
            String fieldName,
            String value
    ) throws Exception {

        String part = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\""
                + fieldName + "\"\r\n\r\n"
                + value + "\r\n";

        os.write(part.getBytes(StandardCharsets.UTF_8));
    }
}