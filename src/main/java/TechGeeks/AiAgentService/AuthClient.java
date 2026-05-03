package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class AuthClient {

    private final WebClient webClient = WebClient.create("http://postwebapp-env.eba-8rgy9ptm.ap-south-1.elasticbeanstalk.com");

    public String loginAndGetToken() {

        Map<String, String> request = Map.of(
                "email", "vijaydeepjain@gmail.com",
                "password", "vijay123"
        );

        Map response = webClient.post()
                .uri("/users/login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("token");
    }
}