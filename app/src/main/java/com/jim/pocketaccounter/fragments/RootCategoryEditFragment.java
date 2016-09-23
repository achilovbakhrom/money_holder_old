package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.finance.SubCategoryAdapter;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.cache.DataCache;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.OnIconPickListener;
import com.jim.pocketaccounter.utils.OnSubcategorySavingListener;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.SubCatAddEditDialog;
import com.jim.pocketaccounter.utils.WarningDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

@SuppressLint({"InflateParams", "ValidFragment"})
public class RootCategoryEditFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private EditText etCatEditName;
	private CheckBox chbCatEditExpanse, chbCatEditIncome;
	private FABIcon fabCatIcon;
	private ImageView ivSubCatAdd, ivSubCatDelete, ivToolbarMostRight;
	private ListView lvSubCats;
	private RootCategory category;
	private int mode = PocketAccounterGeneral.NORMAL_MODE;
	private String selectedIcon = "icons_1";
	private boolean[] selected;
	private String[] icons;
	private List<SubCategory> subCategories;
	private String categoryId;
	private int editMode, pos;
	@Inject
	DaoSession daoSession;
	@Inject
	ToolbarManager toolbarManager;
	@Inject
	PAFragmentManager paFragmentManager;
	@Inject
	IconChooseDialog iconChooseDialog;
	@Inject
	SubCatAddEditDialog subCatAddEditDialog;
	@Inject
	LogicManager logicManager;
	@Inject
	WarningDialog warningDialog;
	public RootCategoryEditFragment(RootCategory rootCategory, int mode, int pos, Calendar date) {
		category = rootCategory;
		editMode = mode;
		this.pos = pos;
	}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cat_edit_layout, container, false);
		((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		toolbarManager.setImageToHomeButton(R.drawable.ic_back_button);
		toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
		toolbarManager.setTitle(getResources().getString(R.string.category));
		toolbarManager.setSubtitle(getResources().getString(R.string.edit));
		toolbarManager.setImageToSecondImage(R.drawable.check_sign);
		toolbarManager.setOnSecondImageClickListener(this);
		toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				v.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (editMode == PocketAccounterGeneral.NO_MODE)
							paFragmentManager.displayFragment(new CategoryFragment());
						else {
							paFragmentManager.displayMainWindow();
							paFragmentManager.getFragmentManager().popBackStack();
						}
					}
				},50);
			}
		});

		etCatEditName = (EditText) rootView.findViewById(R.id.etAccountEditName);
		chbCatEditExpanse = (CheckBox) rootView.findViewById(R.id.chbCatEditExpanse);
		chbCatEditIncome = (CheckBox) rootView.findViewById(R.id.chbCatEditIncome);
		if (editMode == PocketAccounterGeneral.EXPANSE_MODE) {
			chbCatEditExpanse.setChecked(true);
			chbCatEditIncome.setChecked(false);

		}
		if (editMode == PocketAccounterGeneral.INCOME_MODE) {
			chbCatEditExpanse.setChecked(false);
			chbCatEditIncome.setChecked(true);

		}
		chbCatEditExpanse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (editMode == PocketAccounterGeneral.NO_MODE) {
					chbCatEditExpanse.setChecked(isChecked);
					chbCatEditIncome.setChecked(!isChecked);
				}
				if (editMode == PocketAccounterGeneral.EXPANSE_MODE) {
					chbCatEditExpanse.setChecked(true);
					chbCatEditIncome.setChecked(false);
				}
				if (editMode == PocketAccounterGeneral.INCOME_MODE) {
					chbCatEditExpanse.setChecked(false);
					chbCatEditIncome.setChecked(true);
				}
			}
		});
		chbCatEditIncome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (editMode == PocketAccounterGeneral.NO_MODE) {
					chbCatEditExpanse.setChecked(!isChecked);
					chbCatEditIncome.setChecked(isChecked);
				}
				if (editMode == PocketAccounterGeneral.EXPANSE_MODE) {
					chbCatEditExpanse.setChecked(true);
					chbCatEditIncome.setChecked(false);
				}
				if (editMode == PocketAccounterGeneral.INCOME_MODE) {
					chbCatEditExpanse.setChecked(false);
					chbCatEditIncome.setChecked(true);
				}
			}
		});
		fabCatIcon = (FABIcon) rootView.findViewById(R.id.fabAccountIcon);
		fabCatIcon.setOnClickListener(this);
		ivSubCatAdd = (ImageView) rootView.findViewById(R.id.ivSubCatAdd);
		ivSubCatAdd.setOnClickListener(this);
		ivSubCatDelete = (ImageView) rootView.findViewById(R.id.ivSubCatDelete);
		ivSubCatDelete.setOnClickListener(this);
		lvSubCats = (ListView) rootView.findViewById(R.id.lvAccountHistory);
		lvSubCats.setOnItemClickListener(this);
		categoryId = UUID.randomUUID().toString();
		mode = PocketAccounterGeneral.NORMAL_MODE;
		setMode(mode);
		if (category != null) {
			etCatEditName.setText(category.getName());
			chbCatEditIncome.setChecked(false);
			chbCatEditExpanse.setChecked(false);
			switch(category.getType()) {
			case PocketAccounterGeneral.INCOME:
				chbCatEditIncome.setChecked(true);
				break;
			case PocketAccounterGeneral.EXPENSE:
				chbCatEditExpanse.setChecked(true);
				break;
			}
			categoryId = category.getId();
			selectedIcon = category.getIcon();
			subCategories = category.getSubCategories();
			refreshSubCatList(mode);
		}
		int resId = getResources().getIdentifier(selectedIcon, "drawable", getContext().getPackageName());
		Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		Bitmap icon = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp), (int)getResources().getDimension(R.dimen.twentyfive_dp), false);
		fabCatIcon.setImageBitmap(icon);
		return rootView;
	}
	private void refreshSubCatList(int mode) {
		if (subCategories == null) return;
		SubCategoryAdapter adapter = new SubCategoryAdapter(getActivity(), subCategories, selected, mode);
		lvSubCats.setAdapter(adapter);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE) {
			subCatAddEditDialog.setRootCategory(categoryId);
			subCatAddEditDialog.setSubCat(subCategories.get(position), new OnSubcategorySavingListener() {
				@Override
				public void onSubcategorySaving(SubCategory subCategory) {
					if (subCategories == null) return;
					for (SubCategory s : subCategories) {
						if (s.getName().equals(subCategory.getName()) && !s.getId().matches(subCategory.getId())) {
							Toast.makeText(getContext(), R.string.such_subcat_exist, Toast.LENGTH_SHORT).show();
							return;
						}
					}
					for (int i=0; i<subCategories.size(); i++) {
						if (subCategories.get(i).getId().matches(subCategory.getId())) {
							subCategories.set(i, subCategory);
							break;
						}
					}
					refreshSubCatList(mode);
					subCatAddEditDialog.dismiss();
				}
			});
			subCatAddEditDialog.show();
		}
		else {
			CheckBox chbSubCat = (CheckBox)view.findViewById(R.id.chbSubCat);
			chbSubCat.setChecked(!chbSubCat.isChecked());
			selected[position] = chbSubCat.isChecked();
		}
	}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		switch(v.getId()) {
		case R.id.fabAccountIcon:
			iconChooseDialog.setSelectedIcon(selectedIcon);
			iconChooseDialog.setOnIconPickListener(new OnIconPickListener() {
				@Override
				public void OnIconPick(String icon) {
					selectedIcon = icon;
					Bitmap temp, scaled;
					int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
					temp = BitmapFactory.decodeResource(getResources(), resId);
					scaled = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp),
						(int)getResources().getDimension(R.dimen.twentyfive_dp), false);
					fabCatIcon.setImageBitmap(scaled);
					iconChooseDialog.dismiss();
				}
			});
			iconChooseDialog.show();
			break;
		case R.id.ivSubCatAdd:
			subCatAddEditDialog.setRootCategory(categoryId);
			subCatAddEditDialog.setSubCat(null, new OnSubcategorySavingListener() {
				@Override
				public void onSubcategorySaving(SubCategory subCategory) {
					for (SubCategory subcategory : subCategories)
						if(subcategory.getName().equals(subCategory.getName())) {
							Toast.makeText(getContext(), R.string.such_subcat_exist, Toast.LENGTH_SHORT).show();
							return;
						}
					if (subCategories == null)
						subCategories = new ArrayList<>();
					subCategories.add(subCategory);
					refreshSubCatList(mode);
					subCatAddEditDialog.dismiss();
				}
			});
			subCatAddEditDialog.show();
			break;
		case R.id.ivSubCatDelete:
			if (mode == PocketAccounterGeneral.NORMAL_MODE) {
				mode = PocketAccounterGeneral.EDIT_MODE;
				setMode(mode);
			}
			else {
				mode = PocketAccounterGeneral.NORMAL_MODE;
				boolean isAnySelected = false;
				for (int i=0; i<selected.length; i++) {
					if (selected[i]) {
						isAnySelected = true;
						break;
					}
				}
				if (isAnySelected) {
					warningDialog.setText(getResources().getString(R.string.subcat_delete_warning));
					warningDialog.setOnYesButtonListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							List<SubCategory> deleting = new ArrayList<>();
							for (int i=0; i<selected.length; i++)
								if(selected[i]) {
									deleting.add(subCategories.get(i));
									subCategories.set(i, null);
								}
							logicManager.deleteSubcategories(deleting);
							for (int i=0; i<subCategories.size(); i++) {
								if (subCategories.get(i) == null) {
									subCategories.remove(i);
									i--;
								}
							}
							refreshSubCatList(mode);
							mode = PocketAccounterGeneral.NORMAL_MODE;
							setMode(mode);
							warningDialog.dismiss();
						}
					});
					warningDialog.setOnNoButtonClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							warningDialog.dismiss();
						}
					});
					warningDialog.show();
				}
			}
			break;
		case R.id.ivToolbarMostRight:
			if (etCatEditName.getText().toString().matches("")) {
				etCatEditName.setError(getResources().getString(R.string.category_name_error));
				return;
			}
			if (!chbCatEditIncome.isChecked() && !chbCatEditExpanse.isChecked()) {
				Toast.makeText(getActivity(), getResources().getString(R.string.cat_type_not_choosen), Toast.LENGTH_SHORT).show();
				return;
			}

			RootCategory rootCategory = null;
			if (category == null)
				rootCategory = new RootCategory();
			else
				rootCategory = category;
			rootCategory.setName(etCatEditName.getText().toString());
			if (chbCatEditIncome.isChecked())
				rootCategory.setType(PocketAccounterGeneral.INCOME);
			else
				rootCategory.setType(PocketAccounterGeneral.EXPENSE);
			rootCategory.setIcon(selectedIcon);
			if (subCategories != null && logicManager.insertSubCategory(subCategories) == LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS) {
				Toast.makeText(getContext(), R.string.such_subcat_exist, Toast.LENGTH_SHORT).show();
				return;
			}
			rootCategory.setSubCategories(subCategories);
			rootCategory.setId(categoryId);
			logicManager.insertRootCategory(rootCategory);
			if (editMode == PocketAccounterGeneral.NO_MODE) {
				paFragmentManager.displayFragment(new CategoryFragment());
			}
			else {
				logicManager.changeBoardButton(rootCategory.getType(),
						pos, categoryId);
				paFragmentManager.displayMainWindow();
				paFragmentManager.getFragmentManager().popBackStack();
			}
			break;
		}
	}
	private void setMode(int mode) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE) {
			ivSubCatDelete.setImageResource(R.drawable.subcat_delete);
			selected = null;
		}
		else {
			ivSubCatDelete.setImageResource(R.drawable.ic_cat_trash);
			selected = new boolean[subCategories.size()];
		}
		refreshSubCatList(mode);
	}
}