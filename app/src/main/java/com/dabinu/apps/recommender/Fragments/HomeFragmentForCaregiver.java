package com.dabinu.apps.recommender.Fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabinu.apps.recommender.Activities.HomeActivityCaregiver;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;


public class HomeFragmentForCaregiver extends android.app.Fragment {

    CardView profile, my_patients, my_physicians, my_community;
    FirebaseAuth mAuth;

    public HomeFragmentForCaregiver(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_home_for_caregiver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        profile = getView().findViewById(R.id.profileTag);
        my_patients = getView().findViewById(R.id.caregiverPatientTag);
        my_physicians = getView().findViewById(R.id.caregiverPhyTag);
        my_community = getView().findViewById(R.id.caregiverCommunityTag);

        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_caregiver, new ProfileForCaregiver(), "Caregivers");
                fragmentTransaction.commit();
                ((HomeActivityCaregiver) getActivity()).getSupportActionBar().setTitle("Profile");
            }
        });

        my_patients.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utils.list_view_tag = "Caregiver";
                Utils.list_to_display_tag = "Patients";
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_caregiver, new UsersList(), "Caregivers");
                fragmentTransaction.commit();
                ((HomeActivityCaregiver) getActivity()).getSupportActionBar().setTitle("My Patients");
            }
        });

        my_physicians.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.list_view_tag = "Caregiver";
                Utils.list_to_display_tag = "Physicians";
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_caregiver, new UsersList(), "Caregivers");
                fragmentTransaction.commit();
                ((HomeActivityCaregiver) getActivity()).getSupportActionBar().setTitle("My Physicians");
            }
        });

        my_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_caregiver, new CommunitiesForCaregiver(), "Caregivers");
                fragmentTransaction.commit();
                ((HomeActivityCaregiver) getActivity()).getSupportActionBar().setTitle("Communities");
            }
        });

    }
}
