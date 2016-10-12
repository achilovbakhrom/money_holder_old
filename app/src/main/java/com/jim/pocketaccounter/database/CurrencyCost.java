package com.jim.pocketaccounter.database;

import com.jim.pocketaccounter.database.convertors.CalendarConvertor;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.jim.pocketaccounter.database.DaoSession;
@Entity(nameInDb = "CURRENCY_COSTS", active = true)
public class CurrencyCost {
	@Id(autoincrement = true)
	private Long id;
	@Property
	private double cost;
	@Property
	private String currencyId;
	@Convert(converter = CalendarConvertor.class, columnType = String.class)
	private Calendar day;
	/** Used for active entity operations. */
	@Generated(hash = 1159184481)
	private transient CurrencyCostDao myDao;
	/** Used to resolve relations */
	@Generated(hash = 2040040024)
	private transient DaoSession daoSession;
	@Generated(hash = 2002156964)
	public CurrencyCost(Long id, double cost, String currencyId, Calendar day) {
		this.id = id;
		this.cost = cost;
		this.currencyId = currencyId;
		this.day = day;
	}
	@Generated(hash = 205073812)
	public CurrencyCost() {
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public Calendar getDay() {
		return day;
	}
	public void setDay(Calendar day) {
		this.day = (Calendar) day.clone();
	}
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
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
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 184881022)
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getCurrencyCostDao() : null;
	}
}

