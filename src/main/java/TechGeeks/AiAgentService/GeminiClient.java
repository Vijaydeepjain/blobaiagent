package TechGeeks.AiAgentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, String> generateContent(String category) {

        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        String prompt = """
                Act as a professional content writer.

                Write a high-quality LinkedIn post about %s.

                Guidelines:
                - Tone: professional and engaging
                - Length: 120–150 words
                - Keep it informative and easy to read
                - Do NOT copy content; generate original
                - Avoid multiple options

                Return ONLY raw JSON.
                Do NOT wrap in markdown or quotes.

                Format:
                {
                  "headline": "Short catchy headline",
                  "body": "Engaging content"
                }
                """.formatted(category);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        Map response = webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // ===== SAFE EXTRACTION =====
        if (response == null || response.get("candidates") == null) {
            throw new RuntimeException("Invalid response from Gemini");
        }

        List candidates = (List) response.get("candidates");
        Map first = (Map) candidates.get(0);
        Map content = (Map) first.get("content");
        List parts = (List) content.get("parts");

        String raw = (String) ((Map) parts.get(0)).get("text");

        // ===== CLEAN RESPONSE (defensive) =====
        raw = raw.replace("```json", "")
                .replace("```", "")
                .trim();

        // extract JSON if extra text present
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");

        if (start != -1 && end != -1) {
            raw = raw.substring(start, end + 1);
        }

        try {
            return mapper.readValue(raw, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + raw, e);
        }
    }
}