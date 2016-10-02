package com.jim.pocketaccounter.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.finance.IconAdapterAccount;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.OnIconPickListener;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.cache.DataCache;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


public class AddCreditFragment extends Fragment {
    @Inject
    @Named(value = "display_formatter")
    SimpleDateFormat dateFormat;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    LogicManager logicManager;
    @Inject
    CommonOperations commonOperations;
    @Inject
    IconChooseDialog iconChooseDialog;
    @Inject
    DataCache dataCache;
    CreditDetialsDao creditDetialsDao;
    CurrencyDao currencyDao;
    AccountDao accountDao;
    FinanceRecordDao financeRecordDao;
    DebtBorrowDao debtBorrowDao;

    boolean onSucsessed = false;
    Spinner spiner_forValut, spiner_procent, spinner_peiod, spiner_trasnact;
    ImageView icona;
    String[] valyutes;
    String[] valyutes_symbols;
    String[] accs;
    ArrayList<Account> accounts;
    String selectedIcon;
    EditText nameCred, valueCred, procentCred, periodCred, firstCred, lastCred, transactionCred;
    Context context;
    int argFirst[] = new int[3];
    int argLast[] = new int[3];
    long forDay = 1000L * 60L * 60L * 24L;
    long forMoth = 1000L * 60L * 60L * 24L * 30L;
    long forWeek = 1000L * 60L * 60L * 24L * 7L;
    long forYear = 1000L * 60L * 60L * 24L * 365L;
    List<Currency> currencies;
    CreditFragment.EventFromAdding eventLis;
    AddCreditFragment ThisFragment;
    CheckBox isOpkey;
    public static final String OPENED_TAG = "Addcredit";
    public static boolean to_open_dialog = false;
    CreditDetials currentCredit;
    private FrameLayout btnDetalization;
    private String mode = PocketAccounterGeneral.EVERY_DAY, sequence = "";
    private Spinner spNotifMode;
    private ArrayList<String> adapter;
    boolean fromMainWindow=false;
    int modeFromMain;
    int posFromMain;
    public AddCreditFragment() {
        // Required empty public constructor
        ThisFragment = this;
    }

    public AddCreditFragment setDateFormatModes( int mode, int pos){
        fromMainWindow=true;
        this.modeFromMain=mode;
        this.posFromMain=pos;
        return this;
    }

    public void shareForEdit(CreditDetials currentCredit) {
        this.currentCredit = currentCredit;
    }

    public boolean isEdit() {
        return currentCredit != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        if (fromMainWindow) {
            paFragmentManager.setMainReturn(true);
        }
        creditDetialsDao = daoSession.getCreditDetialsDao();
        accountDao = daoSession.getAccountDao();
        currencyDao = daoSession.getCurrencyDao();
        financeRecordDao = daoSession.getFinanceRecordDao();
        debtBorrowDao = daoSession.getDebtBorrowDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_add_credit, container, false);
        context = getActivity();
        spiner_forValut = (Spinner) V.findViewById(R.id.spinner);
        spiner_procent = (Spinner) V.findViewById(R.id.spinner_procent);
        spinner_peiod = (Spinner) V.findViewById(R.id.spinner_period);
        spiner_trasnact = (Spinner) V.findViewById(R.id.spinner_sceta);
        isOpkey = (CheckBox) V.findViewById(R.id.key_for_balance);
        to_open_dialog = false;

        nameCred = (EditText) V.findViewById(R.id.editText);
        valueCred = (EditText) V.findViewById(R.id.value_credit);
        procentCred = (EditText) V.findViewById(R.id.procent_credit);
        periodCred = (EditText) V.findViewById(R.id.for_period_credit);
        firstCred = (EditText) V.findViewById(R.id.date_pick_edit);
        lastCred = (EditText) V.findViewById(R.id.date_ends_edit);
        transactionCred = (EditText) V.findViewById(R.id.for_trasaction_credit);

