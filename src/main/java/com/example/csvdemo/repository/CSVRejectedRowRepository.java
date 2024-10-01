package com.example.csvdemo.repository;

import com.example.csvdemo.model.CSVRejectedRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CSVRejectedRowRepository extends JpaRepository<CSVRejectedRow, Long> {
    List<CSVRejectedRow> findByCsvFileMetaDataId(Long cvsFileMetaDataId);
}
