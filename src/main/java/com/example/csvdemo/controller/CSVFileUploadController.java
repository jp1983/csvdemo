package com.example.csvdemo.controller;

import com.example.csvdemo.dto.FileStatusResponse;
import com.example.csvdemo.dto.FileUploadResponse;
import com.example.csvdemo.model.CSVFileMetaData;

import com.example.csvdemo.service.CSVFileProcessingService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@RestController
@RequestMapping("/csv/files")
public class CSVFileUploadController {

    @Autowired
    private CSVFileProcessingService csvFileProcessingService;

    final static Logger logger = Logger.getLogger(String.valueOf(CSVFileUploadController.class));

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSVFile(@RequestParam("file") MultipartFile file) {

        if(file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file format. Please upload a CSV file.");
        }

        CSVFileMetaData csvFileMetaData = csvFileProcessingService.saveCsvFileMetaData(file.getOriginalFilename());
        Long fileId = csvFileMetaData.getId();
        csvFileProcessingService.saveFileStatus();

        try {
            csvFileProcessingService.processCSVFile(csvFileMetaData, file.getInputStream());
        } catch (Exception e) {
            //logger.error("Error processing CSV file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid server error. Error:"+e.getMessage());
        }

        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setId(fileId);
        fileUploadResponse.setMessage("File Successfully uploaded and processing started");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(fileUploadResponse);

    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> getCSVFileStatus(@PathVariable Long id) {
         FileStatusResponse fileStatusResponse = csvFileProcessingService.getCSVFileStatus(id);
         if (fileStatusResponse.getId() != null) {
             return ResponseEntity.status(HttpStatus.OK).body(fileStatusResponse);
         } else {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tracking ID not found");
         }
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        // return the response
        return csvFileProcessingService.getReport(id);

    }

}