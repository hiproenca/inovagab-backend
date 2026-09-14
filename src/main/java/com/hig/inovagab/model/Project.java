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
@Document(collection = "projects")
public class Project {
    @Id
    private String id;
    private String title;
    private ProjectStage stage;
    private ProjectStatus status;
    private Double investment;
    private String deadline;
    private Double financialReturn;
    private String description;
    private String strategyId;
    private String results;
}
