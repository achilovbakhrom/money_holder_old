package com.jim.pocketaccounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.finance.TransferAccountAdapter;
import com.jim.pocketaccounter.managers.LogicManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by DEV on 06.09.2016.
 */

public class TransferDialog extends Dialog {
    @Inject
    LogicManager logicManager;
    @Inject
    DaoSession daoSession;
    @Inject
    DatePicker datePicker;
    @Inject
    @Named(value = "display_formmatter")
    SimpleDateFormat dateFormat;

    private View dialogView;
    private EditText etAccountEditName;
    private Spinner spTransferFirst, spTransferSecond;
    private TransferAccountAdapter firstAdapter, secondAdapter;
    private Spinner spAccManDialog;
    private List<Currency> currencies;
    private ImageView ivYes, ivAccountManClose;
    private TextView date;
    private Calendar calendar;

    public TransferDialog(Context context) {
        super(context);
        ((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
        dialogView = getLayoutInflater().inflate(R.layout.account_transfer_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        etAccountEditName = (EditText) dialogView.findViewById(R.id.etAccountEditName);
        spTransferFirst = (Spinner) dialogView.findViewById(R.id.spTransferFirst);
        spTransferSecond = (Spinner) dialogView.findViewById(R.id.spTransferSecond);
        spAccManDialog = (Spinner) dialogView.findViewById(R.id.spAccManDialog);
        date = (TextView) dialogView.findViewById(R.id.tvAccountDialogDate);
        currencies = daoSession.getCurrencyDao().loadAll();
        String[] currs = new String[currencies.size()];
        for (int i=0; i<currencies.size(); i++)
            currs[i] = currencies.get(i).getAbbr();
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, currs);
        spAccManDialog.setAdapter(currencyAdapter);
        ivYes = (ImageView) dialogView.findViewById(R.id.ivAccountManSave);
        ivAccountManClose = (ImageView) dialogView.findViewById(R.id.ivAccountManClose);
        calendar = (Calendar) Calendar.getInstance().clone();
        date.setText(dateFormat.format(calendar.getTime()));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
                datePicker.setOnDatePickListener(new OnDatePickListener() {
                    @Override
                    public void OnDatePick(Calendar pickedDate) {
                        calendar = (Calendar) pickedDate.clone();
                        date.setText(dateFormat.format(calendar.getTime()));
                    }
                });
            }
        });
    }

    public TransferDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TransferDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private int firstPos = 0;
    private int secondPos = 1;

    public void setAccountOrPurpose(String id) {
        if (id != null) {
            final List<String> first = new ArrayList<>();
            for (Account account : daoSession.getAccountDao().loadAll())
                first.add(account.getId());
            for (Purpose purpose : daoSession.getPurposeDao().loadAll())
                first.add(purpose.getId());
            int selectedPos = 0;
            for (int i=0; i<first.size(); i++) {
                if (first.get(i).matches(id)) {
                    selectedPos = i;
                    break;
                }
            }
            final List<String> second = new ArrayList<>();
            firstAdapter = new TransferAccountAdapter(getContext(), first);
            spTransferFirst.setAdapter(firstAdapter);
            spTransferFirst.setSelection(selectedPos);
            spTransferFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long pos) {
                    String firstId = first.get(i);
                    second.clear();
                    for (String id : first) {
                        if (!id.matches(firstId))
                            second.add(id);
                    }
                    if (i == spTransferFirst.getSelectedItemPosition()) {
                        spTransferSecond.setSelection(firstPos);
                    } else {
                        firstPos = i;
                    }
                }
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
            secondAdapter = new TransferAccountAdapter(getContext(), first);
            spTransferSecond.setAdapter(secondAdapter);
            spTransferSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == spTransferFirst.getSelectedItemPosition()) {
                        spTransferFirst.setSelection(secondPos);
                    } else {
                        secondPos = position;
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    public void setOnTransferDialogSaveListener(final OnTransferDialogSaveListener onTransferDialogSaveListener) {
        ivYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTransferDialogSaveListener != null && !etAccountEditName.getText().toString().isEmpty()) {
                    onTransferDialogSaveListener.OnTransferDialogSave();
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setAmount(Double.parseDouble(etAccountEditName.getText().toString()));
                    accountOperation.setCurrency(currencies.get(spAccManDialog.getSelectedItemPosition()));
                    accountOperation.setDate(calendar);
                    if (spTransferFirst.getSelectedItemPosition() < daoSession.getAccountDao().loadAll().size()) {
                        accountOperation.setSourceId(daoSession.getAccountDao().loadAll()
                                .get(spTransferFirst.getSelectedItemPosition()).getId());
                    } else {
                        accountOperation.setSourceId(daoSession.getPurposeDao().loadAll()
                                .get(spTransferFirst.getSelectedItemPosition() - daoSession.getAccountDao().loadAll().size()).getId());
                    }
                    if (spTransferSecond.getSelectedItemPosition() < daoSession.getAccountDao().loadAll().size()) {
                        accountOperation.setTargetId(daoSession.getAccountDao().loadAll()
                                .get(spTransferSecond.getSelectedItemPosition()).getId());
                    } else {
                        accountOperation.setTargetId(daoSession.getPurposeDao().loadAll()
                                .get(spTransferFirst.getSelectedItemPosition() - daoSession.getAccountDao().loadAll().size()).getId());
                    }
                    accountOperation.__setDaoSession(daoSession);
                    logicManager.insertAccountOperation(accountOperation);
                    dismiss();
                }
            }
        });
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivAccountManClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public interface OnTransferDialogSaveListener {
        public void OnTransferDialogSave();
    }
}
