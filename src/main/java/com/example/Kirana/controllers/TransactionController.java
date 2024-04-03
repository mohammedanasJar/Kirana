package com.example.Kirana.controllers;

import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.services.GenerateRandomTransactionService;
import com.example.Kirana.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;
    @Autowired
    GenerateRandomTransactionService generateRandomTransactionService;
    
    /**
     * Record a Single Transaction
     *
     * @param transactionDetails
     * @return Success or Error Message of recording the transaction
     *
     */
    @PostMapping
    public ResponseEntity<Object> transaction(@RequestBody TransactionDetails transactionDetails) {
        return transactionService.RecordSingleTransaction(transactionDetails);
    }
    
    /**
     * Generate Random Transaction Data
     *
     * @return Success/Failure of recording data
     *
     */
    @PostMapping("/generate")
    public ResponseEntity<Object> setupRandomTransactionData(){
     return generateRandomTransactionService.generateTransaction();
    }

}