package com.jim.pocketaccounter.debt;

import com.jim.pocketaccounter.fragments.AccountEditFragment;
import com.jim.pocketaccounter.fragments.AccountFragment;
import com.jim.pocketaccounter.fragments.AccountInfoFragment;
import com.jim.pocketaccounter.fragments.AddAutoMarketFragment;
import com.jim.pocketaccounter.fragments.AddCreditFragment;
import com.jim.pocketaccounter.fragments.AddSmsParseFragment;
import com.jim.pocketaccounter.fragments.AutoMarketFragment;
import com.jim.pocketaccounter.fragments.CategoryFragment;
import com.jim.pocketaccounter.fragments.CategoryInfoFragment;
import com.jim.pocketaccounter.fragments.CreditTabLay;
import com.jim.pocketaccounter.fragments.CurrencyChooseFragment;
import com.jim.pocketaccounter.fragments.CurrencyEditFragment;
import com.jim.pocketaccounter.fragments.CurrencyFragment;
import com.jim.pocketaccounter.fragments.InfoCreditFragment;
import com.jim.pocketaccounter.fragments.PurposeEditFragment;
import com.jim.pocketaccounter.fragments.PurposeFragment;
import com.jim.pocketaccounter.fragments.PurposeInfoFragment;
import com.jim.pocketaccounter.fragments.RecordDetailFragment;
import com.jim.pocketaccounter.fragments.RecordEditFragment;
import com.jim.pocketaccounter.fragments.ReportByAccountFragment;
import com.jim.pocketaccounter.fragments.ReportByCategory;
import com.jim.pocketaccounter.fragments.RootCategoryEditFragment;
import com.jim.pocketaccounter.fragments.SMSParseInfoFragment;
import com.jim.pocketaccounter.fragments.SearchFragment;
import com.jim.pocketaccounter.fragments.SmsParseMainFragment;

/**
 * Created by user on 6/16/2016.
 */

public interface PocketClassess {
    // Records
    String RECORD_EDIT_FRAGMENT = RecordEditFragment.class.getName();
    String RECORD_DETEIL_FRAGMENT = RecordDetailFragment.class.getName();

    // Currency
    String CURRENCY_FRAG = CurrencyFragment.class.getName();
    String CURRENCY_CHOOSE = CurrencyChooseFragment.class.getName();
    String CURRENCY_EDIT = CurrencyEditFragment.class.getName();

    // Category
    String CATEGORY_FRAG = CategoryFragment.class.getName();
    String CATEGORY_INFO = CategoryInfoFragment.class.getName();
    String ADD_CATEGORY = RootCategoryEditFragment.class.getName();

    // Account
    String ACCOUNT_FRAG = AccountFragment.class.getName();
    String ACCOUNT_EDIT = AccountEditFragment.class.getName();
    String ACCOUNT_INFO = AccountInfoFragment.class.getName();

    // Auto Market
    String AUTOMARKET_FRAG = AutoMarketFragment.class.getName();
    String ADD_AUTOMARKET = AddAutoMarketFragment.class.getName();

    // Credit
    String CREDIT_FRAG = CreditTabLay.class.getName();
    String INFO_CREDIT= InfoCreditFragment.class.getName();
    String ADD_CREDIT = AddCreditFragment.class.getName();

    // Debt - Borrow
    String DEBTBORROW_FRAG = DebtBorrowFragment.class.getName();
    String ADD_DEBTBORROW = AddBorrowFragment.class.getName();
    String INFO_DEBTBORROW = InfoDebtBorrowFragment.class.getName();

    // Purpose
    String PURPOSE_FRAG = PurposeFragment.class.getName();
    String INFO_PURPOSE = PurposeInfoFragment.class.getName();
    String ADD_PURPOSE = PurposeEditFragment.class.getName();

    // Report by Account
    String REPORT_ACCOUNT = ReportByAccountFragment.class.getName();
    String REPORT_CATEGORY = ReportByCategory.class.getName();

    // Sms Parsing
    String SMS_PARSE_FRAGMENT = SmsParseMainFragment.class.getName();
    String ADD_SMS_PARSE_FRAGMENT = AddSmsParseFragment.class.getName();
    String INFO_SMS_PARSE_FRAGMENT = SMSParseInfoFragment.class.getName();

    // Searching
    String SEARCH_FRAGMENT = SearchFragment.class.getName();
}
