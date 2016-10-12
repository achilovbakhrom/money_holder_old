package com.jim.pocketaccounter;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jim.pocketaccounter.credit.notificat.NotificationManagerCredit;
import com.jim.pocketaccounter.database.AutoMarket;
//import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.debt.PocketClassess;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.DrawerInitializer;
import com.jim.pocketaccounter.managers.SettingsManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.modulesandcomponents.components.DaggerPocketAccounterActivityComponent;
import com.jim.pocketaccounter.utils.CircleImageView;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.cache.DataCache;
import com.jim.pocketaccounter.utils.navdrawer.LeftSideDrawer;
import com.jim.pocketaccounter.utils.password.OnPasswordRightEntered;
import com.jim.pocketaccounter.utils.password.PasswordWindow;
import com.jim.pocketaccounter.utils.record.RecordExpanseView;
import com.jim.pocketaccounter.utils.record.RecordIncomesView;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.modulesandcomponents.components.PocketAccounterActivityComponent;
import com.jim.pocketaccounter.modulesandcomponents.modules.PocketAccounterActivityModule;
import com.jim.pocketaccounter.widget.WidgetKeys;
import com.jim.pocketaccounter.widget.WidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

public class PocketAccounter extends AppCompatActivity {
    TextView userName, userEmail;
    CircleImageView userAvatar;
    public static Toolbar toolbar;

    public static LeftSideDrawer drawer;
    private ListView lvLeftMenu;
    //    public static FinanceManager financeManager;
    private FragmentManager fragmentManager;
    SharedPreferences spref;
    SharedPreferences.Editor ed;
    private RelativeLayout rlRecordsMain, rlRecordIncomes, rlRecordBalance;
    private TextView tvRecordIncome, tvRecordBalanse, tvRecordExpanse;
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private RecordExpanseView expanseView;
    private RecordIncomesView incomeView;
    private PasswordWindow pwPassword;
    private Calendar date;
    private Spinner spToolbar;
    public static boolean isCalcLayoutOpen = false;
    public static boolean openActivity = false;
    boolean downloadnycCanRest = true;
    Uri imageUri;
    ImageView fabIconFrame;
    public static final int key_for_restat = 10101;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-accounter.appspot.com");
    //    DownloadImageTask imagetask;
    View mainRoot;
    private AnimationDrawable mAnimationDrawable;
    private NotificationManagerCredit notific;
    boolean keyFromCalc = false;
    public static boolean PRESSED = false;
    int WidgetID;
    public static boolean keyboardVisible = false;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    @Inject
    SharedPreferences preferences;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    SettingsManager settingsManager;
    @Inject
    @Named(value = "display_formatter")
    SimpleDateFormat format;
    @Inject
    DrawerInitializer drawerInitializer;
    @Inject
    CommonOperations commonOperations;
    @Inject
    DataCache dataCache;
    @Inject
    WarningDialog warningDialog;
    PocketAccounterActivityComponent component;

