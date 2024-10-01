package com.example.csvdemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CSV_FILE_STATUS")
public class CSVFileStatus {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CSV_FILE_METADATA_ID")
    private Long csvFileMetaDataId;

    @Column(name = "STATUS")
    private String status;

}