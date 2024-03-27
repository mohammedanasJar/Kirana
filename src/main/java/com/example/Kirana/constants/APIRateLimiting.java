package com.example.Kirana.constants;

import io.github.bucket4j.Bucket;

import java.util.HashMap;
import java.util.Map;

public class APIRateLimiting {
    public static Map<String, Bucket> transactionBucket = new HashMap<>();
    public static Map<String, Bucket> reportBucket = new HashMap<>();
    public static Map<String, Bucket> ratesBucket = new HashMap<>();

}
