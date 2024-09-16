package com.example.daiwapythonscriptapidemo.controllers;


import com.example.daiwapythonscriptapidemo.dto.HandleFileUploadDto;
import com.example.daiwapythonscriptapidemo.entities.FileEntity;
import com.example.daiwapythonscriptapidemo.entities.ReportEntity;
import com.example.daiwapythonscriptapidemo.repositories.FileRepository;
import com.example.daiwapythonscriptapidemo.repositories.ReportRepository;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class UploadController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ReportRepository reportRepository;

    @PatchMapping("/report")
    public ResponseEntity<String> handlePatchReport(
            @RequestParam("template_name") String templateName,
            @RequestParam(value = "created_by", required = false) @Size(min = 3, max = 20) String createdBy,
            @RequestParam(value = "script_name", required = false) @Size(min = 3, max = 20) String scriptName,
            @RequestParam(value = "filters", required = false) List<String> filters,
            @RequestParam(value = "recipients", required = false) List<String> recipients,
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader("userId") String userId
    ) {

        ReportEntity reportEntity = reportRepository.findByTemplateName(templateName);

        if (createdBy != null) {
            reportEntity.setCreatedBy(createdBy);
        }
        if (scriptName != null) {
            reportEntity.setScriptName(scriptName);
        }
        if (filters != null) {
            reportEntity.setFilters(filters);
        }
        if (recipients != null) {
            reportEntity.setRecipients(recipients);
        }
        if (status != null) {
            reportEntity.setStatus(status);
        }
        reportRepository.save(reportEntity);
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/report")
    public ResponseEntity<String> handlePutReport(
            @RequestParam("template_name")  String templateName,
            @RequestParam("created_by") @Size(min = 3, max = 20) String createdBy,
            @RequestParam("script_name") @Size(min = 3, max = 20) String scriptName,
            @RequestParam("filters") List<String> filters,
            @RequestParam("recipients") List<String> recipients,
            @RequestParam("status") String status,
            @RequestHeader("userId") String userId
    ) {

        ReportEntity reportEntity = reportRepository.findByTemplateName(templateName);

        reportEntity.setCreatedBy(createdBy);
        reportEntity.setScriptName(scriptName);
        reportEntity.setFilters(filters);
        reportEntity.setRecipients(recipients);
        reportEntity.setStatus(status);

        reportRepository.save(reportEntity);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/report")
    public ResponseEntity<String> handlePostReport(
            @RequestParam("template_name") @Size(min = 3, max = 20) String templateName,
            @RequestParam("created_by") @Size(min = 3, max = 20) String createdBy,
            @RequestParam("script_name") @Size(min = 3, max = 20) String scriptName,
            @RequestParam("filters") List<String> filters,
            @RequestParam("recipients") List<String> recipients,
            @RequestParam("status") String status,
            @RequestHeader("userId") String userId
    ) {

        ReportEntity reportEntity = new ReportEntity();

        reportEntity.setTemplateName(templateName);
        reportEntity.setCreatedBy(createdBy);
        reportEntity.setScriptName(scriptName);
        reportEntity.setFilters(filters);
        reportEntity.setRecipients(recipients);
        reportEntity.setStatus(status);

        reportRepository.save(reportEntity);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/upload-save-by-bytes")
    public ResponseEntity<String> handleFileUploadSaveByBytes(
            @RequestParam("created_by") String createdBy,
            @RequestParam("script_name") String scriptName,
            @RequestParam("template_content") MultipartFile templateContent,
            @RequestParam("data_script") MultipartFile dataScript,
            @RequestParam("status") String status
    ) {
        try {

            //
            byte[] content = templateContent.getBytes();
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(templateContent.getOriginalFilename());
            fileEntity.setSaveByBytes(content);
            fileEntity.setContentType(templateContent.getContentType());
            fileRepository.save(fileEntity);

            //
            byte[] pyContent = dataScript.getBytes();
            FileEntity pyFileEntity = new FileEntity();
            pyFileEntity.setFileName(dataScript.getOriginalFilename());
            pyFileEntity.setSaveByBytes(pyContent);
            pyFileEntity.setContentType(dataScript.getContentType());
            fileRepository.save(pyFileEntity);

            return ResponseEntity.ok("File saved to MongoDB successfully with ID: " + fileEntity.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file");
        }
    }
    @PostMapping("/upload-save-by-string")
    public ResponseEntity<String> handleFileUploadSaveByString(
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("data") HandleFileUploadDto data) {

        StringBuilder responseMessage = new StringBuilder();

        // Process each file
        for (MultipartFile file : files) {
            try {
                String content = new BufferedReader(new InputStreamReader(file.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));

                System.out.println("Received file content:");
                System.out.println(content);

                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileName(file.getOriginalFilename());
                fileEntity.setSaveByString(content);
                fileEntity.setContentType(file.getContentType());

                fileEntity.setAccessibility(data.getAccessibility());
                fileEntity.setRecipient(data.getRecipient());
                fileRepository.save(fileEntity);
                responseMessage.append("File saved to MongoDB successfully with ID: ")
                        .append(fileEntity.getId())
                        .append("\n");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + file.getOriginalFilename());
            }
        }

        return ResponseEntity.ok(responseMessage.toString());
    }

}
