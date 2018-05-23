package com.AppDriver.roomdb;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "word_table")
public class Word {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;

    private Double mLat;
    private Double mLng;

    public Word(@NonNull String word, Double lat, Double lng) {
        mWord = word;
        mLat = lat;
        mLng = lng;
    }

    @NonNull
    public String getWord() {
        return mWord;
    }

    public Double getLat() {
        return mLat;
    }

    public Double getLng() {
        return mLng;
    }}