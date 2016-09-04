package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.DatePicker;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.OnDatePickListener;
import com.jim.pocketaccounter.utils.OnIconPickListener;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.FABIcon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressLint({"InflateParams", "ValidFragment"})
public class AccountEditFragment extends Fragment implements OnClickListener, OnItemClickListener {
    @Inject
    LogicManager logicManager;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    DaoSession daoSession;
	@Inject
	@Named(value = "display_formmatter")
	SimpleDateFormat dateFormat;
	@Inject
	PAFragmentManager paFragmentManager;
	@Inject
	IconChooseDialog iconChooseDialog;
	@Inject
	DatePicker datePicker;

	private Account account;
	private EditText etAccountEditName;
	private FABIcon fabAccountIcon;
	private RelativeLayout checkBoxSum;
	private CheckBox chbAccountStartSumEnabled;
	private RelativeLayout rlStartSumContainer;
	private EditText etStartMoney;
	private Spinner spStartMoneyCurrency;
	private CheckBox chbLimit;
	private EditText etLimit;
	private Spinner spLimitCurrency;
	private CheckBox chbAccountLimitInterval;
	private LinearLayout llAccountLimitInterval;
	private TextView tvAccountLimitBegin;
	private TextView tvAccountLimitEnd;
	private LinearLayout llAccountLimitIntervalContainer;
	private RelativeLayout rlAccountLimitContainer;
	private CheckBox chbAccountNoneZero;
	private String choosenIcon = "icons_1";
	private Calendar begin = Calendar.getInstance();
	private Calendar end = Calendar.getInstance();
	@SuppressLint("ValidFragment")
	public AccountEditFragment(Account account) {
		this.account = account;
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.account_edit_layout, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
			}
		},100);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		toolbarManager.setImageToHomeButton(R.drawable.ic_back_button);
		toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paFragmentManager.getFragmentManager().popBackStack();
			}
		});
        toolbarManager.setTitle(getResources().getString(R.string.addedit));
        toolbarManager.setSubtitle("");
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.VISIBLE);
        toolbarManager.setSpinnerVisibility(View.GONE);
		toolbarManager.setImageToSecondImage(R.drawable.check_sign);
		toolbarManager.setOnSecondImageClickListener(this);
		List<Currency> currencies = daoSession.getCurrencyDao().loadAll();
		String[] items = new String[currencies.size()];
		int mainCurrencyPos = 0;
		for (int i=0; i<currencies.size(); i++) {
			if (currencies.get(i).getMain())
				mainCurrencyPos = i;
			items[i] = currencies.get(i).getAbbr();
		}
		ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, items);

		//1 account name
		etAccountEditName = (EditText) rootView.findViewById(R.id.etAccountEditName);

		//2 account icon
		fabAccountIcon = (FABIcon) rootView.findViewById(R.id.fabAccountIcon);
		int resId = getResources().getIdentifier(choosenIcon, "drawable", getContext().getPackageName());
		Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		Bitmap bitmap = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp),
				(int)getResources().getDimension(R.dimen.twentyfive_dp), false);
		fabAccountIcon.setImageBitmap(bitmap);
		fabAccountIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iconChooseDialog.setOnIconPickListener(new OnIconPickListener() {
					@Override
					public void OnIconPick(String icon) {
						choosenIcon = icon;
						int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
						Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
						Bitmap b = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
								(int) getResources().getDimension(R.dimen.twentyfive_dp), false);
						fabAccountIcon.setImageBitmap(b);
						iconChooseDialog.setSelectedIcon(icon);
						iconChooseDialog.dismiss();
					}
				});
				iconChooseDialog.show();
			}
		});

		//3 account start sum
		chbAccountStartSumEnabled = (CheckBox) rootView.findViewById(R.id.chbAccountStartSumEnabled);
		chbAccountStartSumEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					rlStartSumContainer.setVisibility(View.VISIBLE);
				else
					rlStartSumContainer.setVisibility(View.GONE);
			}
		});
		checkBoxSum = (RelativeLayout) rootView.findViewById(R.id.checkBoxSum);
		rlStartSumContainer = (RelativeLayout) rootView.findViewById(R.id.rlStartSumContainer);
		rlStartSumContainer.setVisibility(View.GONE);
		etStartMoney = (EditText) rootView.findViewById(R.id.etStartMoney);
		spStartMoneyCurrency = (Spinner) rootView.findViewById(R.id.spStartMoneyCurrency);
		spStartMoneyCurrency.setAdapter(arrayAdapter);
		spStartMoneyCurrency.setSelection(mainCurrencyPos);

		//4 account limit
		chbLimit = (CheckBox) rootView.findViewById(R.id.chbLimit);
		chbLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					llAccountLimitIntervalContainer.setVisibility(View.VISIBLE);
					rlAccountLimitContainer.setVisibility(View.VISIBLE);
				}
				else {
					llAccountLimitIntervalContainer.setVisibility(View.GONE);
					rlAccountLimitContainer.setVisibility(View.GONE);
				}
			}
		});
		etLimit = (EditText) rootView.findViewById(R.id.etLimit);
		spLimitCurrency = (Spinner) rootView.findViewById(R.id.spLimitCurrency);
		spLimitCurrency.setAdapter(arrayAdapter);
		spLimitCurrency.setSelection(mainCurrencyPos);

		//5 account limit interval
		chbAccountLimitInterval = (CheckBox) rootView.findViewById(R.id.chbAccountLimitInterval);
		chbAccountLimitInterval.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					llAccountLimitInterval.setVisibility(View.VISIBLE);
				else
					llAccountLimitInterval.setVisibility(View.GONE);
			}
		});
		llAccountLimitInterval = (LinearLayout) rootView.findViewById(R.id.llAccountLimitInterval);
		tvAccountLimitBegin = (TextView) rootView.findViewById(R.id.tvAccountLimitBegin);
		tvAccountLimitBegin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				datePicker.setOnDatePickListener(new OnDatePickListener() {
					@Override
					public void OnDatePick(Calendar pickedDate) {
						if (pickedDate.compareTo(end) >= 0)
							end = (Calendar) pickedDate.clone();
						begin = (Calendar) pickedDate.clone();
						tvAccountLimitBegin.setText(dateFormat.format(begin.getTime()));
						tvAccountLimitEnd.setText(dateFormat.format(end.getTime()));
					}
				});
				datePicker.show();
			}
		});
		tvAccountLimitEnd = (TextView) rootView.findViewById(R.id.tvAccountLimitEnd);
		tvAccountLimitEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				datePicker.setOnDatePickListener(new OnDatePickListener() {
					@Override
					public void OnDatePick(Calendar pickedDate) {
						if (pickedDate.compareTo(end) <= 0)
							begin = (Calendar) pickedDate.clone();
						end = (Calendar) pickedDate.clone();
						tvAccountLimitBegin.setText(dateFormat.format(begin.getTime()));
						tvAccountLimitEnd.setText(dateFormat.format(end.getTime()));
					}
				});
				datePicker.show();
			}
		});
		tvAccountLimitBegin.setText(dateFormat.format(begin.getTime()));
		tvAccountLimitEnd.setText(dateFormat.format(end.getTime()));
		llAccountLimitIntervalContainer = (LinearLayout) rootView.findViewById(R.id.llAccountLimitIntervalContainer);
		llAccountLimitIntervalContainer.setVisibility(View.GONE);
		rlAccountLimitContainer = (RelativeLayout) rootView.findViewById(R.id.rlAccountLimitContainer);
		rlAccountLimitContainer.setVisibility(View.GONE);

		//6 account none zero
		chbAccountNoneZero = (CheckBox) rootView.findViewById(R.id.chbAccountNoneZero);

		if (account != null) {
			etAccountEditName.setText(account.getName());
			resId = getResources().getIdentifier(account.getIcon(), "drawable", getContext().getPackageName());
			temp = BitmapFactory.decodeResource(getResources(), resId);
			bitmap = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp),
					(int)getResources().getDimension(R.dimen.twentyfive_dp), false);
			fabAccountIcon.setImageBitmap(bitmap);
			iconChooseDialog.setSelectedIcon(account.getIcon());
			if (account.getAmount()!=0) {
				chbAccountStartSumEnabled.setChecked(true);
				rlStartSumContainer.setVisibility(View.VISIBLE);
				etStartMoney.setText(Double.toString(account.getAmount()));
				for (int i=0; i<currencies.size(); i++)
					if (currencies.get(i).getId().matches(account.getStartMoneyCurrency().getId())) {
						spStartMoneyCurrency.setSelection(i);
						break;
					}
			}
			if (account.getLimited()) {
				chbLimit.setChecked(true);
				rlAccountLimitContainer.setVisibility(View.VISIBLE);
				etLimit.setText(Double.toString(account.getLimitSum()));
				if (account.getLimitInterval()) {
					llAccountLimitIntervalContainer.setVisibility(View.VISIBLE);
					tvAccountLimitBegin.setText(dateFormat.format(account.getLimitBeginTime().getTime()));
					tvAccountLimitEnd.setText(dateFormat.format(account.getLimitTime().getTime()));
				}
			}
		}
		return rootView;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		paFragmentManager.getFragmentManager().popBackStack();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.ivToolbarMostRight:
				if (etAccountEditName.getText().toString().matches("")) {
					etAccountEditName.setError(getString(R.string.enter_name_error));
					return;
				}
				Account account = null;
				if (account == null)
					account = new Account();
				else
					account = this.account;
				account.setName(etAccountEditName.getText().toString());

				if (!etStartMoney.getText().toString().matches("") && Double.parseDouble(etStartMoney.getText().toString()) != 0)
					account.setAmount(Double.parseDouble(etStartMoney.getText().toString()));
				else
					account.setAmount(0);
				account.setStartMoneyCurrency(daoSession.getCurrencyDao().loadAll()
						.get(spStartMoneyCurrency.getSelectedItemPosition()));
				account.setLimited(chbLimit.isChecked());
				if (etLimit.getText().toString().matches(""))
					account.setLimitSum(0);
				else
					account.setLimitSum(Double.parseDouble(etLimit.getText().toString()));
				account.setLimitCurrency(daoSession.getCurrencyDao().loadAll()
						.get(spLimitCurrency.getSelectedItemPosition()));
				account.setLimitBeginTime(begin);
				account.setLimitTime(end);
				account.setId(UUID.randomUUID().toString());
				account.setCalendar(Calendar.getInstance());
				account.setIcon(choosenIcon);
				account.setNonMinus(chbAccountNoneZero.isChecked());
				if (logicManager.insertAccount(account) == LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS) {
					etAccountEditName.setError(getString(R.string.such_account_name_exists_error));
					return;
				}
				else
					paFragmentManager.getFragmentManager().popBackStack();
				break;
		}
	}
}
