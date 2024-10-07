package com.example.csvdemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "CSV_FILE_METADATA")
public class CSVFileMetaData {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "ACCEPTED_DATE")
    private Date acceptedDate;

    @Column(name = "PROCESSED_DATE")
    private Date processedDate;

    @Column(name = "ROWS_PROCESSED")
    private Long rowsProcessed;

    @Column(name = "ROWS_ACCEPTED")
    private Long rowsAccepted;

    @Column(name = "ROWS_REJECTED")
    private Long rowsRejected;

}
