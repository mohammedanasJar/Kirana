package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.models.MyReport;
import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import com.example.Kirana.utils.AuthorisationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
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

@Service
public class ReportService {
    @Autowired
    TransactionRepo tr;
    @Autowired
    AuthorisationDetails currentUserDetails;
    @Autowired
    RateLimiter rateLimiter;
    public ResponseEntity getMonthReport() {
        try {
            String currentUser = currentUserDetails.getUsername();
            if (UserLimitExceeded(currentUser)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
            }
            List<TransactionDetails> td = tr.findAll();
            int weekNum;
            Map<Integer, MyReport> wrManager = new HashMap<>();
            MyReport wr;
            for (TransactionDetails t : td) {
                weekNum = getDateFromObjectId(String.valueOf(t.getId())).getMonthValue();
                if (wrManager.containsKey(weekNum)) {
                    wr = wrManager.get(weekNum);
                    wr.mergeData(t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType());
                } else {
                    wrManager.put(weekNum, new MyReport(weekNum, t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType()));
                }
            }
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(wrManager));
        }
        catch (Exception e){
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sorry for the Inconvenience");
        }
    }

    public ResponseEntity getWeekReport() {
        try {
            String currentUser = currentUserDetails.getUsername();
            if (UserLimitExceeded(currentUser)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
            }
            List<TransactionDetails> td = tr.findAll();
            int weekNum;
            Map<Integer, MyReport> wrManager = new HashMap<>();
            MyReport wr;
            for (TransactionDetails t : td) {
                weekNum = getDateFromObjectId(String.valueOf(t.getId())).get(WeekFields.ISO.weekOfWeekBasedYear());
                if (wrManager.containsKey(weekNum)) {
                    wr = wrManager.get(weekNum);
                    wr.mergeData(t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType());
                } else {
                    wrManager.put(weekNum, new MyReport(weekNum, t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType()));
                }
            }
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(wrManager));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sorry for the Inconvenience");
        }
    }

    public ResponseEntity getYearReport() {
        try {
            String currentUser = currentUserDetails.getUsername();
            if (UserLimitExceeded(currentUser)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
            }
            List<TransactionDetails> td = tr.findAll();
            int weekNum;
            Map<Integer, MyReport> wrManager = new HashMap<>();
            MyReport wr;
            for (TransactionDetails t : td) {
                weekNum = getDateFromObjectId(String.valueOf(t.getId())).getYear();
                if (wrManager.containsKey(weekNum)) {
                    wr = wrManager.get(weekNum);
                    wr.mergeData(t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType());
                } else {
                    wrManager.put(weekNum, new MyReport(weekNum, t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType()));
                }
            }
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(wrManager));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sorry for the Inconvenience");
        }
    }

    public static LocalDate getDateFromObjectId(String objectIdString) {
        ObjectId objectId = new ObjectId(objectIdString);

        int timestamp = objectId.getTimestamp();

        long timestampInMillis = timestamp * 1000L;

        return Instant.ofEpochMilli(timestampInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public Bucket GetUserTokenBucketElseCreate(String currentUserBucketUsername){
        if(RateLimitingBucketStorage.reportBucket.containsKey(currentUserBucketUsername)){
            return RateLimitingBucketStorage.reportBucket.get(currentUserBucketUsername);
        }
        RateLimitingBucketStorage.reportBucket.put(currentUserBucketUsername, rateLimiter.createBucket(currentUserBucketUsername));
        return GetUserTokenBucketElseCreate(currentUserBucketUsername);
    }

    public Boolean UserLimitExceeded (String currentUsername){
        Bucket UserBucket=GetUserTokenBucketElseCreate(currentUsername);
        return !UserBucket.tryConsume(1);
    }
}
