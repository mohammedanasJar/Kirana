package com.example.Kirana.services;

import com.example.Kirana.models.MyReport;
import com.example.Kirana.models.TransactionDetails;
import com.example.Kirana.repos.TransactionRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    TransactionRepo tr;

    public String getMonthReport() {
        List<TransactionDetails> td = tr.findAll();
        int weekNum;
        Map<Integer, MyReport> wrManager = new HashMap<>();
        MyReport wr;
        for (TransactionDetails t : td) {
            weekNum = getDateFromObjectId(t.getId()).getMonthValue();
            if (wrManager.containsKey(weekNum)) {
                wr = wrManager.get(weekNum);
                wr.mergeData(t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType());
            } else {
                wrManager.put(weekNum, new MyReport(weekNum, t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType()));
            }
        }
        return wrManager.toString();

    }

    public String getWeekReport() {
        List<TransactionDetails> td = tr.findAll();
        int weekNum;
        Map<Integer, MyReport> wrManager = new HashMap<>();
        MyReport wr;
        for (TransactionDetails t : td) {
            weekNum = getDateFromObjectId(t.getId()).get(WeekFields.ISO.weekOfWeekBasedYear());
            if (wrManager.containsKey(weekNum)) {
                wr = wrManager.get(weekNum);
                wr.mergeData(t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType());
            } else {
                wrManager.put(weekNum, new MyReport(weekNum, t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType()));
            }
        }
        return wrManager.toString();
    }

    public String getYearReport() {
        List<TransactionDetails> td = tr.findAll();
        int weekNum;
        Map<Integer, MyReport> wrManager = new HashMap<>();
        MyReport wr;
        for (TransactionDetails t : td) {
            weekNum = getDateFromObjectId(t.getId()).getYear();
            if (wrManager.containsKey(weekNum)) {
                wr = wrManager.get(weekNum);
                wr.mergeData(t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType());
            } else {
                wrManager.put(weekNum, new MyReport(weekNum, t.getCurrencyUsed(), t.getTransactionAmount(), t.getTransactionType()));
            }
        }
        return wrManager.toString();
    }

    public static LocalDate getDateFromObjectId(String objectIdString) {
        ObjectId objectId = new ObjectId(objectIdString);

        int timestamp = objectId.getTimestamp();

        long timestampInMillis = timestamp * 1000L;

        return Instant.ofEpochMilli(timestampInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
