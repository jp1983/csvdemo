package com.example.csvdemo.repository;

import com.example.csvdemo.model.CSVFileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CSVFileMetaDataRepository extends JpaRepository<CSVFileMetaData, Long> {
}