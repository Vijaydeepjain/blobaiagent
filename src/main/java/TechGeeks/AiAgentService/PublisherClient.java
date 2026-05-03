package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PublisherClient {

    private final WebClient webClient = WebClient.create("http://postwebapp-env.eba-8rgy9ptm.ap-south-1.elasticbeanstalk.com");

    public void createPost(PostRequest post, String token) {

        webClient.post()
                .uri("/post/createpost")
                .header("Authorization", "Bearer " + token)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}