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

    public void generateCurrencyCosts(Calendar day, double  amount, Currency adding) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String addingDay = format.format(day.getTime());
        Currency mainCurrency = commonOperations.getMainCurrency();
        List<Currency> notMainCurrencies = daoSession.queryBuilder(Currency.class).where(CurrencyDao.Properties.IsMain.eq(false)).list();
        boolean isNew = daoSession.queryBuilder(CurrencyCostState.class).where(CurrencyCostStateDao.Properties.MainCurId.eq(adding.getId())).list().isEmpty();
        if (isNew) {
            List<CurrencyCostState> list = daoSession
                    .queryBuilder(CurrencyCostState.class)
                    .where(CurrencyCostStateDao.Properties.Day.eq(addingDay))
                    .list();
            if (list.isEmpty()) {
                List<CurrencyCostState> allStates = daoSession.loadAll(CurrencyCostState.class);
                Collections.sort(allStates, new Comparator<CurrencyCostState>() {
                    @Override
                    public int compare(CurrencyCostState currencyCostState, CurrencyCostState t1) {
                        return currencyCostState.getDay().compareTo(t1.getDay());
                    }
                });
                String last = "";
                if (day.compareTo(allStates.get(allStates.size()-1).getDay()) >= 0) {
                    last = format.format(allStates.get(allStates.size()-1).getDay().getTime());
                } else if (day.compareTo(allStates.get(0).getDay()) <= 0) {
                    last = format.format(allStates.get(0).getDay().getTime());
                } else {
                    int position = 0;
                    while (position < allStates.size() && day.compareTo(allStates.get(position).getDay()) > 0) {
                        last = format.format(allStates.get(position).getDay().getTime());
                        position++;
                    }
                }
                List<CurrencyCostState> lastStates = daoSession
                        .queryBuilder(CurrencyCostState.class)
                        .where(CurrencyCostStateDao.Properties.Day.eq(last))
                        .list();
                for (CurrencyCostState currencyCostState : lastStates) {
                    CurrencyCostState state = new CurrencyCostState();
                    state.setDay(day);
                    state.setMainCurrency(currencyCostState.getMainCurrency());
                    daoSession.insertOrReplace(state);
                    if (currencyCostState.getMainCurId().equals(mainCurrency.getId())) {
                        CurrencyWithAmount withAmount = new CurrencyWithAmount();
                        withAmount.setParentId(state.getId());
                        withAmount.setCurrency(adding);
                        withAmount.setAmount(amount);
                        daoSession.insertOrReplace(withAmount);
                    }
                    else {
                        double tempAmount = 1.0d;
                        CurrencyCostState main = null;
                        for (CurrencyCostState st : lastStates) {
                            if (st.getMainCurId().equals(mainCurrency.getId())) {
                                main = st;
                                break;
                            }
                        }
                        for (CurrencyWithAmount withAmount : main.getCurrencyWithAmountList()) {
                            if (withAmount.getCurrencyId().equals(currencyCostState.getMainCurId())) {
                                tempAmount = withAmount.getAmount();
                                break;
                            }
                        }
                        CurrencyWithAmount withAmount = new CurrencyWithAmount();
                        withAmount.setCurrency(adding);
                        withAmount.setParentId(state.getId());
                        withAmount.setAmount(amount/tempAmount);
                        daoSession.insertOrReplace(withAmount);

                    }
                    for (CurrencyWithAmount cwa : currencyCostState.getCurrencyWithAmountList()) {
                        CurrencyWithAmount withAmount = new CurrencyWithAmount();
                        withAmount.setParentId(state.getId());
                        withAmount.setCurrency(cwa.getCurrency());
                        withAmount.setAmount(cwa.getAmount());
                        daoSession.insertOrReplace(withAmount);
                    }
                    state.resetCurrencyWithAmountList();
                }
                CurrencyCostState addingState = new CurrencyCostState();
                addingState.setMainCurrency(adding);
                addingState.setDay(day);
                daoSession.insertOrReplace(addingState);
                CurrencyWithAmount mainWithAmount = new CurrencyWithAmount();
                mainWithAmount.setParentId(addingState.getId());
                mainWithAmount.setCurrency(mainCurrency);
                mainWithAmount.setAmount(1/amount);
                daoSession.insertOrReplace(mainWithAmount);
                CurrencyCostState main = null;
                for (CurrencyCostState st : lastStates) {
                    if (st.getMainCurId().equals(mainCurrency.getId())) {
                        main = st;
                        break;
                    }
                }
                for (Currency currency : notMainCurrencies) {
                    double tempAmount = 1.0d;
                    for (CurrencyWithAmount withAmount : main.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(currency.getId())) {
                            tempAmount = withAmount.getAmount();
                            break;
                        }
                    }
                    CurrencyWithAmount withAmount = new CurrencyWithAmount();
                    withAmount.setCurrency(adding);
                    withAmount.setParentId(addingState.getId());
                    withAmount.setAmount(amount/tempAmount);
                    daoSession.insertOrReplace(withAmount);
                }
                addingState.resetCurrencyWithAmountList();
            } else {
                CurrencyCostState mainState = null;
                for (CurrencyCostState state : list) {
                    if (state.getMainCurId().equals(mainCurrency.getId())) {
                        mainState = state;
                        break;
                    }
                }
                for (CurrencyCostState state : list) {
                    CurrencyWithAmount withAmount = new CurrencyWithAmount();
                    withAmount.setCurrency(adding);
                    withAmount.setParentId(state.getId());
                    if (state.getMainCurId().equals(mainCurrency.getId())) {
                        withAmount.setAmount(amount);
                    } else {
                        double tempAmount = 1.0d;
                        for (CurrencyWithAmount wa : mainState.getCurrencyWithAmountList()) {
                            if (withAmount.getCurrencyId().equals(state.getMainCurId())) {
                                tempAmount = wa.getAmount();
                                break;
                            }
                        }
                        withAmount.setAmount(amount/tempAmount);
                    }
                    daoSession.insertOrReplace(withAmount);
                    state.resetCurrencyWithAmountList();
                }
                CurrencyCostState addingState = new CurrencyCostState();
                addingState.setMainCurrency(adding);
                addingState.setDay(day);
                daoSession.insertOrReplace(addingState);
                CurrencyWithAmount mainWithAmount = new CurrencyWithAmount();
                mainWithAmount.setParentId(addingState.getId());
                mainWithAmount.setCurrency(mainCurrency);
                mainWithAmount.setAmount(1/amount);
                daoSession.insertOrReplace(mainWithAmount);
                addingState.resetCurrencyWithAmountList();
                CurrencyCostState main = null;
                for (CurrencyCostState st : list) {
                    if (st.getMainCurId().equals(mainCurrency.getId())) {
                        main = st;
                        break;
                    }
                }
                for (Currency currency : notMainCurrencies) {
                    double tempAmount = 1.0d;
                    for (CurrencyWithAmount withAmount : main.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(currency.getId())) {
                            tempAmount = withAmount.getAmount();
                            break;
                        }
                    }
                    CurrencyWithAmount withAmount = new CurrencyWithAmount();
                    withAmount.setCurrency(currency);
                    withAmount.setParentId(addingState.getId());
                    withAmount.setAmount(amount/tempAmount);
                    daoSession.insertOrReplace(withAmount);
                }
                addingState.resetCurrencyWithAmountList();
            }
            //generate for other days
            List<CurrencyCostState> allStatesWithoutToday = daoSession
                    .queryBuilder(CurrencyCostState.class)
                    .where(CurrencyCostStateDao.Properties.Day.notEq(addingDay))
                    .list();
            List<String> days = new ArrayList<>();
            for (CurrencyCostState state : allStatesWithoutToday) {
                boolean found = false;
                for (String temp : days) {
                    if (format.format(state.getDay().getTime()).equals(temp)) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    days.add(format.format(state.getDay().getTime()));
            }
            for (String temp : days) {
                List<CurrencyCostState> statesForTheDay = daoSession
                        .queryBuilder(CurrencyCostState.class)
                        .where(CurrencyCostStateDao.Properties.Day.eq(temp))
                        .list();
                CurrencyCostState mainState = null;
                for (CurrencyCostState state : statesForTheDay) {
                    if (state.getMainCurId().equals(mainCurrency.getId())) {
                        mainState = state;
                        break;
                    }
                }
                CurrencyCostState addingState = new CurrencyCostState();
                addingState.setDay(day);
                addingState.setMainCurrency(adding);
                daoSession.insertOrReplace(addingState);
                CurrencyWithAmount mainWithAmount = new CurrencyWithAmount();
                mainWithAmount.setCurrency(mainCurrency);
                mainWithAmount.setAmount(1/amount);
                mainWithAmount.setParentId(addingState.getId());
                daoSession.insertOrReplace(mainWithAmount);
                for (Currency currency : notMainCurrencies) {
                    CurrencyWithAmount currencyWithAmount = new CurrencyWithAmount();
                    currencyWithAmount.setParentId(addingState.getId());
                    currencyWithAmount.setCurrency(currency);
                    double tempAmount = 1.0d;
                    for (CurrencyWithAmount withAmount : mainState.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(currency.getId())) {
                            tempAmount = withAmount.getAmount();
                            break;
                        }
                    }
                    currencyWithAmount.setAmount(amount/tempAmount);
                    daoSession.insertOrReplace(currencyWithAmount);
                }
                addingState.resetCurrencyWithAmountList();
                for (CurrencyCostState state : statesForTheDay) {
                    CurrencyWithAmount currencyWithAmount = new CurrencyWithAmount();
                    currencyWithAmount.setParentId(state.getId());
                    currencyWithAmount.setCurrency(adding);
                    if (state.getMainCurId().equals(mainCurrency.getId())) {
                        currencyWithAmount.setAmount(amount);
                    }
                    else {
                        double tempAmount = 1.0d;
                        for (CurrencyWithAmount withAmount : mainState.getCurrencyWithAmountList()) {
                            if (withAmount.getCurrencyId().equals(state.getMainCurId())) {
                                tempAmount = withAmount.getAmount();
                                break;
                            }
                        }
                        currencyWithAmount.setAmount(amount/tempAmount);
                    }
                    daoSession.insertOrReplace(currencyWithAmount);
                    state.resetCurrencyWithAmountList();
                }
            }
        }
        else {
            List<CurrencyCostState> list = daoSession
                    .queryBuilder(CurrencyCostState.class)
                    .where(CurrencyCostStateDao.Properties.Day.eq(addingDay))
                    .list();
            if (list.isEmpty()) {
                CurrencyCostState supplimentaryState = null;
                List<CurrencyCostState> allStates = daoSession
                        .queryBuilder(CurrencyCostState.class)
                        .where(CurrencyCostStateDao.Properties.MainCurId.eq(mainCurrency.getId()))
                        .list();
                Collections.sort(allStates, new Comparator<CurrencyCostState>() {
                    @Override
                    public int compare(CurrencyCostState currencyCostState, CurrencyCostState t1) {
                        return currencyCostState.getDay().compareTo(t1.getDay());
                    }
                });
                if (allStates.get(allStates.size() - 1).getDay().compareTo(day) <= 0)
                    supplimentaryState = allStates.get(allStates.size() - 1);
                else if (allStates.get(0).getDay().compareTo(day) >= 0)
                    supplimentaryState = allStates.get(0);
                else {
                    int position = 0;
                    while (allStates.size() > position && allStates.get(position).getDay().compareTo(day) <= 0) {
                        supplimentaryState = allStates.get(position);
                        position++;
                    }
                }
                CurrencyCostState state = new CurrencyCostState();
                state.setDay(day);
                state.setMainCurrency(mainCurrency);
                daoSession.insertOrReplace(state);
                for (CurrencyWithAmount withAmount : supplimentaryState.getCurrencyWithAmountList()) {
                    CurrencyWithAmount currencyWithAmount = new CurrencyWithAmount();
                    currencyWithAmount.setParentId(state.getId());
                    currencyWithAmount.setCurrency(adding);
                    if (withAmount.getCurrencyId().equals(adding.getId())) {
                        currencyWithAmount.setAmount(amount);
                    } else {
                        currencyWithAmount.setAmount(withAmount.getAmount());
                    }
                    daoSession.insertOrReplace(currencyWithAmount);
                }
                state.resetCurrencyWithAmountList();
                List<CurrencyCostState> notMainStates = daoSession
                        .queryBuilder(CurrencyCostState.class)
                        .where(CurrencyCostStateDao.Properties.Day.eq(format.format(supplimentaryState.getDay().getTime())))
                        .list();
                for (Currency currency : notMainCurrencies) {
                    if (currency.getId().equals(adding.getId())) {
                        CurrencyCostState st = new CurrencyCostState();
                        st.setDay(day);
                        st.setMainCurrency(currency);
                        daoSession.insertOrReplace(st);
                        CurrencyCostState supply = null;
                        for (CurrencyCostState costState : notMainStates) {
                            if (costState.getMainCurId().equals(currency.getId())) {
                                supply = costState;
                                break;
                            }
                        }
                        for (CurrencyWithAmount withAmount : supply.getCurrencyWithAmountList()) {
                            CurrencyWithAmount currencyWithAmount = new CurrencyWithAmount();
                            if (withAmount.getCurrencyId().equals(mainCurrency.getId()))
                                currencyWithAmount.setAmount(1/amount);
                            else
                                currencyWithAmount.setAmount(withAmount.getAmount());
                            currencyWithAmount.setCurrency(withAmount.getCurrency());
                            currencyWithAmount.setParentId(st.getId());
                            daoSession.insertOrReplace(currencyWithAmount);
                        }
                        st.resetCurrencyWithAmountList();
                    }
                    else {
                        CurrencyCostState st = new CurrencyCostState();
                        st.setDay(day);
                        st.setMainCurrency(currency);
                        daoSession.insertOrReplace(st);
                        CurrencyCostState supply = null;
                        for (CurrencyCostState costState : notMainStates) {
                            if (costState.getMainCurId().equals(currency.getId())) {
                                supply = costState;
                                break;
                            }
                        }
                        for (CurrencyWithAmount withAmount : supply.getCurrencyWithAmountList()) {
                            CurrencyWithAmount currencyWithAmount = new CurrencyWithAmount();
                            currencyWithAmount.setAmount(withAmount.getAmount());
                            currencyWithAmount.setCurrency(withAmount.getCurrency());
                            currencyWithAmount.setParentId(st.getId());
                            daoSession.insertOrReplace(currencyWithAmount);
                        }
                        st.resetCurrencyWithAmountList();
                    }
                }
            }
            else {
                List<CurrencyCostState> states = daoSession
                        .queryBuilder(CurrencyCostState.class)
                        .where(CurrencyCostStateDao.Properties.Day.eq(format.format(list.get(0).getDay().getTime())))
                        .list();
                for (CurrencyCostState state : states) {
                    if (state.getMainCurId().equals(adding.getId())) {
                        for (CurrencyWithAmount withAmount : state.getCurrencyWithAmountList()) {
                            if (withAmount.getCurrencyId().equals(mainCurrency.getId())) {
                                withAmount.setAmount(1/amount);
                                daoSession.insertOrReplace(withAmount);
                                break;
                            }
                        }
                    }
                    else if (state.getMainCurId().equals(mainCurrency.getId())) {
                        for (CurrencyWithAmount withAmount : state.getCurrencyWithAmountList()) {
                            if (withAmount.getCurrencyId().equals(adding.getId())) {
                                withAmount.setAmount(amount);
                                daoSession.insertOrReplace(withAmount);
                                break;
                            }
                        }
                    }
                    state.resetCurrencyWithAmountList();
                }
            }
            //TODO rest days
            List<CurrencyCostState> states = daoSession.loadAll(CurrencyCostState.class);
            Collections.sort(states, new Comparator<CurrencyCostState>() {
                @Override
                public int compare(CurrencyCostState currencyCostState, CurrencyCostState t1) {
                    return currencyCostState.getDay().compareTo(t1.getDay());
                }
            });
            List<CurrencyCostState> otherDays = new ArrayList<>();
            Calendar addDay = (Calendar) day.clone();
            addDay.set(Calendar.HOUR_OF_DAY, 23);
            addDay.set(Calendar.MINUTE, 59);
            addDay.set(Calendar.SECOND, 59);
            addDay.set(Calendar.MILLISECOND, 59);
            for (CurrencyCostState state : states) {
                if (state.getDay().compareTo(addDay) > 0) {
                    otherDays.add(state);
                }
            }
            for (CurrencyCostState state : otherDays) {
                if (dayExists(adding, state.getDay()))
                    break;
                if (state.equals(mainCurrency.getId())) {
                    for (CurrencyWithAmount withAmount : state.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(adding.getId())) {
                            withAmount.setAmount(amount);
                            daoSession.insertOrReplace(withAmount);
                            break;
                        }
                    }
                }
                else if (state.equals(adding.getId())) {
                    for (CurrencyWithAmount withAmount : state.getCurrencyWithAmountList()) {
                        if (withAmount.getCurrencyId().equals(mainCurrency.getId())) {
                            withAmount.setAmount(1/amount);
                            daoSession.insertOrReplace(withAmount);
                            break;
                        }
                    }
                }
                state.resetCurrencyWithAmountList();
            }
        }
        List<CurrencyCostState> allStates = daoSession.loadAll(CurrencyCostState.class);
        List<Currency> allCurrencies = daoSession.loadAll(Currency.class);
        for (CurrencyCostState state : allStates)
            state.resetCurrencyWithAmountList();
        for (Currency currency : allCurrencies)
            currency.refreshCosts();
    }
    private boolean dayExists(Currency currency, Calendar day) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        for (UserEnteredCalendars calendar : currency.getUserEnteredCalendarses()) {
            if (format.format(calendar.getCalendar().getTime()).equals(format.format(day.getTime()))) {
                return true;
            }
        }
        return false;
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
        if (currency != null) if( currency.getMain()) return;
        List<Currency> currencies = daoSession.getCurrencyDao().loadAll();
        Currency mainCurrency = null, oldMain;
        if (currency == null) {
            int pos = 0;
            for (int i = 0; i < currencies.size(); i++) {
                if (currencies.get(i).getMain()) {
                    pos = i;
                    break;
                }
            }
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
                    currencies.get(j).getCosts().set(k,currencyCost);
                }
            }
        }
        daoSession.getCurrencyDao().insertOrReplaceInTx(currencies);
        List<CurrencyCostState> allStates = daoSession.loadAll(CurrencyCostState.class);
        for (CurrencyCostState state : allStates)
            state.resetCurrencyWithAmountList();
        List<Currency> allCurrencies = daoSession.loadAll(Currency.class);
        for (Currency curr : allCurrencies)
            curr.refreshCosts();
        commonOperations.refreshCurrency();
