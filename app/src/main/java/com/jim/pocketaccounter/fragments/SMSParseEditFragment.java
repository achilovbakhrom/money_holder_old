package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseKeys;
import com.jim.pocketaccounter.database.SmsParseKeysDao;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

@SuppressLint({"InflateParams", "ValidFragment"})
public class SMSParseEditFragment extends Fragment implements View.OnClickListener {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    ToolbarManager toolbarManager;

    private SmsParseObject object;
    private FloatingActionButton floatingActionButton;
    Dialog dialog;
    MyDialogKeys myDialogKeys;

    private Spinner spCurrency;
    private Spinner spAccount;
    private EditText amount;
    private Button record;
    private TextView operation;
    private TextView curName;
    private RecyclerView rvStrings;

    @SuppressLint("ValidFragment")
    public SMSParseEditFragment(SmsParseObject object) {
        this.object = object;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        View rootView = inflater.inflate(R.layout.sms_parse_edit_moder, container, false);
//        date = (EditText) rootView.findViewById(R.id.etSmsParseDate);
//        spOperation = (Spinner) rootView.findViewById(R.id.spSmsParseAddIncExp);
        spCurrency = (Spinner) rootView.findViewById(R.id.spSmsParseAddCurs);
        spAccount = (Spinner) rootView.findViewById(R.id.spSmsParseAddAccount);
        amount = (EditText) rootView.findViewById(R.id.etSmsParseAddAmount);
        record = (Button) rootView.findViewById(R.id.btnSmsParseAddOk);
        operation = (TextView) rootView.findViewById(R.id.tvSmsParseAddOperation);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fbSmsParseChange);
        curName = (TextView) rootView.findViewById(R.id.tvSmsParseAddCurName);

        rvStrings = (RecyclerView) rootView.findViewById(R.id.rvSmsParseAddString);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvStrings.setLayoutManager(layoutManager);

        List<String> curs = new ArrayList<>();
        for (Currency cr : daoSession.getCurrencyDao().loadAll()) {
            curs.add(cr.getAbbr());
        }
        ArrayAdapter<String> adapterCur = new ArrayAdapter<>(getContext()
                , android.R.layout.simple_list_item_1, curs);
        spCurrency.setAdapter(adapterCur);

        List<String> accs = new ArrayList<>();
        for (Account ac : daoSession.getAccountDao().loadAll()) {
            accs.add(ac.getName());
        }
        ArrayAdapter<String> adapterAcc = new ArrayAdapter<>(getContext()
                , android.R.layout.simple_list_item_1, accs);
        spAccount.setAdapter(adapterAcc);

