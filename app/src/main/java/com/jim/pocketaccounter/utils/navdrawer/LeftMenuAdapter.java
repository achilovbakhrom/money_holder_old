package com.jim.pocketaccounter.utils.navdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;

import java.util.ArrayList;
import java.util.List;

public class LeftMenuAdapter extends BaseAdapter {
	private List<LeftMenuItem> result;
	private LayoutInflater inflater;
	public LeftMenuAdapter(Context context, List<LeftMenuItem> result) {
	    this.result = result;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
    	if (result.get(position).isGroup()) {
    		view = inflater.inflate(R.layout.title_item, parent, false);
    		((ImageView)view.findViewById(R.id.ivTitleIcon)).setImageResource(result.get(position).getIconId());
    		((TextView)view.findViewById(R.id.tvTitleName)).setText(result.get(position).getTitleName());
    	}
    	else {
    		view = inflater.inflate(R.layout.leftmenu_sub_item, parent, false);
    		((ImageView)view.findViewById(R.id.ivSubItemIcon)).setImageResource(result.get(position).getIconId());
    		((TextView)view.findViewById(R.id.tvSubItemName)).setText(result.get(position).getTitleName());
    	}
		return view;
	}

}
