package com.example.Kirana;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
@EnableMongoRepositories
//@EnableAspectJAutoProxy
public class KiranaApplication {
    private static final Logger logger = LoggerFactory.getLogger(KiranaApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Spring Application");
        SpringApplication.run(KiranaApplication.class, args);

    }

}