        floatingActionButton.setOnClickListener(this);

        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setImageToSecondImage(R.drawable.cloud5);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_sms_key_words, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                final TextView income = (TextView) dialogView.findViewById(R.id.tvDialogSmsParseIncome);
                final TextView expance = (TextView) dialogView.findViewById(R.id.tvDialogSmsParseExpanse);
                final TextView curs = (TextView) dialogView.findViewById(R.id.tvDialogSmsParseCurs);
                Button delete = (Button) dialogView.findViewById(R.id.btnDialogSmsKeysDelete);
                Button add = (Button) dialogView.findViewById(R.id.btnDialogSmsKeyAdd);
                final EditText etAdd = (EditText) dialogView.findViewById(R.id.etDialogSmsParseNew);
                final RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rvDialogSmsParseKeys);
                RecyclerView.LayoutManager lM = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(lM);
                myDialogKeys = new MyDialogKeys(0);
                recyclerView.setAdapter(myDialogKeys);
                income.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        income.setTextColor(ContextCompat.getColor(getContext(), R.color.green_light_monoxrom));
                        expance.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                        curs.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                        myDialogKeys = new MyDialogKeys(0);
                        recyclerView.setAdapter(myDialogKeys);
                    }
                });
                expance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        income.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                        expance.setTextColor(ContextCompat.getColor(getContext(), R.color.green_light_monoxrom));
                        curs.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                        myDialogKeys = new MyDialogKeys(1);
                        recyclerView.setAdapter(myDialogKeys);
                    }
                });
                curs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        income.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                        expance.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                        curs.setTextColor(ContextCompat.getColor(getContext(), R.color.green_light_monoxrom));
                        myDialogKeys = new MyDialogKeys(2);
                        recyclerView.setAdapter(myDialogKeys);
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etAdd.getText().toString().isEmpty()) {
                            etAdd.setError("Enter key");
                        } else {
                            etAdd.setError(null);
                            myDialogKeys.addkey(etAdd.getText().toString());
                            etAdd.setText("");
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialogKeys.deleteWords();
                    }
                });
                int width = getResources().getDisplayMetrics().widthPixels;
                dialog.getWindow().setLayout(8 * width / 10, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbSmsParseChange: {
                dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_sms_parse_numbers, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                final RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rvSmsParseDialog);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                MyAdapter myAdapter = new MyAdapter();
                recyclerView.setAdapter(myAdapter);
                int width = getResources().getDisplayMetrics().widthPixels;
                dialog.getWindow().setLayout(8 * width / 10, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                dialog.show();
                break;
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<SMSParseEditFragment.ViewHolder> {
        private List<Sms> smsList;

        public MyAdapter() {
            smsList = getAllSms();
        }

        public int getItemCount() {
            return smsList.size();
        }

        public void onBindViewHolder(final SMSParseEditFragment.ViewHolder view, final int position) {
            view.number.setText(smsList.get(position).getNumber());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            date.setTime(Long.parseLong(smsList.get(position).getDate()));
            view.date.setText(simpleDateFormat.format(date.getTime()));
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smsBodyParse(smsList.get(position).getBody());
                    dialog.dismiss();
                }
            });
        }
        public SMSParseEditFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_sms_parsing, parent, false);
            return new SMSParseEditFragment.ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView number;
        private TextView date;

        public ViewHolder(View view) {
            super(view);
            number = (TextView) view.findViewById(R.id.tvSmsParseDialogItemNumber);
            date = (TextView) view.findViewById(R.id.tvSmsParseDialogItemDate);
        }
    }

    private class MyDialogKeys extends RecyclerView.Adapter<SMSParseEditFragment.ViewHolderKeys> {
        private List<SmsParseKeys> smsKeys;
        private boolean tek[];
        private int type;
        private int MODE = 0;

        public MyDialogKeys(int type) {
            this.type = type;
            smsKeys = daoSession.getSmsParseKeysDao().queryBuilder()
                    .where(SmsParseKeysDao.Properties.Type.eq(type)).list();
            tek = new boolean[smsKeys.size()];
            MODE = 0;
        }

        public void addkey(String key) {
            SmsParseKeys smsParseKeys = new SmsParseKeys();
            smsParseKeys.setType(type);
            smsParseKeys.setNameKey(key);
            smsKeys.add(0, smsParseKeys);
            daoSession.getSmsParseKeysDao().insertOrReplace(smsParseKeys);
            notifyItemInserted(0);
        }

        public void deleteWords () {
            if (MODE == 1) {
                for (int i = tek.length - 1; i >= 0; i--) {
                    if (tek[i]) {
                        daoSession.getSmsParseKeysDao().delete(smsKeys.get(i));
                        smsKeys.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            }else {
                MODE = 1;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            return smsKeys.size();
        }

        public int getMODE() {
            return MODE;
        }

        public void setMODE(int MODE) {
            this.MODE = MODE;
        }

        public void onBindViewHolder(final SMSParseEditFragment.ViewHolderKeys view, final int position) {
            view.checkBox.setVisibility(View.GONE);
            if (MODE == 1) {
                view.checkBox.setVisibility(View.VISIBLE);
                view.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.checkBox.setChecked(!tek[position]);
                    }
                });
                view.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tek[position] = !tek[position];
                    }
                });
            }
            view.textView.setText(smsKeys.get(position).getNameKey());
        }

        public SMSParseEditFragment.ViewHolderKeys onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms_keys_layout, parent, false);
            return new SMSParseEditFragment.ViewHolderKeys(view);
        }
    }

    public class ViewHolderKeys extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView textView;

        public ViewHolderKeys(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.chbItemDialogSmsKeys);
            textView = (TextView) view.findViewById(R.id.tvItemDialogSmsKeys);
        }
    }

    private class MyAdapterString extends RecyclerView.Adapter<SMSParseEditFragment.ViewHolderString> {
        private List<String> strings;

        public MyAdapterString(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }

        public void onBindViewHolder(final SMSParseEditFragment.ViewHolderString view, final int position) {
            view.textView.setText(strings.get(position));
            for (SmsParseKeys smsParseKey : daoSession.getSmsParseKeysDao().queryBuilder().
                    where(SmsParseKeysDao.Properties.Type.eq(2)).list()) {
                if (smsParseKey.getNameKey().startsWith(strings.get(position))
                        || strings.get(position).startsWith(smsParseKey.getNameKey())
                        || strings.get(position).contains(smsParseKey.getNameKey())
                        || smsParseKey.getNameKey().contains(strings.get(position))) {
                    curName.setText(strings.get(position));
                }
            }
        }

        public SMSParseEditFragment.ViewHolderString onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
            return new SMSParseEditFragment.ViewHolderString(view);
        }
    }

    public class ViewHolderString extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolderString(View view) {
            super(view);
            textView = (TextView) view.findViewById(android.R.id.text1);
        }
    }

    private void smsBodyParse(String body) {
        List<String> words = new ArrayList<>();
        String[] strings = body.split(" ");
//        String patternAmount = "([0,9] + [.,])?([0,9])";
//        Pattern pattern = Pattern.compile(patternAmount);
//        for (String s : strings) {
//            Matcher matcher = pattern.matcher(s);
//            if (matcher.find()) {
//                matcher.group(0);
//
//            }
//        }
        for (String s : strings) {
            if (s.split(" ").length == 1 && s.split("\n").length == 1) {
                words.add(s);
            } else {
                if (s.split(" ").length == 1) {
                    for (String s1 : s.split("\n")) {
                        words.add(s1);
                    }
                } else {
                    for (String s1 : s.split(" ")) {
                        words.add(s1);
                    }
                }
            }
        }
        rvStrings.setAdapter(new MyAdapterString(words));
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
                objSms.setNumber(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
//                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setDate(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                lstSms.add(objSms);
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
}
