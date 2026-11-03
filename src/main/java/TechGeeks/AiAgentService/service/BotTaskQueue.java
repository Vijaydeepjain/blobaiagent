package TechGeeks.AiAgentService.service;

import TechGeeks.AiAgentService.enums.BotConfig;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BotTaskQueue {

    private final BlockingQueue<BotConfig> queue =
            new LinkedBlockingQueue<>();

    public void add(BotConfig bot) {
        queue.offer(bot);
    }

    public BotConfig take() throws InterruptedException {
        return queue.take();
    }

    public boolean contains(BotConfig bot) {
        return queue.contains(bot);
    }

    public int size() {
        return queue.size();
    }
}