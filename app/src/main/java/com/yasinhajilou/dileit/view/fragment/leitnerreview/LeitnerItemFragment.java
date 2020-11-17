package com.yasinhajilou.dileit.view.fragment.leitnerreview;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yasinhajilou.dileit.R;
import com.yasinhajilou.dileit.constant.KeysValue;
import com.yasinhajilou.dileit.constant.LeitnerModifierConstants;
import com.yasinhajilou.dileit.constant.LeitnerStateConstant;
import com.yasinhajilou.dileit.databinding.FragmentLeitnerItemBinding;
import com.yasinhajilou.dileit.model.entity.Leitner;
import com.yasinhajilou.dileit.model.entity.WordReviewHistory;
import com.yasinhajilou.dileit.utils.LeitnerUtils;
import com.yasinhajilou.dileit.view.adapter.viewpager.LeitnerItemTranslationViewPagerAdapter;
import com.yasinhajilou.dileit.view.fragment.leitnercardhandler.LeitnerCardModifierBottomSheet;
import com.yasinhajilou.dileit.viewmodel.InternalViewModel;
import com.yasinhajilou.dileit.viewmodel.ReviewLeitnerSharedViewModel;

import java.util.ArrayList;
import java.util.List;


public class LeitnerItemFragment extends Fragment {

    private final String TAG = LeitnerItemFragment.class.getSimpleName();
    private FragmentLeitnerItemBinding mBinding;
    private int listId;
    private InternalViewModel mInternalViewModel;
    private InterfaceReviewButtonClickListener mListener;
    private Leitner mLeitner;
    private String mWord;
    private boolean isHeaderOpen = true;
    private ReviewLeitnerSharedViewModel mReviewLeitnerSharedViewModel;

