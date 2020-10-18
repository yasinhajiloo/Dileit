package com.yasinhajilou.dileit.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.yasinhajilou.dileit.model.dao.LeitnerDao;
import com.yasinhajilou.dileit.model.dao.WordHistoryDao;
import com.yasinhajilou.dileit.model.dao.WordReviewHistoryDao;
import com.yasinhajilou.dileit.model.entity.Leitner;
import com.yasinhajilou.dileit.model.entity.WordHistory;
import com.yasinhajilou.dileit.model.entity.WordReviewHistory;

@Database(entities = {Leitner.class, WordHistory.class, WordReviewHistory.class}, version = 1, exportSchema = false)
public abstract class InternalRoomDatabase extends RoomDatabase {
    public abstract WordHistoryDao mWordHistoryDao();

    public abstract LeitnerDao mLeitnerDao();

    public abstract WordReviewHistoryDao mWordReviewHistoryDao();

    private static InternalRoomDatabase INSTANCE;

    public static InternalRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (InternalRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, InternalRoomDatabase.class, "Dileit_DB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
