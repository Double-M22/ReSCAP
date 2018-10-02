package com.dabinu.apps.recommender.Fragments;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Adapter.PhyPatientListAdapter;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianSecTree;
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
import java.util.Collections;

public class PhysicianPatientList extends android.app.Fragment implements PhyPatientListAdapter.PhyPatientListClickListener{

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private ArrayList<PhyPatient> phyPatients;
    private PhysicianTree physicianTree;
    private PhyPatientListAdapter phyPatientListAdapter;
    private RecyclerView my_patients_list;

    public PhysicianPatientList() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_physician_patient_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                phyPatients = physicianTree.getMyPatients();

                if(phyPatients != null){

                    my_patients_list = getActivity().findViewById(R.id.physician_patient_list_item);
                    my_patients_list.setVisibility(View.VISIBLE);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    my_patients_list.setLayoutManager(layoutManager);
                    my_patients_list.setHasFixedSize(true);
                    phyPatientListAdapter = new PhyPatientListAdapter(getActivity(), phyPatients, PhysicianPatientList.this);
                    my_patients_list.setAdapter(phyPatientListAdapter);

                }else {
                    TextView my_text = getActivity().findViewById(R.id.physician_patient_empty_text);
                    my_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    @Override
    public void onPhyPatientListClicked(View view) {
        int tag = (int)view.getTag();
        final PhyPatient patient = phyPatients.get(tag);

        if(patient.getPhyResponse() == 0){
            new AlertDialog.Builder(getActivity())
                    .setMessage("Patient request")
                    .setCancelable(false)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PhyPatient newPatient = new PhyPatient(patient.getName(), patient.getIllness(),
                                    patient.getSeverity(), patient.getUniqueId(), patient.getUserId(), 1);

                            phyPatients.add(newPatient);
                            Collections.swap(phyPatients, phyPatients.indexOf(patient), phyPatients.indexOf(newPatient));
                            phyPatients.remove(phyPatients.indexOf(patient));

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("MyPatients").setValue(phyPatients);
                            mDatabase.child("users").child(patient.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    PatientTree patientTree = dataSnapshot.getValue(PatientTree.class);
                                    ArrayList<PhysicianSecTree> physicianSecTrees = patientTree.getMyPhysicians();
                                    for(PhysicianSecTree physicianSecTree : physicianSecTrees){
                                        if(physicianSecTree.getUniqueId().equals(physicianTree.getUniqueId())){
                                            PhysicianSecTree physician = new PhysicianSecTree(physicianSecTree.getName(), physicianSecTree.getUniqueId(),
                                                    physicianSecTree.getUserId(), physicianSecTree.getSpecialization(), "YES");
                                            physicianSecTrees.add(physician);
                                            Collections.swap(physicianSecTrees, physicianSecTrees.indexOf(physician), physicianSecTrees.indexOf(physicianSecTree));
                                            physicianSecTrees.remove(physicianSecTrees.indexOf(physicianSecTree));
                                            break;
                                        }
                                    }
                                    PatientTree myPatient = new PatientTree(patientTree.getName(), patientTree.getEmail(), patientTree.getType(), patientTree.getLocation(),
                                            patientTree.getDateOfBirth(), patientTree.getConsent(), patientTree.getUniqueId(), patientTree.getCanUseId(), patientTree.getListOfConditions(),
                                            patientTree.getSeverities(), patientTree.getListOfCommunities(), physicianSecTrees, patientTree.getMyCareGivers());
                                    mDatabase.child("users").child(patient.getUserId()).setValue(myPatient);
                                    phyPatientListAdapter = new PhyPatientListAdapter(getActivity(), phyPatients, PhysicianPatientList.this);
                                    my_patients_list.setAdapter(phyPatientListAdapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });

                        }
                    })
                    .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PhyPatient newPatient = new PhyPatient(patient.getName(), patient.getIllness(),
                                    patient.getSeverity(), patient.getUniqueId(), patient.getUserId(), 2);

                            phyPatients.add(newPatient);
                            Collections.swap(phyPatients, phyPatients.indexOf(patient), phyPatients.indexOf(newPatient));
                            phyPatients.remove(phyPatients.indexOf(patient));

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("MyPatients").setValue(phyPatients);
                            phyPatientListAdapter = new PhyPatientListAdapter(getActivity(), phyPatients, PhysicianPatientList.this);
                            my_patients_list.setAdapter(phyPatientListAdapter);
                        }
                    })
                    .show();
        }else if(patient.getPhyResponse() == 1){
            new AlertDialog.Builder(getActivity())
                    .setMessage("Do you want to chat with "+ patient.getName())
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.community_type = physicianTree.getUniqueId() + "_" + patient.getUniqueId();

                            FragmentManager fragmentManager = getFragmentManager();
                            if(getFragmentManager().getBackStackEntryCount() != 0){
                                fragmentManager.popBackStack();
                            }
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.content_main_patient, new CommunityMessenger(), "messenger");
                            fragmentTransaction.commit();
                            ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle(patient.getName());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
    }
}
