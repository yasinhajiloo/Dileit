package com.example.dileit.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Toast;

import com.example.dileit.constant.KeysValue;
import com.example.dileit.databinding.ActivityReviewLeitnerBinding;
import com.example.dileit.model.entity.Leitner;
import com.example.dileit.utils.LeitnerUtils;
import com.example.dileit.view.adapter.viewpager.LeitnerReviewViewPagerAdapter;
import com.example.dileit.view.fragment.leitner.InterfaceReviewButtonClickListener;
import com.example.dileit.view.fragment.leitner.LeitnerItemFragment;
import com.example.dileit.viewmodel.InternalViewModel;

import java.util.ArrayList;
import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class ReviewLeitnerActivity extends AppCompatActivity implements InterfaceReviewButtonClickListener {

    private InternalViewModel mViewModel;
    private LeitnerReviewViewPagerAdapter mAdapter;
    private ActivityReviewLeitnerBinding mBinding;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityReviewLeitnerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = ViewModelProviders.of(this).get(InternalViewModel.class);

        mAdapter = new LeitnerReviewViewPagerAdapter(getSupportFragmentManager());
        mBinding.viewPagerReviewLeitner.setAdapter(mAdapter);


        CircularProgressIndicator.ProgressTextAdapter progressTextAdapter = currentProgress -> {
            int a = (int) currentProgress;
            return a+"%";
        };


        mBinding.progressCircularReviewBar.setProgressTextAdapter(progressTextAdapter);
        mBinding.progressCircularReviewBar.setShouldDrawDot(true);
        mBinding.progressCircularReviewBar.setMaxProgress(100);
        mBinding.progressCircularReviewBar.setCurrentProgress(47);

        mViewModel.getAllLeitnerItems().observe(this, leitnerList -> {
            List<Leitner> filteredList = LeitnerUtils.getPreparedLeitnerItems(leitnerList);
            fragments = new ArrayList<>();

            for (int i = 0; i < filteredList.size(); i++) {
                LeitnerItemFragment itemFragment = new LeitnerItemFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(KeysValue.KEY_BUNDLE_LEITNER_ITEM_ID, filteredList.get(i).getId());
                itemFragment.setArguments(bundle);
                fragments.add(itemFragment);
            }
            mAdapter.addData(fragments);
        });
    }

    @Override
    public void onYesClicked() {
        handleNextItem();
    }

    @Override
    public void onNoClicked() {
        handleNextItem();
    }

    private void handleNextItem(){
        int nextItem = mBinding.viewPagerReviewLeitner.getCurrentItem()+1;
        int listSize = fragments.size()-1;
        if ( nextItem <= listSize ){
            mBinding.viewPagerReviewLeitner.setCurrentItem(nextItem);
        }else {
            Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
        }
    }
}
