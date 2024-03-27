package com.example.Kirana.repos;

import com.example.Kirana.models.UserDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserRepo extends MongoRepository<UserDetails, String> {
    @Query("{'username': ?0}")
    UserDetails findByUsername(String username);

}


