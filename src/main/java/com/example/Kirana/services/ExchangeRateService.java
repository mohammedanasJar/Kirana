package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.utils.AuthorisationDetails;
import com.example.Kirana.utils.CurrencyConversion;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateService {
    @Autowired
    AuthorisationDetails currentUserDetails;

    @Autowired
    RateLimiter rateLimiter;

    @Autowired
    CurrencyConversion cc;

    public ResponseEntity<Object> fetchCurrencyConversionJSON() {
        String currentUser = currentUserDetails.getUsernameFromAuthorizationHeader();
        if (UserLimitExceeded(currentUser)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }
        return ResponseEntity.ok(cc.fetchRates());
    }

    public Bucket GetUserTokenBucketElseCreate(String currentUserBucketUsername) {
        if (RateLimitingBucketStorage.ratesBucket.containsKey(currentUserBucketUsername)) {
            return RateLimitingBucketStorage.ratesBucket.get(currentUserBucketUsername);
        }
        RateLimitingBucketStorage.ratesBucket.put(currentUserBucketUsername, rateLimiter.resolveBucket(currentUserBucketUsername));
        return GetUserTokenBucketElseCreate(currentUserBucketUsername);
    }

    public Boolean UserLimitExceeded(String currentUsername) {
        Bucket UserBucket = GetUserTokenBucketElseCreate(currentUsername);
        return !UserBucket.tryConsume(1);
    }

}
