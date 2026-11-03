package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.entity.BotDayTracker;
import TechGeeks.AiAgentService.enums.BotConfig;
import TechGeeks.AiAgentService.enums.PostType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotDayTrackerService {

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION = "bot_day_tracker";

    private static final ZoneId ZONE =
            ZoneId.of("Asia/Kolkata");


    // Initialize tracker records for all bots
    @EventListener(ApplicationReadyEvent.class)
    public void initializeTrackers() {
        log.info("MongoDB database: {}", mongoTemplate.getDb().getName());
        for (BotConfig bot : BotConfig.values()) {

            Query query = getBotQuery(bot);

            Instant now = Instant.now();

            Update update = new Update()
                    .setOnInsert("category", bot.getCategory())
                    .setOnInsert("botId", bot.getBotId())
                    .setOnInsert("currentDay", 1)
                    .setOnInsert("createdAt", now)
                    .setOnInsert("updatedAt", now);

            mongoTemplate.upsert(
                    query,
                    update,
                    BotDayTracker.class,
                    COLLECTION
            );
            log.info("Tracker upserted for bot: {}", bot.name());
        }

        log.info("Bot day trackers initialized successfully. Total bots: {}",
                BotConfig.values().length);
    }


    // Get current day for a bot
    public int getCurrentDay(BotConfig bot) {

        BotDayTracker tracker = mongoTemplate.findOne(
                getBotQuery(bot),
                BotDayTracker.class,
                COLLECTION
        );

        if (tracker == null) {
            throw new IllegalStateException(
                    "Tracker not found for bot: " + bot.name()
            );
        }

        return tracker.getCurrentDay() == null
                ? 1
                : tracker.getCurrentDay();
    }


    // Check whether the bot has already completed today's run
    public boolean hasRunToday(BotConfig bot) {

        BotDayTracker tracker = mongoTemplate.findOne(
                getBotQuery(bot),
                BotDayTracker.class,
                COLLECTION
        );

        if (tracker == null || tracker.getLastRunAt() == null) {
            return false;
        }

        LocalDate lastRunDate = tracker.getLastRunAt()
                .atZone(ZONE)
                .toLocalDate();

        LocalDate today = LocalDate.now(ZONE);

        return lastRunDate.equals(today);
    }


    // Mark successful execution and increment the day
    public boolean completeRun(BotConfig bot, int expectedDay) {

        Instant now = Instant.now();

        Query query = getBotQuery(bot);

        query.addCriteria(
                Criteria.where("currentDay").is(expectedDay)
        );

        Update update = new Update()
                .set("currentDay", expectedDay + 1)
                .set("lastRunAt", now)
                .set("updatedAt", now);

        BotDayTracker updatedTracker = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                BotDayTracker.class,
                COLLECTION
        );

        if (updatedTracker == null) {
            log.warn(
                    "Could not update tracker. Bot: {}, Expected day: {}",
                    bot.name(),
                    expectedDay
            );

            return false;
        }

        log.info(
                "Bot execution completed. Bot: {}, Previous day: {}, Next day: {}",
                bot.name(),
                expectedDay,
                expectedDay + 1
        );

        return true;
    }


    // Common query for identifying a bot
    private Query getBotQuery(BotConfig bot) {

        return new Query(
                Criteria.where("category").is(bot.getCategory())
                        .and("botId").is(bot.getBotId())
        );
    }

    // Check whether the bot has already been attempted today
    public boolean hasAttemptedToday(BotConfig bot) {

        BotDayTracker tracker = mongoTemplate.findOne(
                getBotQuery(bot),
                BotDayTracker.class,
                COLLECTION
        );

        if (tracker == null || tracker.getLastAttemptAt() == null) {
            return false;
        }

        LocalDate lastAttemptDate = tracker.getLastAttemptAt()
                .atZone(bot.getTimeZone())
                .toLocalDate();

        LocalDate today = LocalDate.now(bot.getTimeZone());

        return lastAttemptDate.equals(today);
    }


    // Atomically claim the bot for today's execution
    public boolean markAttemptedToday(BotConfig bot) {

        Instant now = Instant.now();

        ZoneId zone = bot.getTimeZone();

        Instant startOfToday = LocalDate.now(zone)
                .atStartOfDay(zone)
                .toInstant();

        Query query = getBotQuery(bot);

        // Allow claim only if there is no attempt today
        query.addCriteria(
                new Criteria().orOperator(
                        Criteria.where("lastAttemptAt").exists(false),
                        Criteria.where("lastAttemptAt").is(null),
                        Criteria.where("lastAttemptAt").lt(startOfToday)
                )
        );

        Update update = new Update()
                .set("lastAttemptAt", now)
                .set("updatedAt", now);

        BotDayTracker updatedTracker = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                BotDayTracker.class,
                COLLECTION
        );

        if (updatedTracker == null) {
            log.info("Bot already attempted today: {}", bot.name());
            return false;
        }

        log.info("Bot attempt claimed successfully: {}", bot.name());

        return true;
    }
}