package com.example.Kirana.controllers;

import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.services.CurrencyConversionFetchJSONService;
import com.example.Kirana.services.GenerateRandomTransactionService;
import com.example.Kirana.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionAPI {

    @Autowired
    TransactionService ts;
    @Autowired
    CurrencyConversionFetchJSONService ccs;

    @Autowired
    GenerateRandomTransactionService grts;

    @PostMapping("/record")
    public ResponseEntity<Object> endpointToRecordSingleTransaction(@RequestBody TransactionDetails tt) {
        return ts.addSingleTransactionEntryToDB(tt);
    }

    @GetMapping("/fetchrate")
    public ResponseEntity<Object> endpointToFetchCurrencyConversionJSON() {
        return ccs.fetchCurrencyConversionJSON();
    }

    @GetMapping("/generateData")
    public ResponseEntity<Object> setupRandomTransactionData(){
     return grts.generateTransaction();
    }

}