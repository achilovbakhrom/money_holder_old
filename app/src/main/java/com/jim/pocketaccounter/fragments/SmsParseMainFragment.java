package com.jim.pocketaccounter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseSuccess;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.FloatingActionButton;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 9/29/16.
 */

public class SmsParseMainFragment extends Fragment implements View.OnClickListener {
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    private RecyclerView recyclerView;
    private AddSmsParseFragment addSmsParseFragment;
    private FloatingActionButton floatingActionButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credit_tab_lay, container, false);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
        rootView.findViewById(R.id.viewpager).setVisibility(View.GONE);
        rootView.findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvSmsParseAllList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fbDebtBorrowFragment);
        floatingActionButton.setOnClickListener(this);
        addSmsParseFragment = new AddSmsParseFragment();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbDebtBorrowFragment: {
                paFragmentManager.getFragmentManager().popBackStack();
                paFragmentManager.displayFragment(new AddSmsParseFragment());
                break;
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<SmsParseMainFragment.ViewHolder> {
        private List<SmsParseObject> smsParseObjects;

        public MyAdapter() {
            this.smsParseObjects = daoSession.getSmsParseObjectDao().loadAll();
            Log.d("sss3", "" + smsParseObjects.size());
            Toast.makeText(getContext(), "" + smsParseObjects.size(), Toast.LENGTH_SHORT).show();
        }

        public int getItemCount() {
            return smsParseObjects.size();
        }

        public void onBindViewHolder(final SmsParseMainFragment.ViewHolder view, final int position) {
            view.tvNumber.setText(smsParseObjects.get(position).getNumber());
            view.tvAccount.setText(smsParseObjects.get(position).getAccount().getName());
            view.tvMessagesCount.setText("" + smsParseObjects.get(position).getSuccessList().size());
            if (smsParseObjects.get(position).getSuccessList() != null) {
                double incomeSumm = 0;
                double expanceSumm = 0;
                for (SmsParseSuccess parseSuccess : smsParseObjects.get(position).getSuccessList()) {
                    if (parseSuccess.getType() == PocketAccounterGeneral.INCOME) {
                        incomeSumm += parseSuccess.getAmount();
                    } else
                        expanceSumm += parseSuccess.getAmount();
                }
                view.tvAllExpense.setText("" + expanceSumm + smsParseObjects.get(position).getCurrency().getAbbr());
                view.tvAllIncomes.setText("" + incomeSumm + smsParseObjects.get(position).getCurrency().getAbbr());
            }
            view.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (view.llInfoGone.getVisibility() == View.GONE) {
                        view.llInfoGone.setVisibility(View.VISIBLE);
                    } else {
                        view.llInfoGone.setVisibility(View.GONE);
                    }
                }
            });
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new SMSParseInfoFragment(smsParseObjects.get(position)));
                }
            });
        }

        public SmsParseMainFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_adapter_root, parent, false);
            return new SmsParseMainFragment.ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNumber;
        public TextView tvNotReadCount;
        public TextView tvAccount;
        public TextView tvAllIncomes;
        public TextView tvAllExpense;
        public TextView tvMessagesCount;
        public RelativeLayout info;
        public LinearLayout llInfoGone;

        public ViewHolder(View view) {
            super(view);
            tvNumber = (TextView) view.findViewById(R.id.tvSmsSenderNumber);
            tvNotReadCount = (TextView) view.findViewById(R.id.tvNotMessageReadCount);
            tvAccount = (TextView) view.findViewById(R.id.tvAccount);
            tvAllExpense= (TextView) view.findViewById(R.id.tvAllExpense);
            tvAllIncomes= (TextView) view.findViewById(R.id.tvAllIncomes);
            tvMessagesCount = (TextView) view.findViewById(R.id.tvProcessedCout);
            info = (RelativeLayout) view.findViewById(R.id.rlSmsAdapterInfo);
            llInfoGone = (LinearLayout) view.findViewById(R.id.llItemInfoView);
        }
    }
}
