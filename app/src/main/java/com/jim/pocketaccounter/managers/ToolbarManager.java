package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.fragments.SearchFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.jim.pocketaccounter.R.color.toolbar_text_color;

/**
 * Created by DEV on 28.08.2016.
 */

public class ToolbarManager {
    private Toolbar toolbar;
    private Context context;
    private ImageView ivToolbarFirst, ivToolbarSecond, ivToolbarStart;
    private Spinner spinner;
    private EditText searchEditToolbar;
    Handler whenKeyboardClosed;
    public void setTitle(String title){
        toolbar.setTitle(title);
    }
    public void setSubtitle(String title){
        toolbar.setSubtitle(title);
    }
    public ToolbarManager(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;
        ivToolbarFirst = (ImageView) toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbarSecond = (ImageView) toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarStart = (ImageView) toolbar.findViewById(R.id.ivToolbarSearch);
        searchEditToolbar = (EditText) toolbar.findViewById(R.id.editToolbar);
        spinner = (Spinner) toolbar.findViewById(R.id.spToolbar);
    }
    public void init() {
        ((PocketAccounter) context).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(context, toolbar_text_color));
        ((PocketAccounter) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void setOnSpinerItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }
    public void setOnFirstImageClickListener(View.OnClickListener listener) {
        ivToolbarFirst.setOnClickListener(listener);
    }
    public void setOnSecondImageClickListener(View.OnClickListener listener) {
        ivToolbarSecond.setOnClickListener(listener);
    }
    public void setVisiblityEditSearch(){
        searchEditToolbar.setVisibility(View.GONE);
    }


