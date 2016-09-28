package com.jim.pocketaccounter.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.FloatingActionButton;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;

import javax.inject.Inject;

@SuppressLint("InflateParams")
public class SMSParseFragment extends Fragment {
	@Inject
	DaoSession daoSession;
	@Inject
	PAFragmentManager paFragmentManager;
	@Inject
	ToolbarManager toolbarManager;

	private FloatingActionButton fabSmsParse;
	private RecyclerView rvSmsParseList;
	private boolean[] selected;
	private int mode = PocketAccounterGeneral.NORMAL_MODE;
//	private ImageView ivToolbarMostRight;
	private final int PERMISSION_REQUEST_CONTACT = 5;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
		final View rootView = inflater.inflate(R.layout.sms_parse_layout, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
				}
			}
		},100);

//		((ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
		toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
//		ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
//		ivToolbarMostRight.setImageResource(R.drawable.pencil);
//		ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				setMode(mode);
//			}
//		});

		rvSmsParseList = (RecyclerView) rootView.findViewById(R.id.rvSmsParseList);
		rvSmsParseList.setLayoutManager(new LinearLayoutManager(getContext()));
		fabSmsParse = (FloatingActionButton)  rootView.findViewById(R.id.fabSmsParse);
//		PocketAccounter.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				PocketAccounter.drawer.openLeftSide();
//			}
//		});
//		PocketAccounter.toolbar.setTitle(R.string.sms_parse);
//		PocketAccounter.toolbar.setSubtitle("");
//		PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_white_24dp);
		int size = (int) getResources().getDimension(R.dimen.thirty_dp);
		Bitmap add = Bitmap.createScaledBitmap(temp, size, size, false);
		fabSmsParse.setImageBitmap(add);
		fabSmsParse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				paFragmentManager.displayFragment(new SMSParseEditFragment(null));
			}
		});
		refreshList();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
						Manifest.permission.RECEIVE_SMS)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Contacts access needed");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setMessage("please confirm Contacts access");//TODO put real question
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@TargetApi(Build.VERSION_CODES.M)
						@Override
						public void onDismiss(DialogInterface dialog) {
							requestPermissions(
									new String[]
											{Manifest.permission.RECEIVE_SMS}
									, PERMISSION_REQUEST_CONTACT);
						}
					});
					builder.show();
				} else {
					ActivityCompat.requestPermissions(getActivity(),
							new String[]{Manifest.permission.RECEIVE_SMS},
							PERMISSION_REQUEST_CONTACT);
				}
			}
		}

		return rootView;
	}
	private void refreshList() {
		MyAdapter adapter = new MyAdapter();
		rvSmsParseList.setAdapter(adapter);
	}

	private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
		public MyAdapter() {

		}
		public int getItemCount() {
			return 0;
		}
		public void onBindViewHolder(final ViewHolder view, final int position) {

		}
		public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_object_list_item, parent, false);
			return new ViewHolder(view);
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public CheckBox chbSmsObjectItem;
		public TextView tvSmsParseItemNumber;
		public TextView tvSmsParsingItemInfo;
		public TextView AccoountName;
		public View rootView;
		public LinearLayout forGONE;
		public ViewHolder(View view) {
			super(view);
			chbSmsObjectItem = (CheckBox) view.findViewById(R.id.chbSmsObjectItem);
			tvSmsParseItemNumber = (TextView) view.findViewById(R.id.tvSmsParseItemNumber);
			tvSmsParsingItemInfo = (TextView) view.findViewById(R.id.tvSmsParsingItemInfo);
			AccoountName = (TextView) view.findViewById(R.id.tvaccount);
			forGONE = (LinearLayout) view.findViewById(R.id.for_gone);
			rootView = view;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_CONTACT: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				} else {

				}
				return;
			}
		}
	}
}
