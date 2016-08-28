package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
@Entity(nameInDb = "PERSONS", active = true)
public class Person {
    @Id(autoincrement = true)
    private Long id;
    @Property
    private String name;
    @Property
    private String phoneNumber;
    @Property
    private String photo;
    /** Used for active entity operations. */
    @Generated(hash = 778611619)
    private transient PersonDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    public Person(String name, String phoneNumber, String photo) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }
    public Person() {}
    @Generated(hash = 2072197380)
    public Person(Long id, String name, String phoneNumber, String photo) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPhoneNumber() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public String getPhoto() {return photo;}
    public void setPhoto(String photo) {this.photo = photo;}
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
    @Generated(hash = 2056799268)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPersonDao() : null;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}