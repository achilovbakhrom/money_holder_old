package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.utils.record.IconWithName;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewHolder")
public class CategoryAdapterForDialog extends BaseAdapter {
	private List<IconWithName> result;
	private LayoutInflater inflater;
	private Context context;
	public CategoryAdapterForDialog(Context context, List<IconWithName> result) {
	    this.result = result;
		this.context = context;
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
		View view = inflater.inflate(R.layout.icon_with_name, parent, false);
		ImageView ivCategoryListIcon = (ImageView) view.findViewById(R.id.ivIconWithName);
		int resId = context.getResources().getIdentifier(result.get(position).getIcon(), "drawable", context.getPackageName());
		ivCategoryListIcon.setImageResource(resId);
		TextView tvCategoryListName = (TextView) view.findViewById(R.id.tvIconWithName);
		tvCategoryListName.setText(result.get(position).getName());
		return view;
	}
}