package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyCost;
import com.jim.pocketaccounter.database.CurrencyCostDao;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.database.ReckingCreditDao;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseObjectDao;

import org.greenrobot.greendao.query.Query;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by DEV on 28.08.2016.
 */

public class LogicManager {
    @Inject
    DaoSession daoSession;

    private CurrencyDao currencyDao;
    private CurrencyCostDao currencyCostDao;
    private FinanceRecordDao recordDao;
    private DebtBorrowDao debtBorrowDao;
    private CreditDetialsDao creditDetialsDao;
    private SmsParseObjectDao smsParseObjectDao;
    private AccountDao accountDao;
    private ReckingCreditDao reckingCreditDao;
    public LogicManager(Context context) {
        ((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
        currencyDao = daoSession.getCurrencyDao();
        currencyCostDao = daoSession.getCurrencyCostDao();
        recordDao = daoSession.getFinanceRecordDao();
        debtBorrowDao = daoSession.getDebtBorrowDao();
        creditDetialsDao = daoSession.getCreditDetialsDao();
        smsParseObjectDao = daoSession.getSmsParseObjectDao();
        accountDao = daoSession.getAccountDao();
        reckingCreditDao = daoSession.getReckingCreditDao();
    }

    public int deleteCurrency(List<Currency> currencies) {
        List<Currency> allCureencies = currencyDao.loadAll();
        if (allCureencies.size() < 2 || currencies.size() == allCureencies.size())
            return LogicManagerConstants.MUST_BE_AT_LEAST_ONE_OBJECT;
        for (Currency currency : currencies) {
            for (FinanceRecord record : recordDao.loadAll()) {
                if (record.getCurrency().getId().matches(currency.getId())) {
                    recordDao.delete(record);
                }
            }
            for (DebtBorrow debtBorrow : debtBorrowDao.loadAll()) {
                if (debtBorrow.getCurrency().getId().matches(currency.getId()))
                    debtBorrowDao.delete(debtBorrow);
            }
            for (CreditDetials creditDetials : creditDetialsDao.loadAll()) {
                if (creditDetials.getValyute_currency().getId().matches(currency.getId()))
                    creditDetialsDao.delete(creditDetials);
            }
            for (SmsParseObject smsParseObject : smsParseObjectDao.loadAll()) {
                if (smsParseObject.getCurrency().getId().matches(currency.getId()))
                    smsParseObjectDao.delete(smsParseObject);
            }
            if (currency.getMain())
                setMainCurrency(null);
            currencyDao.delete(currency);
        }
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertAccount(Account account) {
        Query<Account> accountQuery = accountDao.queryBuilder()
                .where(AccountDao.Properties.Id.eq(account.getId())).build();
        if (!accountQuery.list().isEmpty()) {
            accountDao.save(account);
            return LogicManagerConstants.UPDATED_SUCCESSFULL;
        }
        accountQuery = accountDao.queryBuilder()
                .where(AccountDao.Properties.Name.eq(account.getName())).build();
        if (!accountQuery.list().isEmpty())
            return LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS;
        accountDao.insert(account);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteAccount(List<Account> accounts) {
        List<Account> allAccounts = accountDao.loadAll();
        if (allAccounts.size() < 2 || accounts.size() == accounts.size())
            return LogicManagerConstants.MUST_BE_AT_LEAST_ONE_OBJECT;
        for (Account account: accounts) {
            for (FinanceRecord record : recordDao.loadAll()) {
                if (record.getAccount().getId().matches(account.getId())) {
                    recordDao.delete(record);
                }
            }
            for (DebtBorrow debtBorrow : debtBorrowDao.loadAll()) {
                if (debtBorrow.getAccount().getId().matches(account.getId()))
                    debtBorrowDao.delete(debtBorrow);
            }
            for (CreditDetials creditDetials : creditDetialsDao.loadAll()) {
                for (ReckingCredit reckingCredit : creditDetials.getReckings())
                    if (reckingCredit.getAccountId().matches(account.getId()))
                        reckingCreditDao.delete(reckingCredit);
            }
            for (SmsParseObject smsParseObject : smsParseObjectDao.loadAll()) {
                if (smsParseObject.getAccount().getId().matches(account.getId()))
                    smsParseObjectDao.delete(smsParseObject);
            }
            accountDao.delete(account);
        }
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public void setMainCurrency(Currency currency) {
        List<Currency> currencies = currencyDao.loadAll();
        Currency mainCurrency = null;
        if (currency == null) {
            int pos = 0;
            for (int i=0; i<currencies.size(); i++) {
                if (currencies.get(i).getMain()) {
                    pos = i;
                    break;
                }
            }
            currencies.get(pos).setMain(false);
            if (pos == currencies.size()-1) {
                currencies.get(0).setMain(true);
                mainCurrency = currencies.get(0);
            }
            else {
                currencies.get(pos+1).setMain(true);
                mainCurrency = currencies.get(pos+1);
            }
        }
        else {
            int oldMainPos = 0;
            int currMainPos = 0;
            for (int i= 0; i<currencies.size(); i++) {
                if (currencies.get(i).getMain()) {
                    oldMainPos = i;
                }
                if (currencies.get(i).getId().matches(currency.getId())) {
                    currMainPos = i;
                }
            }
            currencies.get(oldMainPos).setMain(false);
            currencies.get(currMainPos).setMain(true);
            mainCurrency = currencies.get(currMainPos);
        }
        double koeff = mainCurrency.getCosts().get(mainCurrency.getCosts().size()-1).getCost();
        for (int i=0; i<mainCurrency.getCosts().size(); i++) {
            CurrencyCost current = mainCurrency.getCosts().get(i);
            Calendar currDay = (Calendar)current.getDay().clone();
            currDay.set(Calendar.HOUR_OF_DAY, 0);
            currDay.set(Calendar.MINUTE, 0);
            currDay.set(Calendar.SECOND, 0);
            currDay.set(Calendar.MILLISECOND, 0);
            for (int j=0; j<currencies.size(); j++) {
                if (currencies.get(j).getMain()) continue;
                for (int k=0; k<currencies.get(j).getCosts().size(); k++) {
                    CurrencyCost currencyCost = currencies.get(j).getCosts().get(k);
                    if (currencyCost.getDay().compareTo(currDay) >= 0)
                        currencyCost.setCost(currencyCost.getCost()/current.getCost());
                    currencyCostDao.save(currencyCost);
                }
            }
            mainCurrency.getCosts().get(i).setCost(mainCurrency.getCosts().get(i).getCost()/koeff);
            currencyCostDao.save(mainCurrency.getCosts().get(i));
        }
        currencyDao.saveInTx(currencies);
    }

    //currency costs
    public int deleteCurrencyCosts(List<CurrencyCost> currencyCost) {
        if (currencyCost.isEmpty() || currencyCost == null)
            return LogicManagerConstants.LIST_IS_EMPTY;
        String currencyId = currencyCost.get(0).getCurrencyId();
        Currency costsCurrency = null;
        for (Currency currency : currencyDao.loadAll()) {
            if (currency.getId().matches(currencyId)) {
                costsCurrency = currency;
                break;
            }
        }
        if (currencyCost.size() == costsCurrency.getCosts().size()) {
            for (int i=0; i<costsCurrency.getCosts().size(); i++) {
                if (i==0) continue;
                currencyCostDao.delete(costsCurrency.getCosts().get(i));
                costsCurrency.getCosts().remove(i);
                i--;
            }
            return LogicManagerConstants.DELETED_SUCCESSFUL;
        }
        else {
            for (CurrencyCost cc : currencyCost) {
                for (CurrencyCost currcc : costsCurrency.getCosts()) {
                    if (cc.getCurrencyId().matches(costsCurrency.getId()) &&
                            cc.getId() == currcc.getId()) {
                        costsCurrency.getCosts().remove(currcc);
                    }
                }
            }
            currencyCostDao.deleteInTx(currencyCost);
            return LogicManagerConstants.DELETED_SUCCESSFUL;
        }
    }

    

}