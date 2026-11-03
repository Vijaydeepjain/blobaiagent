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
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "18:51"),

    PSYCHOLOGY_BOT_02(PostType.PSYCHOLOGY, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "18:54"),

    PSYCHOLOGY_BOT_03(PostType.PSYCHOLOGY, "BOT_03",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "18:56"),

    PSYCHOLOGY_BOT_04(PostType.PSYCHOLOGY, "BOT_04",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "18:59"),

    PSYCHOLOGY_BOT_05(PostType.PSYCHOLOGY, "BOT_05",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:02"),


    // RELATIONSHIPS - 3 BOTS
    RELATIONSHIPS_BOT_01(PostType.RELATIONSHIPS, "BOT_01",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:04"),

    RELATIONSHIPS_BOT_02(PostType.RELATIONSHIPS, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:07"),

    RELATIONSHIPS_BOT_03(PostType.RELATIONSHIPS, "BOT_03",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:10"),


    // PLACES AND EXPLORATION - 2 BOTS
    PLACES_BOT_01(PostType.TRAVEL, "BOT_01",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:12"),

    PLACES_BOT_02(PostType.TRAVEL, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:15"),


    // FOOD - 2 BOTS
    FOOD_BOT_01(PostType.FOOD, "BOT_01",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "23:55"),

    FOOD_BOT_02(PostType.FOOD, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:20"),


    // HEALTH - 4 BOTS
    HEALTH_BOT_01(PostType.HEALTH, "BOT_01",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "23:05"),

    HEALTH_BOT_02(PostType.HEALTH, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:23"),

    HEALTH_BOT_03(PostType.HEALTH, "BOT_03",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:28"),

    HEALTH_BOT_04(PostType.HEALTH, "BOT_04",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "23:40"),


    // SCIENCE - 5 BOTS
    SCIENCE_BOT_01(PostType.SCIENCE, "BOT_01",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "22:47"),

    SCIENCE_BOT_02(PostType.SCIENCE, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "22:50"),

    SCIENCE_BOT_03(PostType.SCIENCE, "BOT_03",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "23:47"),

    SCIENCE_BOT_04(PostType.SCIENCE, "BOT_04",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:42"),

    SCIENCE_BOT_05(PostType.SCIENCE, "BOT_05",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:44"),


    // POETRY - 6 BOTS
    POETRY_BOT_01(PostType.POETRY, "BOT_01",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:47"),

    POETRY_BOT_02(PostType.POETRY, "BOT_02",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:50"),

    POETRY_BOT_03(PostType.POETRY, "BOT_03",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:52"),

    POETRY_BOT_04(PostType.POETRY, "BOT_04",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:55"),

    POETRY_BOT_05(PostType.POETRY, "BOT_05",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "19:57"),

    POETRY_BOT_06(PostType.POETRY, "BOT_06",
            "6ae8550c425b508c2c92093c", "vijaydeepjain@gmail.com", "vijaydeep24", "20:00");

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