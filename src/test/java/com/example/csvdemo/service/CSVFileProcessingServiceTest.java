package com.example.csvdemo.service;

import com.example.csvdemo.model.CSVFileMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class CSVFileProcessingServiceTest {

    @Autowired
    private CSVFileProcessingService csvFileProcessingService;

    @Autowired
    private MultipartFile mockMultipartFile;

    @BeforeEach
    void setUp(){
        csvFileProcessingService = new CSVFileProcessingService();
        mockMultipartFile = Mockito.mock(MultipartFile.class);
    }

    @Test
    void testProcessFileSuccess() throws IOException {
        Date dt = new Date();
        CSVFileMetaData csvFileMetaData = new CSVFileMetaData(1L, "employees.csv",dt);

    }
}
