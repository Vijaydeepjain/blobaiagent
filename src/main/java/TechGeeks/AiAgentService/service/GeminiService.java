
package TechGeeks.AiAgentService.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public GeminiService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper) {

        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String generateContent(String prompt) {

        try {

            log.info("Sending prompt to Gemini 2.5 Flash");

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "responseMimeType", "application/json",
                            "temperature", 0.7
                    )
            );

            String response = webClient
                    .post()
                    .uri(GEMINI_URL + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> new RuntimeException(
                                            "Gemini API error: " + errorBody
                                    ))
                    )
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isBlank()) {
                throw new RuntimeException(
                        "Empty response received from Gemini"
                );
            }

            JsonNode root = objectMapper.readTree(response);

            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException(
                        "Gemini returned no candidates: " + response
                );
            }

            String generatedText = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText(null);

            if (generatedText == null || generatedText.isBlank()) {
                throw new RuntimeException(
                        "Gemini returned empty generated content"
                );
            }

            log.info(
                    "Gemini content generated successfully. Characters: {}",
                    generatedText.length()
            );

            return generatedText;

        } catch (Exception e) {

            log.error("Gemini content generation failed", e);

            throw new RuntimeException(
                    "Failed to generate content using Gemini", e
            );
        }
    }
}
