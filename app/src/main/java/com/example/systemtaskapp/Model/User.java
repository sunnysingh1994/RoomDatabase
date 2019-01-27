package com.example.systemtaskapp.Model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

@Entity(tableName = "users")
public class User {

   @NonNull
   @PrimaryKey(autoGenerate = true)


    private int id;

   @ColumnInfo(name = "name")
    private String name;

   @ColumnInfo(name = "mobile")
    private String mobile;

    public User() {
    }

    @Ignore
    public User(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Override
    public String toString() {
        return new StringBuilder(name).append("\n").append(mobile).toString();
    }
}
