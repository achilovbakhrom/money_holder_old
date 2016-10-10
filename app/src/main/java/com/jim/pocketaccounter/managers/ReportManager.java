package com.jim.pocketaccounter.managers;

import android.content.Context;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.AccountOperationDao;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.database.ReckingCreditDao;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SmsParseSuccess;
import com.jim.pocketaccounter.database.SmsParseSuccessDao;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.report.CategoryDataRow;
import com.jim.pocketaccounter.report.ReportObject;
import com.jim.pocketaccounter.report.SubCategoryWitAmount;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by DEV on 01.09.2016.
 */

public class ReportManager {
    @Inject DaoSession daoSession;
    @Inject CommonOperations commonOperations;
    @Named(value = "begin") @Inject Calendar begin;
    @Named(value = "end") @Inject Calendar end;
    private Context context;
    private AccountOperationDao accountOperationsDao;
    private FinanceRecordDao financeRecordDao;
    private DebtBorrowDao debtBorrowDao;
    private CreditDetialsDao creditDetialsDao;
    private AccountDao accountDao;
    private SmsParseSuccessDao smsParseSuccessDao;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private List<ReportObject> incomes;
    private List<ReportObject> expances;

    public ReportManager(Context context) {
        ((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
        this.context = context;
        accountOperationsDao = daoSession.getAccountOperationDao();
        financeRecordDao = daoSession.getFinanceRecordDao();
        debtBorrowDao = daoSession.getDebtBorrowDao();
        creditDetialsDao = daoSession.getCreditDetialsDao();
        accountDao = daoSession.getAccountDao();
        smsParseSuccessDao = daoSession.getSmsParseSuccessDao();
    }

    public List<ReportObject> getReportObjects(boolean toMainCurrency, Calendar begin, Calendar end, Class ...classes) {
        List<ReportObject> result = new ArrayList<>();
        for (Class cl : classes) {
            if (cl.getName().equals(Account.class.getName())) {
                List<Account> accounts = accountDao.loadAll();
                for (Account account : accounts) {
                    if (account.getAmount() != 0 &&
                            account.getCalendar().compareTo(begin) >= 0 &&
                            account.getCalendar().compareTo(end) <= 0) {
                        ReportObject reportObject = new ReportObject();
                        reportObject.setType(PocketAccounterGeneral.INCOME);
                        reportObject.setAccount(account);
                        reportObject.setDate((Calendar) account.getCalendar().clone());
                        if (toMainCurrency) {
                            reportObject.setCurrency(commonOperations.getMainCurrency());
                            reportObject.setAmount(commonOperations.getCost(account.getCalendar(),
                                    account.getStartMoneyCurrency(), account.getAmount()));
                        } else {
                            reportObject.setCurrency(account.getStartMoneyCurrency());
                            reportObject.setAmount(account.getAmount());
                        }
                        reportObject.setDescription(context.getResources().getString(R.string.start_amount));
                        result.add(reportObject);
                    }
                }
            }
            if (cl.getName().equals(AccountOperation.class.getName())) {
                for (AccountOperation accountOperations : accountOperationsDao.loadAll()) {
                    if (accountOperations.getDate().compareTo(begin) >= 0 &&
                            accountOperations.getDate().compareTo(end) <= 0) {
                        List<Account> accountList = daoSession.getAccountDao().queryBuilder()
                                .where(AccountDao.Properties.Id.eq(accountOperations.getSourceId()))
                                .list();
                        if (!accountList.isEmpty()) {
                            ReportObject reportObject = new ReportObject();
                            reportObject.setType(PocketAccounterGeneral.EXPENSE);
                            reportObject.setAccount(accountList.get(0));
                            reportObject.setCurrency(accountOperations.getCurrency());
                            if (toMainCurrency)
                                reportObject.setAmount(commonOperations.getCost(accountOperations.getDate(), accountOperations.getCurrency(), accountOperations.getAmount()));
                            else
                                reportObject.setAmount(accountOperations.getAmount());
                            reportObject.setDate(accountOperations.getDate());
                            reportObject.setDescription(context.getResources().getString(R.string.transfer));
                            result.add(reportObject);
                        }
                        accountList = daoSession.getAccountDao().queryBuilder()
                                .where(AccountDao.Properties.Id.eq(accountOperations.getTargetId()))
                                .list();
                        if (!accountList.isEmpty()) {
                            ReportObject reportObject = new ReportObject();
                            reportObject.setType(PocketAccounterGeneral.INCOME);
                            reportObject.setAccount(accountList.get(0));
                            reportObject.setCurrency(accountOperations.getCurrency());
                            if (toMainCurrency)
                                reportObject.setAmount(commonOperations.getCost(accountOperations.getDate(), accountOperations.getCurrency(), accountOperations.getAmount()));
                            else
                                reportObject.setAmount(accountOperations.getAmount());
                            reportObject.setDate(accountOperations.getDate());
                            reportObject.setDescription(context.getResources().getString(R.string.transfer));
                            result.add(reportObject);
                        }
                    }
                }
            }
            if (cl.getName().equals(SmsParseSuccess.class.getName())) {
                List<SmsParseSuccess> list = smsParseSuccessDao
                        .queryBuilder()
                        .where(SmsParseSuccessDao.Properties.IsSuccess.eq(true))
                        .list();
                for (SmsParseSuccess smsParseSuccess : list) {
                    if (smsParseSuccess.getDate().compareTo(begin) >= 0 &&
                            smsParseSuccess.getDate().compareTo(end) <= 0) {
                        ReportObject reportObject = new ReportObject();
                        reportObject.setType(smsParseSuccess.getType());
                        reportObject.setAccount(smsParseSuccess.getAccount());
                        reportObject.setDate((Calendar) smsParseSuccess.getDate().clone());
                        if (toMainCurrency) {
                            reportObject.setCurrency(commonOperations.getMainCurrency());
                            reportObject.setAmount(commonOperations.getCost(smsParseSuccess.getDate(), smsParseSuccess.getCurrency(), smsParseSuccess.getAmount()));
                        }
                        else {
                            reportObject.setCurrency(smsParseSuccess.getCurrency());
                            reportObject.setAmount(smsParseSuccess.getAmount());
                        }
                        reportObject.setDescription(smsParseSuccess.getNumber());
                        result.add(reportObject);
                    }
                }
            }
            if (cl.getName().equals(FinanceRecord.class.getName())) {
                List<FinanceRecord> financeRecordList = financeRecordDao.loadAll();
                for (FinanceRecord financeRecord : financeRecordList) {
                    if (financeRecord.getDate().compareTo(begin) >= 0 &&
                            financeRecord.getDate().compareTo(end) <= 0) {
                        ReportObject reportObject = new ReportObject();
                        reportObject.setType(financeRecord.getCategory().getType());
                        reportObject.setAccount(financeRecord.getAccount());
                        reportObject.setDate((Calendar) financeRecord.getDate().clone());
                        if (toMainCurrency) {
                            reportObject.setCurrency(commonOperations.getMainCurrency());
                            reportObject.setAmount(commonOperations.getCost(financeRecord.getDate(), financeRecord.getCurrency(), financeRecord.getAmount()));
                        }
                        else {
                            reportObject.setCurrency(financeRecord.getCurrency());
                            reportObject.setAmount(financeRecord.getAmount());
                        }
                        reportObject.setDescription(financeRecord.getCategory().getName());
                        result.add(reportObject);
                    }
                }
            }
            if (cl.getName().equals(DebtBorrow.class.getName())) {
                List<DebtBorrow> debtBorrowList = debtBorrowDao.loadAll();
                for (DebtBorrow debtBorrow : debtBorrowList) {
                    if (!debtBorrow.getCalculate()) continue;
                    if (debtBorrow.getTakenDate().compareTo(begin) >= 0 &&
                            debtBorrow.getTakenDate().compareTo(end) <= 0) {
                        ReportObject reportObject = new ReportObject();
                        if (debtBorrow.getType() == DebtBorrow.BORROW) {
                            reportObject.setDescription(context.getResources().getString(R.string.borrow_statistics));
                            reportObject.setType(PocketAccounterGeneral.EXPENSE);
                        }
                        else {
                            reportObject.setDescription(context.getResources().getString(R.string.debt_statistics));
                            reportObject.setType(PocketAccounterGeneral.INCOME);
                        }
                        reportObject.setDate((Calendar) debtBorrow.getTakenDate().clone());
                        reportObject.setAccount(debtBorrow.getAccount());
                        if (toMainCurrency) {
                            reportObject.setAmount(commonOperations.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount()));
                            reportObject.setCurrency(commonOperations.getMainCurrency());
                        }
                        else {
                            reportObject.setCurrency(debtBorrow.getCurrency());
                            reportObject.setAmount(debtBorrow.getAmount());
                        }
                        result.add(reportObject);
                    }
                    for (Recking recking : debtBorrow.getReckings()) {
                        Calendar calendar = recking.getPayDate();

                        if (calendar.compareTo(begin) >= 0 && calendar.compareTo(end) <= 0) {
                            ReportObject reportObject = new ReportObject();
                            reportObject.setDate(calendar);
                            if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                reportObject.setDescription(context.getResources().getString(R.string.borrow_recking_statistics));
                                reportObject.setType(PocketAccounterGeneral.INCOME);
                            }
                            else {
                                reportObject.setDescription(context.getResources().getString(R.string.debt_recking_statistics));
                                reportObject.setType(PocketAccounterGeneral.EXPENSE);
                            }
                            Account account = null;
                            List<Account> accountList = accountDao.loadAll();
                            for (Account acc : accountList) {
                                if (acc.getId().equals(recking.getAccountId())) {
                                    account = acc;
                                    break;
                                }
                            }
                            if (account == null)
                                throw new RuntimeException("Account not found in class: " + getClass().getName() + ". Method: getRecordObjects();");
                            reportObject.setAccount(account);
                            if (toMainCurrency) {
                                reportObject.setAmount(commonOperations.getCost(calendar,
                                        debtBorrow.getCurrency(), recking.getAmount()));
                                reportObject.setCurrency(commonOperations.getMainCurrency());
                            }
                            else {
                                reportObject.setAmount(recking.getAmount());
                                reportObject.setCurrency(debtBorrow.getCurrency());
                            }
                            result.add(reportObject);
                        }
                    }
                }
            }
            if (cl.getName().equals(CreditDetials.class.getName())) {
                List<CreditDetials> creditDetialsList = creditDetialsDao.loadAll();
                for (CreditDetials creditDetials : creditDetialsList) {
                    if (!creditDetials.getKey_for_include()) continue;
                    for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
                        Calendar calendar = reckingCredit.getPayDate();
                        if (calendar.compareTo(begin) >= 0 && calendar.compareTo(end) <= 0) {
                            ReportObject reportObject = new ReportObject();
                            reportObject.setType(PocketAccounterGeneral.EXPENSE);
                            reportObject.setDate(calendar);
                            reportObject.setDescription(context.getResources().getString(R.string.credit));
                            Account account = null;
                            List<Account> accountList = accountDao.loadAll();
                            for (Account acc : accountList) {
                                if (acc.getId().equals(reckingCredit.getAccountId())) {
                                    account = acc;
                                    break;
                                }
                            }
                            if (account == null)
                                throw new RuntimeException("Account not found in class: " +
                                        getClass().getName() +
                                        ". Method: getRecordObjects();");
                            reportObject.setAccount(account);
                            if (toMainCurrency) {
                                reportObject.setCurrency(commonOperations.getMainCurrency());
                                reportObject.setAmount(commonOperations.getCost(calendar,
                                        creditDetials.getValyute_currency(), reckingCredit.getAmount()));
                            }
                            else {
                                reportObject.setCurrency(creditDetials.getValyute_currency());
                                reportObject.setAmount(reckingCredit.getAmount());
                            }
                            result.add(reportObject);
                        }
                    }
                }
            }
        }
        return result;
    }

    public Map<String, Double> calculateBalance(Calendar begin, Calendar end) {
        Map<String, Double> result = new HashMap<>();
        List<ReportObject> list = getReportObjects(true, begin, end, Account.class,
                                                                    FinanceRecord.class,
                                                                    DebtBorrow.class,
                                                                    CreditDetials.class,
                                                                    SmsParseSuccess.class);
        Double incomes = 0.0d, expenses = 0.0d, balance = 0.0d;
        for (ReportObject reportObject : list) {
            if (reportObject.getType() == PocketAccounterGeneral.INCOME)
                incomes += reportObject.getAmount();
            else
                expenses += reportObject.getAmount();
        }
        balance = incomes - expenses;
        result.put(PocketAccounterGeneral.INCOMES, incomes);
        result.put(PocketAccounterGeneral.EXPENSES, expenses);
        result.put(PocketAccounterGeneral.BALANCE, balance);
        return result;
    }

    public ArrayList<CategoryDataRow> getReportByCategories(Calendar begin, Calendar end) {
        ArrayList<CategoryDataRow> result  = new ArrayList<>();
        //income expanses begin
        List<FinanceRecord> financeRecords = daoSession.getFinanceRecordDao().loadAll();
        for (FinanceRecord financeRecord : financeRecords) {
            boolean categoryFound = false;
            CategoryDataRow foundCategory = null;
            for (CategoryDataRow categoryDataRow : result) {
                if (categoryDataRow.getCategory().getId().equals(financeRecord.getCategoryId())) {
                    categoryFound = true;
                    foundCategory = categoryDataRow;
                    break;
                }
            }
            if (categoryFound) {
                if (financeRecord.getSubCategory() == null) {
                    boolean nullSubcatFound = false;
                    int nullSubcatPosition = 0;
                    for (int j = 0; j < foundCategory.getSubCats().size(); j++) {
                        if (foundCategory.getSubCats()
                                        .get(j)
                                        .getSubCategory()
                                        .getId()
                                        .equals(context.getResources().getString(R.string.no_category))) {
                            nullSubcatPosition = j;
                            nullSubcatFound = true;
                            break;
                        }
                    }
                    if (nullSubcatFound)
                        foundCategory.getSubCats()
                                .get(nullSubcatPosition)
                                .setAmount(foundCategory.getSubCats().get(nullSubcatPosition).getAmount()+commonOperations.getCost(financeRecord));
                    else {
                        SubCategoryWitAmount newSubCategoryWithAmount = new SubCategoryWitAmount();
                        SubCategory noSubCategory = new SubCategory();
                        noSubCategory.setId(context.getResources().getString(R.string.no_category));
                        newSubCategoryWithAmount.setSubCategory(noSubCategory);
                        newSubCategoryWithAmount.setAmount(commonOperations.getCost(financeRecord));
                        foundCategory.getSubCats().add(newSubCategoryWithAmount);
                    }
                }
                else {
                    boolean subcatFound = false;
                    int foundSubcatPosition = 0;
                    for (int j=0; j<foundCategory.getSubCats().size(); j++) {
                        if (foundCategory.getSubCats().get(j).getSubCategory().getId().equals(financeRecord.getSubCategory().getId())) {
                            subcatFound = true;
                            foundSubcatPosition = j;
                            break;
                        }
                    }
                    if (subcatFound) {
                        foundCategory.getSubCats()
                                .get(foundSubcatPosition)
                                .setAmount(foundCategory.getSubCats().get(foundSubcatPosition).getAmount()+commonOperations.getCost(financeRecord));
                    }
                    else {
                        SubCategoryWitAmount newSubCategoryWithAmount = new SubCategoryWitAmount();
                        newSubCategoryWithAmount.setSubCategory(financeRecord.getSubCategory());
                        newSubCategoryWithAmount.setAmount(commonOperations.getCost(financeRecord));
                        foundCategory.getSubCats().add(newSubCategoryWithAmount);
                    }
                }
                double amount = 0.0;
                for (int j=0; j<foundCategory.getSubCats().size(); j++)
                    amount = amount + foundCategory.getSubCats().get(j).getAmount();
                foundCategory.setTotalAmount(amount);
            }
            else {
                CategoryDataRow newCategoryDataRow = new CategoryDataRow();
                newCategoryDataRow.setCategory(financeRecord.getCategory());
                newCategoryDataRow.setTotalAmount(commonOperations.getCost(financeRecord));
                SubCategoryWitAmount newSubCategoryWithAmount = new SubCategoryWitAmount();
                if (financeRecord.getSubCategory() == null) {
                    SubCategory noSubCategory = new SubCategory();
                    noSubCategory.setId(context.getResources().getString(R.string.no_category));
                    newSubCategoryWithAmount.setSubCategory(noSubCategory);
                    newSubCategoryWithAmount.setAmount(commonOperations.getCost(financeRecord));
                }
                else {
                    newSubCategoryWithAmount.setSubCategory(financeRecord.getSubCategory());
                    newSubCategoryWithAmount.setAmount(commonOperations.getCost(financeRecord));
                }
                newCategoryDataRow.getSubCats().add(newSubCategoryWithAmount);
                newCategoryDataRow.setTotalAmount(commonOperations.getCost(financeRecord));
                result.add(newCategoryDataRow);
            }
        }
        //end income expanses

        //credit begin
        double creditTotalPaid = 0.0;

        List<CreditDetials> temp = daoSession.getCreditDetialsDao()
                                            .queryBuilder()
                                            .where(CreditDetialsDao.Properties.Key_for_include.eq(true))
                                            .list();
        List<CreditDetials> credits = new ArrayList<>();
        for (CreditDetials creditDetials : temp) {
            for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
                if (reckingCredit.getPayDate().compareTo(begin)>=0 && reckingCredit.getPayDate().compareTo(end)<=0)
                    credits.add(creditDetials);
            }
        }

        for (int i=0; i<credits.size(); i++) {
            for (int j=0; j<credits.get(i).getReckings().size(); j++) {
                if (credits.get(i).getReckings().get(j).getPayDate().compareTo(begin)>=0 && credits.get(i).getReckings().get(j).getPayDate().compareTo(end)<=0)
                    creditTotalPaid = creditTotalPaid
                            + commonOperations.getCost(credits.get(i).getReckings().get(j).getPayDate(),
                            credits.get(i).getValyute_currency(), credits.get(i).getReckings().get(j).getAmount());
            }
            if (creditTotalPaid != 0) {
                CategoryDataRow creditDataRow = new CategoryDataRow();
                RootCategory creditCategory = new RootCategory();
                creditCategory.setType(PocketAccounterGeneral.EXPENSE);
                creditCategory.setName(credits.get(i).getCredit_name());
                creditDataRow.setCategory(creditCategory);
                creditDataRow.setTotalAmount(creditTotalPaid);
                result.add(creditDataRow);
            }
            creditTotalPaid = 0.0;
        }
        //credit end

        //debt borrows begin
        List<DebtBorrow> debtBorrows = daoSession
                .getDebtBorrowDao()
                .queryBuilder()
                .where(DebtBorrowDao.Properties.Calculate.eq(true), DebtBorrowDao.Properties.TakenDate.eq(dateFormat.format(begin.getTime())))
                .list();
        for (int i=0; i<debtBorrows.size(); i++) {
            RootCategory category = new RootCategory();
            if (debtBorrows.get(i).getType() == DebtBorrow.BORROW) {
                category.setType(PocketAccounterGeneral.EXPENSE);
                category.setName(context.getResources().getString(R.string.borrow_statistics));
            } else {
                category.setType(PocketAccounterGeneral.INCOME);
                category.setName(context.getResources().getString(R.string.debt_statistics));
            }
            CategoryDataRow categoryDataRow = new CategoryDataRow();
            categoryDataRow.setTotalAmount(commonOperations.getCost(debtBorrows.get(i).getTakenDate(), debtBorrows.get(i).getCurrency(), debtBorrows.get(i).getAmount()));
            categoryDataRow.setCategory(category);
            result.add(categoryDataRow);
        }
        debtBorrows.clear();
        List<DebtBorrow> temporary = daoSession
                .getDebtBorrowDao()
                .queryBuilder()
                .where(DebtBorrowDao.Properties.Calculate.eq(true))
                .list();
        for (int i=0; i<temporary.size(); i++) {
            for (int j=0; j<temporary.get(i).getReckings().size(); j++) {
                if (begin.compareTo(temporary.get(i).getReckings().get(j).getPayDate())<=0
                        && end.compareTo(temporary.get(i).getReckings().get(j).getPayDate())>=0) {
                    debtBorrows.add(temporary.get(i));
                    break;
                }
            }
        }
        for (int i=0; i<debtBorrows.size(); i++) {
            RootCategory category = new RootCategory();
            double totalAmount = 0.0;
            for (int j=0; j<debtBorrows.get(i).getReckings().size(); j++) {
                if (begin.compareTo(debtBorrows.get(i).getReckings().get(j).getPayDate())<=0
                        && end.compareTo(debtBorrows.get(i).getReckings().get(j).getPayDate()) >= 0) {
                    totalAmount = totalAmount + commonOperations.getCost(debtBorrows.get(i).getReckings().get(j).getPayDate(),
                            debtBorrows.get(i).getCurrency(),
                            debtBorrows.get(i).getReckings().get(j).getAmount());
                }
            }
            if (debtBorrows.get(i).getType() == DebtBorrow.BORROW) {
                category.setName(context.getResources().getString(R.string.borrow_recking_statistics));
                category.setType(PocketAccounterGeneral.INCOME);
            }
            else {
                category.setName(context.getResources().getString(R.string.debt_recking_statistics));
                category.setType(PocketAccounterGeneral.EXPENSE);
            }
            CategoryDataRow categoryDataRow = new CategoryDataRow();
            categoryDataRow.setCategory(category);
            categoryDataRow.setTotalAmount(totalAmount);
            result.add(categoryDataRow);
        }
        //debt borrows end
        return result;
    }

    public Calendar getFirstDay() {
        end.setTimeInMillis(System.currentTimeMillis());
        for (FinanceRecord financeRecord : financeRecordDao.loadAll()) {
            if (financeRecord.getDate().compareTo(end) <= 0)
                end = financeRecord.getDate();
        }
        for (DebtBorrow debtBorrow : debtBorrowDao.loadAll()) {
            if (debtBorrow.getTakenDate().compareTo(end) <= 0)
                end = debtBorrow.getTakenDate();
        }
        for (CreditDetials creditDetials : creditDetialsDao.loadAll()) {
            if (creditDetials.getTake_time().compareTo(end) <= 0)
                end = creditDetials.getTake_time();
        }
        for (Account account : accountDao.loadAll()) {
            if (account.getCalendar().compareTo(end) <= 0)
                end = account.getCalendar();
        }
        return end;
    }

    public Map<Currency, Double> getRemain(Account account) {
        List<ReportObject> list = getReportObjects(false, account.getCalendar(), Calendar.getInstance(),
                Account.class,
                AccountOperation.class,
                FinanceRecord.class,
                DebtBorrow.class,
                CreditDetials.class);
        Map<Currency, Double> result = new HashMap<>();
        for(ReportObject reportObject : list) {
            if (reportObject.getAccount().getId().equals(account.getId())) {
                Currency temp = null;
                boolean found = false;
                for (Currency currency : result.keySet()) {
                    if (currency.getId().equals(reportObject.getCurrency().getId())) {
                        found = true;
                        temp = currency;
                        break;
                    }
                }
                if (found) {
                    if (reportObject.getType() == PocketAccounterGeneral.INCOME)
                        result.put(temp, result.get(temp).doubleValue() + reportObject.getAmount());
                    else
                        result.put(temp, result.get(temp).doubleValue() - reportObject.getAmount());
                }
                else {
                    temp = reportObject.getCurrency();
                    if (reportObject.getType() == PocketAccounterGeneral.INCOME)
                        result.put(temp, reportObject.getAmount());
                    else
                        result.put(temp, -reportObject.getAmount());
                }
            }
        }
        return  result;
    }

    public List<ReportObject> getAccountOperations(Account account, Calendar begin, Calendar end) {
        List<ReportObject> result = new ArrayList<>();
        List<ReportObject> allObjects = getReportObjects(false, begin, end,
                Account.class,
                AccountOperation.class,
                FinanceRecord.class,
                DebtBorrow.class,
                CreditDetials.class);
        for (ReportObject reportObject : allObjects) {
            if (reportObject.getAccount().getId().equals(account.getId()))
                result.add(reportObject);
        }
        return result;
    }



    public List<FinanceRecord> getCategoryOperations(RootCategory rootCategory, Calendar begin, Calendar end) {
        List<FinanceRecord> result = new ArrayList<>();
        for (FinanceRecord financeRecord : financeRecordDao.loadAll()) {
            if (financeRecord.getCategory().getId().equals(rootCategory.getId()) &&
                    financeRecord.getDate().compareTo(begin) >= 0 && financeRecord.getDate().compareTo(end) <= 0)
                result.add(financeRecord);
        }
        return result;
    }

    public Double getTotalAmountByCategory(RootCategory category, Calendar begin, Calendar end) {
        Double result = 0.0;
        List<FinanceRecord> records = getCategoryOperations(category, begin, end);
        for (FinanceRecord record : records) {
            if (record.getCategory().getType() == PocketAccounterGeneral.INCOME)
                result += commonOperations.getCost(record.getDate(), record.getCurrency(), record.getAmount());
            else
                result -= commonOperations.getCost(record.getDate(), record.getCurrency(), record.getAmount());
        }
        return result;
    }

    public List<AccountOperation> getAccountOpertions (Object object) {
        String id = "";
        if (object.getClass().getName().equals(Purpose.class.getName())) {
            id = ((Purpose) object).getId();
        } else {
            id = ((Account) object).getId();
        }
        return daoSession.getAccountOperationDao().queryBuilder()
                .whereOr(AccountOperationDao.Properties.SourceId.eq(id),
                        AccountOperationDao.Properties.TargetId.eq(id))
                .list();
    }

    private void getIncomeExpanceDates(Calendar begin, Calendar end) {
        incomes = new ArrayList<>();
        expances = new ArrayList<>();
        // Finance Record
        for (FinanceRecord fr : daoSession.getFinanceRecordDao().loadAll()) {
            if (fr.getDate().compareTo(begin) > 0 && fr.getDate().compareTo(end) < 0) {
                ReportObject reportObject = new ReportObject();
                reportObject.setDate(fr.getDate());
                reportObject.setAmount(fr.getAmount());
                reportObject.setCurrency(fr.getCurrency());
                reportObject.setAccount(fr.getAccount());
                reportObject.setDescription(fr.getCategory().getName() + "," + fr.getSubCategory().getName());
                if (fr.getCategory().getType() == PocketAccounterGeneral.INCOME) {
                    reportObject.setType(PocketAccounterGeneral.INCOME);
                    incomes.add(reportObject);
                } else {
                    reportObject.setType(PocketAccounterGeneral.EXPENSE);
                    expances.add(reportObject);
                }
            }
        }

        // Debt Borrows
        for (DebtBorrow db : debtBorrowDao.queryBuilder().list()) {
            if (db.getTakenDate().compareTo(begin) > 0 && db.getTakenDate().compareTo(end) < 0) {
                ReportObject reportObject = new ReportObject();
                reportObject.setAccount(db.getAccount());
                reportObject.setCurrency(db.getCurrency());
                reportObject.setAmount(db.getAmount());
                reportObject.setDate(db.getTakenDate());
                reportObject.setDescription(context.getResources().getString(R.string.borrow_recking_statistics));
                if (db.getType() == PocketAccounterGeneral.INCOME) {
                    reportObject.setType(PocketAccounterGeneral.INCOME);
                    incomes.add(reportObject);
                    for (Recking recking : db.getReckings()) {
                        if (recking.getPayDate().compareTo(begin) > 0 && recking.getPayDate().compareTo(end) < 0) {
                            ReportObject rerObj = new ReportObject();
                            rerObj.setDate(recking.getPayDate());
                            rerObj.setAmount(recking.getAmount());
                            rerObj.setCurrency(db.getCurrency());
                            rerObj.setAccount(accountDao.load(recking.getAccountId()));
                            rerObj.setType(PocketAccounterGeneral.EXPENSE);
                            rerObj.setDescription(context.getResources().getString(R.string.debt_recking_statistics));
                            expances.add(rerObj);
                        }
                    }
                } else {
                    reportObject.setType(PocketAccounterGeneral.EXPENSE);
                    expances.add(reportObject);
                    for (Recking recking : db.getReckings()) {
                        if (recking.getPayDate().compareTo(begin) > 0 && recking.getPayDate().compareTo(end) < 0) {
                            ReportObject rerObj = new ReportObject();
                            rerObj.setDate(recking.getPayDate());
                            rerObj.setAmount(recking.getAmount());
                            rerObj.setCurrency(db.getCurrency());
                            rerObj.setAccount(accountDao.load(recking.getAccountId()));
                            rerObj.setType(PocketAccounterGeneral.INCOME);
                            rerObj.setDescription(context.getResources().getString(R.string.borrow_recking_statistics));
                            incomes.add(rerObj);
                        }
                    }
                }
            }
        }

        // Credit
        for (CreditDetials cr : creditDetialsDao.loadAll()) {
            for (ReckingCredit reckingCredit : cr.getReckings()) {
                if (reckingCredit.getPayDate().compareTo(begin) > 0 && reckingCredit.getPayDate().compareTo(end) < 0) {
                    ReportObject reportObject = new ReportObject();
                    reportObject.setType(PocketAccounterGeneral.EXPENSE);
                    reportObject.setAmount(reckingCredit.getAmount());
                    reportObject.setDate(reckingCredit.getPayDate());
                    reportObject.setCurrency(cr.getValyute_currency());
                    reportObject.setAccount(accountDao.load(reckingCredit.getAccountId()));
                    reportObject.setDescription(cr.getCredit_name());
                    expances.add(reportObject);
                }
            }
        }
    }

    public List<ReportObject> getIncomes(Calendar begin, Calendar end) {
//        if (incomes != null)
//            return incomes;
//        else {
        getIncomeExpanceDates(begin, end);
        return incomes;
//        }
    }

    public List<ReportObject> getExpances(Calendar begin, Calendar end) {
//        if (expances != null)
//            return expances;
//        else {
        getIncomeExpanceDates(begin, end);
        return expances;
//        }
    }
}