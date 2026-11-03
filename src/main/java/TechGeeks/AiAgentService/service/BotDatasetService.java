package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.dto.BotDataset;
import TechGeeks.AiAgentService.enums.BotConfig;
import TechGeeks.AiAgentService.enums.PostType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotDatasetService {

    private final MongoTemplate mongoTemplate;

    public BotDataset fetchDataset(BotConfig bot, int day) {

        if (day <= 0) {
            throw new IllegalArgumentException(
                    "Day must be greater than zero"
            );
        }

        Query query = new Query();

        query.addCriteria(
                Criteria.where("day").is(day)
                        .and("bot").is(bot.getBotId())
        );

        PostType category = bot.getCategory();

        log.info(
                "Fetching dataset: category={}, botId={}, day={}",
                category,
                bot.getBotId(),
                day
        );

        return switch (category) {

            case FOOD -> findDataset(
                    query,
                    BotDataset.Food.class,
                    "FOOD",
                    bot,
                    day
            );

            case HEALTH -> findDataset(
                    query,
                    BotDataset.Health.class,
                    "HEALTH",
                    bot,
                    day
            );

            case PSYCHOLOGY -> findDataset(
                    query,
                    BotDataset.Psychology.class,
                    "PSYCHOLOGY",
                    bot,
                    day
            );

            case TRAVEL -> findDataset(
                    query,
                    BotDataset.Travel.class,
                    "TRAVEL",
                    bot,
                    day
            );

            case POETRY -> findDataset(
                    query,
                    BotDataset.Poetry.class,
                    "POETRY",
                    bot,
                    day
            );

            case RELATIONSHIPS -> findDataset(
                    query,
                    BotDataset.Relationships.class,
                    "RELATIONSHIPS",
                    bot,
                    day
            );

            case SCIENCE -> findDataset(
                    query,
                    BotDataset.Science.class,
                    "SCIENCE",
                    bot,
                    day
            );

            default -> throw new IllegalArgumentException(
                    "Unsupported bot category: " + category
            );
        };
    }

    private <T extends BotDataset> T findDataset(
            Query query,
            Class<T> documentClass,
            String collectionName,
            BotConfig bot,
            int day
    ) {

        T dataset = mongoTemplate.findOne(
                query,
                documentClass,
                collectionName
        );

        if (dataset == null) {
            throw new IllegalStateException(
                    "Dataset not found. Category=" + collectionName
                            + ", BotId=" + bot.getBotId()
                            + ", Day=" + day
            );
        }

        return dataset;
    }
}