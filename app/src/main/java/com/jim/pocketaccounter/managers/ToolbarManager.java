package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;

import static com.jim.pocketaccounter.R.color.toolbar_text_color;

/**
 * Created by DEV on 28.08.2016.
 */

public class ToolbarManager {
    private Toolbar toolbar;
    private Context context;
    private ImageView ivToolbarFirst, ivToolbarSecond;
    private Spinner spinner;
    public ToolbarManager(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;
        ivToolbarFirst = (ImageView) toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbarSecond = (ImageView) toolbar.findViewById(R.id.ivToolbarMostRight);
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
    public void setToolbarIconsVisibility(int first, int second) {
        ivToolbarFirst.setVisibility(first);
        ivToolbarSecond.setVisibility(second);
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
