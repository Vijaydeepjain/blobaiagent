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
import java.util.concurrent.ThreadLocalRandom;

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

            case "TECH":
                String today = java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")
                ); // Outputs: "August 29, 2026"

                return "Generate an insightful technology article covering the latest developments, breakthroughs, or product announcements grounded in the current tech landscape as of " + today + ".\n\n" +

                        "Writing Style:\n" +
                        "- Objective, analytical, and authoritative tone.\n" +
                        "- Deep technical context without unnecessary jargon.\n" +
                        "- Focus on real-world engineering, developer takeaways, and industry impact.\n" +
                        "- Concise, active voice.\n\n" +

                        "Requirements:\n" +
                        "- Headline less than 80 characters.\n" +
                        "- Body between 400 and 700 words.\n" +
                        "- Anchor to events, hardware previews, AI developments, or infrastructure news around " + today + ".\n" +
                        "- Structure: Current Context/Announcement, Technical Breakdown, and Strategic Impact.\n\n" +

                        "Avoid:\n" +
                        "- AI buzzwords (e.g., tapestry, delve, game-changer, revolutionary, multifaceted, unlock).\n" +
                        "- Speculative claims without architectural or factual grounding.\n" +
                        "- Hashtags and emojis.\n\n" +

                        "Body Format:\n" +
                        "- Return body as valid HTML.\n" +
                        "- Use only <p>, <h2>, <h3>, <code>, <pre>, <blockquote>, <ul>, and <li> tags.\n" +
                        "- Keep paragraphs short and scannable.\n\n" +

                        "ImagePrompt:\n" +
                        "- Modern tech aesthetic or high-detail conceptual hardware/architecture render.\n" +
                        "- Clean studio lighting, minimal, sharp focus.\n" +
                        "- Photorealistic or sleek isometric style.\n" +
                        "- No distorted text or generic glowing blue circuitry.\n\n" +

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

                String healthTopic = getRandomHealthTopic();

                return "Write a high-quality health and wellness article.\n\n" +

                        "TOPIC:\n" +
                        healthTopic + "\n\n" +

                        "STYLE:\n" +
                        "- Professional, friendly, trustworthy, and easy to understand.\n" +
                        "- Use simple natural English and avoid unnecessary medical jargon.\n" +
                        "- Write like a human health journalist, not an AI.\n" +
                        "- Give practical advice readers can actually use.\n" +
                        "- Explain the problem, useful solutions, and when professional medical advice may be appropriate.\n\n" +

                        "HEALTH RULES:\n" +
                        "- Focus only on the selected topic.\n" +
                        "- Be evidence-based and medically responsible.\n" +
                        "- Never diagnose the reader.\n" +
                        "- Never promise guaranteed results or claim a food, exercise, or habit can cure a disease.\n" +
                        "- Do not recommend starting, stopping, or changing prescription medication.\n" +
                        "- For health conditions, explain common risk factors, symptoms, practical habits, and when to seek professional help when relevant.\n\n" +

                        "FOOD TOPICS:\n" +
                        "- Explain important nutrients and realistic benefits.\n" +
                        "- Explain practical ways to eat or prepare the food.\n" +
                        "- Mention who should limit or avoid it when genuinely relevant and explain why.\n" +
                        "- Do not automatically connect every food to diabetes.\n\n" +

                        "ARTICLE:\n" +
                        "- Headline under 80 characters.\n" +
                        "- Body 500-800 words.\n" +
                        "- Engaging introduction.\n" +
                        "- Use useful <h2> sections, short <p> paragraphs, and <ul> lists.\n" +
                        "- End with a short practical conclusion.\n" +
                        "- Body must use ONLY <p>, <h2>, <blockquote>, and <ul> tags.\n\n" +

                        "AVOID:\n" +
                        "- AI-sounding language, filler, clichés, fear-mongering, miracle claims, and exaggerated promises.\n" +
                        "- Words such as tapestry, transformative, miracle, revolutionary, life-changing, magical, delve, journey, and game-changing.\n" +
                        "- Hashtags, emojis, Markdown, and unnecessary statistics.\n\n" +

                        "IMAGE:\n" +
                        "- Create an authentic photorealistic editorial photograph related to the topic.\n" +
                        "- Make it look like a real professional photograph, not AI art or generic stock photography.\n" +
                        "- Natural lighting, realistic people, skin, hands, body proportions, environment, food texture, and imperfections.\n" +
                        "- Prefer candid, everyday situations over staged poses.\n" +
                        "- Match the person's age and setting to the topic.\n" +
                        "- No text, logos, watermarks, infographics, medical icons, glowing organs, or artificial effects.\n" +
                        "- Blog-cover composition with some natural negative space.\n" +
                        "- Optimize for a JPEG under 500 KB while maintaining good quality.\n\n" +

                        "RETURN ONLY VALID JSON:\n" +
                        "{\n" +
                        "\"headline\":\"\",\n" +
                        "\"body\":\"\",\n" +
                        "\"imagePrompt\":\"\"\n" +
                        "}";

            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }

    private String getRandomHealthTopic() {

        List<String> topics = List.of(

                // =========================
                // DIABETES & ENDOCRINOLOGY
                // =========================
                "How walking after meals may help blood sugar control",
                "Understanding insulin resistance in simple terms",
                "Early lifestyle habits that can support healthy blood sugar",
                "How fiber can affect blood sugar levels",
                "Why sleep matters for blood sugar management",
                "How stress can affect blood glucose",
                "Prediabetes: lifestyle changes worth making early",
                "Understanding HbA1c in simple language",
                "Healthy breakfast ideas for people watching their blood sugar",
                "Common diabetes mistakes people make with food",
                "How regular exercise supports blood sugar management",
                "Why foot care matters for people with diabetes",
                "Diabetes and eye health: why regular eye checks matter",
                "How diabetes can affect kidney health",
                "Understanding low blood sugar and common warning signs",
                "Metabolic syndrome and everyday lifestyle habits",
                "Understanding thyroid health",
                "Common signs of an underactive thyroid",
                "How thyroid health can affect energy and weight",
                "PCOS and everyday lifestyle habits",

                // =========================
                // HEART & BLOOD PRESSURE
                // =========================
                "Simple habits that support healthy blood pressure",
                "How too much sodium can affect blood pressure",
                "Why walking is good for heart health",
                "Understanding LDL and HDL cholesterol",
                "What triglycerides mean for your health",
                "How sleep affects heart health",
                "How stress affects cardiovascular health",
                "Why regular exercise supports a healthy heart",
                "How weight management can support heart health",
                "Everyday foods that provide heart-friendly nutrients",
                "Understanding resting heart rate",
                "How smoking affects cardiovascular health",
                "Why family history matters for heart disease risk",
                "Heart health habits for people over 40",
                "Warning signs of a possible heart attack",
                "Understanding atrial fibrillation",
                "How physical activity supports healthy circulation",

                // =========================
                // ORTHOPEDICS
                // =========================
                "Why your back may hurt after sitting all day",
                "Simple habits that support a healthy spine",
                "Common causes of knee pain",
                "How to protect your knees during exercise",
                "Why muscle strength matters for joint health",
                "Understanding osteoarthritis",
                "How posture affects neck and back discomfort",
                "Simple ways to improve mobility as you age",
                "Why balance exercises matter for older adults",
                "How strength training supports bone health",
                "Understanding osteoporosis",
                "How to reduce the risk of falls as you get older",
                "Common causes of shoulder pain",
                "Understanding frozen shoulder",
                "What can contribute to tennis elbow",
                "How prolonged sitting affects your muscles and joints",
                "Why stretching can help maintain flexibility",
                "How to recover after a minor muscle strain",
                "Why strong legs matter as you age",
                "How footwear can affect foot comfort",

                // =========================
                // NEUROLOGY & BRAIN HEALTH
                // =========================
                "How sleep affects memory and concentration",
                "Simple habits that support brain health",
                "Why regular exercise benefits the brain",
                "Common causes of headaches",
                "Understanding migraine triggers",
                "Why stress can affect concentration",
                "What brain fog can feel like and common causes",
                "How excessive screen time can affect concentration",
                "Understanding peripheral neuropathy",
                "How diabetes can affect nerve health",
                "Stroke prevention habits worth knowing",
                "Understanding dizziness and common causes",
                "Understanding vertigo",
                "How healthy aging can support cognitive health",
                "Why social connection matters for brain health",
                "How nutrition supports brain function",
                "Why sleep is important for cognitive performance",
                "Healthy habits that support memory as you age",

                // =========================
                // RESPIRATORY HEALTH
                // =========================
                "Simple habits that support lung health",
                "Understanding asthma triggers",
                "How air pollution can affect respiratory health",
                "Why secondhand smoke is harmful",
                "How exercise supports lung function",
                "Understanding sleep apnea",
                "Why some people snore more than others",
                "When a persistent cough deserves medical attention",
                "How indoor air quality affects breathing",
                "Seasonal allergies and breathing problems",
                "How smoking damages the lungs",
                "Healthy habits for people with asthma",

                // =========================
                // DIGESTIVE HEALTH
                // =========================
                "Simple habits that support better digestion",
                "Why fiber matters for digestive health",
                "Common causes of bloating",
                "Understanding constipation",
                "How hydration affects digestion",
                "Understanding acid reflux",
                "Foods that may trigger heartburn",
                "Understanding the gut microbiome",
                "Probiotics and digestive health",
                "Prebiotics and gut health",
                "Understanding lactose intolerance",
                "How stress can affect digestion",
                "Healthy habits for better bowel regularity",
                "Understanding irritable bowel syndrome",
                "How eating too quickly can affect digestion",

                // =========================
                // KIDNEY HEALTH
                // =========================
                "Simple habits that support kidney health",
                "How high blood pressure can affect the kidneys",
                "How diabetes can affect kidney health",
                "Understanding kidney stones",
                "How hydration supports kidney function",
                "Why too much sodium can be hard on the kidneys",
                "Understanding chronic kidney disease",
                "Why regular health checks matter for kidney health",
                "How healthy weight management supports kidney health",

                // =========================
                // LIVER HEALTH
                // =========================
                "Simple habits that support liver health",
                "Understanding fatty liver disease",
                "How weight can affect liver health",
                "How diabetes is connected to fatty liver",
                "How alcohol can affect the liver",
                "Foods that can support a balanced diet for liver health",
                "Why regular exercise matters for liver health",
                "Understanding liver function tests",

                // =========================
                // EYE HEALTH
                // =========================
                "How to reduce digital eye strain",
                "Simple habits that support healthy vision",
                "Why regular eye examinations matter",
                "Understanding dry eyes",
                "How screen time can affect your eyes",
                "Understanding cataracts",
                "Understanding glaucoma",
                "How diabetes can affect eye health",
                "Why sunglasses matter for eye health",
                "Eye health habits for older adults",
                "How nutrition supports eye health",

                // =========================
                // SKIN & HAIR
                // =========================
                "Simple habits that support healthy skin",
                "Understanding dry skin and common causes",
                "How to protect your skin from sun exposure",
                "Understanding acne and common triggers",
                "Simple habits for healthier scalp and hair",
                "Understanding dandruff",
                "Common causes of hair loss",
                "How hydration affects skin",
                "How sleep affects skin health",
                "Understanding eczema",
                "Understanding psoriasis",
                "Why sunscreen matters throughout the year",

                // =========================
                // DENTAL HEALTH
                // =========================
                "Simple habits that protect your teeth",
                "Why flossing matters for gum health",
                "Understanding gum disease",
                "How sugar affects dental health",
                "Why regular dental checkups matter",
                "Common causes of tooth sensitivity",
                "How dry mouth can affect oral health",
                "The connection between diabetes and oral health",
                "How acidic drinks can affect tooth enamel",
                "Healthy dental habits for older adults",

                // =========================
                // MENTAL HEALTH
                // =========================
                "Simple ways to manage everyday stress",
                "How exercise can support mental well-being",
                "Why sleep matters for emotional health",
                "Understanding workplace burnout",
                "How social connection supports mental well-being",
                "Simple mindfulness habits for busy people",
                "How excessive screen time can affect mental well-being",
                "Understanding loneliness and social health",
                "Healthy ways to manage work-related stress",
                "Why taking regular breaks can improve mental well-being",
                "How physical activity can improve mood",
                "When ongoing stress may require professional support",

                // =========================
                // SLEEP
                // =========================
                "Simple habits that can improve sleep quality",
                "Why a consistent sleep schedule matters",
                "How evening screen time can affect sleep",
                "How caffeine affects sleep",
                "How exercise can improve sleep",
                "Understanding insomnia",
                "Why some people wake up tired",
                "Understanding sleep apnea",
                "How stress affects sleep",
                "Healthy sleep habits for older adults",
                "Healthy sleep habits for teenagers",
                "How shift work can affect sleep",

                // =========================
                // WOMEN'S HEALTH
                // =========================
                "Understanding menstrual health",
                "Common causes of period pain",
                "Understanding PMS",
                "PCOS and lifestyle habits",
                "Nutrition during pregnancy",
                "Understanding menopause",
                "How menopause can affect sleep",
                "Menopause and bone health",
                "Menopause and heart health",
                "Why iron matters for women's health",
                "Breast health awareness",
                "Women's heart health",
                "Women's mental health",

                // =========================
                // MEN'S HEALTH
                // =========================
                "Simple habits that support men's heart health",
                "Understanding prostate health",
                "Why strength training matters for men",
                "Men's mental health and stress",
                "Healthy weight management for men",
                "Understanding common causes of hair loss",
                "Why sleep matters for men's health",
                "How nutrition supports men's health",

                // =========================
                // HEALTHY AGING
                // =========================
                "Why muscle strength becomes important after 50",
                "How to maintain mobility as you age",
                "Simple exercises for adults over 60",
                "How to reduce fall risk as you get older",
                "Why protein matters for older adults",
                "How hydration needs can change with age",
                "How to support bone health after 50",
                "Healthy habits that support independence in older age",
                "How social connection supports healthy aging",
                "Simple ways to protect cognitive health as you age",

                // =========================
                // FITNESS
                // =========================
                "Why walking is an underrated form of exercise",
                "Benefits of strength training for beginners",
                "Simple exercises for people who sit all day",
                "How to start exercising after a long break",
                "Why warm-ups matter before exercise",
                "Why recovery days matter",
                "How stretching supports mobility",
                "How exercise intensity affects fitness",
                "Simple ways to become more active during the day",
                "Exercise habits for people over 50",
                "How strength training supports healthy aging",
                "How regular exercise supports metabolic health",

                // =========================
                // FOOD & NUTRITION
                // =========================
                "Health benefits of oats and who should be cautious",
                "Nutritional benefits of eggs",
                "What makes almonds a nutritious snack",
                "Health benefits of spinach",
                "Is yogurt a useful part of a balanced diet?",
                "What nutrients do lentils provide?",
                "Health benefits of sweet potatoes",
                "Why berries can be a useful addition to the diet",
                "Nutritional benefits of pumpkin seeds",
                "What makes chickpeas a nutritious food?",
                "Health benefits of walnuts",
                "What nutrients does avocado provide?",
                "Is brown rice healthier than white rice?",
                "Health benefits of apples",
                "What makes guava a nutritious fruit?",
                "Nutritional benefits of papaya",
                "Health benefits of carrots",
                "What makes broccoli a useful vegetable?",
                "Health benefits of tomatoes",
                "What nutrients do bananas provide?",
                "Health benefits of oranges",
                "Is paneer a good source of protein?",
                "What makes tofu a useful protein source?",
                "Health benefits of fish",
                "Why omega-3 fats matter",
                "Health benefits of chia seeds",
                "What makes flax seeds nutritious?",
                "Health benefits of chickpeas",
                "Is coconut healthy to eat regularly?",
                "What makes pomegranate nutritious?",
                "Health benefits of mushrooms",
                "Is potato a healthy food?",
                "Healthy ways to include rice in a balanced diet",
                "How fiber supports overall health",
                "Why protein matters in everyday nutrition",
                "Understanding healthy fats",
                "How added sugar affects health",
                "Why portion size matters",

                // =========================
                // HYDRATION
                // =========================
                "How much water does your body actually need?",
                "Signs that you may not be drinking enough water",
                "How hydration affects exercise performance",
                "Why hydration matters during hot weather",
                "How hydration supports digestion",
                "Hydration habits for older adults",
                "How caffeine affects hydration",

                // =========================
                // PREVENTIVE HEALTH
                // =========================
                "Why regular blood pressure checks matter",
                "Why cholesterol testing matters",
                "Why routine blood sugar testing can be useful",
                "Why regular dental checkups matter",
                "Why eye examinations are important",
                "Understanding preventive health checkups",
                "Why family medical history matters",
                "Healthy habits that reduce long-term health risks",
                "Why preventive care becomes more important with age",

                // =========================
                // WORKPLACE HEALTH
                // =========================
                "How sitting all day affects your body",
                "Simple desk exercises for office workers",
                "How to improve posture at your desk",
                "How screen time affects eye strain at work",
                "How workplace stress affects health",
                "Healthy snack ideas for the office",
                "Why taking walking breaks matters",
                "How remote work can affect physical activity",
                "How shift work affects sleep",
                "Simple ways to stay active during a workday"
        );

        return topics.get(ThreadLocalRandom.current().nextInt(topics.size()));
    }
}