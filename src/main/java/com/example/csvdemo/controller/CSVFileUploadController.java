package com.example.csvdemo.controller;

import com.example.csvdemo.dto.FileStatusResponse;
import com.example.csvdemo.dto.FileUploadResponse;
import com.example.csvdemo.dto.ReportDTO;
import com.example.csvdemo.model.CSVFileMetaData;
import com.example.csvdemo.model.CSVFileStatus;
import com.example.csvdemo.model.CSVRejectedRow;
import com.example.csvdemo.repository.CSVFileMetaDataRepository;
import com.example.csvdemo.repository.CSVFileStatusRepository;
import com.example.csvdemo.repository.CSVRejectedRowRepository;
import com.example.csvdemo.service.CSVFileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/csv/files")
public class CSVFileUploadController {

    @Autowired
    private CSVFileProcessingService csvFileProcessingService;

    @Autowired
    private CSVFileMetaDataRepository csvFileMetaDataRepository;

    @Autowired
    private CSVRejectedRowRepository csvRejectedRowRepository;

    @Autowired
    private CSVFileStatusRepository csvFileStatusRepository;

    final static Logger logger = Logger.getLogger(String.valueOf(CSVFileUploadController.class));


    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSVFile(@RequestParam("file") MultipartFile file) {

        System.out.println(file.getOriginalFilename());
        if(file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file format. Please upload a CSV file.");
        }

        //String trackingId = UUID.randomUUID().toString();


        Date dt  = new Date();
        CSVFileMetaData csvFileMetaData = new CSVFileMetaData();
        csvFileMetaData.setFileName(file.getOriginalFilename());
        csvFileMetaData.setAcceptedDate(dt);
        CSVFileMetaData csvFileMetaDataSave = csvFileMetaDataRepository.save(csvFileMetaData);

        // save the file status
        Long fileId = csvFileMetaDataSave.getId();
        CSVFileStatus csvFileStatus = new CSVFileStatus();
        csvFileStatus.setCsvFileMetaDataId(fileId);
        csvFileStatus.setStatus("PENDING");
        csvFileStatusRepository.save(csvFileStatus);

        try {
            csvFileProcessingService.processCSVFile(csvFileMetaData, file.getInputStream(),fileId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid server error. Error:"+e.getMessage());
        }

        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setId(fileId);
        fileUploadResponse.setMessage("File Successfully uploaded and processing started");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(fileUploadResponse);

    }

    @GetMapping("/status2/{id}")
    public ResponseEntity<CSVFileMetaData> getCSVFileStatus2(@PathVariable Long id) {
        return csvFileMetaDataRepository.findById(id).map(file -> ResponseEntity.ok(file)).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> getCSVFileStatus(@PathVariable Long id) {
         List<CSVFileStatus> csvFileStatus = csvFileStatusRepository.findByCsvFileMetaDataId(id);
         if(!csvFileStatus.isEmpty()) {
             FileStatusResponse fileStatusResponse = new FileStatusResponse();
             fileStatusResponse.setId(csvFileStatus.get(0).getId());
             fileStatusResponse.setStatus(csvFileStatus.get(0).getStatus());
             fileStatusResponse.setFileId(csvFileStatus.get(0).getCsvFileMetaDataId());
             String status = csvFileStatus.get(0).getStatus();
             fileStatusResponse.setMessage("File "+status.toLowerCase()+" successfully");
             return ResponseEntity.status(HttpStatus.OK).body(fileStatusResponse);
         } else  {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tracking ID not found");
         }
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable Long id) {
        // fetch the csv file metadata from db
        Optional<CSVFileMetaData> csvFileMetaData = csvFileMetaDataRepository.findById(id);

        if (!csvFileMetaData.isPresent()) {
            // no record found for the cvs file
            ResponseEntity.notFound().build();
        }

        // fetch all rejected rows for the file
        List<CSVRejectedRow> csvRejectedRowList = csvRejectedRowRepository.findByCsvFileMetaDataId(id);

        // build the report with data
        ReportDTO reportDTO = new ReportDTO();

        reportDTO.setFileId(csvFileMetaData.get().getId());
        reportDTO.setFileName(csvFileMetaData.get().getFileName());

        reportDTO.setAcceptedDate(csvFileMetaData.get().getAcceptedDate());
        reportDTO.setProcessedDate(csvFileMetaData.get().getProcessedDate());

        reportDTO.setNumberOfRowsProcessed(csvFileMetaData.get().getRowsProcessed());
        reportDTO.setNumberOfRowsAccepted(csvFileMetaData.get().getRowsAccepted());
        reportDTO.setNumberOfRowsRejected(csvFileMetaData.get().getRowsRejected());

        //Calculate rejections by reason
        long badEmailCnt = csvRejectedRowList.
                stream().
                filter(row -> row.getRejectionReason().equalsIgnoreCase("bad_email")).
                count();

        long badPhoneCnt = csvRejectedRowList.
                stream().
                filter(row -> row.getRejectionReason().equalsIgnoreCase("bad_phone")).
                count();

        long badAadharCnt = csvRejectedRowList.
                stream().
                filter(row -> row.getRejectionReason().equalsIgnoreCase("bad_aadhar_number")).
                count();

        long emptyMandatoryFieldsCnt = csvRejectedRowList.
                stream().
                filter(row -> row.getRejectionReason().equalsIgnoreCase("empty_mandatory_fields")).
                count();


        // set the rejection cnt value
        reportDTO.setNumberOfRowsRejectedBadEmail(badEmailCnt);
        reportDTO.setNumberOfRowsRejectedBadPhone(badPhoneCnt);
        reportDTO.setNumberOfRowsRejectedBadAadhar(badAadharCnt);
        reportDTO.setNumberOfRowsRejectedEmptyMandatoryFields(emptyMandatoryFieldsCnt);

        // return the response
        return ResponseEntity.ok(reportDTO);

    }

}