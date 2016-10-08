package com.jim.pocketaccounter.utils;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.jim.pocketaccounter.database.DaoMaster;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseObjectDao;
import com.jim.pocketaccounter.database.SmsParseSuccess;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSMonitor extends BroadcastReceiver {
    private static final String SMS_EXTRA_NAME = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getExtras() == null) return;
////        FinanceManager manager = new FinanceManager(context);
////        ArrayList<SmsParseObject> objects = manager.getSmsObjects();
//        if (intent != null && intent.getAction() != null &&
//                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
//            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
//
//            SmsMessage[] messages = new SmsMessage[pduArray.length];
//            for (int i = 0; i < pduArray.length; i++) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    String format = intent.getExtras().getString("format");
//                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i], format);
//                }
//                else {
//                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
//                }
//                for (int j=0; j<objects.size(); j++) {
//                    if (messages[i].getDisplayOriginatingAddress().contains(objects.get(j).getNumber())) {
//                        String messageBody = messages[i].getMessageBody();
//                        String amount = "";
//                        RootCategory category = null;
//                        Account account;
//                        Currency currency ;
//                        if (objects.get(j).getType() == PocketAccounterGeneral.SMS_ONLY_INCOME) {
//                            String sms_income_id = context.getResources().getString(R.string.sms_parse_income_id);
//                            boolean catFound = false;
//                            for (int k=0; k<manager.getCategories().size(); k++) {
//                                if (sms_income_id.matches(manager.getCategories().get(k).getId())) {
//                                    category = manager.getCategories().get(k);
//                                    catFound = true;
//                                    break;
//                                }
//                            }
//                            if (!catFound) {
//                                category = new RootCategory();
////                                category.setSubCategories(new ArrayList<SubCategory>());
//                                category.setName(context.getResources().getString(R.string.sms_parse_income));
//                                category.setType(PocketAccounterGeneral.INCOME);
//                                category.setId(sms_income_id);
//                                category.setIcon("icons_20");
//                                manager.getCategories().add(category);
//                                manager.saveCategories();
//                            }
//
//                        }
//                        if (objects.get(j).getType() == PocketAccounterGeneral.SMS_ONLY_EXPENSE) {
//                            String sms_expense_id = context.getResources().getString(R.string.sms_parse_expense_id);
//                            boolean catFound = false;
//                            for (int k=0; k<manager.getCategories().size(); k++) {
//                                if (sms_expense_id.matches(manager.getCategories().get(k).getId())) {
//                                    category = manager.getCategories().get(k);
//                                    catFound = true;
//                                    break;
//                                }
//                            }
//                            if (!catFound) {
//                                category = new RootCategory();
////                                category.setSubCategories(new ArrayList<SubCategory>());
//                                category.setName(context.getResources().getString(R.string.sms_parse_expense));
//                                category.setType(PocketAccounterGeneral.EXPENSE);
//                                category.setId(sms_expense_id);
//                                category.setIcon("icons_21");
//                                manager.getCategories().add(category);
//                                manager.saveCategories();
//                            }
//                        }
//                        if (objects.get(j).getType() == PocketAccounterGeneral.SMS_BOTH) {
//                            boolean catFound = false;
//                            String[] incomes = objects.get(j).getIncomeWords().split(",");
//                            for (int k=0; k < incomes.length; k++) {
//                                if (messageBody.contains(incomes[k])) {
//                                    catFound = true;
//                                    String sms_income_id = context.getResources().getString(R.string.sms_parse_income_id);
//                                    boolean incFound = false;
//                                    for (int l=0; l<manager.getCategories().size(); l++) {
//                                        if (sms_income_id.matches(manager.getCategories().get(l).getId())) {
//                                            category = manager.getCategories().get(l);
//                                            incFound = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!incFound) {
//                                        category = new RootCategory();
////                                        category.setSubCategories(new ArrayList<SubCategory>());
//                                        category.setName(context.getResources().getString(R.string.sms_parse_income));
//                                        category.setType(PocketAccounterGeneral.INCOME);
//                                        category.setId(sms_income_id);
//                                        category.setIcon("icons_20");
//                                        manager.getCategories().add(category);
//                                        manager.saveCategories();
//                                    }
//                                    break;
//                                }
//                            }
//                            if (!catFound) {
//                                String[] expenses = objects.get(j).getExpenseWords().split(",");
//                                for (int k=0; k < expenses.length; k++) {
//                                    if (messageBody.contains(expenses[k])) {
//                                        catFound = true;
//                                        String sms_expense_id = context.getResources().getString(R.string.sms_parse_expense_id);
//                                        boolean expFound = false;
//                                        for (int l=0; l<manager.getCategories().size(); l++) {
//                                            if (sms_expense_id.matches(manager.getCategories().get(l).getId())) {
//                                                category = manager.getCategories().get(l);
//                                                expFound = true;
//                                                break;
//                                            }
//                                        }
//                                        if (!expFound) {
//                                            category = new RootCategory();
////                                            category.setSubCategories(new ArrayList<SubCategory>());
//                                            category.setName(context.getResources().getString(R.string.sms_parse_expense));
//                                            category.setType(PocketAccounterGeneral.EXPENSE);
//                                            category.setId(sms_expense_id);
//                                            category.setIcon("icons_21");
//                                            manager.getCategories().add(category);
//                                            manager.saveCategories();
//                                        }
//                                    }
//                                }
//                            }
//                            if (!catFound) return;
//                        }
//                        boolean amountFound = false;
//                        String[] amounts = objects.get(j).getAmountWords().split(",");
//                        for (int k=0; k<amounts.length; k++) {
//                            if (messageBody.contains(amounts[k])) {
//                                amountFound = true;
//                                int pos = messageBody.lastIndexOf(amounts[k]);
//                                while(!isNumber(messageBody.charAt(pos))) {
//                                    pos++;
//                                }
//
//                                while (isNumber(messageBody.charAt(pos))) {
//                                    amount += messageBody.charAt(pos);
//                                    pos++;
//                                }
//                            }
//                            if (!amountFound) return;
//                        }
//                        currency = objects.get(i).getCurrency();
//                        account = objects.get(i).getAccount();
//                        double sum = parseDouble(amount);
//                        FinanceRecord record = new FinanceRecord();
//                        record.setDate(Calendar.getInstance());
//                        record.setCategory(category);
//                        record.setSubCategory(null);
//                        record.setCurrency(currency);
//                        record.setAccount(account);
//                        record.setRecordId("record_"+UUID.randomUUID().toString());
//                        record.setAmount(sum);
//                        if (PocketAccounter.financeManager != null) {
//                            PocketAccounter.financeManager.getRecords().add(record);
//                            PocketAccounter.financeManager.saveRecords();
//                        } else
//                        {
//                            manager.getRecords().add(record);
//                            manager.saveRecords();
//                        }
//                        //TODO
//                        SharedPreferences sPref;
//                        sPref = context.getSharedPreferences("infoFirst", MODE_PRIVATE);
//                        int t=sPref.getInt(WidgetKeys.SPREF_WIDGET_ID,-1);
//                        if(t>=0){
//                            if(AppWidgetManager.INVALID_APPWIDGET_ID!=t)
//                                WidgetProvider.updateWidget(context, AppWidgetManager.getInstance(context),
//                                        t);
//                        }
//                    }
//                }
//            }
//        }
//        abortBroadcast();
        // Get the SMS map from Intent
