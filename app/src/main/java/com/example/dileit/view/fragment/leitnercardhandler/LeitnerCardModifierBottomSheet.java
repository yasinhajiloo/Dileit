package com.example.dileit.view.fragment.leitnercardhandler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.dileit.R;
import com.example.dileit.constant.KeysValue;
import com.example.dileit.constant.LeitnerStateConstant;
import com.example.dileit.databinding.BottomSheetAddNewLeitnerBinding;
import com.example.dileit.model.EnglishDef;
import com.example.dileit.model.TranslationExample;
import com.example.dileit.model.TranslationWord;
import com.example.dileit.model.WordInformation;
import com.example.dileit.model.entity.Leitner;
import com.example.dileit.constant.LeitnerModifierConstants;
import com.example.dileit.utils.StringUtils;
import com.example.dileit.view.adapter.viewpager.AddNewLeitnerViewPagerAdapter;
import com.example.dileit.viewmodel.InternalViewModel;
import com.example.dileit.viewmodel.SharedViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class LeitnerCardModifierBottomSheet extends BottomSheetDialogFragment {
    private final String TAG = LeitnerCardModifierBottomSheet.class.getSimpleName();
    private BottomSheetAddNewLeitnerBinding mBinding;
    private SharedViewModel mSharedViewModel;
    private AddNewLeitnerViewPagerAdapter mAdapter;
    private InternalViewModel mInternalViewModel;
    private LeitnerModifierConstants mConstants;
    private int cardId;
    private Leitner mLeitner;
    private String title;

    private List<WordInformation> mWordInformations = new ArrayList<>();
    private List<EnglishDef> mEngDefs = new ArrayList<>();

    private boolean showExamples, showSynonyms = true;

    public static LeitnerCardModifierBottomSheet onNewInstance(LeitnerModifierConstants constants, int cardId) {
        LeitnerCardModifierBottomSheet bottomSheet = new LeitnerCardModifierBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KeysValue.FRAGMENT_LEITNER_CARD_HANDLER, constants);
        bundle.putInt(KeysValue.FRAGMENT_LEITNER_CARD_HANDLER_INT_ID, cardId);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mAdapter = new AddNewLeitnerViewPagerAdapter();
        mInternalViewModel = ViewModelProviders.of(this).get(InternalViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mConstants = (LeitnerModifierConstants) getArguments().getSerializable(KeysValue.FRAGMENT_LEITNER_CARD_HANDLER);
            cardId = getArguments().getInt(KeysValue.FRAGMENT_LEITNER_CARD_HANDLER_INT_ID);
        }
        mBinding = BottomSheetAddNewLeitnerBinding.inflate(inflater, container, false);
        mBinding.viewPagerAddNewLeitner.setAdapter(mAdapter);
        mBinding.tabAddNewLeitner.setupWithViewPager(mBinding.viewPagerAddNewLeitner);
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(this::onDestroyView);
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_card_cat, StringUtils.dropDownItem(getContext()));
        mBinding.dropdownText.setAdapter(arrayAdapter);

        //this class will call in two ways:
        //1- when user wants to add a brand new card
        //2- when user wants to edit a card

        //1:it calls for adding
        if (mConstants == LeitnerModifierConstants.ADD) {
            setUpViewForAdding();
        } else {
            //2:it calls for editing
            if (mConstants == LeitnerModifierConstants.EDIT) {
                setUpViewForEditing();
                mBinding.radioGpDialog.setVisibility(View.INVISIBLE);
                // set edit texts available
                mSharedViewModel.setCostumeCheck(true);
            }
        }


        mBinding.btnSaveItemLeitner.setOnClickListener(view1 -> {
            mSharedViewModel.setSaveBtnCheck(true);
            String edtTitle = mBinding.edtTitleDialogAddLeitner.getText().toString().trim();
            String edtGuide = mBinding.edtDialogGuide.getText().toString().trim();
            String edtCat = mBinding.dropdownText.getText().toString();
            if (!edtTitle.equals("") && !edtCat.equals("")) {
                switch (mConstants) {
                    case ADD:

                        mLeitner = new Leitner(0, title, mSharedViewModel.getTranslation(), mSharedViewModel.getSecondTranslation(),
                                edtCat, edtGuide, LeitnerStateConstant.STARTED, 0, 0, System.currentTimeMillis());
                        mInternalViewModel.insertLeitnerItem(mLeitner);

                        break;
                    case EDIT:

                        mLeitner.setDef(mSharedViewModel.getTranslation());
                        mLeitner.setSecondDef(mSharedViewModel.getSecondTranslation());
                        mLeitner.setWord(edtTitle);
                        mLeitner.setWordAct(edtCat);
                        if (!edtGuide.equals(""))
                            mLeitner.setGuide(edtGuide);

                        mInternalViewModel.updateLeitnerItem(mLeitner);
                        break;
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.pl_fill_fields), Toast.LENGTH_LONG).show();
            }
        });


        mSharedViewModel.getActualWord().observe(getViewLifecycleOwner(), s -> {
            title = s;
            mBinding.edtTitleDialogAddLeitner.setText(s);
        });
        mInternalViewModel.getAddedLeitnerItemId().observe(getViewLifecycleOwner(), aLong -> {
            if (aLong > 0) {
                Toast.makeText(getContext(), getString(R.string.added), Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
        mInternalViewModel.getUpdateItemStatus().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Toast.makeText(getContext(), R.string.leitner_updated, Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpViewForEditing() {
        mInternalViewModel.getLeitnerCardById(cardId).observe(getViewLifecycleOwner(), leitner -> {

            mBinding.edtTitleDialogAddLeitner.setText(leitner.getWord());
            mBinding.dropdownText.setText(leitner.getWordAct(), false);
            mBinding.edtDialogGuide.setText(leitner.getGuide());
            mBinding.edtTitleDialogAddLeitner.setText(leitner.getWord());

//            handleViewPagerItems(leitner.getDef(), leitner.getSecondDef());

            mLeitner = leitner;
        });
    }

    private void setUpViewForAdding() {

        mSharedViewModel.getTranslationWord().observe(getViewLifecycleOwner(), wordInformations -> {
            mWordInformations = wordInformations;
            mAdapter.addData(getString(R.string.translation), getTranslationData(showExamples, mWordInformations));

        });

        mSharedViewModel.getEngDefList().observe(getViewLifecycleOwner(), englishDefs -> {
            mEngDefs = englishDefs;
            mAdapter.addData(getString(R.string.enf_def), getEngDefData(showExamples, showSynonyms, mEngDefs));
        });

        mBinding.rbCostumeBottomSheet.setOnCheckedChangeListener((compoundButton, b) -> mSharedViewModel.setCostumeCheck(b));

        if (mBinding.chipEngSynonyms != null) {
            mBinding.chipEngSynonyms.setOnCheckedChangeListener((buttonView, isChecked) -> {

                showSynonyms = isChecked;
                if (engEditText() != null)
                    engEditText().setText(getEngDefData(showExamples, showSynonyms, mEngDefs));


            });
        }
        assert mBinding.chipExamples != null;
        mBinding.chipExamples.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showExamples = isChecked;
            if (engEditText() != null)
                engEditText().setText(getEngDefData(showExamples, showSynonyms, mEngDefs));
            if (translationEdtText() != null)
                translationEdtText().setText(getEngDefData(showExamples, showSynonyms, mEngDefs));

        });
    }

    private String getEngDefData(boolean showExamples, boolean showSynonyms, List<EnglishDef> engDefList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (EnglishDef englishDef :
                engDefList) {
            stringBuilder.append("*").append(englishDef.getDefinition()).append("\n");
            if (showSynonyms)
                stringBuilder.append(englishDef.getSynonyms()).append("\n");
            if (showExamples)
                stringBuilder.append(englishDef.getExamples()).append("\n");
        }
        return stringBuilder.toString();
    }

    private String getTranslationData(boolean showExamples, List<WordInformation> wordList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (WordInformation wordInformation : wordList) {
            for (TranslationWord translationWord : wordInformation.getTranslationWords()) {
                stringBuilder.append("*").append(translationWord.getTranslatedWord()).append("\n");
                if (translationWord.getTranslationExamples() != null) {
                    if (showExamples) {
                        for (TranslationExample translationExample :
                                translationWord.getTranslationExamples()) {
                            stringBuilder.append(translationExample.getSentence()).append("\n");
                        }
                    }
                }

            }
        }
        return stringBuilder.toString();
    }

    private EditText engEditText() {
        View engView = mBinding.viewPagerAddNewLeitner.findViewWithTag(getString(R.string.enf_def));
        if (engView != null) {
            return engView.findViewById(R.id.edt_pager_leitner_info);
        }
        return null;
    }

    private EditText translationEdtText() {
        View translationView = mBinding.viewPagerAddNewLeitner.findViewWithTag(getString(R.string.translation));
        if (translationView != null) {
            return translationView.findViewById(R.id.edt_pager_leitner_info);
        }
        return null;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
