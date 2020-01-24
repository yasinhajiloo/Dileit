package com.example.dileit.view.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dileit.R;
import com.example.dileit.databinding.FragmentWordInformationBinding;
import com.example.dileit.model.Idiom;
import com.example.dileit.model.TranslationWord;
import com.example.dileit.model.WordInformation;
import com.example.dileit.view.adapter.WordsInformationViewPagerAdapter;
import com.example.dileit.view.fragment.wordinfo.EnglishTranslatedFragment;
import com.example.dileit.view.fragment.wordinfo.TranslationFragment;
import com.example.dileit.view.fragment.wordinfo.RelatedIdiomsFragment;
import com.example.dileit.viewmodel.EnglishDictionaryViewModel;
import com.example.dileit.viewmodel.SharedViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;


public class WordInformationFragment extends Fragment {

//    private TextToSpeech mTextToSpeechUS;
//    private TextToSpeech mTextToSpeechUK;

    private String TAG = WordInformationFragment.class.getSimpleName();
    private SharedViewModel mSharedViewModel;
    private FragmentWordInformationBinding mBinding;
    private Chip chipPersian, chipEnglish, chipIdioms;
    private WordsInformationViewPagerAdapter mAdapter;
    private List<TranslationWord> wordList = new ArrayList<>();
    private List<Idiom> mIdioms = new ArrayList<>();
    private boolean isIdiomAvailable = false;
    private EnglishDictionaryViewModel mEnglishDictionaryViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mEnglishDictionaryViewModel = ViewModelProviders.of(getActivity()).get(EnglishDictionaryViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_word_information, container, false);
        chipPersian = mBinding.chipsTranslatedOnly;
        chipEnglish = mBinding.chipsTranslatedEnglish;
        chipIdioms = mBinding.chipsIdiomsOnly;
        mAdapter = new WordsInformationViewPagerAdapter(getFragmentManager());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        mAdapter.addPage(new TranslationFragment());
        mBinding.viewPagerWordInfo.setAdapter(mAdapter);
        mBinding.viewPagerWordInfo.setCurrentItem(0);


//        mTextToSpeechUK = new TextToSpeech(view.getContext(), i -> {
//            if (i == TextToSpeech.SUCCESS) {
//                int res = mTextToSpeechUS.setLanguage(Locale.UK);
//                if (res == TextToSpeech.LANG_MISSING_DATA
//                        || res == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e("TTS", "Language not supported");
//                } else {
//                    // prepare ui
//                }
//            } else {
//                Log.d(TAG, "onViewCreated: " + "TTS init failed...");
//            }
//        });

//        mTextToSpeechUS = new TextToSpeech(view.getContext(), i -> {
//            if (i == TextToSpeech.SUCCESS) {
//                int res = mTextToSpeechUS.setLanguage(Locale.ENGLISH);
//                if (res == TextToSpeech.LANG_MISSING_DATA
//                        || res == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e("TTS", "Language not supported");
//                } else {
//
//                }
//            } else {
//                Log.d(TAG, "onViewCreated: " + "TTS init failed...");
//            }
//        });


//        mBinding.imgBritishPronounce.setOnClickListener(view1 -> {
//            speakUK(mBinding.tvWordTitle.getText().toString());
//        });
//        mBinding.tvBritishPronounce.setOnClickListener(view1 -> {
//            speakUK(mBinding.tvWordTitle.getText().toString());
//        });
//
//
//        mBinding.imgAmericanPronounce.setOnClickListener(view1 -> {
//            speakUS(mBinding.tvWordTitle.getText().toString());
//        });
//        mBinding.tvAmericanPronounce.setOnClickListener(view1 -> {
//            speakUS(mBinding.tvWordTitle.getText().toString());
//        });

