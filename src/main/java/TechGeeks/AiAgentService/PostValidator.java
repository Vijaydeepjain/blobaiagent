package TechGeeks.AiAgentService;

import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    public boolean isValid(String content) {

        if (content == null || content.length() < 50)
            return false;

        if (content.contains("AI language model"))
            return false;

        return true;
    }
}