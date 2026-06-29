package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class PostTypeClient {

    public List<String> getPostTypes() {
        return List.of(
                //"PERSONAL_STORIES",
               // "TRAVEL",
               // "FICTION"
                "HEALTH"
        );
    }
}
