package com.jim.pocketaccounter.database;

import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by root on 10/12/16.
 */
@Entity
public class UserEnteredCalendars {
    @Property
    @Id
    private long id;
    @Convert(converter = CalendarConvertor.class, columnType = String.class)
    private Calendar calendar;
    @Property
    private String currencyId;
    public String getCurrencyId() {
        return this.currencyId;
    }
    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
    public Calendar getCalendar() {
        return this.calendar;
    }
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Generated(hash = 1203902453)
    public UserEnteredCalendars(long id, Calendar calendar, String currencyId) {
        this.id = id;
        this.calendar = calendar;
        this.currencyId = currencyId;
    }
    @Generated(hash = 443835265)
    public UserEnteredCalendars() {
    }

}
