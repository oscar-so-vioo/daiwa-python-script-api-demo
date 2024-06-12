package com.example.daiwapythonscriptapidemo.controllers;


import com.example.daiwapythonscriptapidemo.dto.HandleFileUploadDto;
import com.example.daiwapythonscriptapidemo.entities.FileEntity;
import com.example.daiwapythonscriptapidemo.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UploadController {

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload-save-by-bytes")
    public ResponseEntity<String> handleFileUploadSaveByBytes(@RequestParam("file") MultipartFile file) {
        try {
            byte[] content = file.getBytes();

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setSaveByBytes(content);
            fileEntity.setContentType(file.getContentType());
            fileRepository.save(fileEntity);

            return ResponseEntity.ok("File saved to MongoDB successfully with ID: " + fileEntity.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file");
        }
    }
    @PostMapping("/upload-save-by-string")
    public ResponseEntity<String> handleFileUploadSaveByString(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("accessibility") List<String> accessibilityList,
            @RequestParam("recipient") List<String> recipientList
            ) {

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

                fileEntity.setAccessibility(accessibilityList);
                fileEntity.setRecipient(recipientList);
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
