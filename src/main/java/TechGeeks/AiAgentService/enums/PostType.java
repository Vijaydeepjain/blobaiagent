package TechGeeks.AiAgentService.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {

    DAILY_DOSE("Daily Dose", ParentCategory.WORLD_AND_SOCIETY),
    WORLD("World", ParentCategory.WORLD_AND_SOCIETY),

    TECH("Technology & AI", ParentCategory.KNOWLEDGE_AND_INNOVATION),
    SCIENCE("Science", ParentCategory.KNOWLEDGE_AND_INNOVATION),
    BUSINESS("Business & Finance", ParentCategory.KNOWLEDGE_AND_INNOVATION),

    HEALTH("Health & Wellness", ParentCategory.PEOPLE_AND_LIFESTYLE),
    PSYCHOLOGY("Mind & Emotions", ParentCategory.PEOPLE_AND_LIFESTYLE),
    RELATIONSHIPS("Relationships", ParentCategory.PEOPLE_AND_LIFESTYLE),
    LIFESTYLE("Lifestyle", ParentCategory.PEOPLE_AND_LIFESTYLE),
    FOOD("Food", ParentCategory.PEOPLE_AND_LIFESTYLE),
    TRAVEL("Places & Exploration", ParentCategory.PEOPLE_AND_LIFESTYLE),

    STORIES("Stories", ParentCategory.COMMUNITY_AND_EXPRESSION),
    POETRY("Poetry", ParentCategory.COMMUNITY_AND_EXPRESSION),

    ENTERTAINMENT("Entertainment", ParentCategory.ENTERTAINMENT_AND_SPORTS),
    SPORTS("Sports", ParentCategory.ENTERTAINMENT_AND_SPORTS);

    private final String displayName;
    private final ParentCategory parentCategory;
}