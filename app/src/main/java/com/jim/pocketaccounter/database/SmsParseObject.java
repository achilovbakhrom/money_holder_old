package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "SMS_PARSE_OBJECTS", active = true)
public class SmsParseObject {
    @Id
    private Long id;
    @Property
    private String number= "";
    @Property
    private String incomeWords = "";
    @Property
    private String expenseWords = "";
    @Property
    private String amountWords = "";
    @ToOne
    private Account account;
    @ToOne
    private Currency currency;
    @Property
    private int type;
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
    @Generated(hash = 1939383941)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSmsParseObjectDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 645439137)
    private transient SmsParseObjectDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getAmountWords() {
        return this.amountWords;
    }
    public void setAmountWords(String amountWords) {
        this.amountWords = amountWords;
    }
    public String getExpenseWords() {
        return this.expenseWords;
    }
    public void setExpenseWords(String expenseWords) {
        this.expenseWords = expenseWords;
    }
    public String getIncomeWords() {
        return this.incomeWords;
    }
    public void setIncomeWords(String incomeWords) {
        this.incomeWords = incomeWords;
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 152815887)
    public SmsParseObject(Long id, String number, String incomeWords,
            String expenseWords, String amountWords, int type) {
        this.id = id;
        this.number = number;
        this.incomeWords = incomeWords;
        this.expenseWords = expenseWords;
        this.amountWords = amountWords;
        this.type = type;
    }
    @Generated(hash = 1140750388)
    public SmsParseObject() {
    }

}
