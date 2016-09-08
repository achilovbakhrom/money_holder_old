package com.jim.pocketaccounter.modulesandcomponents.components;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.credit.AdapterCridetArchive;
import com.jim.pocketaccounter.debt.AddBorrowFragment;
import com.jim.pocketaccounter.debt.BorrowFragment;
import com.jim.pocketaccounter.debt.DebtBorrowFragment;
import com.jim.pocketaccounter.debt.InfoDebtBorrowFragment;
import com.jim.pocketaccounter.finance.CurrencyAdapter;
import com.jim.pocketaccounter.finance.CurrencyExchangeAdapter;
import com.jim.pocketaccounter.finance.TransferAccountAdapter;
import com.jim.pocketaccounter.fragments.AccountEditFragment;
import com.jim.pocketaccounter.fragments.AccountFragment;
import com.jim.pocketaccounter.fragments.AccountInfoFragment;
import com.jim.pocketaccounter.fragments.CategoryFragment;
import com.jim.pocketaccounter.fragments.CategoryInfoFragment;
import com.jim.pocketaccounter.fragments.CurrencyChooseFragment;
import com.jim.pocketaccounter.fragments.CurrencyEditFragment;
import com.jim.pocketaccounter.fragments.CurrencyFragment;
import com.jim.pocketaccounter.fragments.PurposeEditFragment;
import com.jim.pocketaccounter.fragments.PurposeFragment;
import com.jim.pocketaccounter.fragments.PurposeInfoFragment;
import com.jim.pocketaccounter.fragments.RootCategoryEditFragment;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.SettingsManager;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterActivityModule;
import com.jim.pocketaccounter.utils.SubCatAddEditDialog;
import com.jim.pocketaccounter.utils.TransferDialog;
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
    void inject(CategoryFragment categoryFragment);
    void inject(AccountInfoFragment accountInfoFragment);
    void inject(SubCatAddEditDialog subCatAddEditDialog);
    void inject(RootCategoryEditFragment rootCategoryEditFragment);
    void inject(CategoryInfoFragment categoryInfoFragment);
    void inject(PurposeFragment purposeFragment);
    void inject(TransferDialog transferDialog);
    void inject(TransferAccountAdapter transferAccountAdapter);
    void inject(PurposeEditFragment purposeEditFragment);
    void inject(PurposeInfoFragment purposeInfoFragment);
    void inject(DebtBorrowFragment debtBorrowFragment);
    void inject(InfoDebtBorrowFragment infoDebtBorrowFragment);
    void inject(BorrowFragment borrowFragment);
    void inject(AddBorrowFragment addBorrowFragment);
    void inject(AdapterCridetArchive adapterCridetArchive);
}
