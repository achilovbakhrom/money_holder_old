package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.database.PurposeDao;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.report.FilterSelectable;
import com.jim.pocketaccounter.utils.FilterDialog;
import com.jim.pocketaccounter.utils.OperationsListDialog;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.TransferDialog;
import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 9/7/16.
 */
@SuppressLint("ValidFragment")
public class PurposeInfoFragment extends Fragment implements View.OnClickListener {
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    OperationsListDialog operationsListDialog;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    LogicManager logicManager;
    @Inject
    ReportManager reportManager;
    @Inject
    @Named(value = "display_formatter")
    SimpleDateFormat dateFormat;
    @Inject
    DaoSession daoSession;
    @Inject
    FilterDialog filterDialog;
    @Inject
    TransferDialog transferDialog;

    private MyAdapter myAdapter;
    private Purpose purpose;
    private ImageView iconPurpose;
    private ImageView deleteOpertions;
    private ImageView filterOpertions;
    private TextView namePurpose;
    private TextView amountPurpose;
    private TextView cashAdd;
    private TextView cashSend;
    private RecyclerView recyclerView;
    private boolean MODE = false;

    private Calendar beginDate;
    private Calendar endDate;

    public PurposeInfoFragment(Purpose purpose) {
        this.purpose = purpose;
        if (purpose == null) {
            this.purpose = new Purpose();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.purpose_info_layout, container, false);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        beginDate = null;
        endDate = null;
        deleteOpertions = (ImageView) rooView.findViewById(R.id.ivPurposeInfoDelete);
        filterOpertions = (ImageView) rooView.findViewById(R.id.ivPurposeInfoFilter);
        cashAdd = (TextView) rooView.findViewById(R.id.tvPurposeInfoToCash);
        cashSend = (TextView) rooView.findViewById(R.id.tvPurposeInfoReplanish);
        toolbarManager.setImageToSecondImage(R.drawable.ic_more_vert_black_48dp);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] st = new String[2];
                st[0] = getResources().getString(R.string.edit);
                st[1] = getResources().getString(R.string.delete);
                operationsListDialog.setAdapter(st);
                operationsListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            paFragmentManager.displayFragment(new PurposeEditFragment(purpose));
                        } else {
                            switch (logicManager.deletePurpose(purpose)) {
                                case LogicManagerConstants.REQUESTED_OBJECT_NOT_FOUND: {
                                    Toast.makeText(getContext(), "No this purpose", Toast.LENGTH_SHORT).show();
                                    operationsListDialog.dismiss();
                                    break;
                                }
                                case LogicManagerConstants.DELETED_SUCCESSFUL: {
                                    Toast.makeText(getContext(), "Success delete", Toast.LENGTH_SHORT).show();
                                    paFragmentManager.getFragmentManager().popBackStack();
                                    paFragmentManager.displayFragment(new PurposeFragment());
                                    operationsListDialog.dismiss();
                                    break;
                                }
                            }
                        }
                    }
                });
                operationsListDialog.show();
            }
        });

        iconPurpose = (ImageView) rooView.findViewById(R.id.ivPurposeinfoIcon);
        namePurpose = (TextView) rooView.findViewById(R.id.tvPurposeInfoName);
        amountPurpose = (TextView) rooView.findViewById(R.id.tvPurposeInfoAmount);
        recyclerView = (RecyclerView) rooView.findViewById(R.id.rvPurposeInfo);
        // ---------- icon set start ---------
        int resId = getResources().getIdentifier(purpose.getIcon(), "drawable", getContext().getPackageName());
        Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
        Bitmap bitmap = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
                (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
        iconPurpose.setImageBitmap(bitmap);
        // ---------- end icon set ---------
        namePurpose.setText(purpose.getDescription());
        amountPurpose.setText("" + purpose.getPurpose());
        deleteOpertions.setOnClickListener(this);
        filterOpertions.setOnClickListener(this);
        cashAdd.setOnClickListener(this);
        cashSend.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        return rooView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPurposeInfoDelete: {
                if (MODE) {
                    myAdapter.deleteOperation();
                }
                MODE = !MODE;
                myAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.ivPurposeInfoFilter: {
                filterDialog.show();
                filterDialog.setOnDateSelectedListener(new FilterSelectable() {
                    @Override
                    public void onDateSelected(Calendar begin, Calendar end) {
                        beginDate = (Calendar) begin.clone();
                        endDate = (Calendar) end.clone();
                        myAdapter.refreshFilterPurpose();
                    }
                });
                break;
            }
            case R.id.tvPurposeInfoToCash: {
                transferDialog.show();
                transferDialog.setAccountOrPurpose(purpose.getId(), false);
                transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                    @Override
                    public void OnTransferDialogSave() {
                        myAdapter = new MyAdapter();
                        Toast.makeText(getContext(), "" + reportManager.getAccountOpertions(purpose).size(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(myAdapter);
                    }
                });
                break;

            }
            case R.id.tvPurposeInfoReplanish: {
                transferDialog.show();
                transferDialog.setAccountOrPurpose(purpose.getId(), true);
                transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                    @Override
                    public void OnTransferDialogSave() {
                        myAdapter = new MyAdapter();
                        Toast.makeText(getContext(), "" + reportManager.getAccountOpertions(purpose).size(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(myAdapter);
                    }
                });
                break;
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<AccountOperation> purposes;
        private ArrayList<AccountOperation> allPurposes;
        private boolean tek[];

        public MyAdapter() {
            allPurposes = (ArrayList<AccountOperation>) reportManager.getAccountOpertions(purpose);
            purposes = (ArrayList<AccountOperation>) allPurposes.clone();
            tek = new boolean[purposes.size()];
        }

        public int getItemCount() {
            return purposes.size();
        }

        public void deleteOperation() {
            for (int i = tek.length - 1; i >= 0; i--) {
                if (tek[i]) {
                    logicManager.deleteAccountOperation(purposes.get(i));
                    Log.d("sss2", "pos = " + i);
                }
            }
            allPurposes = (ArrayList<AccountOperation>) reportManager.getAccountOpertions(purpose);
            purposes = (ArrayList<AccountOperation>) allPurposes.clone();
            tek = new boolean[purposes.size()];
            refreshFilterPurpose();

//            ReckingDao reckingDao = daoSession.getReckingDao();
//            Query<Recking> reckingQuery = reckingDao.queryBuilder().join(Account.class, ReckingDao.Properties.AccountId)
        }

        public void refreshFilterPurpose() {
            if (beginDate != null && endDate != null) {
                for (AccountOperation pr : allPurposes) {
                    if (pr.getDate().after(endDate) || pr.getDate().before(beginDate)) {
                        purposes.remove(pr);
                    } else {
                        if (purposes.indexOf(pr) == -1) {
                            purposes.add(pr);
                        }
                    }
                }
            }
        }

        public void onBindViewHolder(final ViewHolder view, final int position) {
            int type = purpose.getId().matches(purposes.get(position).getSourceId())
                    ? PocketAccounterGeneral.EXPENSE : PocketAccounterGeneral.INCOME;

            view.dateOperation.setText(dateFormat.format(purposes.get(position).getDate().getTime()));

            String name = "", sign = "";
            int color = 0;

            if (type == PocketAccounterGeneral.EXPENSE) {
                Query query = daoSession.getAccountDao().queryBuilder()
                        .where(AccountDao.Properties.Id.eq(purposes.get(position).getTargetId())).build();
                if (!query.list().isEmpty()) {
                    name = ((Account) query.list().get(0)).getName();
                } else {
                    query = daoSession.getPurposeDao().queryBuilder()
                            .where(PurposeDao.Properties.Id.eq(purposes.get(position).getTargetId())).build();
                    if (!query.list().isEmpty())
                        name = ((Purpose) query.list().get(0)).getDescription();
                }
                sign = "-";
                color = ContextCompat.getColor(getContext(), R.color.red);
            } else {
                Query query = daoSession.getAccountDao().queryBuilder()
                        .where(AccountDao.Properties.Id.eq(purposes.get(position).getSourceId())).build();
                if (!query.list().isEmpty()) {
                    name = ((Account) query.list().get(0)).getName();
                } else {
                    query = daoSession.getPurposeDao().queryBuilder()
                            .where(PurposeDao.Properties.Id.eq(purposes.get(position).getSourceId())).build();
                    if (!query.list().isEmpty()) {
                        name = ((Purpose) query.list().get(0)).getDescription();
                    }
                }
                sign = "+";
                color = ContextCompat.getColor(getContext(), R.color.green_just);
            }
            view.amount.setTextColor(color);
            view.amount.setText(sign + purposes.get(position).getAmount() + purposes.get(position).getCurrency().getAbbr());
            view.checkBox.setVisibility(View.GONE);
            view.accountName.setText(name);
            view.itemView.setOnClickListener(null);
            if (MODE) {
                view.checkBox.setVisibility(View.VISIBLE);
                view.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tek[position] = !tek[position];
                    }
                });
                view.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.checkBox.setChecked(!tek[position]);
                    }
                });
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_info_operations, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView dateOperation;
        public TextView accountName;
        public TextView amount;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.ivAccountInfoOpertionDelete);
            dateOperation = (TextView) view.findViewById(R.id.tvAccountInfoDate);
            accountName = (TextView) view.findViewById(R.id.tvAccountInfoName);
            amount = (TextView) view.findViewById(R.id.tvAccountInfoAmount);
        }
    }
}
