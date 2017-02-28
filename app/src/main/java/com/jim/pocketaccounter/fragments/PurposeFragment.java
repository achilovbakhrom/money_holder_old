package com.jim.pocketaccounter.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.DrawerInitializer;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.ObservableScrollView;
import com.jim.pocketaccounter.utils.TransferDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by DEV on 06.09.2016.
 */

public class PurposeFragment extends Fragment{
    private RecyclerView rvPurposes;
    public static final String PURPOSE_ID = "purpose_id";
    private FABIcon fabPurposesAdd;
    TextView ifListEmpty;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    DrawerInitializer drawerInitializer;
    @Inject
    DaoSession daoSession;
    @Inject
    LogicManager logicManager;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    TransferDialog transferDialog;
    @Inject
    ReportManager reportManager;
    @Inject
    @Named(value = "display_formatter")
    SimpleDateFormat dateFormat;
    @Inject
    CommonOperations commonOperations;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.purpose_layout, container, false);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        toolbarManager.setTitle(getString(R.string.purposes));
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
        toolbarManager.setSubtitle("");
        toolbarManager.setImageToHomeButton(R.drawable.ic_drawer);
        toolbarManager.setOnHomeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerInitializer.getDrawer().openLeftSide();
            }
        });
        ifListEmpty = (TextView) rootView.findViewById(R.id.ifListEmpty);
        rvPurposes = (RecyclerView) rootView.findViewById(R.id.rvPurposes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPurposes.setLayoutManager(layoutManager);
        fabPurposesAdd = (FABIcon) rootView.findViewById(R.id.fabPurposesAdd);
        fabPurposesAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new PurposeEditFragment());
            }
        });
        refreshList();
        rvPurposes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                try {
                    onScrolledList(dy > 0);
                } catch (NullPointerException e) {
                }
            }
        });
        return  rootView;
    }
    private void refreshList() {
        PurposeAdapter adapter = new PurposeAdapter(daoSession.getPurposeDao().loadAll());
        if(daoSession.getPurposeDao().loadAll().size()==0){
            ifListEmpty.setVisibility(View.VISIBLE);
            ifListEmpty.setText(R.string.purpose_list_empty);
        }
        else ifListEmpty.setVisibility(View.GONE);
        rvPurposes.setAdapter(adapter);
    }


    private boolean show = false;
    public void onScrolledList(boolean k) {
        if (k) {
            if (!show)
                fabPurposesAdd.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_down));
            show = true;
        } else {
            if (show)
                fabPurposesAdd.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_up));
            show = false;
        }
    }


    private class PurposeAdapter extends RecyclerView.Adapter<PurposeFragment.ViewHolder> {
        private List<Purpose> result;
        private FrameLayout frameLayout;
        public PurposeAdapter(List<Purpose> result) {
            this.result = result;
        }
        public int getItemCount() {
            return result.size();
        }
        public void onBindViewHolder(final PurposeFragment.ViewHolder view, final int position) {
            final Purpose item = result.get(position);
            view.tvPurposeName.setText(item.getDescription());
            final int resId = getResources().getIdentifier(item.getIcon(), "drawable", getContext().getPackageName());
            view.ivPurposeItem.setImageResource(resId);
            view.tvPurposeName.setText(item.getDescription());
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paFragmentManager.getFragmentManager().popBackStack();

                    PurposeInfoFragment fragment = new PurposeInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(PurposeFragment.PURPOSE_ID, item.getId());
                    fragment.setArguments(bundle);
                    paFragmentManager.displayFragment(fragment);
                }
            });
            view.tvPutMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transferDialog.show();
                    transferDialog.setAccountOrPurpose(item.getId(), true);
                    transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                        @Override
                        public void OnTransferDialogSave() {
                            notifyItemChanged(position);
                            transferDialog.dismiss();
                        }
                    });
                }
            });
            view.tvGetMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transferDialog.show();
                    transferDialog.setAccountOrPurpose(item.getId(), false);
                    transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                        @Override
                        public void OnTransferDialogSave() {
                            notifyItemChanged(position);
                            transferDialog.dismiss();
                        }
                    });
                }
            });
            double leftAmmountdb = lefAmmount(item);
            double allAmmount = item.getPurpose();
            double paid = allAmmount - leftAmmountdb;
            view.tvNal.setText(parseToWithoutNull(leftAmmountdb) + item.getCurrency().getAbbr());
            view.tvPaid.setText(parseToWithoutNull(paid) + item.getCurrency().getAbbr());
            view.tvAllPurpose.setText(parseToWithoutNull(allAmmount) + item.getCurrency().getAbbr());
            double template = allAmmount / 100;
            float persant = (float) (paid / template);
            view.frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, persant));


            if (item.getBegin() != null && item.getEnd() != null) {
                view.leftdateGone.setVisibility(View.VISIBLE);
                int t[] = InfoCreditFragment.getDateDifferenceInDDMMYYYY(item.getBegin().getTime(), item.getEnd().getTime());
                if (t[0] * t[1] * t[2] < 0 && (t[0] + t[1] + t[2]) != 0) {
                    view.tvLeftDate.setText(R.string.ends);
                    view.tvLeftDate.setTextColor(Color.parseColor("#832e1c"));
                } else {
                    String left_date_string = "";
                    if (t[0] != 0) {
                               left_date_string += commonOperations.generateYearString(t[0]);
                    }
                    if (t[1] != 0) {
                        if (!left_date_string.matches("")) {
                            left_date_string += " ";
                        }
                        if (t[1] > 1) {
                            left_date_string += Integer.toString(t[1]) + " " + getString(R.string.moths);
                        } else {
                            left_date_string += Integer.toString(t[1]) + " " + getString(R.string.moth);
                        }
                    }
                    if (t[2] != 0) {
                        if (!left_date_string.matches("")) {
                            left_date_string += " ";
                        }
                        if (t[2] > 1) {
                            left_date_string += Integer.toString(t[2]) + " " + getString(R.string.days);
                        } else {
                            left_date_string += Integer.toString(t[2]) + " " + getString(R.string.day);
                        }
                    }
                    if(!left_date_string.equals(""))
                    view.tvLeftDate.setText(left_date_string);
                    else view.tvLeftDate.setText(R.string.ends);
                }
            } else {
                view.leftdateGone.setVisibility(View.GONE);
            }
        }

        public PurposeFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purpose_list_item, parent, false);
            return new PurposeFragment.ViewHolder(view);
        }
    }
    public String parseToWithoutNull(double A) {
        if (A == (int) A) {
            return Integer.toString((int) A);
        } else
            return dateFormat.format(A);
    }

    public Double lefAmmount(Purpose purpose){
        double qoldiq = 0;
        for (AccountOperation accountOperation: reportManager.getAccountOpertions(purpose)) {
            if (accountOperation.getTargetId().equals(purpose.getId()))
            qoldiq += commonOperations.getCost(accountOperation.getDate(), accountOperation.getCurrency(), purpose.getCurrency(),accountOperation.getAmount());
            else qoldiq -= commonOperations.getCost(accountOperation.getDate(), accountOperation.getCurrency(), purpose.getCurrency(),accountOperation.getAmount());
        }
        return purpose.getPurpose() - qoldiq;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPurposeItem;
        TextView tvPurposeName;
        TextView tvPutMoney;
        TextView tvGetMoney;
        TextView tvLeftDate;
        TextView tvNal;
        TextView tvAllPurpose;
        TextView tvPaid;
        FrameLayout frameLayout;
        RelativeLayout leftdateGone;
        View view;
        public ViewHolder(View view) {
            super(view);
            ivPurposeItem = (ImageView) view.findViewById(R.id.ivPurposeItem);
            tvPurposeName = (TextView) view.findViewById(R.id.tvPurposeName);
            tvPutMoney = (TextView) view.findViewById(R.id.tvPutMoney);
            tvGetMoney = (TextView) view.findViewById(R.id.tvGetMoney);
            tvLeftDate = (TextView) view.findViewById(R.id.tvLeftDate);
            tvAllPurpose = (TextView) view.findViewById(R.id.allAmmount);
            tvPaid = (TextView) view.findViewById(R.id.tvSumAmmount);
            frameLayout = (FrameLayout) view.findViewById(R.id.zapolnit);
            tvNal = (TextView) view.findViewById(R.id.tvNal);
            leftdateGone = (RelativeLayout) view.findViewById(R.id.left_date_for_gone);
            this.view = view;
        }
    }
}
