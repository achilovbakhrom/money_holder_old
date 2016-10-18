package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyCost;
import com.jim.pocketaccounter.database.CurrencyCostState;
import com.jim.pocketaccounter.database.CurrencyCostStateDao;
import com.jim.pocketaccounter.database.CurrencyWithAmount;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.UserEnteredCalendars;
import com.jim.pocketaccounter.database.UserEnteredCalendarsDao;
import com.jim.pocketaccounter.finance.CurrencyExchangeAdapter;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

@SuppressLint("ValidFragment")
public class CurrencyEditFragment extends Fragment implements OnClickListener, OnItemClickListener {
    private ImageView ivExCurrencyAdd, ivExCurrencyDelete;
    private ListView lvCurrencyEditExchange;
    private Currency currency;
    private CheckBox chbCurrencyEditMainCurrency;
    private Calendar day = Calendar.getInstance();
    private int mode = PocketAccounterGeneral.NORMAL_MODE;
    private boolean[] selected;
    @Inject PAFragmentManager paFragmentManager;
    @Inject DaoSession daoSession;
    @Inject LogicManager logicManager;
    @Inject ToolbarManager toolbarManager;
    WarningDialog dialog;
    @Inject CommonOperations commonOperations;

    public CurrencyEditFragment(Currency currency) {
        this.currency = currency;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.currency_edit, container, false);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        dialog = new WarningDialog(getContext());
        ivExCurrencyAdd = (ImageView) rootView.findViewById(R.id.ivExCurrencyAdd);
        ivExCurrencyAdd.setOnClickListener(this);
        ivExCurrencyDelete = (ImageView) rootView.findViewById(R.id.ivExCurrencyDelete);
        ivExCurrencyDelete.setOnClickListener(this);
        lvCurrencyEditExchange = (ListView) rootView.findViewById(R.id.lvCurrencyEditExchange);
        lvCurrencyEditExchange.setOnItemClickListener(this);
        chbCurrencyEditMainCurrency = (CheckBox) rootView.findViewById(R.id.chbCurrencyEditMainCurrency);
        chbCurrencyEditMainCurrency.setChecked(currency.getMain());
        toolbarManager.setImageToHomeButton(R.drawable.ic_back_button);
        toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new CurrencyFragment());
            }
        });
        toolbarManager.setTitle(currency.getName());
        toolbarManager.setSubtitle(getResources().getString(R.string.edit));
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setSpinnerVisibility(View.GONE);
        toolbarManager.setOnSecondImageClickListener(this);
        refreshExchangeList();
        return rootView;
    }

    private void refreshExchangeList() {
        currency.resetUserEnteredCalendarses();
        CurrencyExchangeAdapter adapter = new CurrencyExchangeAdapter(getActivity(),
                (ArrayList<CurrencyCost>) currency.getCosts(), selected, mode, currency.getAbbr());
        lvCurrencyEditExchange.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mode == PocketAccounterGeneral.NORMAL_MODE)
            exchangeEditDialog(currency.getCosts().get(position));
        else {
            if (view != null) {
                CheckBox chbCurrencyExchangeListItem = (CheckBox) view.findViewById(R.id.chbCurrencyExchangeListItem);
                chbCurrencyExchangeListItem.setChecked(!chbCurrencyExchangeListItem.isChecked());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivExCurrencyAdd:
                exchangeEditDialog(null);
                break;
            case R.id.ivExCurrencyDelete:
                if (mode == PocketAccounterGeneral.NORMAL_MODE) {
                    selected = new boolean[currency.getCosts().size()];
                    mode = PocketAccounterGeneral.EDIT_MODE;
                    ivExCurrencyDelete.setImageDrawable(null);
                    ivExCurrencyDelete.setImageResource(R.drawable.ic_cat_trash);
                } else {
                    mode = PocketAccounterGeneral.NORMAL_MODE;
                    ivExCurrencyDelete.setImageDrawable(null);
                    ivExCurrencyDelete.setImageResource(R.drawable.subcat_delete);
                    deleteCosts();
                    selected = null;
                }
                refreshExchangeList();
                break;
            case R.id.ivToolbarMostRight:
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (chbCurrencyEditMainCurrency.isChecked()) {
                    logicManager.setMainCurrency(currency);
                    paFragmentManager.updateCurrencyChanges();
                }
                paFragmentManager.displayFragment(new CurrencyFragment());
                break;
        }
    }

    private void exchangeEditDialog(final CurrencyCost currCost) {
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.exchange_edit, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        final TextView tvExchangeEditDate = (TextView) dialogView.findViewById(R.id.tvExchangeEditDate);
        tvExchangeEditDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.dp);
                ImageView ivDatePickOk = (ImageView) dialogView.findViewById(R.id.ivDatePickOk);
                ivDatePickOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        day.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                        tvExchangeEditDate.setText(format.format(day.getTime()));
                        dialog.dismiss();
                    }
                });
                ImageView ivDatePickCancel = (ImageView) dialogView.findViewById(R.id.ivDatePickCancel);
                ivDatePickCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        final EditText etExchange = (EditText) dialogView.findViewById(R.id.etExchange);
        final TextView glava = (TextView) dialogView.findViewById(R.id.glava);
        glava.setText(1 + currency.getAbbr() + "  = ");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        final DecimalFormat decFormat = new DecimalFormat("0.00##", otherSymbols);
        etExchange.setText(decFormat.format(0.0));
        double cost = 1.0;
        if (currCost != null) {
            tvExchangeEditDate.setText(dateFormat.format(currCost.getDay().getTime()));
            day = (Calendar) currCost.getDay().clone();
            cost = currCost.getCost();
        }
        tvExchangeEditDate.setText(dateFormat.format(day.getTime()));
        etExchange.setText(decFormat.format(cost));
        ImageView ivCurrencyEditDialogOk = (ImageView) dialogView.findViewById(R.id.ivCurrencyEditDialogOk);
        ImageView ivCurrencyEditDialogCancel = (ImageView) dialogView.findViewById(R.id.ivCurrencyEditDialogCancel);
        ivCurrencyEditDialogOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etExchange.getText().toString().matches("") || Double.parseDouble(etExchange.getText().toString()) == 0) {
                    etExchange.setError(getString(R.string.incorrect_value));
                    return;
                }
                if (logicManager.insertUserEnteredCalendars(currency, (Calendar)day.clone()) == LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS) {
                    logicManager.updateGenerateDefinetilyCurrentDay((Calendar)day.clone(), Double.parseDouble(etExchange.getText().toString()), currency);
                }
                else {
                    logicManager.generateForDefinetilyCurrentDay((Calendar)day.clone(), Double.parseDouble(etExchange.getText().toString()), currency);
                }
                refreshExchangeList();
                dialog.dismiss();
            }
        });
        ivCurrencyEditDialogCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(7 * width / 8, LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void deleteCosts() {
        List<UserEnteredCalendars> currencyCostList = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                currencyCostList.add(currency.getUserEnteredCalendarses().get(i));
            }
        }
        if (currencyCostList.isEmpty() || currencyCostList == null) return;
        if (logicManager.deleteCurrencyCosts(currencyCostList, currency) == LogicManagerConstants.LIST_IS_EMPTY)
            Toast.makeText(getActivity(), getResources().getString(R.string.costs_selected_all_warning), Toast.LENGTH_SHORT).show();
        refreshExchangeList();
    }
}