package com.jim.pocketaccounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.PurposeDao;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 10/7/16.
 */

public class TransferAddEditDialog extends Dialog {

    private View dialogView;
    private RecyclerView recyclerView;
    private ImageView ivClose;
    @Inject DaoSession daoSession;
    public TransferAddEditDialog(Context context) {
        super(context);
        ((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
        dialogView = getLayoutInflater().inflate(R.layout.transfer_add_edit_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        recyclerView = (RecyclerView) dialogView.findViewById(R.id.rvTransferAddEdit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshList();
    }

    private void refreshList() {
        TransferAddEditDialogAdapter transferAddEditDialogAdapter = new TransferAddEditDialogAdapter(daoSession.getAccountOperationDao().loadAll());
        recyclerView.setAdapter(transferAddEditDialogAdapter);
    }
    private String getAccountOrPurposeNameById(String id) {
        if (!daoSession.getAccountDao().queryBuilder().where(AccountDao.Properties.Id.eq(id)).list().isEmpty()) {
            return  daoSession.getAccountDao().queryBuilder().where(AccountDao.Properties.Id.eq(id)).list().get(0).getName();
        } else if (!daoSession.getPurposeDao().queryBuilder().where(PurposeDao.Properties.Id.eq(id)).list().isEmpty()) {
            return daoSession.getPurposeDao().queryBuilder().where(PurposeDao.Properties.Id.eq(id)).list().get(0).getDescription();
        }
        else return null;
    }

    private class TransferAddEditDialogAdapter extends RecyclerView.Adapter<TransferAddEditDialog.ViewHolder> {
        private List<AccountOperation> result;
        public TransferAddEditDialogAdapter(List<AccountOperation> result) {
            this.result = result;
        }
        public int getItemCount() {
            return result.size();
        }
        public void onBindViewHolder(final TransferAddEditDialog.ViewHolder view, final int position) {
            view.ivTransferAddEditEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TransferDialog transferDialog = new TransferDialog(getContext());
                    transferDialog.setAccountOperation(result.get(position));
                    transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                        @Override
                        public void OnTransferDialogSave() {
                            refreshList();
                        }
                    });
                    transferDialog.show();
                }
            });
            view.ivTransferAddEditTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final WarningDialog warningDialog = new WarningDialog(getContext());
                    warningDialog.setText(getContext().getResources().getString(R.string.do_you_want_to_delete));
                    warningDialog.setOnYesButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            daoSession.getAccountOperationDao().delete(result.get(0));
                            refreshList();
                            warningDialog.dismiss();
                        }
                    });
                    warningDialog.setOnNoButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            warningDialog.dismiss();
                        }
                    });
                    warningDialog.show();
                }
            });
            String fromName = getAccountOrPurposeNameById(result.get(position).getSourceId());
            if (fromName != null)
                view.tvTransferAddEditFrom.setText(getContext().getResources().getString(R.string.from)+fromName);
            DecimalFormat dateFormat = new DecimalFormat("0.00");
            String toName = getAccountOrPurposeNameById(result.get(position).getTargetId());
            if (toName != null)
                view.tvTransferAddEditTo.setText(getContext().getResources().getString(R.string.to)+toName);
            view.tvTransferAddEditAmount.setText(getContext().getResources().getString(R.string.amount)+
                    dateFormat.format(result.get(position).getAmount())+result.get(position).getCurrency().getAbbr());
        }

        public TransferAddEditDialog.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_add_edit_list_item, parent, false);
            return new TransferAddEditDialog.ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTransferAddEditEdit;
        ImageView ivTransferAddEditTrash;
        TextView tvTransferAddEditFrom;
        TextView tvTransferAddEditTo;
        TextView tvTransferAddEditAmount;
        public ViewHolder(View view) {
            super(view);
            ivTransferAddEditEdit = (ImageView) view.findViewById(R.id.ivTransferAddEditEdit);
            ivTransferAddEditTrash = (ImageView) view.findViewById(R.id.ivTransferAddEditTrash);
            tvTransferAddEditFrom = (TextView) view.findViewById(R.id.tvTransferAddEditFrom);
            tvTransferAddEditTo = (TextView) view.findViewById(R.id.tvTransferAddEditTo);
            tvTransferAddEditAmount = (TextView) view.findViewById(R.id.tvTransferAddEditAmount);
        }
    }

}
