package com.jim.pocketaccounter.debt;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.photocalc.ViewPagerFixed;
import com.jim.pocketaccounter.utils.FloatingActionButton;

import java.util.ArrayList;

import javax.inject.Inject;

public class DebtBorrowFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    private DebtBorrowDao debtBorrowDao;

    private BorrowFragment archiv;
    public static String DEBT_BORROW_ID = "debt_borrow_id";
    private final int BORROW_FRAGMENT = 0;
    private final int DEBT_FRAGMENT = 1;
    private TabLayout tabLayout;
    private ViewPagerFixed viewPager;
    private FloatingActionButton fb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        debtBorrowDao = daoSession.getDebtBorrowDao();
        final View view = inflater.inflate(R.layout.debt_borrow_fragment_mod, container, false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PocketAccounter.keyboardVisible){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);}
            }
        },100);
        tabLayout = (TabLayout) view.findViewById(R.id.tlDebtBorrowFragment);
        viewPager = (ViewPagerFixed) view.findViewById(R.id.vpDebtBorrowFragment);

        toolbarManager.setTitle(getResources().getString(R.string.debts_title));
        toolbarManager.setSubtitle("");
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
        toolbarManager.setSpinnerVisibility(View.GONE);

        fb = (FloatingActionButton) view.findViewById(R.id.fbDebtBorrowFragment);
        fb.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        restartAdapter();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
    }

    public void restartAdapter() {
        ArrayList<BorrowFragment> borrowFragments = new ArrayList<>();
        archiv = BorrowFragment.getInstance(2);
        BorrowFragment debt = BorrowFragment.getInstance(1);
        BorrowFragment borrow = BorrowFragment.getInstance(0);
        debt.setDebtBorrowFragment(this);
        borrow.setDebtBorrowFragment(this);
        borrowFragments.add(archiv);
        borrowFragments.add(debt);
        borrowFragments.add(borrow);
        viewPager.setAdapter(new MyAdapter(borrowFragments, ((PocketAccounter) getContext()).getSupportFragmentManager()));
        if (getArguments() != null) {
            if (getArguments().getInt("type", -1) != -1) {
                viewPager.setCurrentItem(getArguments().getInt("type", 0));
            } else {
                if (getArguments().getInt("pos", -1) != -1) {
                    viewPager.setCurrentItem(getArguments().getInt("pos", 0));
                } else {
                    if (getArguments().getString("id") != null) {
                        for (DebtBorrow db : debtBorrowDao.queryBuilder().list()) {
                            if (db.getId().matches(getArguments().getString("id"))) {
                                viewPager.setCurrentItem(db.getType());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    private boolean show = false;
    public void onScrolledList(boolean k) {
        if (k) {
            if (!show)
            fb.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_down));
            show = true;
        } else {
            if (show)
            fb.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_up));
            show = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fbDebtBorrowFragment) {
            switch (viewPager.getCurrentItem()) {
                case BORROW_FRAGMENT: {
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(AddBorrowFragment.getInstance(BORROW_FRAGMENT, null));
                    break;
                }
                case DEBT_FRAGMENT: {
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(AddBorrowFragment.getInstance(DEBT_FRAGMENT, null));
                    break;
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == DEBT_FRAGMENT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fb.setAlpha(1-positionOffset);
            }
            else {
                if(positionOffset>=0.1f&&fb.getVisibility()==View.VISIBLE)
                    fb.setVisibility(View.GONE);
                else if(positionOffset<=0.1f&&fb.getVisibility()==View.GONE)
                    fb.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 2) {
            archiv.changeList();
            fb.setVisibility(View.GONE);
        } else fb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (show){
            onScrolledList(false);
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        ArrayList<BorrowFragment> list;

        public MyAdapter(ArrayList<BorrowFragment> list, FragmentManager fm) {
            super(fm);
            this.list = list;
        }

        public Fragment getItem(int position) {
            return list.get(list.size() - 1 - position);
        }

        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            if (position == BORROW_FRAGMENT) {
                return getResources().getString(R.string.borrows);
            }
            if (position == DEBT_FRAGMENT) {
                return getResources().getString(R.string.debts);
            }
            return getResources().getString(R.string.archive);
        }
    }
}