package com.jim.pocketaccounter.database;

import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "DEBT_BORROWS", active = true)
public class DebtBorrow {
    @ToOne
    private Person person;
    @Convert(converter = CalendarConvertor.class, columnType = Long.class)
    private Calendar takenDate;
    @Convert(converter = CalendarConvertor.class, columnType = Long.class)
    private Calendar returnDate;
    @Property
    private int type;
    @ToOne
    private Account account;
    @ToOne
    private Currency currency;
    @Property
    private boolean calculate;
    @Property
    private boolean to_archive = false;
    @Property
    private double amount;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "debtBorrowsId")
    })
    private List<Recking> reckings;
    public static final int DEBT = 1, BORROW = 0;
    private String info = "";


    private String id; //"debt_"+UUID.randowUUID().toString();
    @Generated(hash = 131953365)
    private transient boolean currency__refreshed;
    @Generated(hash = 1833446427)
    private transient boolean account__refreshed;
    @Generated(hash = 1689085377)
    private transient boolean person__refreshed;
    /** Used for active entity operations. */
    @Generated(hash = 1007809152)
    private transient DebtBorrowDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DebtBorrow(Person person, Calendar takenDate, Calendar returnDate,
                      String id, Account account, Currency currency,
                      double amount, int type, boolean calculate) {
        this.person = person;
        this.takenDate = takenDate;
        this.returnDate = returnDate;
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        reckings = new ArrayList<>();
        this.type = type;
        this.id = id;
        this.calculate = calculate;
    }

    public DebtBorrow(Person person, Calendar takenDate,
                      String id, Account account, Currency currency,
                      double amount, int type, boolean calculate) {
        this.person = person;
        this.takenDate = takenDate;
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        reckings = new ArrayList<>();
        this.type = type;
        this.id = id;
        this.calculate = calculate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DebtBorrow() {}
    public void setReckings(List<Recking> reckings) {
        this.reckings = reckings;
    }
    @Generated(hash = 325674475)
    public DebtBorrow(Calendar takenDate, Calendar returnDate, int type, boolean calculate,
            boolean to_archive, double amount, String info, String id) {
        this.takenDate = takenDate;
        this.returnDate = returnDate;
        this.type = type;
        this.calculate = calculate;
        this.to_archive = to_archive;
        this.amount = amount;
        this.info = info;
        this.id = id;
    }
    public Calendar getTakenDate() {return takenDate;}
    public void setTakenDate(Calendar takenDate) {this.takenDate = (Calendar)takenDate.clone();}
    public Calendar getReturnDate() {return returnDate;}
    public void setReturnDate(Calendar returnDate) {this.returnDate = returnDate;}
    public int getType() {
        return type;
    }
    public boolean isTo_archive() {return to_archive;}
    public void setTo_archive(boolean to_archive) {this.to_archive = to_archive;}
    public void setType(int type) {
        this.type = type;
    }
    public boolean isCalculate() {
        return calculate;
    }
    public void setCalculate(boolean calculate) {
        this.calculate = calculate;
    }
    public void addRecking(Recking recking) {
        reckings.add(recking);
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

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1034520580)
    public synchronized void resetReckings() {
        reckings = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1906484870)
    public List<Recking> getReckings() {
        if (reckings == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReckingDao targetDao = daoSession.getReckingDao();
            List<Recking> reckingsNew = targetDao._queryDebtBorrow_Reckings(id);
            synchronized (this) {
                if(reckings == null) {
                    reckings = reckingsNew;
                }
            }
        }
        return reckings;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1432851435)
    public void setPerson(Person person) {
        synchronized (this) {
            this.person = person;
            person__refreshed = true;
        }
    }

    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 1134129394)
    public Person peakPerson() {
        return person;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1413817282)
    public Person getPerson() {
        if (person != null || !person__refreshed) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonDao targetDao = daoSession.getPersonDao();
            targetDao.refresh(person);
            person__refreshed = true;
        }
        return person;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 861691792)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDebtBorrowDao() : null;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean getTo_archive() {
        return this.to_archive;
    }

    public boolean getCalculate() {
        return this.calculate;
    }
}