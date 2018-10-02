package com.dabinu.apps.recommender.Firebase_trees;

public class PhysicianSecTree {
    private String name;
    private String uniqueId;
    private String userId;
    private String specialization;
    private String isConfirmed;

    public PhysicianSecTree(String name, String uniqueId, String userId, String specialization, String isConfirmed) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.userId = userId;
        this.specialization = specialization;
        this.isConfirmed = isConfirmed;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public PhysicianSecTree(){

    }
}
