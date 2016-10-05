package com.jim.pocketaccounter.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 9/29/16.
 */

public class AddSmsParseFragment extends Fragment {
    @Inject DaoSession daoSession;
    @Inject PAFragmentManager paFragmentManager;
    @Inject ToolbarManager toolbarManager;

    private EditText etNumber;
    private RadioGroup rgSortSms;
    private RecyclerView rvSmsList;
    private TextView tvSmsCount;
    private EditText etIncome;
    private EditText etExpance;
    private EditText etAmount;
    private Spinner spAccount;
    private Spinner spCurrency;

    private MyAdapter myAdapter;

    private final int ALL_SMS = 0;
    private final int INCOME_SMS = 1;
    private final int EXPANCE_SMS = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_sms_sender, container, false);
        etNumber = (EditText) rootView.findViewById(R.id.etSmsParseAddNumber);
        rgSortSms = (RadioGroup) rootView.findViewById(R.id.rgSmsParseAddSort);
        rvSmsList = (RecyclerView) rootView.findViewById(R.id.rvSmsParseAdd);
        etIncome = (EditText) rootView.findViewById(R.id.etSmsParseAddIncome);
        etExpance = (EditText) rootView.findViewById(R.id.etSmsParseAddExpance);
        etAmount = (EditText) rootView.findViewById(R.id.etSmsParseAddAmount);
        spAccount = (Spinner) rootView.findViewById(R.id.spSmsParseAddAccount);
        spCurrency = (Spinner) rootView.findViewById(R.id.spSmsParseAddCurrency);
        myAdapter = new MyAdapter(ALL_SMS, getAllSms());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvSmsList.setLayoutManager(layoutManager);
        rvSmsList.setAdapter(myAdapter);
        rgSortSms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (myAdapter.getTypeSort() != (checkedId == R.id.rbnSmsParseAddExpance ? ALL_SMS
                        : checkedId == R.id.rbnSmsParseAddIncome ? INCOME_SMS : EXPANCE_SMS)) {
                    if (checkedId == R.id.rbnSmsParseAddExpance) {
                        myAdapter.setType(EXPANCE_SMS);
                    } else if (checkedId == R.id.rbnSmsParseAddIncome) {
                        myAdapter.setType(INCOME_SMS);
                    } else {
                        myAdapter.setType(ALL_SMS);
                    }
                }
            }
        });
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                List<Sms> smsList = new ArrayList<>();
                for (Sms sms : getAllSms()) {
                    if(sms.getNumber().contains(s.toString())) {
                        smsList.add(sms);
                    }
                }

            }
        });
        return rootView;
    }

    public List<Sms> getAllSms() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getActivity().getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        getActivity().startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setDate(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                    lstSms.add(objSms);
                }
                c.moveToNext();
            }
        }
        c.close();
        return lstSms;
    }

    class Sms {
        private String body;
        private String number;
        private String id;
        private String folderName;
        private String date;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<AddSmsParseFragment.ViewHolder> {
        private int typeSort;
        private List<Sms> list;

        public MyAdapter(int typeSort, List<Sms> list) {
            this.typeSort = typeSort;
            this.list = list;
        }

        public int getTypeSort() {
            return typeSort;
        }

        public void setType(int typeSms) {
            this.typeSort = typeSms;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return list.size();
        }

        public void onBindViewHolder(final AddSmsParseFragment.ViewHolder view, final int position) {

        }

        public AddSmsParseFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_adapter_root, parent, false);
            return new AddSmsParseFragment.ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }

}
