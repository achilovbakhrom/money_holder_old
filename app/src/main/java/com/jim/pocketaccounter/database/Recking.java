package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by user on 6/8/2016.
 */
@Entity(nameInDb = "RECKINGS", active = true)
public class Recking {
    @Id(autoincrement = true)
    @Property
    private Long id;
    @Property
    private String payDate;
    @Property
    private double amount;
    @Property
    private String accountId;
    @Property
    private String debtBorrowsId;
    @Property
    private String comment;
    /** Used for active entity operations. */
    @Generated(hash = 55176781)
    private transient ReckingDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Recking(String payDate, double amount, String id, String accountId, String comment) {
        this.payDate = payDate;
        this.amount = amount;
        this.debtBorrowsId = id;
        this.accountId = accountId;
        this.comment = comment;
    }

    public Recking(String payDate, double amount, String id, String comment) {
        this.payDate = payDate;
        this.amount = amount;
        this.debtBorrowsId = id;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Recking() {
    }

    @Generated(hash = 1165042321)
    public Recking(Long id, String payDate, double amount, String accountId, String debtBorrowsId,
            String comment) {
        this.id = id;
        this.payDate = payDate;
        this.amount = amount;
        this.accountId = accountId;
        this.debtBorrowsId = debtBorrowsId;
        this.comment = comment;
    }

    public String getPayDate() {
        return payDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDebtBorrowsId() {
        return debtBorrowsId;
    }

    public void setDebtBorrowsId(String debtBorrowsId) {
        this.debtBorrowsId = debtBorrowsId;
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
    @Generated(hash = 1014423750)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReckingDao() : null;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
