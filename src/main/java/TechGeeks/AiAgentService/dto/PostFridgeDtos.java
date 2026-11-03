package TechGeeks.AiAgentService.dto;

import java.util.List;

public class PostFridgeDtos {

    public record LoginRequest(
            String loginType,
            String email,
            String password
    ) {}

    public record LoginResponse(
            String token
    ) {}

    public record UploadUrlResponse(
            String uploadUrl,
            String fileKey,
            String token
    ) {}

    public record PostFile(
            String fileKey,
            String token
    ) {}

    public record CreatePostRequest(
            String headline,
            String body,
            List<PostFile> files,
            List<String> postType,
            String mediaType,
            String postVisibility,
            String languageCode
    ) {}
}