package com.dabinu.apps.recommender.Firebase_trees;

public class PhyPatient {
    private String uniqueId;
    private String userId;
    private String name;
    private String illness;
    private String severity;
    private int phyResponse;

    public PhyPatient(){}

    public String getUniqueId() {
        return uniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName(){
        return name;
    }

    public int getPhyResponse() {
        return phyResponse;
    }

    public String getIllness(){
        return illness;
    }

    public String getSeverity() {
        return severity;
    }

    public PhyPatient(String name, String illness, String severity, String uniqueId, String userId, int phyResponse) {

        this.illness = illness;
        this.severity = severity;
        this.name = name;
        this.uniqueId = uniqueId;
        this.userId = userId;
        this.phyResponse = phyResponse;

    }
}
