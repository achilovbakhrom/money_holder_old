package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.Purpose;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@SuppressLint("ViewHolder")
public class TransferAccountAdapter extends BaseAdapter {
	private List<String> result;
	private LayoutInflater inflater;
	private Context context;
	@Inject
	DaoSession daoSession;
	public TransferAccountAdapter(Context context, List<String> result) {
	    this.result = result;
		this.context = context;
		((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
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
		View view = inflater.inflate(R.layout.transfer_account_item, parent, false);
		ImageView ivTransferItem = (ImageView) view.findViewById(R.id.ivTransferItem);
		TextView tvTransferItem = (TextView) view.findViewById(R.id.tvTransferItem);
		String name = "";
		int resId = 0;
		String id = result.get(position);
		List<Account> accounts = daoSession.getAccountDao().loadAll();
		for (Account account : accounts) {
			if (id.matches(account.getId())) {
				name = account.getName();
				resId = context.getResources().getIdentifier(account.getIcon(), "drawable", context.getPackageName());
				break;
			}
		}
		if (!name.matches("")) {
			List<Purpose> purposes = daoSession.getPurposeDao().loadAll();
			for (Purpose purpose : purposes) {
				if (id.matches(purpose.getId())) {
					name = purpose.getDescription();
					resId = context.getResources().getIdentifier(purpose.getIcon(), "drawable", context.getPackageName());
					break;
				}
			}
		}
		tvTransferItem.setText(name);
		ivTransferItem.setImageResource(resId);
		return view;
	}
}
