package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.fragments.MainPageFragment;
import com.jim.pocketaccounter.utils.cache.DataCache;
import java.util.Calendar;

import javax.inject.Inject;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.jim.pocketaccounter.PocketAccounter.PRESSED;

/**
 * Created by DEV on 27.08.2016.
 */

public class PAFragmentManager {
    private PocketAccounter activity;
    private FragmentManager fragmentManager;
    private int lastPos = 5000;
    private Boolean direction = null;

    @Inject
    ReportManager reportManager;
    @Inject
    CommonOperations commonOperations;
    @Inject
    DataCache dataCache;
    private MainPageFragment nextPage;
    public PAFragmentManager(PocketAccounter activity) {
        this.activity = activity;
        ((PocketAccounterApplication) activity.getApplicationContext()).component().inject(this);
        fragmentManager = activity.getSupportFragmentManager();
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void initialize(final Calendar begin, final Calendar end) {
        final ViewPager lvpMain = (ViewPager) activity.findViewById(R.id.lvpMain);
        FragmentPagerAdapter adapter = new LVPAdapter(getFragmentManager());
        lvpMain.setCurrentItem(5000, false);
        lvpMain.post(new Runnable() {
            @Override
            public void run() {
                lvpMain.setCurrentItem(5000, false);
            }
        });
        lvpMain.setAdapter(adapter);
        lvpMain.setOffscreenPageLimit(0);
        lvpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (lastPos != position && direction == null) {
                    direction = lastPos<position;
                }
                if (lastPos>position && direction) {
                    Calendar day = nextPage.getDay();
                    day.add(Calendar.DAY_OF_MONTH, -2);
                    ((MainPageFragment) fragmentManager.findFragmentByTag(nextPage.getTag())).setDay(day);
                    direction = !direction;
                }
                if (lastPos<position && !direction) {
                    Calendar day = nextPage.getDay();
                    day.add(Calendar.DAY_OF_MONTH, 2);
                    nextPage.setDay(day);
                    ((MainPageFragment) fragmentManager.findFragmentByTag(nextPage.getTag())).setDay(day);
                    direction = !direction;
                }
                MainPageFragment page = (MainPageFragment) getFragmentManager().findFragmentByTag("android:switcher:"+R.id.lvpMain+":"+position);
                MainPageFragment prevFragment;
                if (lastPos>position && !direction) {
                    prevFragment = ((MainPageFragment)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.lvpMain+":"+(position+1)));
                    if (prevFragment.getDay().compareTo(page.getDay()) <= 0) {
                        Calendar day = (Calendar) prevFragment.getDay().clone();
                        day.add(Calendar.DAY_OF_MONTH, -1);
                        page.setDay(day);
                    }
                }
                if (lastPos<position && direction) {
                    prevFragment = ((MainPageFragment)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.lvpMain+":"+(position-1)));
                    if (prevFragment.getDay().compareTo(page.getDay()) >= 0) {
                        Calendar day = (Calendar) prevFragment.getDay().clone();
                        day.add(Calendar.DAY_OF_MONTH, 1);
                        page.setDay(day);
                    }
                }
                if (page != null) {
                    page.update();
                    dataCache.getEndDate().setTimeInMillis(page.getDay().getTimeInMillis());
                }
                lastPos = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
        public LVPAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Log.d("sss", "getItem() pos: "+position);
            Calendar date = (Calendar)dataCache.getEndDate().clone();
            date.add(Calendar.DAY_OF_MONTH, position-5000);
            Fragment fragment = new MainPageFragment(activity, (Calendar) date.clone());
            nextPage = (MainPageFragment) fragment;
            return fragment;
        }
        @Override
        public int getCount() {
            return 10000;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
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
