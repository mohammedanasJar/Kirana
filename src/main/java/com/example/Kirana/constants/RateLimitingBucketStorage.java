package com.example.Kirana.constants;

import com.example.Kirana.services.RateLimiter;
import io.github.bucket4j.Bucket;

import java.util.HashMap;
import java.util.Map;

public class RateLimitingBucketStorage {
    RateLimiter rateLimiter;
    public static Map<String, Bucket> transactionEndpointBucket = new HashMap<>();
    public static Map<String, Bucket> reportBucket = new HashMap<>();
    public static Map<String, Bucket> ratesBucket = new HashMap<>();



}
