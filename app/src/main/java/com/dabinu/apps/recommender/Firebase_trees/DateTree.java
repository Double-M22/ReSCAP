package com.dabinu.apps.recommender.Firebase_trees;

public class DateTree {
    private String Date;
    private int Count;

    public DateTree(){}

    public DateTree(String date, int count) {
        Date = date;
        Count = count;
    }

    public int getCount() {
        return Count;
    }

    public String getDate() {
        return Date;
    }
}
