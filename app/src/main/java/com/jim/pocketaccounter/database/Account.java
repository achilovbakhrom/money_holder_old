package com.jim.pocketaccounter.database;

import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import dagger.Provides;

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
	@ToOne(joinProperty = "startMoneyCurrencyId")
	private Currency startMoneyCurrency;
	@Property
	private String startMoneyCurrencyId;
	@Property
	private boolean limited;
	@Property
	private double limitSum;
	@ToOne(joinProperty = "limitMoneyCurrencyId")
	private Currency limitCurrency;
	@Property
	private String limitMoneyCurrencyId;
	@Property
	private boolean limitInterval;
	@Property
	private boolean nonMinus;
	@Convert(converter = CalendarConvertor.class, columnType = Long.class)
	private Calendar calendar;
	@Convert(converter = CalendarConvertor.class, columnType = Long.class)
	private Calendar limitTime;
	@Convert(converter = CalendarConvertor.class, columnType = Long.class)
	private Calendar limitBeginTime;
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
	@Generated(hash = 1915325500)
	public void setLimitCurrency(Currency limitCurrency) {
		synchronized (this) {
			this.limitCurrency = limitCurrency;
			limitMoneyCurrencyId = limitCurrency == null ? null : limitCurrency.getId();
			limitCurrency__resolvedKey = limitMoneyCurrencyId;
		}
	}
	/** To-one relationship, resolved on first access. */
	@Generated(hash = 1987378384)
	public Currency getLimitCurrency() {
		String __key = this.limitMoneyCurrencyId;
		if (limitCurrency__resolvedKey == null || limitCurrency__resolvedKey != __key) {
			final DaoSession daoSession = this.daoSession;
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			CurrencyDao targetDao = daoSession.getCurrencyDao();
			Currency limitCurrencyNew = targetDao.load(__key);
			synchronized (this) {
				limitCurrency = limitCurrencyNew;
				limitCurrency__resolvedKey = __key;
			}
		}
		return limitCurrency;
	}
	@Generated(hash = 38267958)
	private transient String limitCurrency__resolvedKey;
	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 1165649871)
	public void setStartMoneyCurrency(Currency startMoneyCurrency) {
		synchronized (this) {
			this.startMoneyCurrency = startMoneyCurrency;
			startMoneyCurrencyId = startMoneyCurrency == null ? null : startMoneyCurrency
					.getId();
			startMoneyCurrency__resolvedKey = startMoneyCurrencyId;
		}
	}
	/** To-one relationship, resolved on first access. */
	@Generated(hash = 1596678324)
	public Currency getStartMoneyCurrency() {
		String __key = this.startMoneyCurrencyId;
		if (startMoneyCurrency__resolvedKey == null
				|| startMoneyCurrency__resolvedKey != __key) {
			final DaoSession daoSession = this.daoSession;
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			CurrencyDao targetDao = daoSession.getCurrencyDao();
			Currency startMoneyCurrencyNew = targetDao.load(__key);
			synchronized (this) {
				startMoneyCurrency = startMoneyCurrencyNew;
				startMoneyCurrency__resolvedKey = __key;
			}
		}
		return startMoneyCurrency;
	}
	@Generated(hash = 1719102162)
	private transient String startMoneyCurrency__resolvedKey;
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
	public Calendar getLimitBeginTime() {
		return this.limitBeginTime;
	}
	public void setLimitBeginTime(Calendar limitBeginTime) {
		this.limitBeginTime = limitBeginTime;
	}
	public Calendar getLimitTime() {
		return this.limitTime;
	}
	public void setLimitTime(Calendar limitTime) {
		this.limitTime = limitTime;
	}
	public Calendar getCalendar() {
		return this.calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	public boolean getNonMinus() {
		return this.nonMinus;
	}
	public void setNonMinus(boolean nonMinus) {
		this.nonMinus = nonMinus;
	}
	public boolean getLimitInterval() {
		return this.limitInterval;
	}
	public void setLimitInterval(boolean limitInterval) {
		this.limitInterval = limitInterval;
	}
	public String getLimitMoneyCurrencyId() {
		return this.limitMoneyCurrencyId;
	}
	public void setLimitMoneyCurrencyId(String limitMoneyCurrencyId) {
		this.limitMoneyCurrencyId = limitMoneyCurrencyId;
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
	public String getStartMoneyCurrencyId() {
		return this.startMoneyCurrencyId;
	}
	public void setStartMoneyCurrencyId(String startMoneyCurrencyId) {
		this.startMoneyCurrencyId = startMoneyCurrencyId;
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
	@Generated(hash = 204401447)
	public Account(String name, String id, String icon, double amount,
			String startMoneyCurrencyId, boolean limited, double limitSum,
			String limitMoneyCurrencyId, boolean limitInterval, boolean nonMinus,
			Calendar calendar, Calendar limitTime, Calendar limitBeginTime) {
		this.name = name;
		this.id = id;
		this.icon = icon;
		this.amount = amount;
		this.startMoneyCurrencyId = startMoneyCurrencyId;
		this.limited = limited;
		this.limitSum = limitSum;
		this.limitMoneyCurrencyId = limitMoneyCurrencyId;
		this.limitInterval = limitInterval;
		this.nonMinus = nonMinus;
		this.calendar = calendar;
		this.limitTime = limitTime;
		this.limitBeginTime = limitBeginTime;
	}
	@Generated(hash = 882125521)
	public Account() {
	}

}