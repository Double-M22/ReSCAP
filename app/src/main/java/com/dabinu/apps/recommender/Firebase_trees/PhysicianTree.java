package com.dabinu.apps.recommender.Firebase_trees;

import java.util.ArrayList;

public class PhysicianTree {

    private String name, email, type, location, dateOfBirth, consent, license, affiliation, uniqueId;
    private ArrayList<String> listOfSpecialization, listOfCommunities;
    private ArrayList<PhyPatient> MyPatients;
    private ArrayList<UsersSecTree> MyCareGivers;

    public PhysicianTree(String name, String email, String type, String location, String dateOfBirth, String consent,
                         String uniqueId, String license, String affiliation, ArrayList<String> listOfSpecializations,
                         ArrayList<String> listOfCommunities, ArrayList<PhyPatient> MyPatients, ArrayList<UsersSecTree> MyCareGivers){

        this.name = name;
        this.email = email;
        this.type = type;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.consent = consent;
        this.uniqueId = uniqueId;
        this.license = license;
        this.affiliation = affiliation;
        this.listOfCommunities = listOfCommunities;
        this.listOfSpecialization = listOfSpecializations;
        this.MyPatients = MyPatients;
        this.MyCareGivers = MyCareGivers;

    }

    public PhysicianTree(){
    }

    public ArrayList<UsersSecTree> getMyCareGivers() {
        return MyCareGivers;
    }

    public ArrayList<PhyPatient> getMyPatients() {
        return MyPatients;
    }

    public String getUniqueId() {
        return uniqueId;
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

    public String getLicense() {
        return license;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public ArrayList<String> getListOfSpecialization() {
        return listOfSpecialization;
    }

    public ArrayList<String> getListOfCommunities() {
        return listOfCommunities;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

}
