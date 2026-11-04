package TechGeeks.AiAgentService.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum BotConfig {

    // PSYCHOLOGY - 5 BOTS
    PSYCHOLOGY_BOT_01(PostType.PSYCHOLOGY, "BOT_01",
            "6aeaeb8f2dcf1e678b22829a", "bot1phy@postfridge.com", "string12345", "12:00"),

    PSYCHOLOGY_BOT_02(PostType.PSYCHOLOGY, "BOT_02",
            "6aeaf0974563f9888591dd8b", "bot2phy@postfridge.com", "string12345", "13:53"),

    PSYCHOLOGY_BOT_03(PostType.PSYCHOLOGY, "BOT_03",
            "6aeaf0974563f9888591dd8c", "bot3phy@postfridge.com", "string12345", "15:30"),

    PSYCHOLOGY_BOT_04(PostType.PSYCHOLOGY, "BOT_04",
            "6aeaf0974563f9888591dd8d", "bot4phy@postfridge.com", "string12345", "17:07"),

    PSYCHOLOGY_BOT_05(PostType.PSYCHOLOGY, "BOT_05",
            "6aeaf0974563f9888591dd8e", "bot5phy@postfridge.com", "string12345", "18:12"),


    // RELATIONSHIPS - 3 BOTS
    RELATIONSHIPS_BOT_01(PostType.RELATIONSHIPS, "BOT_01",
            "6aeaf1f64563f9888591dd8f", "bot1rel@postfridge.com", "string12345", "12:16"),

    RELATIONSHIPS_BOT_02(PostType.RELATIONSHIPS, "BOT_02",
            "6aeaf1f64563f9888591dd90", "bot2rel@postfridge.com", "string12345", "14:42"),

    RELATIONSHIPS_BOT_03(PostType.RELATIONSHIPS, "BOT_03",
            "6aeaf1f64563f9888591dd91", "bot3rel@postfridge.com", "string12345", "16:51"),


    // PLACES AND EXPLORATION - 2 BOTS
    PLACES_BOT_01(PostType.TRAVEL, "BOT_01",
            "6aeaf28d4563f9888591dd92", "bot1trv@postfridge.com", "string12345", "13:37"),

    PLACES_BOT_02(PostType.TRAVEL, "BOT_02",
            "6aeaf28d4563f9888591dd93", "bot2trv@postfridge.com", "string12345", "15:46"),


    // FOOD - 2 BOTS
    FOOD_BOT_01(PostType.FOOD, "BOT_01",
            "6aeaf30a4563f9888591dd94", "bot1food@postfridge.com", "string12345", "12:48"),

    FOOD_BOT_02(PostType.FOOD, "BOT_02",
            "6aeaf30a4563f9888591dd95", "bot2food@postfridge.com", "string12345", "15:14"),


    // HEALTH - 4 BOTS
    HEALTH_BOT_01(PostType.HEALTH, "BOT_01",
            "6aeaf36a4563f9888591dd96", "bot1health@postfridge.com", "string12345", "13:21"),

    HEALTH_BOT_02(PostType.HEALTH, "BOT_02",
            "6aeaf36a4563f9888591dd97", "bot2health@postfridge.com", "string12345", "14:58"),

    HEALTH_BOT_03(PostType.HEALTH, "BOT_03",
            "6aeaf36a4563f9888591dd98", "bot3health@postfridge.com", "string12345", "16:35"),

    HEALTH_BOT_04(PostType.HEALTH, "BOT_04",
            "6aeaf36a4563f9888591dd99", "bot4health@postfridge.com", "string12345", "17:56"),


    // SCIENCE - 5 BOTS
    SCIENCE_BOT_01(PostType.SCIENCE, "BOT_01",
            "6aeaf3b44563f9888591dd9a", "bot1sci@postfridge.com", "string12345", "12:32"),

    SCIENCE_BOT_02(PostType.SCIENCE, "BOT_02",
            "6aeaf3b44563f9888591dd9b", "bot2sci@postfridge.com", "string12345", "14:25"),

    SCIENCE_BOT_03(PostType.SCIENCE, "BOT_03",
            "6aeaf3b44563f9888591dd9c", "bot3sci@postfridge.com", "string12345", "16:19"),

    SCIENCE_BOT_04(PostType.SCIENCE, "BOT_04",
            "6aeaf3b44563f9888591dd9d", "bot4sci@postfridge.com", "string12345", "17:40"),

    SCIENCE_BOT_05(PostType.SCIENCE, "BOT_05",
            "6aeaf3b44563f9888591dd9e", "bot5sci@postfridge.com", "string12345", "18:44"),


    // POETRY - 6 BOTS
    POETRY_BOT_01(PostType.POETRY, "BOT_01",
            "6aeaf4254563f9888591dd9f", "bot1poe@postfridge.com", "string12345", "13:05"),

    POETRY_BOT_02(PostType.POETRY, "BOT_02",
            "6aeaf4254563f9888591dda0", "bot2poe@postfridge.com", "string12345", "14:09"),

    POETRY_BOT_03(PostType.POETRY, "BOT_03",
            "6aeaf4254563f9888591dda1", "bot3poe@postfridge.com", "string12345", "16:02"),

    POETRY_BOT_04(PostType.POETRY, "BOT_04",
            "6aeaf4254563f9888591dda2", "bot4poe@postfridge.com", "string12345", "17:23"),

    POETRY_BOT_05(PostType.POETRY, "BOT_05",
            "6aeaf4254563f9888591dda3", "bot5poe@postfridge.com", "string12345", "18:28"),

    POETRY_BOT_06(PostType.POETRY, "BOT_06",
            "6aeaf4254563f9888591dda4", "bot6poe@postfridge.com", "string12345", "19:00");
    private final PostType category;
    private final String botId;
    private final String accountId;
    private final String email;
    private final String password;
    private final String publishTime;

    private static final ZoneId TIME_ZONE =
            ZoneId.of("Asia/Kolkata");

    public LocalTime getScheduledTime() {
        return LocalTime.parse(publishTime);
    }

    public ZoneId getTimeZone() {
        return TIME_ZONE;
    }

    public static List<BotConfig> getBotsByCategory(PostType category) {
        return Arrays.stream(values())
                .filter(bot -> bot.category == category)
                .toList();
    }

    public static Optional<BotConfig> getBot(
            PostType category,
            String botId) {

        return Arrays.stream(values())
                .filter(bot -> bot.category == category)
                .filter(bot -> bot.botId.equalsIgnoreCase(botId))
                .findFirst();
    }

    public static int getBotCount(PostType category) {
        return (int) Arrays.stream(values())
                .filter(bot -> bot.category == category)
                .count();
    }
}