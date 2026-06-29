package TechGeeks.AiAgentService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContentScheduler {

    @Autowired
    private ContentService contentService;

    @Scheduled(fixedRate = 100000) // 5 minutes
    public void run() {
        contentService.generateAndPublish();
    }
}
