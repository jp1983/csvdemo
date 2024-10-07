package com.example.csvdemo.service;

import com.example.csvdemo.model.CSVFileMetaData;
import com.example.csvdemo.model.CSVFileStatus;
import com.example.csvdemo.model.CSVRejectedRow;
import com.example.csvdemo.repository.CSVFileMetaDataRepository;
import com.example.csvdemo.repository.CSVFileStatusRepository;
import com.example.csvdemo.repository.CSVRejectedRowRepository;
import com.example.csvdemo.validator.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CSVFileProcessingServiceTest {

   /* @Autowired
    private CSVFileMetaDataRepository csvFileMetaDataRepository;

    @Autowired
    private CSVFileStatusRepository csvFileStatusRepository;

    @Autowired
    private CSVRejectedRowRepository csvRejectedRowRepository;

    @Autowired
    private CSVFileProcessingService csvFileProcessingService;

    private final ExecutorService executorService =  mock(ExecutorService.class);*/

    private final EmailValidator emailValidator = new EmailValidator();

    /*
    @Test
    void processCSVFile() throws IOException {
        String csvContent = "Employee ID,Email,First Name,Last Name,Designation,Aadhar,Phone\n"+
                "1,test@gmail.com,Jagadish,Patil,Software Engineer,123456789012,9096732204\n"+
                "1,invalid_email,Ram,Patil,Sr. Software Engineer,123456789013,9096732209\n";

        MultipartFile mockFile = mock(MultipartFile.class);
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        when(mockFile.getInputStream()).thenReturn(inputStream);

        CSVFileMetaData csvFileMetaData = csvFileProcessingService.saveCsvFileMetaData("file1.csv");
        Long fileId = csvFileMetaData.getId();
        csvFileProcessingService.saveFileStatus();

        csvFileProcessingService.processCSVFile(csvFileMetaData,inputStream);

        // verity that roes were submitted for validation
        verify(executorService, times(2)).submit(any(Runnable.class));

        // ensure that the executor shuts down after processing
        verify(executorService, times(1)).shutdown();

    }

    void validateRowTest() throws Exception {

        // Prepare mock futures to simulate field validation results
        Future<Boolean> mockMandatoryFieldFuture = mock(Future.class);
        Future<Boolean> mockEmailFuture = mock(Future.class);
        Future<Boolean> mockAadharFuture = mock(Future.class);
        Future<Boolean> mockPhoneFuture = mock(Future.class);

        when(executorService.submit(any(Callable.class)))
                .thenReturn(mockMandatoryFieldFuture)
                .thenReturn(mockEmailFuture)
                .thenReturn(mockAadharFuture)
                .thenReturn(mockPhoneFuture);

        //mock the validation results
        when(mockMandatoryFieldFuture.get()).thenReturn(true);
        when(mockEmailFuture.get()).thenReturn(true);
        when(mockAadharFuture.get()).thenReturn(true);
        when(mockPhoneFuture.get()).thenReturn(true);

        Date dt = new Date();

        String csvRow = "1,test@gmail.com,Jagadish,Patil,Software Engineer,123456789012,9096732204\n";
        CSVFileMetaData csvFileMetaData = new CSVFileMetaData();
        csvFileMetaData.setFileName("file1.csv");
        csvFileMetaData.setAcceptedDate(dt);

        List<CSVRejectedRow> csvRejectedRowList = new ArrayList<>();

        // Call the method under test
        csvFileProcessingService.validateRow(csvRow.split(","), 1, csvFileMetaData, csvRejectedRowList, dt);

        // verify that all fields were validated using the executorService
        verify(executorService, times(4)).submit(any(Callable.class));

    }*/

    @Test
    void validatorEmailTest() {
        assertTrue("Valid Email", emailValidator.validate( "test@gmail.com"));
        assertFalse("Invalid Email", emailValidator.validate("Invalid-email"));
    }

}
