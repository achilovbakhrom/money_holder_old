package com.jim.pocketaccounter.database;

import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by DEV on 06.09.2016.
 */
@Entity(nameInDb = "PURPOSES", active = true)
public class Purpose {
    @Property
    private String icon;
    @Property
    private String description;
    @Convert(converter = CalendarConvertor.class, columnType = Long.class)
    private Calendar begin;
    @Convert(converter = CalendarConvertor.class, columnType = Long.class)
    private Calendar end;
    @Property
    private double purpose;
    @Property
    private double accumulated;
    @Id
    @Property
    private String id;
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1602104432)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPurposeDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 1375959903)
    private transient PurposeDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    public double getAccumulated() {
        return this.accumulated;
    }
    public void setAccumulated(double accumulated) {
        this.accumulated = accumulated;
    }
    public double getPurpose() {
        return this.purpose;
    }
    public void setPurpose(double purpose) {
        this.purpose = purpose;
    }
    public Calendar getEnd() {
        return this.end;
    }
    public void setEnd(Calendar end) {
        this.end = end;
    }
    public Calendar getBegin() {
        return this.begin;
    }
    public void setBegin(Calendar begin) {
        this.begin = begin;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Generated(hash = 1288017454)
    public Purpose(String icon, String description, Calendar begin, Calendar end,
            double purpose, double accumulated, String id) {
        this.icon = icon;
        this.description = description;
        this.begin = begin;
        this.end = end;
        this.purpose = purpose;
        this.accumulated = accumulated;
        this.id = id;
    }
    @Generated(hash = 450982221)
    public Purpose() {
    }
}