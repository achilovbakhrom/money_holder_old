package com.jim.pocketaccounter.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.SmsParseKeys;
import com.jim.pocketaccounter.database.SmsParseKeysDao;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 9/29/16.
 */

public class SmsParseMainFragment extends Fragment implements View.OnClickListener {
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddSmsParseFragment addSmsParseFragment;
    private FloatingActionButton floatingActionButton;
    private Dialog dialog;
    private MyDialogKeys myDialogKeys;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credit_tab_lay, container, false);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setImageToSecondImage(R.id.addcomment);
        toolbarManager.setOnSecondImageClickListener(this);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fbDebtBorrowFragment);
        floatingActionButton.setOnClickListener(this);
        addSmsParseFragment = new AddSmsParseFragment();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbDebtBorrowFragment: {
                if (daoSession.getSmsParseKeysDao().loadAll().isEmpty()) {
                    showDialogKeys();
                } else {

                }
                break;
            }
            default: {
                showDialogKeys();
                break;
            }
        }
    }

    private void showDialogKeys () {
        dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_sms_key_words, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        final TextView income = (TextView) dialogView.findViewById(R.id.tvDialogSmsParseIncome);
        final TextView expance = (TextView) dialogView.findViewById(R.id.tvDialogSmsParseExpanse);
        final TextView curs = (TextView) dialogView.findViewById(R.id.tvDialogSmsParseCurs);
        Button delete = (Button) dialogView.findViewById(R.id.btnDialogSmsKeysDelete);
        Button add = (Button) dialogView.findViewById(R.id.btnDialogSmsKeyAdd);
        final EditText etAdd = (EditText) dialogView.findViewById(R.id.etDialogSmsParseNew);
        final RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rvDialogSmsParseKeys);
        RecyclerView.LayoutManager lM = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lM);
        myDialogKeys = new MyDialogKeys(0);
        recyclerView.setAdapter(myDialogKeys);
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income.setTextColor(ContextCompat.getColor(getContext(), R.color.green_light_monoxrom));
                expance.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                curs.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                myDialogKeys = new MyDialogKeys(0);
                recyclerView.setAdapter(myDialogKeys);
            }
        });
        expance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                expance.setTextColor(ContextCompat.getColor(getContext(), R.color.green_light_monoxrom));
                curs.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                myDialogKeys = new MyDialogKeys(1);
                recyclerView.setAdapter(myDialogKeys);
            }
        });
        curs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                expance.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
                curs.setTextColor(ContextCompat.getColor(getContext(), R.color.green_light_monoxrom));
                myDialogKeys = new MyDialogKeys(2);
                recyclerView.setAdapter(myDialogKeys);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAdd.getText().toString().isEmpty()) {
                    etAdd.setError("Enter key");
                } else {
                    etAdd.setError(null);
                    myDialogKeys.addkey(etAdd.getText().toString());
                    etAdd.setText("");
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogKeys.deleteWords();
            }
        });
        int width = getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setLayout(8 * width / 10, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private class MyDialogKeys extends RecyclerView.Adapter<SmsParseMainFragment.ViewHolderKeys> {
        private List<SmsParseKeys> smsKeys;
        private boolean tek[];
        private int type;
        private int MODE = 0;

        public MyDialogKeys(int type) {
            this.type = type;
            smsKeys = daoSession.getSmsParseKeysDao().queryBuilder()
                    .where(SmsParseKeysDao.Properties.Type.eq(type)).list();
            tek = new boolean[smsKeys.size()];
            MODE = 0;
        }

        public void addkey(String key) {
            SmsParseKeys smsParseKeys = new SmsParseKeys();
            smsParseKeys.setType(type);
            smsParseKeys.setNameKey(key);
            smsKeys.add(0, smsParseKeys);
            daoSession.getSmsParseKeysDao().insertOrReplace(smsParseKeys);
            notifyItemInserted(0);
        }

        public void deleteWords () {
            if (MODE == 1) {
                for (int i = tek.length - 1; i >= 0; i--) {
                    if (tek[i]) {
                        daoSession.getSmsParseKeysDao().delete(smsKeys.get(i));
                        smsKeys.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            }else {
                MODE = 1;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            return smsKeys.size();
        }

        public int getMODE() {
            return MODE;
        }

        public void setMODE(int MODE) {
            this.MODE = MODE;
        }

        public void onBindViewHolder(final SmsParseMainFragment.ViewHolderKeys view, final int position) {
            view.checkBox.setVisibility(View.GONE);
            if (MODE == 1) {
                view.checkBox.setVisibility(View.VISIBLE);
                view.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.checkBox.setChecked(!tek[position]);
                    }
                });
                view.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tek[position] = !tek[position];
                    }
                });
            }
            view.textView.setText(smsKeys.get(position).getNameKey());
        }

        public SmsParseMainFragment.ViewHolderKeys onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms_keys_layout, parent, false);
            return new SmsParseMainFragment.ViewHolderKeys(view);
        }
    }

    public class ViewHolderKeys extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView textView;

        public ViewHolderKeys(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.chbItemDialogSmsKeys);
            textView = (TextView) view.findViewById(R.id.tvItemDialogSmsKeys);
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter( FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return addSmsParseFragment;
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            return "sms";
        }
    }
}
