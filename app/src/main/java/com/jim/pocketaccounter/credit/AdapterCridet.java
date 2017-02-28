package com.jim.pocketaccounter.credit;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.fragments.AddCreditFragment;
import com.jim.pocketaccounter.fragments.CreditFragment;
import com.jim.pocketaccounter.fragments.CreditTabLay;
import com.jim.pocketaccounter.fragments.InfoCreditFragment;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.cache.DataCache;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by developer on 02.06.2016.
 */

public class AdapterCridet extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    CommonOperations commonOperations;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    @Inject
    LogicManager logicManager;
    @Inject
    DataCache dataCache;
    WarningDialog warningDialog;
    CreditDetialsDao creditDetialsDao;
    AccountDao accountDao;
    private CreditTabLay creditTabLay;
    @Inject
    @Named(value = "display_formatter")
    SimpleDateFormat dateFormat;

    List<CreditDetials> cardDetials;
    Context context;
    ArrayList<Account> accaunt_AC;
    DecimalFormat formater;
    long forDay = 1000L * 60L * 60L * 24L;
    long forMoth = 1000L * 60L * 60L * 24L * 30L;
    long forYear = 1000L * 60L * 60L * 24L * 365L;
    final static long forWeek = 1000L * 60L * 60L * 24L * 7L;
    public void updateList(){
        this.cardDetials=creditDetialsDao.queryBuilder()
                .where(CreditDetialsDao.Properties.Key_for_archive.eq(false)).orderDesc(CreditDetialsDao.Properties.MyCredit_id).build().list();
    }
    public AdapterCridet(Context This ) {
        ((PocketAccounter) This).component((PocketAccounterApplication) This.getApplicationContext()).inject(this);
        warningDialog = new WarningDialog(This);
        creditDetialsDao = daoSession.getCreditDetialsDao();
        accountDao = daoSession.getAccountDao();
        this.cardDetials=creditDetialsDao.queryBuilder()
                .where(CreditDetialsDao.Properties.Key_for_archive.eq(false)).orderDesc(CreditDetialsDao.Properties.MyCredit_id).build().list();
        this.context = This;
        formater = new DecimalFormat("0.##");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holdeer, final int position) {
        if (holdeer instanceof Fornull) {
            return;
        }

        final myViewHolder holder = (myViewHolder) holdeer;
        final CreditDetials itemCr = cardDetials.get(position);
        Log.d("sizeee", itemCr.getReckings().size()+"");
        holder.credit_procent.setText(parseToWithoutNull(itemCr.getProcent()) + "%");
        holder.total_value.setText(parseToWithoutNull(itemCr.getValue_of_credit_with_procent()) + itemCr.getValyute_currency().getAbbr());
        double total_paid = 0;
        for (ReckingCredit item : itemCr.getReckings())
            total_paid += item.getAmount();
        holder.total_paid.setText(parseToWithoutNull(total_paid) + itemCr.getValyute_currency().getAbbr());
        holder.nameCredit.setText(itemCr.getCredit_name());
        Date AAa = (new Date());
        AAa.setTime(itemCr.getTake_time().getTimeInMillis());
        holder.taken_credit_date.setText(dateFormat.format(AAa));
        int resId = context.getResources().getIdentifier(itemCr.getIcon_ID(), "drawable", context.getPackageName());
        holder.iconn.setImageResource(resId);

        Calendar to = (Calendar) itemCr.getTake_time().clone();
        long period_tip = itemCr.getPeriod_time_tip();
        long period_voqt = itemCr.getPeriod_time();
        int voqt_soni = (int) (period_voqt / period_tip);
        if (period_tip == forDay) {
            to.add(Calendar.DAY_OF_YEAR, (int) voqt_soni);
        } else if (period_tip == forWeek) {
            to.add(Calendar.WEEK_OF_YEAR, (int) voqt_soni);
        } else if (period_tip == forMoth) {
            to.add(Calendar.MONTH, (int) voqt_soni);
        } else {
            to.add(Calendar.YEAR, (int) voqt_soni);
        }

        Date from = new Date();
        int t[] = getDateDifferenceInDDMMYYYY(from, to.getTime());
        if (t[0] * t[1] * t[2] < 0 && (t[0] + t[1] + t[2]) != 0) {
            holder.left_date.setText(R.string.ends);
            holder.left_date.setTextColor(Color.parseColor("#832e1c"));
        } else {
            String left_date_string = "";
            if (t[0] != 0) {
                if (t[0] > 1) {
                    left_date_string += Integer.toString(t[0]) + " " + context.getString(R.string.years);
                } else {
                    left_date_string += Integer.toString(t[0]) + " " + context.getString(R.string.year);
                }
            }
            if (t[1] != 0) {
                if (!left_date_string.matches("")) {
                    left_date_string += " ";
                }
                if (t[1] > 1) {
                    left_date_string += Integer.toString(t[1]) + " " + context.getString(R.string.moths);
                } else {
                    left_date_string += Integer.toString(t[1]) + " " + context.getString(R.string.moth);
                }
            }
            if (t[2] != 0) {
                if (!left_date_string.matches("")) {
                    left_date_string += " ";
                }
                if (t[2] > 1) {
                    left_date_string += Integer.toString(t[2]) + " " + context.getString(R.string.days);
                } else {
                    left_date_string += Integer.toString(t[2]) + " " + context.getString(R.string.day);
                }
            }
            holder.left_date.setText(left_date_string);
        }

        double template= itemCr.getValue_of_credit_with_procent()/100;
        int procet=(int) (total_paid/template);
        holder.frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, procet));

        if (itemCr.getValue_of_credit_with_procent() - total_paid <= 0) {
            holder.overall_amount.setText(context.getString(R.string.repaid));
            holder.pay_or_archive.setText(R.string.archive);
        } else {
            holder.pay_or_archive.setText(R.string.pay);
            holder.overall_amount.setText(parseToWithoutNull(itemCr.getValue_of_credit_with_procent() - total_paid) + itemCr.getValyute_currency().getAbbr());
        }
        holder.glav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoCreditFragment temp = new InfoCreditFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(CreditTabLay.CREDIT_ID,itemCr.getMyCredit_id());
                temp.setArguments(bundle);
                paFragmentManager.displayFragment(temp);
            }
        });
        holder.pay_or_archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toArcive = context.getString(R.string.archive).matches(holder.pay_or_archive.getText().toString());
                int pos = cardDetials.indexOf(itemCr);
                if (toArcive) {
                    CreditDetials toArc = cardDetials.get(position);
                    toArc.setKey_for_archive(true);
                    logicManager.insertCredit(toArc);
                    cardDetials.set(position,toArc);
                    notifyItemChanged(position);

                    List<BoardButton> boardButtons=daoSession.getBoardButtonDao().loadAll();
                    for(BoardButton boardButton:boardButtons){
                        if(boardButton.getCategoryId()!=null)
                            if(boardButton.getCategoryId().equals(Long.toString(cardDetials.get(position).getMyCredit_id()))){
                                if(boardButton.getTable()== PocketAccounterGeneral.EXPENSE)
                                    logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE,boardButton.getPos(),null);
                                else
                                    logicManager.changeBoardButton(PocketAccounterGeneral.INCOME,boardButton.getPos(),null);
                                commonOperations.changeIconToNull(boardButton.getPos(),dataCache,boardButton.getTable());

                            }
                    }
                    if(creditTabLay==null) {
                        for (Fragment fragment : paFragmentManager.getFragmentManager().getFragments()){
                            if (fragment == null) continue;
                            if (fragment.getClass().getName().equals(CreditTabLay.class.getName())){
                                creditTabLay = (CreditTabLay) fragment;
                                break;
                            }
                        }
                    }
                    dataCache.updateAllPercents();
                    paFragmentManager.updateAllFragmentsOnViewPager();
                    creditTabLay.updateArchive();
                } else
                    openDialog(itemCr, position);
            }
        });
    }

    public static int[] getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year, month, day;
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment = fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }
        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

        year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);
        return new int[]{year, month, day};
    }

    @Override
    public int getItemCount() {
        return cardDetials.size();
    }

    final static int VIEW_NULL = 0;
    final static int VIEW_NOT_NULL = 1;

    @Override
    public int getItemViewType(int position) {
        return cardDetials.get(position).getKey_for_archive() ? VIEW_NULL : VIEW_NOT_NULL;
    }

    public String parseToWithoutNull(double A) {
        if (A == (int) A) {
            return Integer.toString((int) A);
        } else
            return formater.format(A);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Razvetleniya na dve view. odin pustoy odin realniy
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_NULL) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.null_lay, parent, false);
            vh = new Fornull(v);
        } else if (viewType == VIEW_NOT_NULL) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.moder_titem, parent, false);
            vh = new myViewHolder(v);
        }
        return vh;
    }

    public static class Fornull extends RecyclerView.ViewHolder {
        public Fornull(View v) {
            super(v);
        }
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView credit_procent;
        TextView total_value;
        TextView total_paid;
        TextView taken_credit_date;
        TextView left_date;
        TextView overall_amount;
        TextView pay_or_archive;
        TextView nameCredit;
        View glav;
        ImageView iconn;
        FrameLayout frameLayout;
        public myViewHolder(View v) {
            super(v);
            credit_procent = (TextView) v.findViewById(R.id.procent_of_credit);
            total_value = (TextView) v.findViewById(R.id.total_value);
            total_paid = (TextView) v.findViewById(R.id.totalpayd);
            taken_credit_date = (TextView) v.findViewById(R.id.date_start);
            left_date = (TextView) v.findViewById(R.id.left_date);
            overall_amount = (TextView) v.findViewById(R.id.overallpay);
            pay_or_archive = (TextView) v.findViewById(R.id.pay);
            nameCredit = (TextView) v.findViewById(R.id.NameCr);
            iconn = (ImageView) v.findViewById(R.id.iconaaa);
            frameLayout = (FrameLayout) v.findViewById(R.id.zapolnit);
            glav = v;
        }
    }


    private void openDialog(final CreditDetials current, final int position) {
        final Dialog dialog = new Dialog(context);
        final View dialogView = ((PocketAccounter) context).getLayoutInflater().inflate(R.layout.add_pay_debt_borrow_info_mod, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        final EditText enterDate = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowDate);
        final EditText enterPay = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPaySumm);
        final EditText comment = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPayComment);
        final Spinner accountSp = (Spinner) dialogView.findViewById(R.id.spInfoDebtBorrowAccount);
        if (current.getKey_for_include()) {
            accaunt_AC = (ArrayList<Account>) accountDao.queryBuilder().list();
            String[] accaounts = new String[accaunt_AC.size()];
            for (int i = 0; i < accaounts.length; i++) {
                accaounts[i] = accaunt_AC.get(i).getName();
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    context, R.layout.spiner_gravity_right, accaounts);
            accountSp.setAdapter(arrayAdapter);
        } else {
            dialogView.findViewById(R.id.is_calc).setVisibility(View.GONE);
        }
        final Calendar date = Calendar.getInstance();
        enterDate.setText(dateFormat.format(date.getTime()));
        ImageView cancel = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowCancel);
        final ImageView save = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowSave);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final DatePickerDialog.OnDateSetListener getDatesetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (current.getTake_time().getTimeInMillis() >= (new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTimeInMillis()) {
                    enterDate.setError(context.getString(R.string.incorrect_date));
                    enterDate.setText(dateFormat.format(current.getTake_time().getTime()));
                }
                enterDate.setText(dateFormat.format((new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime()));
                date.set(year, monthOfYear, dayOfMonth);
            }
        };

        enterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                Dialog mDialog = new DatePickerDialog(context,
                        getDatesetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                mDialog.show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String amount = enterPay.getText().toString();
                double total_paid = 0;
                for (ReckingCredit item : current.getReckings())
                    total_paid += item.getAmount();

                if (!amount.matches("")) {
                    if(current.getKey_for_include()){
                        Account account = accaunt_AC.get(accountSp.getSelectedItemPosition());


                        int state = logicManager.isItPosibleToAdd(account,Double.parseDouble(amount.replace(",",".")),current.getValyute_currency(),date,0,null,null);
                        if(state == LogicManager.CAN_NOT_NEGATIVE){

                            Toast.makeText(context, R.string.none_minus_account_warning, Toast.LENGTH_SHORT).show();
                            return;

                        }
                        else if(state == LogicManager.LIMIT){
                            Toast.makeText(context, R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                            return;

                        }
                    }
                    if (Double.parseDouble(amount.replace(",",".")) > current.getValue_of_credit_with_procent() - total_paid) {
                        warningDialog.setOnYesButtonListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String amount = enterPay.getText().toString();
                                ReckingCredit rec = null;
                                if (!amount.matches("") && current.getKey_for_include())
                                    rec = new ReckingCredit(date, Double.parseDouble(amount.replace(",",".")), accaunt_AC.get(accountSp.getSelectedItemPosition()).getId(), current.getMyCredit_id(), comment.getText().toString());
                                else
                                    rec = new ReckingCredit(date, Double.parseDouble(amount.replace(",",".")), "", current.getMyCredit_id(), comment.getText().toString());
                                int pos = cardDetials.indexOf(current);
                                logicManager.insertReckingCredit(rec);
                                current.resetReckings();
                                dataCache.updateAllPercents();
                                paFragmentManager.updateAllFragmentsOnViewPager();
                                notifyItemChanged(position);
                                dialog.dismiss();
                                warningDialog.dismiss();
                            }
                        });
                        warningDialog.setOnNoButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningDialog.dismiss();
                            }
                        });
                        warningDialog.setText(context.getString(R.string.payment_balans) + parseToWithoutNull(current.getValue_of_credit_with_procent() - total_paid) +
                                current.getValyute_currency().getAbbr() + "." + context.getString(R.string.payment_balance2) +
                                parseToWithoutNull(Double.parseDouble(amount.replace(",",".")) - (current.getValue_of_credit_with_procent() - total_paid)) +
                                current.getValyute_currency().getAbbr());
                        warningDialog.show();
                    } else {
                        ReckingCredit rec = null;
                        if (!amount.matches("") && current.getKey_for_include())
                            rec = new ReckingCredit(date, Double.parseDouble(amount.replace(",",".")), accaunt_AC.get(accountSp.getSelectedItemPosition()).getId(), current.getMyCredit_id(), comment.getText().toString());
                        else
                            rec = new ReckingCredit(date, Double.parseDouble(amount.replace(",",".")), "", current.getMyCredit_id(), comment.getText().toString());
                        int pos = cardDetials.indexOf(current);
                        logicManager.insertReckingCredit(rec);
                        current.resetReckings();
                        dataCache.updateAllPercents();
                        paFragmentManager.updateAllFragmentsOnViewPager();
                        notifyItemChanged(pos);
                        dialog.dismiss();
                    }
                }

            }
        });
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(7 * width / 8, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
