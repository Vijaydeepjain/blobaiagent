package TechGeeks.AiAgentService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

public interface BotDataset {

    Integer getDay();

    String getBotId();

    @Data
    @NoArgsConstructor
    class Food implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String country;
        private String regionOrProvince;
        private String foodName;
        private String editorialNote;
    }

    @Data
    @NoArgsConstructor
    class Health implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String topicArea;
        private String topicName;
        private String articleTitle;
        private String scientificFocus;
    }

    @Data
    @NoArgsConstructor
    class Psychology implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String topicArea;
        private String topicName;
        private String issue;
        private String selectedTitle;
    }

    @Data
    @NoArgsConstructor
    class Travel implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String country;
        private String regionOrProvince;
        private String placeName;
    }

    @Data
    @NoArgsConstructor
    class Poetry implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String languageCode;
        private String languageName;
        private String script;
        private String poetryStyle;
        private String theme;
        private String poemTitle;
        private String poemText;
        private Integer lineCount;
    }

    @Data
    @NoArgsConstructor
    class Relationships implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String topicArea;
        private String topicName;
        private String issue;
        private String scenario;
        private String title;
    }

    @Data
    @NoArgsConstructor
    class Science implements BotDataset {
        private Integer day;

        @Field("botid")
        private String botId;

        private String subject;
        private String subfield;
        private String topicName;
        private String articleTitle;
        private String learningAngle;
    }
}