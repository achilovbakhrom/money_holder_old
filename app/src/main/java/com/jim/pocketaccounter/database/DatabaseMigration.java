package com.jim.pocketaccounter.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.jim.pocketaccounter.utils.DataCache;

import org.greenrobot.greendao.database.Database;

import javax.inject.Inject;

/**
 * Created by DEV on 27.08.2016.
 */

public class DatabaseMigration extends DaoMaster.DevOpenHelper{

    private Context context;
    private final String OLD_DB_NAME = "PocketAccounterDatabase";
    @Inject
    DataCache dataCache;
    @Inject
    SharedPreferences preferences;
    public DatabaseMigration(Context context, String name) {
        super(context, name);
        this.context = context;
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);

    }
}
