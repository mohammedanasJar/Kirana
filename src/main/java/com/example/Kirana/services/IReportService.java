package com.example.Kirana.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface IReportService {
    public LocalDate extractDate(String objectIdString);

    public ResponseEntity<String> generateReport(String period) throws JsonProcessingException;

    public String processReport(String period) throws JsonProcessingException;

    int getTimeFrame(Object dateFromObjectId, String period);

}

