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
    @Property
    private String accountId;
    @ToOne(joinProperty = "accountId")
    private Account account;
    @Property
    private String currencyId;
    @ToOne(joinProperty = "currencyId")
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
    @Generated(hash = 1889019422)
    public void setCurrency(Currency currency) {
        synchronized (this) {
            this.currency = currency;
            currencyId = currency == null ? null : currency.getId();
            currency__resolvedKey = currencyId;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 376477166)
    public Currency getCurrency() {
        String __key = this.currencyId;
        if (currency__resolvedKey == null || currency__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CurrencyDao targetDao = daoSession.getCurrencyDao();
            Currency currencyNew = targetDao.load(__key);
            synchronized (this) {
                currency = currencyNew;
                currency__resolvedKey = __key;
            }
        }
        return currency;
    }
    @Generated(hash = 1170963677)
    private transient String currency__resolvedKey;
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1910176546)
    public void setAccount(Account account) {
        synchronized (this) {
            this.account = account;
            accountId = account == null ? null : account.getId();
            account__resolvedKey = accountId;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1230349477)
    public Account getAccount() {
        String __key = this.accountId;
        if (account__resolvedKey == null || account__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccountDao targetDao = daoSession.getAccountDao();
            Account accountNew = targetDao.load(__key);
            synchronized (this) {
                account = accountNew;
                account__resolvedKey = __key;
            }
        }
        return account;
    }
    @Generated(hash = 1221310859)
    private transient String account__resolvedKey;
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
    public String getCurrencyId() {
        return this.currencyId;
    }
    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
    public String getAccountId() {
        return this.accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
    @Generated(hash = 496845812)
    public SmsParseObject(Long id, String number, String incomeWords,
            String expenseWords, String amountWords, String accountId,
            String currencyId, int type) {
        this.id = id;
        this.number = number;
        this.incomeWords = incomeWords;
        this.expenseWords = expenseWords;
        this.amountWords = amountWords;
        this.accountId = accountId;
        this.currencyId = currencyId;
        this.type = type;
    }
    @Generated(hash = 1140750388)
    public SmsParseObject() {
    }

}
