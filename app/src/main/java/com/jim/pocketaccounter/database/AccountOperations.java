package com.jim.pocketaccounter.database;

import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by DEV on 31.08.2016.
 */
@Entity(nameInDb = "ACCOUNT_OPERATIONS", active = true)
public class AccountOperations {
    @Id
    @Property
    private Long id;
    @Property
    private int type;
    @Convert(converter = CalendarConvertor.class, columnType = Long.class)
    private Calendar date;
    @ToOne
    private Account account;
    @ToOne
    private Currency currency;
    @Property
    private double amount;
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
    @Generated(hash = 1942461383)
    public void setAccount(Account account) {
        synchronized (this) {
            this.account = account;
            account__refreshed = true;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 899885349)
    public Account getAccount() {
        if (account != null || !account__refreshed) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccountDao targetDao = daoSession.getAccountDao();
            targetDao.refresh(account);
            account__refreshed = true;
        }
        return account;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2068393221)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAccountOperationsDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 459259647)
    private transient AccountOperationsDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    @Generated(hash = 131953365)
    private transient boolean currency__refreshed;
    @Generated(hash = 1833446427)
    private transient boolean account__refreshed;
    public Calendar getDate() {
        return this.date;
    }
    public void setDate(Calendar date) {
        this.date = date;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 509189523)
    public void setCurrency(Currency currency) {
        synchronized (this) {
            this.currency = currency;
            currency__refreshed = true;
        }
    }
    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 257181933)
    public Currency peakCurrency() {
        return currency;
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 878915352)
    public Currency getCurrency() {
        if (currency != null || !currency__refreshed) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CurrencyDao targetDao = daoSession.getCurrencyDao();
            targetDao.refresh(currency);
            currency__refreshed = true;
        }
        return currency;
    }
    public double getAmount() {
        return this.amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 1320961525)
    public Account peakAccount() {
        return account;
    }
    @Generated(hash = 2097325390)
    public AccountOperations(Long id, int type, Calendar date, double amount) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.amount = amount;
    }
    @Generated(hash = 1367819725)
    public AccountOperations() {
    }
}
