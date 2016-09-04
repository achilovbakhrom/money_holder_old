package com.jim.pocketaccounter.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.intropage.IntroIndicator;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by DEV on 28.08.2016.
 */

public class SettingsManager {
    @Inject
    SharedPreferences preferences;
    PocketAccounter context;
    public SettingsManager(PocketAccounter context) {
        this.context = context;
        context.component((PocketAccounterApplication) context.getApplication()).inject(this);
    }

    public void setup() {
        launchSetup();
        languageSetup();

    }
    private void launchSetup() {
        if (preferences.getBoolean("FIRST_KEY", true)) {
            Intent first = new Intent(context, IntroIndicator.class);
            PocketAccounter.openActivity=true;
            context.startActivity(first);
            context.finish();
        }
    }
    private void languageSetup() {
        String lang = preferences.getString("language", context.getResources().getString(R.string.language_default));
        if (lang.matches(context.getResources().getString(R.string.language_default)))
            setLocale(Locale.getDefault().getLanguage());
        else
            setLocale(lang);
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}

