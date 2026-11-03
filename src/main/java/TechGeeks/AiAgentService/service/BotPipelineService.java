package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.dto.BotDataset;
import TechGeeks.AiAgentService.enums.BotConfig;
import TechGeeks.AiAgentService.enums.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotPipelineService {

    private final BotDayTrackerService botDayTrackerService;
    private final BotDatasetService botDatasetService;
    private final BotPromptDispatcher promptDispatcher;
    private final GeminiService geminiService;
    private final StabilityImageService stabilityImageService;
    private final ObjectMapper objectMapper;
    private final PostFridgeApiService postFridgeApiService;

    public void execute(BotConfig bot) {
        // 1. Get current bot day
        int day = botDayTrackerService.getCurrentDay(bot);

// 2. Fetch dataset
        BotDataset dataset = botDatasetService.fetchDataset(bot, day);

// 3. Build Gemini prompt
        String prompt = promptDispatcher.buildPrompt(
                bot.getCategory(),
                dataset
        );

// 4. Generate content
        String generatedResponse = geminiService.generateContent(prompt);

// 5. Generate image only for HEALTH and SCIENCE
        byte[] generatedImage = null;

        if (bot.getCategory() == PostType.HEALTH
                || bot.getCategory() == PostType.SCIENCE) {

            JsonNode responseJson = objectMapper.readTree(generatedResponse);

            String imagePrompt = responseJson.path("imagePrompt").asText();

            if (imagePrompt.isBlank()) {
                throw new IllegalStateException("Gemini imagePrompt is missing");
            }

            generatedImage = stabilityImageService.generateImage(imagePrompt);
        }

        // 6. Get language code for poetry
        String languageCode = "";

        if (dataset instanceof BotDataset.Poetry poetry) {
            languageCode = poetry.getLanguageCode();
        }

        // 7. Publish post to PostFridge
        postFridgeApiService.publishPost(
                bot,
                generatedResponse,
                generatedImage,
                languageCode
        );

// 8. Update tracker only after successful publishing
        boolean completed = botDayTrackerService.completeRun(bot, day);

        if (!completed) {
            throw new IllegalStateException(
                    "Unable to update bot tracker for " + bot.name()
            );
        }
    }
}