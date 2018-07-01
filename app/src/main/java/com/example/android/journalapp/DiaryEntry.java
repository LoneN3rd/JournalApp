package com.example.android.journalapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Diary") //define the table name here
public class DiaryEntry {

    @PrimaryKey(autoGenerate = true) //define id as the primary key
    private int id;
    private String description;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public DiaryEntry(String description, Date updatedAt) {
        this.description = description;
        this.updatedAt = updatedAt;
    }

    public DiaryEntry(int id, String description, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
