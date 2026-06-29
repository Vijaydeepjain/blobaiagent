package TechGeeks.AiAgentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
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

        String prompt = buildPrompt(category);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );
        System.out.println(prompt.length());
//        Map response = webClient.post()
//                .uri(url)
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();



        Map response = webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.out.println("GOOGLE ERROR = " + errorBody);
                                    return Mono.error(new RuntimeException(errorBody));
                                })
                )
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

    private String buildPrompt(String category) {

        switch (category) {

            case "PERSONAL_STORIES":
                return "Generate a heartfelt personal story.\n\n" +

                        "Writing Style:\n" +
                        "- First person narration.\n" +
                        "- Natural and conversational English.\n" +
                        "- Emotional but realistic.\n" +
                        "- Include small details and conversations.\n" +
                        "- Show emotions through actions.\n" +
                        "- Avoid dramatic or poetic language.\n\n" +

                        "Requirements:\n" +
                        "- Headline less than 80 characters.\n" +
                        "- Body between 400 and 700 words.\n" +
                        "- Beginning, middle and ending.\n" +
                        "- End with a reflection or realization.\n\n" +

                        "Avoid:\n" +
                        "- AI phrases.\n" +
                        "- Words like tapestry, soul, timeless, cherish, unforgettable, embrace and vibrant.\n" +
                        "- Cliches.\n" +
                        "- Hashtags and emojis.\n\n" +

                        "Body Format:\n" +
                        "- Return body as valid HTML.\n" +
                        "- Use only <p>, <h2>, <blockquote> and <ul> tags.\n" +
                        "- Use multiple short paragraphs.\n\n" +

                        "ImagePrompt:\n" +
                        "- Realistic photography.\n" +
                        "- Warm cinematic lighting.\n" +
                        "- Highly detailed.\n" +
                        "- Describe one important scene from the story.\n\n" +

                        "Return ONLY valid JSON:\n" +
                        "{\n" +
                        "\"headline\":\"\",\n" +
                        "\"body\":\"\",\n" +
                        "\"imagePrompt\":\"\"\n" +
                        "}";


            case "TRAVEL":
                return "Generate a travel experience story.\n\n" +

                        "Writing Style:\n" +
                        "- Warm and immersive.\n" +
                        "- Write like a real traveler sharing memories.\n" +
                        "- Use simple natural English.\n" +
                        "- Include food, culture and people.\n" +
                        "- Include one memorable moment.\n" +
                        "- Include small conversations if appropriate.\n\n" +
                        "- Include small inconveniences or funny moments occasionally.\n" +
                        "- Make experiences realistic, not perfect.\n" +

                        "Requirements:\n" +
                        "- Headline less than 80 characters.\n" +
                        "- Body between 400 and 700 words.\n" +
                        "- Inspire readers to travel.\n" +
                        "- Keep paragraphs short.\n\n" +

                        "Avoid:\n" +
                        "- Poetic language.\n" +
                        "- AI phrases.\n" +
                        "- Words like tapestry, whisper, soul, timeless, cherish and unforgettable.\n" +
                        "- Cliches.\n" +
                        "- Hashtags and emojis.\n\n" +


                        "Body Format:\n" +
                        "- Return body as valid HTML.\n" +
                        "- Use only <p>, <h2>, <blockquote> and <ul> tags.\n\n" +

                        "ImagePrompt:\n" +
                        "- Ultra realistic travel photography.\n" +
                        "- Golden hour lighting.\n" +
                        "- Cinematic composition.\n" +
                        "- Vibrant colors.\n" +
                        "- Highly detailed.\n" +
                        "- Describe one scene from the story.\n\n" +

                        "Return ONLY valid JSON:\n" +
                        "{\n" +
                        "\"headline\":\"\",\n" +
                        "\"body\":\"\",\n" +
                        "\"imagePrompt\":\"\"\n" +
                        "}";


            case "FICTION":
                return "Generate an original fiction story.\n\n" +

                        "Randomly choose one genre:\n" +
                        "- Mystery\n" +
                        "- Romance\n" +
                        "- Fantasy\n" +
                        "- Horror\n" +
                        "- Sci-Fi\n" +
                        "- Slice of Life\n\n" +

                        "Writing Style:\n" +
                        "- Strong characters.\n" +
                        "- Natural dialogue.\n" +
                        "- Rich descriptions.\n" +
                        "- Build suspense gradually.\n" +
                        "- Emotional and immersive.\n" +
                        "- Memorable ending.\n\n" +

                        "Requirements:\n" +
                        "- Headline less than 80 characters.\n" +
                        "- Body between 700 and 1200 words.\n" +
                        "- Story should feel like a short chapter.\n\n" +

                        "Avoid:\n" +
                        "- AI phrases.\n" +
                        "- Cliches.\n" +
                        "- Repetitive sentences.\n" +
                        "- Hashtags and emojis.\n\n" +

                        "Body Format:\n" +
                        "- Return body as valid HTML.\n" +
                        "- Use only <p>, <h2>, <blockquote> and <ul> tags.\n" +
                        "- Use multiple short paragraphs.\n\n" +

                        "ImagePrompt:\n" +
                        "- Studio Ghibli style illustration.\n" +
                        "- Dreamy atmosphere.\n" +
                        "- Soft golden lighting.\n" +
                        "- Beautiful scenery.\n" +
                        "- Vibrant colors.\n" +
                        "- Detailed anime art.\n" +
                        "- Cinematic composition.\n" +
                        "- The image should look like a frame from a Studio Ghibli movie.\n" +
                        "- Describe the exact scene from the story.\n\n" +

                        "Return ONLY valid JSON:\n" +
                        "{\n" +
                        "\"headline\":\"\",\n" +
                        "\"body\":\"\",\n" +
                        "\"imagePrompt\":\"\"\n" +
                        "}";

            case "HEALTH":
                return "Generate a high-quality health and wellness article.\n\n" +

                        "Writing Style:\n" +
                        "- Professional but easy to understand.\n" +
                        "- Friendly and trustworthy.\n" +
                        "- Use simple natural English.\n" +
                        "- Explain concepts clearly without medical jargon.\n" +
                        "- Focus on practical advice readers can apply.\n" +
                        "- Use evidence-based general health information.\n\n" +

                        "Topics:\n" +
                        "- Nutrition\n" +
                        "- Exercise and Fitness\n" +
                        "- Sleep\n" +
                        "- Mental Well-being\n" +
                        "- Healthy Habits\n" +
                        "- Preventive Health\n" +
                        "- Hydration\n" +
                        "- Workplace Health\n\n" +

                        "Requirements:\n" +
                        "- Headline less than 80 characters.\n" +
                        "- Body between 500 and 800 words.\n" +
                        "- Include an engaging introduction.\n" +
                        "- Explain the topic clearly.\n" +
                        "- Provide practical tips.\n" +
                        "- End with a short conclusion encouraging healthy habits.\n\n" +

                        "Avoid:\n" +
                        "- AI phrases.\n" +
                        "- Medical diagnosis.\n" +
                        "- Claims of guaranteed results.\n" +
                        "- Fear-mongering.\n" +
                        "- Words like tapestry, transformative, miracle, revolutionary, life-changing and magical.\n" +
                        "- Cliches.\n" +
                        "- Hashtags and emojis.\n\n" +

                        "Body Format:\n" +
                        "- Return body as valid HTML.\n" +
                        "- Use only <p>, <h2>, <blockquote> and <ul> tags.\n" +
                        "- Use multiple short paragraphs.\n" +
                        "- Use bullet lists where appropriate.\n\n" +

                        "ImagePrompt:\n" +
                        "- Ultra realistic professional health photography.\n" +
                        "- Bright natural lighting.\n" +
                        "- Clean modern environment.\n" +
                        "- Healthy people performing the activity discussed.\n" +
                        "- Highly detailed.\n" +
                        "- Photorealistic.\n" +
                        "- No text, logos or watermarks.\n" +
                        "- Simple composition suitable for a blog cover.\n" +
                        "- Optimize composition so the generated JPEG image is likely to remain under 500 KB while maintaining good visual quality.\n\n" +

                        "Return ONLY valid JSON:\n" +
                        "{\n" +
                        "\"headline\":\"\",\n" +
                        "\"body\":\"\",\n" +
                        "\"imagePrompt\":\"\"\n" +
                        "}";
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }
//    private String buildPrompt(String category) {
//
//        switch (category) {
//
//            case "PERSONAL_STORIES":
//                return "Generate a heartfelt personal experience post.\n\n" +
//
//                        "Writing Style:\n" +
//                        "- First person narration.\n" +
//                        "- Emotional and relatable.\n" +
//                        "- Natural human language.\n" +
//                        "- Include thoughts, feelings and reflections.\n" +
//                        "- Make it feel like someone sharing a memorable life experience.\n\n" +
//
//                        "Requirements:\n" +
//                        "- Headline should be meaningful and catchy.\n" +
//                        "- Headline should be less than 80 characters.\n" +
//                        "- Body should be between 350 and 600 words.\n" +
//                        "- Include beginning, middle and ending.\n" +
//                        "- Story should leave readers with emotions or a lesson.\n" +
//                        "- Use proper paragraphs.\n\n" +
//
//                        "Special Instructions:\n" +
//                        "- Include conversations if suitable.\n" +
//                        "- Show emotions through actions and dialogue.\n" +
//                        "- Make emotions realistic.\n" +
//                        "- End with a reflection or realization.\n" +
//                        "- Story should feel like something a person would share with friends.\n\n" +
//
//                        "ImagePrompt:\n" +
//                        "- Cinematic realistic photography.\n" +
//                        "- Warm lighting.\n" +
//                        "- Highly detailed.\n" +
//                        "- Professional photography style.\n" +
//                        "- Describe one key scene from the story.\n\n" +
//
//                        "Additional Instructions:\n" +
//                        "- Content should feel authentic and written by a real person.\n" +
//                        "- Avoid sounding AI generated.\n" +
//                        "- Avoid generic AI phrases.\n" +
//                        "- Avoid overusing words like journey, unforgettable, delve, embrace, vibrant and cherish.\n" +
//                        "- Avoid clichés.\n" +
//                        "- Do not include hashtags.\n" +
//                        "- Do not include emojis.\n" +
//                        "- Never mention being an AI.\n\n" +
//
//                        "STRICT RULES:\n" +
//                        "- Output MUST be valid JSON.\n" +
//                        "- Do NOT use markdown.\n" +
//                        "- Do NOT add explanations.\n\n" +
//
//                        "Return exactly this format:\n" +
//                        "{\n" +
//                        "\"headline\":\"\",\n" +
//                        "\"body\":\"\",\n" +
//                        "\"imagePrompt\":\"\"\n" +
//                        "}";
//
//
//            case "TRAVEL":
//                return "Generate a beautiful travel experience post.\n\n" +
//
//                        "Writing Style:\n" +
//                        "- Warm and immersive.\n" +
//                        "- Describe places, food, people and culture.\n" +
//                        "- Include emotions and memorable moments.\n" +
//                        "- Make readers feel like they are travelling with the writer.\n\n" +
//
//                        "Requirements:\n" +
//                        "- Headline should be attractive and blog style.\n" +
//                        "- Headline should be less than 80 characters.\n" +
//                        "- Body should be between 350 and 600 words.\n" +
//                        "- Use proper paragraphs.\n" +
//                        "- Inspire readers to visit the place.\n\n" +
//
//                        "Special Instructions:\n" +
//                        "- Include local food and culture.\n" +
//                        "- Describe sounds, smells and atmosphere.\n" +
//                        "- Include one memorable moment.\n" +
//                        "- Make readers feel present there.\n\n" +
//
//                        "ImagePrompt:\n" +
//                        "- Ultra realistic travel photography.\n" +
//                        "- Golden hour lighting.\n" +
//                        "- Cinematic composition.\n" +
//                        "- Vibrant colors.\n" +
//                        "- Beautiful scenery.\n" +
//                        "- Highly detailed.\n\n" +
//
//                        "Additional Instructions:\n" +
//                        "- Content should feel authentic and written by a real person.\n" +
//                        "- Avoid sounding AI generated.\n" +
//                        "- Avoid generic AI phrases.\n" +
//                        "- Avoid clichés.\n" +
//                        "- Do not include hashtags.\n" +
//                        "- Do not include emojis.\n" +
//                        "- Never mention being an AI.\n\n" +
//
//                        "STRICT RULES:\n" +
//                        "- Output MUST be valid JSON.\n" +
//                        "- Do NOT use markdown.\n" +
//                        "- Do NOT add explanations.\n\n" +
//
//                        "Return exactly this format:\n" +
//                        "{\n" +
//                        "\"headline\":\"\",\n" +
//                        "\"body\":\"\",\n" +
//                        "\"imagePrompt\":\"\"\n" +
//                        "}";
//
//
//            case "FICTION":
//                return "Generate an original short fiction story.\n\n" +
//
//                        "Randomly choose one genre:\n" +
//                        "- Mystery\n" +
//                        "- Fantasy\n" +
//                        "- Romance\n" +
//                        "- Horror\n" +
//                        "- Sci-Fi\n" +
//                        "- Slice of Life\n\n" +
//
//                        "Writing Style:\n" +
//                        "- Rich descriptions.\n" +
//                        "- Emotional storytelling.\n" +
//                        "- Strong characters.\n" +
//                        "- Include natural dialogues.\n" +
//                        "- Build suspense gradually.\n" +
//                        "- End with a memorable ending.\n\n" +
//
//                        "Requirements:\n" +
//                        "- Headline should feel like a novel title.\n" +
//                        "- Headline should be less than 80 characters.\n" +
//                        "- Body should be between 600 and 900 words.\n" +
//                        "- Use proper paragraphs.\n" +
//                        "- Story should feel like a chapter from a book.\n\n" +
//
//                        "Special Instructions:\n" +
//                        "- Characters should feel believable.\n" +
//                        "- Create emotional attachment to the characters.\n" +
//                        "- Include vivid scene descriptions.\n" +
//                        "- Avoid predictable endings.\n" +
//                        "- End with a powerful final paragraph.\n\n" +
//
//                        "ImagePrompt:\n" +
//                        "- Studio Ghibli style illustration.\n" +
//                        "- Dreamy atmosphere.\n" +
//                        "- Soft golden lighting.\n" +
//                        "- Beautiful scenery.\n" +
//                        "- Highly detailed anime art.\n" +
//                        "- Vibrant colors.\n" +
//                        "- Cinematic composition.\n" +
//                        "- Whimsical mood.\n" +
//                        "- The image should feel like a frame from a Studio Ghibli movie.\n" +
//                        "- Describe the exact scene from the story.\n\n" +
//
//                        "Additional Instructions:\n" +
//                        "- Content should feel authentic.\n" +
//                        "- Avoid sounding AI generated.\n" +
//                        "- Avoid generic AI phrases.\n" +
//                        "- Show emotions through actions and dialogue.\n" +
//                        "- Avoid clichés.\n" +
//                        "- Do not include hashtags.\n" +
//                        "- Do not include emojis.\n" +
//                        "- Never mention being an AI.\n\n" +
//
//                        "STRICT RULES:\n" +
//                        "- Output MUST be valid JSON.\n" +
//                        "- Do NOT use markdown.\n" +
//                        "- Do NOT add explanations.\n\n" +
//
//                        "Return exactly this format:\n" +
//                        "{\n" +
//                        "\"headline\":\"\",\n" +
//                        "\"body\":\"\",\n" +
//                        "\"imagePrompt\":\"\"\n" +
//                        "}";
//
//            default:
//                throw new IllegalArgumentException("Unknown category: " + category);
//        }
//    }
}