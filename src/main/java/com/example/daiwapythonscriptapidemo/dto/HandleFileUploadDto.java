package com.example.daiwapythonscriptapidemo.dto;

import lombok.Data;

import java.util.List;

@Data
public class HandleFileUploadDto {

    private String title;

    private List<String> accessibility;

    private List<String> recipient;
}
