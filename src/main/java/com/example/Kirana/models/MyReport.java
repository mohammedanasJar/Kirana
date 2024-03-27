package com.example.Kirana.models;

import java.util.HashMap;
import java.util.Map;

public class MyReport {
    @Override
    public String toString() {
        return "Myeport{" +
                "netFlow=" + netFlow +
                ", number=" + number +
                ", totalTransaction=" + totalTransaction +
                ", currencyAmountMap=" + currencyAmountMap +
                ", typeAmountMap=" + typeAmountMap +
                '}';
    }

    private double netFlow;
    private final int number;

    private int totalTransaction = 0;

    private Map<String, Double> currencyAmountMap = new HashMap<>();
    private Map<String, Double> typeAmountMap = new HashMap<>();

    public MyReport(int weekNumber, String currency, double Amount, String Type) {
        this.number = weekNumber;
        typeAmountMap.put("Creds", 0.0);
        typeAmountMap.put("Debs", 0.0);
        totalTransaction += 1;
        currencyAmountMap.put(currency, Amount);
        typeAmountMap.put(Type, typeAmountMap.get(Type) + Amount);
        if (Type.equals("Creds")) {
            this.netFlow = Amount;
        } else {
            this.netFlow = -Amount;
        }
    }


    public void mergeData(String currency, double Amount, String Type) {
        totalTransaction += 1;
        if (currencyAmountMap.containsKey(currency)) {
            currencyAmountMap.put(currency, currencyAmountMap.get(currency) + Amount);
        } else {
            currencyAmountMap.put(currency, Amount);
        }
        typeAmountMap.put(Type, typeAmountMap.get(Type) + Amount);
        this.netFlow = this.typeAmountMap.get("Creds") - this.typeAmountMap.get("Debs");

    }

}
