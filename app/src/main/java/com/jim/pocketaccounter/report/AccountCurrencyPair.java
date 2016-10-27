package com.jim.pocketaccounter.report;

import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;

/**
 * Created by root on 10/10/16.
 */

public class AccountCurrencyPair {
    private Account account;
    private Currency currency;
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
