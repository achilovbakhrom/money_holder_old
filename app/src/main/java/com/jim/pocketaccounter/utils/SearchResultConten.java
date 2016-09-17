package com.jim.pocketaccounter.utils;

import android.content.Context;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Currency;

import java.util.Calendar;

import static com.jim.pocketaccounter.utils.ContenerStaticSearchVariables.*;

/**
 * Created by DEV on 14.09.2016.
 */

public class SearchResultConten {
    private String stNameOfItem;
    private String comment;
    private double dAmount;
    private int stTypeSearch;
    private Calendar date;
    private Object object;
    private String icon;
    private Currency currency;
    private int icon_Id;


    public SearchResultConten(String stNameOfItem, double dAmount, int stTypeSearch, Calendar date, Object object, String icon, String comment, Currency currency) {
        this.stNameOfItem = stNameOfItem;
        this.dAmount = dAmount;
        this.stTypeSearch = stTypeSearch;
        this.date = date;
        this.comment=comment;
        this.object=object;
        this.icon=icon;
        this.currency=currency;
        this.icon_Id=0;

    }
    public SearchResultConten(String stNameOfItem, double dAmount, int stTypeSearch, Calendar date, Object object, int icon, String comment, Currency currency) {
        this.stNameOfItem = stNameOfItem;
        this.dAmount = dAmount;
        this.stTypeSearch = stTypeSearch;
        this.date = date;
        this.comment=comment;
        this.object=object;
        this.icon=null;
        this.currency=currency;
        this.icon_Id=icon;

    }

    public boolean isItIconWithId(){
        return (icon==null)&&(icon_Id!=0);
    }

    public int getIcon_Id() {
        return icon_Id;
    }

    public void setIcon_Id(int icon_Id) {
        this.icon_Id = icon_Id;
    }
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Object getParrentObject() {
        return object;
    }

    public void setParrentObject(Object object) {
        this.object = object;
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

    public Calendar getMyDate() {
        return date;
    }

    public void setMyDate(Calendar date) {
        this.date = date;
    }

    public String getTypeInString(Context context){

    switch (stTypeSearch){
        case SIMPLE_RECKING:
            return context.getResources().getString(R.string.simple_recking);
        case CREDIT_VAR:
            return context.getResources().getString(R.string.credit_var);
        case DEBT_VAR:
            return context.getResources().getString(R.string.debt_var);
        case BORROW_VAR:
            return context.getResources().getString(R.string.borrow_var);
        case CREDIT_RECKING:
            return context.getResources().getString(R.string.credit_reckings_var);
        case DEBT_RECKING:
            return context.getResources().getString(R.string.debt_reckings_var);
        case BORROW_RECKING:
            return context.getResources().getString(R.string.borrow_reckings_var);

    }
        return null;
    }
}