    public PocketAccounterActivityComponent component(PocketAccounterApplication application) {
        if (component == null) {
            component = DaggerPocketAccounterActivityComponent
                    .builder()
                    .pocketAccounterActivityModule(new PocketAccounterActivityModule(this, (Toolbar) findViewById(R.id.toolbar)))
                    .pocketAccounterApplicationComponent(application.component())
                    .build();
        }
        return component;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        keyFromCalc=false;
//        settingsManager.setup();
        setContentView(R.layout.pocket_accounter);
        component((PocketAccounterApplication) getApplication()).inject(this);
        toolbarManager.init();
        date = Calendar.getInstance();
        treatToolbar();
        paFragmentManager.initialize();
        dataCache.getCategoryEditFragmentDatas().setDate(date);
        pwPassword = (PasswordWindow) findViewById(R.id.pwPassword);
        checkAutoMarket();
//        SmsParseKeys smsParseKeys = new SmsParseKeys();
//        smsParseKeys.setNumber("+99931121");
//        smsParseKeys.setTemplates(new String[] {"salmkga", "sadsdsa", "sdasd"});
//
//        rlRecordsMain = (RelativeLayout) findViewById(R.id.rlRecordExpanses);
//        tvRecordIncome = (TextView) findViewById(R.id.tvRecordIncome);
//        tvRecordBalanse = (TextView) findViewById(R.id.tvRecordBalanse);
//        rlRecordIncomes = (RelativeLayout) findViewById(R.id.rlRecordIncomes);
//        ivToolbarMostRight = (ImageView) findViewById(R.id.ivToolbarMostRight);
//        spToolbar = (Spinner) toolbar.findViewById(R.id.spToolbar);
//        ivToolbarExcel = (ImageView) findViewById(R.id.ivToolbarExcel);
//        rlRecordBalance = (RelativeLayout) findViewById(R.id.rlRecordBalance);
//        rlRecordBalance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PRESSED) return;
//                replaceFragment(new RecordDetailFragment(date));
//                PRESSED = true;
//            }
//        });
//        pwPassword = (PasswordWindow) findViewById(R.id.pwPassword);
//
//        tvRecordExpanse = (TextView) findViewById(R.id.tvRecordExpanse);
//        date = Calendar.getInstance();
//        initialize(date);
//        notific = new NotificationManagerCredit(PocketAccounter.this);
//
//        switch (getIntent().getIntExtra("TIP", 0)) {
//            case AlarmReceiver.TO_DEBT:
//                replaceFragment(new DebtBorrowFragment(), PockerTag.DEBTS);
//                break;
//            case AlarmReceiver.TO_CRIDET:
//                replaceFragment(new CreditTabLay(), PockerTag.CREDITS);
//                break;
//        }
//        boolean notif = prefs.getBoolean("general_notif", true);
//        if (!notif) {
//            (new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        notific.cancelAllNotifs();
//                    } catch (Exception o) {
//                    }
//                }
//            })).start();
//        }
//
//
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("secure", false)) {
            pwPassword.setVisibility(View.VISIBLE);
            pwPassword.setOnPasswordRightEnteredListener(new OnPasswordRightEntered() {
                @Override
                public void onPasswordRight() {
                    pwPassword.setVisibility(View.GONE);
                }

                @Override
                public void onExit() {
                    finish();
                }
            });}

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("secure", false)) {
            pwPassword.setVisibility(View.VISIBLE);
            pwPassword.setOnPasswordRightEnteredListener(new OnPasswordRightEntered() {
                @Override
                public void onPasswordRight() {
                    pwPassword.setVisibility(View.GONE);
                }

                @Override
                public void onExit() {
                    finish();
                }
            });
        }

    }

    public Calendar getDate() {
        return date;
    }

    private Calendar beginDate;
    private Calendar endDate;
    private int state = 0;
    private EditText startTimeFilter;
    private EditText endTimeFilter;

    private void checkAutoMarket() {
        for (AutoMarket au : daoSession.getAutoMarketDao().loadAll()) {
            String[] days = au.getDates().split(",");
            for (String day : days) {
                if (au.getType() && day.matches("" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH))) {
                    FinanceRecord financeRecord = new FinanceRecord();
                    financeRecord.setRecordId("auto" + UUID.randomUUID().toString());
                    financeRecord.setCategory(au.getRootCategory());
                    financeRecord.setSubCategory(au.getSubCategory());
                    financeRecord.setCurrency(au.getCurrency());
                    financeRecord.setAccount(au.getAccount());
                    financeRecord.setAmount(au.getAmount());
                    financeRecord.setDate(Calendar.getInstance());
                    boolean tek = false;
                    for (FinanceRecord fn : daoSession.getFinanceRecordDao().loadAll()) {
                        if (fn.getDate().compareTo(financeRecord.getDate()) == 0 && fn.getRecordId().startsWith("auto")
                                && fn.getCategory().getId().matches(financeRecord.getCategory().getId())
                                && fn.getSubCategory().getId().matches(financeRecord.getSubCategory().getId())) {
                            tek = true;
                            break;
                        } else if (au.getType() && day.matches("" + getResources().getStringArray(R.array.week_days)[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)])) {
                            tek = true;
                        }
                    }
                    if (!tek)
                        daoSession.getFinanceRecordDao().insertOrReplace(financeRecord);
                }
            }
        }
    }

    public void treatToolbar() {
        // toolbar set
        toolbarManager.setImageToHomeButton(R.drawable.ic_drawer);
        toolbarManager.setTitle(getResources().getString(R.string.app_name));
        toolbarManager.setSubtitle(format.format(dataCache.getEndDate().getTime()));

        toolbarManager.setOnHomeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerInitializer.getDrawer().openLeftSide();
            }
        });
        toolbarManager.setSpinnerVisibility(View.GONE);
        toolbarManager.setToolbarIconsVisibility(View.VISIBLE, View.GONE, View.VISIBLE);
        toolbarManager.setSearchView(drawerInitializer, format, paFragmentManager, findViewById(R.id.main));
        toolbarManager.setImageToSecondImage(R.drawable.finance_calendar);
        toolbarManager.setSearchView(drawerInitializer, format, paFragmentManager, findViewById(R.id.main));
