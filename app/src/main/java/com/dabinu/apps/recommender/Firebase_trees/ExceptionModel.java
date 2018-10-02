package com.dabinu.apps.recommender.Firebase_trees;


public class ExceptionModel{

    public String userName, issue, time, date, email;

    public ExceptionModel(String userName, String email, String issue, String date, String time) {
        this.userName = userName;
        this.issue = issue;
        this.time = time;
        this.date = date;
        this.email = email;
    }

    public ExceptionModel(){}

    public String getUserName() {
        return userName;
    }

    public String getCause() {
        return issue;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }
}
