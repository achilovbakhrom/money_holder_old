package com.jim.pocketaccounter.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;

import java.util.UUID;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by root on 9/29/16.
 */
@Entity
public class SmsParseKeys {
    @Property
    private String nameKey;
    @Property
    private int type;
    @Property
    @Id
    private String id;
    @Keep
    public SmsParseKeys () {
        id = UUID.randomUUID().toString();
    }
    @Keep
    public SmsParseKeys(String nameKey) {
        id = UUID.randomUUID().toString();
        this.nameKey = nameKey;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getNameKey() {
        return this.nameKey;
    }
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }
    @Generated(hash = 1172767148)
    public SmsParseKeys(String nameKey, int type, String id) {
        this.nameKey = nameKey;
        this.type = type;
        this.id = id;
    }

}
