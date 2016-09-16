package com.jim.pocketaccounter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.AutoMarket;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.PAFragmentManager;

import javax.inject.Inject;

/**
 * Created by root on 9/15/16.
 */
public class InfoAutoMarketFragment extends Fragment {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;

    private TextView marketName;
    private AutoMarket autoMarket;

    public InfoAutoMarketFragment (AutoMarket autoMarket) {
        this.autoMarket = autoMarket;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_auto_market_layout, container, false);
        marketName = (TextView) rootView.findViewById(R.id.tvInfoAutoMarketName);

        return rootView;
    }
}
