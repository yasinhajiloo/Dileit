package com.example.dileit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WordDefinition {
    @SerializedName("g")
    private String type;
    @SerializedName("ss")
    private List<TranslationWord> translationWords;
    @SerializedName("p")
    private String pronunciation;
    @SerializedName("is")
    private List<Object> idioms;


    public WordDefinition(String type, List<TranslationWord> translationWords, String pronunciation, List<Object> idioms) {
        this.type = type;
        this.translationWords = translationWords;
        this.pronunciation = pronunciation;
        this.idioms = idioms;
    }

    public String getType() {
        return type;
    }

    public List<TranslationWord> getTranslationWords() {
        return translationWords;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public List<Object> getIdioms() {
        return idioms;
    }
}
