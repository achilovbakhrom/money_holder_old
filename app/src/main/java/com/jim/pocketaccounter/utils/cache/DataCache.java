package com.jim.pocketaccounter.utils.cache;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class DataCache {
    private CategoryEditFragmentDatas categoryEditFragmentDatas;
    private LruCache<Integer, List<Bitmap>> boardBitmaps;
    private LruCache<Integer, Bitmap> elements;
    private Calendar beginDate, endDate;
    @Inject SharedPreferences sharedPreferences;
    @Inject DaoSession daoSession;
    @Inject CommonOperations commonOperations;
    public DataCache(PocketAccounterApplication application) {
        application.component().inject(this);
        initBeginAndEndDates();
        categoryEditFragmentDatas = new CategoryEditFragmentDatas();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        elements = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
                    return bitmap.getByteCount() / 1024;
                else
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
        boardBitmaps = new LruCache<Integer, List<Bitmap>>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, List<Bitmap> bitmaps) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
                    return bitmaps.get(0).getByteCount() / 1024;
                else
                    return bitmaps.get(0).getRowBytes() * bitmaps.get(0).getHeight() / 1024;
            }
        };
    }
    public LruCache<Integer, List<Bitmap>> getBoardBitmapsCache() {
        return boardBitmaps;
    }
    public LruCache<Integer, Bitmap> getElements() {
        return elements;
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
