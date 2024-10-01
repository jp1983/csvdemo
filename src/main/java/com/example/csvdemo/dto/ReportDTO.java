package com.example.csvdemo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ReportDTO {
    private Long fileId;
    private String fileName;
    private Date acceptedDate;
    private Date processedDate;
    private Long numberOfRowsProcessed;
    private Long numberOfRowsAccepted;
    private Long numberOfRowsRejected;
    private Long numberOfRowsRejectedBadEmail;
    private Long numberOfRowsRejectedBadPhone;
    private Long numberOfRowsRejectedBadAadhar;
    private Long numberOfRowsRejectedEmptyMandatoryFields;
}