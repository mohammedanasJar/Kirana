package com.example.Kirana.repos;

import com.example.Kirana.models.TransactionDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TransactionRepo extends MongoRepository<TransactionDetails,String> {
    @Query("{$expr:{$and:[{$eq:[{$year:'$_id'}, ?0]}, {$eq:[{$month:'$_id'}, ?1]}]}}")
    List<TransactionDetails> findByMonthQuery(int year, int month);

    @Query("{ '_id': { '$gte': { '$date': 7 } } }")
    List<TransactionDetails> findByWeekQuery();

    @Query("{$expr:{$eq:[{$year:'$_id'}, ?0]}}")
    List<TransactionDetails> findByYearQuery(int year, int month);


}