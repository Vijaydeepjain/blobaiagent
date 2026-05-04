package TechGeeks.AiAgentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ContentService {

    @Autowired
    private AuthClient authClient;

    @Autowired
    private GeminiClient geminiClient;

    @Autowired
    private PublisherClient publisher;

    @Autowired
    private FileUploadClient fileUploadClient;

    @Autowired
    private PostTypeClient postTypeClient;

    @Value("${stability.api.key}")
    private String apiKey;

    public void generateAndPublish() {

        // 1. login
        String token = authClient.loginAndGetToken();

        // 2. get categories
        List<String> types = postTypeClient.getPostTypes();
        String category = types.get(new Random().nextInt(types.size()));

        // 3. generate content
        Map<String, String> result = geminiClient.generateContent(category);

        String headline = result.get("headline");
        String body = result.get("body");
        String imagePrompt = result.get("imagePrompt");
        // 4. get upload URL
        Map<String, String> uploadData = fileUploadClient.getUploadUrl(token);

        String uploadUrl = uploadData.get("uploadUrl");
        String fileKey = uploadData.get("fileKey");
        String fileToken = uploadData.get("token");

        byte[] image;
        try {
            image = generateImage(imagePrompt);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate image", ex);
        }

// safety check
        if (image == null || image.length == 0) {
            throw new RuntimeException("Image is empty or null");
        }

        fileUploadClient.uploadToS3(uploadUrl, image);

        // 6. create post
        PostRequest post = new PostRequest();
        post.setHeadline(headline);
        post.setBody(body);
        post.setPostType(List.of(category));
        post.setFiles(List.of(new FileData(fileKey, fileToken)));

        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
            System.out.println("POST JSON:\n" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        publisher.createPost(post, token);

        System.out.println("Post created successfully!");
    }

    public byte[] generateImage(String prompt) {

        try {
            URL url = new URL("https://api.stability.ai/v2beta/stable-image/generate/sd3");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Headers
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Accept", "image/*");

            // Multipart boundary
            String boundary = "----Boundary" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream os = conn.getOutputStream();

            // 🔥 prompt field
            writeFormField(os, boundary, "prompt", prompt);

            // 🔥 output_format field
            writeFormField(os, boundary, "output_format", "jpeg");

            // End boundary
            os.write(("--" + boundary + "--\r\n").getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("Image API failed: " + responseCode);
            }

            // Read image bytes
            InputStream is = conn.getInputStream();
            return is.readAllBytes();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image", e);
        }
    }

    private void writeFormField(OutputStream os, String boundary, String name, String value) throws Exception {
        os.write(("--" + boundary + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
        os.write((value + "\r\n").getBytes());
    }
}