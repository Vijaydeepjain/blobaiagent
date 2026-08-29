package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class AuthClient {

    private final WebClient webClient = WebClient.create("https://postfridgeprod.onrender.com");

    public String loginAndGetToken() {

        Map<String, String> request = Map.of(
              //  "email", "vijayjain0824@gmail.com",
                "email", "harsh@postfridge.com",
                "password", "string"
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