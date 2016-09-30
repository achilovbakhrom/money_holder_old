package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.debt.DebtBorrowFragment;
import com.jim.pocketaccounter.debt.PocketClassess;
import com.jim.pocketaccounter.fragments.AccountFragment;
import com.jim.pocketaccounter.fragments.AutoMarketFragment;
import com.jim.pocketaccounter.fragments.CategoryFragment;
import com.jim.pocketaccounter.fragments.CreditTabLay;
import com.jim.pocketaccounter.fragments.CurrencyFragment;
import com.jim.pocketaccounter.fragments.MainPageFragment;
import com.jim.pocketaccounter.fragments.PurposeFragment;
import com.jim.pocketaccounter.utils.cache.DataCache;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

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
    @Inject
    @Named(value = "end")
    Calendar end;
    private MainPageFragment nextPage;
    private FrameLayout main;
    private boolean keyboardVisible = false;

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
                    direction = lastPos < position;
                }
                if (lastPos > position && direction) {
                    Calendar day = nextPage.getDay();
                    day.add(Calendar.DAY_OF_MONTH, -2);
                    ((MainPageFragment) fragmentManager.findFragmentByTag(nextPage.getTag())).setDay(day);
                    direction = !direction;
                }
                if (lastPos < position && !direction) {
                    Calendar day = nextPage.getDay();
                    day.add(Calendar.DAY_OF_MONTH, 2);
                    nextPage.setDay(day);
                    ((MainPageFragment) fragmentManager.findFragmentByTag(nextPage.getTag())).setDay(day);
                    direction = !direction;
                }
                final MainPageFragment page = (MainPageFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.lvpMain + ":" + position);
                MainPageFragment prevFragment;
                if (lastPos > position && !direction) {
                    prevFragment = ((MainPageFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.lvpMain + ":" + (position + 1)));
                    if (prevFragment.getDay().compareTo(page.getDay()) <= 0) {
                        Calendar day = (Calendar) prevFragment.getDay().clone();
                        day.add(Calendar.DAY_OF_MONTH, -1);
                        page.setDay(day);
                    }
                }
                if (lastPos < position && direction) {
                    prevFragment = ((MainPageFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.lvpMain + ":" + (position - 1)));
                    if (prevFragment.getDay().compareTo(page.getDay()) >= 0) {
                        final Calendar day = (Calendar) prevFragment.getDay().clone();
                        day.add(Calendar.DAY_OF_MONTH, 1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                page.setDay(day);
                            }
                        }).run();
                    }
                }
                if (page != null) {
                    page.update();
                    dataCache.setEndDate(page.getDay());
                    dataCache.updatePercents();
                }
                lastPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void updateAllFragmentsOnViewPager() {
        int size = fragmentManager.getFragments().size();
        for (int i = 0; i < size; i++) {
            Fragment fragment = fragmentManager.getFragments().get(i);
            if (fragment != null && fragment.getClass().getName().matches(MainPageFragment.class.getName())) {
                ((MainPageFragment) fragment).update();
            }
        }
    }

    public void updateAllFragmentsPageChanges() {
        int size = fragmentManager.getFragments().size();
        for (int i = 0; i < size; i++) {
            Fragment fragment = fragmentManager.getFragments().get(i);
            if (fragment != null && fragment.getClass().getName().matches(MainPageFragment.class.getName())) {
                ((MainPageFragment) fragment).updatePageChanges();
            }
        }
    }

    public void displayMainWindow() {
        activity.treatToolbar();
//        main.setVisibility(View.VISIBLE);
        activity.findViewById(R.id.rlRecordTable).setVisibility(View.VISIBLE);
        PRESSED = false;
//        if (fragmentManager.getBackStackEntryCount() != 0) {
//            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
//                fragmentManager.popBackStack();
//        if (fragmentManager.getBackStackEntryCount() > 0) {
        fragmentManager.popBackStackImmediate();
        activity.findViewById(R.id.change).setVisibility(View.VISIBLE);
//        }
//            initialize(dataCache.getBeginDate(), dataCache.getEndDate());
//        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public void displayFragment(Fragment fragment) {
//        main.setVisibility(View.GONE);
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
//        main.setVisibility(View.GONE);
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
            Calendar end = Calendar.getInstance();
            end.add(Calendar.DAY_OF_MONTH, position - 5000);
            Fragment fragment = new MainPageFragment(activity, end);
            nextPage = (MainPageFragment) fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return 10000;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void remoteBackPress() {
        String fragName = getFragmentManager().findFragmentById(R.id.flMain).getClass().getName();
        int count = getFragmentManager().getBackStackEntryCount();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        displayMainWindow();
//        while (count > 0) {
//            getFragmentManager().popBackStack();
//            count--;
//        }
//        fragmentManager.beginTransaction().disallowAddToBackStack().commit();

//        fragmentTransaction.commit();
        if (fragName.matches(PocketClassess.DEBTBORROW_FRAG) || fragName.matches(PocketClassess.AUTOMARKET_FRAG)
                || fragName.matches(PocketClassess.CURRENCY_FRAG) || fragName.matches(PocketClassess.CATEGORY_FRAG)
                || fragName.matches(PocketClassess.ACCOUNT_FRAG) || fragName.matches(PocketClassess.CREDIT_FRAG)
                || fragName.matches(PocketClassess.PURPOSE_FRAG) || fragName.matches(PocketClassess.REPORT_ACCOUNT)
                || fragName.matches(PocketClassess.REPORT_CATEGORY)) {
//            main.setVisibility(View.VISIBLE);
//            displayMainWindow();
        } else if (fragName.matches(PocketClassess.ADD_DEBTBORROW) || fragName.matches(PocketClassess.INFO_DEBTBORROW)) {
            displayFragment(new DebtBorrowFragment());
        } else if (fragName.matches(PocketClassess.ADD_AUTOMARKET) || fragName.matches(PocketClassess.INFO_DEBTBORROW)) {
            displayFragment(new AutoMarketFragment());
        } else if (fragName.matches(PocketClassess.CURRENCY_CHOOSE) || fragName.matches(PocketClassess.CURRENCY_EDIT)) {
            displayFragment(new CurrencyFragment());
        } else if (fragName.matches(PocketClassess.CATEGORY_INFO) || fragName.matches(PocketClassess.ADD_CATEGORY)) {
            displayFragment(new CategoryFragment());
        } else if (fragName.matches(PocketClassess.ACCOUNT_EDIT) || fragName.matches(PocketClassess.ACCOUNT_INFO)) {
            displayFragment(new AccountFragment());
        } else if (fragName.matches(PocketClassess.ADD_AUTOMARKET)) {
            displayFragment(new AutoMarketFragment());
        } else if (fragName.matches(PocketClassess.INFO_CREDIT) || fragName.matches(PocketClassess.ADD_CREDIT)) {
            displayFragment(new CreditTabLay());
        } else if (fragName.matches(PocketClassess.INFO_PURPOSE) || fragName.matches(PocketClassess.ADD_PURPOSE)) {
            displayFragment(new PurposeFragment());
        }
    }
}