//        toolbarManager.setImageToStartImage(R.drawable.ic_search_black_24dp);
        toolbarManager.setImageToStartImage(R.drawable.ic_search_black_24dp);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PocketAccounter.this);
                final View dialogView = getLayoutInflater().inflate(R.layout.date_picker, null);
                dialogView.findViewById(R.id.dp).setVisibility(View.VISIBLE);
//                dialogView.findViewById(R.id.rlDatePickerPeriod).setVisibility(View.GONE);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.dp);
                ImageView ivDatePickOk = (ImageView) dialogView.findViewById(R.id.ivDatePickOk);
                ivDatePickOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = preferences.getString("balance_solve", "0");
                        Calendar begin, end = Calendar.getInstance();
                        if (key.equals("0")) {
                            Calendar firstDay = commonOperations.getFirstDay();
                            if (firstDay == null) {
                                firstDay = Calendar.getInstance();
                                firstDay.set(2016, Calendar.JANUARY, 1);
                            }
                            begin = (Calendar) firstDay.clone();
                            end.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                        }
                        else {
                            begin = Calendar.getInstance();
                            begin.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                            begin.set(Calendar.HOUR_OF_DAY, 0);
                            begin.set(Calendar.MINUTE, 0);
                            begin.set(Calendar.SECOND, 0);
                            begin.set(Calendar.MILLISECOND, 0);
                            end.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                            end.set(Calendar.HOUR_OF_DAY, 23);
                            end.set(Calendar.MINUTE, 59);
                            end.set(Calendar.SECOND, 59);
                            end.set(Calendar.MILLISECOND, 59);
                        }
                        dataCache.setBeginDate(begin);
                        dataCache.setEndDate(end);
                        long countOfDays = 0;
                        if (end.compareTo(Calendar.getInstance()) >= 0) {
                            countOfDays = commonOperations.betweenDays(Calendar.getInstance(), end)-1;
                            paFragmentManager.getLvpMain().setCurrentItem(5000 + (int)countOfDays, false);
                        }
                        else {
                            countOfDays = commonOperations.betweenDays(end, Calendar.getInstance())-1;
                            paFragmentManager.getLvpMain().setCurrentItem(5000 - (int)countOfDays, false);
                        }
                        if (paFragmentManager.getCurrentFragment() != null)
                            paFragmentManager.getCurrentFragment().update();
                        dialog.dismiss();
                    }
                });
                ImageView ivDatePickCancel = (ImageView) dialogView.findViewById(R.id.ivDatePickCancel);
                ivDatePickCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (paFragmentManager.getFragmentManager().getBackStackEntryCount() > 0) {
            if (paFragmentManager.getFragmentManager().findFragmentById(R.id.flMain) != null &&
                    paFragmentManager.getFragmentManager().findFragmentById(R.id.flMain).
                            getClass().getName().equals(PocketClassess.SEARCH_FRAGMENT)) {
                toolbarManager.closeSearchTools();
            } else
            paFragmentManager.remoteBackPress();
        } else {
            warningDialog.setOnYesButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PocketAccounter.super.onBackPressed();
                }
            });
            warningDialog.setOnNoButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    warningDialog.dismiss();
                }
            });
            warningDialog.show();
        }
    }
//
//    public Calendar getDate() {
//        return date;
//    }
//

    //
