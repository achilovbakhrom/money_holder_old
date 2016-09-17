package com.jim.pocketaccounter.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.LinearManagerWithOutEx;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.CreditDetialsDao;
import com.jim.pocketaccounter.database.Currency;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.ReckingCredit;
import com.jim.pocketaccounter.database.ReckingCreditDao;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.database.RootCategoryDao;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.utils.SearchResultConten;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import static com.jim.pocketaccounter.utils.ContenerStaticSearchVariables.*;

public class SearchFragment extends Fragment {
    RecyclerView rvSearchItems;
    RVAdapterSearch rvAdapterSearch = new RVAdapterSearch();
    String prevSearchString="";
    SimpleDateFormat dateformarter;
    DecimalFormat formater;
    @Inject
    DaoSession daoSession;
    @Inject
    CommonOperations comonOperation;
    @Inject
    PAFragmentManager paFragmentManager;
    public SearchFragment() {
        // Required empty public constructor
        dateformarter=new  SimpleDateFormat("dd.MM.yyyy");
        formater=new DecimalFormat("0.00##");
    }
    public TextChangeListnerW getListnerChange(){
        return new TextChangeListnerW() {
            @Override
            public void onTextChange(String searchString) {
                if(!prevSearchString.equals(searchString)){
                    changeListForResult(searchString);

                    prevSearchString=searchString;
                    Log.d("SystemCalls",searchString);
                }
            }
        };
    }
    public interface TextChangeListnerW{
        void onTextChange(String searchString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
    }
    String[] tempIcons;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        rvSearchItems =(RecyclerView) view.findViewById(R.id.recyc_item_search);
        LinearManagerWithOutEx llm = new LinearManagerWithOutEx(getContext());
        rvSearchItems.setLayoutManager(llm);
        rvSearchItems.setAdapter(rvAdapterSearch);
        tempIcons = getResources().getStringArray(R.array.icons);
        rvSearchItems.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(container.getWindowToken(), 0);
                }
                return false;
            }
        });


        return view;
    }

    public void changeListForResult(String searchSt){
        List<SearchResultConten> searchItemsToSend=new ArrayList<>();
//        searchItemsToSend.add(new SearchResultConten("Vasiya",44.5d,DEBT_RECKING, Calendar.getInstance(),"1","icons_14","Nimadir qilgon erdim",new Currency("Dollar","P","asda",false)));
//        searchItemsToSend.add(new SearchResultConten("Vasiya",44.5d,CREDIT_VAR, Calendar.getInstance(),"1",null,"Nimadir qilgon erdim",new Currency("Dollar","P","asda",false)));
//        searchItemsToSend.add(new SearchResultConten("Motor",-25d,SIMPLE_RECKING, Calendar.getInstance(),"1","icons_14",null,new Currency("Dollar","$","asda",false)));

        QueryBuilder<CreditDetials> queryBuilderCred=daoSession.getCreditDetialsDao().queryBuilder();
        queryBuilderCred.whereOr(CreditDetialsDao.Properties.Credit_name.like("%" + searchSt + "%"),
                CreditDetialsDao.Properties.Value_of_credit_with_procent.like("%" + searchSt + "%"));
        List<CreditDetials> cd=queryBuilderCred.build().list();
        for (CreditDetials temp:cd) {
            searchItemsToSend.add(new SearchResultConten(temp.getCredit_name(),temp.getValue_of_credit_with_procent(),CREDIT_VAR,temp.getTake_time(),temp,temp.getIcon_ID(),(temp.isKey_for_include())?"This credit include the balance":"This Credit Not include the balance",temp.getValyute_currency()));
        }

        QueryBuilder<ReckingCredit> queryBuilder=daoSession.getReckingCreditDao().queryBuilder();
        queryBuilder.whereOr(ReckingCreditDao.Properties.Comment.like("%" + searchSt + "%"),
                ReckingCreditDao.Properties.Amount.like("%" + searchSt + "%"),
                queryBuilder.join(ReckingCreditDao.Properties.MyCredit_id,CreditDetials.class, CreditDetialsDao.Properties.MyCredit_id).or(CreditDetialsDao.Properties.Credit_name.like("%" + searchSt + "%"),CreditDetialsDao.Properties.Credit_name.like("%" + searchSt + "%")));
        List<ReckingCredit> rk=queryBuilder.build().list();
        for (ReckingCredit temp:rk) {
            CreditDetials creditDetialse=daoSession.getCreditDetialsDao().load(temp.getMyCredit_id());
            Calendar calen=new GregorianCalendar();
            calen.setTimeInMillis(temp.getPayDate());
            searchItemsToSend.add(new SearchResultConten(creditDetialse.getCredit_name(),temp.getAmount(),CREDIT_RECKING, calen,temp,creditDetialse.getIcon_ID(),temp.getComment(),creditDetialse.getValyute_currency()));
        }



//        FinanceRecordDao reckingDao = daoSession.getFinanceRecordDao();
//        CreditDetialsDao creditDetials = daoSession.getCreditDetialsDao();
//
//        Query<FinanceRecordDao> query = reckingDao.queryBuilder()
//                .where(FinanceRecordDao.Properties..like("%" + searchSt + "%"))
//                .build();
//
//        Query<CreditDetials> queryCredit = creditDetials.queryBuilder()
//                .where(CreditDetialsDao.Properties.Credit_name.like("%" + searchSt + "%"))
//                .build();
//
//        for (CreditDetials temp: queryCredit.list() ) {
//            if (!temp.isKey_for_archive())
//           searchItemsToSend.add(new SearchResultConten(temp.getCredit_name(),temp.getValue_of_credit_with_procent(),CREDIT_VAR,temp.getTake_time(),temp,"icons_11"/*tempIcons[temp.getIcon_ID()]*/,(temp.isKey_for_include())?"This credit include the balance":"This Credit Not include the balance",temp.getValyute_currency()));
//        }
//


        rvAdapterSearch.setDataList(searchItemsToSend,getActivity(),searchSt);
        rvAdapterSearch.notifyDataSetChanged();

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class RVAdapterSearch extends RecyclerView.Adapter<RVAdapterSearch.SearchItemViewHolder>{
        List<SearchResultConten> searchItems;
        Context context;
        String seq;
        public void setDataList(List<SearchResultConten> searchItems,Context context,String seq){
            this.searchItems=searchItems;
            this.context=context;
            this.seq=seq;
        }

        @Override
        public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
            SearchItemViewHolder vh=new SearchItemViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(SearchItemViewHolder holder, int position) {
            final SearchResultConten item=searchItems.get(position);

            /*Set icon*/
            if(item.isItIconWithId()) {
                holder.iconik.setImageResource(item.getIcon_Id());
            }
            else if (item.getIcon()!=null)
                holder.iconik.setImageResource(getResources().getIdentifier(item.getIcon(),"drawable",context.getPackageName()));
            else
                holder.iconik.setImageResource(getResources().getIdentifier("icons_2","drawable",context.getPackageName()));

            /*Set date*/
            if(item.getMyDate()!=null)
                comonOperation.ColorSubSeq(dateformarter.format(item.getMyDate().getTime()),seq,"#e2e2e2",holder.date);

            /*Set name of item*/
            comonOperation.ColorSubSeq(item.getStNameOfItem(),seq,"#e2e2e2",holder.name);

            /*Set item amount*/
            if(item.getdAmount()==0){
                holder.ammount.setTextColor(Color.parseColor("#028929"));
                comonOperation.ColorSubSeq(formater.format(item.getdAmount())+item.getCurrency().getAbbr(),seq,"#e2e2e2",holder.ammount);

            }
            if (item.getdAmount()>0){
                holder.ammount.setTextColor(Color.parseColor("#028929"));
                comonOperation.ColorSubSeq("+"+formater.format(item.getdAmount())+item.getCurrency().getAbbr(),seq,"#e2e2e2",holder.ammount);
            }
            else {
                comonOperation.ColorSubSeq(formater.format(item.getdAmount())+item.getCurrency().getAbbr(),seq,"#e2e2e2",holder.ammount);

            }
            /*Set type*/
            comonOperation.ColorSubSeq(item.getTypeInString(context),seq,"#e2e2e2",holder.type);

            /*Set Comment*/
            if(item.getComment()!=null){
                if(item.getComment().length()!=0)
                    comonOperation.ColorSubSeq(item.getComment(),seq,"#e2e2e2",holder.comment);
                else {
                    holder.relativeLayout.setVisibility(View.GONE);
                    holder.forgoneLine.setVisibility(View.GONE);
                }
            }
            else {
                holder.relativeLayout.setVisibility(View.GONE);
                holder.forgoneLine.setVisibility(View.GONE);
            }

            /*Set Onclick Listner*/
            switch (item.getStTypeSearch()){
                case SIMPLE_RECKING:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            paFragmentManager.displayFragment(n);
                            Toast.makeText(context, "Simple Racking", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case CREDIT_VAR:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "CREDIT_VAR", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case DEBT_VAR:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "DEBT_VAR", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case BORROW_VAR:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "BORROW_VAR", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case CREDIT_RECKING:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "CREDIT_RECKING", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case DEBT_RECKING:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "DEBT_RECKING", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case BORROW_RECKING:
                    holder.mainItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "BORROW_RECKING", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;

            }

        }

        @Override
        public int getItemCount() {
            return (searchItems==null)?0:searchItems.size();
        }

        public class SearchItemViewHolder extends RecyclerView.ViewHolder {
            ImageView iconik;
            TextView name;
            TextView ammount;
            TextView type;
            TextView date;
            TextView comment;
            FrameLayout forgoneLine;
            RelativeLayout relativeLayout;
            LinearLayout mainItemView;
            SearchItemViewHolder(View itemView) {
                super(itemView);
                iconik=(ImageView) itemView.findViewById(R.id.ivRecordDetail);
                name=(TextView) itemView.findViewById(R.id.tvRecordDetailCategoryName);
                ammount=(TextView) itemView.findViewById(R.id.tvRecordDetailCategoryAmount);
                type=(TextView) itemView.findViewById(R.id.type_search_item);
                date=(TextView) itemView.findViewById(R.id.dateToSearch);
                comment=(TextView) itemView.findViewById(R.id.tvComment);
                forgoneLine=(FrameLayout) itemView.findViewById(R.id.for_gone_a);
                mainItemView=(LinearLayout) itemView.findViewById(R.id.mainview);
                relativeLayout=(RelativeLayout) itemView.findViewById(R.id.visibleIfCommentHave);
            }
        }
    }
}
