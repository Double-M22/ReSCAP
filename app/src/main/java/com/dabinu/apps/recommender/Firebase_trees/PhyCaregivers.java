package com.dabinu.apps.recommender.Firebase_trees;

import java.util.ArrayList;

public class PhyCaregivers {
    private String uniqueId;
    private String userId;
    private String name;
    private String illness;
    private ArrayList<UsersSecTree> caregiversPatients;

    public PhyCaregivers(){}

    public PhyCaregivers(String uniqueId, String userId, String name, String illness, ArrayList<UsersSecTree> caregiversPatients) {
        this.uniqueId = uniqueId;
        this.userId = userId;
        this.name = name;
        this.illness = illness;
        this.caregiversPatients = caregiversPatients;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getIllness() {
        return illness;
    }

    public void setCaregiversPatients(ArrayList<UsersSecTree> caregiversPatients) {
        this.caregiversPatients = caregiversPatients;
    }

    public ArrayList<UsersSecTree> getCaregiversPatients() {
        return caregiversPatients;
    }
}
