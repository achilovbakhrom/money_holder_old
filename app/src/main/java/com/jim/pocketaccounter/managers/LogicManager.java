package com.jim.pocketaccounter.managers;

import android.content.Context;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.AccountOperationDao;
import com.jim.pocketaccounter.database.AutoMarket;
import com.jim.pocketaccounter.database.AutoMarketDao;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.BoardButtonDao;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyCost;
import com.jim.pocketaccounter.database.CurrencyCostState;
import com.jim.pocketaccounter.database.CurrencyCostStateDao;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.CurrencyWithAmount;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.Person;
import com.jim.pocketaccounter.database.PersonDao;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.database.PurposeDao;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.database.ReckingCreditDao;
import com.jim.pocketaccounter.database.ReckingDao;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.RootCategoryDao;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseObjectDao;
import com.jim.pocketaccounter.database.SmsParseSuccess;
import com.jim.pocketaccounter.database.SmsParseSuccessDao;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.database.SubCategoryDao;
import com.jim.pocketaccounter.database.UserEnteredCalendars;
import com.jim.pocketaccounter.database.UserEnteredCalendarsDao;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.cache.DataCache;

import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
/**
 * Created by DEV on 28.08.2016.
 */

public class LogicManager {
    @Inject
    DaoSession daoSession;
    @Inject
    CommonOperations commonOperations;
    @Inject
    DataCache dataCache;
    private Context context;
    private CurrencyDao currencyDao;
    private FinanceRecordDao recordDao;
    private DebtBorrowDao debtBorrowDao;
    private CreditDetialsDao creditDetialsDao;
    private AccountDao accountDao;
    private ReckingCreditDao reckingCreditDao;
    private SubCategoryDao subCategoryDao;
    private BoardButtonDao boardButtonDao;
    private RootCategoryDao rootCategoryDao;
    private PurposeDao purposeDao;
    private PersonDao personDao;
    private ReckingDao reckingDao;
    private AccountOperationDao accountOperationDao;
    private AutoMarketDao autoMarketDao;
    private SmsParseObjectDao smsParseObjectDao;
    private SmsParseSuccessDao smsParseSuccessDao;
    private CurrencyCostStateDao currencyCostStateDao;
    private UserEnteredCalendarsDao userEnteredCalendarsDao;
    public LogicManager(Context context) {
        this.context = context;
        ((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
        currencyDao = daoSession.getCurrencyDao();
        recordDao = daoSession.getFinanceRecordDao();
        debtBorrowDao = daoSession.getDebtBorrowDao();
        creditDetialsDao = daoSession.getCreditDetialsDao();
        smsParseObjectDao = daoSession.getSmsParseObjectDao();
        accountDao = daoSession.getAccountDao();
        reckingCreditDao = daoSession.getReckingCreditDao();
        subCategoryDao = daoSession.getSubCategoryDao();
        boardButtonDao = daoSession.getBoardButtonDao();
        rootCategoryDao = daoSession.getRootCategoryDao();
        purposeDao = daoSession.getPurposeDao();
        personDao = daoSession.getPersonDao();
        reckingDao = daoSession.getReckingDao();
        accountOperationDao = daoSession.getAccountOperationDao();
        autoMarketDao = daoSession.getAutoMarketDao();
        smsParseObjectDao = daoSession.getSmsParseObjectDao();
        smsParseSuccessDao = daoSession.getSmsParseSuccessDao();
        currencyCostStateDao = daoSession.getCurrencyCostStateDao();
        userEnteredCalendarsDao = daoSession.getUserEnteredCalendarsDao();
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
            for (CurrencyCostState currencyCostState : currencyCostStateDao.loadAll()) {
                boolean found = currencyCostState.getMainCurrency().getId().equals(currency.getId());
                if (found) {
                    for (CurrencyWithAmount withAmount : currencyCostState.getCurrencyWithAmountList())
                        daoSession.getCurrencyWithAmountDao().delete(withAmount);
                    currencyCostStateDao.delete(currencyCostState);
                }
                else {
                    for (CurrencyWithAmount withAmount : currencyCostState.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(currency.getId())) {
                            daoSession.getCurrencyWithAmountDao().delete(withAmount);
                            currencyCostState.resetCurrencyWithAmountList();
                        }
                    }
                }
            }
            if (currency.getMain())
                setMainCurrency(null);
            for (UserEnteredCalendars userEnteredCalendars : currency.getUserEnteredCalendarses())
                daoSession.getUserEnteredCalendarsDao().delete(userEnteredCalendars);
            currencyDao.delete(currency);
        }
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertAccount(Account account) {
        Query<Account> accountQuery = accountDao.queryBuilder()
                .where(AccountDao.Properties.Name.eq(account.getName())).build();
        if (!accountQuery.list().isEmpty())
            return LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS;
        accountDao.insertOrReplace(account);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteAccount(List<Account> accounts) {
        List<Account> allAccounts = accountDao.loadAll();
        if (allAccounts.size() < 2 || allAccounts.size() == accounts.size())
            return LogicManagerConstants.MUST_BE_AT_LEAST_ONE_OBJECT;
        for (Account account : accounts) {
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

    public void updateGenerateDefinetilyCurrentDay(Calendar day, double amount, Currency currentCur) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Currency mainCur = currencyDao.queryBuilder().where(CurrencyDao.Properties.IsMain.eq(true)).list().get(0);
        List<CurrencyCostState> currencyCostStates = currencyCostStateDao
                                                    .queryBuilder()
                                                    .where(CurrencyCostStateDao
                                                    .Properties.Day.eq(simpleDateFormat.format(day.getTime())))
                                                    .list();
        if (!currencyCostStates.isEmpty()) {
            CurrencyCostState findCostState = null;
            for (CurrencyCostState currencyCostState : currencyCostStates) {
                if (currencyCostState.getMainCurId().equals(mainCur.getId())) {
                    for (CurrencyWithAmount withAmount : currencyCostState.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(currentCur.getId())) {
                            withAmount.setAmount(amount);
                            daoSession.getCurrencyWithAmountDao().insertOrReplace(withAmount);
                            findCostState = currencyCostState;
                            break;
                        }
                    }
                } else {
                    for (CurrencyWithAmount withAmount : currencyCostState.getCurrencyWithAmountList())
                        daoSession.getCurrencyWithAmountDao().delete(withAmount);
                    daoSession.getCurrencyCostStateDao().delete(currencyCostState);
                }
            }
            currencyCostStateDao.insertOrReplace(findCostState);
            findCostState.resetCurrencyWithAmountList();
            for (CurrencyWithAmount currencyWithAmount : findCostState.getCurrencyWithAmountList()) {
                CurrencyCostState costState = new CurrencyCostState();
                costState.setDay(day);
                costState.setMainCurrency(currencyWithAmount.getCurrency());
                daoSession.getCurrencyCostStateDao().insertOrReplace(costState);
                CurrencyWithAmount tempWithAmount = new CurrencyWithAmount();
                tempWithAmount.setCurrency(findCostState.getMainCurrency());
                tempWithAmount.setAmount(1 / currencyWithAmount.getAmount());
                tempWithAmount.setParentId(costState.getId());
                daoSession.getCurrencyWithAmountDao().insertOrReplace(tempWithAmount);
                for (CurrencyWithAmount withAmount : findCostState.getCurrencyWithAmountList()) {
                    if (!withAmount.getCurrencyId().equals(currencyWithAmount.getCurrencyId())) {
                        CurrencyWithAmount newWithAmount = new CurrencyWithAmount();
                        newWithAmount.setCurrency(withAmount.getCurrency());
                        newWithAmount.setAmount(withAmount.getAmount() / currencyWithAmount.getAmount());
                        newWithAmount.setParentId(costState.getId());
                        daoSession.getCurrencyWithAmountDao().insertOrReplace(newWithAmount);
                    }
                }
            }
        }
    }

    public void generateWhenAddingNewCurrency(Calendar day, double  amount, Currency currentCurrency) {
        Currency mainCur = currencyDao.queryBuilder().where(CurrencyDao.Properties.IsMain.eq(true)).list().get(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDay = simpleDateFormat.format(day.getTime());
        List<CurrencyCostState> currencyCostStates = currencyCostStateDao
                .queryBuilder()
                .where(CurrencyCostStateDao.Properties.Day.eq(formattedDay))
                .list();
        if (!currencyCostStates.isEmpty()) {
            CurrencyCostState mainCurrencyState = null;
            for (CurrencyCostState state : currencyCostStates) {
                if (state.getMainCurrency().getIsMain()) {
                    mainCurrencyState = state;
                    CurrencyWithAmount withAmount = new CurrencyWithAmount();
                    withAmount.setCurrency(currentCurrency);
                    withAmount.setAmount(amount);
                    withAmount.setParentId(state.getId());
                    daoSession.getCurrencyWithAmountDao().insertOrReplace(withAmount);
                }
                else {
                    for (CurrencyWithAmount withAmount : state.getCurrencyWithAmountList())
                        daoSession.getCurrencyWithAmountDao().delete(withAmount);
                    currencyCostStateDao.delete(state);
                }
            }
            mainCurrencyState.resetCurrencyWithAmountList();
            List<CurrencyWithAmount> currencyWithAmounts = mainCurrencyState.getCurrencyWithAmountList();
            for (CurrencyWithAmount currencyWithAmount : currencyWithAmounts) {
                CurrencyCostState anotherState = new CurrencyCostState();
                anotherState.setDay((Calendar)day.clone());
                anotherState.setMainCurrency(currencyWithAmount.getCurrency());
                daoSession.getCurrencyCostStateDao().insertOrReplace(anotherState);
                CurrencyWithAmount anotherStatesAmount = new CurrencyWithAmount();
                anotherStatesAmount.setCurrency(mainCurrencyState.getMainCurrency());
                anotherStatesAmount.setAmount(1/currencyWithAmount.getAmount());
                anotherStatesAmount.setParentId(anotherState.getId());
                daoSession.getCurrencyWithAmountDao().insertOrReplace(anotherStatesAmount);
                for (CurrencyWithAmount amnt : currencyWithAmounts) {
                    if (!amnt.getCurrencyId().equals(currencyWithAmount.getCurrencyId())) {
                        CurrencyWithAmount anotherStatesRestAmounts = new CurrencyWithAmount();
                        anotherStatesRestAmounts.setCurrency(amnt.getCurrency());
                        anotherStatesRestAmounts.setAmount(amnt.getAmount()/currencyWithAmount.getAmount());
                        anotherStatesRestAmounts.setParentId(anotherState.getId());
                        daoSession.getCurrencyWithAmountDao().insertOrReplace(anotherStatesRestAmounts);
                    }
                }
            }
        } else {
            List<CurrencyCostState> allStates = currencyCostStateDao.loadAll();
            Collections.sort(allStates, new Comparator<CurrencyCostState>() {
                @Override
                public int compare(CurrencyCostState lhs, CurrencyCostState rhs) {
                    return lhs.getDay().compareTo(rhs.getDay());
                }
            });
            Calendar lastDay = null;
            if (day.compareTo(currentCurrency.getUserEnteredCalendarses().get(0).getCalendar()) < 0) {
                lastDay = (Calendar) currentCurrency.getUserEnteredCalendarses().get(0).getCalendar().clone();
            }
            else if (day.compareTo(currentCurrency.getUserEnteredCalendarses().get(currentCurrency.getUserEnteredCalendarses().size()-1).getCalendar()) > 0) {
                lastDay = (Calendar) currentCurrency.getUserEnteredCalendarses().get(currentCurrency.getUserEnteredCalendarses().size()-1).getCalendar().clone();
            }
            else {
                for (int i=0; i<currentCurrency.getUserEnteredCalendarses().size(); i++) {
                    if (i != currentCurrency.getUserEnteredCalendarses().size()-1) {
                        if (day.compareTo(currentCurrency.getUserEnteredCalendarses().get(i).getCalendar())>0 &&
                                day.compareTo(currentCurrency.getUserEnteredCalendarses().get(i+1).getCalendar())<0) {
                            lastDay = (Calendar) currentCurrency.getUserEnteredCalendarses().get(i).getCalendar().clone();
                            break;
                        }
                    }
                }
            }
            if (lastDay == null)
                lastDay = (Calendar) allStates.get(allStates.size()-1).getDay().clone();
            String formattedLastDay = simpleDateFormat.format(lastDay.getTime());
            List<CurrencyCostState> lastDayState = currencyCostStateDao
                    .queryBuilder()
                    .where(CurrencyCostStateDao.Properties.Day.eq(formattedLastDay),
                            CurrencyCostStateDao.Properties.MainCurId.eq(mainCur.getId()))
                    .list();
            List<CurrencyWithAmount> lastStateAmounts = lastDayState.get(0).getCurrencyWithAmountList();
            List<Currency> noneMainCurrencies = currencyDao
                    .queryBuilder()
                    .where(CurrencyDao.Properties.IsMain.eq(false))
                    .list();
            CurrencyCostState mainState = new CurrencyCostState();
            mainState.setMainCurrency(mainCur);
            mainState.setDay(day);
            currencyCostStateDao.insertOrReplace(mainState);
            CurrencyWithAmount mainCostAmounts = new CurrencyWithAmount();
            mainCostAmounts.setParentId(mainState.getId());
            mainCostAmounts.setCurrency(currentCurrency);
            mainCostAmounts.setAmount(amount);
            daoSession.getCurrencyWithAmountDao().insertOrReplace(mainCostAmounts);
            for (Currency currency : noneMainCurrencies) {
                if (currency.getId().equals(currentCurrency.getId())) continue;
                CurrencyWithAmount mainsAnotherAmount = new CurrencyWithAmount();
                mainsAnotherAmount.setCurrency(currency);
                mainsAnotherAmount.setParentId(mainState.getId());
                for (CurrencyWithAmount currencyWithAmount : lastStateAmounts) {
                    if (currencyWithAmount.getCurrencyId().equals(currency.getId())) {
                        mainsAnotherAmount.setAmount(currencyWithAmount.getAmount());
                    }
                }
                daoSession.getCurrencyWithAmountDao().insertOrReplace(mainsAnotherAmount);
            }
            mainState.resetCurrencyWithAmountList();
            List<CurrencyWithAmount> currencyWithAmounts = mainState.getCurrencyWithAmountList();
            for (CurrencyWithAmount currencyWithAmount : currencyWithAmounts) {
                CurrencyCostState anotherState = new CurrencyCostState();
                anotherState.setDay((Calendar)day.clone());
                anotherState.setMainCurrency(currencyWithAmount.getCurrency());
                daoSession.getCurrencyCostStateDao().insertOrReplace(anotherState);
                CurrencyWithAmount anotherStatesAmount = new CurrencyWithAmount();
                anotherStatesAmount.setCurrency(mainState.getMainCurrency());
                anotherStatesAmount.setAmount(1/currencyWithAmount.getAmount());
                anotherStatesAmount.setParentId(anotherState.getId());
                daoSession.getCurrencyWithAmountDao().insertOrReplace(anotherStatesAmount);
                for (CurrencyWithAmount amnt : currencyWithAmounts) {
                    if (!amnt.getCurrencyId().equals(currencyWithAmount.getCurrencyId())) {
                        CurrencyWithAmount anotherStatesRestAmounts = new CurrencyWithAmount();
                        anotherStatesRestAmounts.setCurrency(amnt.getCurrency());
                        anotherStatesRestAmounts.setAmount(amnt.getAmount()/currencyWithAmount.getAmount());
                        anotherStatesRestAmounts.setParentId(anotherState.getId());
                        daoSession.getCurrencyWithAmountDao().insertOrReplace(anotherStatesRestAmounts);
                    }
                }
            }
        }
    }

    public void generateForDefinetilyCurrentDay(Calendar day, double amount, Currency currentCur) {
        Currency mainCur = currencyDao.queryBuilder().where(CurrencyDao.Properties.IsMain.eq(true)).list().get(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDay = simpleDateFormat.format(day.getTime());
        List<CurrencyCostState> currencyCostStates = currencyCostStateDao
                .queryBuilder()
                .where(CurrencyCostStateDao.Properties.Day.eq(formattedDay))
                .list();
        if (!currencyCostStates.isEmpty()) {
            updateGenerateDefinetilyCurrentDay(day, amount, currentCur);
        } else {
            List<CurrencyCostState> allStates = currencyCostStateDao.loadAll();
            Collections.sort(allStates, new Comparator<CurrencyCostState>() {
                @Override
                public int compare(CurrencyCostState lhs, CurrencyCostState rhs) {
                    return lhs.getDay().compareTo(rhs.getDay());
                }
            });
            Calendar lastDay = null;
            if (day.compareTo(currentCur.getUserEnteredCalendarses().get(0).getCalendar()) < 0) {
                lastDay = (Calendar) currentCur.getUserEnteredCalendarses().get(0).getCalendar().clone();
            }
            else if (day.compareTo(currentCur.getUserEnteredCalendarses().get(currentCur.getUserEnteredCalendarses().size()-1).getCalendar()) > 0) {
                lastDay = (Calendar) currentCur.getUserEnteredCalendarses().get(currentCur.getUserEnteredCalendarses().size()-1).getCalendar().clone();
            }
            else {
                for (int i=0; i<currentCur.getUserEnteredCalendarses().size(); i++) {
                    if (i != currentCur.getUserEnteredCalendarses().size()-1) {
                        if (day.compareTo(currentCur.getUserEnteredCalendarses().get(i).getCalendar())>0 &&
                                day.compareTo(currentCur.getUserEnteredCalendarses().get(i+1).getCalendar())<0) {
                            lastDay = (Calendar) currentCur.getUserEnteredCalendarses().get(i).getCalendar().clone();
                            break;
                        }
                    }
                }
            }
            if (lastDay == null)
                lastDay = (Calendar) allStates.get(allStates.size()-1).getDay().clone();
            String formattedLastDay = simpleDateFormat.format(lastDay.getTime());
            List<CurrencyCostState> lastDayState = currencyCostStateDao
                    .queryBuilder()
                    .where(CurrencyCostStateDao.Properties.Day.eq(formattedLastDay),
                            CurrencyCostStateDao.Properties.MainCurId.eq(mainCur.getId()))
                    .list();
            List<CurrencyWithAmount> lastStateAmounts = lastDayState.get(0).getCurrencyWithAmountList();
            List<Currency> noneMainCurrencies = currencyDao
                                        .queryBuilder()
                                        .where(CurrencyDao.Properties.IsMain.eq(false))
                                        .list();
            CurrencyCostState mainCostState = new CurrencyCostState();
            mainCostState.setMainCurrency(mainCur);
            mainCostState.setDay(day);
            currencyCostStateDao.insertOrReplace(mainCostState);
            CurrencyWithAmount mainCostAmounts = new CurrencyWithAmount();
            mainCostAmounts.setParentId(mainCostState.getId());
            mainCostAmounts.setCurrency(currentCur);
            mainCostAmounts.setAmount(amount);
            daoSession.getCurrencyWithAmountDao().insertOrReplace(mainCostAmounts);
            for (Currency currency : noneMainCurrencies) {
                if (currency.getId().equals(currentCur.getId())) continue;
                CurrencyWithAmount mainsAnotherAmount = new CurrencyWithAmount();
                mainsAnotherAmount.setCurrency(currency);
                mainsAnotherAmount.setParentId(mainCostState.getId());
                for (CurrencyWithAmount currencyWithAmount : lastStateAmounts) {
                    if (currencyWithAmount.getCurrencyId().equals(currency.getId())) {
                        mainsAnotherAmount.setAmount(currencyWithAmount.getAmount());
                    }
                }
                daoSession.getCurrencyWithAmountDao().insertOrReplace(mainsAnotherAmount);
            }
            mainCostState.resetCurrencyWithAmountList();
            List<CurrencyWithAmount> currencyWithAmounts = mainCostState.getCurrencyWithAmountList();
            for (CurrencyWithAmount currencyWithAmount : currencyWithAmounts) {
                CurrencyCostState anotherState = new CurrencyCostState();
                anotherState.setDay((Calendar)day.clone());
                anotherState.setMainCurrency(currencyWithAmount.getCurrency());
                daoSession.getCurrencyCostStateDao().insertOrReplace(anotherState);
                CurrencyWithAmount anotherStatesAmount = new CurrencyWithAmount();
                anotherStatesAmount.setCurrency(mainCostState.getMainCurrency());
                anotherStatesAmount.setAmount(1/currencyWithAmount.getAmount());
                anotherStatesAmount.setParentId(anotherState.getId());
                daoSession.getCurrencyWithAmountDao().insertOrReplace(anotherStatesAmount);
                for (CurrencyWithAmount amnt : currencyWithAmounts) {
                    if (!amnt.getCurrencyId().equals(currencyWithAmount.getCurrencyId())) {
                        CurrencyWithAmount anotherStatesRestAmounts = new CurrencyWithAmount();
                        anotherStatesRestAmounts.setCurrency(amnt.getCurrency());
                        anotherStatesRestAmounts.setAmount(amnt.getAmount()/currencyWithAmount.getAmount());
                        anotherStatesRestAmounts.setParentId(anotherState.getId());
                        daoSession.getCurrencyWithAmountDao().insertOrReplace(anotherStatesRestAmounts);
                    }
                }
            }
        }
    }

    public int insertUserEnteredCalendars(Currency currency, Calendar day) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        List<UserEnteredCalendars> list = userEnteredCalendarsDao
                .queryBuilder()
                .where(UserEnteredCalendarsDao.Properties.CurrencyId.eq(currency.getId()),
                        UserEnteredCalendarsDao.Properties.Calendar.eq(format.format(day.getTime())))
                .list();
        if (!list.isEmpty()) return LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS;
        UserEnteredCalendars userEnteredCalendars = new UserEnteredCalendars();
        userEnteredCalendars.setCalendar((Calendar)day.clone());
        userEnteredCalendars.setCurrencyId(currency.getId());
        userEnteredCalendarsDao.insertOrReplace(userEnteredCalendars);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public void setMainCurrency(Currency currency) {
        if (currency != null && currency.getMain()) return;
        List<Currency> currencies = currencyDao.loadAll();
        Currency mainCurrency = null, oldMain;
        if (currency == null) {
            int pos = 0;
            for (int i = 0; i < currencies.size(); i++) {
                if (currencies.get(i).getMain()) {
                    pos = i;
                    break;
                }
            }
            oldMain = currencies.get(pos);
            currencies.get(pos).setMain(false);
            if (pos == currencies.size() - 1) {
                currencies.get(0).setMain(true);
                mainCurrency = currencies.get(0);
            } else {
                currencies.get(pos + 1).setMain(true);
                mainCurrency = currencies.get(pos + 1);
            }
        } else {
            int oldMainPos = 0;
            int currMainPos = 0;
            for (int i = 0; i < currencies.size(); i++) {
                if (currencies.get(i).getMain()) {
                    oldMainPos = i;
                }
                if (currencies.get(i).getId().matches(currency.getId())) {
                    currMainPos = i;
                }
            }
            oldMain = currencies.get(oldMainPos);
            currencies.get(oldMainPos).setMain(false);
            currencies.get(currMainPos).setMain(true);
            mainCurrency = currencies.get(currMainPos);
        }
        Calendar nextDate, currDay;
        for (int i = 0; i < mainCurrency.getCosts().size(); i++) {
            CurrencyCost current = mainCurrency.getCosts().get(i);
            currDay = (Calendar) current.getDay().clone();
            currDay.set(Calendar.HOUR_OF_DAY, 0);
            currDay.set(Calendar.MINUTE, 0);
            currDay.set(Calendar.SECOND, 0);
            currDay.set(Calendar.MILLISECOND, 0);
            if (i != mainCurrency.getCosts().size() - 1) {
                nextDate = (Calendar) mainCurrency.getCosts().get(i + 1).getDay().clone();
                nextDate.set(Calendar.HOUR_OF_DAY, 0);
                nextDate.set(Calendar.MINUTE, 0);
                nextDate.set(Calendar.SECOND, 0);
                nextDate.set(Calendar.MILLISECOND, 0);
            } else {
                nextDate = Calendar.getInstance();
                nextDate.set(Calendar.HOUR_OF_DAY, 23);
                nextDate.set(Calendar.MINUTE, 59);
                nextDate.set(Calendar.SECOND, 59);
                nextDate.set(Calendar.MILLISECOND, 59);
            }

            for (int j = 0; j < currencies.size(); j++) {
                if (currencies.get(j).getMain()) continue;
                for (int k = 0; k < currencies.get(j).getCosts().size(); k++) {
                    CurrencyCost currencyCost = currencies.get(j).getCosts().get(k);
                    if (i == 0) {
                        if (currencyCost.getDay().compareTo(nextDate) < 0) {
                            currencyCost.setCost(currencyCost.getCost() / current.getCost());
                        }
                    } else if (currencyCost.getDay().compareTo(currDay) >= 0 && currencyCost.getDay().compareTo(nextDate) < 0)
                        currencyCost.setCost(currencyCost.getCost() / current.getCost());

                }
            }
        }
        currencyDao.insertOrReplaceInTx(currencies);
        daoSession.getCurrencyDao().detachAll();
    }

    //currency costs
    public int deleteCurrencyCosts(List<UserEnteredCalendars> userEnteredCalendarses, Currency currency) {
        List<UserEnteredCalendars> calendars = userEnteredCalendarsDao
                .queryBuilder()
                .where(UserEnteredCalendarsDao.Properties.CurrencyId.eq(currency.getId()))
                .list();
        if (userEnteredCalendarses.size() == calendars.size())
            return LogicManagerConstants.LIST_IS_EMPTY;
        userEnteredCalendarsDao.deleteInTx(userEnteredCalendarses);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertSubCategory(List<SubCategory> subCategories) {
        subCategoryDao.insertOrReplaceInTx(subCategories);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteSubcategories(List<SubCategory> subCategories) {
        for (SubCategory subCategory : subCategories) {
            for (FinanceRecord financeRecord : recordDao.loadAll()) {
                if (financeRecord.getSubCategory() != null && financeRecord.getSubCategory().getId().equals(subCategory.getId()))
                    recordDao.delete(financeRecord);
            }
        }
        subCategoryDao.deleteInTx(subCategories);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public void changeBoardButton(int type, int pos, String categoryId) {
        int t = PocketAccounterGeneral.CATEGORY;
        if (categoryId != null) {
            List<RootCategory> categoryList = daoSession.getRootCategoryDao().loadAll();
            boolean categoryFound = false, operationFound = false, creditFound = false,
                    debtBorrowFound = false;
            for (RootCategory category : categoryList) {
                if (categoryId.matches(category.getId())) {
                    categoryFound = true;
                    t = PocketAccounterGeneral.CATEGORY;
                    break;
                }
            }
            if (!categoryFound) {
                String[] operationIds = context.getResources().getStringArray(R.array.operation_ids);
                for (String operationId : operationIds) {
                    if (operationId.matches(categoryId)) {
                        operationFound = true;
                        t = PocketAccounterGeneral.FUNCTION;
                        break;
                    }
                }
            }
            if (!operationFound) {
                List<CreditDetials> credits = daoSession.getCreditDetialsDao().loadAll();
                for (CreditDetials creditDetials : credits) {
                    if (Long.toString(creditDetials.getMyCredit_id()).matches(categoryId)) {
                        creditFound = true;
                        t = PocketAccounterGeneral.CREDIT;
                        break;
                    }
                }
            }
            if (!creditFound) {
                List<DebtBorrow> debtBorrows = daoSession.getDebtBorrowDao().loadAll();
                for (DebtBorrow debtBorrow : debtBorrows) {
                    if (debtBorrow.getId().matches(categoryId)) {
                        debtBorrowFound = true;
                        t = PocketAccounterGeneral.DEBT_BORROW;
                        break;
                    }
                }
            }
            if (!debtBorrowFound) {
                String[] pageIds = context.getResources().getStringArray(R.array.page_ids);
                for (int i = 0; i < pageIds.length; i++) {
                    if (pageIds[i].matches(categoryId)) {
                        t = PocketAccounterGeneral.PAGE;
                        break;
                    }
                }
            }
        }
        Query<BoardButton> query = boardButtonDao
                .queryBuilder()
                .where(BoardButtonDao.Properties.Table.eq(type),
                        BoardButtonDao.Properties.Pos.eq(pos))
                .build();
        List<BoardButton> list = query.list();
        BoardButton boardButton = null;
        if (!list.isEmpty()) {
            boardButton = list.get(0);
            boardButton.setCategoryId(categoryId);
        }
        if (boardButton != null)
            boardButton.setType(t);
        boardButtonDao.insertOrReplace(boardButton);
    }

    public int insertRootCategory(RootCategory rootCategory) {
        Query<RootCategory> query = rootCategoryDao
                .queryBuilder()
                .where(RootCategoryDao.Properties.Name.eq(rootCategory.getName()))
                .build();
        if (!query.list().isEmpty())
            return LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS;
        rootCategoryDao.insertOrReplace(rootCategory);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteRootCategory(RootCategory category) {
        for (FinanceRecord record : recordDao.loadAll())
            if (record.getCategory().getId().matches(category.getId()))
                recordDao.delete(record);
        for (BoardButton boardButton : boardButtonDao.loadAll())
            if (boardButton.getCategoryId() != null && boardButton.getCategoryId().matches(category.getId())) {
                boardButton.setCategoryId(null);
                boardButtonDao.insertOrReplace(boardButton);
                commonOperations.changeIconToNull(boardButton.getPos(), dataCache, boardButton.getTable());
            }
        rootCategoryDao.delete(category);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertPurpose(Purpose purpose) {
        Query<Purpose> query = purposeDao
                .queryBuilder()
                .where(PurposeDao.Properties.Id.eq(purpose.getId()))
                .build();
        if (query.list().isEmpty()) {
            query = purposeDao
                    .queryBuilder()
                    .where(PurposeDao.Properties.Description.eq(purpose.getDescription()))
                    .build();
            if (!query.list().isEmpty())
                return LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS;
        }
        purposeDao.insertOrReplace(purpose);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deletePurpose(Purpose purpose) {
        Query<Purpose> query = purposeDao
                .queryBuilder()
                .where(PurposeDao.Properties.Id.eq(purpose.getId()))
                .build();
        if (query.list().isEmpty()) {
            return LogicManagerConstants.REQUESTED_OBJECT_NOT_FOUND;
        }
        purposeDao.delete(purpose);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertDebtBorrow(DebtBorrow debtBorrow) {

        debtBorrowDao.insertOrReplace(debtBorrow);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteDebtBorrow(DebtBorrow debtBorrow) {
        Query<DebtBorrow> query = debtBorrowDao.queryBuilder()
                .where(DebtBorrowDao.Properties.Id.eq(debtBorrow.getId()))
                .build();
        if (query.list().isEmpty()) {
            return LogicManagerConstants.REQUESTED_OBJECT_NOT_FOUND;
        }
        debtBorrowDao.delete(debtBorrow);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertPerson(Person person) {
        Query<Person> query = personDao
                .queryBuilder()
                .where(PersonDao.Properties.Id.eq(person.getId()))
                .build();
        personDao.insertOrReplace(person);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int insertCredit(CreditDetials creditDetials) {
        creditDetialsDao.insertOrReplace(creditDetials);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteCredit(CreditDetials creditDetials) {
        Query<CreditDetials> query = creditDetialsDao.queryBuilder()
                .where(CreditDetialsDao.Properties.MyCredit_id.eq(creditDetials.getMyCredit_id()))
                .build();
        if (query.list().isEmpty()) {
            return LogicManagerConstants.REQUESTED_OBJECT_NOT_FOUND;
        }
        creditDetialsDao.delete(creditDetials);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertReckingDebt(Recking recking) {
        reckingDao.insertOrReplace(recking);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteRecking(Recking recking) {
        reckingDao.delete(recking);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertReckingCredit(ReckingCredit reckingCredit) {
        reckingCreditDao.insertOrReplace(reckingCredit);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteReckingCredit(ReckingCredit reckingCredit) {
        reckingCreditDao.delete(reckingCredit);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertAccountOperation(AccountOperation accountOperation) {
        accountOperationDao.insertOrReplace(accountOperation);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteAccountOperation(AccountOperation accountOperation) {
        accountOperationDao.delete(accountOperation);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int insertAutoMarket(AutoMarket autoMarket) {
        autoMarket.__setDaoSession(daoSession);
        Query<AutoMarket> query = autoMarketDao.queryBuilder()
                .where(autoMarketDao.queryBuilder()
                        .and(AutoMarketDao.Properties.CatId.eq(autoMarket.getCatId()),
                                AutoMarketDao.Properties.CatSubId.eq(autoMarket.getSubCategory() == null ? "" : autoMarket.getCatSubId()))).build();

        if (query.list() != null && query.list().isEmpty()) {
            autoMarketDao.insertOrReplace(autoMarket);
            return LogicManagerConstants.SAVED_SUCCESSFULL;
        }
        return LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS;
    }

    public int deleteAutoMarket(AutoMarket autoMarket) {
        autoMarketDao.delete(autoMarket);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int deleteSmsParseObject(SmsParseObject smsParseObject) {
        smsParseObjectDao.delete(smsParseObject);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int deleteSmsParseSuccess(SmsParseSuccess smsParseSuccess) {
        smsParseSuccessDao.delete(smsParseSuccess);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public double isLimitAccess(Account account, Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        double accounted = commonOperations.getCost(date, account.getStartMoneyCurrency(), account.getAmount());
        for (int i = 0; i < recordDao.queryBuilder().list().size(); i++) {
            FinanceRecord tempac = recordDao.queryBuilder().list().get(i);
            if (tempac.getAccount().getId().matches(account.getId())) {
                if (tempac.getCategory().getType() == PocketAccounterGeneral.INCOME)
                    accounted = accounted + commonOperations.getCost(tempac.getDate(), tempac.getCurrency(), account.getCurrency(), tempac.getAmount());
                else
                    accounted = accounted - commonOperations.getCost(tempac.getDate(), tempac.getCurrency(), account.getCurrency(), tempac.getAmount());
            }
        }
        for (DebtBorrow debtBorrow : debtBorrowDao.queryBuilder().list()) {
            if (debtBorrow.getCalculate()) {
                if (debtBorrow.getAccount().getId().matches(account.getId())) {
                    if (debtBorrow.getType() == DebtBorrow.BORROW) {
                        accounted = accounted - commonOperations.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), account.getCurrency(), debtBorrow.getAmount());
                    } else {
                        accounted = accounted + commonOperations.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), account.getCurrency(), debtBorrow.getAmount());
                    }
                    for (Recking recking : debtBorrow.getReckings()) {
                        Calendar cal = recking.getPayDate();

                        if (debtBorrow.getType() == DebtBorrow.DEBT) {
                            accounted = accounted - commonOperations.getCost(cal, debtBorrow.getCurrency(), account.getCurrency(), recking.getAmount());
                        } else {
                            accounted = accounted + commonOperations.getCost(cal, debtBorrow.getCurrency(), account.getCurrency(), recking.getAmount());
                        }
                    }
                } else {
                    for (Recking recking : debtBorrow.getReckings()) {
                        Calendar cal = recking.getPayDate();
                        if (recking.getAccountId().matches(account.getId())) {

                            if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                accounted = accounted + commonOperations.getCost(cal, debtBorrow.getCurrency(), account.getCurrency(), recking.getAmount());
                            } else {
                                accounted = accounted - commonOperations.getCost(cal, debtBorrow.getCurrency(), account.getCurrency(), recking.getAmount());
                            }
                        }
                    }
                }
            }
        }
        for (CreditDetials creditDetials : creditDetialsDao.queryBuilder().list()) {
            if (creditDetials.getKey_for_include()) {
                for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
                    if (reckingCredit.getAccountId().matches(account.getId())) {
                        accounted = accounted - commonOperations.getCost(reckingCredit.getPayDate(), creditDetials.getValyute_currency(), account.getCurrency(), reckingCredit.getAmount());
                    }
                }
            }
        }
        for (SmsParseSuccess success: smsParseSuccessDao.loadAll()) {
            if (success.getType() == PocketAccounterGeneral.INCOME) {
                accounted += commonOperations.getCost(success.getDate(), success.getCurrency(), success.getAmount());
            } else {
                accounted -= commonOperations.getCost(success.getDate(), success.getCurrency(), success.getAmount());
            }
        }
        return accounted;
    }

    public int insertRecord(FinanceRecord record) {
        recordDao.insertOrReplace(record);
        return LogicManagerConstants.SAVED_SUCCESSFULL;
    }

    public int deleteRecord(FinanceRecord record) {
        recordDao.delete(record);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }
}