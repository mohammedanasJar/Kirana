package com.example.Kirana.controllers;


import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.services.TransactionService;
import com.example.Kirana.utils.CurrencyConversion;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/transactions")
public class TransactionAPI {

    private final Bucket bucket;
    @Autowired
    TransactionService ts;

    public TransactionAPI(){
        Bandwidth limit=Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }
    @Autowired
    CurrencyConversion cc;
    @PostMapping("/record")
    public  String setTransact(@RequestBody TransactionDetails tt){
        if(!tt.getCurrencyUsed().equals("USD")){
            tt.setTransactionAmount(cc.conversion(tt.getCurrencyUsed(),tt.getTransactionAmount()));
        }
        ts.setTransaction(tt);
        return "test value is "+tt.toString();
    }


    @GetMapping("/getrate")
    public ResponseEntity<Object> getconversion(){
        if(bucket.tryConsume(1)) {
            System.out.println("Conversion");
            return ResponseEntity.ok(cc.fetchRates());
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

   }
}