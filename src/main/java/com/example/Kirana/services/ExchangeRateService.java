package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.utils.AuthorisationDetails;
import com.example.Kirana.utils.ExchangeRates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateService {
    @Autowired
    AuthorisationDetails authorisationDetails;

    @Autowired
    BucketService bucketService;

    @Autowired
    ExchangeRates cc;

    public ResponseEntity<Object> fetchExchangeRateJSON() {
        String currentUser = authorisationDetails.getUsername();
        if (bucketService.UserLimitExceeded(currentUser, RateLimitingBucketStorage.ratesBucket)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }
        return ResponseEntity.ok(cc.fetchExchangeRatesJSON());
    }
}
