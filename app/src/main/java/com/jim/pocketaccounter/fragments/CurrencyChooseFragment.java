package com.jim.pocketaccounter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.UserEnteredCalendars;
import com.jim.pocketaccounter.finance.CurrencyChooseAdapter;
import com.jim.pocketaccounter.utils.WarningDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CurrencyChooseFragment extends PABaseInfoFragment {
    private GridView gvCurrencyChoose;
    private ArrayList<Currency> currencies;
    private boolean[] chbs;
    private WarningDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currency_choose_fragment, container, false);
        dialog = new WarningDialog(getContext());
        toolbarManager.setTitle(getResources().getString(R.string.choose_currencies)); // toolbar settings
        toolbarManager.setSubtitle("");
        toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new CurrencyFragment());
            }
        });
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        gvCurrencyChoose = (GridView) view.findViewById(R.id.gvCurrencyChoose); // gridview for representing currencies

        final String[] baseCurrencies = getResources().getStringArray(R.array.base_currencies); // getting data from resources to creating default currency list
        final String[] baseAbbrs = getResources().getStringArray(R.array.base_abbrs);
        final String[] currIds = getResources().getStringArray(R.array.currency_ids);
        final String[] costs = getResources().getStringArray(R.array.currency_costs);
        chbs = new boolean[baseCurrencies.length];
        List<Currency> allCurrenciesFromDb = daoSession.getCurrencyDao().loadAll();
        for (int i = 0; i < currIds.length; i++) {
            boolean found = false;
            for (int j = 0; j < allCurrenciesFromDb.size(); j++) {
                if (currIds[i].matches(allCurrenciesFromDb.get(j).getId())) {
                    found = true;
                    break;
                }
            }
            chbs[i] = found;
        }
        currencies = new ArrayList<>();
        for (int i = 0; i < baseCurrencies.length; i++) {
            Currency currency = new Currency();
            currency.setAbbr(baseAbbrs[i]);
            currency.setName(baseCurrencies[i]);
            currency.setId(currIds[i]);
            currencies.add(currency);
        }
        CurrencyChooseAdapter adapter = new CurrencyChooseAdapter(getActivity(), currencies, chbs);
        gvCurrencyChoose.setAdapter(adapter);
        toolbarManager.setOnSecondImageClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = false; // must be at least on cell is checked, else show warning toast
                for (int i = 0; i < chbs.length; i++) {
                    if (chbs[i]) {
                        checked = true;
                        break;
                    }
                }
                if (!checked) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.curr_not_choosen), Toast.LENGTH_SHORT).show(); // toast for denying
                    return;
                }
                final List<Currency> checkedCurrencies = new ArrayList<>(); // accumulating all checked cell from gridView
                for (int i=0; i < chbs.length; i++) {
                    if (chbs[i]) {
                        checkedCurrencies.add(currencies.get(i));
                    }
                }
                boolean isCurrencyListChanged = false; // checking for the some of an old currency is not checked
                final List<Currency> dbCurrencies = daoSession.getCurrencyDao().loadAll();
                for (Currency currency : dbCurrencies) {
                    boolean found = false;
                    for (Currency curr : checkedCurrencies) {
                        if (curr.getId().equals(currency.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        isCurrencyListChanged = true;
                        break;
                    }
                }
                if (isCurrencyListChanged) { // if has not checked some of an old currencies
                    dialog.setText(getResources().getString(R.string.currency_exchange_warning));
                    dialog.setOnYesButtonListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (Currency currency : dbCurrencies) {
                                boolean found = false;
                                for (Currency curr : checkedCurrencies) {
                                    if (curr.getId().equals(currency.getId())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    List<Currency> currencies = new ArrayList<>();
                                    currencies.add(currency);
                                    logicManager.deleteCurrency(currencies);
                                }
                            }
                            for (Currency currency : checkedCurrencies) {
                                boolean found = false;
                                List<Currency> dbCurrs = daoSession.getCurrencyDao().loadAll();
                                for (Currency curr : dbCurrs) {
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
                                    logicManager.generateCurrencyCosts(Calendar.getInstance(), Double.parseDouble(costs[pos]), currency);
                                }
                            }
                            dialog.dismiss();
                            paFragmentManager.getFragmentManager().popBackStack();
                            paFragmentManager.displayFragment(new CurrencyFragment());

                        }
                    });
                    dialog.setOnNoButtonClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else { // all old currencies are present
                    for (Currency currency : checkedCurrencies) {
                        boolean found = false;
                        List<Currency> dbCurrs = daoSession.getCurrencyDao().loadAll();
                        for (Currency curr : dbCurrs) {
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
                            logicManager.generateCurrencyCosts(Calendar.getInstance(), Double.parseDouble(costs[pos]), currency);
                        }
                    }
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new CurrencyFragment());
            }
            }
        });
        return view;
    }

    @Override
    void refreshList() {

    }
}