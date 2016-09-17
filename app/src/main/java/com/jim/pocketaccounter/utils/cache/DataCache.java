package com.jim.pocketaccounter.utils.cache;

import android.content.SharedPreferences;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.utils.SharedPreferencesKeys;

import java.util.Calendar;

import javax.inject.Inject;

public class DataCache {
    private CategoryEditFragmentDatas categoryEditFragmentDatas;
    private Calendar beginDate, endDate;
    @Inject SharedPreferences sharedPreferences;
    public DataCache(PocketAccounterApplication application) {
        application.component().inject(this);
        initBeginAndEndDates();
        categoryEditFragmentDatas = new CategoryEditFragmentDatas();
    }
    public CategoryEditFragmentDatas getCategoryEditFragmentDatas() {
        if (categoryEditFragmentDatas == null)
            categoryEditFragmentDatas = new CategoryEditFragmentDatas();
        return categoryEditFragmentDatas;
    }
    public class CategoryEditFragmentDatas {
        private int mode;
        private Calendar date;
        private int pos;
        private int type;
        public void setPos(int pos) {
            this.pos = pos;
        }
        public int getPos() {
            return pos;
        }
        public int getMode() {
            return mode;
        }
        public void setMode(int mode) {
            this.mode = mode;
        }
        public Calendar getDate() {
            return date;
        }
        public void setDate(Calendar date) {
            this.date = date;
        }
        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }
    }

    private void initBeginAndEndDates() {
        endDate = Calendar.getInstance();
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 59);
        beginDate = Calendar.getInstance();
        beginDate.set(Calendar.HOUR_OF_DAY, 0);
        beginDate.set(Calendar.MINUTE, 0);
        beginDate.set(Calendar.SECOND, 0);
        beginDate.set(Calendar.MILLISECOND, 0);
        beginDate.add(Calendar.YEAR, -1);
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = (Calendar) beginDate.clone();
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = (Calendar) endDate.clone();
    }
}
