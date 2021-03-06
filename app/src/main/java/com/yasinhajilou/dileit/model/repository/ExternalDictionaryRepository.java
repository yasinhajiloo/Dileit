package com.yasinhajilou.dileit.model.repository;

import android.app.Application;
import android.database.Cursor;
import android.util.Log;

import com.yasinhajilou.dileit.model.EnglishDef;
import com.yasinhajilou.dileit.model.SearchDictionary;
import com.yasinhajilou.dileit.model.database.engdb.EnglishDatabaseAccess;
import com.yasinhajilou.dileit.model.database.persiandb.PersianDatabaseAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class ExternalDictionaryRepository {
    private EnglishDatabaseAccess mEnglishDatabaseAccess;
    private PersianDatabaseAccess mPersianDatabaseAccess;

    public ExternalDictionaryRepository(Application application) {
        mEnglishDatabaseAccess = EnglishDatabaseAccess.getINSTANCE(application);
        mPersianDatabaseAccess = PersianDatabaseAccess.getINSTANCE(application);
    }

    //search for list of data
    public Flowable<List<SearchDictionary>> doEngSearch(String word) {
        return Flowable.fromCallable(() -> {
            mEnglishDatabaseAccess.openDatabase();
            List<SearchDictionary> wordEnglishDics = new ArrayList<>();

            Cursor cursor = mEnglishDatabaseAccess.getDatabase().rawQuery("SELECT _idref,word FROM keys WHERE word like ? LIMIT 75", new String[]{word + "%"});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int refrenceId = cursor.getInt(0);
                    String actualWord = cursor.getString(1).trim();
                    SearchDictionary englishDic = new SearchDictionary(actualWord, refrenceId);
                    wordEnglishDics.add(englishDic);
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            return wordEnglishDics;
        })
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<SearchDictionary>> doPersianSearch(String word) {
        return Flowable.fromCallable(() -> {
            // ORDER BY word COLLATE NOCASE ASC
            mPersianDatabaseAccess.openDatabase();
            List<SearchDictionary> wordsList = new ArrayList<>();
            Cursor cursor = mPersianDatabaseAccess.getDatabase().rawQuery("SELECT word from dictionary WHERE word LIKE ? ORDER BY LOWER(word) LIMIT 75 ", new String[]{word + "%"});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String actualWord = cursor.getString(0).trim();
                    SearchDictionary searchDictionary = new SearchDictionary(actualWord, 0);
                    wordsList.add(searchDictionary);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return wordsList;
        }).debounce(1800, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
    }

    //getting data for word information view
    public Flowable<List<EnglishDef>> getRefById(int engId) {
        return Flowable.fromCallable(() -> {
            mEnglishDatabaseAccess.openDatabase();
            List<EnglishDef> englishDefs = new ArrayList<>();

            Cursor cursor = mEnglishDatabaseAccess.getDatabase().rawQuery("SELECT definition,category,synonyms,examples,antonyms,similar FROM description WHERE _id like ?", new String[]{String.valueOf(engId)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String def = cursor.getString(0);
                    String cat = cursor.getString(1);
                    String syn = cursor.getString(2);
                    String exam = cursor.getString(3);
                    String antonyms = cursor.getString(4);
                    String similar = cursor.getString(5);
                    EnglishDef englishDic = new EnglishDef(cat, def, syn, exam, similar, antonyms);
                    englishDefs.add(englishDic);
                }
            }
            mEnglishDatabaseAccess.closeDatabase();
            if (cursor != null) {
                cursor.close();
            }
            return englishDefs;
        }).debounce(1800, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<String> getExactWord(String word) {
        return Flowable.fromCallable(() -> {
            mPersianDatabaseAccess.openDatabase();
            Cursor cursor = mPersianDatabaseAccess.getDatabase().rawQuery("SELECT def from dictionary WHERE word LIKE ?  ", new String[]{word});
            String data = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    data = cursor.getString(0);
                }
            }

            if (cursor != null)
                cursor.close();
            mPersianDatabaseAccess.closeDatabase();
            return data;
        }).onErrorReturnItem("Error")
                .subscribeOn(Schedulers.io());
    }

    public void closeExternalDatabases() {
        mEnglishDatabaseAccess.closeDatabase();
        mPersianDatabaseAccess.closeDatabase();
    }
}
