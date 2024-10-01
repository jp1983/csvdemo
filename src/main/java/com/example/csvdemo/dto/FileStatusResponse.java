package com.example.csvdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileStatusResponse {
    private Long id;
    private String status;
    private Long fileId;
    private String message;
}
