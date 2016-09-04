package com.jim.pocketaccounter.utils;


import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SmsParseObject;

import java.util.List;

public class DataCache {
    private List<Currency> currencies;
    private List<RootCategory> categories;
    private List<RootCategory> incomes;
    private List<RootCategory> expenses;
    private List<Account> accounts;
    private List<FinanceRecord> records;
    private List<DebtBorrow> debtBorrows;
    private List<CreditDetials> credits;
    private List<SmsParseObject> smsParseObjects;

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public List<RootCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<RootCategory> categories) {
        this.categories = categories;
    }

    public List<RootCategory> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<RootCategory> incomes) {
        this.incomes = incomes;
    }

    public List<RootCategory> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<RootCategory> expenses) {
        this.expenses = expenses;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<FinanceRecord> getRecords() {
        return records;
    }

    public void setRecords(List<FinanceRecord> records) {
        this.records = records;
    }

    public List<DebtBorrow> getDebtBorrows() {
        return debtBorrows;
    }

    public void setDebtBorrows(List<DebtBorrow> debtBorrows) {
        this.debtBorrows = debtBorrows;
    }

    public List<CreditDetials> getCredits() {
        return credits;
    }

    public void setCredits(List<CreditDetials> credits) {
        this.credits = credits;
    }

    public List<SmsParseObject> getSmsParseObjects() {
        return smsParseObjects;
    }

    public void setSmsParseObjects(List<SmsParseObject> smsParseObjects) {
        this.smsParseObjects = smsParseObjects;
    }

    public void clearCache() {
        currencies.clear();
        categories.clear();
        incomes.clear();
        expenses.clear();
        accounts.clear();
        records.clear();
        debtBorrows.clear();
        credits.clear();
        smsParseObjects.clear();
    }
}
