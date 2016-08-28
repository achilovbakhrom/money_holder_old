package com.jim.pocketaccounter.database;
import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Calendar;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "FINANCE_RECORDS", active = true)
public class FinanceRecord {
    @Convert(converter = CalendarConvertor.class, columnType = Long.class)
    private Calendar date;
    @Property
    private double amount = 0.0;
    @ToOne
    private RootCategory category = null;
    @ToOne
    private SubCategory subCategory = null;
    @ToOne
    private Account account = null;
    @ToOne
    private Currency currency = null;
    @Property
    @Id
    private String recordId;
    @ToMany(joinProperties = {
            @JoinProperty(name = "recordId", referencedName = "recordId")
    })
    private List<PhotoDetails> allTickets=null;
    @Property
    private String comment;
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
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2058894754)
    public synchronized void resetAllTickets() {
        allTickets = null;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public void setAllTickets(List<PhotoDetails> allTickets) {
        this.allTickets = allTickets;
    }
    @Keep
    public List<PhotoDetails> getAllTickets() {
        if (allTickets == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PhotoDetailsDao targetDao = daoSession.getPhotoDetailsDao();
            List<PhotoDetails> allTicketsNew = targetDao._queryFinanceRecord_AllTickets(recordId);
            synchronized (this) {
                if(allTickets == null) {
                    allTickets = allTicketsNew;
                }
            }
        }
        return allTickets;
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
    @Generated(hash = 131953365)
    private transient boolean currency__refreshed;
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1942461383)
    public void setAccount(Account account) {
        synchronized (this) {
            this.account = account;
            account__refreshed = true;
        }
    }
    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 1320961525)
    public Account peakAccount() {
        return account;
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
    @Generated(hash = 1833446427)
    private transient boolean account__refreshed;
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 166655378)
    public void setSubCategory(SubCategory subCategory) {
        synchronized (this) {
            this.subCategory = subCategory;
            subCategory__refreshed = true;
        }
    }
    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 1292138850)
    public SubCategory peakSubCategory() {
        return subCategory;
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 118870038)
    public SubCategory getSubCategory() {
        if (subCategory != null || !subCategory__refreshed) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SubCategoryDao targetDao = daoSession.getSubCategoryDao();
            targetDao.refresh(subCategory);
            subCategory__refreshed = true;
        }
        return subCategory;
    }
    @Generated(hash = 1118830490)
    private transient boolean subCategory__refreshed;
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 440135474)
    public void setCategory(RootCategory category) {
        synchronized (this) {
            this.category = category;
            category__refreshed = true;
        }
    }
    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 1685191291)
    public RootCategory peakCategory() {
        return category;
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 110101601)
    public RootCategory getCategory() {
        if (category != null || !category__refreshed) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RootCategoryDao targetDao = daoSession.getRootCategoryDao();
            targetDao.refresh(category);
            category__refreshed = true;
        }
        return category;
    }
    @Generated(hash = 451216896)
    private transient boolean category__refreshed;
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 453688691)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFinanceRecordDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 43612006)
    private transient FinanceRecordDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getRecordId() {
        return this.recordId;
    }
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    public double getAmount() {
        return this.amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Calendar getDate() {
        return this.date;
    }
    public void setDate(Calendar date) {
        this.date = date;
    }
    @Generated(hash = 255089889)
    public FinanceRecord(Calendar date, double amount, String recordId,
            String comment) {
        this.date = date;
        this.amount = amount;
        this.recordId = recordId;
        this.comment = comment;
    }
    @Generated(hash = 1746567756)
    public FinanceRecord() {
    }
}
