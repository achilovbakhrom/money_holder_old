package com.jim.pocketaccounter.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.managers.DrawerInitializer;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.DataCache;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.OnSubcategorySavingListener;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.SubCatAddEditDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CategoryFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
	private RecyclerView rvCategories;
	private CheckBox chbCatIncomes, chbCatExpanses;
	private FABIcon fabCategoryAdd;
	@Inject
	LogicManager logicManager;
	@Inject
	ToolbarManager toolbarManager;
	@Inject
	DrawerInitializer drawerInitializer;
	@Inject
	SubCatAddEditDialog subCatAddEditDialog;
	@Inject
	DaoSession daoSession;
	@Inject
	PAFragmentManager paFragmentManager;
	@Inject
	DataCache dataCache;
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.category_layout, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
			}
		},100);
		((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		toolbarManager.setTitle(getResources().getString(R.string.category));
		toolbarManager.setSubtitle("");
		toolbarManager.setImageToHomeButton(R.drawable.ic_drawer);
		toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerInitializer.getDrawer().openLeftSide();
			}
		});
		toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
		toolbarManager.setSpinnerVisibility(View.GONE);
		fabCategoryAdd = (FABIcon) rootView.findViewById(R.id.fabAccountAdd);
		fabCategoryAdd.setOnClickListener(this);
		rvCategories = (RecyclerView) rootView.findViewById(R.id.rvCategories);
		rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
		chbCatIncomes = (CheckBox) rootView.findViewById(R.id.chbCatIncomes);
		chbCatIncomes.setOnCheckedChangeListener(this);
		chbCatExpanses = (CheckBox) rootView.findViewById(R.id.chbCatExpanses);
		chbCatExpanses.setOnCheckedChangeListener(this);
		refreshList();
		return rootView;
	}
	private void refreshList() {
		ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
		List<RootCategory> rootCategories = daoSession.getRootCategoryDao().loadAll();
		for (RootCategory rootCategory : rootCategories) {
			if (chbCatIncomes.isChecked()) {
				if (rootCategory.getType() == PocketAccounterGeneral.INCOME)
					categories.add(rootCategory);
			}
			if(chbCatExpanses.isChecked()) {
				if (rootCategory.getType() == PocketAccounterGeneral.EXPENSE)
					categories.add(rootCategory);
			}
		}
		CategoryAdapter adapter = new CategoryAdapter(categories);
		rvCategories.setAdapter(adapter);
		getClass().getDeclaredMethods();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fabAccountAdd:
				paFragmentManager.displayFragment(new RootCategoryEditFragment(null));
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		refreshList();
	}

	private class CategoryAdapter extends RecyclerView.Adapter<CategoryFragment.ViewHolder> {
		private List<RootCategory> result;
		public CategoryAdapter(List<RootCategory> result) {
			this.result = result;
		}
		public int getItemCount() {
			return result.size();
		}
		public void onBindViewHolder(final CategoryFragment.ViewHolder view, final int position) {
			view.tvCategoryListItemName.setText(result.get(position).getName());
			final int resId = getResources().getIdentifier(result.get(position).getIcon(),"drawable", getContext().getPackageName());
			view.ivCategoryItem.setImageResource(resId);
			view.tvCategoryMainSubCatAdd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					subCatAddEditDialog.setRootCategory(result.get(position).getId());
					subCatAddEditDialog.setSubCat(null, new OnSubcategorySavingListener() {
						@Override
						public void onSubcategorySaving(SubCategory subCategory) {
							List<SubCategory> subCategories = new ArrayList<>();
							subCategories.add(subCategory);
							if (logicManager.insertSubCategory(subCategories) == LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS)
								Toast.makeText(getContext(), R.string.such_subcat_exist,
										Toast.LENGTH_SHORT).show();
						}
					});
					subCatAddEditDialog.show();
				}
			});
			view.tvCategoryMainEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dataCache.getCategoryEditFragmentDatas().setMode(PocketAccounterGeneral.NO_MODE);
					paFragmentManager.displayFragment(new RootCategoryEditFragment(result.get(position)));
				}
			});
			view.view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					paFragmentManager.displayFragment(new CategoryInfoFragment(result.get(position)));
				}
			});
		}
		public CategoryFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
			return new CategoryFragment.ViewHolder(view);
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView ivCategoryItem;
		TextView tvCategoryListItemName;
		TextView tvCategoryMainSubCatAdd;
		TextView tvCategoryMainEdit;
		View view;
		public ViewHolder(View view) {
			super(view);
			ivCategoryItem = (ImageView) view.findViewById(R.id.ivCategoryItem);
			tvCategoryListItemName = (TextView) view.findViewById(R.id.tvCategoryListItemName);
			tvCategoryMainSubCatAdd = (TextView) view.findViewById(R.id.tvCategoryMainSubCatAdd);
			tvCategoryMainEdit = (TextView) view.findViewById(R.id.tvCategoryMainEdit);
			this.view = view;
		}
	}

}