//
//    public void calculateBalance(Calendar date) {
//        if (PocketAccounter.financeManager == null) return;
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String balanceSolve = prefs.getString("balance_solve", "0");
//        String whole = "0", currentDay = "1";
//        Calendar beginTime = (Calendar) date.clone();
//        beginTime.set(Calendar.HOUR_OF_DAY, 0);
//        beginTime.set(Calendar.MINUTE, 0);
//        beginTime.set(Calendar.SECOND, 0);
//        beginTime.set(Calendar.MILLISECOND, 0);
//        Calendar endTime = (Calendar) date.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 23);
//        endTime.set(Calendar.MINUTE, 59);
//        endTime.set(Calendar.SECOND, 59);
//        endTime.set(Calendar.MILLISECOND, 59);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
//        ArrayList<FinanceRecord> records = new ArrayList<>();
//        if (balanceSolve.matches(whole)) {
//            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
//                if (PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(endTime) <= 0)
//                    records.add(PocketAccounter.financeManager.getRecords().get(i));
//            }
//        } else {
//            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
//                if (PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(beginTime) >= 0 &&
//                        PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(endTime) <= 0)
//                    records.add(PocketAccounter.financeManager.getRecords().get(i));
//            }
//        }
//        double income = 0.0, expanse = 0.0, balance = 0.0;
//        for (int i = 0; i < records.size(); i++) {
//            if (records.get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
//                income = income + PocketAccounterGeneral.getCost(records.get(i));
//            else
//                expanse = expanse + PocketAccounterGeneral.getCost(records.get(i));
//        }
//        for (Account account:PocketAccounter.financeManager.getAccounts()) {
//            if (account.getLimitCurrency() != null)
//                income = income + PocketAccounterGeneral.getCost(date, account.getStartMoneyCurrency(), account.getAmount());
//        }
//        //calculating debt borrows
//        if (balanceSolve.matches(whole)) {
//            for (int i=0; i<PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
//                DebtBorrow debtBorrow = PocketAccounter.financeManager.getDebtBorrows().get(i);
//                if (debtBorrow.isCalculate()) {
//                    if (debtBorrow.getTakenDate().compareTo(endTime) <= 0){
//                        if (debtBorrow.getType() == DebtBorrow.DEBT) {
//                            income = income + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
//                        }
//                        else {
//                            expanse = expanse + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
//                        }
//                    }
//                    for (Recking recking : debtBorrow.getReckings()) {
//                        Calendar calendar = Calendar.getInstance();
//                        try {
//                            calendar.setTime(simpleDateFormat.parse(recking.getPayDate()));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        if (calendar.compareTo(endTime) <= 0) {
//                            if (debtBorrow.getType() == DebtBorrow.DEBT)
//                                expanse = expanse + PocketAccounterGeneral.getCost(calendar, debtBorrow.getCurrency(), recking.getAmount());
//                            else
//                                income = income + PocketAccounterGeneral.getCost(calendar, debtBorrow.getCurrency(), recking.getAmount());
//                        }
//                    }
//                }
//            }
//        } else {
//            for (int i=0; i<PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
//                DebtBorrow debtBorrow = PocketAccounter.financeManager.getDebtBorrows().get(i);
//                if (debtBorrow.isCalculate()) {
//                    if (debtBorrow.getTakenDate().compareTo(beginTime) >= 0 && debtBorrow.getTakenDate().compareTo(endTime) <= 0) {
//                        if (debtBorrow.getType() == DebtBorrow.BORROW) {
//                            income = income + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
//                        }
//                        else {
//                            expanse = expanse + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), debtBorrow.getAmount());
//                        }
//                    }
//                    for (Recking recking : debtBorrow.getReckings()) {
//                        Calendar calendar = Calendar.getInstance();
//                        try {
//                            calendar.setTime(simpleDateFormat.parse(recking.getPayDate()));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        if (calendar.compareTo(beginTime) >= 0 && calendar.compareTo(endTime) <= 0) {
//                            if (debtBorrow.getType() == DebtBorrow.BORROW)
//                                expanse = expanse + PocketAccounterGeneral.getCost(calendar, debtBorrow.getCurrency(), recking.getAmount());
//                            else
//                                income = income + PocketAccounterGeneral.getCost(calendar, debtBorrow.getCurrency(), recking.getAmount());
//                        }
//                    }
//                }
//            }
//        }
//
//        if (balanceSolve.matches(whole)) {
//            for (CreditDetials creditDetials : PocketAccounter.financeManager.getCredits()) {
//                if (creditDetials.getKey_for_include()) {
//                    for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(reckingCredit.getPayDate());
//                        if (calendar.compareTo(endTime) <= 0)
//                            expanse = expanse + PocketAccounterGeneral.getCost(calendar, creditDetials.getValyute_currency(), reckingCredit.getAmount());
//                    }
//                }
//            }
//        } else {
//            for (CreditDetials creditDetials : PocketAccounter.financeManager.getCredits()) {
//                if (creditDetials.getKey_for_include()) {
//                    for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(reckingCredit.getPayDate());
//                        if (calendar.compareTo(beginTime) >= 0 && calendar.compareTo(endTime) <= 0)
//                            expanse = expanse + PocketAccounterGeneral.getCost(calendar, creditDetials.getValyute_currency(), reckingCredit.getAmount());
//                    }
//                }
//            }
//        }
//        balance = income - expanse;
//
//        //new method
////        Log.d("sss", "calculateBalance: init "+financeManager.getMainCurrency().getAbbr());
////        ArrayList<Double> result = financeManager.calculateBalance(date);
////        double income = result.get(0);
////        double expanse = result.get(1);
////        double balance = result.get(2);
////        Log.d("sss", "income "+income + " exp: "+expanse+" balance: "+balance);
//        String mainCurrencyAbbr = PocketAccounter.financeManager.getMainCurrency().getAbbr();
//        DecimalFormat decFormat = new DecimalFormat("0.00");
//        tvRecordIncome.setText(decFormat.format(income) + mainCurrencyAbbr);
//        tvRecordExpanse.setText(decFormat.format(expanse) + mainCurrencyAbbr);
//        tvRecordBalanse.setText(decFormat.format(balance) + mainCurrencyAbbr);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(PreferenceManager.getDefaultSharedPreferences(PocketAccounter.this).getBoolean(KEY_FOR_INSTALAZING,false)&&keyFromCalc){
//            PreferenceManager.getDefaultSharedPreferences(PocketAccounter.this).edit().putBoolean(KEY_FOR_INSTALAZING,false).apply();
//            financeManager = new FinanceManager(this);
//            initialize(new GregorianCalendar());
//        }
//    }
    @Override
    protected void onStop() {
        super.onStop();
//        financeManager.saveRecords();
//        SharedPreferences sPref;
//        sPref = getSharedPreferences("infoFirst", MODE_PRIVATE);
//        WidgetID = sPref.getInt(WidgetKeys.SPREF_WIDGET_ID, -1);
//        if (WidgetID >= 0) {
//            if (AppWidgetManager.INVALID_APPWIDGET_ID != WidgetID)
//                WidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this),
//                        WidgetID);
//        }
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean notif = prefs.getBoolean("general_notif", true);
//        if (notif) {
//            try {
//                notific.cancelAllNotifs();
//                notific.notificSetDebt();
//                notific.notificSetCredit();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            notific.cancelAllNotifs();
//        }
//        financeManager.saveRecords();
        SharedPreferences sPref;
        sPref = getSharedPreferences("infoFirst", MODE_PRIVATE);
        WidgetID = sPref.getInt(WidgetKeys.SPREF_WIDGET_ID, -1);
        if (WidgetID >= 0) {
            if (AppWidgetManager.INVALID_APPWIDGET_ID != WidgetID)
                WidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this),
                        WidgetID);
        }
        drawerInitializer.onStopSuniy();
