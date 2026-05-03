package TechGeeks.AiAgentService;

import lombok.Data;

@Data
public class FileData {
    private String fileKey;
    private String token;

    public FileData(String fileKey, String token) {
        this.fileKey = fileKey;
        this.token = token;
    }
}
