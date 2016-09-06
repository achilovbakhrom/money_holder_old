package com.jim.pocketaccounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.managers.LogicManager;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by DEV on 29.08.2016.
 */

public class SubCatAddEditDialog extends Dialog {
    private TextView tv;
    private View dialogView;
    private FABIcon fabChooseIcon;
    private String subcatIcon;
    private SubCategory subCategory;
    private ImageView ivSubCatClose, ivSubCatSave;
    private String rootCategoryId;
    private EditText etSubCategoryName;
    @Inject
    IconChooseDialog iconsChooseDialog;
    @Inject
    LogicManager logicManager;
    public SubCatAddEditDialog(Context context) {
        super(context);
        ((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
        dialogView = getLayoutInflater().inflate(R.layout.sub_category_edit_layout, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        fabChooseIcon = (FABIcon) dialogView.findViewById(R.id.fabChooseIcon);
        etSubCategoryName = (EditText) dialogView.findViewById(R.id.etSubCategoryName);
    }

    public void setRootCategory(String rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
    }

    public void setSubCat(SubCategory subCategory, final OnSubcategorySavingListener onSubcategorySavingListener) {
        this.subCategory = subCategory;
        Bitmap temp, scaled;
        if (subCategory != null) {
            etSubCategoryName.setText(subCategory.getName());
            subcatIcon = subCategory.getIcon();
            int resId = getContext().getResources().getIdentifier(subCategory.getIcon(), "drawable", getContext().getPackageName());
            temp = BitmapFactory.decodeResource(getContext().getResources(), resId);
            scaled = Bitmap.createScaledBitmap(temp, (int) getContext().getResources().getDimension(R.dimen.twentyfive_dp),
                    (int) getContext().getResources().getDimension(R.dimen.twentyfive_dp), false);
        } else {
            etSubCategoryName.setText("");
            subcatIcon = "icons_4";
            int resId = getContext().getResources().getIdentifier(subcatIcon, "drawable", getContext().getPackageName());
            temp = BitmapFactory.decodeResource(getContext().getResources(), resId);
            scaled = Bitmap.createScaledBitmap(temp, (int) getContext().getResources().getDimension(R.dimen.twentyfive_dp),
                    (int) getContext().getResources().getDimension(R.dimen.twentyfive_dp), false);
        }
        fabChooseIcon.setImageBitmap(scaled);
        fabChooseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconsChooseDialog.setSelectedIcon(subcatIcon);
                iconsChooseDialog.setOnIconPickListener(new OnIconPickListener() {
                    @Override
                    public void OnIconPick(String icon) {
                        int resId = getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
                        Bitmap temp = BitmapFactory.decodeResource(getContext().getResources(), resId);
                        Bitmap scaled = Bitmap.createScaledBitmap(temp, (int) getContext().getResources().getDimension(R.dimen.twentyfive_dp),
                                (int) getContext().getResources().getDimension(R.dimen.twentyfive_dp), false);
                        fabChooseIcon.setImageBitmap(scaled);
                        subcatIcon = icon;
                        iconsChooseDialog.dismiss();
                    }
                });
                iconsChooseDialog.show();
            }
        });
        ivSubCatClose = (ImageView) dialogView.findViewById(R.id.ivSubCatClose);
        ivSubCatClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ivSubCatSave = (ImageView) dialogView.findViewById(R.id.ivSubCatSave);
        ivSubCatSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubCategory subCategory = null;
                if (SubCatAddEditDialog.this.subCategory == null) {
                    subCategory = new SubCategory();
                    subCategory.setId(UUID.randomUUID().toString());
                }
                else
                    subCategory = SubCatAddEditDialog.this.subCategory;
                subCategory.setParentId(rootCategoryId);
                subCategory.setName(etSubCategoryName.getText().toString());
                subCategory.setIcon(subcatIcon);
                onSubcategorySavingListener
                        .onSubcategorySaving(subCategory);
                etSubCategoryName.setText("");
            }
        });
    }

    public SubCatAddEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SubCatAddEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
