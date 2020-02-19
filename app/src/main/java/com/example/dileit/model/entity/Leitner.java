package com.example.dileit.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Leitner {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int state;

    private int repeatCounter;

    private long lastReviewTime;

    private long timeAdded;

    public Leitner(int id, int state, int repeatCounter, long lastReviewTime, long timeAdded) {
        this.id = id;
        this.state = state;
        this.repeatCounter = repeatCounter;
        this.lastReviewTime = lastReviewTime;
        this.timeAdded = timeAdded;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public int getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    public int getRepeatCounter() {
        return repeatCounter;
    }

    public long getLastReviewTime() {
        return lastReviewTime;
    }
}
