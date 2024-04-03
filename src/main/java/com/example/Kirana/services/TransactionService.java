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

    /**
     * Initiate Recording the Transaction
     *
     * @param newTransactionRecord
     * @return The result of recording the transaction.
      *         <ul>
      *             <li>{@code OK with recorded Transaction}, if the transaction was successfully recorded.</li>
      *             <li>{@code TOO_MANY_REQUESTS}, if the transaction recording failed.</li>
      *         </ul>
     */
    public ResponseEntity<Object> RecordSingleTransaction(TransactionDetails newTransactionRecord) {
        String currentUser = currentUserDetails.getUsernameFromAuthorizationHeader();
        if(UserLimitExceeded(currentUser)){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }
        CommitToMongo(newTransactionRecord);
        return ResponseEntity.ok("Transaction Recorded Successfully\n" + newTransactionRecord.toString());
    }


    /**
     * Commit a Single Transaction To MongoDB
     *
     * @param newTransactionRecord
     */
    private void CommitToMongo(TransactionDetails newTransactionRecord) {
        if (!newTransactionRecord.getCurrencyUsed().equals("USD")) {
            newTransactionRecord.setTransactionAmount(cc.conversion(newTransactionRecord.getCurrencyUsed(), newTransactionRecord.getTransactionAmount()));
        }
        tr.save(newTransactionRecord);
    }

    /**
     * Retrieve Token Bucket for a User
     *
     * @param userBucket
     * @return User specific Token Bucket
     *<ul>
     *<li>{@code IF USER ALREADY ACCESSED IT BEFORE}, Retrieve User Token Bucket </li>
     *<li>{@code IF USER ACCESSING FOR FIRST TIME}, Generate User Token Bucket</li>
     *</ul>
     */
    public Bucket GetTokenBucket(String userBucket){
        if(RateLimitingBucketStorage.transactionEndpointBucket.containsKey(userBucket)){
            return RateLimitingBucketStorage.transactionEndpointBucket.get(userBucket);
        }
        return GenerateTokenBucket(userBucket);
    }

    /**
     * Generate Token Bucket for a User
     *
     * @param userBucket
     * @return User specific Token Bucket
     */
    public Bucket GenerateTokenBucket(String userBucket){
        RateLimitingBucketStorage.transactionEndpointBucket.put(userBucket, rateLimiter.resolveBucket(userBucket));
        return GetTokenBucket(userBucket);
    }
    /**
     * Check if User API Limit Exceeded
     *
     * @param username
     * @return
     * <ul>
     *     <li>{@code true}, if limit exceeded</li>
     *     <li>{@code false}, if limit not exceeded</li>
     * </ul>
     */
    public Boolean UserLimitExceeded (String username){
        Bucket UserBucket= GetTokenBucket(username);
        return !UserBucket.tryConsume(1);
    }

}