    private int reviewedCards = 0;
    private int reviewAgainCards = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InterfaceReviewButtonClickListener) {
            mListener = (InterfaceReviewButtonClickListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInternalViewModel = new ViewModelProvider(requireActivity()).get(InternalViewModel.class);
        mReviewLeitnerSharedViewModel = new ViewModelProvider(requireActivity()).get(ReviewLeitnerSharedViewModel.class);

        if (getArguments() != null)
            listId = getArguments().getInt(KeysValue.KEY_BUNDLE_LEITNER_ITEM_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentLeitnerItemBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //handle
        if (savedInstanceState != null) {
            isHeaderOpen = savedInstanceState.getBoolean(KeysValue.BUNDLE_KEY_FOR_BOOL_ROTATION);
            if (isHeaderOpen)
                mBinding.layoutSecondReview.setVisibility(View.GONE);
            else
                mBinding.layoutSecondReview.setVisibility(View.VISIBLE);
        }

        mInternalViewModel.getLeitnerCardById(listId).observe(getViewLifecycleOwner(), leitner -> {
            if (leitner.getId() == listId) {
                mLeitner = leitner;
                mWord = leitner.getWord();
                mBinding.tvWordTitleReviewLeitner.setText(mWord);
                //second title in second view
                mBinding.tvTitleLeitner.setText(mWord);

                mBinding.tvLeitnerCat.setText(leitner.getWordAct());

                //second cat in second view
                mBinding.tvLeitnerCatSecond.setText("-" + leitner.getWordAct().toLowerCase() + "-");

                if (leitner.getGuide() != null)
                    mBinding.tvGuideLeitnerCard.setText(leitner.getGuide());

                List<String> strings = new ArrayList<>();

                if (leitner.getDef() == null || leitner.getDef().equals("")) {
                    mBinding.btnTabTranslation.setVisibility(View.GONE);
                } else {
                    strings.add(leitner.getDef());
                    mBinding.btnTabTranslation.setVisibility(View.VISIBLE);
                }

                if (leitner.getSecondDef() == null || leitner.getSecondDef().equals("")) {
                    mBinding.btnTabEnglishTrans.setVisibility(View.GONE);
                } else {
                    strings.add(leitner.getSecondDef());
                    mBinding.btnTabEnglishTrans.setVisibility(View.VISIBLE);
                }

                mBinding.viewPagerLeitnerItemTranslation.setAdapter(new LeitnerItemTranslationViewPagerAdapter(strings, getContext()));
            }

        });


        mBinding.btnTabTranslation.setOnClickListener(view1 -> mBinding.viewPagerLeitnerItemTranslation.setCurrentItem(0));

        mBinding.btnTabEnglishTrans.setOnClickListener(view12 -> mBinding.viewPagerLeitnerItemTranslation.setCurrentItem(1));

        mBinding.layoutContainerReview.setOnTouchListener((view13, motionEvent) ->

        {

            showSecondView((int) motionEvent.getX(), (int) motionEvent.getY());

            return false;
        });


        mBinding.btnReviewYes.setOnClickListener(view15 ->

        {

            addReviewedHistory(mLeitner.getWord());

            int nextBox = LeitnerUtils.nextBoxFinder(mLeitner.getState());

            mLeitner.setState(nextBox);
            mLeitner.setLastReviewTime(System.currentTimeMillis());

            int currentRepeat = mLeitner.getRepeatCounter();
            mLeitner.setRepeatCounter(++currentRepeat);

            mInternalViewModel.updateLeitnerItem(mLeitner);

            mListener.onYesClicked();

            handleReviewedCardsCounter();

        });

        mBinding.btnReviewNo.setOnClickListener(view14 ->

        {
            addReviewedHistory(mLeitner.getWord());

            int currentRepeat = mLeitner.getRepeatCounter();
            mLeitner.setRepeatCounter(++currentRepeat);

            mLeitner.setLastReviewTime(System.currentTimeMillis());
            mLeitner.setState(LeitnerStateConstant.BOX_ONE);
            mInternalViewModel.updateLeitnerItem(mLeitner);
            mListener.onNoClicked();


            handleReviewedCardsCounter();
            handleReviewAgainCardsCounter();
        });


        mBinding.ivLeitnerMenu.setOnClickListener(this::setUpMenu);

        mBinding.ivLeitnerMenuSecond.setOnClickListener(this::setUpMenu);
    }

    private void addReviewedHistory(String word) {
        WordReviewHistory wordReviewHistory = new WordReviewHistory(0, word, System.currentTimeMillis());
        mInternalViewModel.insertWordReviewedHistory(wordReviewHistory);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KeysValue.BUNDLE_KEY_FOR_BOOL_ROTATION, isHeaderOpen);
    }

    //Handle view visibility witch circular animation
    private void showSecondView(int x, int y) {

        if (isHeaderOpen) {
            //start from center
            int startRadius = 0;

            // get the final radius for the clipping circle which is layoutMain whole size
            int finalRadius = Math.max(mBinding.layoutContainerReview.getWidth(), mBinding.layoutContainerReview.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(mBinding.layoutSecondReview, x, y, startRadius, finalRadius);
            }


            // make the view visible and start the animation
            mBinding.layoutSecondReview.setVisibility(View.VISIBLE);


            if (anim != null) {
                anim.start();
            }

            isHeaderOpen = false;

        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void setUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = new MenuInflater(view.getContext());
        menuInflater.inflate(R.menu.menu_review_leitner, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onOptionsItemSelected);
        popupMenu.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_delete_leitner:
                mInternalViewModel.deleteLeitnerItem(mLeitner);
                break;
            case R.id.menu_edit_leitner:
                LeitnerCardModifierBottomSheet bottomSheet = LeitnerCardModifierBottomSheet.onNewInstance(LeitnerModifierConstants.EDIT, mLeitner.getId());
                bottomSheet.show(getChildFragmentManager(), "BottomSheetEditor");
                break;
        }
        return true;
    }

    private void handleReviewedCardsCounter() {
        reviewedCards++;
        if (mReviewLeitnerSharedViewModel.getReviewedCards().getValue() != null)
            reviewedCards = reviewedCards + mReviewLeitnerSharedViewModel.getReviewedCards().getValue();

        mReviewLeitnerSharedViewModel.setReviewedCards(reviewedCards);

    }

    private void handleReviewAgainCardsCounter() {
        reviewAgainCards++;
        if (mReviewLeitnerSharedViewModel.getReviewAgainCards().getValue() != null)
            reviewAgainCards = reviewAgainCards + mReviewLeitnerSharedViewModel.getReviewAgainCards().getValue();

        mReviewLeitnerSharedViewModel.setReviewAgainCards(reviewAgainCards);
    }
}