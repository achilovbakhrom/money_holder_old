package com.jim.pocketaccounter.modulesandcomponents.components;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterActivityModule;

import dagger.Component;

/**
 * Created by DEV on 27.08.2016.
 */
@Component(
        modules = {PocketAccounterActivityModule.class},
        dependencies = {PocketAccounterApplicationComponent.class}
)
public interface PocketAccounterActivityComponent {
    public void inject(PocketAccounter pocketAccounter);
}
