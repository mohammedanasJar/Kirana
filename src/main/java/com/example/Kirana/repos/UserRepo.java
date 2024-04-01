package com.example.Kirana.repos;

import com.example.Kirana.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;


public interface UserRepo extends MongoRepository<User, String> {
    @Query("{'username': ?0}")
    Optional<User> findByUsername(String username);

}


