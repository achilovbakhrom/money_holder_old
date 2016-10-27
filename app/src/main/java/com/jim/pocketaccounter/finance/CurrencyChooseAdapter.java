package com.jim.pocketaccounter.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.utils.CurrencyChecbox;
import com.jim.pocketaccounter.database.DaoSession;

import java.util.ArrayList;

import javax.inject.Inject;

public class CurrencyChooseAdapter extends BaseAdapter {
	private ArrayList<Currency> result;
	private LayoutInflater inflater;
	private boolean[] chbs;
	@Inject
	DaoSession daoSession;

	public CurrencyChooseAdapter(Context context, ArrayList<Currency> result, boolean[] chbs) {
	    this.result = result;
		this.chbs = chbs;
		((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return result.size();
	}

	@Override
	public Object getItem(int position) {
		return result.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.currency_choose_item, parent, false);
		TextView tvChooseAbbr = (TextView) view.findViewById(R.id.tvCurrencyChooseSign);
		tvChooseAbbr.setText(result.get(position).getAbbr());
		CurrencyChecbox chbChoose = (CurrencyChecbox) view.findViewById(R.id.chbCurrencyChoose);
		for (Currency currency : daoSession.getCurrencyDao().loadAll()) {
			if (result.get(position).getId().matches(currency.getId())) {
				chbChoose.setChecked(true);
				break;
			}
		}
		chbChoose.setChecked(chbs[position]);
		chbChoose.setOnCheckListener(new CurrencyChecbox.OnCheckListener() {

			@Override
			public void onCheck(boolean isChecked) {
				chbs[position] = isChecked;
			}
		});
		return view;
	}
}
