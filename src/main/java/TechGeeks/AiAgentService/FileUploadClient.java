package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Component
public class FileUploadClient {

    private final WebClient webClient = WebClient.create("http://postwebapp-env.eba-8rgy9ptm.ap-south-1.elasticbeanstalk.com");

    public Map<String, String> getUploadUrl(String token) {

        return webClient.get()
                .uri("/files/upload-url")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public void uploadToS3(String uploadUrl, byte[] imageBytes) {
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(imageBytes);
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("S3 upload failed with code: " + responseCode);
            }

            System.out.println("Upload successful ✅");

        } catch (Exception e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }
}