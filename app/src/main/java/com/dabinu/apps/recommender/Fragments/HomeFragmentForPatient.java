package com.dabinu.apps.recommender.Fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.AuthClasses.LoginActivity;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragmentForPatient extends android.app.Fragment {

    CardView profile, console, community, doctors, caregivers;
    FirebaseAuth mAuth;

    public HomeFragmentForPatient(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_home_for_patient, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final View layout_view = getView();
        dbRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PatientTree userTree = dataSnapshot.getValue(PatientTree.class);
                TextView user_id = layout_view.findViewById(R.id.patient_user_id);
                String user_code;
                if(userTree != null) {
                    if(userTree.getCanUseId().equals("YES")) {
                        user_code = "User ID : " + userTree.getUniqueId();
                        user_id.setText(user_code);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        profile = getView().findViewById(R.id.profileTag);
        console = getView().findViewById(R.id.consoleTag);
        community = getView().findViewById(R.id.communitiesTag);
        caregivers = getView().findViewById(R.id.patient_caregiverTag);
        doctors = getView().findViewById(R.id.patient_phy_tag);


        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_patient, new ProfileForPatient());
                fragmentTransaction.commit();
            }
        });


//        doctors.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getFragmentManager();
//                if(getFragmentManager().getBackStackEntryCount() != 0){
//                    fragmentManager.popBackStack();
//                }
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.content_main_patient, new DoctorsForMe());
//                fragmentTransaction.commit();
//            }
//        });
//
//
//        community.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getFragmentManager();
//                if(getFragmentManager().getBackStackEntryCount() != 0){
//                    fragmentManager.popBackStack();
//                }
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.content_main_patient, new CommunitiesForPatient());
//                fragmentTransaction.commit();
//            }
//        });

        //Todo mike addition
        community.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_patient, new CommunitiesForPatient(), "Patient");
                fragmentTransaction.commit();
                ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle("Communities");
            }
        });

        doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.list_view_tag = "Patient";
                Utils.list_to_display_tag = "Physicians";
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_patient, new UsersList(), "Patient");
                fragmentTransaction.commit();
                ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle("My Physicians");
            }
        });

        caregivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.list_view_tag = "Patient";
                Utils.list_to_display_tag = "Caregivers";
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_patient, new UsersList(), "Patient");
                fragmentTransaction.commit();
                ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle("My Caregivers");
            }
        });
    }
}
