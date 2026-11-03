package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.enums.BotConfig;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotQueueWorker {

    private final BotTaskQueue botTaskQueue;
    private final BotPipelineService pipelineService;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @PostConstruct
    public void startWorker() {

        executor.submit(() -> {

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    BotConfig bot = botTaskQueue.take();

                    log.info("Processing bot: {}", bot.name());

                    pipelineService.execute(bot);

                } catch (InterruptedException exception) {

                    Thread.currentThread().interrupt();

                } catch (Exception exception) {

                    log.error("Bot processing failed", exception);
                }
            }
        });
    }
}