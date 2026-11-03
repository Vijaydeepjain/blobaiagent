package TechGeeks.AiAgentService.entity;

import TechGeeks.AiAgentService.enums.PostType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "bot_day_tracker")
@CompoundIndex(
        name = "unique_category_bot",
        def = "{'category': 1, 'botId': 1}",
        unique = true
)
public class BotDayTracker {

    @Id
    private String id;

    private PostType category;

    private String botId;

    private Integer currentDay;

    private Instant lastRunAt;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant lastAttemptAt;
}