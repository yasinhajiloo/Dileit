package com.example.dileit.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dileit.model.entity.WordReviewHistory;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface WordReviewHistoryDao {
    @Insert
    Completable insert(WordReviewHistory wordReviewHistory);

    @Query("SELECT * FROM WordReviewHistory WHERE reviewedTime BETWEEN :start AND :end")
    Flowable<List<WordReviewHistory>> LIST_FLOWABLE(long start, long end);

    @Query("SELECT COUNT(id)  FROM WordReviewHistory")
    Flowable<Integer> LIST();

    @Query("SELECT COUNT(id) FROM WordReviewHistory WHERE reviewedTime BETWEEN :start AND :end")
    Maybe<Integer> getReviewedCardsCountByTimeRange(long start, long end);

    @Query("Delete FROM WordReviewHistory WHERE cardTitle LIKE :cardTitle")
    Completable deleteByCardTitle(String cardTitle);
}
