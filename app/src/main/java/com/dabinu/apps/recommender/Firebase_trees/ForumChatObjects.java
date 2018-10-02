package com.dabinu.apps.recommender.Firebase_trees;

public class ForumChatObjects {

    private String sanderName;
    private String message;
    private String time;
    private String date;
    private int chatPerDay;

    public ForumChatObjects(String sanderName, String message, String time, String date, int chatPerDay) {
        this.sanderName = sanderName;
        this.message = message;
        this.time = time;
        this.date = date;
    }

    public ForumChatObjects(){ }

    public String getSanderName() {
        return sanderName;
    }

    public void setSanderName(String sanderName) {
        this.sanderName = sanderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getChatPerDay() {
        return chatPerDay;
    }

    public void setChatPerDay(int chatPerDay) {
        this.chatPerDay = chatPerDay;
    }

}
