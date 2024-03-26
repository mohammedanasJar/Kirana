package com.example.Kirana.controllers;


import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.UserRepo;
import com.example.Kirana.services.AuthorisationDetails;
import com.example.Kirana.services.RateLimiter;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionAPI {

    @Autowired
    UserRepo ur;
    private final Bucket bucket;
    private Map<String,Bucket> mybucketlist=new HashMap<>();

    AuthorisationDetails ad=new AuthorisationDetails();
    @Autowired
    RateLimiter rateLimiter;
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
    public  ResponseEntity<Object> setTransact(@RequestBody TransactionDetails tt){
        if(bucket.tryConsume(1)) {

        if(!tt.getCurrencyUsed().equals("USD")){
            tt.setTransactionAmount(cc.conversion(tt.getCurrencyUsed(),tt.getTransactionAmount()));
        }
        ts.setTransaction(tt);
        return ResponseEntity.ok("test value is "+tt.toString());
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

    }


    @GetMapping("/getrate")
    public String getconversion(){
        String username=ad.getUsernameFromAuthorizationHeader();
        if(mybucketlist.containsKey(username)){
            Bucket mybucket= mybucketlist.get(username);
            if (mybucket.tryConsume(1)) {
                System.out.println(mybucket.getAvailableTokens());
                return cc.fetchRates();
            } else {
                return "Rate limit exceeded";
            }

        }
        else{
            mybucketlist.put(username,rateLimiter.resolveBucket(username));
        }
        return "";

   }
}