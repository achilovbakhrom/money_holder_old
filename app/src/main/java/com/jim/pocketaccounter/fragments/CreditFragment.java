package com.jim.pocketaccounter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.AdapterCridet;
import com.jim.pocketaccounter.credit.LinearManagerWithOutEx;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class CreditFragment extends Fragment {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    ToolbarManager toolbarManager;

    private CreditDetialsDao creditDetialsDao;
    RecyclerView crRV;
    AdapterCridet crAdap;
    Context contextt;
    TextView ifListEmpty;
    private CreditTabLay creditTabLay;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V=inflater.inflate(R.layout.fragment_credit, container, false);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        creditDetialsDao = daoSession.getCreditDetialsDao();
        contextt =getActivity();
        toolbarManager.setTitle(getResources().getString(R.string.cred_managment));
        toolbarManager.setSubtitle("");
        toolbarManager.setSpinnerVisibility(View.GONE);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
        ifListEmpty=(TextView) V.findViewById(R.id.ifListEmpty);
        if(creditDetialsDao.queryBuilder()
                .where(CreditDetialsDao.Properties.Key_for_archive.eq(false)).orderDesc(CreditDetialsDao.Properties.MyCredit_id).build().list().size()==0){
            ifListEmpty.setVisibility(View.VISIBLE);
            ifListEmpty.setText(getResources().getString(R.string.credit_are_empty));
        }
        else ifListEmpty.setVisibility(View.GONE);
        crRV=(RecyclerView) V.findViewById(R.id.my_recycler_view);
        LinearManagerWithOutEx llm = new LinearManagerWithOutEx(contextt);
        crRV.setLayoutManager(llm);

        updateList();

        crRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(creditTabLay==null) {
                    for (Fragment fragment : paFragmentManager.getFragmentManager().getFragments()){
                        if (fragment == null) continue;
                        if (fragment.getClass().getName().equals(CreditTabLay.class.getName())){
                            creditTabLay = (CreditTabLay) fragment;
                            break;
                        }
                    }
                }
                creditTabLay.onScrolledList(dy > 0);
            }
        });
        return V;
    }

    public void updateList(){
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
        toolbarManager.setSubtitle("");
        List<CreditDetials> creditDetialses = creditDetialsDao
                .queryBuilder()
                .where(CreditDetialsDao.Properties.Key_for_archive.eq(false))
                .orderDesc(CreditDetialsDao.Properties.MyCredit_id)
                .build()
                .list();
        if (creditDetialses.isEmpty())
            ifListEmpty.setVisibility(View.VISIBLE);
        else
            ifListEmpty.setVisibility(View.GONE);
        AdapterCridet adapterCridet = new AdapterCridet(getContext());
        if(crRV!=null) crRV.setAdapter(adapterCridet);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface EventFromAdding{
        void addedCredit();
        void canceledAdding();
    }
}