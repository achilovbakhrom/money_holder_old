package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;


@Entity(nameInDb = "CURRENCIES", active = true)
public class Currency {
	@Property
	private String name;
	@Property
	private String abbr;
	@Id
	@Property
	private String id;
	@Property
	private boolean isMain = false;
	@ToMany(referencedJoinProperty = "currencyId")
	@NotNull
	private List<CurrencyCost> costs;
	/** Used for active entity operations. */
	@Generated(hash = 1033120508)
	private transient CurrencyDao myDao;
	/** Used to resolve relations */
	@Generated(hash = 2040040024)
	private transient DaoSession daoSession;
	@Generated(hash = 2048053614)
	public Currency(String name, String abbr, String id, boolean isMain) {
		this.name = name;
		this.abbr = abbr;
		this.id = id;
		this.isMain = isMain;
	}
	@Generated(hash = 1387171739)
	public Currency() {
	}

	public Currency(String currency_name) {
		this.name = currency_name;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	} 
	public boolean getMain() {
		return isMain;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String getAbbr() {
		return this.abbr;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	@Generated(hash = 443003054)
	public synchronized void resetCosts() {
		costs = null;
	}
	/**
	 * To-many relationship, resolved on first access (and after reset).
	 * Changes to to-many relations are not persisted, make changes to the target entity.
	 */
	@Generated(hash = 808286883)
	public List<CurrencyCost> getCosts() {
	    if (costs == null) {
	        final DaoSession daoSession = this.daoSession;
	        if (daoSession == null) {
	            throw new DaoException("Entity is detached from DAO context");
	        }
	        CurrencyCostDao targetDao = daoSession.getCurrencyCostDao();
	        List<CurrencyCost> costsNew = targetDao._queryCurrency_Costs(id);
	        synchronized (this) {
	            if(costs == null) {
	                costs = costsNew;
	            }
	        }
	    }
	    return costs;
	}

	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 869658167)
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getCurrencyDao() : null;
	}
	public boolean getIsMain() {
		return this.isMain;
	}
	public void setIsMain(boolean isMain) {
		this.isMain = isMain;
	}
	public void setCosts(List<CurrencyCost> currencyCosts) {
		this.costs = currencyCosts;
	}
	public List<CurrencyCost> getCurrencyCosts() {
		return costs;
	}
}
