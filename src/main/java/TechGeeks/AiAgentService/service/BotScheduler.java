package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.enums.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotScheduler {

    private final BotTaskQueue botTaskQueue;
    private final BotDayTrackerService botDayTrackerService;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    public void checkScheduledBots() {

        for (BotConfig bot : BotConfig.values()) {

            LocalTime currentTime = LocalTime.now(bot.getTimeZone())
                    .withSecond(0)
                    .withNano(0);

            LocalDate today = LocalDate.now(bot.getTimeZone());

            // Execute only at the exact scheduled minute
            if (!currentTime.equals(
                    bot.getScheduledTime()
                            .withSecond(0)
                            .withNano(0))) {
                continue;
            }

            // Prevent duplicate queue entries
            if (botTaskQueue.contains(bot)) {
                continue;
            }

            // Atomically check and claim today's execution
            if (!botDayTrackerService.markAttemptedToday(bot)) {
                continue;
            }

            try {
                botTaskQueue.add(bot);

                log.info(
                        "Bot added to queue: {}, date: {}, time: {}",
                        bot.name(),
                        today,
                        currentTime
                );

            } catch (Exception e) {
                log.error(
                        "Failed to add bot to queue: {}",
                        bot.name(),
                        e
                );
            }
        }
    }
}