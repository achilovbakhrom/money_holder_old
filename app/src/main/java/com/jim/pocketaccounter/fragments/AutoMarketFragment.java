package com.jim.pocketaccounter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.AutoMarket;
import com.jim.pocketaccounter.database.AutoMarketDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by root on 9/15/16.
 */
public class AutoMarketFragment extends Fragment implements View.OnClickListener {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private AutoMarketDao autoMarketDao;
    private AutoAdapter autoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        autoMarketDao = daoSession.getAutoMarketDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auto_market_layout, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvAutoMarketFragment);
        autoAdapter = new AutoAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(autoAdapter);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fbAutoMarketAdd);
        floatingActionButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbAutoMarketAdd: {
                paFragmentManager.getFragmentManager().popBackStack();
                paFragmentManager.displayFragment(new AddAutoMarketFragment());
                break;
            }
        }
    }

    private class AutoAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<AutoMarket> list;

        public AutoAdapter() {
            list = (ArrayList<AutoMarket>) autoMarketDao.loadAll();
        }

        public int getItemCount() {
            return list.size();
        }

        public void onBindViewHolder(final ViewHolder view, final int position) {
            view.name.setText(list.get(position).getName());
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new InfoAutoMarketFragment(list.get(position)));
                }
            });
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_market_item_layout, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tvAutoMarketItem);
        }
    }
}
