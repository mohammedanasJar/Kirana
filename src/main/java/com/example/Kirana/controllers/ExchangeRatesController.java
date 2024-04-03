package com.example.Kirana.controllers;

import com.example.Kirana.services.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchange_rates")
public class ExchangeRatesController {
    @Autowired
    ExchangeRateService ccs;

    /**
     * Retrieve Foreign Currency Exchange Rates
     *
     * @return JSON String
     */
    @GetMapping
    public ResponseEntity<Object> endpointToFetchCurrencyConversionJSON() {
        return ccs.fetchCurrencyConversionJSON();
    }
}
