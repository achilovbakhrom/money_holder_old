package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.BoardButtonDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.report.FilterSelectable;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.FilterDialog;
import com.jim.pocketaccounter.utils.OperationsListDialog;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.cache.DataCache;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressLint({"InflateParams", "ValidFragment"})
public class CategoryInfoFragment extends Fragment {
	WarningDialog warningDialog;
    @Inject LogicManager logicManager;
    @Inject ToolbarManager toolbarManager;
    @Inject DaoSession daoSession;
	@Inject	ReportManager reportManager;
	@Inject	@Named(value = "display_formatter")	SimpleDateFormat dateFormat;
	@Inject	CommonOperations commonOperations;
	@Inject	PAFragmentManager paFragmentManager;
	@Inject	OperationsListDialog operationsListDialog;
	@Inject	FilterDialog filterDialog;
	@Inject	SharedPreferences sharedPreferences;
	@Inject	DataCache dataCache;
	private RootCategory rootCategory;
	private FABIcon fabCategoryIcon;
	private TextView tvCategoryInfoName;
	private TextView tvCategoryInfoType;
	private RecyclerView rvCategoryInfoOperations;
	private ImageView ivCategoryInfoFilter;
	private TextView tvCategoryInfoTotal;
	private TextView tvCategoryInfoSubcategories;
	@SuppressLint("ValidFragment")
	public CategoryInfoFragment(RootCategory rootCategory) {
		this.rootCategory = rootCategory;
	}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.category_info_layout, container, false);
		((PocketAccounter)getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		warningDialog = new WarningDialog(getContext());
		toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
		toolbarManager.setImageToSecondImage(R.drawable.ic_more_vert_black_48dp);
		toolbarManager.setImageToHomeButton(R.drawable.ic_back_button);
		toolbarManager.setTitle(getResources().getString(R.string.category));
		toolbarManager.setSubtitle(rootCategory.getName());
		toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paFragmentManager.getFragmentManager().popBackStack();
				paFragmentManager.displayFragment(new CategoryFragment());
			}
		});
		toolbarManager.setOnSecondImageClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showOperationsList();
			}
		});
		fabCategoryIcon = (FABIcon) rootView.findViewById(R.id.fabCategoryIcon);
		tvCategoryInfoName = (TextView) rootView.findViewById(R.id.tvCategoryInfoName);
		tvCategoryInfoType = (TextView) rootView.findViewById(R.id.tvCategoryInfoType);
		rvCategoryInfoOperations = (RecyclerView) rootView.findViewById(R.id.rvAccountDetailsInfo);
		int resId = getContext().getResources().getIdentifier(rootCategory.getIcon(), "drawable", getContext().getPackageName());
		Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		Bitmap bitmap = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
				(int) getResources().getDimension(R.dimen.twentyfive_dp), false);
		fabCategoryIcon.setImageBitmap(bitmap);
		tvCategoryInfoName.setText(rootCategory.getName());
		if (rootCategory.getType() == PocketAccounterGeneral.INCOME)
			tvCategoryInfoType.setText(getResources().getString(R.string.income));
		else
			tvCategoryInfoType.setText(getResources().getString(R.string.expanse));
		rvCategoryInfoOperations = (RecyclerView) rootView.findViewById(R.id.rvCategoryInfoOperations);
		rvCategoryInfoOperations.setLayoutManager(new LinearLayoutManager(getContext()));
		ivCategoryInfoFilter = (ImageView) rootView.findViewById(R.id.ivCategoryInfoFilter);
		ivCategoryInfoFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filterDialog.setOnDateSelectedListener(new FilterSelectable() {
					@Override
					public void onDateSelected(Calendar begin, Calendar end) {
						refreshOperationsList(begin, end);
					}
				});
				filterDialog.show();
			}
		});
		refreshOperationsList(filterDialog.getBeginDate(), filterDialog.getEndDate());
		tvCategoryInfoTotal = (TextView) rootView.findViewById(R.id.tvCategoryInfoTotal);
		DecimalFormat format = new DecimalFormat("0.##");
		tvCategoryInfoTotal.setText(getResources().getString(R.string.total)+" "+format.format(reportManager.getTotalAmountByCategory(rootCategory, filterDialog.getBeginDate(), filterDialog.getEndDate()))+
				commonOperations.getMainCurrency().getAbbr());
		tvCategoryInfoSubcategories = (TextView) rootView.findViewById(R.id.tvCategoryInfoSubcategories);
		if (!rootCategory.getSubCategories().isEmpty()) {
			String subcats = getResources().getString(R.string.sub_cats)+": ";
			for (int i = 0; i<rootCategory.getSubCategories().size(); i++) {
				subcats += rootCategory.getSubCategories().get(i).getName();
				if (i != rootCategory.getSubCategories().size()-1)
					subcats += ", ";
			}
			tvCategoryInfoSubcategories.setVisibility(View.VISIBLE);
			tvCategoryInfoSubcategories.setText(subcats);
		}
		return rootView;
	}

	private void showOperationsList() {
		String[] ops = new String[2];
		ops[0] = getResources().getString(R.string.to_edit);
		ops[1] = getResources().getString(R.string.delete);
		operationsListDialog.setAdapter(ops);
		operationsListDialog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position) {
					case 0:
						paFragmentManager.getFragmentManager().popBackStack();
						paFragmentManager.displayFragment(new RootCategoryEditFragment(rootCategory, PocketAccounterGeneral.NO_MODE, 0, null));
						break;
					case 1:
						warningDialog.setText(getResources().getString(R.string.category_delete_warning));
						warningDialog.setOnNoButtonClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								warningDialog.dismiss();
							}
						});
						warningDialog.setOnYesButtonListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								List<BoardButton> list = daoSession.getBoardButtonDao().queryBuilder().where(BoardButtonDao.Properties.CategoryId.eq(rootCategory.getId())).list();
								if (!list.isEmpty()) {
									int currentPage = 0, countOfButtons = 0;
									if (rootCategory.getType() == PocketAccounterGeneral.INCOME) {
										currentPage = sharedPreferences.getInt("income_current_page", 1);
										countOfButtons = 4;
									}
									else {
										currentPage = sharedPreferences.getInt("expense_current_page", 1);
										countOfButtons = 16;
									}
									for (BoardButton boardButton : list) {
										if (currentPage*countOfButtons <= boardButton.getPos()
												&& (currentPage+1)*countOfButtons > currentPage*countOfButtons) {
											BitmapFactory.Options options = new BitmapFactory.Options();
											options.inPreferredConfig = Bitmap.Config.RGB_565;
											Bitmap scaled = BitmapFactory.decodeResource(getResources(), R.drawable.no_category, options);
											scaled = Bitmap.createScaledBitmap(scaled, (int) getResources().getDimension(R.dimen.thirty_dp),
													(int) getResources().getDimension(R.dimen.thirty_dp), false);
											dataCache.getBoardBitmapsCache().put(boardButton.getId(), scaled);
										}
									}
								}
								logicManager.deleteRootCategory(rootCategory);
								dataCache.updateAllPercents();
								paFragmentManager.getFragmentManager().popBackStack();
								paFragmentManager.displayFragment(new CategoryFragment());
								warningDialog.dismiss();
							}
						});
						warningDialog.show();
						break;
				}
				operationsListDialog.dismiss();
			}
		});
		operationsListDialog.show();
	}

	private void refreshOperationsList(Calendar begin, Calendar end) {
		List<FinanceRecord> objects = reportManager.getCategoryOperations(rootCategory, begin, end);
		CategoryOperationsAdapter accountOperationsAdapter = new CategoryOperationsAdapter(objects);
		rvCategoryInfoOperations.setAdapter(accountOperationsAdapter);
	}

	private class CategoryOperationsAdapter extends RecyclerView.Adapter<CategoryInfoFragment.ViewHolder> {
		private List<FinanceRecord> result;
		public CategoryOperationsAdapter(List<FinanceRecord> result) {
			this.result = result;
		}
		public int getItemCount() {
			return result.size();
		}
		public void onBindViewHolder(final CategoryInfoFragment.ViewHolder view, final int position) {
			view.tvAccountInfoDate.setText(dateFormat.format(result.get(position).getDate().getTime()));
			String text = result.get(position).getCategory().getName();
			if (result.get(position).getSubCategory() != null)
				text += ", " + result.get(position).getSubCategory().getName();
			view.tvAccountInfoName.setText(text);
			String amount = "";
			if (result.get(position).getCategory().getType() == PocketAccounterGeneral.INCOME) {
				amount += "+"+result.get(position).getAmount() + result.get(position).getCurrency().getAbbr();
				view.tvAccountInfoAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.green_just));
			}
			else {
				amount += "-"+result.get(position).getAmount() + result.get(position).getCurrency().getAbbr();
				view.tvAccountInfoAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
			}
			view.tvAccountInfoAmount.setText(amount);
		}

		public CategoryInfoFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_info_operations, parent, false);
			return new CategoryInfoFragment.ViewHolder(view);
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView tvAccountInfoDate;
		TextView tvAccountInfoName;
		TextView tvAccountInfoAmount;
		public ViewHolder(View view) {
			super(view);
			tvAccountInfoDate = (TextView) view.findViewById(R.id.tvAccountInfoDate);
			tvAccountInfoName = (TextView) view.findViewById(R.id.tvAccountInfoName);
			tvAccountInfoAmount = (TextView) view.findViewById(R.id.tvAccountInfoAmount);
		}
	}
}
