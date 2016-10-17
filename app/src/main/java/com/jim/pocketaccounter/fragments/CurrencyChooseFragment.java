package com.jim.pocketaccounter.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.UserEnteredCalendars;
import com.jim.pocketaccounter.finance.CurrencyChooseAdapter;
import com.jim.pocketaccounter.database.CurrencyCost;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.WarningDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class CurrencyChooseFragment extends Fragment {
    private GridView gvCurrencyChoose;
    private ArrayList<Currency> currencies;
    private boolean[] chbs;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    LogicManager logicManager;
    @Inject
    DaoSession daoSession;
    @Inject
    ToolbarManager toolbarManager;
    WarningDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currency_choose_fragment, container, false);
        dialog = new WarningDialog(getContext());
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        toolbarManager.setTitle(getResources().getString(R.string.choose_currencies));
        toolbarManager.setSubtitle("");
        toolbarManager.setImageToHomeButton(R.drawable.ic_back_button);
        toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new CurrencyFragment());
            }
        });
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setSpinnerVisibility(View.GONE);
        gvCurrencyChoose = (GridView) view.findViewById(R.id.gvCurrencyChoose);
        final String[] baseCurrencies = getResources().getStringArray(R.array.base_currencies);
        final String[] baseAbbrs = getResources().getStringArray(R.array.base_abbrs);
        final String[] currIds = getResources().getStringArray(R.array.currency_ids);
        final String[] costs = getResources().getStringArray(R.array.currency_costs);
        chbs = new boolean[baseCurrencies.length];
        for (int i = 0; i < currIds.length; i++) {
            boolean found = false;
            for (int j = 0; j < daoSession.getCurrencyDao().loadAll().size(); j++) {
                if (currIds[i].matches(daoSession.getCurrencyDao().loadAll().get(j).getId())) {
                    found = true;
                    break;
                }
            }
            chbs[i] = found;
        }
        currencies = new ArrayList<>();
        for (int i = 0; i < baseCurrencies.length; i++) {
            Currency currency = new Currency();
//            currenc
            currency.setAbbr(baseAbbrs[i]);
            currency.setName(baseCurrencies[i]);
            currency.setId(currIds[i]);
//            CurrencyCost cost = new CurrencyCost();
//            cost.setCost(Double.parseDouble(costs[i]));
//            cost.setDay(Calendar.getInstance());
//            ArrayList<CurrencyCost> tempCost = new ArrayList<CurrencyCost>();
//            tempCost.add(cost);
//            currency.setCosts(tempCost);
            currencies.add(currency);
        }
        CurrencyChooseAdapter adapter = new CurrencyChooseAdapter(getActivity(), currencies, chbs);
        gvCurrencyChoose.setAdapter(adapter);
        toolbarManager.setOnSecondImageClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = false;
                for (int i = 0; i < chbs.length; i++) {
                    if (chbs[i]) {
                        checked = true;
                        break;
                    }
                }
                if (!checked) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.curr_not_choosen), Toast.LENGTH_SHORT).show();
                    return;
                }
                final List<Currency> checkedCurrencies = new ArrayList<>();
                for (int i=0; i < chbs.length; i++) {
                    if (chbs[i]) {
                        checkedCurrencies.add(currencies.get(i));
                    }
                }
                boolean isCurrencyListChanged = false;
                for (Currency currency : daoSession.getCurrencyDao().loadAll()) {
                    for (Currency curr : checkedCurrencies) {
                        if (curr.getId().matches(currency.getId())) {
                            isCurrencyListChanged = true;
                            break;
                        }
                    }
                }
                if (isCurrencyListChanged) {
                    dialog.setText(getResources().getString(R.string.currency_exchange_warning));
                    dialog.setOnYesButtonListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (Currency currency : daoSession.getCurrencyDao().loadAll()) {
                                boolean found = false;
                                for (Currency curr : checkedCurrencies) {
                                    if (curr.getId().matches(currency.getId())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) {
                                    List<Currency> currencies = new ArrayList<>();
                                    currencies.add(currency);
                                    logicManager.deleteCurrency(currencies);
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.setOnNoButtonClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                for (Currency currency : checkedCurrencies) {

                    boolean found = false;
                    for (Currency curr : daoSession.getCurrencyDao().loadAll()) {
                        if (currency.getId().matches(curr.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        int pos = 0;
                        for (int i=0; i<currencies.size(); i++) {
                            if (currency.getId().equals(currencies.get(i).getId())) {
                                pos = i;
                                break;
                            }
                        }
                        UserEnteredCalendars userEnteredCalendars = new UserEnteredCalendars();
                        userEnteredCalendars.setCurrencyId(currency.getId());
                        userEnteredCalendars.setCalendar(Calendar.getInstance());
                        daoSession.getUserEnteredCalendarsDao().insertOrReplace(userEnteredCalendars);
                        daoSession.getCurrencyDao().insertOrReplace(currency);
                        logicManager.generateWhenAddingNewCurrency(Calendar.getInstance(), Double.parseDouble(costs[pos]), currency);
                    }
                }
                paFragmentManager.displayFragment(new CurrencyFragment());
            }
        });
        return view;
    }
}