package com.dabinu.apps.recommender.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Adapter.PhyCaregiversListAdapter;
import com.dabinu.apps.recommender.Firebase_trees.CaregiverTree;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.Firebase_trees.PhyCaregivers;
import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.Firebase_trees.UsersSecTree;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PhysicianCaregiverList extends Fragment implements PhyCaregiversListAdapter.PhyCaregiversListClickListener{

    private RecyclerView caregiverListItem;
    private TextView my_text;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private PhysicianTree physicianTree;
    private String ownUniqueId;
    private ArrayList<PhyCaregivers>  myCaregivers;
    private PhyCaregiversListAdapter adapter;

    public PhysicianCaregiverList() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_physician_caregiver_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        my_text = getActivity().findViewById(R.id.physician_caregiver_empty_text);
        caregiverListItem = getActivity().findViewById(R.id.physician_caregiver_list_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        caregiverListItem.setLayoutManager(layoutManager);
        caregiverListItem.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final Button delegate = getView().findViewById(R.id.delegate_caregiver);

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                ownUniqueId = physicianTree.getUniqueId();
                myCaregivers = physicianTree.getMyCareGivers();

                delegate.setOnClickListener((view1) -> delegateCaregiver());

                if(myCaregivers != null){
                    if(!myCaregivers.isEmpty()) {
                        caregiverListItem.setVisibility(View.VISIBLE);
                        my_text.setVisibility(View.GONE);
                        adapter = new PhyCaregiversListAdapter(myCaregivers, getActivity().getApplicationContext(), PhysicianCaregiverList.this);
                        caregiverListItem.setAdapter(adapter);

                    }else{
                        my_text.setVisibility(View.VISIBLE);
                    }
                }else {
                    my_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void delegateCaregiver(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dayalog);
        dialog.setTitle("Community");
        final ListView listView = dialog.findViewById(R.id.List);
        listView.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.populate, R.layout.show));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            final String communityIntent = ((String) listView.getItemAtPosition(position));
            dialog.hide();

            boolean isPartOfCommunity = false;
            if(physicianTree.getListOfCommunities() != null){
                for(String community : physicianTree.getListOfCommunities()){
                    if(community.equals(communityIntent)){
                        isPartOfCommunity = true;
                        break;
                    }else
                        isPartOfCommunity = false;
                }

                if(isPartOfCommunity){

                    mDatabase.child("ReSCAP Caregivers").child(communityIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final ArrayList<PhyCaregivers> phyCaregivers = getData(dataSnapshot);
                            final ArrayList<UsersSecTree> caregivers = new ArrayList<>();

                            if(!phyCaregivers.isEmpty()) {
                                ArrayList<String> carers = new ArrayList<>();
                                for (PhyCaregivers phyCaregiver : phyCaregivers) {
                                    String userName = phyCaregiver.getName();
                                    String uniqueId = phyCaregiver.getUniqueId();
                                    String combo = uniqueId+" | "+userName;
                                    carers.add(combo);

                                    String userId = phyCaregiver.getUserId();
                                    String illness = phyCaregiver.getIllness();
                                    UsersSecTree user = new UsersSecTree(userName, uniqueId, userId, illness);
                                    caregivers.add(user);
                                }

                                final Dialog dialog1 = new Dialog(getActivity());
                                dialog1.setContentView(R.layout.dayalog);
                                dialog1.setTitle("Caregivers");
                                final ListView listView1 = dialog1.findViewById(R.id.List);
                                listView1.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.show, carers));
                                listView1.setOnItemClickListener((adapterView, view12, i, l) -> {
                                    final PhyCaregivers phyCaregivers1 = phyCaregivers.get(i);
                                    final UsersSecTree patientCaregiver = caregivers.get(i);

                                    dialog1.hide();

                                    final ArrayList<PhyPatient> phyPatients = physicianTree.getMyPatients();
                                    if(phyPatients != null){
                                        ArrayList<String> patients = new ArrayList<>();
                                        for(PhyPatient phyPatient : phyPatients){
                                            String patientName = phyPatient.getName();
                                            String patientId = phyPatient.getUniqueId();
                                            String combo = "To : "+patientId+" | "+patientName;
                                            patients.add(combo);
                                        }

                                        final Dialog dialog2 = new Dialog(getActivity());
                                        dialog2.setContentView(R.layout.dayalog);
                                        dialog2.setTitle("My Patients");
                                        final ListView listView2 = dialog2.findViewById(R.id.List);
                                        listView2.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.show, patients));
                                        listView2.setOnItemClickListener((adapterView1, view1, i1, l1) -> {
                                            final PhyPatient patient = phyPatients.get(i1);
                                            dialog2.hide();

                                            final UsersSecTree patientSec = new UsersSecTree(patient.getName(), patient.getUniqueId(),
                                                    patient.getUserId(), communityIntent);
                                            final UsersSecTree physicianSec = new UsersSecTree(physicianTree.getName(), physicianTree.getUniqueId(),
                                                    mAuth.getCurrentUser().getUid(), communityIntent);

                                            myCaregivers = physicianTree.getMyCareGivers();
                                            if(myCaregivers == null)
                                                myCaregivers = new ArrayList<>();

                                            mDatabase.child("users").child(phyCaregivers1.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                                    CaregiverTree caregiverTree1 = dataSnapshot1.getValue(CaregiverTree.class);
                                                    ArrayList<UsersSecTree> myPatients = caregiverTree1.getMyPatients();
                                                    ArrayList<UsersSecTree> myPhysicians = caregiverTree1.getMyPhysicians();
                                                    ArrayList<String> myCommunities = caregiverTree1.getListOfCommunities();
                                                    if(myPatients == null)
                                                        myPatients = new ArrayList<>();
                                                    if(myPhysicians == null)
                                                        myPhysicians = new ArrayList<>();
                                                    if(myCommunities == null)
                                                        myCommunities = new ArrayList<>();

                                                    boolean hasCommunity = false;
                                                    if(myCommunities.contains(communityIntent))
                                                        hasCommunity = true;
                                                    else
                                                        myCommunities.add(communityIntent);

                                                    boolean isDel = false;
                                                    if(!myPatients.isEmpty()) {
                                                        for (UsersSecTree myPatient : myPatients) {
                                                            if (myPatient.getUniqueId().equals(patientSec.getUniqueId())) {
                                                                if(!hasCommunity)
                                                                    Toast.makeText(getActivity().getApplicationContext(), "Caregiver already delegated to patient",
                                                                            Toast.LENGTH_SHORT).show();

                                                                isDel = true;
                                                                break;
                                                            } else
                                                                isDel = false;
                                                        }
                                                    }else
                                                        isDel = false;

                                                    boolean isPartOfMyCaregiver = false;
                                                    if(!isDel){
                                                        for(PhyCaregivers myCaregiver : myCaregivers){
                                                            if(myCaregiver.getUniqueId().equals(phyCaregivers1.getUniqueId())){
                                                                isPartOfMyCaregiver = true;
                                                                break;
                                                            }else
                                                                isPartOfMyCaregiver = false;
                                                        }

                                                        if(!isPartOfMyCaregiver){
                                                            ArrayList<UsersSecTree> prev_care_pa = phyCaregivers1.getCaregiversPatients();
                                                            if(prev_care_pa == null)
                                                                prev_care_pa = new ArrayList<>();
                                                            prev_care_pa.add(patientSec);
                                                            phyCaregivers1.setCaregiversPatients(prev_care_pa);
                                                            myCaregivers.add(phyCaregivers1);
                                                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                                                                    .child("MyCareGivers").setValue(myCaregivers);

                                                            caregiverListItem.setVisibility(View.VISIBLE);
                                                            my_text.setVisibility(View.GONE);
                                                            adapter = new PhyCaregiversListAdapter(myCaregivers, getActivity().getApplicationContext(), PhysicianCaregiverList.this);
                                                            caregiverListItem.setAdapter(adapter);
                                                        }

                                                        myPatients.add(patientSec);
                                                        myPhysicians.add(physicianSec);
                                                        mDatabase.child("users").child(phyCaregivers1.getUserId()).child("myPatients").setValue(myPatients);
                                                        mDatabase.child("users").child(phyCaregivers1.getUserId()).child("myPhysicians").setValue(myPhysicians);

                                                        if(!hasCommunity)
                                                            mDatabase.child("users").child(phyCaregivers1.getUserId()).child("listOfCommunities").setValue(myCommunities);

                                                        mDatabase.child("users").child(patient.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                                PatientTree patientTree1 = dataSnapshot1.getValue(PatientTree.class);
                                                                ArrayList<UsersSecTree> myCaregivers = patientTree1.getMyCareGivers();
                                                                if(myCaregivers == null)
                                                                    myCaregivers = new ArrayList<>();

                                                                myCaregivers.add(patientCaregiver);
                                                                mDatabase.child("users").child(patient.getUserId()).child("myCareGivers").setValue(myCaregivers);
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) { }
                                                        });
                                                    }else if(!hasCommunity){
                                                        //todo add to community here
                                                        mDatabase.child("users").child(phyCaregivers1.getUserId()).child("listOfCommunities").setValue(myCommunities);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) { }
                                            });

                                        });
                                        dialog2.show();

                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(), "You have no Patients yet!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog1.show();

                            }else{
                                Toast.makeText(getActivity().getApplicationContext(), "No Caregivers yet!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                }else
                    Toast.makeText(getActivity().getApplicationContext(), "You are not part of the selected community", Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(getActivity().getApplicationContext(), "You are not part of the selected community", Toast.LENGTH_LONG).show();
        });
        dialog.show();

    }

    private ArrayList<PhyCaregivers> getData(DataSnapshot dataSnapshot){
        ArrayList<PhyCaregivers> myUsers = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PhyCaregivers myData = ds.getValue(PhyCaregivers.class);
                myUsers.add(myData);
            }
        }
        return myUsers;
    }

    @Override
    public void onPhyCaregiversListClicked(View view) {
        int tag = (int)view.getTag();
        final PhyCaregivers caregiver = myCaregivers.get(tag);
        final String chat_tag = ownUniqueId + "_" + caregiver.getUniqueId();

        new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to chat with " + caregiver.getName())
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Utils.community_type = chat_tag;

                    FragmentManager fragmentManager = getFragmentManager();
                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                        fragmentManager.popBackStack();
                    }
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_main_physician, new CommunityMessenger(), "messenger");
                    fragmentTransaction.commit();
                    ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle(caregiver.getName());
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }
}