//        for (AbstractDao temp:daoSession.getAllDaos()) {
        }
//
//    private void fillLeftMenu() {
//        String[] cats = getResources().getStringArray(R.array.drawer_cats);
//        String[] financeSubItemTitles = getResources().getStringArray(R.array.finance_subitems);
//        String[] financeSubItemIcons = getResources().getStringArray(R.array.finance_subitem_icons);
//        String[] statisticsSubItemTitles = getResources().getStringArray(R.array.statistics_subitems);
//        String[] statisticsSubItemIcons = getResources().getStringArray(R.array.statistics_subitems_icons);
//        String[] debtSubItemTitles = getResources().getStringArray(R.array.debts_subitems);
//        String[] debtSubItemIcons = getResources().getStringArray(R.array.debts_subitem_icons);
//        ArrayList<LeftMenuItem> items = new ArrayList<LeftMenuItem>();
//
//        userAvatar = (CircleImageView) findViewById(R.id.userphoto);
//        userName = (TextView) findViewById(R.id.tvToolbarName);
//        userEmail = (TextView) findViewById(R.id.tvGoogleMail);
//
//        FABIcon fabIcon = (FABIcon) findViewById(R.id.fabDrawerNavIcon);
//        fabIconFrame = (ImageView) findViewById(R.id.iconFrameForAnim);
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
//            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
//
//        } else
//            fabIconFrame.setBackgroundResource(R.drawable.cloud_sign_in);
//
//
//        reg = new SignInGoogleMoneyHold(PocketAccounter.this, new SignInGoogleMoneyHold.UpdateSucsess() {
//            @Override
//            public void updateToSucsess() {
//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    imagetask = new DownloadImageTask(userAvatar);
//                    userName.setText(user.getDisplayName());
//                    userEmail.setText(user.getEmail());
//                    if (user.getPhotoUrl() != null) {
//                        try {
//                            imagetask.execute(user.getPhotoUrl().toString());
//
//                        } catch (Exception o) {
//                        }
//                        imageUri = user.getPhotoUrl();
//                    }
//
//                    showProgressDialog(getString(R.string.cheking_user));
//                    PocketAccounter.mySync.meta_Message(user.getUid(), new SyncBase.ChangeStateLisMETA() {
//                        @Override
//                        public void onSuccses(final long inFormat) {
//                            hideProgressDialog();
//                            Date datee = new Date();
//                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
//                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
//                            datee.setTime(inFormat);
//                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PocketAccounter.this);
//                            builder.setMessage(getString(R.string.sync_last_data_sign_up) + (new SimpleDateFormat("dd.MM.yyyy kk:mm")).format(datee))
//                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            showProgressDialog(getString(R.string.download));
//                                            PocketAccounter.mySync.downloadLast(user.getUid(), new SyncBase.ChangeStateLis() {
//                                                @Override
//                                                public void onSuccses() {
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            hideProgressDialog();
//                                                            PocketAccounter.financeManager = new FinanceManager(PocketAccounter.this);
//                                                            initialize(new GregorianCalendar());
//                                                            if (!drawer.isClosed()) {
//                                                                drawer.close();
//                                                            }
//                                                        }
//                                                    });
//                                                }
//
//                                                @Override
//                                                public void onFailed(String e) {
//                                                    hideProgressDialog();
//                                                }
//                                            });
//                                        }
//                                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    hideProgressDialog();
//                                    dialog.cancel();
//
//                                }
//                            });
//                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                                @Override
//                                public void onCancel(DialogInterface dialog) {
//                                    hideProgressDialog();
//                                }
//                            });
//                            builder.create().show();
//                        }
//
//                        @Override
//                        public void onFailed(Exception e) {
//                            hideProgressDialog();
//                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
//                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void updateToFailed() {
//                userName.setText(R.string.try_later);
//                userEmail.setText(R.string.err_con);
//            }
//        });
//
//        fabIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final FirebaseUser userim = FirebaseAuth.getInstance().getCurrentUser();
//                if (userim != null) {
//
//                    (new Handler()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PocketAccounter.this);
//                            builder.setMessage(R.string.sync_message)
//                                    .setPositiveButton(R.string.sync_short, new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            mAnimationDrawable.start();
//                                            financeManager.saveAllDatas();
//                                            mySync.uploadBASE(userim.getUid(), new SyncBase.ChangeStateLis() {
//                                                @Override
//                                                public void onSuccses() {
//                                                    mAnimationDrawable.stop();
//                                                    fabIconFrame.setBackgroundResource(R.drawable.cloud_sucsess);
//                                                    (new Handler()).postDelayed(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//
//                                                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
//                                                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
//
//                                                        }
//                                                    }, 2000);
//                                                }
//
//                                                @Override
//                                                public void onFailed(String e) {
//                                                    mAnimationDrawable.stop();
//                                                    fabIconFrame.setBackgroundResource(R.drawable.cloud_error);
//                                                    (new Handler()).postDelayed(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
//                                                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
//
//                                                        }
//                                                    }, 2000);
//                                                }
//                                            });
//
//                                        }
//                                    }).setNegativeButton(getString(R.string.cancel1), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//                            builder.create().show();
//                        }
//                    }, 150);
//                } else {
//                    drawer.close();
//                    (new Handler()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (spref.getBoolean("FIRSTSYNC", true)) {
//                                reg.openDialog();
//                            } else
//                                reg.regitUser();
//                        }
//                    }, 150);
//                }
//            }
//        });
//        LeftMenuItem main = new LeftMenuItem(cats[0], R.drawable.drawer_home);
//        main.setGroup(true);
//        items.add(main);
//        LeftMenuItem finance = new LeftMenuItem(cats[1], R.drawable.drawer_finance);
//        finance.setGroup(true);
//        items.add(finance);
//        for (int i = 0; i < financeSubItemTitles.length; i++) {
//            int resId = getResources().getIdentifier(financeSubItemIcons[i], "drawable", getPackageName());
//            LeftMenuItem subItem = new LeftMenuItem(financeSubItemTitles[i], resId);
//            subItem.setGroup(false);
//            items.add(subItem);
//        }
//        LeftMenuItem debts = new LeftMenuItem(cats[3], R.drawable.drawer_debts);
//        debts.setGroup(true);
//        items.add(debts);
//        for (int i = 0; i < debtSubItemTitles.length; i++) {
//            int resId = getResources().getIdentifier(debtSubItemIcons[i], "drawable", getPackageName());
//            LeftMenuItem subItem = new LeftMenuItem(debtSubItemTitles[i], resId);
//            subItem.setGroup(false);
//            items.add(subItem);
//        }
//        LeftMenuItem statistics = new LeftMenuItem(cats[2], R.drawable.drawer_statistics);
//        statistics.setGroup(true);
//        items.add(statistics);
//        for (int i = 0; i < statisticsSubItemTitles.length; i++) {
//            int resId = getResources().getIdentifier(statisticsSubItemIcons[i], "drawable", getPackageName());
//            LeftMenuItem subItem = new LeftMenuItem(statisticsSubItemTitles[i], resId);
//            subItem.setGroup(false);
//            items.add(subItem);
//        }
//        LeftMenuItem smsParse = new LeftMenuItem(cats[4], R.drawable.drawer_sms);
//        smsParse.setGroup(true);
//        items.add(smsParse);
//        LeftMenuItem settings = new LeftMenuItem(cats[5], R.drawable.drawer_settings);
//        settings.setGroup(true);
//        items.add(settings);
//        LeftMenuItem rateApp = new LeftMenuItem(cats[6], R.drawable.drawer_rate);
//        rateApp.setGroup(true);
//        items.add(rateApp);
//        LeftMenuItem share = new LeftMenuItem(cats[7], R.drawable.drawer_share);
//        share.setGroup(true);
//        items.add(share);
//        LeftMenuItem writeToUs = new LeftMenuItem(cats[8], R.drawable.drawer_letter_us);
//        writeToUs.setGroup(true);
//        items.add(writeToUs);
//        LeftMenuAdapter adapter = new LeftMenuAdapter(this, items);
//        lvLeftMenu.setAdapter(adapter);
//        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                if (getSupportFragmentManager().getBackStackEntryCount() == 0 && position == 0) {
//                    findViewById(R.id.change).setVisibility(View.VISIBLE);
//                } else {
//                    findViewById(R.id.change).setVisibility(View.GONE);
//                }
//                drawer.closeLeftSide();
//                drawer.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        switch (position) {
//                            case 0:
//                                findViewById(R.id.change).setVisibility(View.VISIBLE);
//                                PRESSED = false;
//                                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
//                                    FragmentManager fm = getSupportFragmentManager();
//                                    for (int i = 0; i < fm.getBackStackEntryCount(); i++)
//                                        fm.popBackStack();
//                                    initialize(date);
//                                }
//                                break;
//                            case 1:
////                                Intent ssettings = new Intent(PocketAccounter.this, SettingsActivity.class);
////                                PocketAccounter.openActivity=true;
////                                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
////                                    fragmentManager.popBackStack();
////                                }
////                                startActivityForResult(ssettings, key_for_restat);
//                            case 2:
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CURRENCY))
//                                    return;
//                                replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
////                                //Currency management
//                                break;
//                            case 3:
//
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CATEGORY))
//                                    return;
//                                replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
//                                //Category management
//                                break;
//                            case 4:
//
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.ACCOUNT))
//                                    return;
//                                replaceFragment(new AccountFragment(), PockerTag.ACCOUNT);
//                                //Accounting management
//                                break;
//                            case 5:
//                            case 6:
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.CREDITS))
//                                    return;
//                                replaceFragment(new CreditTabLay(), PockerTag.CREDITS);
//                                //Statistics by account
//                                break;
//                            case 7:
//
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.DEBTS))
//                                    return;
//                                replaceFragment(new DebtBorrowFragment(), PockerTag.DEBTS);
//                                //Statistics by income/expanse
//                                break;
//                            case 8:
//                            case 9:
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_ACCOUNT))
//                                    return;
//                                replaceFragment(new ReportByAccountFragment(), PockerTag.REPORT_ACCOUNT);
//                                // accounting debt
//                                break;
//                            case 10:
//
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_INCOM_EXPENSE))
//                                    return;
//                                replaceFragment(new TableBarFragment(), PockerTag.REPORT_INCOM_EXPENSE);
//                                break;
//                            case 11:
//
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.REPORT_CATEGORY))
//                                    return;
//                                replaceFragment(new ReportByCategory(), PockerTag.REPORT_CATEGORY);
//                                break;
//                            case 12:
//
//
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 1
//                                        && getSupportFragmentManager().findFragmentById(R.id.flMain).getTag()
//                                        .matches(com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT))
//                                    return;
//                                replaceFragment(new SMSParseFragment(), com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT);
//                                break;
//                            case 13:
//
//                                Intent zssettings = new Intent(PocketAccounter.this, SettingsActivity.class);
//                                PocketAccounter.openActivity=true;
//                                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
//                                    fragmentManager.popBackStack();
//                                }
//                                startActivityForResult(zssettings, key_for_restat);
//                                break;
//                            case 14:
//                                if (keyboardVisible) {
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);
//                                }
//
//                                findViewById(R.id.change).setVisibility(View.VISIBLE);
//                                Intent rate_app_web = new Intent(Intent.ACTION_VIEW);
//                                PocketAccounter.openActivity=true;
//
//                                rate_app_web.setData(Uri.parse(getString(R.string.rate_app_web)));
//                                startActivity(rate_app_web);
//                                break;
//                            case 15:
//                                if (keyboardVisible) {
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);
//                                }
//
//                                findViewById(R.id.change).setVisibility(View.VISIBLE);
//                                Intent Email = new Intent(Intent.ACTION_SEND);
//                                PocketAccounter.openActivity=true;
//
//                                Email.setType("text/email");
//                                Email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app));
//                                Email.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text));
//                                startActivity(Intent.createChooser(Email, getString(R.string.share_app)));
//                                break;
//                            case 16:
//                                if (keyboardVisible) {
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);
//                                }
//
//                                findViewById(R.id.change).setVisibility(View.VISIBLE);
//                                openGmail(PocketAccounter.this, new String[]{getString(R.string.to_email)}, getString(R.string.feedback_subject), getString(R.string.feedback_content));
//                                break;
//                        }
//
//                    }
//                }, 170);
//            }
//        });
//    }
//
//    public static void openGmail(Activity activity, String[] email, String subject, String content) {
//        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        PocketAccounter.openActivity=true;
//
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        emailIntent.setType("text/plain");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
//        final PackageManager pm = activity.getPackageManager();
//        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
//        ResolveInfo best = null;
//        for (final ResolveInfo info : matches)
//            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
//                best = info;
//        if (best != null)
//            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
//        activity.startActivity(emailIntent);
//    }
//
//
//
    @Override
    public void onRestart() {
        super.onRestart();
        keyFromCalc = true;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("secure", false) && !openActivity) {
            if (!drawerInitializer.getDrawer().isClosed())
                drawerInitializer.getDrawer().close();
            pwPassword.setVisibility(View.VISIBLE);
            pwPassword.setOnPasswordRightEnteredListener(new OnPasswordRightEntered() {
                @Override
                public void onPasswordRight() {
                    pwPassword.setVisibility(View.GONE);
                }

                @Override
                public void onExit() {
                    finish();
                }
            });
        }
        openActivity = false;
//
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            try {
//                if (downloadnycCanRest && imageUri != null) {
//                    imagetask = new DownloadImageTask(userAvatar);
//                    imagetask.execute(imageUri.toString());
//                }
//            } catch (Exception o) {
//            }
//        } else {
//            userAvatar.setImageResource(R.drawable.no_photo);
//            userName.setText(R.string.please_sign);
//            userEmail.setText(R.string.and_sync_your_data);
//
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        findViewById(R.id.change).setVisibility(View.VISIBLE);
        drawerInitializer.onActivResultForDrawerCalls(requestCode, resultCode, data);

        if (requestCode == key_for_restat && resultCode == 1111) {
            if (WidgetID >= 0) {
                if (AppWidgetManager.INVALID_APPWIDGET_ID != WidgetID)
                    WidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this),
                            WidgetID);
            }
            finish();
        }


    }


//
//    public static float convertDpToPixel(float dp, Context context) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//        return px;
//    }
//
//
//    private ProgressDialog mProgressDialog;
//
//
//    public void showProgressDialog(String message) {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(message);
//            mProgressDialog.setIndeterminate(true);
//        }
}

