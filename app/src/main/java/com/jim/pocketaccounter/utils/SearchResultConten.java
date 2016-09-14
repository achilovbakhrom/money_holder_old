package com.jim.pocketaccounter.utils;

import java.util.Calendar;

/**
 * Created by DEV on 14.09.2016.
 */

public class SearchResultConten {
    private String stNameOfItem;
    private double dAmount;
    private int stTypeSearch;
    private Calendar date;

    public SearchResultConten(String stNameOfItem, double dAmount, int stTypeSearch, Calendar date) {
        this.stNameOfItem = stNameOfItem;
        this.dAmount = dAmount;
        this.stTypeSearch = stTypeSearch;
        this.date = date;
    }

    public String getStNameOfItem() {
        return stNameOfItem;
    }

    public void setStNameOfItem(String stNameOfItem) {
        this.stNameOfItem = stNameOfItem;
    }

    public double getdAmount() {
        return dAmount;
    }

    public void setdAmount(double dAmount) {
        this.dAmount = dAmount;
    }

    public int getStTypeSearch() {
        return stTypeSearch;
    }

    public void setStTypeSearch(int stTypeSearch) {
        this.stTypeSearch = stTypeSearch;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
