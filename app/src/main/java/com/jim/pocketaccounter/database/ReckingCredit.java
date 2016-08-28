package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by DEV on 11.06.2016.
 */
@Entity(nameInDb = "RECKING_CREDITS", active = true)
public class ReckingCredit {
    @Id(autoincrement = true)
    private Long id;
    @Property
    private long payDate;
    @Property
    private double amount;
    @Property
    private String accountId;
    @Property
    private long myCredit_id;
    @Property
    private String comment;
    /** Used for active entity operations. */
    @Generated(hash = 1358241115)
    private transient ReckingCreditDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public ReckingCredit(long payDate, double amount, String accountId, long myCredit_id, String comment) {
        this.payDate = payDate;
        this.amount = amount;
        this.accountId = accountId;
        this.myCredit_id = myCredit_id;
        this.comment = comment;
    }

    @Generated(hash = 423989890)
    public ReckingCredit(Long id, long payDate, double amount, String accountId, long myCredit_id, String comment) {
        this.id = id;
        this.payDate = payDate;
        this.amount = amount;
        this.accountId = accountId;
        this.myCredit_id = myCredit_id;
        this.comment = comment;
    }

    @Generated(hash = 277218453)
    public ReckingCredit() {
    }

    public long getPayDate() {
        return payDate;
    }

    public void setPayDate(long payDate) {
        this.payDate = payDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getMyCredit_id() {
        return myCredit_id;
    }

    public void setMyCredit_id(long myCredit_id) {
        this.myCredit_id = myCredit_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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
    @Generated(hash = 897927379)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReckingCreditDao() : null;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
