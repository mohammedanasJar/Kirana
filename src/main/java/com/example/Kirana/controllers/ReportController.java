package com.example.Kirana.controllers;

import com.example.Kirana.services.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    ReportService rs;

    /**
     *
     * @param period
     * @return {@code ResponseEntity<String>}
     */
    @GetMapping("/{period}")
    public ResponseEntity<String> Report(@PathVariable String period) throws JsonProcessingException {
       return rs.getReport(period);
    }
}
