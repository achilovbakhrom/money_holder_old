package com.jim.pocketaccounter.modulesandcomponents.components;

import android.content.SharedPreferences;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DatabaseMigration;
import com.jim.pocketaccounter.finance.CurrencyChooseAdapter;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.utils.DataCache;
import com.jim.pocketaccounter.utils.SystemConfigurator;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterApplicationModule;
import com.jim.pocketaccounter.utils.record.RecordButtonExpanse;
import com.jim.pocketaccounter.utils.record.RecordButtonIncome;
import com.jim.pocketaccounter.utils.record.RecordExpanseView;
import com.jim.pocketaccounter.utils.record.RecordIncomesView;

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
    CommonOperations getCommonOperations();
    ReportManager reportManager();
    void inject(DatabaseMigration databaseMigration);
    void inject(PocketAccounterApplication pocketAccounterApplication);
    void inject(SystemConfigurator systemConfigurator);
    void inject(RecordButtonExpanse recordButtonExpense);
    void inject(RecordButtonIncome recordButtonIncome);
    void inject(RecordIncomesView recordIncomesView);
    void inject(RecordExpanseView recordExpanseView);
    void inject(CurrencyChooseAdapter currencyChooseAdapter);
    void inject(ReportManager reportManager);
    void inject(CommonOperations commonOperations);
    void inject(LogicManager logicManager);
}
