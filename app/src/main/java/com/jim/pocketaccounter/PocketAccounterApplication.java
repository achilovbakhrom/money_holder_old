package com.jim.pocketaccounter;

import android.app.Application;
import android.content.SharedPreferences;

import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.utils.cache.DataCache;
import com.jim.pocketaccounter.utils.SystemConfigurator;
import com.jim.pocketaccounter.modulesandcomponents.components.DaggerPocketAccounterApplicationComponent;
import com.jim.pocketaccounter.modulesandcomponents.components.PocketAccounterApplicationComponent;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterApplicationModule;

import javax.inject.Inject;

/**
 * Created by DEV on 27.08.2016.
 */

public class PocketAccounterApplication extends Application {
    private PocketAccounterApplicationComponent pocketAccounterApplicationComponent;
     boolean keyIsMegraded = false;
    @Inject
    DaoSession daoSession;
    @Inject
    DataCache dataCache;
    @Inject
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "robotoRegular.ttf");
        pocketAccounterApplicationComponent = DaggerPocketAccounterApplicationComponent
                .builder()
                .pocketAccounterApplicationModule(new PocketAccounterApplicationModule(this))
                .build();
        pocketAccounterApplicationComponent.inject(this);
        keyIsMegraded=sharedPreferences.getBoolean("migrated",false);
        if(!keyIsMegraded){
            try {
                SystemConfigurator configurator = new SystemConfigurator(this);
                configurator.configurate();
                keyIsMegraded=true;
            }
            catch (Exception o){
                o.printStackTrace();
                //TODO  MEGRATSIYADAN OWIBKANI OBRABOTKASI
            }
        }

    }
    public PocketAccounterApplicationComponent component() {
        return pocketAccounterApplicationComponent;
    }
}
