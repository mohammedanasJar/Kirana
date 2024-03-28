package com.example.Kirana.services;

import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
@Service
public class GenerateRandomTransactionService {

    private final String[] CurrencyPossible= new String[]{"USD", "INR","EUR","JPY","CNY"};
    private final String[] TransactionType=new String[]{"Creds","Debs"};
    Random randomGenerator = new Random();
    @Autowired
    TransactionRepo tr;

    public ResponseEntity<Object> generateTransaction() {
            TransactionDetails td = new TransactionDetails();
            td.setCurrencyUsed(CurrencyPossible[randomGenerator.nextInt(CurrencyPossible.length)]);
            td.setTransactionAmount(randomGenerator.nextInt(1000));
            td.setTransactionType(TransactionType[randomGenerator.nextInt(TransactionType.length)]);
            td.setTransactionDate(generateRandomDate());
            td.setId(new ObjectId(generateRandomDate()));
            tr.save(td);
            return ResponseEntity.ok(td.toString()) ;
    }

    public Date generateRandomDate(){
        Calendar calendar = Calendar.getInstance();
        int year = randomGenerator.nextInt(4) + 2020;
        int month = randomGenerator.nextInt(12) + 1;
        int day = randomGenerator.nextInt(28) + 1;
        int hour = randomGenerator.nextInt(24);
        int minute = randomGenerator.nextInt(60);
        int second = randomGenerator.nextInt(60);
        int millisecond = randomGenerator.nextInt(1000);
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        System.out.println("Generated Time"+calendar.getTime());
        return calendar.getTime();
    }


}
