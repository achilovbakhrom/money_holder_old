package com.jim.pocketaccounter.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountOperation;
import com.jim.pocketaccounter.database.AccountOperationDao;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.Purpose;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.debt.InfoDebtBorrowFragment;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.DatePicker;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.OnDatePickListener;
import com.jim.pocketaccounter.utils.OnIconPickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint({"InflateParams", "ValidFragment"})
public class PurposeEditFragment extends Fragment implements OnClickListener, OnItemClickListener {
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

    private String choosenIcon = "icons_1";
    private Purpose purpose;
    private EditText purposeName;
    private FABIcon iconPurpose;
    private EditText amountPurpose;
    private Spinner curPurpose;
    private Spinner periodPurpose;
    private TextView beginDate;
    private TextView endDate;

    private Calendar begCalendar;
    private Calendar endCalendar;

    public PurposeEditFragment(Purpose purpose) {
        this.purpose = purpose;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        begCalendar = (Calendar) Calendar.getInstance().clone();
        endCalendar = (Calendar) Calendar.getInstance().clone();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.purpose_edit_layout, container, false);
        purposeName = (EditText) rootView.findViewById(R.id.etPurposeEditName);
        iconPurpose = (FABIcon) rootView.findViewById(R.id.fabPurposeIcon);
        amountPurpose = (EditText) rootView.findViewById(R.id.etPurposeTotal);
        curPurpose = (Spinner) rootView.findViewById(R.id.spPurposeCurrency);
        periodPurpose = (Spinner) rootView.findViewById(R.id.spPurposePeriod);
        beginDate = (TextView) rootView.findViewById(R.id.tvPurposeBeginDate);
        endDate = (TextView) rootView.findViewById(R.id.tvPurposeEndDate);
        // ------------ Toolbar setting ----------
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setOnSecondImageClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purpose == null)
                    purpose = new Purpose();
                purpose.setDescription(purposeName.getText().toString());
                purpose.setIcon(choosenIcon);
                purpose.setPeriodPos(periodPurpose.getSelectedItemPosition());
                purpose.setPurpose(Double.parseDouble(amountPurpose.getText().toString()));
                purpose.setBegin(begCalendar);
                purpose.setEnd(endCalendar);
                switch (logicManager.insertPurpose(purpose)) {
                    case LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS: {
                        Toast.makeText(getContext(), "such name have", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case LogicManagerConstants.SAVED_SUCCESSFULL: {
                        Toast.makeText(getContext(), "saved success", Toast.LENGTH_SHORT).show();
                        paFragmentManager.getFragmentManager().popBackStack();
                        paFragmentManager.displayFragment(new PurposeFragment());
                        break;
                    }
                }
            }
        });
        // ------------ end toolbar setting ------
        // ------------ icon set ----------
        int resId = getResources().getIdentifier(purpose != null ? purpose.getIcon() : choosenIcon, "drawable", getContext().getPackageName());
        Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
        Bitmap bitmap = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp),
                (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
        iconPurpose.setImageBitmap(bitmap);
        iconPurpose.setOnClickListener(new OnClickListener() {
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
                        iconPurpose.setImageBitmap(b);
                        iconChooseDialog.setSelectedIcon(icon);
                        iconChooseDialog.dismiss();
                    }
                });
                iconChooseDialog.show();
            }
        });
        // ------------ end icon set ---------
        // ------------ spinner currency --------
        CurrencyDao currencyDao = daoSession.getCurrencyDao();
        List<String> curList = new ArrayList<>();
        for (Currency c : currencyDao.queryBuilder().list()) {
            curList.add(c.getAbbr());
        }
        ArrayAdapter<String> curAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, curList);
        curPurpose.setAdapter(curAdapter);
        // ------------ end spinner currency -------
        // ------------ period purpose spinner ------
        String periodList[] = getResources().getStringArray(R.array.period_purpose);
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, periodList);
        periodPurpose.setAdapter(periodAdapter);
        periodPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.purpose_dialog_layout, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                final EditText editText = (EditText) dialogView.findViewById(R.id.etDialogPurpose);
                final EditText editTextSecond = (EditText) dialogView.findViewById(R.id.etDialogPurposeSecond);
                final TextView textView = (TextView) dialogView.findViewById(R.id.tvDialogPurposeTitle);
                begCalendar = (Calendar) Calendar.getInstance().clone();
                endCalendar = (Calendar) Calendar.getInstance().clone();
                switch (position) {
                    case 0: {
                        beginDate.setText("----");
                        endDate.setText("----");
                        begCalendar = null;
                        endCalendar = null;
                        break;
                    }
                    case 1: {
                        textView.setText("Enter count week");
                        editText.setHint("Enter week");
                        editTextSecond.setVisibility(View.GONE);
                        final ImageView save = (ImageView) dialogView.findViewById(R.id.ivDialogPurposeSave);
                        save.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                endCalendar.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(editText.getText().toString()));
                                beginDate.setText(dateFormat.format(begCalendar.getTime()));
                                endDate.setText(dateFormat.format(endCalendar.getTime()));
                            }
                        });
                        dialog.show();
                        break;
                    }
                    case 2: {
                        textView.setText("Enter count month");
                        editText.setHint("Enter month");
                        editTextSecond.setVisibility(View.GONE);
                        final ImageView save = (ImageView) dialogView.findViewById(R.id.ivDialogPurposeSave);
                        save.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                endCalendar.add(Calendar.WEEK_OF_MONTH, Integer.parseInt(editText.getText().toString()));
                                beginDate.setText(dateFormat.format(begCalendar.getTime()));
                                endDate.setText(dateFormat.format(endCalendar.getTime()));
                            }
                        });
                        dialog.show();
                        break;
                    }
                    case 3: {
                        textView.setText("Enter count year");
                        editText.setHint("Enter year");
                        final ImageView save = (ImageView) dialogView.findViewById(R.id.ivDialogPurposeSave);
                        editTextSecond.setVisibility(View.GONE);
                        save.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                endCalendar.add(Calendar.YEAR, Integer.parseInt(editText.getText().toString()));
                                beginDate.setText(dateFormat.format(begCalendar.getTime()));
                                endDate.setText(dateFormat.format(endCalendar.getTime()));
                            }
                        });
                        dialog.show();
                        break;
                    }
                    case 4: {
                        textView.setText("Enter between date");
                        editTextSecond.setVisibility(View.VISIBLE);
                        editText.setHint("Enter start");
                        editTextSecond.setHint("Enter end");
                        editText.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                datePicker.setOnDatePickListener(new OnDatePickListener() {
                                    @Override
                                    public void OnDatePick(Calendar pickedDate) {
                                        begCalendar = pickedDate;
                                        editText.setText(dateFormat.format(begCalendar.getTime()));
                                    }
                                });
                                datePicker.show();
                                return true;
                            }
                        });
                        editTextSecond.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                datePicker.setOnDatePickListener(new OnDatePickListener() {
                                    @Override
                                    public void OnDatePick(Calendar pickedDate) {
                                        endCalendar = pickedDate;
                                        editTextSecond.setText(dateFormat.format(endCalendar.getTime()));
                                    }
                                });
                                datePicker.show();
                                return true;
                            }
                        });
                        final ImageView save = (ImageView) dialogView.findViewById(R.id.ivDialogPurposeSave);
                        save.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                beginDate.setText(dateFormat.format(begCalendar.getTime()));
                                endDate.setText(dateFormat.format(endCalendar.getTime()));
                            }
                        });
                        dialog.show();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // ------------ end period spinner ---------
        if (purpose != null) {
            purposeName.setText(purpose.getDescription());
            amountPurpose.setText("" + purpose.getPurpose());
            begCalendar = purpose.getBegin();
            endCalendar = purpose.getEnd();
            periodPurpose.setSelection(purpose.getPeriodPos());
            beginDate.setText(dateFormat.format(purpose.getBegin().getTime()));
            endDate.setText(dateFormat.format(purpose.getEnd().getTime()));
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        paFragmentManager.getFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {

    }

}

