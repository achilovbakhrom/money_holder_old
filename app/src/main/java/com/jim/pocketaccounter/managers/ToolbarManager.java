package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

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

    public ToolbarManager(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;

        Log.d("nimadir", "ToolbarManager: ");
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



    public void setOnHomeButtonClickListener(View.OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setTitle(String title) {
        toolbar.setTitle(title);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSubtitle(String subtitle) {
        toolbar.setSubtitle(subtitle);
    }
    public void setToolbarIconsVisibility(int start, int first, int second) {
        ivToolbarFirst.setVisibility(first);
        ivToolbarSecond.setVisibility(second);
        ivToolbarStart.setVisibility(start);
    }
    boolean firstIconActive,secondIconActive;
    DrawerInitializer  drawerInitializer;
    SimpleDateFormat format;
    public void setSearchView(DrawerInitializer  drawerInitializer,SimpleDateFormat format){
        ivToolbarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchTools();
            }
        });


        this.drawerInitializer=drawerInitializer;
        this.format=format;
    }

    public void openSearchTools( ){
        setImageToHomeButton(R.drawable.ic_back_button);
        searchEditToolbar.setVisibility(View.VISIBLE);
        searchEditToolbar.setFocusableInTouchMode(true);
        searchEditToolbar.requestFocus();

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
                closeSearchTools(firstIconActive,secondIconActive);
            }
        });
        setToolbarIconsVisibility(View.VISIBLE,View.GONE,View.GONE);
        ivToolbarStart.setImageResource(R.drawable.ic_close_black_24dp);
        toolbar.setTitle(null);
        toolbar.setSubtitle(null);

     }

    public void closeSearchTools( boolean firstIconActive, final boolean secondIconActive){
        setImageToHomeButton(R.drawable.ic_back_button);
        searchEditToolbar.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm==null)
            return;
        imm.hideSoftInputFromWindow(searchEditToolbar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

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
    }
    public void setImageToStartImage(int resId) {
        ivToolbarStart.setImageDrawable(null);
        ivToolbarStart.setImageResource(resId);
    }
    public void setImageToFirstImage(int resId) {
        ivToolbarFirst.setImageDrawable(null);
        ivToolbarFirst.setImageResource(resId);
    }
    public void setImageToSecondImage(int resId) {
        ivToolbarSecond.setImageDrawable(null);
        ivToolbarSecond.setImageResource(resId);
    }
    public void setImageToHomeButton(int resId) {
        ((PocketAccounter) context).getSupportActionBar().setHomeAsUpIndicator(resId);
    }
    public void setSpinnerVisibility(int visibility) {
        spinner.setVisibility(visibility);
    }
}
