package com.example.Kirana.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

//@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetails{

   public TransactionDetails() {
           this.transactionDate=new Date();
       }
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public double getTransactionAmount() {
      return transactionAmount;
   }

   public void setTransactionAmount(double transactionAmount) {
      this.transactionAmount = transactionAmount;
   }

   public String getTransactionType() {
      return transactionType;
   }

   public void setTransactionType(String transactionType) {
      this.transactionType = transactionType;
   }

   public String getCurrencyUsed() {
      return currencyUsed;
   }

   public void setCurrencyUsed(String currencyUsed) {
      this.currencyUsed = currencyUsed;
   }
   @Id
   String id;
   double transactionAmount;
   String transactionType;
   String currencyUsed;
   Date transactionDate;

   public Date getTransactionDate() {
      return transactionDate;
   }

   @Override
   public String toString() {
      return "TransactionDetails{" +
              "transactionID='" + id + '\'' +
              ", transactionAmount=" + transactionAmount +
              ", transactionType='" + transactionType + '\'' +
              ", currencyUsed='" + currencyUsed + '\'' +
              ", transactionDate=" + transactionDate +
              '}';
   }

   public void setTransactionDate(Date transactionDate) {
      this.transactionDate = transactionDate;
   }

}