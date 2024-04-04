package com.example.Kirana.services;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BucketService {
    @Autowired
    RateLimiter rateLimiter;
    /**
     * Retrieve Token Bucket for a User
     *
     * @param username
     * @param requestedBucket
     * @return User specific Token Bucket
     * <ul>
     * <li>{@code IF USER ALREADY ACCESSED IT BEFORE}, Retrieve User Token Bucket </li>
     * <li>{@code IF USER ACCESSING FOR FIRST TIME}, Generate User Token Bucket</li>
     * </ul>
     */
    public Bucket GetTokenBucket(String username, Map<String, Bucket> requestedBucket) {
        if (requestedBucket.containsKey(username)) {
            return requestedBucket.get(username);
        }
        return GenerateTokenBucket(username,requestedBucket);
    }

    /**
     * Generate Token Bucket for a User
     *
     * @param username
     * @param requestedBucket
     * @return User specific Token Bucket
     */
    public Bucket GenerateTokenBucket(String username, Map<String, Bucket> requestedBucket) {
        requestedBucket.put(username, rateLimiter.createBucket(username));
        return GetTokenBucket(username, requestedBucket);
    }

    /**
     * Check if User API Limit Exceeded
     *
     * @param username
     * @param requestedBucket
     * @return <ul>
     * <li>{@code true}, if limit exceeded</li>
     * <li>{@code false}, if limit not exceeded</li>
     * </ul>
     */
    public Boolean UserLimitExceeded(String username, Map<String, Bucket> requestedBucket) {
        Bucket UserBucket = GetTokenBucket(username,requestedBucket);
        return !UserBucket.tryConsume(1);
    }
}
