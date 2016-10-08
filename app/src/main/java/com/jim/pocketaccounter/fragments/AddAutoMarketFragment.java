package com.jim.pocketaccounter.fragments;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.jim.pocketaccounter.database.RootCategoryDao;
import com.jim.pocketaccounter.database.SubCategory;
import com.jim.pocketaccounter.finance.IconAdapterCategory;
import com.jim.pocketaccounter.finance.RecordCategoryAdapter;
import com.jim.pocketaccounter.finance.RecordSubCategoryAdapter;
import com.jim.pocketaccounter.finance.TransferAccountAdapter;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.LogicManagerConstants;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.OnSubcategorySavingListener;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.SubCatAddEditDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 9/15/16.
 */
public class AddAutoMarketFragment extends Fragment {
    @Inject
    DaoSession daoSession;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    LogicManager logicManager;
    @Inject
    SubCatAddEditDialog subCatAddEditDialog;

    private AccountDao accountDao;
    private CurrencyDao currencyDao;

    private AutoMarketDao autoMarketDao;
    private EditText amount;
    private Spinner spCurrency,account_sp;
    private ImageView ivCategory;
    private TextView categoryName;
    private TextView subCategoryName;

    private int selectCategory = -1;
    private int selectSubCategory = -1;
    private AutoMarket autoMarket;
    private boolean type = false;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Account> accounts;
    String[] accs;
    private RecyclerView rvDays;
    private DaysAdapter daysAdapter;
    private RadioGroup radioGroup;

    public AddAutoMarketFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        accountDao = daoSession.getAccountDao();
        currencyDao = daoSession.getCurrencyDao();
        autoMarketDao = daoSession.getAutoMarketDao();
        try {
            this.autoMarket = autoMarketDao.load(null == getArguments().getString("key") ? "" : getArguments().getString("key"));
        } catch (NullPointerException e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_auto_market_layout_modern, container, false);
        amount = (EditText) rootView.findViewById(R.id.etAddAutoMarketAmount);
        ivCategory = (ImageView) rootView.findViewById(R.id.ivAddAutoMarketCategory);
        spCurrency = (Spinner) rootView.findViewById(R.id.spAddAutoMarketCurrency);
        account_sp = (Spinner) rootView.findViewById(R.id.acountSpinner);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.rgMonthWeek);
        categoryName = (TextView) rootView.findViewById(R.id.tvAddAutoMarketCatName);
        subCategoryName = (TextView) rootView.findViewById(R.id.tvAddAutoMarketSubCatName);
        rvDays = (RecyclerView) rootView.findViewById(R.id.rvAddAutoMarketPerItems);

        final List<String> curs = new ArrayList<>();
        for (Currency cr : currencyDao.loadAll()) {
            curs.add(cr.getAbbr());
        }

        accounts = (ArrayList<Account>) accountDao.queryBuilder().list();
        accs = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            accs[i] = accounts.get(i).getName();
        }
        ArrayAdapter<String> adapter_scet = new ArrayAdapter<String>(getActivity(),
                R.layout.spiner_gravity_right, accs);
        ArrayAdapter<String> curAdapter = new ArrayAdapter<String>(getActivity()
                , R.layout.spiner_gravity_right, curs);



        account_sp.setAdapter(adapter_scet);
        spCurrency.setAdapter(curAdapter);

