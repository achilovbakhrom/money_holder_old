package com.jim.pocketaccounter.modulesandcomponents.modules;


import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.managers.PAFragmentManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by DEV on 27.08.2016.
 */
@Module
public class PocketAccounterActivityModule {
    private PAFragmentManager paFragmentManager;
    private PocketAccounter pocketAccounter;
    public PocketAccounterActivityModule(PocketAccounter pocketAccounter) {
        this.pocketAccounter = pocketAccounter;
    }
    @Provides
    public PAFragmentManager getPaFragmentManager() {
        paFragmentManager = new PAFragmentManager(pocketAccounter);
        return paFragmentManager;
    }
}
