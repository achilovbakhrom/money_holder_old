package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.DrawerInitializer;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.FloatingActionButton;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.ScrollDirectionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressLint("InflateParams")
public class AccountFragment extends Fragment {
	private FloatingActionButton fabAccountAdd;
	private boolean[] selected;
    private RecyclerView recyclerView;
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
	@Inject
	DrawerInitializer drawerInitializer;
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.account_layout, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
			}
		},100);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		toolbarManager.setImageToHomeButton(R.drawable.ic_drawer);
		toolbarManager.setOnSecondImageClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerInitializer.getDrawer().openLeftSide();
			}
		});
        toolbarManager.setTitle(getResources().getString(R.string.accounts));
        toolbarManager.setSubtitle("");
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE);
        toolbarManager.setSpinnerVisibility(View.GONE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvAccounts);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAccountAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAccountAdd);
		fabAccountAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paFragmentManager.displayFragment(new AccountEditFragment(null));

			}
		});
        refreshList();
		return rootView;
	}

	private void refreshList() {
		AccountAdapter adapter = new AccountAdapter(daoSession.getAccountDao().loadAll());
		for (Account account : daoSession.getAccountDao().loadAll()) {
			Log.d("sss", account.getName() + "\n"+
			account.getLimited() + "\n"+
			account.getLimitInterval() + "\n"+
			account.getNonMinus() + "\n"+
			account.getLimitCurrency().getAbbr() + "\n"+
			account.getStartMoneyCurrency().getAbbr());

		}
		recyclerView.setAdapter(adapter);
	}

	private void deleteAccounts() {
		final List<Account> deletingAccounts = new ArrayList<>();
		List<Account> allAccounts = daoSession.getAccountDao().loadAll();
		for (int i=0; i<selected.length; i++) {
			if (!selected[i]) {
				deletingAccounts.add(allAccounts.get(i));
				break;
			}
		}
		warningDialog.setText(getResources().getString(R.string.account_delete_warning));
		warningDialog.setOnYesButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int result = logicManager.deleteAccount(deletingAccounts);
				if (result == LogicManagerConstants.MUST_BE_AT_LEAST_ONE_OBJECT)
					Toast.makeText(getContext(), "В системе должен быть, покрайней мере один счет!", Toast.LENGTH_SHORT).show();
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

    private class AccountAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Account> result;
        public AccountAdapter(List<Account> result) {
            this.result = result;
        }
        public int getItemCount() {
            return result.size();
        }
        public void onBindViewHolder(final ViewHolder view, final int position) {
            view.tvAccountListName.setText(result.get(position).getName());
			int resId = getResources().getIdentifier(result.get(position).getIcon(),"drawable", getContext().getPackageName());
            view.ivAccountListIcon.setImageResource(resId);
			if (result.get(position).getAmount() == 0)
            	view.tvAccStartMoney.setText(result.get(position).getAmount()+"");
			else
				view.tvAccStartMoney.setText(result.get(position).getAmount()+result.get(position).getStartMoneyCurrency().getAbbr());
			if (result.get(position).getLimited()) {
				view.rlProgressBar.setMax((int) result.get(position).getLimitSum());
				view.rlProgressBar.setProgress((int)reportManager.calculateLimitAccountsAmount(result.get(position)));
				view.llAccountLimit.setVisibility(View.VISIBLE);
                view.tvAccLimit.setText(Double.toString(result.get(position).getLimitSum()));
				if (result.get(position).getLimitInterval()) {
					view.tvAccLimitTimeInterval.setVisibility(View.VISIBLE);
					view.tvAccLimitTimeInterval.setText(dateFormat.format(result.get(position).getLimitBeginTime().getTime())+ "-"+
														dateFormat.format(result.get(position).getLimitTime().getTime()));
					view.tvAccountLimitPerDay.setVisibility(View.VISIBLE);
					view.tvAccountLimitPerDay.setText(Double.toString(result.get(position).getLimitSum()/commonOperations.countOfDayBetweenCalendars(
							result.get(position).getLimitBeginTime(),
							result.get(position).getLimitTime()
					)));
				}
				else {
					view.tvAccLimitTimeInterval.setVisibility(View.GONE);
					view.tvAccountLimitPerDay.setVisibility(View.GONE);
				}
				view.tvAccountRemain.setText(Double.toString(result.get(position).getLimitSum()-reportManager.calculateLimitAccountsAmount(result.get(position))));
            }
            else {
                view.tvAccLimit.setText(R.string.limit_not_setted);
				view.llAccountLimit.setVisibility(View.GONE);
			}
			Map<Currency, Double> map = reportManager.getRemain(result.get(position));
			String text = "";
			for (Currency currency : map.keySet())
				text = text + currency.getName() + ": " + map.get(currency).doubleValue() + " "+currency.getAbbr()+"\n";
			view.tvContent.setText(text);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list_item, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAccountListIcon;
        TextView tvAccountListName;
        TextView tvAccStartMoney;
        TextView tvAccLimit;
        TextView tvAccLimitDate;
        LinearLayout llAccountLimit;
        ProgressBar rlProgressBar;
        TextView tvContent;
		TextView tvAccountLimitPerDay;
		TextView tvAccountRemain;
		TextView tvAccLimitTimeInterval;
        View view;
        public ViewHolder(View view) {
            super(view);
            rlProgressBar = (ProgressBar) view.findViewById(R.id.rlProgressBar);
            ivAccountListIcon = (ImageView) view.findViewById(R.id.ivAccountListIcon);
            tvAccStartMoney = (TextView) view.findViewById(R.id.tvAccStartMoney);
            tvAccountListName = (TextView) view.findViewById(R.id.tvAccountListName);
            tvAccLimitDate = (TextView) view.findViewById(R.id.tvAccLimitDate);
            tvAccLimit = (TextView) view.findViewById(R.id.tvAccLimit);
			llAccountLimit = (LinearLayout) view.findViewById(R.id.llAccountLimit);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
			tvAccountLimitPerDay = (TextView) view.findViewById(R.id.tvAccountLimitPerDay);
			tvAccountRemain = (TextView) view.findViewById(R.id.tvAccountRemain);
			tvAccLimitTimeInterval = (TextView) view.findViewById(R.id.tvAccLimitTimeInterval);
            this.view = view;
        }
    }
}
