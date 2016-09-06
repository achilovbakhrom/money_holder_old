package com.jim.pocketaccounter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.jim.pocketaccounter.Configuration;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyCost;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.Person;
import com.jim.pocketaccounter.database.PhotoDetails;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SubCategory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by DEV on 28.08.2016.
 */

public class SystemConfigurator {
    private Context context;
    @Inject
    DaoSession daoSession;
    @Inject
    SharedPreferences preferences;
    public SystemConfigurator(Context context) {
        this.context = context;
        ((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);

    }
    public void configurate() {
        migrateDatabase();
    }
    private void migrateDatabase() {
        String  currentDBPath= "//data//" + context.getPackageName().toString()
                + "//databases//" + Configuration.OLD_DB_NAME;
        File oldDBFile = new File(Environment.getDataDirectory(), currentDBPath);
        if (oldDBFile.exists()) {
            SQLiteDatabase old = SQLiteDatabase.openDatabase(oldDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = old.query("currency_table", null, null, null, null, null, null);
            Cursor costCursor = old.query("currency_costs_table", null, null, null, null, null, null);
            //loading currencies
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            List<Currency> currencies = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Currency newCurrency = new Currency(cursor.getString(cursor.getColumnIndex("currency_name")));
                newCurrency.setAbbr(cursor.getString(cursor.getColumnIndex("currency_sign")));
                String currId = cursor.getString(cursor.getColumnIndex("currency_id"));
                newCurrency.setId(currId);
                newCurrency.setMain(cursor.getInt(cursor.getColumnIndex("currency_main"))!=0);
                costCursor.moveToFirst();
                while(!costCursor.isAfterLast()) {
                    if (costCursor.getString(costCursor.getColumnIndex("currency_id")).matches(currId)) {
                        CurrencyCost newCurrencyCost = new CurrencyCost();
                        try {
                            Calendar day = Calendar.getInstance();
                            day.setTime(dateFormat.parse(costCursor.getString(costCursor.getColumnIndex("date"))));
                            newCurrencyCost.setDay(day);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        newCurrencyCost.setCost(costCursor.getDouble(costCursor.getColumnIndex("cost")));
                        newCurrency.getCosts().add(newCurrencyCost);
                    }
                    costCursor.moveToNext();
                }
                currencies.add(newCurrency);
                cursor.moveToNext();
            }

            //loading categories
            Cursor catCursor = old.query("category_table", null, null, null, null, null, null);
            Cursor subcatCursor = old.query("subcategory_table", null, null, null, null, null, null);
            ArrayList<RootCategory> categories = new ArrayList<>();
            catCursor.moveToFirst();
            while(!catCursor.isAfterLast()) {
                RootCategory newCategory = new RootCategory();
                newCategory.setName(catCursor.getString(catCursor.getColumnIndex("category_name")));
                String catId = catCursor.getString(catCursor.getColumnIndex("category_id"));
                newCategory.setId(catId);
                newCategory.setType(catCursor.getInt(catCursor.getColumnIndex("category_type")));
                newCategory.setIcon(catCursor.getString(catCursor.getColumnIndex("icon")));
                subcatCursor.moveToFirst();
                List<SubCategory> subCats = new ArrayList<>();
                while(!subcatCursor.isAfterLast()) {
                    if (subcatCursor.getString(subcatCursor.getColumnIndex("category_id")).matches(catId)) {
                        SubCategory newSubCategory = new SubCategory();
                        newSubCategory.setName(subcatCursor.getString(subcatCursor.getColumnIndex("subcategory_name")));
                        newSubCategory.setId(subcatCursor.getString(subcatCursor.getColumnIndex("subcategory_id")));
                        newSubCategory.setParentId(catId);
                        newSubCategory.setIcon(subcatCursor.getString(subcatCursor.getColumnIndex("icon")));
                        subCats.add(newSubCategory);
                    }
                    subcatCursor.moveToNext();
                }
                newCategory.setSubCategories(subCats);
                categories.add(newCategory);
                catCursor.moveToNext();
            }

            //load incomes
            ArrayList<RootCategory> incomes = new ArrayList<>();
            cursor = old.query("incomes_table", null, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                RootCategory newCategory = new RootCategory();
                if (cursor.getString(cursor.getColumnIndex("category_name")).matches(context.getResources().getString(R.string.no_category))) {
                    incomes.add(null);
                    cursor.moveToNext();
                    continue;
                }
                newCategory.setName(cursor.getString(cursor.getColumnIndex("category_name")));
                newCategory.setId(cursor.getString(cursor.getColumnIndex("category_id")));
                newCategory.setType(cursor.getInt(cursor.getColumnIndex("category_type")));
                newCategory.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
                incomes.add(newCategory);
                cursor.moveToNext();
            }

            //load expenses
            ArrayList<RootCategory> expenses = new ArrayList<RootCategory>();
            cursor = old.query("expanses_table", null, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                RootCategory newCategory = new RootCategory();
                if (cursor.getString(cursor.getColumnIndex("category_name")).matches(context.getResources().getString(R.string.no_category))) {
                    expenses.add(null);
                } else {
                    newCategory.setName(cursor.getString(cursor.getColumnIndex("category_name")));
                    newCategory.setId(cursor.getString(cursor.getColumnIndex("category_id")));
                    newCategory.setType(cursor.getInt(cursor.getColumnIndex("category_type")));
                    newCategory.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
                    expenses.add(newCategory);
                }
                cursor.moveToNext();
            }

            ArrayList<Account> accounts = new ArrayList<Account>();
            cursor = old.query("account_table", null, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                Account newAccount = new Account();
                newAccount.setName(cursor.getString(cursor.getColumnIndex("account_name")));
                newAccount.setId(cursor.getString(cursor.getColumnIndex("account_id")));
                int resId = cursor.getInt(cursor.getColumnIndex("icon"));
                boolean iconFound = false;
                int pos = 0;
                String icons[] = context.getResources().getStringArray(R.array.icons);
                for (int i=0; i<icons.length; i++) {
                    if (context.getResources().getIdentifier(icons[i], "drawable", context.getPackageName()) == resId) {
                        iconFound = true;
                        pos = i;
                        break;
                    }
                }
                newAccount.setIcon(iconFound ? icons[pos] : "icons_25");
                newAccount.setAmount(cursor.getDouble(cursor.getColumnIndex("start_amount")));
                String startMoneyCurrencyId = cursor.getString(cursor.getColumnIndex("start_money_currency_id"));
                String limitCurrencyId = cursor.getString(cursor.getColumnIndex("limit_currency_id"));
                if (startMoneyCurrencyId != null) {
                    for (Currency currency:currencies) {
                        if (currency.getId().matches(startMoneyCurrencyId)) {
                            newAccount.setStartMoneyCurrency(currency);
                            break;
                        }
                    }
                }
//                if (limitCurrencyId != null) {
//                    for (Currency currency:currencies) {
//                        if (currency.getId().matches(limitCurrencyId)) {
//                            newAccount.setLimitCurrency(currency);
//                            break;
//                        }
//                    }
//                }
//                newAccount.setLimited(cursor.getInt(cursor.getColumnIndex("is_limited")) != 0);
//                newAccount.setLimitSum(cursor.getDouble(cursor.getColumnIndex("limit_amount")));
                accounts.add(newAccount);
                cursor.moveToNext();
            }

            //loading records
            ArrayList<FinanceRecord> financeRecords = new ArrayList<FinanceRecord>();
            cursor = old.query("daily_record_table", null, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                FinanceRecord newRecord = new FinanceRecord();
                Calendar cal = Calendar.getInstance();
                String date = cursor.getString(cursor.getColumnIndex("date"));
                try {
                    cal.setTime(dateFormat.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                newRecord.setDate(cal);
                for (int i=0; i<categories.size(); i++) {
                    if (cursor.getString(cursor.getColumnIndex("category_id")).equals(categories.get(i).getId())) {
                        newRecord.setCategory(categories.get(i));
                        if (cursor.getString(cursor.getColumnIndex("subcategory_id")).matches(context.getResources().getString(R.string.no_category))) {
                            newRecord.setSubCategory(null);
                            break;
                        }
                        for (int j=0; j<categories.get(i).getSubCategories().size(); j++) {
                            if (cursor.getString(cursor.getColumnIndex("subcategory_id")).matches(categories.get(i).getSubCategories().get(j).getId()))
                                newRecord.setSubCategory(categories.get(i).getSubCategories().get(j));
                        }
                        break;
                    }
                }
                for (int i=0; i<accounts.size(); i++) {
                    if (cursor.getString(cursor.getColumnIndex("account_id")).matches(accounts.get(i).getId()))
                        newRecord.setAccount(accounts.get(i));
                }
                for (int i=0; i<currencies.size(); i++) {
                    if (cursor.getString(cursor.getColumnIndex("currency_id")).matches(currencies.get(i).getId()))
                        newRecord.setCurrency(currencies.get(i));
                }
                newRecord.setRecordId(cursor.getString(cursor.getColumnIndex("record_id")));
                newRecord.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
                newRecord.setComment(cursor.getString(cursor.getColumnIndex("empty")));
                List<PhotoDetails> phDet=new ArrayList<>();
                Cursor cursorPhotoTable = old.query("record_photo_table", null, null, null, null, null, null);
                cursorPhotoTable.moveToFirst();
                while (!cursorPhotoTable.isAfterLast()) {
                    if(cursorPhotoTable.getString(cursorPhotoTable.getColumnIndex("record_id")).matches(newRecord.getRecordId())){
                        PhotoDetails temp=new PhotoDetails();
                        temp.setPhotopath(cursorPhotoTable.getString(cursorPhotoTable.getColumnIndex("photopath")));
                        temp.setPhotopathCache(cursorPhotoTable.getString(cursorPhotoTable.getColumnIndex("photopathCache")));
                        temp.setRecordId(cursorPhotoTable.getString(cursorPhotoTable.getColumnIndex("record_id")));
                        phDet.add(temp);
                    }
                    cursorPhotoTable.moveToNext();
                }
                newRecord.setAllTickets(phDet);
                financeRecords.add(newRecord);
                cursor.moveToNext();
            }

            //loading debt borrows
            ArrayList<DebtBorrow> debtBorrows = new ArrayList();
            Cursor dbCursor = old.query("debt_borrow_table", null, null, null, null, null, null);
            Cursor reckCursor = old.query("debtborrow_recking_table", null, null, null, null, null, null);
            dbCursor.moveToFirst();
            while (!dbCursor.isAfterLast()) {
                Log.d(PocketAccounterGeneral.TAG, "regeneration-db");
                DebtBorrow newDebtBorrow = new DebtBorrow();
                Person newPerson = new Person();
                newPerson.setName(dbCursor.getString(dbCursor.getColumnIndex("person_name")));
                newPerson.setPhoneNumber(dbCursor.getString(dbCursor.getColumnIndex("person_number")));
                newPerson.setPhoto(dbCursor.getString(dbCursor.getColumnIndex("photo_id")));
                newDebtBorrow.setPerson(newPerson);
                try {
                    Calendar takenCalendar = Calendar.getInstance();
                    Calendar returnCalendar = Calendar.getInstance();
                    takenCalendar.setTime(dateFormat.parse(dbCursor.getString(dbCursor.getColumnIndex("taken_date"))));
                    if (dbCursor.getString(dbCursor.getColumnIndex("return_date")).matches(""))
                        returnCalendar = null;
                    else
                        returnCalendar.setTime(dateFormat.parse(dbCursor.getString(dbCursor.getColumnIndex("return_date"))));
                    newDebtBorrow.setTakenDate(takenCalendar);
                    newDebtBorrow.setReturnDate(returnCalendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountId = dbCursor.getString(dbCursor.getColumnIndex("account_id"));
                String currencyId = dbCursor.getString(dbCursor.getColumnIndex("currency_id"));
                for (int i=0; i<accounts.size(); i++) {
                    if (accounts.get(i).getId().matches(accountId)) {
                        newDebtBorrow.setAccount(accounts.get(i));
                        break;
                    }
                }
                newDebtBorrow.setCalculate(dbCursor.getInt(dbCursor.getColumnIndex("calculate")) == 0 ? false : true);
                for (Currency cr : currencies) {
                    if (cr.getId().equals(currencyId)) {
                        newDebtBorrow.setCurrency(cr);
                        break;
                    }
                }
                newDebtBorrow.setAmount(dbCursor.getDouble(dbCursor.getColumnIndex("amount")));
                newDebtBorrow.setType(dbCursor.getInt(dbCursor.getColumnIndex("type")));
                newDebtBorrow.setTo_archive(dbCursor.getInt(dbCursor.getColumnIndex("to_archive")) == 0 ? false : true);
                String id = dbCursor.getString(dbCursor.getColumnIndex("id"));
                newDebtBorrow.setId(id);
                reckCursor.moveToFirst();
                ArrayList<Recking> list = new ArrayList<Recking>();
                while (!reckCursor.isAfterLast()) {
                    if (id.matches(reckCursor.getString(reckCursor.getColumnIndex("id")))) {
                        list.add(new Recking(reckCursor.getString(reckCursor.getColumnIndex("pay_date")),
                                reckCursor.getDouble(reckCursor.getColumnIndex("amount")), id,
                                reckCursor.getString(reckCursor.getColumnIndex("account_id")),
                                reckCursor.getString(reckCursor.getColumnIndex("comment"))
                        ));
                    }
                    reckCursor.moveToNext();
                }
                newDebtBorrow.setInfo(dbCursor.getString(dbCursor.getColumnIndex("empty")));
                newDebtBorrow.setReckings(list);
                debtBorrows.add(newDebtBorrow);
                dbCursor.moveToNext();
            }

            //loading credits
            ArrayList<CreditDetials> creditDetialses = new ArrayList<>();
            Cursor curCreditTable = old.query("credit_table", null, null, null, null, null, null);
            Cursor curCreditRecking = old.query("credit_recking_table", null, null, null, null, null, null);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            curCreditTable.moveToFirst();
            while (!curCreditTable.isAfterLast()) {
                CreditDetials credit = new CreditDetials();
                credit.setCredit_name(curCreditTable.getString(curCreditTable.getColumnIndex("credit_name")));
                credit.setIcon_ID(curCreditTable.getInt(curCreditTable.getColumnIndex("icon_id")));
                try {
                    Calendar takenDate = Calendar.getInstance();
                    takenDate.setTime(format.parse(curCreditTable.getString(curCreditTable.getColumnIndex("taken_date"))));
                    credit.setTake_time(takenDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                credit.setProcent(curCreditTable.getDouble(curCreditTable.getColumnIndex("percent")));
                credit.setProcent_interval(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("percent_interval"))));
                credit.setPeriod_time(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("period_time"))));
                credit.setMyCredit_id(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("credit_id"))));
                credit.setValue_of_credit(curCreditTable.getDouble(curCreditTable.getColumnIndex("credit_value")));
                credit.setValue_of_credit_with_procent(curCreditTable.getDouble(curCreditTable.getColumnIndex("credit_value_with_percent")));
                credit.setPeriod_time_tip(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("period_time_tip"))));
                credit.setKey_for_include(curCreditTable.getInt(curCreditTable.getColumnIndex("key_for_include"))!=0);
                credit.setKey_for_archive(curCreditTable.getInt(curCreditTable.getColumnIndex("key_for_archive"))!=0);
                String currencyId = curCreditTable.getString(curCreditTable.getColumnIndex("currency_id"));
                Currency currency = null;
                for (int i = 0; i<currencies.size(); i++)  {
                    if (currencyId.matches(currencies.get(i).getId())) {
                        currency = currencies.get(i);
                        break;
                    }
                }
                credit.setValyute_currency(currency);
                List<ReckingCredit> reckings = new ArrayList<ReckingCredit>();
                curCreditRecking.moveToFirst();
                while(!curCreditRecking.isAfterLast()) {
                    if (Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("credit_id"))) == Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("credit_id")))) {
                        double amount = curCreditRecking.getDouble(curCreditRecking.getColumnIndex("amount"));
                        long payDate = Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("pay_date")));
                        String comment = curCreditRecking.getString(curCreditRecking.getColumnIndex("comment"));
                        String accountId = curCreditRecking.getString(curCreditRecking.getColumnIndex("account_id"));
                        long creditId = Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("credit_id")));
                        ReckingCredit newReckingCredit = new ReckingCredit(payDate, amount, accountId, creditId, comment);
                        reckings.add(newReckingCredit);
                    }
                    curCreditRecking.moveToNext();
                }
                credit.setReckings(reckings);
                credit.setInfo(curCreditTable.getString(curCreditTable.getColumnIndex("empty")));
                creditDetialses.add(credit);
                curCreditTable.moveToNext();
            }

            //loading smsparseobjects
            ArrayList<SmsParseObject> smsParseObjects = new ArrayList<SmsParseObject>();
            cursor = old.query("sms_parsing_table", null, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SmsParseObject object = new SmsParseObject();
                object.setNumber(cursor.getString(cursor.getColumnIndex("number")));
                object.setIncomeWords(cursor.getString(cursor.getColumnIndex("income_words")));
                object.setExpenseWords(cursor.getString(cursor.getColumnIndex("expense_words")));
                object.setAmountWords(cursor.getString(cursor.getColumnIndex("amount_words")));
                String accountId = cursor.getString(cursor.getColumnIndex("account_id"));
                for (int i=0; i<accounts.size(); i++) {
                    if (accountId.matches(accounts.get(i).getId())) {
                        object.setAccount(accounts.get(i));
                        break;
                    }
                }
                String currencyId = cursor.getString(cursor.getColumnIndex("currency_id"));
                for (int i=0; i<currencies.size(); i++) {
                    if (currencyId.matches(currencies.get(i).getId())) {
                        object.setCurrency(currencies.get(i));
                        break;
                    }
                }
                object.setType(cursor.getInt(cursor.getColumnIndex("type")));
                smsParseObjects.add(object);
                cursor.moveToNext();
            }
            daoSession.getRootCategoryDao().saveInTx(categories);
            daoSession.getCurrencyDao().saveInTx(currencies);
            daoSession.getAccountDao().saveInTx(accounts);
            for (int i=0; i<expenses.size(); i++) {
                BoardButton boardButton = new BoardButton();
                boardButton.setType(PocketAccounterGeneral.EXPENSE);
                boardButton.setPos(i);
                if (expenses.get(i) == null) {
                    boardButton.setCategoryId(null);
                } else {
                    boardButton.setCategoryId(expenses.get(i).getId());
                }
                daoSession.getBoardButtonDao().save(boardButton);
            }
            for (int i=0; i<incomes.size(); i++) {
                BoardButton boardButton = new BoardButton();
                boardButton.setType(PocketAccounterGeneral.INCOME);
                boardButton.setPos(i);
                if (expenses.get(i) == null) {
                    boardButton.setCategoryId(null);
                } else {
                    boardButton.setCategoryId(incomes.get(i).getId());
                }
                daoSession.getBoardButtonDao().save(boardButton);
            }
            daoSession.getFinanceRecordDao().saveInTx(financeRecords);
            daoSession.getDebtBorrowDao().saveInTx(debtBorrows);
            daoSession.getCreditDetialsDao().saveInTx(creditDetialses);
            for (Currency currency : currencies)
                    Log.d("sss", currency.getName() + " " + currency.getAbbr());
            //delete file
            if (oldDBFile.delete())
                Log.d(PocketAccounterGeneral.TAG, oldDBFile.getName() + " is deleted successfully !!!");
            else
                Log.d(PocketAccounterGeneral.TAG, "Can't delete file: " + oldDBFile.getName() + ". Please try again...");
        } else {
            if (preferences.getBoolean(PocketAccounterGeneral.DB_ONCREATE_ENTER, true)) {
                preferences.edit().putBoolean(PocketAccounterGeneral.DB_ONCREATE_ENTER, false).commit();
                //inserting currencies
                String [] currencyNames = context.getResources().getStringArray(R.array.base_currencies);
                String [] currencyIds = context.getResources().getStringArray(R.array.currency_ids);
                String [] currencyCosts = context.getResources().getStringArray(R.array.currency_costs);
                String [] currencySigns = context.getResources().getStringArray(R.array.base_abbrs);

                for (int i=0; i<3; i++) {
                    Currency currency = new Currency();
                    currency.setName(currencyNames[i]);
                    currency.setId(currencyIds[i]);
                    currency.setMain(i == 0);
                    currency.setAbbr(currencySigns[i]);
                    CurrencyCost currencyCost = new CurrencyCost();
                    currencyCost.setCurrencyId(currencyIds[i]);
                    currencyCost.setDay(Calendar.getInstance());
                    currencyCost.setCost(Double.parseDouble(currencyCosts[i]));
                    daoSession.getCurrencyCostDao().insert(currencyCost);
                    List<CurrencyCost> costs = new ArrayList<>();
                    costs.add(currencyCost);
                    currency.setCosts(costs);
                    daoSession.getCurrencyDao().insert(currency);
                }

                //inserting accounts
                String[] accountNames = context.getResources().getStringArray(R.array.account_names);
                String[] accountIds = context.getResources().getStringArray(R.array.account_ids);
                String[] accountIcons = context.getResources().getStringArray(R.array.account_icons);
                int[] icons = new int[accountIcons.length];
                for (int i=0; i<accountIcons.length; i++) {
                    int resId = context.getResources().getIdentifier(accountIcons[i], "drawable", context.getPackageName());
                    icons[i] = resId;
                }
                for (int i=0; i<accountNames.length; i++) {
                    Account account = new Account();
                    account.setName(accountNames[i]);
                    account.setIcon(accountIcons[i]);
                    account.setId(accountIds[i]);
                    account.setStartMoneyCurrency(daoSession.getCurrencyDao().loadAll().get(0));
                    account.setAmount(0.0d);
                    account.setNoneMinusAccount(false);
                    account.setCalendar(Calendar.getInstance());
                    daoSession.getAccountDao().insert(account);
                }

                //inserting categories
                String[] catValues = context.getResources().getStringArray(R.array.cat_values);
                String[] catTypes = context.getResources().getStringArray(R.array.cat_types);
                String[] catIcons = context.getResources().getStringArray(R.array.cat_icons);
                for (int i=0; i<catValues.length; i++) {
                    RootCategory rootCategory = new RootCategory();
                    int resId = context.getResources().getIdentifier(catValues[i], "string", context.getPackageName());
                    rootCategory.setName(context.getResources().getString(resId));
                    rootCategory.setId(catValues[i]);
                    rootCategory.setType(Integer.parseInt(catTypes[i]));
                    rootCategory.setIcon(catIcons[i]);
                    int arrayId = context.getResources().getIdentifier(catValues[i], "array", context.getPackageName());
                    if (arrayId != 0) {
                        int subcatIconArrayId = context.getResources().getIdentifier(catValues[i]+"_icons", "array", context.getPackageName());
                        String[] subCats = context.getResources().getStringArray(arrayId);
                        String[] tempIcons = context.getResources().getStringArray(subcatIconArrayId);
                        List<SubCategory> subCategories = new ArrayList<>();
                        for (int j=0; j<subCats.length; j++) {
                            SubCategory subCategory = new SubCategory();
                            subCategory.setName(subCats[j]);
                            subCategory.setId(UUID.randomUUID().toString());
                            subCategory.setParentId(catValues[i]);
                            subCategory.setIcon(tempIcons[j]);
                            subCategories.add(subCategory);
                            daoSession.getSubCategoryDao().insert(subCategory);
                        }
                        rootCategory.setSubCategories(subCategories);
                    }
                    daoSession.getRootCategoryDao().insert(rootCategory);
                }

                //inserting expenses and incomes
                int incomes = 0, expenses = 0;
                List<RootCategory> categories = daoSession.getRootCategoryDao().loadAll();
                Log.d("sss", "exp list and inc list "+categories.size());
                for (int i=0; i<categories.size() && incomes<PocketAccounterGeneral.INCOME_BUTTONS_COUNT; i++) {
                    if (categories.get(i).getType() == PocketAccounterGeneral.INCOME) {
                        BoardButton boardButton = new BoardButton();
                        boardButton.setCategoryId(categories.get(i).getId());
                        boardButton.setPos(incomes);
                        boardButton.setType(PocketAccounterGeneral.INCOME);
                        daoSession.getBoardButtonDao().insert(boardButton);
                        incomes++;
                    }
                }
                for (int i=0; i<categories.size() && expenses<PocketAccounterGeneral.EXPANCE_BUTTONS_COUNT; i++) {
                    if (categories.get(i).getType() == PocketAccounterGeneral.EXPENSE) {
                        BoardButton boardButton = new BoardButton();
                        boardButton.setCategoryId(categories.get(i).getId());
                        boardButton.setPos(expenses);
                        boardButton.setType(PocketAccounterGeneral.EXPENSE);
                        daoSession.getBoardButtonDao().insert(boardButton);
                        expenses++;
                    }
                }
            }
        }
    }
}
