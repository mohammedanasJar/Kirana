package com.example.Kirana.models;

import java.util.List;

public class Report {
    public double totalCred;
    public double totalDeb;
    public double netFlow;
    public List<TransactionDetails> transactions;

    @Override
    public String toString() {
        return "Report{" +
                "totalCred=" + totalCred +
                ", totalDeb=" + totalDeb +
                ", netFlow=" + netFlow +
                ", transactions=" + transactions +
                '}';
    }

    public List<TransactionDetails> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDetails> transactions) {
        this.transactions = transactions;
    }

    public double getTotalCred() {
        return totalCred;
    }

    public void setTotalCred(double totalCred) {
        this.totalCred = totalCred;
    }

    public double getTotalDeb() {
        return totalDeb;
    }

    public void setTotalDeb(double totalDeb) {
        this.totalDeb = totalDeb;
    }

    public double getNetFlow() {
        return netFlow;
    }

    public void setNetFlow(double netFlow) {
        this.netFlow = netFlow;
    }
}
