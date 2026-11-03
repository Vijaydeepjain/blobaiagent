package TechGeeks.AiAgentService.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParentCategory {

    WORLD_AND_SOCIETY("World & Society"),
    KNOWLEDGE_AND_INNOVATION("Knowledge & Innovation"),
    PEOPLE_AND_LIFESTYLE("People & Lifestyle"),
    COMMUNITY_AND_EXPRESSION("Community & Expression"),
    ENTERTAINMENT_AND_SPORTS("Entertainment & Sports");

    private final String displayName;
}
