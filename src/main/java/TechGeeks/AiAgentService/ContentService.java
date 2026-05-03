package TechGeeks.AiAgentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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

        // 4. get upload URL
        Map<String, String> uploadData = fileUploadClient.getUploadUrl(token);

        String uploadUrl = uploadData.get("uploadUrl");
        String fileKey = uploadData.get("fileKey");
        String fileToken = uploadData.get("token");

        byte[] image;

        try {
            image = Files.readAllBytes(
                    Path.of("C:/Users/vijay/Downloads/my gibli phto.png")
            );
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load image from disk", ex);
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
        post.setPublisher("Vijay Jain");
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
}