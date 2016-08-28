package com.jim.pocketaccounter.modulesandcomponents.components;

import android.content.SharedPreferences;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DatabaseMigration;
import com.jim.pocketaccounter.utils.DataCache;
import com.jim.pocketaccounter.utils.SystemConfigurator;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterApplicationModule;

import dagger.Component;

/**
 * Created by DEV on 27.08.2016.
 */

@Component(modules = {PocketAccounterApplicationModule.class})
public interface PocketAccounterApplicationComponent {
    PocketAccounterApplication getPocketAccounterApplication();
    DaoSession getDaoSession();
    SharedPreferences getSharedPreferences();
    DataCache getDataCache();
    void inject(DatabaseMigration databaseMigration);
    void inject(PocketAccounterApplication pocketAccounterApplication);
    void inject(SystemConfigurator systemConfigurator);
}
