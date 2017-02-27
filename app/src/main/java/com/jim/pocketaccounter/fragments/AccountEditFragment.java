package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.OnIconPickListener;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AccountEditFragment extends PABaseInfoFragment implements OnClickListener {
    private Account account;
    private EditText etAccountEditName;
    private FABIcon fabAccountIcon;
    private CheckBox chbAccountStartSumEnabled;
    private RelativeLayout rlStartSumContainer;
    private RelativeLayout rlStartLimitContainer;
    private EditText etStartMoney;
    private EditText etStartLimit;
    private Spinner spStartMoneyCurrency;
    private CheckBox chbAccountNoneZero;
    private CheckBox chbAccountEnabledLimit;
    private Spinner spStartLimit;
    private String choosenIcon = "icons_1";
    private TextView tvNoneMinusAccountTitle, tvStartSumAccountTitle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.account_edit_layout, container, false);
        if (getArguments() != null) {
            String accountId = getArguments().getString(AccountFragment.ACCOUNT_ID);
            if (accountId != null) {
                account = daoSession.load(Account.class, accountId);
            }
        }
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }
        }, 100);
        toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.getFragmentManager().popBackStack();
                paFragmentManager.displayFragment(new AccountFragment());
            }
        });
        toolbarManager.setTitle(getResources().getString(R.string.addedit));
        toolbarManager.setSubtitle("");
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setOnSecondImageClickListener(this);
        List<Currency> currencies = daoSession.getCurrencyDao().loadAll();
        String[] items = new String[currencies.size()];
        int mainCurrencyPos = 0;
        for (int i = 0; i < currencies.size(); i++) {
            if (currencies.get(i).getMain())
                mainCurrencyPos = i;
            items[i] = currencies.get(i).getAbbr();
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, items);
        etAccountEditName = (EditText) rootView.findViewById(R.id.etAccountEditName); // account name
        fabAccountIcon = (FABIcon) rootView.findViewById(R.id.fabAccountIcon); // icon chooser
        int resId = getResources().getIdentifier(choosenIcon, "drawable", getContext().getPackageName());
        Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
        Bitmap bitmap = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
                (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
        fabAccountIcon.setImageBitmap(bitmap);
        fabAccountIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final IconChooseDialog iconChooseDialog = new IconChooseDialog(getContext());
                if (account != null) iconChooseDialog.setSelectedIcon(account.getIcon());
                iconChooseDialog.setOnIconPickListener(new OnIconPickListener() {
                    @Override
                    public void OnIconPick(String icon) {
                        choosenIcon = icon;
                        int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
                        Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
                        Bitmap b = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
                                (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
                        fabAccountIcon.setImageBitmap(b);
                        iconChooseDialog.setSelectedIcon(icon);
                        iconChooseDialog.dismiss();
                    }
                });
                iconChooseDialog.show();
            }
        });
        chbAccountStartSumEnabled = (CheckBox) rootView.findViewById(R.id.chbAccountStartSumEnabled); // start sum
        rlStartLimitContainer = (RelativeLayout) rootView.findViewById(R.id.rlStartLimitContainer);
        chbAccountStartSumEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // for enabling and disabling start sum
                if (isChecked)
                    rlStartSumContainer.setVisibility(View.VISIBLE);
                else
                    rlStartSumContainer.setVisibility(View.GONE);
            }
        });
        chbAccountEnabledLimit = (CheckBox) rootView.findViewById(R.id.chbAccountEnabledLimit); // for enabling and disabling account limit
        chbAccountEnabledLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    rlStartLimitContainer.setVisibility(View.VISIBLE);
                else
                    rlStartLimitContainer.setVisibility(View.GONE);
            }
        });
        rlStartSumContainer = (RelativeLayout) rootView.findViewById(R.id.rlStartSumContainer);
        rlStartSumContainer.setVisibility(View.GONE);
        etStartMoney = (EditText) rootView.findViewById(R.id.etStartMoney); // start money amount
        spStartMoneyCurrency = (Spinner) rootView.findViewById(R.id.spStartMoneyCurrency); //start money currency
        spStartMoneyCurrency.setAdapter(arrayAdapter);
        spStartMoneyCurrency.setSelection(mainCurrencyPos);
        tvStartSumAccountTitle = (TextView) rootView.findViewById(R.id.tvStartSumAccountTitle);
        etStartLimit = (EditText) rootView.findViewById(R.id.etStartLimit); //limit amount
        spStartLimit = (Spinner) rootView.findViewById(R.id.spStartLimitCurrency); //limit currency
        spStartLimit.setAdapter(arrayAdapter);
        tvStartSumAccountTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chbAccountStartSumEnabled.toggle();
            }
        });
        chbAccountNoneZero = (CheckBox) rootView.findViewById(R.id.noneZeroAccount); // none minus account's checkbox
        tvNoneMinusAccountTitle = (TextView) rootView.findViewById(R.id.tvNoneMinusAccountTitle);
        tvNoneMinusAccountTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chbAccountNoneZero.toggle();
            }
        });
        if (account != null) { // fill, if account is editing
            etAccountEditName.setText(account.getName());
            resId = getResources().getIdentifier(account.getIcon(), "drawable", getContext().getPackageName());
            temp = BitmapFactory.decodeResource(getResources(), resId);
            bitmap = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
                    (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
            choosenIcon = account.getIcon();
            fabAccountIcon.setImageBitmap(bitmap);
            chbAccountNoneZero.setChecked(account.getNoneMinusAccount());
            if (account.getAmount() != 0) {
                chbAccountStartSumEnabled.setChecked(true);
                rlStartSumContainer.setVisibility(View.VISIBLE);
                etStartMoney.setText(Double.toString(account.getAmount()));
                for (int i = 0; i < currencies.size(); i++)
                    if (currencies.get(i).getId().matches(account.getStartMoneyCurrency().getId())) {
                        spStartMoneyCurrency.setSelection(i);
                        break;
                    }
            }
            if (account.getIsLimited()) {
                chbAccountEnabledLimit.setChecked(true);
                etStartLimit.setText("" + account.getLimite());
                for (int i = 0; i < currencies.size(); i++) {
                    if (currencies.get(i).getId().equals(account.getLimitCurId())) {
                        spStartLimit.setSelection(i);
                        break;
                    }
                }
            }
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivToolbarMostRight:
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (etAccountEditName.getText().toString().matches("")) {
                    etAccountEditName.setError(getString(R.string.enter_name_error));
                    return;
                }
                Account account;
                if (this.account == null) {
                    account = new Account();
                    account.setId(UUID.randomUUID().toString());
                    account.setCalendar(Calendar.getInstance());
                } else
                    account = this.account;
                account.setName(etAccountEditName.getText().toString());
                if (chbAccountStartSumEnabled.isChecked() && !etStartMoney.getText().toString().matches("")) {
                    try {
                        Double.parseDouble(etStartMoney.getText().toString().replace(",","."));
                        etStartMoney.setError(null);
                        account.setAmount(Double.parseDouble(etStartMoney.getText().toString().replace(",",".")));
                    } catch (Exception e) {
                        etStartMoney.setError(getString(R.string.wrong_input_type));
                        return;
                    }
                }else
                    account.setAmount(0);
                if (chbAccountEnabledLimit.isChecked()) {
                    if (etStartLimit.getText().toString().equals("")) {
                        etStartLimit.setError(getResources().getString(R.string.enter_amount_error));
                        return;
                    }
                    double limitSum;
                    try {
                        String temp = etStartLimit.getText().toString();
                        temp.replace(",", ".");
                        limitSum = Double.parseDouble(temp.replace(",","."));
                    } catch (Exception e) {
                        etStartLimit.setError(getString(R.string.wrong_input_type));
                        return;
                    }
                    if (this.account != null) {
                        List<Currency> currencies = daoSession.getCurrencyDao().loadAll();
                        int state = logicManager.changeAccount(account,Calendar.getInstance(),limitSum,currencies.get(spStartLimit.getSelectedItemPosition()),account.getAmount(),daoSession.getCurrencyDao().loadAll()
                                .get(spStartMoneyCurrency.getSelectedItemPosition()));
                        if(state == LogicManager.LIMIT){
                            Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                    account.setIsLimited(true);
                    account.setLimite(Double.parseDouble(etStartLimit.getText().toString().replace(",",".")));
                    List<Currency> currencies = daoSession.getCurrencyDao().loadAll();
                    account.setLimitCurId(currencies.get(spStartLimit.getSelectedItemPosition()).getId());
                } else {
                    account.setIsLimited(false);

                    if(chbAccountNoneZero.isChecked()){
                        List<Currency> currencies = daoSession.getCurrencyDao().loadAll();
                        int state = logicManager.changeAccount(account,Calendar.getInstance(),0,currencies.get(spStartLimit.getSelectedItemPosition()),account.getAmount(),daoSession.getCurrencyDao().loadAll()
                                .get(spStartMoneyCurrency.getSelectedItemPosition()));
                        if(state == LogicManager.LIMIT){
                            Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if(chbAccountNoneZero.isChecked()){
                        account.setLimitCurId(commonOperations.getMainCurrency().getId());
                    } else
                        account.setLimitCurId(null);
                }
                account.setStartMoneyCurrency(daoSession.getCurrencyDao().loadAll()
                        .get(spStartMoneyCurrency.getSelectedItemPosition()));
                account.setIcon(choosenIcon);
                if (account != null && chbAccountNoneZero.isChecked()) {
                    double limit = logicManager.isLimitAccess(account, Calendar.getInstance());
                    if (limit < 0) {
                        Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                account.setNoneMinusAccount(chbAccountNoneZero.isChecked());
                if (this.account != null) {
                    daoSession.getAccountDao().insertOrReplace(account);
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new AccountFragment());
                } else {
                    if (logicManager.insertAccount(account) == LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS) {
                        etAccountEditName.setError(getString(R.string.such_account_name_exists_error));
                        return;
                    } else {
                        paFragmentManager.getFragmentManager().popBackStack();
                        paFragmentManager.displayFragment(new AccountFragment());
                    }
                }
                break;
        }
    }
    @Override
    void refreshList() {

    }
}
