package com.example.csvdemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "CSV_REJECTED_ROW")
public class CSVRejectedRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CSV_FILE_METADATA_ID")
    private Long csvFileMetaDataId;

    @Column(name = "ROWS_NUMBER")
    private Long rowNumber;

    @Column(name = "REJECTED_REASON")
    private String rejectionReason;

}