//        Bundle extras = intent.getExtras();
//        String messages = "";
//        if ( extras != null )
//        {
//            // Get received SMS array
//            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
//            // Get ContentResolver object for pushing encrypted SMS to the incoming folder
//            ContentResolver contentResolver = context.getContentResolver();
//
//            for ( int i = 0; i < smsExtra.length; ++i ) {
//                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
//                String body = sms.getMessageBody().toString();
//                String address = sms.getOriginatingAddress();
//                messages += "SMS from " + address + " :\n";
//                messages += body + "\n";
//                // Here you can add any your code to work with incoming SMS
//                // I added encrypting of all received SMS
////                putSmsToDatabase( contentResolver, sms );
//                Toast.makeText( context, "chisad" + messages, Toast.LENGTH_SHORT ).show();
//            }
//            // Display SMS message
////            Toast.makeText( context, messages, Toast.LENGTH_SHORT ).show();
//        }
        Bundle extras = intent.getExtras();

        String messages = "";
        if (extras != null) {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

            // Get ContentResolver object for pushing encrypted SMS to the incoming folder
            ContentResolver contentResolver = context.getContentResolver();
            String address = "";
            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                String body = sms.getMessageBody().toString();
                address = sms.getOriginatingAddress();
                messages += "SMS from " + address + " :\n";
                messages += body + "\n";
                // Here you can add any your code to work with incoming SMS
                // I added encrypting of all received SMS
//                putSmsToDatabase(contentResolver, sms);
                checkSmsParse(address, messages, sms.getTimestampMillis(), context);
            }
            // Display SMS message
            Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void checkSmsParse (String number, String body, long date, Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, PocketAccounterGeneral.CURRENT_DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        SmsParseObject smsParseObject =daoSession.getSmsParseObjectDao().queryBuilder().where(SmsParseObjectDao.Properties.Number.eq(number)).list().get(0);
        if (smsParseObject != null) {
            SmsParseSuccess smsParseSuccess = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            for (TemplateSms templateSms : smsParseObject.getTemplates()) {
                Pattern pattern = Pattern.compile(templateSms.getRegex());
                Matcher matcher = pattern.matcher(body);
                matcher.matches();
                if (matcher.matches()) {
                    smsParseSuccess = new SmsParseSuccess();
                    smsParseSuccess.setBody(body);
                    double amount = 0;
                    try {
                        smsParseSuccess.setDate(calendar);
                        smsParseSuccess.setCurrency(smsParseObject.getCurrency());
                        smsParseSuccess.setAccount(smsParseObject.getAccount());
                        smsParseSuccess.setNumber(number);
                        smsParseSuccess.setSmsParseObjectId(smsParseObject.getId());
                        amount = Double.parseDouble(matcher.group(templateSms.getPosAmountGroup()));
                        smsParseSuccess.setAmount(amount);
                        smsParseSuccess.setType(templateSms.getType());
                        smsParseSuccess.setIsSuccess(true);
                    } catch (Exception e) {
                        smsParseSuccess.setIsSuccess(false);
                    }
                    break;
                }
            }
            if (smsParseSuccess == null) {
                smsParseSuccess = new SmsParseSuccess();
                smsParseSuccess.setNumber(number);
                smsParseSuccess.setDate(calendar);
                smsParseSuccess.setSmsParseObjectId(smsParseObject.getId());
                smsParseSuccess.setAccount(smsParseObject.getAccount());
                smsParseSuccess.setCurrency(smsParseObject.getCurrency());
                smsParseSuccess.setBody(body);
                smsParseSuccess.setIsSuccess(false);
            }
            daoSession.getSmsParseSuccessDao().insertOrReplace(smsParseSuccess);
        }
    }
}
