package com.dabinu.apps.recommender.Fragments;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragmentForPhysician extends android.app.Fragment{

    CardView profile, community, my_patients, my_caregivers;
    FirebaseAuth mAuth;

    public HomeFragmentForPhysician(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_home_for_physician, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        profile = getView().findViewById(R.id.profileTag);
        community = getView().findViewById(R.id.communitiesTag);
        my_patients = getView().findViewById(R.id.patientTag);
        my_caregivers = getView().findViewById(R.id.caregiverTag);

        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_physician, new ProfileForPhysician(), "Physician");
                fragmentTransaction.commit();
                ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle("Profile");
            }
        });

        community.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_physician, new CommunitiesForPhysician(), "Physician");
                fragmentTransaction.commit();
                ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle("Communities");
            }
        });

        my_patients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_physician, new PhysicianPatientList(), "Physician");
                fragmentTransaction.commit();
                ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle("My Patients");
            }
        });

        my_caregivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.list_view_tag = "Physician";
                Utils.list_to_display_tag = "Caregivers";
                FragmentManager fragmentManager = getFragmentManager();
                if(getFragmentManager().getBackStackEntryCount() != 0){
                    fragmentManager.popBackStack();
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main_physician, new UsersList(), "Physician");
                fragmentTransaction.commit();
                ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle("My Caregivers");
            }
        });

        checkAndSet();
    }

    private void checkAndSet(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PhysicianTree physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                ArrayList<PhyPatient> phyPatients = physicianTree.getMyPatients();
                if(phyPatients != null) {
                    for (PhyPatient phyPatient : phyPatients) {
                        if (phyPatient.getPhyResponse() == 0) {
                            showDialogForUnRespondedToPatient();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void showDialogForUnRespondedToPatient(){
        new AlertDialog.Builder(getActivity())
                .setMessage("You have patients waiting for your response!")
                .setCancelable(false)
                .setPositiveButton("Respond", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager fragmentManager = getFragmentManager();
                        if(getFragmentManager().getBackStackEntryCount() != 0){
                            fragmentManager.popBackStack();
                        }
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_main_physician, new PhysicianPatientList(), "Physician");
                        fragmentTransaction.commit();
                        ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle("My Patients");
                    }
                })
                .setNegativeButton("Ignore", null)
                .show();
    }

}
