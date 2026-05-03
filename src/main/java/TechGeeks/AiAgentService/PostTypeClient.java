package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class PostTypeClient {

    private final WebClient webClient = WebClient.create("http://postwebapp-env.eba-8rgy9ptm.ap-south-1.elasticbeanstalk.com");

    public List<String> getPostTypes() {

        Map response = webClient.get()
                .uri("/post/Post-type")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (List<String>) response.get("List");
    }
}
