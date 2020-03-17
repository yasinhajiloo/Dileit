package com.example.dileit.view.fragment.wordinfo.leitnersetup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.dileit.R;
import com.example.dileit.view.adapter.viewpager.AddNewLeitnerViewPagerAdapter;
import com.example.dileit.viewmodel.SharedViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

public class AddNewLeitnerBottomSheet extends BottomSheetDialogFragment {
    private final String TAG = AddNewLeitnerBottomSheet.class.getSimpleName();
    private SharedViewModel mSharedViewModel;
    private String title;
    private TextView tvTitle;
    private EditText edtGuide;
    private ViewPager mPager;
    private TabLayout mTabLayout;
    private AddNewLeitnerViewPagerAdapter mAdapter;
    private String mainTranslation;
    private String secondTranslation;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mAdapter = new AddNewLeitnerViewPagerAdapter(getChildFragmentManager());


    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_new_item_leitner , container,false);
        tvTitle = view.findViewById(R.id.tv_title_dialog_add_leitner);
        edtGuide = view.findViewById(R.id.edt_dialog_guide);
        mPager = view.findViewById(R.id.view_pager_add_new_leitner);
        mTabLayout = view.findViewById(R.id.tab_add_new_leitner);
        mPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mPager);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedViewModel.getLeitnerItemData().observe(getViewLifecycleOwner(),strings -> {
            title = strings[0];
            tvTitle.setText(title);

            mainTranslation = strings[1];
            secondTranslation = strings[2];

            Log.d(TAG, "onViewCreated: " + strings[1]);
            Log.d(TAG, "onViewCreated: " + strings[2]);
            mAdapter.addData("Translation" , TranslationDialogFragment.newInstance(mainTranslation));
            if (secondTranslation!=null){
                mAdapter.addData("Second Translation" , TranslationDialogFragment.newInstance(secondTranslation));
            }
        });



    }
}
