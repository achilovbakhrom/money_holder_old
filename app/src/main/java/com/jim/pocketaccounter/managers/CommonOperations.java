package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.FinanceRecord;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by DEV on 01.09.2016.
 */

public class CommonOperations {
    @Inject
    DaoSession daoSession;
    private CurrencyDao currencyDao;
    public CommonOperations(Context context) {
        ((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
        this.currencyDao = daoSession.getCurrencyDao();
    }
    public Currency getMainCurrency() {
        List<Currency> currencies = currencyDao.loadAll();
        for (Currency currency : currencies) {
            if (currency.getMain()) return currency;
        }
        return null;
    }

    public double getCost(FinanceRecord record) {
        double amount = 0.0;
        if (record.getCurrency().getMain())
            return record.getAmount();
        double koeff = 1.0;
        long diff = record.getDate().getTimeInMillis() - record.getCurrency().getCosts().get(0).getDay().getTimeInMillis();
        if (diff < 0) {
            koeff = record.getCurrency().getCosts().get(0).getCost();
            return record.getAmount()/koeff;
        }
        int pos = 0;
        while (diff >= 0 && pos < record.getCurrency().getCosts().size()) {
            diff = record.getDate().getTimeInMillis() - record.getCurrency().getCosts().get(pos).getDay().getTimeInMillis();
            if(diff>=0)
                koeff = record.getCurrency().getCosts().get(pos).getCost();
            pos++;
        }
        amount = record.getAmount()/koeff;
        return amount;
    }
    public double getCost(Calendar date, Currency currency, double amount) {
        if (currency.getMain()) return amount;
        double koeff = 1.0;
        long diff = date.getTimeInMillis() - currency.getCosts().get(0).getDay().getTimeInMillis();
        if (diff < 0) {
            koeff = currency.getCosts().get(0).getCost();
            return amount/koeff;
        }
        int pos = 0;
        while (diff >= 0 && pos < currency.getCosts().size()) {
            diff = date.getTimeInMillis() - currency.getCosts().get(pos).getDay().getTimeInMillis();
            if(diff>=0)
                koeff = currency.getCosts().get(pos).getCost();
            pos++;
        }
        amount = amount/koeff;
        Log.d("sss", "getCost: "+amount);
        return amount;
    }

    public double getCost(Calendar date, Currency fromCurrency, Currency toCurrency, double amount) {
        //TODO tekwir bir yana

        if (fromCurrency.getId().matches(toCurrency.getId())) return amount;
        double tokoeff = 1.0;
        double fromkoeff2 = 1.0;
        long todiff1 = date.getTimeInMillis() - toCurrency.getCosts().get(0).getDay().getTimeInMillis();
        long fromdiff = date.getTimeInMillis() - fromCurrency.getCosts().get(0).getDay().getTimeInMillis();
        if (todiff1 < 0) {
            tokoeff = toCurrency.getCosts().get(0).getCost();
        }
        if(fromdiff < 0){
            fromkoeff2 = fromCurrency.getCosts().get(0).getCost();
        }
        int pos = 0;
        while (todiff1 >= 0 && pos < toCurrency.getCosts().size()) {
            todiff1 = date.getTimeInMillis() - toCurrency.getCosts().get(pos).getDay().getTimeInMillis();
            if(todiff1>=0)
                tokoeff = toCurrency.getCosts().get(pos).getCost();
            pos++;
        }
        pos=0;
        while (fromdiff >= 0 && pos < fromCurrency.getCosts().size()) {
            fromdiff = date.getTimeInMillis() - fromCurrency.getCosts().get(pos).getDay().getTimeInMillis();
            if(fromdiff>=0)
                fromkoeff2 = fromCurrency.getCosts().get(pos).getCost();
            pos++;
        }
        amount = tokoeff*amount/fromkoeff2;
        return amount;
    }

    public int countOfDayBetweenCalendars(Calendar begin, Calendar end) {
        int countOfDays = 0;
        Calendar b = (Calendar) begin.clone();
        b.set(Calendar.HOUR_OF_DAY, 0);
        b.set(Calendar.MINUTE, 0);
        b.set(Calendar.SECOND, 0);
        b.set(Calendar.MILLISECOND, 0);
        Calendar e = (Calendar) end.clone();
        e.set(Calendar.HOUR_OF_DAY, 23);
        e.set(Calendar.MINUTE, 59);
        e.set(Calendar.SECOND, 59);
        e.set(Calendar.MILLISECOND, 59);
        while (b.compareTo(e) <= 0) {
            countOfDays++;
            b.add(Calendar.DAY_OF_MONTH, 1);
        }
        return countOfDays;
    }

    public  void ColorSubSeq(String text, String whichWordColor, String colorCode, TextView textView){
        String textUpper=text.toUpperCase();
        String whichWordColorUpper=whichWordColor.toUpperCase();
        SpannableString ss = new SpannableString(text);
        int strar=0;

        while (textUpper.indexOf(whichWordColorUpper,strar)>=0&&whichWordColor.length()!=0) {
            Log.d("filtering", "ColorSubSeq: "+strar);
            ss.setSpan(new BackgroundColorSpan(Color.parseColor(colorCode)),textUpper.indexOf(whichWordColorUpper,strar), textUpper.indexOf(whichWordColorUpper,strar)+whichWordColorUpper.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            strar=textUpper.indexOf(whichWordColorUpper,strar)+whichWordColorUpper.length();
        }
        textView.setText(ss);
    }

}
