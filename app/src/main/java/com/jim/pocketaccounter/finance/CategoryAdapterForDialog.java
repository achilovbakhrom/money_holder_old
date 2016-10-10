package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
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
			Bitmap bitmap = queryContactImage(Integer.parseInt(result.get(0).getIcon()));
			if (bitmap == null)
				Drawable.createFromPath(result.get(0).getIcon());
			ivCategoryListIcon.setImageBitmap(bitmap);
		}
		TextView tvCategoryListName = (TextView) view.findViewById(R.id.tvIconWithName);
		tvCategoryListName.setText(result.get(position).getName());
		return view;
	}
	private Bitmap queryContactImage(int imageDataRow) {
		Cursor c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
				ContactsContract.CommonDataKinds.Photo.PHOTO
		}, ContactsContract.Data._ID + "=?", new String[]{
				Integer.toString(imageDataRow)
		}, null);
		byte[] imageBytes = null;
		if (c != null) {
			if (c.moveToFirst()) {
				imageBytes = c.getBlob(0);
			}
			c.close();
		}
		if (imageBytes != null) {
			return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		} else {
			return null;
		}
	}
}