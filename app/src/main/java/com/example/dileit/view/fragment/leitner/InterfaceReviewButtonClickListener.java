package com.example.dileit.view.fragment.leitner;

import com.example.dileit.model.entity.Leitner;

public interface InterfaceReviewButtonClickListener {
    void onYesClicked();
    void onNoClicked();
    void onBritishPronounce(String word);
    void onAmericanPronounce(String word);
}
