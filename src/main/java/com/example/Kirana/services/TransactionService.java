package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import com.example.Kirana.utils.AuthorisationDetails;
import com.example.Kirana.utils.CurrencyConversion;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    TransactionRepo tr;
    @Autowired
    AuthorisationDetails currentUserDetails;
    @Autowired
    RateLimiter rateLimiter;
    @Autowired
    CurrencyConversion cc;

    public ResponseEntity<Object> addSingleTransactionEntryToDB(TransactionDetails newTransactionRecord) {
        String currentUser = currentUserDetails.getUsernameFromAuthorizationHeader();
        if(UserLimitExceeded(currentUser)){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }
        saveTransactionToMongoDB(newTransactionRecord);
        return ResponseEntity.ok("Transaction Recorded Successfully\n" + newTransactionRecord.toString());
    }

    private void saveTransactionToMongoDB(TransactionDetails newTransactionRecord) {
        if (!newTransactionRecord.getCurrencyUsed().equals("USD")) {
            newTransactionRecord.setTransactionAmount(cc.conversion(newTransactionRecord.getCurrencyUsed(), newTransactionRecord.getTransactionAmount()));
        }
        tr.save(newTransactionRecord);
    }

    public Bucket GetUserTokenBucketElseCreate(String currentUserBucketUsername){
        if(RateLimitingBucketStorage.transactionEndpointBucket.containsKey(currentUserBucketUsername)){
            return RateLimitingBucketStorage.transactionEndpointBucket.get(currentUserBucketUsername);
        }
        RateLimitingBucketStorage.transactionEndpointBucket.put(currentUserBucketUsername, rateLimiter.resolveBucket(currentUserBucketUsername));
        return GetUserTokenBucketElseCreate(currentUserBucketUsername);
    }

    public Boolean UserLimitExceeded (String currentUsername){
        Bucket UserBucket=GetUserTokenBucketElseCreate(currentUsername);
        return !UserBucket.tryConsume(1);
    }

}