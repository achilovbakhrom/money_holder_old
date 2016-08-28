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

@Entity(nameInDb = "ACCOUNTS", active = true)
public class Account {
	@Property
	private String name;
	@Id
	@Property
	private String id;
	@Property
	private String icon;
	@Property
	private double amount;
	@ToOne
	private Currency startMoneyCurrency;
	@ToOne
	private Currency limitCurrency;
	@Property
	private boolean limited;
	@Property
	private double limitSum;
	@Property
	private boolean nonMinus;
	@Convert(converter = CalendarConvertor.class, columnType = Long.class)
	private Calendar calendar;
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
	@Generated(hash = 953800683)
	public void setLimitCurrency(Currency limitCurrency) {
		synchronized (this) {
			this.limitCurrency = limitCurrency;
			limitCurrency__refreshed = true;
		}
	}
	/** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
	@Generated(hash = 1435640176)
	public Currency peakLimitCurrency() {
		return limitCurrency;
	}
	/** To-one relationship, resolved on first access. */
	@Generated(hash = 1801110366)
	public Currency getLimitCurrency() {
		if (limitCurrency != null || !limitCurrency__refreshed) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			CurrencyDao targetDao = daoSession.getCurrencyDao();
			targetDao.refresh(limitCurrency);
			limitCurrency__refreshed = true;
		}
		return limitCurrency;
	}
	@Generated(hash = 652530661)
	private transient boolean limitCurrency__refreshed;
	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 1306502033)
	public void setStartMoneyCurrency(Currency startMoneyCurrency) {
		synchronized (this) {
			this.startMoneyCurrency = startMoneyCurrency;
			startMoneyCurrency__refreshed = true;
		}
	}
	/** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
	@Generated(hash = 1631773075)
	public Currency peakStartMoneyCurrency() {
		return startMoneyCurrency;
	}
	/** To-one relationship, resolved on first access. */
	@Generated(hash = 2006729364)
	public Currency getStartMoneyCurrency() {
		if (startMoneyCurrency != null || !startMoneyCurrency__refreshed) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			CurrencyDao targetDao = daoSession.getCurrencyDao();
			targetDao.refresh(startMoneyCurrency);
			startMoneyCurrency__refreshed = true;
		}
		return startMoneyCurrency;
	}
	@Generated(hash = 199401354)
	private transient boolean startMoneyCurrency__refreshed;
	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 1812283172)
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getAccountDao() : null;
	}
	/** Used for active entity operations. */
	@Generated(hash = 335469827)
	private transient AccountDao myDao;
	/** Used to resolve relations */
	@Generated(hash = 2040040024)
	private transient DaoSession daoSession;
	public Calendar getCalendar() {
		return this.calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	public double getLimitSum() {
		return this.limitSum;
	}
	public void setLimitSum(double limitSum) {
		this.limitSum = limitSum;
	}
	public boolean getLimited() {
		return this.limited;
	}
	public void setLimited(boolean limited) {
		this.limited = limited;
	}
	public double getAmount() {
		return this.amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getNonMinus() {
		return this.nonMinus;
	}
	public void setNonMinus(boolean nonMinus) {
		this.nonMinus = nonMinus;
	}
	@Generated(hash = 680489374)
	public Account(String name, String id, String icon, double amount, boolean limited,
			double limitSum, boolean nonMinus, Calendar calendar) {
		this.name = name;
		this.id = id;
		this.icon = icon;
		this.amount = amount;
		this.limited = limited;
		this.limitSum = limitSum;
		this.nonMinus = nonMinus;
		this.calendar = calendar;
	}
	@Generated(hash = 882125521)
	public Account() {
	}

}