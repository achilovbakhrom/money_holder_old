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

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.AdapterCridet;
import com.jim.pocketaccounter.credit.AdapterCridetArchive;
import com.jim.pocketaccounter.credit.LinearManagerWithOutEx;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;

import java.util.ArrayList;

import javax.inject.Inject;

public class CreditFragment extends Fragment {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    ToolbarManager toolbarManager;

    private CreditDetialsDao creditDetialsDao;
    ArrayList<CreditDetials> crList;
    RecyclerView crRV;
    AdapterCridet crAdap;
    Context This;
    CreditTabLay.SvyazkaFragmentov svyaz;
    private CreditTabLay creditTabLay;

    public AdapterCridetArchive.GoCredFragForNotify getInterfaceNotify(){
        return new AdapterCridetArchive.GoCredFragForNotify() {
            @Override
            public void notifyCredFrag() {
                crAdap.notifyDataSetChanged();
            }
        };
    }

    public CreditFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        creditDetialsDao = daoSession.getCreditDetialsDao();
        crList= (ArrayList<CreditDetials>) creditDetialsDao.queryBuilder().list();
        This=getActivity();
    }
    public  CreditTabLay.ForFab getEvent(){
        return new CreditTabLay.ForFab() {
            @Override
            public void pressedFab() {
                openFragment(new AddCreditFragment(),AddCreditFragment.OPENED_TAG);
            }
        };
    }
    public void setSvyaz(CreditTabLay.SvyazkaFragmentov A){
        svyaz=A;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        toolbarManager.setTitle(getResources().getString(R.string.cred_managment));
        toolbarManager.setSubtitle("");
        toolbarManager.setSpinnerVisibility(View.GONE);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
        View V=inflater.inflate(R.layout.fragment_credit, container, false);
        crRV=(RecyclerView) V.findViewById(R.id.my_recycler_view);
        LinearManagerWithOutEx llm = new LinearManagerWithOutEx(This);
        crRV.setLayoutManager(llm);

        crAdap=new AdapterCridet(This,svyaz);
        crRV.setAdapter(crAdap);

        crRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                creditTabLay.onScrolledList(dy > 0);
            }
        });
        return V;
    }

    public void setCreditTabLay (CreditTabLay creditTabLay) {
        this.creditTabLay = creditTabLay;
    }

    public void openFragment(Fragment fragment,String tag) {
        if (fragment != null) {
            if(tag.matches("Addcredit"))
                ((AddCreditFragment)fragment).addEventLis(new EventFromAdding() {
                    @Override
                    public void addedCredit() {
                        updateToFirst();
                    }

                    @Override
                    public void canceledAdding() {
                    }
                });
            paFragmentManager.getFragmentManager().popBackStack();
            paFragmentManager.displayFragment(fragment);
        }
    }
    public void updateToFirst(){
        try{
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                   crAdap.notifyItemInserted(0);
                }
            }, 50);
            try {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      crRV.scrollToPosition(0);
                    }
                }, 100);
            }
            catch (Exception o){}
        }
        catch (Exception o){}
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