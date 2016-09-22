package com.jim.pocketaccounter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.AdapterCridetArchive;

public class CreditArchiveFragment extends Fragment {
    RecyclerView crRV;
    AdapterCridetArchive crAdap;
    Context This;
    AdapterCridetArchive.GoCredFragForNotify svyazForNotifyFromArchAdap;
    public void setSvyazToAdapter(AdapterCridetArchive.GoCredFragForNotify goNotify){
        svyazForNotifyFromArchAdap=goNotify;
    }
    public CreditArchiveFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         This=getActivity();
    }
    public CreditTabLay.SvyazkaFragmentov getSvyaz(){
        return new CreditTabLay.SvyazkaFragmentov() {
            @Override
            public void itemInsertedToArchive() {
                updateList();
                Log.d("checkInterfaces", "ARCHIVE - updateList();");
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("checkInterfaces", "onCreatView : " );
        View V=inflater.inflate(R.layout.fragment_credit, container, false);
        crRV=(RecyclerView) V.findViewById(R.id.my_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(This);
        crRV.setLayoutManager(llm);
        crAdap=new AdapterCridetArchive(This);
        crAdap.setSvyazToAdapter(svyazForNotifyFromArchAdap);
        crRV.setAdapter(crAdap);
        return V;
    }

    public void updateList(){
        Log.d("checkInterfaces", (crAdap==null)?"AdapterIsNull":"AdapterIsNotNull");
        crAdap.updateBase();
        crAdap.notifyDataSetChanged();
        if(crRV.getChildCount()>0);
        crRV.scrollToPosition(0);
    }
}