//        dialog = new Dialog(getActivity());
//        dialogMonth = new Dialog(getActivity());
//
//        List<String> acNames = new ArrayList<>();
//        for (Account ac : accountDao.loadAll()) {
//            acNames.add(ac.getId());
//        }
//
//        accountAdapter = new TransferAccountAdapter(getContext(), acNames);
//        spAccount.setAdapter(accountAdapter);
//
//        final List<String> curs = new ArrayList<>();
//        for (Currency cr : currencyDao.loadAll()) {
//            curs.add(cr.getAbbr());
//        }
//        ArrayAdapter<String> curAdapter = new ArrayAdapter<String>(getContext()
//                , android.R.layout.simple_list_item_1, curs);
//        spCurrency.setAdapter(curAdapter);
//        spCurrency.setOnItemSelectedListener(this);
//        spAccount.setOnItemSelectedListener(this);
//        btnMonth.setEnabled(false);
//        rbWeek.setChecked(true);
//
//        ivSubCategory.setImageResource(getResources().getIdentifier(daoSession.getRootCategoryDao().loadAll()
//                .get(selectCategory).getSubCategories().get(selectSubCategory).getIcon(), "drawable", getActivity().getPackageName()));
//
//        ivCategory.setImageResource(getResources().getIdentifier(daoSession.getRootCategoryDao().loadAll()
//                .get(selectCategory).getIcon(), "drawable", getActivity().getPackageName()));
//
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rbAutoMarketAddWeek) {
//                    btnMonth.setEnabled(false);
//                    btnWeek.setEnabled(true);
//                } else {
//                    btnMonth.setEnabled(true);
//                    btnWeek.setEnabled(false);
//                }
//            }
//        });
//
//        dialog = new Dialog(getActivity());
//        btnWeek.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openNotifSettingDialog();
//            }
//        });

//        btnMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_market_month_layout, null);
//                dialogMonth.setContentView(dialogView);
//                rvDialogMonth = (RecyclerView) dialogView.findViewById(R.id.rvDialogMarketMonth);
//                RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
//                rvDialogMonth.setLayoutManager(layoutManager);
//                final DaysAdapter monthAdapter = new DaysAdapter();
//                rvDialogMonth.setAdapter(monthAdapter);
//                dialogView.findViewById(R.id.ivCloseDialog).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialogMonth.dismiss();
//                    }
//                });
//                dialogView.findViewById(R.id.ivSaveDialog).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        boolean[] tek = monthAdapter.getResult();
//                        sequence = "";
//                        for (int i = 0; i < tek.length; i++) {
//                            if (tek[i]) {
//                                sequence = sequence + (i + 1) + ",";
//                            }
//                        }
//                        type = true;
//                        dialogMonth.dismiss();
//                    }
//                });
//                dialogMonth.show();
//            }
//        });

        List<String> acNames = new ArrayList<>();
        for (Account ac : accountDao.loadAll()) {
            acNames.add(ac.getId());
        }

        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setSpinnerVisibility(View.GONE);
        toolbarManager.setTitle("Adding");
        toolbarManager.setImageToSecondImage(R.drawable.check_sign);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daysAdapter.getResult();
                if (amount.getText().toString().isEmpty()) {
                    amount.setError("Enter amount");
                } else if (sequence.isEmpty()) {
                    amount.setError(null);
                    Toast.makeText(getContext(), "Choose dates", Toast.LENGTH_SHORT).show();
                } else if (autoMarket != null) {
                    amount.setError(null);
                    if (category_item != null && subCategory !=null) {
                        autoMarket.setRootCategory(category_item);
                        autoMarket.setSubCategory(subCategory);
                    }
                    autoMarket.setAmount(Double.parseDouble(amount.getText().toString()));
                    autoMarket.setCurrency(currencyDao.queryBuilder().where(CurrencyDao.Properties.Abbr.eq(curs.get(spCurrency.getSelectedItemPosition()))).list().get(0));
                    autoMarket.setAccount(accountDao.loadAll().get(account_sp.getSelectedItemPosition()));
                    autoMarket.setType(type);
                    autoMarket.setDates(sequence.substring(0, sequence.length() - 1));
                    daoSession.getAutoMarketDao().insertOrReplace(autoMarket);
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(new AutoMarketFragment());
                } else {
                    amount.setError(null);
                    AutoMarket autoMarket = new AutoMarket();
                    autoMarket.__setDaoSession(daoSession);
                    autoMarket.setAmount(Double.parseDouble(amount.getText().toString()));

                    autoMarket.setRootCategory(category_item);
                    autoMarket.setSubCategory(subCategory);

                    autoMarket.setCurrency(currencyDao.queryBuilder().where(CurrencyDao.Properties.Abbr.eq(curs.get(spCurrency.getSelectedItemPosition()))).list().get(0));
                    autoMarket.setAccount(accountDao.loadAll().get(account_sp.getSelectedItemPosition()));
                    autoMarket.setType(type);
                    autoMarket.__setDaoSession(daoSession);
                    autoMarket.setDates(sequence.substring(0, sequence.length() - 1));
                    switch (logicManager.insertAutoMarket(autoMarket)) {
                        case LogicManagerConstants.SUCH_NAME_ALREADY_EXISTS: {
                            Toast.makeText(getContext(), "this have category market", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case LogicManagerConstants.SAVED_SUCCESSFULL: {
                            paFragmentManager.getFragmentManager().popBackStack();
                            paFragmentManager.displayFragment(new AutoMarketFragment());
                            break;
                        }
                    }
                }
            }
        });

        ivCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                final Dialog dialog = new Dialog(getContext());
