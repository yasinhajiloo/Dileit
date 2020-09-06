package com.example.dileit.view.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.dileit.R;
import com.example.dileit.constant.KeysValue;
import com.example.dileit.databinding.FragmentWordSearchBinding;
import com.example.dileit.model.entity.WordHistory;
import com.example.dileit.view.adapter.recycler.AllWordsRecyclerAdapter;
import com.example.dileit.view.viewinterface.WordsRecyclerViewInterface;
import com.example.dileit.viewmodel.ExternalViewModel;
import com.example.dileit.viewmodel.InternalViewModel;
import com.example.dileit.viewmodel.SharedViewModel;


public class SearchFragment extends Fragment implements WordsRecyclerViewInterface {

    private AllWordsRecyclerAdapter mAdapter;
    private SharedViewModel mSharedViewModel;
    private String TAG = SearchFragment.class.getSimpleName();
    private InternalViewModel mInternalViewModel;
    private FragmentWordSearchBinding mBinding;
    private ExternalViewModel mExternalViewModel;
    private InputMethodManager inputMethodManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mInternalViewModel = ViewModelProviders.of(getActivity()).get(InternalViewModel.class);
        mExternalViewModel = ViewModelProviders.of(this).get(ExternalViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentWordSearchBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mBinding.edtSearchWord.requestFocus();

        showSoftInput(view.findFocus());

        setUpRecyclerView();

        mExternalViewModel.getSyncedSearchResult().observe(getViewLifecycleOwner(), searchDictionaries -> {
            if (!mBinding.edtSearchWord.getQuery().equals("")) {
                mAdapter.setData(searchDictionaries);
            }
        });

        mSharedViewModel.getVoiceWord().observe(getViewLifecycleOwner(), s -> {
            if (!s.equals("")) {
                mBinding.edtSearchWord.setQuery(s, false);
            }
        });

        mBinding.btnBackwardSearch.setOnClickListener(view12 -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            inputMethodManager.hideSoftInputFromWindow(view12.getWindowToken(), 0);

        });

        mBinding.edtSearchWord.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    mExternalViewModel.getSearchEng(newText);
                    mExternalViewModel.getSearchPer(newText);
                } else {
                    mExternalViewModel.getSearchEng("");
                    mExternalViewModel.getSearchPer("");
                    mAdapter.setData(null);
                }
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        mAdapter = new AllWordsRecyclerAdapter(this::onItemClicked);
        mBinding.rvSearchRagment.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvSearchRagment.setAdapter(mAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mBinding = null;
        mSharedViewModel.resetVoiceWord();
    }

    public void showSoftInput(View view) {
        if (inputMethodManager != null)
            inputMethodManager.showSoftInput(view, 0);
    }

    @Override
    public void onItemClicked(String actualWord, int engId) {
        Bundle bundle = new Bundle();
        bundle.putString(KeysValue.KEY_BUNDLE_ACTUAL_WORD, actualWord.trim());
        bundle.putInt(KeysValue.KEY_BUNDLE_WORD_REF_ID, engId);
        WordHistory wordHistory = new WordHistory(actualWord.trim(), engId, 0, System.currentTimeMillis());
        mInternalViewModel.insertWordHistory(wordHistory);

        Navigation.findNavController(getView()).navigate(R.id.action_wordSearchFragment_to_wordInformationFragment, bundle);

    }
}
