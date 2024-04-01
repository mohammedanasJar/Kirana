package com.example.Kirana.repos;

import com.example.Kirana.models.TransactionDetails;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TransactionRepo extends MongoRepository<TransactionDetails, ObjectId> {
    @Query("{$expr:{$and:[{$eq:[{$year:'$_id'}, ?0]}, {$eq:[{$month:'$_id'}, ?1]}]}}")
    List<TransactionDetails> findByMonthQuery(int year, int month);

    @Query("{ '_id': { '$gte': { '$date': 7 } } }")
    List<TransactionDetails> findByWeekQuery();

    @Query("{$expr:{$eq:[{$year:'$_id'}, ?0]}}")
    List<TransactionDetails> findByYearQuery(int year, int month);

    @Query(value = "["
            + "{'$match': {'transactionType': 'Debs'}},"
            + "{'$project': {'year': {'$year': '$transactionDate'}, 'transactionAmount': 1}},"
            + "{'$group': {'_id': '$year', 'totalDebsAmount': {'$sum': '$transactionAmount'}}},"
            + "{'$project': {'year': '$_id', 'totalDebsAmount': 1, '_id': 0}}"
            + "]")
    AggregationResults<Document> debitYearWise();
}