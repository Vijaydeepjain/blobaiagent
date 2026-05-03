package TechGeeks.AiAgentService;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {

    private String headline;
    private String body;
    private List<FileData> files;
    private String publisher;
    private List<String> postType;

    // constructor, getters, setters
}

