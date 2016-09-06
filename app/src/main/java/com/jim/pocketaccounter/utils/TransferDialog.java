package com.jim.pocketaccounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.finance.TransferAccountAdapter;
import com.jim.pocketaccounter.managers.LogicManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by DEV on 06.09.2016.
 */

public class TransferDialog extends Dialog {
    @Inject
    LogicManager logicManager;
    @Inject
    DaoSession daoSession;
    private View dialogView;
    private EditText etAccountEditName;
    private Spinner spTransferFirst;
    private TransferAccountAdapter firstAdapter, secondAdapter;
    private Spinner spAccManDialog;
    private List<Currency> currencies;
    private ImageView ivYes, ivAccountManClose;
    public TransferDialog(Context context) {
        super(context);
        ((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
        dialogView = getLayoutInflater().inflate(R.layout.account_transfer_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        etAccountEditName = (EditText) dialogView.findViewById(R.id.etAccountEditName);
        spTransferFirst = (Spinner) dialogView.findViewById(R.id.spTransferFirst);
        spAccManDialog = (Spinner) dialogView.findViewById(R.id.spAccManDialog);
        currencies = daoSession.getCurrencyDao().loadAll();
        String[] currs = new String[currencies.size()];
        for (int i=0; i<currencies.size(); i++)
            currs[i] = currencies.get(i).getAbbr();
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, currs);

        ivYes = (ImageView) dialogView.findViewById(R.id.ivAccountManSave);
        ivAccountManClose = (ImageView) dialogView.findViewById(R.id.ivAccountManClose);

    }

    public TransferDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TransferDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

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
            final ArrayList<Account> temp = new ArrayList<Account>();
            spTransferFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long pos) {
                    String firstId = first.get((int) pos);
                    second.clear();
                    for (String id : first) {
                        if (!id.matches(firstId))
                            second.add(id);
                    }

                    TransferAccountAdapter secondAdapter = new TransferAccountAdapter(getContext(), second);
                    spTransferSecond.setAdapter(secondAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    public void setOnTransferDialogSaveListener(final OnTransferDialogSaveListener onTransferDialogSaveListener) {
        ivYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onTransferDialogSaveListener != null)
                    onTransferDialogSaveListener.OnTransferDialogSave();
                dismiss();
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
