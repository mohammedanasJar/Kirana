package com.example.Kirana.services;

import com.example.Kirana.constants.RateLimitingBucketStorage;
import com.example.Kirana.utils.AuthorisationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ExchangeRateService {
    public static String RATE_API = "https://api.fxratesapi.com/latest";
    public static long CACHE_EXPIRATION_TIME_IN_SECONDS = 60 * 60;
    @Autowired
    private AuthorisationDetails authorisationDetails;
    @Autowired
    private BucketService bucketService;
    private RestTemplate restTemplate = new RestTemplate();
    private String exchangeRates;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * To Get Currency Exchange Rates
     *
     * @return String
     */
    public String fetchExchangeRateJSON() {
        String currentUser = authorisationDetails.getUsername();
        if (bucketService.UserLimitExceeded(currentUser, RateLimitingBucketStorage.ratesBucket)) {
            return null;
        }
        exchangeRates = getExchangeRatesFromCache();
        if (Objects.isNull(exchangeRates)) {
            log.info("Data Retrieved from API");
            return getExchangeRatesFromAPI();
        } else {
            log.info("Data Retrieved from Cache");
            return exchangeRates;
        }
    }

    /**
     * To Get Currency Exchange Rates From Cache
     *
     * @return String
     */
    public String getExchangeRatesFromCache() {
        return redisTemplate.opsForValue().get("rates");
    }

    /**
     * To Get Currency Exchange Rates From API
     *
     * @return String
     */
    public String getExchangeRatesFromAPI() {
        exchangeRates = restTemplate.getForObject(RATE_API, String.class);
        redisTemplate.opsForValue().set("rates", exchangeRates);
        redisTemplate.expire("rates", CACHE_EXPIRATION_TIME_IN_SECONDS, TimeUnit.SECONDS);
        return exchangeRates;
    }

}
