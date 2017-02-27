package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.cache.DataCache;
import com.jim.pocketaccounter.utils.record.RecordExpanseView;
import com.jim.pocketaccounter.utils.record.RecordIncomesView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.jim.pocketaccounter.PocketAccounter.PRESSED;

@SuppressLint("ValidFragment")
public class MainPageFragment extends Fragment {
    private Calendar day;
    private PocketAccounter pocketAccounter;
    private boolean keyboardVisible = false;
    private TextView tvRecordIncome, tvRecordExpanse, tvRecordBalanse;
    private RelativeLayout rlRecordExpanses, rlRecordIncomes;
    private LinearLayout rlRecordBalance;
    private RecordExpanseView expenseView;
    private RecordIncomesView incomeView;
    private Map<String, Double> balance;
    @Inject ReportManager reportManager;
    @Inject DataCache dataCache;
    @Inject CommonOperations commonOperations;
    @Inject ToolbarManager toolbarManager;
    @Inject @Named(value = "display_formatter") SimpleDateFormat simpleDateFormat;
    @Inject PAFragmentManager paFragmentManager;
    @Inject @Named(value = "begin") Calendar begin;
    @Inject @Named(value = "end") Calendar end;
    @Inject SharedPreferences preferences;
    public static String CALENDAR_DAY = "fromPaFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_page_fragment, container, false);
        this.pocketAccounter = (PocketAccounter) getActivity();
        pocketAccounter.component((PocketAccounterApplication) pocketAccounter.getApplicationContext()).inject(this);
        if(getArguments()!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getArguments().getLong(CALENDAR_DAY));
            Log.d("TESTTTT", "onCreateView: " + calendar.getTime().getTime());
            this.day = calendar;

        }
        if(day==null){
            Log.d("TESTTTT", "onCreateView: " + " hi is null");
        }
        pocketAccounter.findViewById(R.id.main).setVisibility(View.VISIBLE);
        pocketAccounter.findViewById(R.id.main).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = pocketAccounter.findViewById(R.id.main).getRootView().getHeight() - pocketAccounter.findViewById(R.id.main).getHeight();
                if (heightDiff > dpToPx(pocketAccounter, 200)) { // if more than 200 dp, it's probably a keyboard...
                    keyboardVisible = true;
                } else {
                    keyboardVisible = false;
                }
            }
        });
        if (keyboardVisible) {
            InputMethodManager imm = (InputMethodManager) pocketAccounter.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(pocketAccounter.findViewById(R.id.main).getWindowToken(), 0);
            pocketAccounter.findViewById(R.id.main).postDelayed(new Runnable() {
                @Override
                public void run() {
                    keyboardVisible=false;
                    initialize();
                }
            },100);
        }
        rlRecordExpanses = (RelativeLayout) rootView.findViewById(R.id.rlRecordExpanses);
        rlRecordIncomes = (RelativeLayout) rootView.findViewById(R.id.rlRecordIncomes);
        rlRecordBalance = (LinearLayout) rootView.findViewById(R.id.rlRecordBalance);
        tvRecordIncome = (TextView) rootView.findViewById(R.id.tvRecordIncome);
        tvRecordExpanse = (TextView) rootView.findViewById(R.id.tvRecordExpanse);
        tvRecordBalanse = (TextView) rootView.findViewById(R.id.tvRecordBalanse);
        PRESSED = false;
        rlRecordBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PRESSED) return;
                RecordDetailFragment fragment = new RecordDetailFragment();
                Bundle bundle = new Bundle();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                bundle.putString(RecordDetailFragment.DATE, format.format(dataCache.getEndDate().getTime()));
                fragment.setArguments(bundle);
                paFragmentManager.displayFragment(fragment);
                PRESSED = true;
            }
        });
        initialize();
        return rootView;
    }
    public void initialize() {
        DisplayMetrics dm = pocketAccounter.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int side;
        if (height * 0.55 > width)
            side = width;
        else
            side = (int) (height * 0.55);
        expenseView = new RecordExpanseView(pocketAccounter, day);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(side, side);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        expenseView.setLayoutParams(lp);
        rlRecordExpanses.removeAllViews();
        rlRecordExpanses.addView(expenseView);
        incomeView = new RecordIncomesView(pocketAccounter, day);
        RelativeLayout.LayoutParams lpIncomes = new RelativeLayout.LayoutParams(side,
                side / 4 + (int) (pocketAccounter.getResources().getDimension(R.dimen.thirty_dp)));
        lpIncomes.addRule(RelativeLayout.CENTER_HORIZONTAL);
        incomeView.setLayoutParams(lpIncomes);
        rlRecordIncomes.removeAllViews();
        rlRecordIncomes.addView(incomeView);
        calculateBalance();
    }
    public void refreshCurrencyChanges() {
        commonOperations.refreshCurrency();
    }
    public void update() {
        Log.d("TESTTTT", "update: ");
        if(day==null) return;
        toolbarManager.setSubtitle(simpleDateFormat.format(day.getTime()));
        calculateBalance();
        expenseView.invalidate();
        incomeView.invalidate();
    }
    public void updatePageChanges() {
        expenseView.updatePageCountAndPosition();
        expenseView.invalidate();
        incomeView.updatePageCountAndPosition();
        incomeView.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    public void calculateBalance() {
        String mode = preferences.getString("balance_solve", "0");
        end.setTimeInMillis(day.getTimeInMillis());
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 59);
        if (mode.equals("0")) {
            begin.setTimeInMillis(dataCache.getBeginDate().getTimeInMillis());
        }
        else {
            begin.setTimeInMillis(day.getTimeInMillis());
            begin.set(Calendar.HOUR_OF_DAY, 0);
            begin.set(Calendar.MINUTE, 0);
            begin.set(Calendar.SECOND, 0);
            begin.set(Calendar.MILLISECOND, 0);
        }
        balance = reportManager.calculateBalance(begin, end);
        DecimalFormat decFormat = new DecimalFormat("0.00");
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

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    public Calendar getDay() {return day;}
    public void setDay(Calendar day) {
        this.day = day;
    }
}
