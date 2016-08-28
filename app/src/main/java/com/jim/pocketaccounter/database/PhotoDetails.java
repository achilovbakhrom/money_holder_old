package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by DEV on 07.08.2016.
 */
@Entity(nameInDb = "PHOTO_DETAILS", active = true)
public class PhotoDetails {
    @Id
    @Property
    private Long id;
    @Property
    String photopath;
    @Property
    String photopathCache;
    @Property
    String recordId;
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
    @Generated(hash = 2103253507)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPhotoDetailsDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 1547580238)
    private transient PhotoDetailsDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    public String getRecordId() {
        return this.recordId;
    }
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    public String getPhotopathCache() {
        return this.photopathCache;
    }
    public void setPhotopathCache(String photopathCache) {
        this.photopathCache = photopathCache;
    }
    public String getPhotopath() {
        return this.photopath;
    }
    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 365682649)
    public PhotoDetails(Long id, String photopath, String photopathCache,
            String recordId) {
        this.id = id;
        this.photopath = photopath;
        this.photopathCache = photopathCache;
        this.recordId = recordId;
    }
    @Generated(hash = 1285452818)
    public PhotoDetails() {
    }

}
