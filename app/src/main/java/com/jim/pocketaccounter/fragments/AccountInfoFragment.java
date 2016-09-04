package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountOperations;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.report.ReportObject;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.WarningDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressLint({"InflateParams", "ValidFragment"})
public class AccountInfoFragment extends Fragment {
	@Inject
	WarningDialog warningDialog;
    @Inject
    LogicManager logicManager;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    DaoSession daoSession;
	@Inject
	ReportManager reportManager;
	@Inject
	@Named(value = "display_formmatter")
	SimpleDateFormat dateFormat;
	@Inject
	CommonOperations commonOperations;
	@Inject
	PAFragmentManager paFragmentManager;

	private Account account;
	private FABIcon fabAccountIcon;
	private TextView tvAccountNameInfo;
	private TextView tvAccountConfigurationInfo;
	private LinearLayout llAccountLimitInfo;
	private RecyclerView rvAccountDetailsInfo;
	private ProgressBar pbAccountInfo;
	@SuppressLint("ValidFragment")
	public AccountInfoFragment(Account account) {
		this.account = account;
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.account_info_layout, container, false);
		fabAccountIcon = (FABIcon) rootView.findViewById(R.id.fabAccountIcon);
		tvAccountNameInfo = (TextView) rootView.findViewById(R.id.tvAccountNameInfo);
		tvAccountConfigurationInfo = (TextView) rootView.findViewById(R.id.tvAccountConfigurationInfo);
		llAccountLimitInfo = (LinearLayout) rootView.findViewById(R.id.llAccountLimitInfo);
		llAccountLimitInfo.setVisibility(View.GONE);
		rvAccountDetailsInfo = (RecyclerView) rootView.findViewById(R.id.rvAccountDetailsInfo);
		pbAccountInfo = (ProgressBar) rootView.findViewById(R.id.pbAccountInfo);
		int resId = getContext().getResources().getIdentifier(account.getIcon(), "drawable", getContext().getPackageName());
		Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		Bitmap bitmap = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
				(int) getResources().getDimension(R.dimen.twentyfive_dp), false);
		fabAccountIcon.setImageBitmap(bitmap);
		tvAccountNameInfo.setText(account.getName());
		String info = "";
		if (account.getAmount() == 0)
			info += 0 + "\n";
		else
			info += account.getAmount() + " "+account.getStartMoneyCurrency().getAbbr() + "\n";
		if (account.getLimited())
			info += account.getLimitSum() + " " + account.getLimitCurrency().getAbbr() + "\n";
		else
			info += getString(R.string.not_set) + "\n";
		if (account.getLimitInterval())
			info += dateFormat.format(account.getLimitBeginTime()) + " : "+dateFormat.format(account.getLimitTime()) + "\n";
		if (account.getNonMinus())
			info += getResources().getString(R.string.none_zero_account);
		tvAccountConfigurationInfo.setText(info);
		double sum = 0.0d;
		if (account.getLimited()) {
			sum = reportManager.calculateLimitAccountsAmount(account);
			pbAccountInfo.setMax((int) account.getLimitSum());
			pbAccountInfo.setProgress((int) sum);
		}
		rvAccountDetailsInfo = (RecyclerView) rootView.findViewById(R.id.rvAccountDetailsInfo);
		rvAccountDetailsInfo.setLayoutManager(new LinearLayoutManager(getContext()));
		refreshOperationsList();
		return rootView;
	}

	private void refreshOperationsList() {
		List<ReportObject> objects = reportManager.getAccountOperations(account, reportManager.getFirstDay(), Calendar.getInstance());
		AccountOperationsAdapter accountOperationsAdapter = new AccountOperationsAdapter(objects);
		rvAccountDetailsInfo.setAdapter(accountOperationsAdapter);
	}

	private class AccountOperationsAdapter extends RecyclerView.Adapter<AccountInfoFragment.ViewHolder> {
		private List<ReportObject> result;
		public AccountOperationsAdapter(List<ReportObject> result) {
			this.result = result;
		}
		public int getItemCount() {
			return result.size();
		}
		public void onBindViewHolder(final AccountInfoFragment.ViewHolder view, final int position) {
			view.tvAccountInfoDate.setText(dateFormat.format(result.get(position).getDate().getTime()));
			view.tvAccountInfoName.setText(result.get(position).getDescription());
			String amount = "";
			if (result.get(position).getType() == PocketAccounterGeneral.INCOME) {
				amount += "+"+result.get(position).getAmount() + result.get(position).getCurrency().getAbbr();
				view.tvAccountInfoAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.green_just));
			}
			else {
				amount += "-"+result.get(position).getAmount() + result.get(position).getCurrency().getAbbr();
				view.tvAccountInfoAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
			}
			view.tvAccountInfoAmount.setText(amount);
		}

		public AccountInfoFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_info_operations, parent, false);
			return new AccountInfoFragment.ViewHolder(view);
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
