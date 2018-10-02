package com.dabinu.apps.recommender.Firebase_trees;

import java.util.ArrayList;

public class PatientTree{

    private String name, email, type, location, dateOfBirth, consent, uniqueId, canUseId;
    private ArrayList<String> listOfConditions, severities,listOfCommunities;
    private ArrayList<PhysicianSecTree> myPhysicians;
    private ArrayList<UsersSecTree> myCareGivers;

    public PatientTree(String name, String email, String type, String location, String dateOfBirth, String consent,
                       String uniqueId, String canUseId, ArrayList<String> listOfConditions, ArrayList<String> severities,
                       ArrayList<String> listOfCommunities, ArrayList<PhysicianSecTree> myPhysicians,
                       ArrayList<UsersSecTree> myCareGivers){
        this.name = name;
        this.email = email;
        this.type = type;
        this.uniqueId = uniqueId;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.consent = consent;
        this.listOfConditions = listOfConditions;
        this.severities = severities;
        this.listOfCommunities = listOfCommunities;
        this.myPhysicians = myPhysicians;
        this.myCareGivers = myCareGivers;
        this.canUseId = canUseId;
    }

    public PatientTree(){ }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getConsent() {
        return consent;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getCanUseId() {
        return canUseId;
    }

    public ArrayList<String> getListOfConditions() {
        return listOfConditions;
    }

    public ArrayList<String> getSeverities() {
        return severities;
    }

    public ArrayList<String> getListOfCommunities() {
        return listOfCommunities;
    }

    public ArrayList<PhysicianSecTree> getMyPhysicians() {
        return myPhysicians;
    }

    public ArrayList<UsersSecTree> getMyCareGivers() {
        return myCareGivers;
    }
}