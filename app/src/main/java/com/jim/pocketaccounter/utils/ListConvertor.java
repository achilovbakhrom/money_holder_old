package com.jim.pocketaccounter.utils;

import org.greenrobot.greendao.converter.PropertyConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/2/16.
 */

public class ListConvertor implements PropertyConverter<String[], String> {

    @Override
    public String[] convertToEntityProperty(String databaseValue) {
        String [] list = databaseValue.split("qwerty");
        return list;
    }

    @Override
    public String convertToDatabaseValue(String[] list) {
        String result = "";
        for (String s : list) {
            result += s + "qwerty";
        }
        return result;
    }
}
