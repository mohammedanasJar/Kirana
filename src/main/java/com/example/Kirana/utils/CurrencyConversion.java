package com.example.Kirana.utils;

import com.example.Kirana.constants.ExchangeRates;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Component
public class CurrencyConversion {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    HashMap mapping;

    @Cacheable(value = "currencyRates", key = "'rates'")
    public String fetchRates() {
        System.out.println("Fetching rates...");
        try {
            String cachedData = redisTemplate.opsForValue().get("rates");
            System.out.println(cachedData == null);
            System.out.println(System.currentTimeMillis()-ExchangeRates.LAST_CACHE_TIME+" "+ExchangeRates.CACHE_EXPIRATION_TIME);

            System.out.println(System.currentTimeMillis()-ExchangeRates.LAST_CACHE_TIME<ExchangeRates.CACHE_EXPIRATION_TIME);
            if (!(cachedData == null || System.currentTimeMillis()-ExchangeRates.LAST_CACHE_TIME>=ExchangeRates.CACHE_EXPIRATION_TIME )) {
                System.out.println("Data retrieved from cache.");
                return cachedData;
            }
            URL url = new URL(ExchangeRates.RATE_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseStatus = connection.getResponseCode();
            if (responseStatus == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Data retrieved from API.");
                ExchangeRates.LAST_CACHE_TIME=System.currentTimeMillis();
                redisTemplate.opsForValue().set("rates", response.toString());

                return response.toString();
            } else {
                System.out.println("Unsuccessful Connection");
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double conversion(String currency, double amount) {
        String cc = this.fetchRates();
        try {
            mapping = new ObjectMapper().readValue(cc, HashMap.class);

        } catch (Exception e) {
            System.out.println("Error parsing");
        }
        HashMap map = (HashMap) mapping.get("rates");
        return amount / (double) map.get(currency);
    }
}
