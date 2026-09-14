package com.hig.inovagab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ideas")
public class Idea {
    @Id
    private String id;
    private String title;
    private String description;
    private IdeaStatus status;
    private String authorId;
    private String strategyId;
}
