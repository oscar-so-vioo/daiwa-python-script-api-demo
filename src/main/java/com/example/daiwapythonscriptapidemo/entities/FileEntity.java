package com.example.daiwapythonscriptapidemo.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "files")
public class FileEntity {
    @Id
    private String id;
    private String fileName;
    private String saveByString;
    private byte[] saveByBytes;
    private String contentType;
}
