package com.example.Kirana.controllers;

import com.example.Kirana.constants.APIRateLimiting;
import com.example.Kirana.services.RateLimiter;
import com.example.Kirana.services.ReportService;
import com.example.Kirana.utils.AuthorisationDetails;
import io.github.bucket4j.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/record")
public class ReportAPI {
    private static final Logger logger = LoggerFactory.getLogger(ReportAPI.class);

    @Autowired
    RateLimiter rateLimiter;
    AuthorisationDetails ad = new AuthorisationDetails();
    @Autowired
    ReportService rs;

    @GetMapping("/{period}")
    public ResponseEntity Report(@PathVariable String period) {
        logger.info("Calling " + period + "'s ReportingAPI");
        String username = ad.getUsernameFromAuthorizationHeader();
        if (APIRateLimiting.reportBucket.containsKey(username)) {
            Bucket mybucket = APIRateLimiting.reportBucket.get(username);
            if (mybucket.tryConsume(1)) {
                switch (period) {
                    case "week":
                        return ResponseEntity.ok(rs.getWeekReport());
                    case "month":
                        return ResponseEntity.ok(rs.getMonthReport());
                    case "year":
                        return ResponseEntity.ok(rs.getYearReport());
                    default:
                        return ResponseEntity.notFound().build();
                }
            } else {
                logger.warn("API Limit Exceeded for " + username);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
            }
        } else {
            logger.info("Creating a new Bucket for " + username);
            APIRateLimiting.reportBucket.put(username, rateLimiter.resolveBucket(username));
            return this.Report(period);
        }
    }
}
