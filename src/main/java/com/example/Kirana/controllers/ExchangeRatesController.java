package com.example.Kirana.controllers;

import com.example.Kirana.services.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/exchange_rates")
public class ExchangeRatesController {
    @Autowired
    ExchangeRateService exchangeRateService;

    /**
     * Retrieve Foreign Currency Exchange Rates
     *
     * @return {@code ResponseEntity<String>}
     */
    @GetMapping
    public ResponseEntity<String> endpointToFetchCurrencyConversionJSON() {
        String response= exchangeRateService.fetchExchangeRateJSON();
        if(Objects.isNull(response)){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Limit Exceeded. Try Again after a minute");
        }
        return ResponseEntity.ok(response);
    }
}
