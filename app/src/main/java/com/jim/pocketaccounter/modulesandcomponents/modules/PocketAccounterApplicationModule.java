package com.jim.pocketaccounter.modulesandcomponents.modules;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.DaoMaster;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DatabaseMigration;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.utils.DataCache;

import org.greenrobot.greendao.database.Database;

import dagger.Module;
import dagger.Provides;

/**
 * Created by DEV on 27.08.2016.
 */
@Module
public class PocketAccounterApplicationModule {
    private PocketAccounterApplication pocketAccounterApplication;
    private DaoSession daoSession;
    private DataCache dataCache;
    private SharedPreferences preferences;

    public PocketAccounterApplicationModule(PocketAccounterApplication pocketAccounterApplication) {
        this.pocketAccounterApplication = pocketAccounterApplication;
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(pocketAccounterApplication, "notes-db-encrypted");
        DaoMaster.DevOpenHelper helper = new DatabaseMigration(pocketAccounterApplication, "pocketaccounter-db");
        Database db = helper.getEncryptedWritableDb("super-secret");
        daoSession = new DaoMaster(db).newSession();
        preferences = PreferenceManager.getDefaultSharedPreferences(pocketAccounterApplication);

    }

    @Provides
    public PocketAccounterApplication getPocketAccounterApplication() {
        return pocketAccounterApplication;
    }

    @Provides
    public DaoSession getDaoSession() {
        if (daoSession == null) {
//            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(pocketAccounterApplication, "notes-db-encrypted");
            DaoMaster.DevOpenHelper helper = new DatabaseMigration(pocketAccounterApplication, "pocketaccounter-db");
            Database db = helper.getEncryptedWritableDb("super-secret");
            daoSession = new DaoMaster(db).newSession();
        }
        return daoSession;
    }

    @Provides
    public DataCache getDataCache() {
        if (dataCache == null)
            dataCache = new DataCache();
        return dataCache;
    }

    @Provides
    public SharedPreferences getSharedPreferences() {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(pocketAccounterApplication);
        return preferences;
    }

    @Provides
    public ReportManager reportManager() {
        return new ReportManager(pocketAccounterApplication);
    }

    @Provides
    public CommonOperations getCommonOperations() {
        return new CommonOperations(pocketAccounterApplication);
    }
}
