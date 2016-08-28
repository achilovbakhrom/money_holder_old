package com.jim.pocketaccounter.database.convertors;

import org.greenrobot.greendao.converter.PropertyConverter;
import java.util.Calendar;

/**
 * Created by DEV on 27.08.2016.
 */

public class CalendarConvertor implements PropertyConverter<Calendar, Long> {
    @Override
    public Calendar convertToEntityProperty(Long databaseValue) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(databaseValue);
        return calendar;
    }

    @Override
    public Long convertToDatabaseValue(Calendar entityProperty) {
        return entityProperty.getTimeInMillis();
    }
}