    public void setOnHomeButtonClickListener(View.OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }
    public void setToolbarIconsVisibility(int start, int first, int second) {
        ivToolbarFirst.setVisibility(first);
        ivToolbarSecond.setVisibility(second);
        ivToolbarStart.setVisibility(start);
    }
    boolean firstIconActive,secondIconActive;
    DrawerInitializer  drawerInitializer;
    SimpleDateFormat format;
    PAFragmentManager fragmentManager;
    boolean keyboardIsOpen=true;

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    View main;
    public void setSearchView(DrawerInitializer  drawerInitializer,SimpleDateFormat format, PAFragmentManager fragmentManager,final View main){
        ivToolbarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchTools();
            }
        });
        this.fragmentManager=fragmentManager;
        this.drawerInitializer=drawerInitializer;
        this.format=format;
        this.main=main;
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = main.getRootView().getHeight() - main.getHeight();
                if (heightDiff > dpToPx(context, 200)) { // if more than 200 dp, it's probably a keyboard...
                    keyboardIsOpen = true;
                } else {
                    keyboardIsOpen = false;
                }
            }
        });
    }
    SearchFragment.TextChangeListnerW textChangeListnerW;
    SearchFragment searchFragment;
    public void openSearchTools( ){
        setImageToHomeButton(R.drawable.ic_back_button);
        searchEditToolbar.setVisibility(View.VISIBLE);
        searchEditToolbar.setFocusableInTouchMode(true);
        searchEditToolbar.requestFocus();

        if (searchFragment == null) {

            searchFragment = new SearchFragment();
            textChangeListnerW=searchFragment.getListnerChange();

            searchEditToolbar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    textChangeListnerW.onTextChange(searchEditToolbar.getText().toString());
                }
            });
        }

        fragmentManager.displayFragment(searchFragment, "salom");

        final InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager==null)
            return;
        inputMethodManager.showSoftInput(searchEditToolbar, InputMethodManager.SHOW_IMPLICIT);

        ivToolbarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditToolbar.setText("");
            }
        });
        firstIconActive = ivToolbarFirst.getVisibility()==View.VISIBLE;
        secondIconActive = ivToolbarSecond.getVisibility()==View.VISIBLE;
        setOnHomeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearchTools();
            }
        });
        setToolbarIconsVisibility(View.VISIBLE,View.GONE,View.GONE);
        ivToolbarStart.setImageResource(R.drawable.ic_close_black_24dp);
        toolbar.setTitle(null);
        toolbar.setSubtitle(null);

    }
    Runnable runForItClose;
    public void closeSearchFragment(){
        whenKeyboardClosed=new Handler();
        runForItClose=new Runnable() {
            @Override
            public void run() {
                if(!keyboardIsOpen){
                    setImageToHomeButton(R.drawable.ic_drawer);
                    searchEditToolbar.setVisibility(View.GONE);
                    ivToolbarStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openSearchTools();
                        }
                    });

                    if(firstIconActive)  ivToolbarFirst.setVisibility(View.VISIBLE);
                    else ivToolbarFirst.setVisibility(View.GONE);
                    if(secondIconActive) ivToolbarSecond.setVisibility(View.VISIBLE);
                    else ivToolbarSecond.setVisibility(View.GONE);

                    ivToolbarStart.setImageResource(R.drawable.ic_search_black_24dp);
                    toolbar.setTitle(context.getResources().getString(R.string.app_name));
                    toolbar.setSubtitle(format.format(Calendar.getInstance().getTime()));

                }
                else{
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm==null)
                        return;
                    imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), 0);
                    whenKeyboardClosed.postDelayed(runForItClose,100);
                }
            }
        };

        if(keyboardIsOpen){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm==null)
                return;
            imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), 0);
        }

        whenKeyboardClosed.postDelayed(runForItClose,200);
    }
    public void closeSearchFragmentWithOutVisibleGone(){
        whenKeyboardClosed=new Handler();
        runForItClose=new Runnable() {
            @Override
            public void run() {
                if(!keyboardIsOpen){
                    setImageToHomeButton(R.drawable.ic_drawer);
                    searchEditToolbar.setVisibility(View.GONE);
                    ivToolbarStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openSearchTools();
                        }
                    });

                    ivToolbarStart.setImageResource(R.drawable.ic_search_black_24dp);
                    toolbar.setTitle(context.getResources().getString(R.string.app_name));
                    toolbar.setSubtitle(format.format(Calendar.getInstance().getTime()));

                }
                else{
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm==null)
                        return;
                    imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), 0);
                    whenKeyboardClosed.postDelayed(runForItClose,100);
                }
            }
        };

        if(keyboardIsOpen){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm==null)
                return;
            imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), 0);
        }

        whenKeyboardClosed.postDelayed(runForItClose,200);
    }
    public void closeSearchTools(){
        whenKeyboardClosed=new Handler();
        runForItClose=new Runnable() {
            @Override
            public void run() {
                if(!keyboardIsOpen){

                    searchEditToolbar.setVisibility(View.GONE);
                    ivToolbarStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openSearchTools();
                        }
                    });

                    if(firstIconActive)  ivToolbarFirst.setVisibility(View.VISIBLE);
                    else ivToolbarFirst.setVisibility(View.GONE);
                    if(secondIconActive) ivToolbarSecond.setVisibility(View.VISIBLE);
                    else ivToolbarSecond.setVisibility(View.GONE);

                    ivToolbarStart.setImageResource(R.drawable.ic_search_black_24dp);
                    setImageToHomeButton(R.drawable.ic_drawer);
                    toolbar.setTitle(context.getResources().getString(R.string.app_name));
                    toolbar.setSubtitle(format.format(Calendar.getInstance().getTime()));
                    setOnHomeButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawerInitializer.getDrawer().openLeftSide();
                        }
                    });

                    fragmentManager.getFragmentManager().popBackStack();
                    fragmentManager.displayMainWindow();
                }
                else{
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm==null)
                        return;
                    imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), 0);
                    whenKeyboardClosed.postDelayed(runForItClose,100);
                }
            }
        };

        if(keyboardIsOpen){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm==null)
                return;
            imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), 0);
        }

        whenKeyboardClosed.postDelayed(runForItClose,200);

    }
    public void setImageToStartImage(int resId) {
        ivToolbarStart.setImageDrawable(null);
        ivToolbarStart.setImageResource(resId);
    }
    public void setImageToFirstImage(int resId) {
        ivToolbarFirst.setImageDrawable(null);
        ivToolbarFirst.setImageResource(resId);
    }
    public ImageView setImageToSecondImage(int resId) {
        ivToolbarSecond.setImageDrawable(null);
        ivToolbarSecond.setImageResource(resId);
        return null;
    }
    public void setImageToHomeButton(int resId) {
        ((PocketAccounter) context).getSupportActionBar().setHomeAsUpIndicator(resId);
    }
    public Spinner getSpinner() {
        return spinner;
    }
    public void setSpinnerVisibility(int visibility) {
        spinner.setVisibility(visibility);
    }
}
