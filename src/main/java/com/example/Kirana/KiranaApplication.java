package com.example.Kirana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
@EnableMongoRepositories
@EnableAspectJAutoProxy
public class KiranaApplication {

	public static void main(String[] args) {

		SpringApplication.run(KiranaApplication.class, args);

	}

}
