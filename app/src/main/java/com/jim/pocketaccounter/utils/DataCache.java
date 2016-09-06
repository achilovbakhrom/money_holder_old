package com.jim.pocketaccounter.utils;

import java.util.Calendar;

public class DataCache {
    private CategoryEditFragmentDatas categoryEditFragmentDatas;
    public DataCache() {
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
}
