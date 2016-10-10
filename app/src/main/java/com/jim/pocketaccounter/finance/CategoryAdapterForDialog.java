package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.utils.record.IconWithName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
		if (result.get(position).getIcon().startsWith("icon")) {
			int resId = context.getResources().getIdentifier(result.get(position).getIcon(), "drawable", context.getPackageName());
			ivCategoryListIcon.setImageResource(resId);
		}
		else {
			Bitmap bitmap = decodeFile(new File(result.get(0).getIcon()));
			bitmap = Bitmap.createScaledBitmap(bitmap, (int) context.getResources().getDimension(R.dimen.twentyfive_dp),
					(int) context.getResources().getDimension(R.dimen.twentyfive_dp), false);
			ivCategoryListIcon.setImageBitmap(bitmap);
		}
		TextView tvCategoryListName = (TextView) view.findViewById(R.id.tvIconWithName);
		tvCategoryListName.setText(result.get(position).getName());
		return view;
	}
	private Bitmap decodeFile(File f) {
		try {
//            Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//            The new size we want to scale to
			final int REQUIRED_SIZE = 128;
//            Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;
			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
}