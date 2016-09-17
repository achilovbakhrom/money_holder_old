package com.jim.pocketaccounter.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.AutoMarket;
import com.jim.pocketaccounter.database.AutoMarketDao;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.CurrencyDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.finance.IconAdapterAccount;
import com.jim.pocketaccounter.finance.TransferAccountAdapter;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.IconChooseDialog;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import javax.inject.Inject;

/**
 * Created by root on 9/15/16.
 */
public class AddAutoMarketFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    LogicManager logicManager;
    private AccountDao accountDao;
    private CurrencyDao currencyDao;

    private AutoMarketDao autoMarketDao;
    private EditText amount;
    private Spinner spAccount;
    private Spinner spCurrency;
    private TextView catName;
    private TextView subCatName;
    private ImageView ivCategory;
    private ImageView ivSubCategory;
    private Button btnWeek;
    private Button btnMonth;
    private RadioGroup radioGroup;
    private RadioButton rbWeek;
    private RadioButton rbMonth;

    private RecyclerView recyclerView;
    private TransferAccountAdapter accountAdapter;
    private String selectCategory;
    private String selectSubCategory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        accountDao = daoSession.getAccountDao();
        currencyDao = daoSession.getCurrencyDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_auto_market_layout, container, false);
        amount = (EditText) rootView.findViewById(R.id.etAutoMarketAddAmount);
        spAccount = (Spinner) rootView.findViewById(R.id.spAutoMarketAddAcc);
        ivCategory = (ImageView) rootView.findViewById(R.id.ivAddAutoMarketCategory);
        ivSubCategory = (ImageView) rootView.findViewById(R.id.ivAddAutoMarketSubCategory);
        spCurrency = (Spinner) rootView.findViewById(R.id.spAutoMarketAddCur);
        btnWeek = (Button) rootView.findViewById(R.id.btnAutoMarketAddWeek);
        btnMonth = (Button) rootView.findViewById(R.id.btnAutoMarketAddMonth);
        rbWeek = (RadioButton) rootView.findViewById(R.id.rbAutoMarketAddWeek);
        rbMonth = (RadioButton) rootView.findViewById(R.id.rbAutoMarketAddMonth);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.rgAutoMarketAdd);
        catName = (TextView) rootView.findViewById(R.id.tvAddAutoMarketCatName);
        subCatName = (TextView) rootView.findViewById(R.id.tvAddAutoMarketSubCatName);

        List<String> acNames = new ArrayList<>();
        for (Account ac : accountDao.loadAll()) {
            acNames.add(ac.getName());
        }
        accountAdapter = new TransferAccountAdapter(getContext(), acNames);
        spAccount.setAdapter(accountAdapter);

        List<String> curs = new ArrayList<>();
        for (Currency cr : currencyDao.loadAll()) {
            curs.add(cr.getAbbr());
        }
        ArrayAdapter<String> curAdapter = new ArrayAdapter<String>(getContext()
                , android.R.layout.simple_list_item_1, curs);
        spCurrency.setAdapter(curAdapter);
        spCurrency.setOnItemSelectedListener(this);
        spAccount.setOnItemSelectedListener(this);
        btnMonth.setEnabled(false);
        rbWeek.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbAutoMarketAddWeek) {
                    btnMonth.setEnabled(false);
                    btnWeek.setEnabled(true);
                } else {
                    btnMonth.setEnabled(true);
                    btnWeek.setEnabled(false);
                }
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotifSettingDialog();
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoMarket autoMarket = new AutoMarket();
                autoMarket.setAmount(Double.parseDouble(amount.getText().toString()));

                logicManager.insertAutoMarket(autoMarket);
                paFragmentManager.getFragmentManager().popBackStack();
                paFragmentManager.displayFragment(new AutoMarketFragment());
            }
        });

        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.auto_market_category_layout, null);
        recyclerView = (RecyclerView) dialogView.findViewById(R.id.rvAddAutoMarketCategoryDialog);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        final Dialog dialog = new Dialog(getActivity());

        ivCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new AutoAdapter(0));
                dialog.show();
            }
        });

        ivSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new AutoAdapter(1));
                dialog.show();
            }
        });

        return rootView;
    }

    String sequence;

    private void openNotifSettingDialog() {
        final Dialog dialog = new Dialog(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.notif_settings, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        LinearLayout llNotifSettingBody = (LinearLayout) dialogView.findViewById(R.id.llNotifSettingBody);
        llNotifSettingBody.removeAllViews();
        final Spinner sp = new Spinner(getContext());
        final ArrayList<CheckBox> chbs = new ArrayList<>();
        String[] weekdays = getResources().getStringArray(R.array.week_days);
        for (int i = 0; i < weekdays.length; i++) {
            CheckBox chb = new CheckBox(getContext());
            if (i == 0) chb.setChecked(true);
            chb.setText(weekdays[i]);
            chb.setTextSize(getResources().getDimension(R.dimen.five_dp));
            chb.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar_text_color));
            chb.setPadding(0, 0, (int) getResources().getDimension(R.dimen.ten_sp), 0);
            chbs.add(chb);
            llNotifSettingBody.addView(chb);
        }
        ImageView btnYes = (ImageView) dialogView.findViewById(R.id.ivAccountSave);
        btnYes.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          String text = "";
                                          for (int i = 0; i < chbs.size(); i++) {
                                              if (chbs.get(i).isChecked()) {
                                                  text = text + i + ",";
                                              }
                                          }
                                          if (!text.matches("") && text.endsWith(","))
                                              sequence = text.substring(0, text.length() - 1);

                                          dialog.dismiss();
                                      }
                                  }
        );
        ImageView btnNo = (ImageView) dialogView.findViewById(R.id.ivAccountClose);
        btnNo.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         dialog.dismiss();
                                     }
                                 }
        );
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.spAutoMarketAddAcc: {

                break;
            }
            case R.id.spAutoMarketAddCur: {

                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class AutoAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<RootCategory> list1;
        private List<SubCategory> list2;
        private int type = 0;

        public AutoAdapter(int type) {
            if (type == 0)
                list1 = daoSession.getRootCategoryDao().loadAll();
            else list2 = daoSession.getSubCategoryDao().loadAll();
            this.type = type;

            Toast.makeText(getContext(), "" + daoSession.getRootCategoryDao().loadAll().size()
                    + "\n" + daoSession.getSubCategoryDao().loadAll().size(), Toast.LENGTH_SHORT).show();
        }

        public int getItemCount() {
            if (type == 1) {
                return list2.size();
            }
            return list1.size();
        }

        public void onBindViewHolder(final ViewHolder view, final int position) {
            if (type == 0) {
                view.catName.setText(list1.get(position).getName());
                view.imageView.setImageResource(getResources().getIdentifier(list1.get(position).getIcon(), "drawable", getActivity().getPackageName()));
            } else {
                view.catName.setText(list2.get(position).getName());
                view.imageView.setImageResource(getResources().getIdentifier(list2.get(position).getIcon(), "drawable", getActivity().getPackageName()));
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auto_market_category, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView catName;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.ivItemAutoMarketCategory);
            catName = (TextView) view.findViewById(R.id.tvItemAutoMarketCatName);
        }
    }
}