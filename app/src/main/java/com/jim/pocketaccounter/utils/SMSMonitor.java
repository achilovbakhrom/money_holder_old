package com.jim.pocketaccounter.utils;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.DaoMaster;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseObjectDao;
import com.jim.pocketaccounter.database.SmsParseSuccess;
import com.jim.pocketaccounter.database.TemplateSms;

import org.greenrobot.greendao.database.Database;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class SMSMonitor extends BroadcastReceiver {
    private static final String SMS_EXTRA_NAME = "pdus";
//    @Inject
//    DaoSession daoSession;

    @Override
    public void onReceive(Context context, Intent intent) {
//        ((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext())
//                .inject(this);
//        ((PocketAccounter) context).component((PocketAccounterApplication)((PocketAccounter) context)
//                .getApplication()).inject(this);

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
                messages += body;
                // Here you can add any your code to work with incoming SMS
                // I added encrypting of all received SMS
//                putSmsToDatabase(contentResolver, sms);

//                checkSmsParse(address, messages, sms.getTimestampMillis(), context);
                Intent myIntent = new Intent();
                myIntent.putExtra("number", address);
                myIntent.putExtra("body", messages);
                myIntent.putExtra("date", sms.getTimestampMillis());
                context.startService(myIntent);
            }
        }
    }
    
//    private void checkSmsParse (String number, String body, long date, Context context) {
//        if (daoSession == null) {
//            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, PocketAccounterGeneral.CURRENT_DB_NAME);
//            Database db = helper.getWritableDb();
//            daoSession = new DaoMaster(db).newSession();
//        }
//        SmsParseObject smsParseObject =daoSession.getSmsParseObjectDao().queryBuilder().where(SmsParseObjectDao.Properties.Number.eq(number)).list().get(0);
//        if (smsParseObject != null) {
//            SmsParseSuccess smsParseSuccess = null;
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(date);
//            for (TemplateSms templateSms : smsParseObject.getTemplates()) {
//                Pattern pattern = Pattern.compile(templateSms.getRegex());
//                Matcher matcher = pattern.matcher(body);
//                matcher.matches();
//                if (matcher.matches()) {
//                    smsParseSuccess = new SmsParseSuccess();
//                    smsParseSuccess.setBody(body);
//                    double summ = 0;
//                    try {
//                        smsParseSuccess.setDate(calendar);
//                        smsParseSuccess.setCurrency(smsParseObject.getCurrency());
//                        smsParseSuccess.setAccount(smsParseObject.getAccount());
//                        smsParseSuccess.setNumber(number);
//                        smsParseSuccess.setSmsParseObjectId(smsParseObject.getId());
//                        if (matcher.group(templateSms.getPosAmountGroup()) != null
//                                && !matcher.group(templateSms.getPosAmountGroup()).isEmpty()) {
//                            try {
//                                summ = Double.parseDouble(matcher.group(templateSms.getPosAmountGroup()));
//                                smsParseSuccess.setAmount(summ);
//                                smsParseSuccess.setIsSuccess(true);
//                                smsParseSuccess.setType(templateSms.getType());
//                            } catch (Exception e) {
//                                try {
//                                    if (matcher.group(templateSms.getPosAmountGroupSecond()) != null
//                                            && !matcher.group(templateSms.getPosAmountGroupSecond()).isEmpty()) {
//                                        summ = Double.parseDouble(matcher.group(templateSms.getPosAmountGroupSecond()));
//                                        smsParseSuccess.setAmount(summ);
//                                        smsParseSuccess.setIsSuccess(true);
//                                        smsParseSuccess.setType(templateSms.getType());
//                                    }
//                                } catch (Exception e1) {
//                                    smsParseSuccess.setIsSuccess(false);
//                                }
//                            }
//                        } else if (matcher.group(templateSms.getPosAmountGroupSecond()) != null
//                                && !matcher.group(templateSms.getPosAmountGroupSecond()).isEmpty()) {
//                            try {
//                                summ = Double.parseDouble(matcher.group(templateSms.getPosAmountGroupSecond()));
//                                smsParseSuccess.setAmount(summ);
//                                smsParseSuccess.setIsSuccess(true);
//                                smsParseSuccess.setType(templateSms.getType());
//                            } catch (Exception e1) {
//                                smsParseSuccess.setIsSuccess(false);
//                            }
//                        } else {
//                            smsParseSuccess.setIsSuccess(false);
//                        }
//                        smsParseSuccess.setAmount(summ);
//                        smsParseSuccess.setType(templateSms.getType());
//                        smsParseSuccess.setIsSuccess(true);
//                    } catch (Exception e) {
//                        smsParseSuccess.setIsSuccess(false);
//                    }
//                    break;
//                }
//            }
//            if (smsParseSuccess == null) {
//                smsParseSuccess = new SmsParseSuccess();
//                smsParseSuccess.setNumber(number);
//                smsParseSuccess.setDate(calendar);
//                smsParseSuccess.setSmsParseObjectId(smsParseObject.getId());
//                smsParseSuccess.setAccount(smsParseObject.getAccount());
//                smsParseSuccess.setCurrency(smsParseObject.getCurrency());
//                smsParseSuccess.setBody(body);
//                smsParseSuccess.setIsSuccess(false);
//            }
//            daoSession.getSmsParseSuccessDao().insertOrReplace(smsParseSuccess);
//        }
//    }
}
