package com.jim.pocketaccounter.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.jim.pocketaccounter.database.ReckingCreditDao;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.managers.ReportManager;
import com.jim.pocketaccounter.managers.ToolbarManager;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.cache.DataCache;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


public class InfoCreditFragmentForArchive extends Fragment {
    @Inject
    ToolbarManager toolbarManager;
    @Inject
    @Named(value = "display_formatter")
    SimpleDateFormat dateFormat;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    DaoSession daoSession;
    @Inject
    LogicManager logicManager;
    @Inject
    DataCache dataCache;
    @Inject
    SharedPreferences sPref;
    @Inject
    CommonOperations commonOperations;
    @Inject
    ReportManager reportManager;
    private CreditDetialsDao creditDetialsDao;
    private ReckingCreditDao reckingCreditDao;
    private AccountDao accountDao;

    ImageView expandableBut;
    FrameLayout expandablePanel;
    FrameLayout expandableLiniya;
    FrameLayout ifHaveItem;
    RecyclerView tranact_recyc;
    CreditDetials currentCredit;
    TextView myCreditName;
    TextView myLefAmount;
    TextView myProcent;
    TextView myLefDate;
    TextView myPeriodOfCredit;
    TextView myTakedCredTime;
    TextView myTakedValue;
    TextView myReturnValue;
    TextView myTotalPaid;
    TextView calculeted;
    ImageView icon_credit;
    PaysCreditAdapter adapRecyc;
    ArrayList<ReckingCredit> rcList;
    WarningDialog warningDialog;
    final static long forDay=1000L*60L*60L*24L;
    final static long forMoth=1000L*60L*60L*24L*30L;
    final static long forWeek=1000L*60L*60L*24L*7L;
    final static long forYear=1000L*60L*60L*24L*365L;
    final static String CALCULATED="Calculeted";
    final static String NOT_CALCULATED="Not calculeted";
    boolean isExpandOpen=false;
    private Context context;
    TextView myPay,myDelete;
    private DecimalFormat formater =new DecimalFormat("0.##");
    int position;
    int modeOfMain = PocketAccounterGeneral.NO_MODE;
    public InfoCreditFragmentForArchive() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_info_credit_archive, container, false);
        context=getActivity();
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);

        if(getArguments()!=null){
            currentCredit = daoSession.load(CreditDetials.class,getArguments().getLong(CreditTabLay.CREDIT_ID));
            modeOfMain = getArguments().getInt(CreditTabLay.MODE);
            position = getArguments().getInt(CreditTabLay.POSITION);
            if (currentCredit != null){
                sPref.edit().putLong("CREDIT_ID", currentCredit.getMyCredit_id()).apply();
            }
        }
        creditDetialsDao = daoSession.getCreditDetialsDao();
        reckingCreditDao = daoSession.getReckingCreditDao();
        accountDao = daoSession.getAccountDao();
        warningDialog = new WarningDialog(context);
        Date dateForSimpleDate = (new Date());
        expandableBut=(ImageView) V.findViewById(R.id.wlyuzik_opener);
        expandablePanel=(FrameLayout) V.findViewById(R.id.shlyuzik);
        expandableLiniya=(FrameLayout) V.findViewById(R.id.with_wlyuzik);
        ifHaveItem=(FrameLayout) V.findViewById(R.id.ifListHave);
         myCreditName=(TextView) V.findViewById(R.id.name_of_credit);
        myLefAmount=(TextView) V.findViewById(R.id.value_credit_all);
        myProcent=(TextView) V.findViewById(R.id.procentCredInfo);
        myLefDate=(TextView) V.findViewById(R.id.leftDateInfo);
        myPeriodOfCredit=(TextView) V.findViewById(R.id.intervalCreditInfo);
        myTakedCredTime=(TextView) V.findViewById(R.id.takedtimeInfo);
        myTakedValue=(TextView) V.findViewById(R.id.takedValueInfo);
        myReturnValue=(TextView) V.findViewById(R.id.totalReturnValueInfo);
        myTotalPaid=(TextView) V.findViewById(R.id.total_transaction);
        calculeted=(TextView) V.findViewById(R.id.it_is_include_balance);
        tranact_recyc=(RecyclerView) V.findViewById(R.id.recycler_for_transactions);
        icon_credit=(ImageView) V.findViewById(R.id.icon_creditt);
        rcList= (ArrayList<ReckingCredit>) currentCredit.getReckings();
        adapRecyc=new PaysCreditAdapter(rcList);
        myPay=(TextView)  V.findViewById(R.id.paybut);
        myDelete=(TextView)  V.findViewById(R.id.deleterbut);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        tranact_recyc.setLayoutManager(llm);

        tranact_recyc.setAdapter(adapRecyc);
        if(rcList.size()==0){
            ifHaveItem.setVisibility(View.GONE);
        }else{
            ifHaveItem.setVisibility(View.VISIBLE);

        }
            double total_paid=0;
        for(ReckingCredit item:rcList){
            total_paid+=item.getAmount();
        }

        adapRecyc.notifyDataSetChanged();

        toolbarManager.setImageToSecondImage(R.drawable.ic_delete_black);
        toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.VISIBLE);
        toolbarManager.setOnSecondImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentCredit.getKey_for_include()){
                    warningDialog.setOnYesButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (modeOfMain == PocketAccounterGeneral.NO_MODE) {
                                List<BoardButton> boardButtons = daoSession.getBoardButtonDao().loadAll();
                                for (BoardButton boardButton : boardButtons) {
                                    if (boardButton.getCategoryId() != null)
                                        if (boardButton.getCategoryId().equals(Long.toString(currentCredit.getMyCredit_id()))) {
                                            if (boardButton.getTable() == PocketAccounterGeneral.EXPENSE)
                                                logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, boardButton.getPos(), null);
                                            else
                                                logicManager.changeBoardButton(PocketAccounterGeneral.INCOME, boardButton.getPos(), null);
                                            commonOperations.changeIconToNull(boardButton.getPos(), dataCache, boardButton.getTable());

                                        }
                                }
                                for(ReckingCredit reckingCredit:currentCredit.getReckings()){
                                    logicManager.deleteReckingCredit(reckingCredit);
                                }
                                logicManager.deleteCredit(currentCredit);
                                dataCache.updateAllPercents();
                                paFragmentManager.updateAllFragmentsPageChanges();
                            } else if (modeOfMain == PocketAccounterGeneral.SEARCH_MODE) {
                                List<BoardButton> boardButtons = daoSession.getBoardButtonDao().loadAll();
                                for (BoardButton boardButton : boardButtons) {
                                    if (boardButton.getCategoryId() != null)
                                        if (boardButton.getCategoryId().equals(Long.toString(currentCredit.getMyCredit_id()))) {

                                            if (boardButton.getTable() == PocketAccounterGeneral.EXPENSE)
                                                logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, boardButton.getPos(), null);
                                            else
                                                logicManager.changeBoardButton(PocketAccounterGeneral.INCOME, boardButton.getPos(), null);

                                            commonOperations.changeIconToNull(boardButton.getPos(), dataCache, boardButton.getTable());

                                        }
                                }
                                for(ReckingCredit reckingCredit:currentCredit.getReckings()){
                                    logicManager.deleteReckingCredit(reckingCredit);
                                }
                                logicManager.deleteCredit(currentCredit);
                                dataCache.updateAllPercents();
                                paFragmentManager.updateAllFragmentsPageChanges();

                            } else {
                                if (modeOfMain == PocketAccounterGeneral.EXPANSE_MODE) {
                                    logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, position, null);
                                } else {
                                    logicManager.changeBoardButton(PocketAccounterGeneral.INCOME, position, null);
                                }
                                commonOperations.changeIconToNull(position, dataCache, modeOfMain);

                                List<BoardButton> boardButtons = daoSession.getBoardButtonDao().loadAll();
                                for (BoardButton boardButton : boardButtons) {
                                    if (boardButton.getCategoryId() != null) {
                                        if (boardButton.getCategoryId().equals(Long.toString(currentCredit.getMyCredit_id()))) {

                                            if (boardButton.getTable() == PocketAccounterGeneral.EXPENSE) {
                                                logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, boardButton.getPos(), null);
                                            } else {
                                                logicManager.changeBoardButton(PocketAccounterGeneral.INCOME, boardButton.getPos(), null);
                                            }

                                            commonOperations.changeIconToNull(boardButton.getPos(), dataCache, boardButton.getTable());

                                        }
                                    }
                                }
                                for(ReckingCredit reckingCredit:currentCredit.getReckings()){
                                    logicManager.deleteReckingCredit(reckingCredit);
                                }
                                logicManager.deleteCredit(currentCredit);
                                dataCache.updateAllPercents();
                                paFragmentManager.updateAllFragmentsPageChanges();


                            }
                            if (modeOfMain != PocketAccounterGeneral.NO_MODE && modeOfMain !=PocketAccounterGeneral.DETAIL && modeOfMain!= PocketAccounterGeneral.SEARCH_MODE){
                                dataCache.updateOneDay(dataCache.getEndDate());
                                getActivity().getSupportFragmentManager().popBackStack();
                                paFragmentManager.displayMainWindow();
                            } else {

                                    paFragmentManager.getFragmentManager().popBackStack();
                                    paFragmentManager.displayFragment(new CreditTabLay());

                            }
                            warningDialog.dismiss();
                        }
                    });
                    warningDialog.setOnNoButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            warningDialog.dismiss();
                        }
                    });
                    warningDialog.setText(getString(R.string.delete_credit));
                    warningDialog.show();
                }
            }
        });

        myTakedValue.setText(parseToWithoutNull(currentCredit.getValue_of_credit())+currentCredit.getValyute_currency().getAbbr());
        myReturnValue.setText(parseToWithoutNull(currentCredit.getValue_of_credit_with_procent())+currentCredit.getValyute_currency().getAbbr());
        int resId = context.getResources().getIdentifier(currentCredit.getIcon_ID(), "drawable", context.getPackageName());
        icon_credit.setImageResource(resId);
        dateForSimpleDate.setTime(currentCredit.getTake_time().getTimeInMillis());
        myTakedCredTime.setText(dateFormat.format(dateForSimpleDate));
        calculeted.setText((currentCredit.getKey_for_include())?CALCULATED:NOT_CALCULATED);
        myCreditName.setText(currentCredit.getCredit_name());

        myTotalPaid.setText(parseToWithoutNull(total_paid)+currentCredit.getValyute_currency().getAbbr());

        myLefAmount.setText(getString(R.string.repaid));

        String suffix="";
        if(currentCredit.getProcent_interval()==forDay){
            suffix=getString(R.string.per_day);
        }
        else if(currentCredit.getProcent_interval()==forWeek){
            suffix=getString(R.string.per_week);
        }
        else if(currentCredit.getProcent_interval()==forMoth){
            suffix=getString(R.string.per_month);
        }
        else {
            suffix=getString(R.string.per_year);
        }

        myProcent.setText(parseToWithoutNull(currentCredit.getProcent())+"%"+" "+suffix);

        Calendar to= (Calendar) currentCredit.getTake_time().clone();

        long period_tip=currentCredit.getPeriod_time_tip();
        long period_voqt=currentCredit.getPeriod_time();
        int voqt_soni= (int) (period_voqt/period_tip);

        if(period_tip==forDay){
            suffix=getString(R.string.dayy);
            to.add(Calendar.DAY_OF_YEAR, (int) voqt_soni);
        }
        else if(period_tip==forWeek){
            suffix=getString(R.string.weekk);
            to.add(Calendar.WEEK_OF_YEAR, (int) voqt_soni);
        }
        else if(period_tip==forMoth){
            suffix=getString(R.string.mont);
            to.add(Calendar.MONTH, (int) voqt_soni);

        }
        else {
            suffix=getString(R.string.yearr);
            to.add(Calendar.YEAR, (int) voqt_soni);

        }
        myPeriodOfCredit.setText(Integer.toString(voqt_soni)+" "+suffix);

        V.findViewById(R.id.infoooc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpandOpen){
                    expandablePanel.setVisibility(View.GONE);
                    expandableLiniya.setVisibility(View.GONE);
                    expandableBut.setImageResource(R.drawable.infoo);
                    isExpandOpen=false;
                }
                else {
                    expandablePanel.setVisibility(View.VISIBLE);
                    expandableLiniya.setVisibility(View.VISIBLE);
                    expandableBut.setImageResource(R.drawable.pasga_ochil);
                    isExpandOpen=true;
                }
            }
        });

        long for_compute_interval=currentCredit.getTake_time().getTimeInMillis()+currentCredit.getPeriod_time()-System.currentTimeMillis();

        Date from=new Date();
        int t[]=getDateDifferenceInDDMMYYYY(from,to.getTime());
        Log.d("Myday",t[0]+" "+t[1]+" "+t[2]);
        if(t[0]*t[1]*t[2]<0&&(t[0]+t[1]+t[2])!=0){
            myLefDate.setText(R.string.ends);
        }
        else {
            String left_date_string="";
            if(t[0]!=0){
                if(t[0]>1){
                    left_date_string+=Integer.toString(t[0])+" "+getString(R.string.years);
                }
                else{
                    left_date_string+=Integer.toString(t[0])+" "+getString(R.string.year);
                }

            }
            if(t[1]!=0){
                if(!left_date_string.matches("")){
                    left_date_string+=" ";
                }
                if(t[1]>1){
                    left_date_string+=Integer.toString(t[1])+" "+getString(R.string.moths);
                }
                else{
                    left_date_string+=Integer.toString(t[1])+" "+getString(R.string.moth);
                }
            }
            if(t[2]!=0){
                if(!left_date_string.matches("")){
                    left_date_string+=" ";
                }
                if(t[2]>1){
                    left_date_string+=Integer.toString(t[2])+" "+getString(R.string.days);
                }
                else{
                    left_date_string+=Integer.toString(t[2])+" "+getString(R.string.day);
                }
            }
            myLefDate.setText(left_date_string);
        }

        V.findViewById(R.id.pustooyy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return V;
    }
    public String parseToWithoutNull(double A){
        if(A==(int)A)
            return Integer.toString((int)A);
        else
            return formater.format(A);

    }
    public static int [] getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate=Calendar.getInstance();
        Calendar toDate=Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year,month,day;
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment =fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
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
        return   new int[]{year, month, day};
    }
    interface ConWithFragments{
        void change_item(CreditDetials changed_item, int position);
        void to_Archive(int position);
    }

    ArrayList<Account> accaunt_AC;

    private class PaysCreditAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<ReckingCredit> list;

        public PaysCreditAdapter(ArrayList<ReckingCredit> list) {
            this.list = list;
        }

        public int getItemCount() {
            return list.size();
        }

        public void onBindViewHolder(final ViewHolder view, int position) {
            ReckingCredit item=list.get(position);
            view.infoDate.setText(getString(R.string.date_of_pay)+": "+dateFormat.format(item.getPayDate().getTime()));
            view.infoSumm.setText(parseToWithoutNull(item.getAmount())+currentCredit.getValyute_currency().getAbbr());
            if(currentCredit.getKey_for_include()){
                ArrayList<Account> accounts = (ArrayList<Account>) accountDao.queryBuilder().list();
                String accs = accounts.get(0).getName();
                for (int i = 0; i < accounts.size(); i++) {
                    if(item.getAccountId().equals(accounts.get(i).getId())){
                        accs=accounts.get(i).getName();
                    }
                }
                view.infoAccount.setText(getString(R.string.via)+": " + accs);
            }
            else {
                view.infoAccount.setVisibility(View.GONE);
            }
            if(!item.getComment().matches(""))
            view.comment.setText(getString(R.string.comment)+": " + item.getComment());
            else
                view.comment.setVisibility(View.GONE);
                view.forDelete.setChecked(false);
                view.forDelete.setVisibility(View.GONE);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payed_item, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView infoDate;
        public TextView infoSumm;
        public TextView infoAccount;
        public TextView comment;
        public CheckBox forDelete;
        public View glav;
        public ViewHolder(View view) {
            super(view);
            infoDate = (TextView) view.findViewById(R.id.date_of_trans);
            infoAccount = (TextView) view.findViewById(R.id.via_acount);
            comment = (TextView) view.findViewById(R.id.comment_trans);
            infoSumm = (TextView) view.findViewById(R.id.paid_value);
            forDelete = (CheckBox) view.findViewById(R.id.for_delete_check_box);
            glav=view;
        }
    }
}
