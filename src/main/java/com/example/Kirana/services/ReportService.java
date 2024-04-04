package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.models.ReportWrapper;
import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import com.example.Kirana.utils.AuthorisationDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportService implements IReportService {
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    AuthorisationDetails currentUserDetails;

    @Autowired
    BucketService bucketService;
    int timeFrame;


    /**
     * @param objectIdString
     * @return LocalDate
     */

    public  LocalDate extractDate(String objectIdString) {
        ObjectId objectId = new ObjectId(objectIdString);

        int timestamp = objectId.getTimestamp();

        long timestampInMillis = timestamp * 1000L;

        return Instant.ofEpochMilli(timestampInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * @param period
     * @return {@code ResponseEntity<String>}
     * @throws JsonProcessingException
     */
    public ResponseEntity<String> generateReport(String period) throws JsonProcessingException {
        log.info("Calling " + period + "ly Report");
        String username = currentUserDetails.getUsername();
        if (bucketService.UserLimitExceeded(username, RateLimitingBucketStorage.reportBucket)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }

        return ResponseEntity.ok(processReport(period));
    }

    /**
     * @param period
     * @return String
     * @throws JsonProcessingException
     */
    public String processReport(String period) throws JsonProcessingException {
        List<TransactionDetails> td = transactionRepo.findAll();
        Map<Integer, ReportWrapper> wrManager = new HashMap<>();
        ReportWrapper reportWrapper;
        for (TransactionDetails transactionDetails : td) {
            timeFrame = getTimeFrame(transactionDetails.getId(), period);
            if (wrManager.containsKey(timeFrame)) {
                reportWrapper = wrManager.get(timeFrame);
                reportWrapper.mergeData(transactionDetails.getCurrencyUsed(), transactionDetails.getTransactionAmount(), transactionDetails.getTransactionType());
            } else {
                wrManager.put(timeFrame, new ReportWrapper(timeFrame, transactionDetails.getCurrencyUsed(), transactionDetails.getTransactionAmount(), transactionDetails.getTransactionType()));
            }
        }
        return new ObjectMapper().writeValueAsString(wrManager);
    }

    /**
     * @param dateFromObjectId
     * @param period
     * @return int
     */
    public int getTimeFrame(Object dateFromObjectId, String period) {
        LocalDate date = extractDate(String.valueOf(dateFromObjectId));
        switch (period) {
            case "year":
                return date.getYear();
            case "week":
                return date.get(WeekFields.ISO.weekOfWeekBasedYear());
            case "month":
                return date.getMonthValue();
        }
        return getTimeFrame(dateFromObjectId, "year");
    }
}
