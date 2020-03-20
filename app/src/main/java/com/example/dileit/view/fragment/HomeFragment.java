package com.example.dileit.view.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dileit.R;
import com.example.dileit.constant.KeysValue;
import com.example.dileit.databinding.FragmentHomeBinding;
import com.example.dileit.utils.JsonUtils;
import com.example.dileit.view.adapter.recycler.WordHistoryRecyclerAdapter;
import com.example.dileit.view.viewinterface.WordsRecyclerViewInterface;
import com.example.dileit.viewmodel.InternalViewModel;
import com.example.dileit.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.Locale;


public class HomeFragment extends Fragment implements WordsRecyclerViewInterface {
    private static final int REQ_CODE_SPEECH_TO_TEXT = 12;
    private FragmentHomeBinding mBinding;
    private InternalViewModel mViewModel;
    private WordHistoryRecyclerAdapter mAdapter;
    private SharedViewModel mSharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(InternalViewModel.class);
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mAdapter = new WordHistoryRecyclerAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.nestedScrollView.scrollTo(0,0);
        mBinding.rvWordHistory.setAdapter(mAdapter);
        mBinding.rvWordHistory.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mViewModel.getAllWordHistory().observe(getViewLifecycleOwner() , wordHistories -> {
            if (wordHistories.size()>0){
                mBinding.tvWordHistoryInfo.setVisibility(View.GONE);
                mBinding.rvWordHistory.setVisibility(View.VISIBLE);
                mAdapter.setData(wordHistories);
            }else {
                mBinding.tvWordHistoryInfo.setVisibility(View.VISIBLE);
                mBinding.rvWordHistory.setVisibility(View.GONE); }
        });

        mBinding.linearLayoutVoice.setOnClickListener(view12 -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_TO_TEXT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.tvHomeWord.setOnClickListener(this::goToSearchView);

        mBinding.fabAddLeitner.setOnClickListener(view1 -> {
            DialogAddCostumeLeitner dialogAddCostumeLeitner = new DialogAddCostumeLeitner();
            dialogAddCostumeLeitner.show(getChildFragmentManager() , "tag_dialog_costume_leitner");
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_TO_TEXT) {
            if (data != null) {
                ArrayList res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (res != null) {
                    mSharedViewModel.setVoiceWord(res.get(0).toString());
                    goToSearchView(getView());
                } else {
                    Toast.makeText(getContext(), "is null", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void goToSearchView(View view){
        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_wordSearchFragment);
    }
    @Override
    public void onItemClicked(String data, String actualWord) {
        JsonUtils jsonUtils = new JsonUtils();
        mSharedViewModel.setWordInformation(jsonUtils.getWordDefinition(data));
        Bundle bundle = new Bundle();
        bundle.putString(KeysValue.KEY_BUNDLE_ACTUAL_WORD , actualWord.trim());
        Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_wordInformationFragment , bundle);
    }
}
