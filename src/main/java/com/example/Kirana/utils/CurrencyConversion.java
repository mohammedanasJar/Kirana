package com.example.Kirana.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.Kirana.constants.ExchangeRates;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Component
public class CurrencyConversion {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    HashMap mapping;
     @Cacheable(value = "currencyRates", key = "'rates'")
     public String fetchRates()
     {
        System.out.println("Fetching rates...");
        try {
            String cachedData = redisTemplate.opsForValue().get("rates");
            if (cachedData != null) {
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

                // Log that data is retrieved from the API
                System.out.println("Data retrieved from API.");

                // Store the data in the cache
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
    public double conversion(String currency,double amount)  {
         String cc=this.fetchRates();
try {
    mapping = new ObjectMapper().readValue(cc, HashMap.class);

}
catch (Exception e){
System.out.println("Error parsing");
}
        HashMap map= (HashMap) mapping.get("rates");
        return amount/(double)map.get(currency);
    }
}
