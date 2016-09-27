package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.FinanceRecord;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by DEV on 01.09.2016.
 */

public class CommonOperations {
    @Inject
    DaoSession daoSession;
    private CurrencyDao currencyDao;
    private Context context;
    private Currency mainCurrency;
    @Inject @Named(value = "begin") Calendar begin;
    @Inject @Named(value = "end") Calendar end;

    public CommonOperations(Context context) {
        ((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
        this.currencyDao = daoSession.getCurrencyDao();
        this.context = context;
    }
    public Currency getMainCurrency() {
        if (mainCurrency == null) {
            List<Currency> currencies = currencyDao.loadAll();
            for (Currency currency : currencies) {
                if (currency.getIsMain())
                    mainCurrency = currency;
            }
        }
        return mainCurrency;
    }

    public double getCost(FinanceRecord record) {
        double amount = 0.0;
        if (record.getCurrency().getMain())
            return record.getAmount();
        double koeff = 1.0;
        long diff = record.getDate().getTimeInMillis() - record.getCurrency().getCosts().get(0).getDay().getTimeInMillis();
        if (diff < 0) {
            koeff = record.getCurrency().getCosts().get(0).getCost();
            return record.getAmount()/koeff;
        }
        int pos = 0;
        while (diff >= 0 && pos < record.getCurrency().getCosts().size()) {
            diff = record.getDate().getTimeInMillis() - record.getCurrency().getCosts().get(pos).getDay().getTimeInMillis();
            if(diff>=0)
                koeff = record.getCurrency().getCosts().get(pos).getCost();
            pos++;
        }
        amount = record.getAmount()/koeff;
        return amount;
    }
    public double getCost(Calendar date, Currency currency, double amount) {
        if (currency.getMain()) return amount;
        double koeff = 1.0;
        long diff = date.getTimeInMillis() - currency.getCosts().get(0).getDay().getTimeInMillis();
        if (diff < 0) {
            koeff = currency.getCosts().get(0).getCost();
            return amount/koeff;
        }
        int pos = 0;
        while (diff >= 0 && pos < currency.getCosts().size()) {
            diff = date.getTimeInMillis() - currency.getCosts().get(pos).getDay().getTimeInMillis();
            if(diff>=0)
                koeff = currency.getCosts().get(pos).getCost();
            pos++;
        }
        amount = amount/koeff;
        return amount;
    }

    public double getCost(Calendar date, Currency fromCurrency, Currency toCurrency, double amount) {
        //TODO tekwir bir yana

        if (fromCurrency.getId().matches(toCurrency.getId())) return amount;
        double tokoeff = 1.0;
        double fromkoeff2 = 1.0;
        long todiff1 = date.getTimeInMillis() - toCurrency.getCosts().get(0).getDay().getTimeInMillis();
        long fromdiff = date.getTimeInMillis() - fromCurrency.getCosts().get(0).getDay().getTimeInMillis();
        if (todiff1 < 0) {
            tokoeff = toCurrency.getCosts().get(0).getCost();
        }
        if(fromdiff < 0){
            fromkoeff2 = fromCurrency.getCosts().get(0).getCost();
        }
        int pos = 0;
        while (todiff1 >= 0 && pos < toCurrency.getCosts().size()) {
            todiff1 = date.getTimeInMillis() - toCurrency.getCosts().get(pos).getDay().getTimeInMillis();
            if(todiff1>=0)
                tokoeff = toCurrency.getCosts().get(pos).getCost();
            pos++;
        }
        pos=0;
        while (fromdiff >= 0 && pos < fromCurrency.getCosts().size()) {
            fromdiff = date.getTimeInMillis() - fromCurrency.getCosts().get(pos).getDay().getTimeInMillis();
            if(fromdiff>=0)
                fromkoeff2 = fromCurrency.getCosts().get(pos).getCost();
            pos++;
        }
        amount = tokoeff*amount/fromkoeff2;
        return amount;
    }

    public int countOfDayBetweenCalendars(Calendar begin, Calendar end) {
        int countOfDays = 0;
        Calendar b = (Calendar) begin.clone();
        b.set(Calendar.HOUR_OF_DAY, 0);
        b.set(Calendar.MINUTE, 0);
        b.set(Calendar.SECOND, 0);
        b.set(Calendar.MILLISECOND, 0);
        Calendar e = (Calendar) end.clone();
        e.set(Calendar.HOUR_OF_DAY, 23);
        e.set(Calendar.MINUTE, 59);
        e.set(Calendar.SECOND, 59);
        e.set(Calendar.MILLISECOND, 59);
        while (b.compareTo(e) <= 0) {
            countOfDays++;
            b.add(Calendar.DAY_OF_MONTH, 1);
        }
        return countOfDays;
    }
    public float convertDpToPixel(float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public  void ColorSubSeq(String text, String whichWordColor, String colorCode, TextView textView){
        String textUpper=text.toUpperCase();
        String whichWordColorUpper=whichWordColor.toUpperCase();
        SpannableString ss = new SpannableString(text);
        int strar=0;

        while (textUpper.indexOf(whichWordColorUpper,strar)>=0&&whichWordColor.length()!=0) {
            ss.setSpan(new BackgroundColorSpan(Color.parseColor(colorCode)),textUpper.indexOf(whichWordColorUpper,strar), textUpper.indexOf(whichWordColorUpper,strar)+whichWordColorUpper.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            strar=textUpper.indexOf(whichWordColorUpper,strar)+whichWordColorUpper.length();
        }
        textView.setText(ss);
    }

    public int betweenDays(Calendar begin, Calendar end) {
        Calendar b = (Calendar) begin.clone();
        Calendar e = (Calendar) end.clone();
        int result = 0;
        while (b.compareTo(e) <= 0) {
            b.add(Calendar.DAY_OF_MONTH, 1);
            result++;
        }
        return result;
    }


    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int cornerRadius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerRadius/8, cornerRadius/8, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public boolean compareTimeInOneDay(Calendar source, Calendar target){
        begin.setTimeInMillis(source.getTimeInMillis());
        begin.set(Calendar.HOUR,0);
        begin.set(Calendar.MINUTE,0);
        begin.set(Calendar.SECOND,0);
        begin.set(Calendar.MILLISECOND,0);
        end.setTimeInMillis(source.getTimeInMillis());
        end.set(Calendar.HOUR,23);
        end.set(Calendar.MINUTE,59);
        end.set(Calendar.SECOND,59);
        end.set(Calendar.MILLISECOND,59);
        return begin.compareTo(target) <= 0 &&
                end.compareTo(target) >= 0;
    }
}