        mBinding.imgCloseToolBar.setOnClickListener(view12 -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        mSharedViewModel.getActualWord().observe(getViewLifecycleOwner(), s -> mBinding.tvWordTitle.setText(s));

        mSharedViewModel.getWordInformation().observe(getViewLifecycleOwner(), wordInformation -> {

            mBinding.tvPronounceTitle.setText(wordInformation[0].getPronunciation());

            for (WordInformation information : wordInformation) {
                wordList.addAll(information.getTranslationWords());
                if (information.getIdioms() != null)
                    mIdioms.addAll(information.getIdioms());
            }

            mSharedViewModel.setTranslationWord(wordList);
            if (mIdioms != null) {
                if (mIdioms.size() > 0) {
                    chipIdioms.setVisibility(View.VISIBLE);
                    mAdapter.addPage(new RelatedIdiomsFragment());
                    isIdiomAvailable = true;
                    mSharedViewModel.setIdiom(mIdioms);
                }
            }

            mAdapter.addPage(new EnglishTranslatedFragment());
        });

        mEnglishDictionaryViewModel.getLiveList().observe(getViewLifecycleOwner() , wordEnglishDics -> {
            if (wordEnglishDics.size()==0){
                chipEnglish.setVisibility(View.GONE);
            }
        });

        chipEnglish.setOnClickListener(view15 -> {
            selectEnglishChip();
            undoIdiomChip();
            undoPersianChip();
            if (isIdiomAvailable)
                mBinding.viewPagerWordInfo.setCurrentItem(2);
            else
                mBinding.viewPagerWordInfo.setCurrentItem(1);

        });
        chipIdioms.setOnClickListener(view13 -> {
            selectIdiomChip();
            undoPersianChip();
            undoEnglishChip();
            mBinding.viewPagerWordInfo.setCurrentItem(1);

        });

        chipPersian.setOnClickListener(view14 -> {
            selectPersianChip();
            undoIdiomChip();
            undoEnglishChip();
            mBinding.viewPagerWordInfo.setCurrentItem(0);

        });


        mBinding.viewPagerWordInfo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        selectPersianChip();
                        undoIdiomChip();
                        undoEnglishChip();
                        break;
                    case 1:
                        if (isIdiomAvailable){
                            selectIdiomChip();
                            undoPersianChip();
                            undoEnglishChip();
                        }else {
                            selectEnglishChip();
                            undoIdiomChip();
                            undoPersianChip();
                        }

                        break;
                    case 2:
                        selectEnglishChip();
                        undoIdiomChip();
                        undoPersianChip();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


//    private void speakUS(String text) {
//        a++;
//        if (a % 2 != 0) {
//            mTextToSpeechUS.setSpeechRate(0.0f);
//        } else {
//            mTextToSpeechUS.setSpeechRate(0.6f);
//        }
//        mTextToSpeechUS.setPitch(0f);
//        mTextToSpeechUS.setLanguage(Locale.US);
//
//        mTextToSpeechUS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//    }
//
//    private void speakUK(String text) {
//        a++;
//        if (a % 2 != 0) {
//            mTextToSpeechUK.setSpeechRate(0.0f);
//        } else {
//            mTextToSpeechUK.setSpeechRate(0.6f);
//        }
//        mTextToSpeechUK.setPitch(0f);
//        mTextToSpeechUK.setLanguage(Locale.UK);
//        mTextToSpeechUK.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//    }


    private void selectIdiomChip(){
        chipIdioms.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondary)));
        chipIdioms.setTextColor(Color.WHITE);
    }
    private void selectPersianChip(){
        chipPersian.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondary)));
        chipPersian.setTextColor(Color.WHITE);
    }
    private void selectEnglishChip(){
        chipEnglish.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondary)));
        chipEnglish.setTextColor(Color.WHITE);

    }

    private void undoIdiomChip(){
        chipIdioms.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBackgroundWhite)));
        chipIdioms.setTextColor(Color.BLACK);
    }
    private void undoPersianChip(){
        chipPersian.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBackgroundWhite)));
        chipPersian.setTextColor(Color.BLACK);
    }
    private void undoEnglishChip(){
        chipEnglish.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBackgroundWhite)));
        chipEnglish.setTextColor(Color.BLACK);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }
}
