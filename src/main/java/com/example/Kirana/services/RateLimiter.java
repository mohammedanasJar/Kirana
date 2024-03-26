package com.example.Kirana.services;

import com.example.Kirana.models.UserDetails;
import com.example.Kirana.repos.UserRepo;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiter {

    @Autowired
    private UserRepo userRepo;


    public Bucket resolveBucket(String key) {
        UserDetails user = userRepo.findByUsername(key);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + key);
        }
        Refill refill = Refill.intervally(Long.parseLong(user.getLimit()), Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(Long.parseLong(user.getLimit()), refill);
        return Bucket4j.builder().addLimit(limit).build();
    }
}
