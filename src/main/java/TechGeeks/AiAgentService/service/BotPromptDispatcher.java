package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.dto.BotDataset;
import TechGeeks.AiAgentService.enums.PostType;
import TechGeeks.AiAgentService.prompt.Prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotPromptDispatcher {

    private final Prompt prompt;


    public String buildPrompt(PostType category, BotDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException(
                    "Dataset cannot be null for category: " + category
            );
        }

        return switch (category) {

            case FOOD -> {

                BotDataset.Food data = (BotDataset.Food) dataset;

                yield prompt.buildFoodPrompt(
                        data.getCountry(),
                        data.getRegionOrProvince(),
                        data.getFoodName(),
                        data.getEditorialNote()
                );
            }

            case HEALTH -> {

                BotDataset.Health data = (BotDataset.Health) dataset;

                yield prompt.buildHealthPrompt(
                        data.getTopicArea(),
                        data.getTopicName(),
                        data.getArticleTitle(),
                        data.getScientificFocus()
                );
            }

            case PSYCHOLOGY -> {

                BotDataset.Psychology data =
                        (BotDataset.Psychology) dataset;

                yield prompt.buildPsychologyPrompt(
                        data.getTopicArea(),
                        data.getTopicName(),
                        data.getIssue(),
                        data.getSelectedTitle()
                );
            }

            case TRAVEL -> {

                BotDataset.Travel data = (BotDataset.Travel) dataset;

                yield prompt.buildTravelPrompt(
                        data.getCountry(),
                        data.getRegionOrProvince(),
                        data.getPlaceName()
                );
            }

            case POETRY -> {

                BotDataset.Poetry data = (BotDataset.Poetry) dataset;

                yield prompt.buildPoetryPrompt(
                        data.getLanguageCode(),
                        data.getLanguageName(),
                        data.getScript(),
                        data.getPoetryStyle(),
                        data.getTheme(),
                        data.getPoemTitle(),
                        data.getPoemText(),
                        data.getLineCount()
                );
            }

            case RELATIONSHIPS -> {

                BotDataset.Relationships data =
                        (BotDataset.Relationships) dataset;

                yield prompt.buildRelationshipsPrompt(
                        data.getTopicArea(),
                        data.getTopicName(),
                        data.getIssue(),
                        data.getScenario(),
                        data.getTitle()
                );
            }

            case SCIENCE -> {

                BotDataset.Science data = (BotDataset.Science) dataset;

                yield prompt.buildSciencePrompt(
                        data.getSubject(),
                        data.getSubfield(),
                        data.getTopicName(),
                        data.getArticleTitle(),
                        data.getLearningAngle()
                );
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported bot category: " + category
            );
        };
    }
}