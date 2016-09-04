package com.jim.pocketaccounter.modulesandcomponents.components;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.finance.CurrencyAdapter;
import com.jim.pocketaccounter.finance.CurrencyExchangeAdapter;
import com.jim.pocketaccounter.fragments.AccountEditFragment;
import com.jim.pocketaccounter.fragments.AccountFragment;
import com.jim.pocketaccounter.fragments.CurrencyChooseFragment;
import com.jim.pocketaccounter.fragments.CurrencyEditFragment;
import com.jim.pocketaccounter.fragments.CurrencyFragment;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.SettingsManager;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterActivityModule;
import com.jim.pocketaccounter.utils.record.RecordExpanseView;

import dagger.Component;

/**
 * Created by DEV on 27.08.2016.
 */
@Component(
        modules = {PocketAccounterActivityModule.class},
        dependencies = {PocketAccounterApplicationComponent.class}
)
public interface PocketAccounterActivityComponent {
    void inject(PocketAccounter pocketAccounter);
    void inject(SettingsManager settingsManager);
    void inject(CurrencyFragment currencyFragment);
    void inject(CurrencyAdapter currencyAdapter);
    void inject(CurrencyChooseFragment currencyChooseFragment);
    void inject(CurrencyEditFragment currencyEditFragment);
    void inject(CurrencyExchangeAdapter currencyExchangeAdapter);
    void inject(AccountFragment accountFragment);
    void inject(AccountEditFragment accountEditFragment);
}
