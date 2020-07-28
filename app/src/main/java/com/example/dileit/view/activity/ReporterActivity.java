package com.example.dileit.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.dileit.R;
import com.example.dileit.constant.TimeReporterFilter;
import com.example.dileit.view.adapter.viewpager.BarChartViewPagerAdapter;
import com.example.dileit.view.fragment.reporter.DatePickerDialogFragment;
import com.example.dileit.databinding.ActivityReporterBinding;
import com.example.dileit.viewmodel.ReporterViewModel;

import static com.example.dileit.constant.TimeReporterFilter.DAY;
import static com.example.dileit.constant.TimeReporterFilter.MONTH;
import static com.example.dileit.constant.TimeReporterFilter.WEEK;
import static com.example.dileit.constant.TimeReporterFilter.YEAR;

public class ReporterActivity extends AppCompatActivity {

    private ActivityReporterBinding mBinding;
    private long duration = 1000;
    private ReporterViewModel mReporterViewModel;

    private String TAG = ReporterActivity.class.getSimpleName();
    private int allLeitnerWord;
    private int allReviewedCards;
    private BarChartViewPagerAdapter mBarChartViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityReporterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbarReporter);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mReporterViewModel = ViewModelProviders.of(this).get(ReporterViewModel.class);

        mBarChartViewPagerAdapter = new BarChartViewPagerAdapter(getSupportFragmentManager());

        mBinding.vpBarChart.setAdapter(mBarChartViewPagerAdapter);

        mReporterViewModel.getAllLeitnerCardCount().observe(this, integer -> {
            showAllCountessWithAnimation(integer);
            allLeitnerWord = integer;

            //first init for month
            mReporterViewModel.setLiveTimeRange(new long[]{30 * 24 * 60 * 60 * 1000L, System.currentTimeMillis()});
            mReporterViewModel.setSelectedTime(TimeReporterFilter.MONTH);
        });

        mReporterViewModel.getWordReviewedHistoryCount().observe(this, count -> {
            allReviewedCards = count;
        });

        mReporterViewModel.getSelectedTimeRange().observe(this, new Observer<long[]>() {
            @Override
            public void onChanged(long[] longs) {
                mReporterViewModel.setAddedCardsTimeRange(longs[0] , longs[1]);
                mReporterViewModel.setReviewedCardsTimeRange(longs[0] , longs[1]);
            }
        });

        mReporterViewModel.getLearnedCardsCount().observe(this, this::showLearnedCountessWithAnimation);

        mReporterViewModel.getLiveAddedCardCount().observe(this, size -> {
            mBinding.cpAdded.setProgress(size, allLeitnerWord);
        });

        mReporterViewModel.getLiveReviewedCardCount().observe(this, size -> {
            mBinding.cpReviewed.setProgress(size, allReviewedCards);
        });

        mBinding.chipHeadFilter.setOnClickListener(view -> {
            DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
            datePickerDialogFragment.show(getSupportFragmentManager(), "DatePickerDialogFragment");
        });

        mReporterViewModel.getTimeFilterFlag().observe(this, integer -> {
            switch (integer) {
                case DAY:
                    mBinding.chipHeadFilter.setText("Day");
                    break;
                case WEEK:
                    mBinding.chipHeadFilter.setText("Week");
                    break;
                case MONTH:
                    mBinding.chipHeadFilter.setText("Month");
                    break;
                case YEAR:
                    mBinding.chipHeadFilter.setText("Year");
                    break;
            }
        });

        mBinding.tvVpHandlerAdded.setOnClickListener(view -> {
            mBinding.vpBarChart.setCurrentItem(1);
        });

        mBinding.tvVpHandlerReview.setOnClickListener(view -> {
            mBinding.vpBarChart.setCurrentItem(0);
        });

        mBinding.vpBarChart.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBinding.tvVpHandlerReview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
                        mBinding.tvVpHandlerAdded.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                        mBinding.tvVpHandlerReview.setTextColor(getResources().getColor(R.color.colorSecondary));
                        mBinding.tvVpHandlerAdded.setTextColor(getResources().getColor(R.color.darkGrey));
                        break;
                    case 1:
                        mBinding.tvVpHandlerReview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                        mBinding.tvVpHandlerAdded.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
                        mBinding.tvVpHandlerReview.setTextColor(getResources().getColor(R.color.darkGrey));
                        mBinding.tvVpHandlerAdded.setTextColor(getResources().getColor(R.color.colorSecondary));
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showAllCountessWithAnimation(int allCardsCount) {
        ValueAnimator animator = ValueAnimator.ofInt(0, allCardsCount);
        animator.setDuration(duration);
        animator.addUpdateListener(valueAnimator -> mBinding.tvTotalCardsCount.setText(valueAnimator.getAnimatedValue().toString()));

        animator.start();
    }

    private void showLearnedCountessWithAnimation(int learnedCards) {
        ValueAnimator animator = ValueAnimator.ofInt(0, learnedCards);
        animator.setDuration(duration);
        animator.addUpdateListener(valueAnimator -> mBinding.tvLearnedCardsCount.setText(valueAnimator.getAnimatedValue().toString()));

        animator.start();
    }
}