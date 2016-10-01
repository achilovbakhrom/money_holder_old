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
    @Inject LogicManager logicManager;
    @Inject ToolbarManager toolbarManager;
    @Inject DaoSession daoSession;
	@Inject	@Named(value = "display_formatter")	SimpleDateFormat dateFormat;
	@Inject	PAFragmentManager paFragmentManager;
	@Inject	IconChooseDialog iconChooseDialog;
	@Inject	DatePicker datePicker;
	private Account account;
	private EditText etAccountEditName;
	private FABIcon fabAccountIcon;
	private RelativeLayout checkBoxSum;
	private CheckBox chbAccountStartSumEnabled;
	private RelativeLayout rlStartSumContainer;
	private EditText etStartMoney;
	private Spinner spStartMoneyCurrency;
	private CheckBox chbAccountNoneZero;
	private String choosenIcon = "icons_1";
	private TextView tvNoneMinusAccountTitle, tvStartSumAccountTitle;
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
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
			}
		},100);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		toolbarManager.setImageToHomeButton(R.drawable.ic_back_button);
		toolbarManager.setOnHomeButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paFragmentManager.getFragmentManager().popBackStack();
				paFragmentManager.displayFragment(new AccountFragment());
			}
		});
        toolbarManager.setTitle(getResources().getString(R.string.addedit));
        toolbarManager.setSubtitle("");
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
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
		tvStartSumAccountTitle = (TextView) rootView.findViewById(R.id.tvStartSumAccountTitle);
		tvStartSumAccountTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chbAccountStartSumEnabled.toggle();
			}
		});
		//4 account none zero
		chbAccountNoneZero = (CheckBox) rootView.findViewById(R.id.noneZeroAccount);
		tvNoneMinusAccountTitle = (TextView) rootView.findViewById(R.id.tvNoneMinusAccountTitle);
		tvNoneMinusAccountTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chbAccountNoneZero.toggle();
			}
		});
		if (account != null) {
			etAccountEditName.setText(account.getName());
			resId = getResources().getIdentifier(account.getIcon(), "drawable", getContext().getPackageName());
			temp = BitmapFactory.decodeResource(getResources(), resId);
			bitmap = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp),
					(int)getResources().getDimension(R.dimen.twentyfive_dp), false);
			fabAccountIcon.setImageBitmap(bitmap);
			chbAccountNoneZero.setChecked(account.getNoneMinusAccount());
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
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (etAccountEditName.getText().toString().matches("")) {
					etAccountEditName.setError(getString(R.string.enter_name_error));
					return;
				}
				Account account;
				if (this.account == null) {
					account = new Account();
					account.setId(UUID.randomUUID().toString());
					account.setCalendar(Calendar.getInstance());
				}
				else
					account = this.account;
				account.setName(etAccountEditName.getText().toString());

				if (!etStartMoney.getText().toString().matches("") && Double.parseDouble(etStartMoney.getText().toString()) != 0)
					account.setAmount(Double.parseDouble(etStartMoney.getText().toString()));
				else
					account.setAmount(0);
				account.setStartMoneyCurrency(daoSession.getCurrencyDao().loadAll()
						.get(spStartMoneyCurrency.getSelectedItemPosition()));
				account.setIcon(choosenIcon);
				account.setNoneMinusAccount(chbAccountNoneZero.isChecked());
				if (this.account != null) {
					daoSession.getAccountDao().insertOrReplace(account);
					paFragmentManager.getFragmentManager().popBackStack();
					paFragmentManager.displayFragment(new AccountFragment());
				}
				else {
					if (logicManager.insertAccount(account) == LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS) {
						etAccountEditName.setError(getString(R.string.such_account_name_exists_error));
						return;
					}
					else {
						paFragmentManager.getFragmentManager().popBackStack();
						paFragmentManager.displayFragment(new AccountFragment());
					}
				}
				break;
		}
	}
}
