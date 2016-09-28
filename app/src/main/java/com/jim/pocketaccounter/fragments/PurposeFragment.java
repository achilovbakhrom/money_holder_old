package com.jim.pocketaccounter.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.managers.DrawerInitializer;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.TransferDialog;

import java.util.List;
import javax.inject.Inject;

/**
 * Created by DEV on 06.09.2016.
 */

public class PurposeFragment extends Fragment {
    private RecyclerView rvPurposes;
    private FABIcon fabPurposesAdd;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    DrawerInitializer drawerInitializer;
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    TransferDialog transferDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PurposeInfoFragment purposeInfoFragment = new PurposeInfoFragment(null);
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
        rvPurposes = (RecyclerView) rootView.findViewById(R.id.rvPurposes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPurposes.setLayoutManager(layoutManager);
        fabPurposesAdd = (FABIcon) rootView.findViewById(R.id.fabPurposesAdd);
        fabPurposesAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new PurposeEditFragment(null));
            }
        });
        refreshList();
        return  rootView;
    }
    private void refreshList() {
        PurposeAdapter adapter = new PurposeAdapter(daoSession.getPurposeDao().loadAll());
        rvPurposes.setAdapter(adapter);
    }

    private class PurposeAdapter extends RecyclerView.Adapter<PurposeFragment.ViewHolder> {
        private List<Purpose> result;
        public PurposeAdapter(List<Purpose> result) {
            Toast.makeText(getContext(), "" + result.size(), Toast.LENGTH_SHORT).show();
            this.result = result;
        }
        public int getItemCount() {
            return result.size();
        }
        public void onBindViewHolder(final PurposeFragment.ViewHolder view, final int position) {
            view.tvPurposeName.setText(result.get(position).getDescription());
            final int resId = getResources().getIdentifier(result.get(position).getIcon(), "drawable", getContext().getPackageName());
            view.ivPurposeItem.setImageResource(resId);
            view.tvPurposeName.setText(result.get(position).getDescription());
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new PurposeInfoFragment(result.get(position)));
                }
            });
            view.tvPutMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transferDialog.show();
                    transferDialog.setAccountOrPurpose(result.get(position).getId(), false);
                    transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                        @Override
                        public void OnTransferDialogSave() {
                            Toast.makeText(getContext(), "saved ", Toast.LENGTH_SHORT).show();
                            transferDialog.dismiss();
                        }
                    });
                }
            });
            view.tvGetMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transferDialog.show();
                    transferDialog.setAccountOrPurpose(result.get(position).getId(), true);
                    transferDialog.setOnTransferDialogSaveListener(new TransferDialog.OnTransferDialogSaveListener() {
                        @Override
                        public void OnTransferDialogSave() {
                            Toast.makeText(getContext(), "saved ", Toast.LENGTH_SHORT).show();
                            transferDialog.dismiss();
                        }
                    });
                }
            });
        }

        public PurposeFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purpose_list_item, parent, false);
            return new PurposeFragment.ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPurposeItem;
        TextView tvPurposeName;
        TextView tvPurposeRemain;
        TextView tvPutMoney;
        TextView tvGetMoney;
        View view;
        public ViewHolder(View view) {
            super(view);
            ivPurposeItem = (ImageView) view.findViewById(R.id.ivPurposeItem);
            tvPurposeName = (TextView) view.findViewById(R.id.tvPurposeName);
            tvPurposeRemain = (TextView) view.findViewById(R.id.tvPurposeRemain);
            tvPutMoney = (TextView) view.findViewById(R.id.tvPutMoney);
            tvGetMoney = (TextView) view.findViewById(R.id.tvGetMoney);
            this.view = view;
        }
    }
}