//                final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_category_auto_market, null);
//                final ImageView catImage = (ImageView) dialogView.findViewById(R.id.ivDialogAutoItemCat);
//
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                final LinearLayout linearLayout = (LinearLayout) dialogView.findViewById(R.id.llDialogAddAutoMarket);
//                linearLayout.setVisibility(View.GONE);
//                final TextView catName = (TextView) dialogView.findViewById(R.id.tvAddAutoMarketCatName);
//
//                dialog.setContentView(dialogView);
//                final GridView gridView = (GridView) dialogView.findViewById(R.id.gvDialogAddAutoMarket);
//
//                final String[] iconsCategory = new String[daoSession.getRootCategoryDao().loadAll().size()];
//                for (int i = 0; i < iconsCategory.length; i++) {
//                    iconsCategory[i] = daoSession.getRootCategoryDao().loadAll().get(i).getIcon();
//                }
//                adapter = new IconAdapterCategory(getContext(), iconsCategory, iconsCategory[0]);
//                gridView.setAdapter(adapter);
//
//                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        if (!catSelected) {
//                            selectCategory = position;
//                            linearLayout.setVisibility(View.VISIBLE);
//                            ivCategory.setImageResource(getResources().getIdentifier(iconsCategory[position], "drawable", getActivity().getPackageName()));
//                            catImage.setImageResource(getResources().getIdentifier(iconsCategory[position], "drawable", getActivity().getPackageName()));
//                            catName.setText(daoSession.getRootCategoryDao().loadAll().get(selectCategory).getName());
//
//                            String[] subString = new String[daoSession.getRootCategoryDao().loadAll().get(selectCategory).getSubCategories().size()];
//                            for (int i = 0; i < subString.length; i++) {
//                                subString[i] = daoSession.getRootCategoryDao().loadAll().get(selectCategory).getSubCategories().get(i).getIcon();
//                            }
//
//                            adapter = new IconAdapterCategory(getContext(), subString, subString[0]);
//                            catSelected = true;
//                        } else {
//                            selectSubCategory = position;
//                            catSelected = false;
//                            categoryName.setText(daoSession.getRootCategoryDao().loadAll().get(selectCategory).getName());
//                            subCategoryName.setText(daoSession.getRootCategoryDao().loadAll().get(selectCategory).getSubCategories().get(selectSubCategory).getName());
//                            dialog.dismiss();
//                        }
//                        gridView.setAdapter(adapter);
//                    }
//                });
//
//                Button cancel = (Button) dialogView.findViewById(R.id.btnDialogAddAutoMarketCancel);
//                Button ok = (Button) dialogView.findViewById(R.id.btnDialogAddAutoMarketOk);
//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (catSelected) {
//                            dialog.dismiss();
//                        }
//                    }
//                });
//
//                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//                int width = dm.widthPixels;
//                getActivity().getWindow().setLayout(width, ActionBar.LayoutParams.MATCH_PARENT);
//
//                dialog.show();
//            }

                final Dialog dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
                String expanse = getResources().getString(R.string.expanse);
                String income = getResources().getString(R.string.income);
                String[] items = new String[2];
                items[0] = expanse;
                items[1] = income;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
                lvCategoryChoose.setAdapter(adapter);
                lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList<RootCategory> categories = new ArrayList<>();
                        List<RootCategory> categoryList = daoSession.getRootCategoryDao().loadAll();
                        if (position == 0) {
                            for (int i = 0; i < categoryList.size(); i++) {
                                if (categoryList.get(i).getType() == PocketAccounterGeneral.EXPENSE)
                                    categories.add(categoryList.get(i));
                            }
                        } else {
                            for (int i = 0; i < categoryList.size(); i++) {
                                if (categoryList.get(i).getType() == PocketAccounterGeneral.INCOME)
                                    categories.add(categoryList.get(i));
                            }
                        }
                        dialog.dismiss();
                        openCategoryDialog(categories);
                    }
                });
                dialog.show();

