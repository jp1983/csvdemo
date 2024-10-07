package com.example.csvdemo.service;

import com.example.csvdemo.dto.FileStatusResponse;
import com.example.csvdemo.dto.ReportDTO;
import com.example.csvdemo.model.CSVFileMetaData;
import com.example.csvdemo.model.CSVFileStatus;
import com.example.csvdemo.model.CSVRejectedRow;
import com.example.csvdemo.model.Employee;
import com.example.csvdemo.repository.CSVFileMetaDataRepository;
import com.example.csvdemo.repository.CSVFileStatusRepository;
import com.example.csvdemo.repository.CSVRejectedRowRepository;
import com.example.csvdemo.repository.EmployeeRepository;
import com.example.csvdemo.validator.AadharValidator;
import com.example.csvdemo.validator.EmailValidator;
import com.example.csvdemo.validator.MandatoryFieldValidator;
import com.example.csvdemo.validator.PhoneValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CSVFileProcessingService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CSVFileMetaDataRepository csvFileMetaDataRepository;

    @Autowired
    private CSVRejectedRowRepository csvRejectedRowRepository;

    @Autowired
    private CSVFileStatusRepository csvFileStatusRepository;

    final static Logger logger = Logger.getLogger(String.valueOf(CSVFileProcessingService.class));

    private Long fileId;

    @Async
    public void processCSVFile(CSVFileMetaData csvFileMetaData, InputStream inputStream) {

        Long fileId = this.fileId;

        int rowsProcessed = 0;
        int rowsAccepted = 0;
        int rowsRejected = 0;

        List<CSVFileStatus>  csvFileStatus = csvFileStatusRepository.findByCsvFileMetaDataId(fileId);
        CSVFileStatus CSVFileStatusUpdate =  csvFileStatus.get(0);

        Date dt = new Date();

        List<CSVRejectedRow> csvRejectedRowList = new ArrayList<>();

        List<Future<Boolean>> futures = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // parse the csv file and validate each row in parallel

        try (BufferedReader fileReader
                     = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = "";
            int rowNumber = 0;

            CSVFileStatusUpdate.setStatus("PROCESSING");
            csvFileStatusRepository.save(CSVFileStatusUpdate);

            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {
                // counts of rows and processed
                rowNumber++;
                rowsProcessed++;

                if (rowNumber == 1 || line.isEmpty()){
                    continue;
                }

                //Get all columns available in line
                String[] row = line.split(",");
                int finalRowNumber = (rowNumber-1);
                Future<Boolean> future = executor.submit(() -> validateRow(row, finalRowNumber, csvFileMetaData, csvRejectedRowList, dt));
                futures.add(future);

                //logger.log(Level.INFO, Arrays.toString(row));
            }

            // parallel validation and verify of row columns data
            for(Future<Boolean> future : futures){
                if(future.get()) {
                    rowsAccepted++;
                } else {
                    rowsRejected++;
                }
            }

            csvFileMetaData.setProcessedDate(dt);
            csvFileMetaData.setRowsProcessed(Long.valueOf((rowsProcessed-1)));
            csvFileMetaData.setRowsAccepted(Long.valueOf(rowsAccepted));
            csvFileMetaData.setRowsRejected(Long.valueOf(rowsRejected));
            csvFileMetaDataRepository.save(csvFileMetaData);

            // set all rejected rows
            csvRejectedRowRepository.saveAll(csvRejectedRowList);

            CSVFileStatusUpdate.setStatus("COMPLETED");
            csvFileStatusRepository.save(CSVFileStatusUpdate);

        } catch (IOException | InterruptedException | java.util.concurrent.ExecutionException e) {
           // e.printStackTrace();
            logger.log(Level.INFO, e.getMessage());
            CSVFileStatusUpdate.setStatus("FAILED");
            csvFileStatusRepository.save(CSVFileStatusUpdate);
        }

        executor.shutdown();


    }

    @Async
    public void processCSVFile2(CSVFileMetaData csvFileMetaData, InputStream inputStream) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // parse the csv file and validate each row in parallel
        try (BufferedReader fileReader
                     = new BufferedReader(new InputStreamReader(inputStream))) {

           // multithreading of csv rows
            List<String> rows = fileReader.lines().skip(1).toList();
            rows.parallelStream().forEach(
                    row -> {
                        executor.submit(()->validateRow2(row, csvFileMetaData));
                    }
            );

        } catch (IOException e) {
           // throw new RuntimeException(e);
        }
    }

    public boolean validateRow(String[] row, int rowNumber,  CSVFileMetaData csvFileMetaData,
                                List<CSVRejectedRow> csvRejectedRowList, Date dt) {

        String employeeId  = row[0];
        String email  = row[1];
        String firstName  = row[2];
        String lastName  = row[3];
        String aadhar  = row[5];
        String phone  = row[6];

        ExecutorService executor = Executors.newFixedThreadPool(4);

        List<Future<Boolean>> validationFutures = new ArrayList<>();

        Future<Boolean> emailValidation = executor.submit(() -> EmailValidator.validate(email));
        Future<Boolean> phoneValidation = executor.submit(() -> PhoneValidator.validate(phone));
        Future<Boolean> aadharValidation = executor.submit(() -> AadharValidator.validate(aadhar));
        Future<Boolean> mandatoryFieldsValidation = executor.submit(() -> MandatoryFieldValidator.validate(employeeId, email, firstName, lastName, aadhar));

        validationFutures.add(emailValidation);
        validationFutures.add(phoneValidation);
        validationFutures.add(aadharValidation);
        validationFutures.add(mandatoryFieldsValidation);

        // check results and reject if necessary
        boolean isValid = true;
        String rejectionReason = "";

        try{

            if(!mandatoryFieldsValidation.get()) {
                isValid = false;
                rejectionReason = "empty_mandatory_fields";
            } else if (!emailValidation.get()){
                isValid = false;
                rejectionReason = "bad_email";
            } else if (!phoneValidation.get()){
                isValid = false;
                rejectionReason = "bad_phone";
            } else if (!aadharValidation.get()){
                isValid = false;
                rejectionReason = "bad_aadhar_number";
            }
        } catch (InterruptedException | ExecutionException e){
            logger.log(Level.INFO,  e.getMessage());
            isValid = false;
            rejectionReason = "validation_error";
        } finally {
            executor.shutdown();
        }

        if (isValid) {
            // current row is valid and save in employee table
            Employee employee = new Employee();
            employee.setEmployeeId(row[0]);
            employee.setEmail(row[1]);
            employee.setFirstName(row[2]);
            employee.setLastName(row[3]);
            employee.setDesignation(row[4]);
            employee.setAadharNumber(row[5]);
            employee.setPhoneNumber(row[6]);
            employee.setCreatedDate(dt);
            employeeRepository.save(employee);
            return true;
        } else {
            // this row is invalid so save it as the rejection reason
            CSVRejectedRow csvRejectedRow = new CSVRejectedRow();
            csvRejectedRow.setCsvFileMetaDataId(csvFileMetaData.getId());
            csvRejectedRow.setRowNumber(Long.valueOf(rowNumber));
            csvRejectedRow.setRejectionReason(rejectionReason);
            csvRejectedRowList.add(csvRejectedRow);
            return false;
        }
    }

    public CSVFileMetaData saveCsvFileMetaData(String fieName) {
        //String trackingId = UUID.randomUUID().toString();
        Date dt  = new Date();
        CSVFileMetaData csvFileMetaData = new CSVFileMetaData();
        csvFileMetaData.setFileName(fieName);
        csvFileMetaData.setAcceptedDate(dt);
        CSVFileMetaData csvFileMetaDataSave = csvFileMetaDataRepository.save(csvFileMetaData);
        this.fileId = csvFileMetaDataSave.getId();
        return csvFileMetaDataSave;
    }

    public void saveFileStatus() {
        // save the file status
        CSVFileStatus csvFileStatus = new CSVFileStatus();
        csvFileStatus.setCsvFileMetaDataId(this.fileId);
        csvFileStatus.setStatus("PENDING");
        csvFileStatusRepository.save(csvFileStatus);
    }

    public FileStatusResponse getCSVFileStatus(Long id) {
        List<CSVFileStatus> csvFileStatus = csvFileStatusRepository.findByCsvFileMetaDataId(id);
        FileStatusResponse fileStatusResponse = new FileStatusResponse();
        if(!csvFileStatus.isEmpty()) {
            fileStatusResponse.setId(csvFileStatus.get(0).getId());
            fileStatusResponse.setStatus(csvFileStatus.get(0).getStatus());
            fileStatusResponse.setFileId(csvFileStatus.get(0).getCsvFileMetaDataId());
            String status = csvFileStatus.get(0).getStatus();
            fileStatusResponse.setMessage("File "+status.toLowerCase()+" successfully");
        }
        return fileStatusResponse;
    }

    public ResponseEntity<?> getReport(Long id) {
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

    private void validateRow2(String row, CSVFileMetaData csvFileMetaData) {
        // code here
    }
}
