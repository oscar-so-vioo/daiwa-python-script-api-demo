package com.example.daiwapythonscriptapidemo.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "report")
public class ReportEntity {
    @Id
    private String id;
    private String templateName;
    private String createdBy;
    private String scriptName;
    private List<String> filters;
    private List<String> recipients;
    private String status;
}
