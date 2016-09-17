package com.jim.pocketaccounter.managers;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.fragments.MainPageFragment;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.cache.DataCache;
import com.jim.pocketaccounter.utils.lvp.LoopViewPager;
import com.jim.pocketaccounter.utils.record.RecordExpanseView;
import com.jim.pocketaccounter.utils.record.RecordIncomesView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.jim.pocketaccounter.PocketAccounter.PRESSED;

/**
 * Created by DEV on 27.08.2016.
 */

public class PAFragmentManager {
    private PocketAccounter activity;
    private FragmentManager fragmentManager;
    private boolean keyboardVisible = false;
    private TextView tvRecordIncome, tvRecordExpanse, tvRecordBalanse;
    private int lastPosition = 0;
    private List<MainPageFragment> fragments;
    @Inject
    ReportManager reportManager;
    @Inject
    CommonOperations commonOperations;
    @Inject
    DataCache dataCache;
    public PAFragmentManager(PocketAccounter activity) {
        this.activity = activity;
        ((PocketAccounterApplication) activity.getApplicationContext()).component().inject(this);
        fragmentManager = activity.getSupportFragmentManager();
        fragments = new ArrayList<>();
    }
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void initialize(final Calendar begin, final Calendar end) {
        final LoopViewPager lvpMain = (LoopViewPager) activity.findViewById(R.id.lvpMain);
        PagerAdapter adapter = new LVPAdapter(getFragmentManager());
        lvpMain.setAdapter(adapter);
        lvpMain.setOffscreenPageLimit(5);
        Log.d("sss", "current item "+lvpMain.getCurrentItem());
        lvpMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("sss", "last "+lastPosition + " curr "+position);
                if ((lastPosition == 0 && position == 1) || (lastPosition == 1 && position == 2)
                        || (lastPosition == 2 && position == 3) || (lastPosition == 3 && position == 4)
                        || (lastPosition == 4 && position == 0)) {
                    dataCache.getEndDate().add(Calendar.DAY_OF_MONTH, 1);
                }
                if ((lastPosition== 0 && position == 4) || (lastPosition == 4 && position == 3)
                        || (lastPosition == 3 && position == 2) || (lastPosition == 2 && position == 1)
                        || (lastPosition == 1 && position == 0)) {
                    dataCache.getEndDate().add(Calendar.DAY_OF_MONTH, -1);
                }
                Fragment page = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.lvpMain + ":" + lvpMain.getCurrentItem());
                if (page != null) {
                    ((MainPageFragment)page).update();
                }
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        activity.treatToolbar();
//        main.setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.main).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int heightDiff = activity.findViewById(R.id.main).getRootView().getHeight() - activity.findViewById(R.id.main).getHeight();
//                if (heightDiff > dpToPx(activity, 200)) { // if more than 200 dp, it's probably a keyboard...
//                    keyboardVisible = true;
//                } else {
//                    keyboardVisible = false;
//                }
//            }
//        });
//        if (keyboardVisible) {
//            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(activity.findViewById(R.id.main).getWindowToken(), 0);
//            activity.findViewById(R.id.main).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    keyboardVisible=false;
//                    initialize(dataCache.getBeginDate(), dataCache.getEndDate());
//                }
//            },100);
//        }
//        PRESSED = false;
//
//        reportManager.calculateBalance(begin, end);
//        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        int side;
//        if (height * 0.55 > width)
//            side = width;
//        else
//            side = (int) (height * 0.55);
//        RecordExpanseView expanseView = new RecordExpanseView(activity, end);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(side, side);
//        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        expanseView.setLayoutParams(lp);
//        ((RelativeLayout)activity.findViewById(R.id.rlRecordExpanses)).removeAllViews();
//        ((RelativeLayout)activity.findViewById(R.id.rlRecordExpanses)).addView(expanseView);
//        RecordIncomesView incomeView = new RecordIncomesView(activity, end);
//        RelativeLayout.LayoutParams lpIncomes = new RelativeLayout.LayoutParams(side,
//                side / 4 + (int) (activity.getResources().getDimension(R.dimen.thirty_dp)));
//        lpIncomes.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        incomeView.setLayoutParams(lpIncomes);
//        ((RelativeLayout) activity.findViewById(R.id.rlRecordIncomes)).removeAllViews();
//        ((RelativeLayout) activity.findViewById(R.id.rlRecordIncomes)).addView(incomeView);

    }

    public void calculateBalance(Calendar begin, Calendar end) {
        DecimalFormat decFormat = new DecimalFormat("0.00");
        Map<String, Double> balance = reportManager.calculateBalance(begin, end);
        String abbr = commonOperations.getMainCurrency().getAbbr();
        for (String key : balance.keySet()) {
            switch (key) {
                case PocketAccounterGeneral.INCOMES:
                    tvRecordIncome.setText(decFormat.format(balance.get(key)) + abbr);
                    break;
                case PocketAccounterGeneral.EXPENSES:
                    tvRecordExpanse.setText(decFormat.format(balance.get(key)) + abbr);
                    break;
                case PocketAccounterGeneral.BALANCE:
                    tvRecordBalanse.setText(decFormat.format(balance.get(key)) + abbr);
                    break;
            }
        }
    }

    public void displayMainWindow() {
//        main.setVisibility(View.VISIBLE);
        activity.findViewById(R.id.rlRecordTable).setVisibility(View.VISIBLE);
        PRESSED = false;
        if (fragmentManager.getBackStackEntryCount() != 0) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
                fragmentManager.popBackStack();
            initialize(dataCache.getBeginDate(), dataCache.getEndDate());
        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public void displayFragment(Fragment fragment) {
        activity.findViewById(R.id.rlRecordTable).setVisibility(View.GONE);
        if (fragmentManager.findFragmentById(R.id.flMain) != null && fragment.getClass().getName().matches(fragmentManager.findFragmentById(R.id.flMain).getClass().getName()))
            return;
        PRESSED = true;
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .add(R.id.flMain, fragment)
                .commit();
    }

    public void displayFragment(Fragment fragment, String tag) {
        activity.findViewById(R.id.rlRecordTable).setVisibility(View.GONE);
        if (fragmentManager.findFragmentById(R.id.flMain) != null && fragment.getClass().getName().matches(fragmentManager.findFragmentById(R.id.flMain).getClass().getName()))
            return;
        PRESSED = true;
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .add(R.id.flMain, fragment, tag)
                .commit();
    }

    class LVPAdapter extends FragmentPagerAdapter {
        Fragment fragment;
        public LVPAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            fragment = new MainPageFragment(activity, dataCache.getEndDate());
            return fragment;
        }
        @Override
        public int getCount() {
            return 5;
        }
        public void updateFragment() {
            ((MainPageFragment)fragment).update();
        }
    }


    public void remoteBackPress() {
////        if(calcEventBackPressed!=null){
////            if(calcEventBackPressed.isOpenLayout()){
////                calcEventBackPressed.backpressToCalc();
////                return;
////            }
////        }
//
//        if (isCalcLayoutOpen && getSupportFragmentManager().findFragmentById(R.id.flMain).getClass()
//                .getName().matches("com.jim.pocketaccounter.RecordEditFragment")) {

//            RecordEditFragment recordEditFragment = (RecordEditFragment) getSupportFragmentManager().findFragmentById(R.id.flMain);
//            recordEditFragment.closeLayout();
//            return;
//
//        }
//        PRESSED = false;
//        android.support.v4.app.Fragment temp00 = getSupportFragmentManager().
//                findFragmentById(R.id.flMain);
//        if (!drawer.isClosed()) {
//            drawer.closeLeftSide();
//        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(PocketAccounter.this);
//            builder.setMessage(getString(R.string.dou_you_want_quit))
//                    .setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                        }
//                    }).setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.cancel();
//                    finish();
//                }
//            });
//            builder.create().show();
//        } else {
//            if (temp00.getTag() != null) {
//                if (temp00.getTag().equals(AddCreditFragment.OPENED_TAG) && AddCreditFragment.to_open_dialog) {
//                    //Sardor
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(PocketAccounter.this);
//                    builder.setMessage(getString(R.string.dou_you_want_discard))
//                            .setPositiveButton(getString(R.string.cancel1), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                }
//                            }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            getSupportFragmentManager().popBackStack();
//
//                        }
//                    });
//                    builder.create().show();
//                } else {
//                    if (temp00.getTag().matches("Addcredit")) {
//                        PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight).setVisibility(View.GONE);
//                        PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight).setOnClickListener(null);
//                        getSupportFragmentManager().popBackStack();
//                        return;
//                    }
//                    if (temp00.getTag().matches("InfoFragment")) {
//                        PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight).setVisibility(View.GONE);
//                        PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight).setOnClickListener(null);
//                        getSupportFragmentManager().popBackStack();
//                        return;
//                    }
//
//
//                    if (temp00.getTag().matches(com.jim.pocketaccounter.debt.PockerTag.Edit)) {
//                        getSupportFragmentManager().popBackStack();
//                        replaceFragment(new CreditTabLay(), com.jim.pocketaccounter.debt.PockerTag.CREDITS);
//                        return;
//                    }
//
//                    Log.d("gogogo", "onBackPressed: ");
//                    AddCreditFragment.to_open_dialog = true;
//                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    initialize(date);
//                    String tag = getSupportFragmentManager().findFragmentById(R.id.flMain).getTag();
//
//                    switch (tag) {
//                        case com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT:
//                        case PockerTag.ACCOUNT:
//                        case PockerTag.CATEGORY:
//                        case PockerTag.CURRENCY:
//                        case PockerTag.CREDITS:
//                        case PockerTag.REPORT_ACCOUNT:
//                        case PockerTag.REPORT_INCOM_EXPENSE:
//                        case PockerTag.REPORT_CATEGORY:
//                        case PockerTag.DEBTS: {
//                            findViewById(R.id.change).setVisibility(View.VISIBLE);
//                            initialize(date);
//                            break;
//                        }
//                    }
//                }
//            } else {
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                if (getSupportFragmentManager().findFragmentById(R.id.flMain) != null) {
//                    if (fragmentManager.findFragmentById(R.id.flMain).getTag() == null) {
//                        switch (fragmentManager.findFragmentById(R.id.flMain).getClass().getName()) {
//                            case "com.jim.pocketaccounter.RecordEditFragment": {
//                                if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
//                                    replaceFragment(new RecordDetailFragment(date));
//                                    break;
//                                }
//                            }
//                            case "com.jim.pocketaccounter.AccountManagementFragment":
//                            case "com.jim.pocketaccounter.RecordDetailFragment":
//                                initialize(date);
//                                break;
//                            case "com.jim.pocketaccounter.CurrencyEditFragment":
//                            case "com.jim.pocketaccounter.CurrencyChooseFragment":
//                                findViewById(R.id.change).setVisibility(View.VISIBLE);
//                                replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
//                                break;
//                            case "com.jim.pocketaccounter.RootCategoryEditFragment": {
//                                replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
//                                break;
//                            }
//                            case "com.jim.pocketaccounter.debt.InfoDebtBorrowFragment":
//                            case "com.jim.pocketaccounter.debt.AddBorrowFragment": {
//                                DebtBorrowFragment fragment = new DebtBorrowFragment();
//                                fragment.setArguments(fragmentManager.findFragmentById(R.id.flMain).getArguments());
//                                replaceFragment(fragment, PockerTag.DEBTS);
//                                break;
//                            }
//                            case "com.jim.pocketaccounter.SMSParseEditFragment": {
//                                replaceFragment(new SMSParseFragment(), PockerTag.DEBTS);
//                            }
//                        }
//                        return;
//                    }
//                    if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.ACCOUNT)) {
//                        replaceFragment(new AccountFragment(), PockerTag.ACCOUNT);
//                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.DEBTS)) {
//                        replaceFragment(new DebtBorrowFragment(), PockerTag.DEBTS);
//                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.CURRENCY)) {
//                        replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
//                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.CATEGORY)) {
//                        replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
//                    } else if (fragmentManager.findFragmentById(R.id.flMain).getTag().matches(PockerTag.ACCOUNT)) {
//                        replaceFragment(new AccountFragment(), PockerTag.ACCOUNT);
//                    }
//                }
//            }
//        }
    }
}
