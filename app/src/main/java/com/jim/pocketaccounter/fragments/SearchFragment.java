package com.jim.pocketaccounter.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.LinearManagerWithOutEx;
import com.jim.pocketaccounter.utils.SearchResultConten;

import java.util.List;

public class SearchFragment extends Fragment {
    RecyclerView rvSearchItems;
    RVAdapterSearch rvAdapterSearch = new RVAdapterSearch();
    String prevSearchString="";
    public SearchFragment() {
        // Required empty public constructor
    }
    public TextChangeListnerW getListnerChange(){
        return new TextChangeListnerW() {
            @Override
            public void onTextChange(String searchString) {
                if(!prevSearchString.equals(searchString)){

                    rvAdapterSearch.notifyDataSetChanged();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvSearchItems =(RecyclerView) view.findViewById(R.id.recyc_item_search);
        LinearManagerWithOutEx llm = new LinearManagerWithOutEx(getContext());
        rvSearchItems.setLayoutManager(llm);
        rvSearchItems.setAdapter(rvAdapterSearch);
        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class RVAdapterSearch extends RecyclerView.Adapter<RVAdapterSearch.SearchItemViewHolder>{
        List<SearchResultConten> searchItems;
        @Override
        public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
            SearchItemViewHolder vh=new SearchItemViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(SearchItemViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class SearchItemViewHolder extends RecyclerView.ViewHolder {
            
            SearchItemViewHolder(View itemView) {
                super(itemView);
            
            }
        }
    }
}
