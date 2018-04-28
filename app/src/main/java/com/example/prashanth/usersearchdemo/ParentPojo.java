package com.example.prashanth.usersearchdemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParentPojo {

    @SerializedName("total_count")
    @Expose
    private String count;

    @SerializedName("items")
    @Expose
    private ArrayList<Pojo> list = null;

    public String getCount() {
        return count;
    }

    public void setCount(String name) {
        this.count = name;
    }

    public ArrayList<Pojo> getList() {
        return list;
    }

    public void setList(ArrayList<Pojo> list) {
        this.list = list;
    }
}