        spNotifMode = (Spinner) V.findViewById(R.id.spNotifModeCredit);
        btnDetalization = (FrameLayout) V.findViewById(R.id.btnDetalizationCredit);
        btnDetalization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotifSettingDialog();
            }
        });
        adapter = new ArrayList<>();
        adapter.add(getResources().getString(R.string.notif_everyday));
        adapter.add(getResources().getString(R.string.notif_weekly));
        adapter.add(getResources().getString(R.string.notif_monthly));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, adapter);
        spNotifMode.setAdapter(adapter1);
        spNotifMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mode = PocketAccounterGeneral.EVERY_DAY;
                        btnDetalization.setVisibility(View.GONE);
                        break;
                    case 1:
                        mode = PocketAccounterGeneral.EVERY_WEEK;
                        btnDetalization.setVisibility(View.VISIBLE);
                        break;

                    case 2:
                        sequence = "1";
                        mode = PocketAccounterGeneral.EVERY_MONTH;
                        btnDetalization.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nameCred.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (nameCred.getText().toString().matches("")) {
                    to_open_dialog = false;
                } else {
                    to_open_dialog = true;
                }
            }
        });

        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMojno = true;
                if (nameCred.getText().toString().equals("")) {
                    nameCred.setError(getString(R.string.should_not_empty));
                    isMojno = false;
                } else
                    nameCred.setHintTextColor(ContextCompat.getColor(context, R.color.black_for_secondary_text));
                if (valueCred.getText().toString().equals("")) {
                    valueCred.setError(getString(R.string.value_shoud_not_empty));
                    isMojno = false;
                } else {
                    try {
                        if (!(Double.parseDouble(valueCred.getText().toString()) > 0)) {
                            valueCred.setError(getString(R.string.incorrect_value));
                            isMojno = false;
                        }
                    } catch (Exception o) {
                    }
                }
                if (procentCred.getText().toString().equals("")) {
                    procentCred.setError(getString(R.string.procent_should_not_empty));
                    isMojno = false;
                }

                if (periodCred.getText().toString().equals("")) {
                    periodCred.setError(getString(R.string.period_should_not_empty));
                    isMojno = false;
                } else {
                    try {
                        if (!(Integer.parseInt(periodCred.getText().toString()) > 0)) {
                            periodCred.setError(getString(R.string.incorrect_value));
                            isMojno = false;
                        }
                    } catch (Exception o) {
                    }
                }

                if (firstCred.getText().toString().equals("")) {
                    firstCred.setError(getString(R.string.after_per_choise));
                    isMojno = false;
                }

                //TODO first transaction
                if (isMojno) {
                    openDialog();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener getDatesetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                Date AAa = (new Date());
                argFirst[0] = arg1;
                argFirst[1] = arg2;
                argFirst[2] = arg3;
                Calendar calend = new GregorianCalendar(arg1, arg2, arg3);
                AAa.setTime(calend.getTimeInMillis());

                firstCred.setText(dateFormat.format(AAa));

                int period_long = 1;
                if (!periodCred.getText().toString().matches("")) {
                    period_long = Integer.parseInt(periodCred.getText().toString());

                    switch (spinner_peiod.getSelectedItemPosition()) {
                        case 0:
                            //moth
                            calend.add(Calendar.MONTH, period_long);

                            break;
                        case 1:
                            //year
                            calend.add(Calendar.YEAR, period_long);
                            break;
                        case 2:
                            //week
                            calend.add(Calendar.WEEK_OF_YEAR, period_long);
                            break;
                        case 3:
                            //day
                            calend.add(Calendar.DAY_OF_YEAR, period_long);

                            break;
                        default:
                            break;
                    }


                    long forCompute = calend.getTimeInMillis();
                    // forCompute+=period_long;

                    AAa.setTime(forCompute);
                    lastCred.setText(dateFormat.format(AAa));

                } else {
                    periodCred.setError(getString(R.string.first_enter_period));
                }
            }
        };
        final DatePickerDialog.OnDateSetListener getDatesetListener2 = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                argLast[0] = arg1;
                argLast[1] = arg2;
                argLast[2] = arg3;

                Date AAa = (new Date());
                Calendar calend = new GregorianCalendar(arg1, arg2, arg3);
                AAa.setTime(calend.getTimeInMillis());
                lastCred.setText(dateFormat.format(AAa));
                int period_long = 1;
                if (!periodCred.getText().toString().matches("")) {
                    period_long = Integer.parseInt(periodCred.getText().toString());
                    switch (spinner_peiod.getSelectedItemPosition()) {
                        case 0:
                            //moth
                            calend.add(Calendar.MONTH, -period_long);
                            break;
                        case 1:
                            //year
                            calend.add(Calendar.YEAR, -period_long);
                            break;
                        case 2:
                            //week
                            calend.add(Calendar.WEEK_OF_YEAR, -period_long);
                            break;
                        case 3:
                            //day
                            calend.add(Calendar.DAY_OF_YEAR, -period_long);

                            break;
                        default:
                            break;
                    }
                    long forCompute = calend.getTimeInMillis();
                    AAa.setTime(forCompute);
                    firstCred.setText(dateFormat.format(AAa));
                } else {
                    periodCred.setError(getString(R.string.first_enter_period));
                }
            }
        };
        lastCred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstCred.setError(null);
                lastCred.setError(null);
                Calendar calendar = Calendar.getInstance();
                Dialog mDialog = new DatePickerDialog(getContext(),
                        getDatesetListener2, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                mDialog.show();
            }
        });
        firstCred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstCred.setError(null);
                lastCred.setError(null);
                Calendar calendar = Calendar.getInstance();
                Dialog mDialog = new DatePickerDialog(getContext(),
                        getDatesetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                mDialog.show();
            }
        });


        isOpkey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isOpkey.isChecked()) {
                    spiner_trasnact.setVisibility(View.VISIBLE);
                } else spiner_trasnact.setVisibility(View.GONE);
            }
        });
        procentCred.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String A = procentCred.getText().toString();
                    if (!A.equals("")) {
                        if (A.contains("%")) {
                            StringBuilder sb = new StringBuilder(A);
                            sb.deleteCharAt(A.indexOf("%"));
                            procentCred.setText(sb.toString() + "%");
                        } else {
                            procentCred.setText(A + "%");
                        }
                    }
                }
            }
        });
        spinner_peiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (argFirst[0] != 0) {
                    forDateSyncFirst();
                } else if (argLast[0] != 0) {
                    forDateSyncLast();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        periodCred.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (argFirst[0] != 0) {
                    forDateSyncFirst();
                } else if (argLast[0] != 0) {
                    forDateSyncLast();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        icona = (ImageView) V.findViewById(R.id.imageForIcon);
        String[] tempIcons = getResources().getStringArray(R.array.icons);
        selectedIcon = tempIcons[4];

        icona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO ICON DIALOG
                iconChooseDialog.setSelectedIcon(selectedIcon);
                iconChooseDialog.setOnIconPickListener(new OnIconPickListener() {
                    @Override
                    public void OnIconPick(String icon) {
                        int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
                        Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
                        Bitmap iconik = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp), (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
                        icona.setImageBitmap(iconik);
                        selectedIcon = icon;
                        iconChooseDialog.dismiss();
                    }
                });
                iconChooseDialog.show();

            }
        });


        V.findViewById(R.id.pustoyy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        currencies = currencyDao.loadAll();
        valyutes = new String[currencies.size()];
        valyutes_symbols = new String[currencies.size()];

        for (int i = 0; i < valyutes.length; i++) {
            valyutes[i] = currencies.get(i).getAbbr();
        }
        for (int i = 0; i < valyutes.length; i++) {
            valyutes_symbols[i] = currencies.get(i).getAbbr();
        }

        accounts = (ArrayList<Account>) accountDao.queryBuilder().list();
        accs = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            accs[i] = accounts.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.adapter_spiner,
                new String[]{
                        getString(R.string.per_month), getString(R.string.per_year), getString(R.string.per_week), getString(R.string.per_day)
                });


        ArrayAdapter<String> adapter_valyuta = new ArrayAdapter<String>(getActivity(),
                R.layout.adapter_spiner, valyutes);


        ArrayAdapter<String> adapter_period = new ArrayAdapter<String>(getActivity(),
                R.layout.adapter_spiner, new String[]{
                getString(R.string.mont), getString(R.string.yearr), getString(R.string.weekk), getString(R.string.dayy)
        });

        ArrayAdapter<String> adapter_scet = new ArrayAdapter<String>(getActivity(),
                R.layout.spiner_gravity_right, accs);

        spiner_forValut.setAdapter(adapter_valyuta);
        spiner_procent.setAdapter(adapter);
        spinner_peiod.setAdapter(adapter_period);
        spiner_trasnact.setAdapter(adapter_scet);

        if (isEdit()) {
            int resId = getResources().getIdentifier(selectedIcon, "drawable", getContext().getPackageName());
            icona.setImageResource(resId);
            nameCred.setText(currentCredit.getCredit_name());
            valueCred.setText(parseToWithoutNull(currentCredit.getValue_of_credit()));
            procentCred.setText(parseToWithoutNull(currentCredit.getProcent()) + "%");
            spiner_forValut.setSelection(getIndex(spiner_forValut, currentCredit.getValyute_currency().getAbbr()));

            if (currentCredit.getProcent_interval() == forMoth) {
                spiner_procent.setSelection(0);
            } else if (currentCredit.getProcent_interval() == forYear) {
                spiner_procent.setSelection(1);
            } else if (currentCredit.getProcent_interval() == forWeek) {
                spiner_procent.setSelection(2);
            } else if (currentCredit.getProcent_interval() == forDay) {
                spiner_procent.setSelection(3);
            }
            periodCred.setText(Long.toString(currentCredit.getPeriod_time() / currentCredit.getPeriod_time_tip()));

            if (currentCredit.getPeriod_time_tip() == forMoth) {
                spinner_peiod.setSelection(0);
            } else if (currentCredit.getPeriod_time_tip() == forYear) {
                spinner_peiod.setSelection(1);
            } else if (currentCredit.getPeriod_time_tip() == forWeek) {
                spinner_peiod.setSelection(2);
            } else if (currentCredit.getPeriod_time_tip() == forDay) {
                spinner_peiod.setSelection(3);
            }
            if (!currentCredit.getKey_for_include()) {
                isOpkey.setChecked(false);
                spiner_trasnact.setVisibility(View.GONE);
            }

            for (ReckingCredit temp : currentCredit.getReckings()) {
                Log.d("gogogo", "onCreateView: " + temp.getAmount());
                Log.d("gogogo", "onCreateView: " + temp.getPayDate() + "  ==  " + currentCredit.getTake_time().getTimeInMillis());
                if (temp.getPayDate().getTimeInMillis() == currentCredit.getTake_time().getTimeInMillis()) {
                    transactionCred.setText(parseToWithoutNull(temp.getAmount()));
                    if (currentCredit.getKey_for_include())
                        for (Account acca : accounts) {
                            if (acca.getId().matches(temp.getAccountId()))
                                spiner_trasnact.setSelection(getIndex(spiner_trasnact, acca.getName()));
                        }
                    break;
                }
            }

            firstCred.setText(dateFormat.format(currentCredit.getTake_time().getTime()));

            Calendar calc = (Calendar) currentCredit.getTake_time().clone();

            if (currentCredit.getProcent_interval() == forMoth) {
                calc.add(Calendar.MONTH, (int) (currentCredit.getPeriod_time() / currentCredit.getPeriod_time_tip()));
            } else if (currentCredit.getProcent_interval() == forYear) {
                calc.add(Calendar.YEAR, (int) (currentCredit.getPeriod_time() / currentCredit.getPeriod_time_tip()));
            } else if (currentCredit.getProcent_interval() == forWeek) {
                calc.add(Calendar.WEEK_OF_YEAR, (int) (currentCredit.getPeriod_time() / currentCredit.getPeriod_time_tip()));
            } else if (currentCredit.getProcent_interval() == forDay) {
                calc.add(Calendar.DAY_OF_YEAR, (int) (currentCredit.getPeriod_time() / currentCredit.getPeriod_time_tip()));
            }

            lastCred.setText(dateFormat.format(calc.getTime()));
            argFirst[0] = currentCredit.getTake_time().get(Calendar.YEAR);
            argFirst[1] = currentCredit.getTake_time().get(Calendar.MONTH);
            argFirst[2] = currentCredit.getTake_time().get(Calendar.DAY_OF_MONTH);
            argLast[0] = calc.get(Calendar.YEAR);
            argLast[1] = calc.get(Calendar.MONTH);
            argLast[2] = calc.get(Calendar.DAY_OF_MONTH);
        }
        return V;
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void openNotifSettingDialog() {
        final Dialog dialog = new Dialog(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.notif_settings, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        LinearLayout llNotifSettingBody = (LinearLayout) dialogView.findViewById(R.id.llNotifSettingBody);
        llNotifSettingBody.removeAllViews();
        final Spinner sp = new Spinner(getContext());
        final ArrayList<CheckBox> chbs = new ArrayList<>();
        switch (mode) {
            case PocketAccounterGeneral.EVERY_WEEK:
                String[] weekdays = getResources().getStringArray(R.array.week_days);
                for (int i = 0; i < weekdays.length; i++) {
                    CheckBox chb = new CheckBox(getContext());
                    if (i == 0) chb.setChecked(true);
                    chb.setText(weekdays[i]);
                    chb.setTextSize(getResources().getDimension(R.dimen.five_dp));
                    chb.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar_text_color));
                    chb.setPadding(0, 0, (int) getResources().getDimension(R.dimen.ten_sp), 0);
                    chbs.add(chb);
                    llNotifSettingBody.addView(chb);
                }
                break;
            case PocketAccounterGeneral.EVERY_MONTH:
                LinearLayout ll = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(lp);

                String[] days = new String[31];
                for (int i = 0; i < 31; i++) {
                    days[i] = Integer.toString(i + 1);
                }
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, days);
                sp.setAdapter(adapter1);
                TextView tv = new TextView(getContext());
                tv.setText(getResources().getString(R.string.choose_date) + ": ");
                tv.setTextSize(getResources().getDimension(R.dimen.five_dp));
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar_text_color));
                LinearLayout.LayoutParams tvlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvlp.setMargins(0, 0, (int) getResources().getDimension(R.dimen.ten_sp), 0);
                tv.setLayoutParams(tvlp);
                ll.addView(tv);
                ll.addView(sp);
                llNotifSettingBody.addView(ll);
                break;
        }
        ImageView btnYes = (ImageView) dialogView.findViewById(R.id.ivAccountSave);
        btnYes.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          String text = "";
                                          if (mode.matches(PocketAccounterGeneral.EVERY_WEEK)) {
                                              for (int i = 0; i < chbs.size(); i++) {
                                                  if (chbs.get(i).isChecked()) {
                                                      text = text + i + ",";
                                                  }
                                              }
                                          }
                                          if (mode.matches(PocketAccounterGeneral.EVERY_MONTH)) {
                                              text = sp.getSelectedItem() + "";
                                          }
                                          if (!text.matches("") && text.endsWith(","))
                                              sequence = text.substring(0, text.length() - 1);
                                          else
                                              sequence = text;
                                          dialog.dismiss();
                                      }
                                  }
        );
        ImageView btnNo = (ImageView) dialogView.findViewById(R.id.ivAccountClose);
        btnNo.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         dialog.dismiss();
                                     }
                                 }
        );
        dialog.show();
    }

    public void forDateSyncFirst() {
        Date AAa = (new Date());
        Calendar calend = new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2]);
        AAa.setTime(calend.getTimeInMillis());

        firstCred.setText(dateFormat.format(AAa));

        int period_long = 1;
        if (!periodCred.getText().toString().matches("")) {
            period_long = Integer.parseInt(periodCred.getText().toString());
            switch (spinner_peiod.getSelectedItemPosition()) {
                case 0:
                    //moth
                    calend.add(Calendar.MONTH, period_long);

                    break;
                case 1:
                    //year
                    calend.add(Calendar.YEAR, period_long);
                    break;
                case 2:
                    //week
                    calend.add(Calendar.WEEK_OF_YEAR, period_long);
                    break;
                case 3:
                    //day
                    calend.add(Calendar.DAY_OF_YEAR, period_long);

                    break;
                default:
                    break;
            }


            long forCompute = calend.getTimeInMillis();
            // forCompute+=period_long;

            AAa.setTime(forCompute);
            lastCred.setText(dateFormat.format(AAa));
        } else {
            periodCred.setError(getString(R.string.first_enter_period));
        }
    }

    public void forDateSyncLast() {
        Date AAa = (new Date());
        Calendar calend = new GregorianCalendar(argLast[0], argLast[1], argLast[2]);
        AAa.setTime(calend.getTimeInMillis());
        lastCred.setText(dateFormat.format(AAa));

        int period_long = 1;
        if (!periodCred.getText().toString().matches("")) {
            period_long = Integer.parseInt(periodCred.getText().toString());
            switch (spinner_peiod.getSelectedItemPosition()) {
                case 0:
                    //moth
                    calend.add(Calendar.MONTH, -period_long);
                    break;
                case 1:
                    //year
                    calend.add(Calendar.YEAR, -period_long);
                    break;
                case 2:
                    //week
                    calend.add(Calendar.WEEK_OF_YEAR, -period_long);
                    break;
                case 3:
                    //day
                    calend.add(Calendar.DAY_OF_YEAR, -period_long);

                    break;
                default:
                    break;
            }
            long forCompute = calend.getTimeInMillis();
            // forCompute+=period_long;

            AAa.setTime(forCompute);
            firstCred.setText(dateFormat.format(AAa));
        } else {
            periodCred.setError(getString(R.string.first_enter_period));

        }
    }

    StringBuilder sb;


    private void openDialog() {
        final Dialog dialog = new Dialog(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.info_about_all, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        final TextView value = (TextView) dialogView.findViewById(R.id.textView9);
        final TextView procent = (TextView) dialogView.findViewById(R.id.textView11);
        final EditText solution = (EditText) dialogView.findViewById(R.id.edit_result);
        ImageView cancel = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowCancel);
        ImageView save = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowSave);
        sb = new StringBuilder(procentCred.getText().toString());
        Log.d("sbb", sb.toString());
        int a = sb.toString().indexOf('%');
        if (a != -1)
            sb.deleteCharAt(a);
        value.setText(valueCred.getText().toString());
        procent.setText(procentCred.getText().toString());
        solution.setText(parseToWithoutNull(Double.parseDouble(valueCred.getText().toString()) * (1d + Double.parseDouble(sb.toString()) / 100)));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long procent_inter = 1;
                switch (spiner_procent.getSelectedItemPosition()) {
                    case 0:
                        procent_inter *= forMoth;
                        break;
                    case 1:
                        procent_inter *= forYear;
                        break;
                    case 2:
                        procent_inter *= forWeek;
                        break;
                    case 3:
                        procent_inter *= forDay;
                        break;
                }
                long period_inter = Long.parseLong(periodCred.getText().toString());
                long period_tip = 0;
                switch (spinner_peiod.getSelectedItemPosition()) {
                    case 0:
                        period_inter *= forMoth;
                        period_tip = forMoth;
                        break;
                    case 1:
                        period_inter *= forYear;
                        period_tip = forYear;
                        break;
                    case 2:
                        period_inter *= forWeek;
                        period_tip = forWeek;
                        break;
                    case 3:
                        period_inter *= forDay;
                        period_tip = forDay;
                        break;
                }

                boolean key = true;
                key = isOpkey.isChecked();

                String sloution = solution.getText().toString();
                if (sloution.indexOf(',') != -1)
                    sloution = sloution.substring(0, sloution.indexOf(',')) + "." + sloution.substring(sloution.indexOf(',') + 1, sloution.length());
                CreditDetials A1 ;
                Account account = accounts.get(spiner_trasnact.getSelectedItemPosition());

                if (account.getIsLimited() && key) {
                    double limit = account.getLimite();
                    double accounted = logicManager.isLimitAccess(account, new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2]));
                    if (isEdit()&&currentCredit.getKey_for_include()){
                        for (ReckingCredit reckingCredit : currentCredit.getReckings()) {
                            if (currentCredit.getTake_time().getTimeInMillis() == reckingCredit.getPayDate().getTimeInMillis())
                                accounted=+commonOperations.getCost(reckingCredit.getPayDate(), currentCredit.getValyute_currency(), reckingCredit.getAmount());
                        }
                    }
                    accounted = accounted - commonOperations.getCost((new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2])), currencies.get(spiner_forValut.getSelectedItemPosition()), account.getCurrency(), Double.parseDouble(transactionCred.getText().toString()));
                    if (-limit > accounted) {
                        Toast.makeText(context, R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (isEdit()) {
                    Log.d("sbb",Double.parseDouble(sb.toString())+"" );
                    A1 = new CreditDetials(selectedIcon, nameCred.getText().toString(), new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2]),
                            Double.parseDouble(sb.toString()), procent_inter, period_inter, period_tip, key, Double.parseDouble(valueCred.getText().toString()),
                            currencies.get(spiner_forValut.getSelectedItemPosition()), Double.parseDouble(sloution), currentCredit.getMyCredit_id());

                } else {
                    A1 = new CreditDetials(selectedIcon, nameCred.getText().toString(), new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2]),
                            Double.parseDouble(sb.toString()), procent_inter, period_inter, period_tip, key, Double.parseDouble(valueCred.getText().toString()),
                            currencies.get(spiner_forValut.getSelectedItemPosition()), Double.parseDouble(sloution), System.currentTimeMillis());
                }
                A1.__setDaoSession(daoSession);
                String transactionCredString = transactionCred.getText().toString();
                if (!transactionCredString.matches("")) {
                    ReckingCredit first_pay;

                    if (key && accounts.size() != 0) {
                        first_pay = new ReckingCredit((new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2])), Double.parseDouble(transactionCredString), accounts.get(spiner_trasnact.getSelectedItemPosition()).getId(),
                                A1.getMyCredit_id(), getString(R.string.this_first_comment));
                    } else {
                        first_pay = new ReckingCredit((new GregorianCalendar(argFirst[0], argFirst[1], argFirst[2])), Double.parseDouble(transactionCredString), "pustoy",
                                A1.getMyCredit_id(), getString(R.string.this_first_comment));
                    }
                    first_pay.__setDaoSession(daoSession);
                    if (isEdit()){
                        List<ReckingCredit> tempiker = currentCredit.getReckings();
                        boolean iskeeeep = true;
                        for (ReckingCredit temp : tempiker) {
                            if (temp.getPayDate().getTimeInMillis() == currentCredit.getTake_time().getTimeInMillis()) {
                                first_pay.setId(temp.getId());
                                logicManager.insertReckingCredit(first_pay);
                                iskeeeep = false;
                                break;
                            }
                        }
                        if (iskeeeep) {
                           logicManager.insertReckingCredit(first_pay);
                        }
                    }
                else {
                        logicManager.insertReckingCredit(first_pay);
                    }
                }

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dialogView.getWindowToken(), 0);

                A1.setInfo(mode + ":" + sequence);
                if (isEdit()) {
                    logicManager.insertCredit(A1);
                    //TODO CLOSE ALL FRAGMENTS
                } else {
                    switch(logicManager.insertCredit(A1)) {
                        case LogicManagerConstants.SAVED_SUCCESSFULL: {
                            break;
                        }
                    }
                }
                dialog.dismiss();
                if (isEdit()&&!fromMainWindow) {

                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new CreditTabLay());

                }
                else if(fromMainWindow){

                    if(modeFromMain==PocketAccounterGeneral.EXPANSE_MODE)
                        logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE,posFromMain,Long.toString(A1.getMyCredit_id()));
                    else
                        logicManager.changeBoardButton(PocketAccounterGeneral.INCOME,posFromMain,Long.toString(A1.getMyCredit_id()));


                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inPreferredConfig= Bitmap.Config.RGB_565;
                    Bitmap temp=BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(A1.getIcon_ID(),"drawable",context.getPackageName()),options);
                    temp=Bitmap.createScaledBitmap(temp,(int)getResources().getDimension(R.dimen.thirty_dp),(int)getResources().getDimension(R.dimen.thirty_dp),true);
//                    dataCache.getBoardBitmapsCache().put(modeFromMain, temp);
                    paFragmentManager.displayMainWindow();
                }
                else {

                    paFragmentManager.getFragmentManager().popBackStack();
                }

                onSucsessed = true;
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(7 * width / 8, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void addEventLis(CreditFragment.EventFromAdding even) {
        eventLis = even;
    }

    public String parseToWithoutNull(double A) {
        if (A == (int) A)
            return Integer.toString((int) A);
        else {
            DecimalFormat format = new DecimalFormat("0.##");
            return format.format(A);
        }
    }

    @Override
    public void onDetach() {
        if(!fromMainWindow){
            if (!onSucsessed && currentCredit == null)
                eventLis.canceledAdding();
            else if (currentCredit == null) {
                eventLis.addedCredit();
            }
        }
        super.onDetach();
    }

}
