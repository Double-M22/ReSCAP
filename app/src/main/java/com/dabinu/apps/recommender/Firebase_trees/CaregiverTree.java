package com.dabinu.apps.recommender.Firebase_trees;

import java.util.ArrayList;

public class CaregiverTree{

    private String name, email, type, location, dateOfBirth, uniqueId;
    private ArrayList<String> listOfCommunities, listOfSpecialization;
    private ArrayList<UsersSecTree> myPhysicians;
    private ArrayList<UsersSecTree> myPatients;

    public CaregiverTree(String name, String email, String type, String location, String dateOfBirth,
                         String uniqueId, ArrayList<String> listOfCommunities, ArrayList<String> listOfSpecialization,
                         ArrayList<UsersSecTree> myPhysicians, ArrayList<UsersSecTree> myPatients){

        this.name = name;
        this.email = email;
        this.type = type;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.uniqueId = uniqueId;
        this.listOfCommunities = listOfCommunities;
        this.listOfSpecialization = listOfSpecialization;
        this.myPatients = myPatients;
        this.myPhysicians = myPhysicians;

    }

    public CaregiverTree(){
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getType(){
        return type;
    }

    public String getLocation(){
        return location;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public ArrayList<String> getListOfCommunities() {
        return listOfCommunities;
    }

    public ArrayList<String> getListOfSpecialization() {
        return listOfSpecialization;
    }

    public ArrayList<UsersSecTree> getMyPhysicians() {
        return myPhysicians;
    }

    public ArrayList<UsersSecTree> getMyPatients() {
        return myPatients;
    }

}