//
//                final Dialog dialog = new Dialog(getContext());
//                final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_category_auto_market, null);
//                final ImageView catImage = (ImageView) dialogView.findViewById(R.id.ivDialogAutoItemCat);
//
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                final LinearLayout linearLayout = (LinearLayout) dialogView.findViewById(R.id.llDialogAddAutoMarket);
//                linearLayout.setVisibility(View.GONE);
//                final TextView catName = (TextView) dialogView.findViewById(R.id.tvAddAutoMarketCatName);
//
//                dialog.setContentView(dialogView);
//                final GridView gridView = (GridView) dialogView.findViewById(R.id.gvDialogAddAutoMarket);
//
//                final String[] iconsCategory = new String[daoSession.getRootCategoryDao().loadAll().size()];
//                for (int i = 0; i < iconsCategory.length; i++) {
//                    iconsCategory[i] = daoSession.getRootCategoryDao().loadAll().get(i).getIcon();
//                }
//                adapter = new IconAdapterCategory(getContext(), iconsCategory, iconsCategory[0]);
//                gridView.setAdapter(adapter);
//
//                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        if (!catSelected) {
//                            selectCategory = position;
//                            linearLayout.setVisibility(View.VISIBLE);
//                            ivCategory.setImageResource(getResources().getIdentifier(iconsCategory[position], "drawable", getActivity().getPackageName()));
//                            catImage.setImageResource(getResources().getIdentifier(iconsCategory[position], "drawable", getActivity().getPackageName()));
//                            catName.setText(daoSession.getRootCategoryDao().loadAll().get(selectCategory).getName());
//
//                            String[] subString = new String[daoSession.getRootCategoryDao().loadAll().get(selectCategory).getSubCategories().size()];
//                            for (int i = 0; i < subString.length; i++) {
//                                subString[i] = daoSession.getRootCategoryDao().loadAll().get(selectCategory).getSubCategories().get(i).getIcon();
//                            }
//
//                            adapter = new IconAdapterCategory(getContext(), subString, subString[0]);
//                            catSelected = true;
//                        } else {
//                            selectSubCategory = position;
//                            catSelected = false;
//                            categoryName.setText(daoSession.getRootCategoryDao().loadAll().get(selectCategory).getName());
//                            subCategoryName.setText(daoSession.getRootCategoryDao().loadAll().get(selectCategory).getSubCategories().get(selectSubCategory).getName());
//                            dialog.dismiss();
//                        }
//                        gridView.setAdapter(adapter);
//                    }
//                });
//
//                Button cancel = (Button) dialogView.findViewById(R.id.btnDialogAddAutoMarketCancel);
//                Button ok = (Button) dialogView.findViewById(R.id.btnDialogAddAutoMarketOk);
//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (catSelected) {
//                            dialog.dismiss();
//                        }
//                    }
//                });
//
//                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//                int width = dm.widthPixels;
//                getActivity().getWindow().setLayout(width, ActionBar.LayoutParams.MATCH_PARENT);
//
//                dialog.show();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtnAddAutoMarketMonth) {
                    daysAdapter = new DaysAdapter(1);
                    layoutManager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
                } else {
                    daysAdapter = new DaysAdapter(0);
                    layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
                }
                type = !type;
                rvDays.setLayoutManager(layoutManager);
                rvDays.setAdapter(daysAdapter);
            }
        });

        if (autoMarket != null) {
            categoryName.setText(autoMarket.getRootCategory().getName());
            subCategoryName.setText(autoMarket.getSubCategory().getName());
            ivCategory.setImageResource(getResources().getIdentifier(autoMarket.getRootCategory().getIcon(), "drawable", getActivity().getPackageName()));
            amount.setText("" + autoMarket.getAmount());
            type = autoMarket.getType();
            for (int i = 0; i < curs.size(); i++) {
                if (curs.get(i).matches(autoMarket.getCurrency().getAbbr())) {
                    spCurrency.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < acNames.size(); i++) {
                if (acNames.get(i).matches(autoMarket.getAccountId())) {
                    toolbarManager.getSpinner().setSelection(i);
                    break;
                }
            }
            if (autoMarket.getType()) {
                daysAdapter = new DaysAdapter(1);
                layoutManager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
                radioGroup.check(R.id.rbtnAddAutoMarketMonth);
            } else {
                daysAdapter = new DaysAdapter(0);
                layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
                radioGroup.check(R.id.rbtnAddAutoMarketWeek);
            }

        } else {
            daysAdapter = new DaysAdapter(0);
            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
            radioGroup.check(R.id.rbtnAddAutoMarketWeek);
        }
        rvDays.setLayoutManager(layoutManager);
        rvDays.setAdapter(daysAdapter);
        return rootView;
    }

    private IconAdapterCategory adapter;
    private boolean catSelected = false;

    String sequence = "";
    private boolean inc = false;

    RootCategory category_item;
    private void openCategoryDialog(final ArrayList<RootCategory> categories) {
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
        RecordCategoryAdapter adapter = new RecordCategoryAdapter(getContext(), categories);
        lvCategoryChoose.setAdapter(adapter);
        lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category_item = categories.get(position);
                openSubCategoryDialog();
                dialog.dismiss();
            }
        });
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(8 * width / 9, ActionBarOverlayLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }
    SubCategory subCategory;
    private void openSubCategoryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
        final ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
        SubCategory noSubCategory = new SubCategory();
        noSubCategory.setIcon("category_not_selected");
        noSubCategory.setName(getResources().getString(R.string.no_category_name));
        noSubCategory.setId(getResources().getString(R.string.no_category));
        subCategories.add(noSubCategory);
        for (int i = 0; i < category_item.getSubCategories().size(); i++)
            subCategories.add(category_item.getSubCategories().get(i));

        RecordSubCategoryAdapter adapter = new RecordSubCategoryAdapter(getContext(), subCategories);
        lvCategoryChoose.setAdapter(adapter);
        lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (subCategories.get(position) == null) {
                    subCategory=null;
                    ivCategory.setImageResource(getResources().getIdentifier(subCategory.getIcon(), "drawable", getActivity().getPackageName()));
                    categoryName.setText(category_item.getName());
                    subCategoryName.setText((category_item.getType()==PocketAccounterGeneral.INCOME)?"Income category":"Expanse category");

                } else if (subCategories.get(position).getId().matches(getResources().getString(R.string.no_category))){
                    subCategory=null;
                    ivCategory.setImageResource(getResources().getIdentifier(category_item.getIcon(), "drawable", getActivity().getPackageName()));
                    categoryName.setText(category_item.getName());
                    subCategoryName.setText((category_item.getType()==PocketAccounterGeneral.INCOME)?"Income category":"Expanse category");

                }
                else
                if (subCategories.get(position) != null) {
                    subCategory=subCategories.get(position);
                    selectSubCategory = position;
                    categoryName.setText(category_item.getName());
                    subCategoryName.setText(subCategory.getName());
                    ivCategory.setImageResource(getResources().getIdentifier(subCategory.getIcon(), "drawable", getActivity().getPackageName()));

                }
                dialog.dismiss();
            }
        });
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(8 * width / 9, ActionBarOverlayLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }


    private class DaysAdapter extends RecyclerView.Adapter<ViewHolderDialog> {
        private String[] days;
        private boolean tek[];

        public DaysAdapter(int type) {
            sequence = "";
            if (type == 0) {
                days = getResources().getStringArray(R.array.week_day_auto);
            } else {
                days = new String[31];
                for (int i = 0; i < days.length; i++) {
                    days[i] = i < 9 ? " " + (i + 1) : "" + (i + 1);
                }
            }
            tek = new boolean[days.length];
            if (autoMarket != null) {
                String [] dates = autoMarket.getDates().split(",");
                for (int i = 0; i < days.length; i++) {
                    for (String date : dates) {
                        if (days[i].matches(date)) {
                            tek[i] = true;
                            break;
                        }
                    }
                }
            }
        }

        public void getResult() {
            for (int i = 0; i < tek.length; i++) {
                if (tek[i]) {
                    sequence = sequence + days[i] + ",";
                }
            }
        }

        @Override
        public int getItemCount() {
            return days.length;
        }

        public void onBindViewHolder(final ViewHolderDialog view, final int position) {
            if (position % 7 == 0) {
                view.frameLayout.setVisibility(View.GONE);
            }
            view.day.setText(days[position]);
            view.day.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
            if (tek[position]) view.day.setTextColor(ContextCompat.getColor(getContext(), R.color.green_just));
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tek[position]) {
                        view.day.setTextColor(ContextCompat.getColor(getContext(), R.color.green_just));
                    } else {
                        view.day.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                    }
                    tek[position] = !tek[position];
                }
            });
        }

        public ViewHolderDialog onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_month_layout, parent, false);
            return new ViewHolderDialog(view);
        }
    }



    public class ViewHolderDialog extends RecyclerView.ViewHolder {
        public TextView day;
        public FrameLayout frameLayout;
        public View itemView;
        public ViewHolderDialog(View view) {
            super(view);
            itemView  = view;
            day = (TextView) view.findViewById(R.id.tvItemDay);
            frameLayout = (FrameLayout) view.findViewById(R.id.flItemDay);
        }
    }
}