package com.example.dileit.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dileit.R;
import com.example.dileit.databinding.FragmentAdvancedSearchResultBinding;
import com.example.dileit.view.adapter.AdvancedDicItemsRecyclerAdapter;
import com.example.dileit.viewmodel.AdvancedDictionaryViewModel;
import com.example.dileit.viewmodel.SharedViewModel;


public class AdvancedSearchResultFragment extends Fragment {

    private SharedViewModel mSharedViewModel;
    private FragmentAdvancedSearchResultBinding mBinding;
    private AdvancedDictionaryViewModel mAdvancedDictionaryViewModel;
    private AdvancedDicItemsRecyclerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mAdvancedDictionaryViewModel = ViewModelProviders.of(getActivity()).get(AdvancedDictionaryViewModel.class);
        mAdapter = new AdvancedDicItemsRecyclerAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_advanced_search_result, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.rvAdvancedDic.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mBinding.rvAdvancedDic.setAdapter(mAdapter);


        mSharedViewModel.getAdvancedRes().observe(getViewLifecycleOwner(), wordSearches -> {
            mAdapter.setData(wordSearches);
        });
    }

}
