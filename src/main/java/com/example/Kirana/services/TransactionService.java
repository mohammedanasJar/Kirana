package com.example.Kirana.services;

import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService{
    @Autowired
    TransactionRepo tr;
    public void setTransaction(TransactionDetails t){
        System.out.println(t.toString());
        tr.save(t);
    }

}