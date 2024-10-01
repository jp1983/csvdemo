package com.example.csvdemo.repository;

import com.example.csvdemo.model.CSVFileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CSVFileStatusRepository extends JpaRepository<CSVFileStatus, Long> {
 List<CSVFileStatus> findByCsvFileMetaDataId(Long csvFileMetaDataId);
}