package com.dabinu.apps.recommender.Firebase_trees;

public class UsersSecTree {
    private String name;
    private String uniqueId;
    private String userId;
    private String illness;

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public String getIllness() {
        return illness;
    }

    public UsersSecTree(String name, String uniqueId, String userId, String illness) {

        this.name = name;
        this.uniqueId = uniqueId;
        this.userId = userId;
        this.illness = illness;
    }

    public UsersSecTree() {

    }
}
