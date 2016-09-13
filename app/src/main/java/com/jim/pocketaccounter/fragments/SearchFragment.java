package com.jim.pocketaccounter.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jim.pocketaccounter.R;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }
    public TextChangeListnerW getListnerChange(){
        return new TextChangeListnerW() {
            @Override
            public void onTextChange(String searchString) {
            Log.d("SystemCalls",searchString);
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

        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }


}