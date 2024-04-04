package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import com.example.Kirana.utils.AuthorisationDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TransactionService {
    @Autowired
    TransactionRepo tr;

    @Autowired
    AuthorisationDetails currentUserDetails;
    @Autowired
    BucketService bucketService;

    @Autowired
    ExchangeRateService cc;



    /**
     * Initiate Recording the Transaction
     *
     * @param newTransactionRecord
     * @return The result of recording the transaction.
     * <ul>
     *     <li>{@code OK with recorded Transaction}, if the transaction was successfully recorded.</li>
     *     <li>{@code TOO_MANY_REQUESTS}, if the transaction recording failed.</li>
     * </ul>
     * @throws JsonProcessingException
     */
    public ResponseEntity<Object> recordSingleTransaction(TransactionDetails newTransactionRecord) throws JsonProcessingException {
        String currentUser = currentUserDetails.getUsername();
        if (bucketService.UserLimitExceeded(currentUser, RateLimitingBucketStorage.transactionEndpointBucket)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }
        commitToMongo(newTransactionRecord);
        return ResponseEntity.ok("Transaction Recorded Successfully\n" + newTransactionRecord.toString());
    }


    /**
     * Commit a Single Transaction To MongoDB
     *
     * @param newTransactionRecord
     * @throws JsonProcessingException
     */
    private void commitToMongo(TransactionDetails newTransactionRecord) throws JsonProcessingException {
        if (!newTransactionRecord.getCurrencyUsed().equals("USD")) {
            newTransactionRecord.setTransactionAmount(convertForeignExchangeRates(newTransactionRecord.getCurrencyUsed(), newTransactionRecord.getTransactionAmount()));
        }
        tr.save(newTransactionRecord);
    }
    /**
     * Commit a Single Transaction To MongoDB
     *
     * @param currency
     * @param amount
     * @return Generate Standard Currency
     * @throws JsonProcessingException
     */
    public double convertForeignExchangeRates(String currency, double amount) throws JsonProcessingException {
        String exchangeRatesJSON = cc.fetchExchangeRateJSON();
        HashMap exchangeRatesHashmap = new ObjectMapper().readValue(exchangeRatesJSON, HashMap.class);
        HashMap map = (HashMap) exchangeRatesHashmap.get("rates");
        return amount / (double) map.get(currency);
    }

}