package com.example.Kirana.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetails {

    public TransactionDetails() {
        this.transactionDate = new Date();
    }

    @Id
    String id;
    double transactionAmount;
    String transactionType;
    String currencyUsed;
    Date transactionDate;

}