//

//        currencyDao.insertOrReplaceInTx(currencies);
//        daoSession.getCurrencyDao().detachAll();
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
        smsParseSuccessDao.deleteInTx(smsParseSuccessDao.queryBuilder().
                where(SmsParseSuccessDao.Properties
                        .SmsParseObjectId.eq(smsParseObject.getId())).list());
        smsParseObjectDao.delete(smsParseObject);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public int deleteSmsParseSuccess(SmsParseSuccess smsParseSuccess) {
        smsParseSuccessDao.delete(smsParseSuccess);
        return LogicManagerConstants.DELETED_SUCCESSFUL;
    }

    public double isLimitAccess(Account account, Calendar date) {
        double accounted = commonOperations.getCost(date, account.getStartMoneyCurrency(), account.getAmount());
        List<AccountOperation> operations = daoSession.getAccountOperationDao().loadAll();
        for (AccountOperation accountOperation : operations) {
            if (accountOperation.getSourceId().equals(account.getId())) {
                accounted -= commonOperations.getCost(date, accountOperation.getCurrency(), accountOperation.getAmount());
            }
            if (accountOperation.getTargetId().equals(account.getId())) {
                accounted += commonOperations.getCost(date, accountOperation.getCurrency(), accountOperation.getAmount());
            }
        }
        for (int i = 0; i < recordDao.queryBuilder().list().size(); i++) {
            FinanceRecord tempac = recordDao.queryBuilder().list().get(i);
            if (tempac.getAccount().getId().matches(account.getId())) {
                if (tempac.getCategory().getType() == PocketAccounterGeneral.INCOME)
                    accounted = accounted + commonOperations.getCost(tempac.getDate(), tempac.getCurrency(), tempac.getAmount());
                else
                    accounted = accounted - commonOperations.getCost(tempac.getDate(), tempac.getCurrency(), tempac.getAmount());
            }
        }
        for (DebtBorrow debtBorrow : debtBorrowDao.queryBuilder().list()) {
            if (debtBorrow.getCalculate()) {
                if (debtBorrow.getAccount().getId().matches(account.getId())) {
                    if (debtBorrow.getType() == DebtBorrow.BORROW) {
                        accounted = accounted - commonOperations.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
                    } else {
                        accounted = accounted + commonOperations.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
                    }
                    for (Recking recking : debtBorrow.getReckings()) {
                        Calendar cal = recking.getPayDate();

                        if (debtBorrow.getType() == DebtBorrow.DEBT) {
                            accounted = accounted - commonOperations.getCost(cal, debtBorrow.getCurrency(), recking.getAmount());
                        } else {
                            accounted = accounted + commonOperations.getCost(cal, debtBorrow.getCurrency(), recking.getAmount());
                        }
                    }
                } else {
                    for (Recking recking : debtBorrow.getReckings()) {
                        Calendar cal = recking.getPayDate();
                        if (recking.getAccountId().matches(account.getId())) {

                            if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                accounted = accounted + commonOperations.getCost(cal, debtBorrow.getCurrency(), recking.getAmount());
                            } else {
                                accounted = accounted - commonOperations.getCost(cal, debtBorrow.getCurrency(), recking.getAmount());
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
                        accounted = accounted - commonOperations.getCost(reckingCredit.getPayDate(), creditDetials.getValyute_currency(), reckingCredit.getAmount());
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