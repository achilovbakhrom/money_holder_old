package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseObject;
import com.jim.pocketaccounter.database.SmsParseSuccess;
import com.jim.pocketaccounter.database.SmsParseSuccessDao;
import com.jim.pocketaccounter.debt.DebtBorrowFragment;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

public class SMSParseInfoFragment extends Fragment {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    LogicManager logicManager;

    private SmsParseObject object;
    private RecyclerView recyclerView;

    public SMSParseInfoFragment(SmsParseObject object) {
        this.object = object;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        View rootView = inflater.inflate(R.layout.sms_parse_info, container, false);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setImageToSecondImage(R.drawable.trash);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getResources().getString(R.string.delete))
                        .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        logicManager.deleteSmsParseObject(object);
                        paFragmentManager.getFragmentManager().popBackStack();
                        paFragmentManager.displayFragment(new SmsParseMainFragment());
                    }
                });
                builder.create().show();
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvSmsParseInfo);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        return rootView;
    }

    private class MyAdapter extends RecyclerView.Adapter<SMSParseInfoFragment.ViewHolder> {
        private List<SmsParseSuccess> successList;
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy HH:MM");
        public MyAdapter() {
            successList = daoSession.getSmsParseSuccessDao().queryBuilder().
                    where(SmsParseSuccessDao.Properties.SmsParseObjectId.eq(object.getId())).list();
            Toast.makeText(getContext(), "" + successList.size(), Toast.LENGTH_SHORT).show();
        }

        public int getItemCount() {
            return successList.size();
        }

        public void onBindViewHolder(final SMSParseInfoFragment.ViewHolder view, final int position) {
            view.tvDate.setText(simpleDateFormat.format(successList.get(position).getDate().getTime()));
            view.smsBody.setText(successList.get(position).getBody());
            view.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getResources().getString(R.string.delete))
                            .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            logicManager.deleteSmsParseSuccess(successList.get(position));
                            notifyDataSetChanged();
                            paFragmentManager.getFragmentManager().popBackStack();
                            paFragmentManager.displayFragment(new SmsParseMainFragment());
                        }
                    });
                    builder.create().show();
                }
            });
            if (successList.get(position).getIsSuccess()) {
                view.tvAmount.setText("" + successList.get(position).getAmount()
                        + successList.get(position).getCurrency().getAbbr());
                view.tvType.setText(successList.get(position).getType() == PocketAccounterGeneral.INCOME ? "income" : "expense");
            } else {
                view.tvAmount.setText("not success");
                view.tvType.setText("not parsing");
            }
        }

        public SMSParseInfoFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_recived_item, parent, false);
            return new SMSParseInfoFragment.ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView smsBody;
        public TextView tvType;
        public TextView tvAmount;
        public TextView tvDate;
        public ImageView deleteImage;

        public ViewHolder(View view) {
            super(view);
            smsBody = (TextView) view.findViewById(R.id.tvSmsParseRecieveBody);
            tvType = (TextView) view.findViewById(R.id.tvIncomeOrExpenseType);
            tvAmount = (TextView) view.findViewById(R.id.tvSmsParseRecieveAmount);
            tvDate = (TextView) view.findViewById(R.id.tvSmsParseSuccessDate);
            deleteImage = (ImageView) view.findViewById(R.id.imageView8);
        }
    }
}
