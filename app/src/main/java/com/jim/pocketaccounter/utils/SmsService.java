package com.jim.pocketaccounter.utils;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.DaoMaster;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseObjectDao;
import com.jim.pocketaccounter.database.SmsParseSuccess;
import com.jim.pocketaccounter.database.TemplateSms;

import org.greenrobot.greendao.database.Database;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by root on 10/16/16.
 */

public class SmsService extends Service {
    @Inject
    DaoSession daoSession;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((PocketAccounterApplication) getApplicationContext()).component().inject(this);
        Bundle bundle=null;
        if(intent!=null)
            bundle= intent.getExtras();
        if(bundle==null)
            return 0;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), PocketAccounterGeneral.CURRENT_DB_NAME);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        if (!sqLiteDatabase.isOpen()) {
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        }

        List<SmsParseObject> smsParseObjects = daoSession.getSmsParseObjectDao().queryBuilder()
                .where(SmsParseObjectDao.Properties.Number.eq(intent.getStringExtra("number"))).list();

        if (!smsParseObjects.isEmpty()) {
            SmsParseObject smsParseObject = smsParseObjects.get(0);
            SmsParseSuccess smsParseSuccess = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(intent.getLongExtra("date", 0));
            for (TemplateSms templateSms : smsParseObject.getTemplates()) {
                Pattern pattern = Pattern.compile(templateSms.getRegex());
                Matcher matcher = pattern.matcher(intent.getStringExtra("body"));
                if (matcher.matches()) {
                    smsParseSuccess = new SmsParseSuccess();
                    smsParseSuccess.setBody(intent.getStringExtra("body"));
                    double summ = 0;
                    smsParseSuccess.setDate(calendar);
                    smsParseSuccess.setCurrency(smsParseObject.getCurrency());
                    smsParseSuccess.setAccount(smsParseObject.getAccount());
                    smsParseSuccess.setNumber(intent.getStringExtra("number"));
                    smsParseSuccess.setSmsParseObjectId(smsParseObject.getId());
                    String posAmountGroup = matcher.group(templateSms.getPosAmountGroup());
                    if (posAmountGroup != null
                            && posAmountGroup.matches("([0-9]+[.,]?[0-9]*)")) {
                        summ = Double.parseDouble(matcher.group(templateSms.getPosAmountGroup()));
                        smsParseSuccess.setAmount(summ);
                        smsParseSuccess.setIsSuccess(true);
                        smsParseSuccess.setType(templateSms.getType());
                    } else if (matcher.group(templateSms.getPosAmountGroupSecond()) != null
                            && matcher.group(templateSms.getPosAmountGroupSecond()).matches("([0-9]+[.,]?[0-9]*)")) {
                        summ = Double.parseDouble(matcher.group(templateSms.getPosAmountGroupSecond()));
                        smsParseSuccess.setAmount(summ);
                        smsParseSuccess.setIsSuccess(true);
                        smsParseSuccess.setType(templateSms.getType());
                    } else {
                        smsParseSuccess.setIsSuccess(false);
                    }
                    break;
                }
            }
            if (smsParseSuccess == null) {
                smsParseSuccess = new SmsParseSuccess();
                smsParseSuccess.setNumber(intent.getStringExtra("number"));
                smsParseSuccess.setDate(calendar);
                smsParseSuccess.setSmsParseObjectId(smsParseObject.getId());
                smsParseSuccess.setAccount(smsParseObject.getAccount());
                smsParseSuccess.setCurrency(smsParseObject.getCurrency());
                smsParseSuccess.setBody(intent.getStringExtra("body"));
                smsParseSuccess.setIsSuccess(false);
            }
            daoSession.getSmsParseSuccessDao().insertOrReplace(smsParseSuccess);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
