package com.example.Kirana.services;

import com.example.Kirana.controllers.ReportAPI;
import com.example.Kirana.models.User;
import com.example.Kirana.repos.UserRepo;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RateLimiter {

    @Autowired
    private UserRepo userRepo;

    private static final Logger logger = LoggerFactory.getLogger(ReportAPI.class);

//    public Bucket resolveBucket(String username) {
//        Optional<User> user = userRepo.findByUsername(username);
//        if (user == null) {
//            logger.info("User " + username + "does not exist");
//            throw new IllegalArgumentException("User not found: " + username);
//        }
//        Refill refill = Refill.intervally(Long.parseLong(user.getLimit()), Duration.ofMinutes(1));
//        Bandwidth limit = Bandwidth.classic(Long.parseLong(user.getLimit()), refill);
//        return Bucket4j.builder().addLimit(limit).build();
//    }
    public Bucket resolveBucket(String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            logger.info("User " + username + " does not exist");
            throw new IllegalArgumentException("User not found: " + username);
        }
        User user = userOptional.get();
        if (user.getLimit() == null || !user.getLimit().matches("\\d+")) {
            logger.error("Invalid or missing limit for user: " + username);
            throw new IllegalArgumentException("Invalid or missing limit for user: " + username);
        }
        Refill refill = Refill.intervally(Long.parseLong(user.getLimit()), Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(Long.parseLong(user.getLimit()), refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

}
