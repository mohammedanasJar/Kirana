package com.example.Kirana.controllers;


import com.example.Kirana.constants.APIRateLimiting;
import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.services.AuthorisationDetails;
import com.example.Kirana.services.RateLimiter;
import com.example.Kirana.services.TransactionService;
import com.example.Kirana.utils.CurrencyConversion;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionAPI {
    AuthorisationDetails ad=new AuthorisationDetails();
    @Autowired
    RateLimiter rateLimiter;
    @Autowired
    TransactionService ts;

    @Autowired
    CurrencyConversion cc;
    @PostMapping("/record")
    public  ResponseEntity<Object> setTransact(@RequestBody TransactionDetails tt){
        String username=ad.getUsernameFromAuthorizationHeader();
        if(APIRateLimiting.transactionBucket.containsKey(username)){
            Bucket mybucket=APIRateLimiting.transactionBucket.get(username);
            if (mybucket.tryConsume(1)) {
                if(!tt.getCurrencyUsed().equals("USD")){
                    tt.setTransactionAmount(cc.conversion(tt.getCurrencyUsed(),tt.getTransactionAmount()));
                }
                ts.setTransaction(tt);
                return ResponseEntity.ok("Transaction Recorded Successfully\n"+tt.toString());
            } else {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }
        }
        else{
            APIRateLimiting.transactionBucket.put(username,rateLimiter.resolveBucket(username));
            return this.setTransact(tt);
        }
    }


    @GetMapping("/getrate")
    public ResponseEntity<Object> getconversion(){
        String username=ad.getUsernameFromAuthorizationHeader();
        if(APIRateLimiting.ratesBucket.containsKey(username)){
            Bucket mybucket=APIRateLimiting.ratesBucket.get(username);
            if (mybucket.tryConsume(1)) {
                return ResponseEntity.ok(cc.fetchRates());
            } else {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }
        }
        else{
            APIRateLimiting.ratesBucket.put(username,rateLimiter.resolveBucket(username));
            return getconversion();
        }
       }
}