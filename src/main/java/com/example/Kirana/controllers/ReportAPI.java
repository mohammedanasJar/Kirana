package com.example.Kirana.controllers;

import com.example.Kirana.services.ReportService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/record")
public class ReportAPI {
    private final Bucket bucket;

    @Autowired
    ReportService rs;

    public ReportAPI() {
        Bandwidth limit=Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();    }

    @GetMapping("/{period}")
    public ResponseEntity<Object> Report(@PathVariable String period){
        if(bucket.tryConsume(1)) {
            switch (period) {
                case "week":
                    return ResponseEntity.ok(rs.getWeekReport());
                case "month":
                    System.out.println("Calling report");
                    return ResponseEntity.ok(rs.getMonthReport());
                case "year":
                    System.out.println("Calling report");
                    return ResponseEntity.ok(rs.getYearReport());
                default :
                    return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

    }
}
