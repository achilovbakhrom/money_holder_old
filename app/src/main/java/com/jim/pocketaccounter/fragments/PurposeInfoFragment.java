package com.jim.pocketaccounter.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.debt.AddBorrowFragment;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.OperationsListDialog;

import javax.inject.Inject;

/**
 * Created by root on 9/7/16.
 */
public class PurposeInfoFragment extends Fragment {
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    OperationsListDialog operationsListDialog;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    LogicManager logicManager;

    private Purpose purpose;
    private ImageView iconPurpose;
    private TextView namePurpose;
    private TextView amountPurpose;
    private TextView remindPurpose;
    private TextView datePurpose;
    private RecyclerView recyclerView;

    public PurposeInfoFragment (Purpose purpose) {
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
        toolbarManager.setImageToSecondImage(R.drawable.ic_more_vert_black_48dp);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] st = new String[2];
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
        remindPurpose = (TextView) rooView.findViewById(R.id.tvPurposeInfoRemained);
        datePurpose = (TextView) rooView.findViewById(R.id.tvPurposeInfoDate);
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

        return rooView;
    }



}
