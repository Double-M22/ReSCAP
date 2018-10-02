package com.dabinu.apps.recommender.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityCaregiver;
import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Adapter.UsersListAdapter;
import com.dabinu.apps.recommender.Firebase_trees.CaregiverTree;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianSecTree;
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

public class UsersList extends android.app.Fragment implements UsersListAdapter.UsersListClickListener{

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private ArrayList<UsersSecTree> usersSecTrees = new ArrayList<>();
    private UsersListAdapter usersListAdapter;
    private RecyclerView users_list;
    private String ownUniqueId;
    private PatientTree patientTree;
    private PhysicianTree physicianTree;
    private CaregiverTree caregiverTree;

    private ArrayList<UsersSecTree> physicianCaregivers;

    public UsersList() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        users_list = getActivity().findViewById(R.id.users_list_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        users_list.setLayoutManager(layoutManager);
        users_list.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String tag = Utils.list_view_tag;

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch (tag) {
                    case "Patient": {
                        patientTree = dataSnapshot.getValue(PatientTree.class);
                        ownUniqueId = patientTree.getUniqueId();
                        if (Utils.list_to_display_tag.equals("Caregivers")) {
                            usersSecTrees = patientTree.getMyCareGivers();
                        } else if (Utils.list_to_display_tag.equals("Physicians")) {
                            ArrayList<PhysicianSecTree> myPhysicians = patientTree.getMyPhysicians();
                            for (PhysicianSecTree physician : myPhysicians) {
                                UsersSecTree usersSecTree = new UsersSecTree(physician.getName(), physician.getUniqueId(), physician.getUserId(),
                                        physician.getSpecialization());
                                usersSecTrees.add(usersSecTree);
                            }
                        }
                        break;
                    }
                    case "Caregiver": {
                        caregiverTree = dataSnapshot.getValue(CaregiverTree.class);
                        ownUniqueId = caregiverTree.getUniqueId();
                        if (Utils.list_to_display_tag.equals("Patients")) {
                            usersSecTrees = caregiverTree.getMyPatients();
                        } else if (Utils.list_to_display_tag.equals("Physicians")) {
                            usersSecTrees = caregiverTree.getMyPhysicians();
                        }
                        break;
                    }
                    case "Physician": {
                        physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                        ownUniqueId = physicianTree.getUniqueId();
                        usersSecTrees = physicianTree.getMyCareGivers();
                        Button delegate = getView().findViewById(R.id.delegate_caregiver);
                        delegate.setVisibility(View.VISIBLE);
                        delegate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.dayalog);
                                dialog.setTitle("Community");
                                final ListView listView = dialog.findViewById(R.id.List);
                                listView.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.populate, R.layout.show));
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                    @Override
                                    public void onItemClick(final AdapterView<?> parent, View view, int position, long id){
                                        final String communityIntent = ((String) listView.getItemAtPosition(position));

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
                                                        final ArrayList<UsersSecTree> caregivers = getData(dataSnapshot);
                                                        dialog.hide();

                                                        if(!caregivers.isEmpty()) {
                                                            ArrayList<String> carers = new ArrayList<>();
                                                            for (UsersSecTree user : caregivers) {
                                                                String userName = user.getName();
                                                                String userId = user.getUniqueId();
                                                                String combo = userId+" | "+userName;
                                                                carers.add(combo);
                                                            }

                                                            final Dialog dialog1 = new Dialog(getActivity());
                                                            dialog1.setContentView(R.layout.dayalog);
                                                            dialog1.setTitle("Caregivers");
                                                            final ListView listView1 = dialog1.findViewById(R.id.List);
                                                            listView1.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.show, carers));
                                                            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                    final UsersSecTree caregiverSec = caregivers.get(i);
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
                                                                        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                            @Override
                                                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                final PhyPatient patient = phyPatients.get(i);
                                                                                dialog2.hide();

                                                                                final UsersSecTree patientSec = new UsersSecTree(patient.getName(), patient.getUniqueId(),
                                                                                        patient.getUserId(), communityIntent);
                                                                                final UsersSecTree physicianSec = new UsersSecTree(physicianTree.getName(), physicianTree.getUniqueId(),
                                                                                        mAuth.getCurrentUser().getUid(), communityIntent);

                                                                                physicianCaregivers = physicianTree.getMyCareGivers();
                                                                                if(physicianCaregivers == null)
                                                                                    physicianCaregivers = new ArrayList<>();

                                                                                mDatabase.child("users").child(caregiverSec.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        CaregiverTree caregiverTree1 = dataSnapshot.getValue(CaregiverTree.class);
                                                                                        ArrayList<UsersSecTree> myPatients = caregiverTree1.getMyPatients();
                                                                                        ArrayList<UsersSecTree> myPhysician = caregiverTree1.getMyPhysicians();
                                                                                        if(myPatients == null)
                                                                                            myPatients = new ArrayList<>();
                                                                                        if(myPhysician == null)
                                                                                            myPhysician = new ArrayList<>();

                                                                                        boolean isDel = false;
                                                                                        if(!myPatients.isEmpty()) {
                                                                                            for (UsersSecTree myPatient : myPatients) {
                                                                                                if (myPatient.getUniqueId().equals(patientSec.getUniqueId())) {
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
                                                                                            for(UsersSecTree physicianCaregiver : physicianCaregivers){
                                                                                                if(physicianCaregiver.getUniqueId().equals(caregiverSec.getUniqueId())){
                                                                                                    isPartOfMyCaregiver = true;
                                                                                                    break;
                                                                                                }else
                                                                                                    isPartOfMyCaregiver = false;
                                                                                            }

                                                                                            if(!isPartOfMyCaregiver){
                                                                                                physicianCaregivers.add(caregiverSec);
                                                                                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                                                                                                        .child("MyCareGivers").setValue(physicianCaregivers);

                                                                                                users_list.setVisibility(View.VISIBLE);
                                                                                                usersListAdapter = new UsersListAdapter(physicianCaregivers, UsersList.this);
                                                                                                users_list.setAdapter(usersListAdapter);
                                                                                            }

                                                                                            myPatients.add(patientSec);
                                                                                            myPhysician.add(physicianSec);
                                                                                            mDatabase.child("users").child(caregiverSec.getUserId()).child("myPatients").setValue(myPatients);
                                                                                            mDatabase.child("users").child(caregiverSec.getUserId()).child("myPhysicians").setValue(myPhysician);

                                                                                            mDatabase.child("users").child(patient.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    PatientTree patientTree1 = dataSnapshot.getValue(PatientTree.class);
                                                                                                    ArrayList<UsersSecTree> myCaregivers = patientTree1.getMyCareGivers();
                                                                                                    if(myCaregivers == null)
                                                                                                        myCaregivers = new ArrayList<>();

                                                                                                    myCaregivers.add(caregiverSec);
                                                                                                    mDatabase.child("users").child(patient.getUserId()).child("myCareGivers").setValue(myCaregivers);
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(DatabaseError databaseError) { }
                                                                                            });
                                                                                        }

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) { }
                                                                                });

                                                                            }

                                                                        });
                                                                        dialog2.show();

                                                                    }else{
                                                                        Toast.makeText(getActivity().getApplicationContext(), "You have no Patients yet!", Toast.LENGTH_SHORT).show();
                                                                    }
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
                                    }
                                });
                                dialog.show();

                            }
                        });
                        break;
                    }
                }

                if(usersSecTrees != null){
                    if(!usersSecTrees.isEmpty()) {
                        users_list.setVisibility(View.VISIBLE);
                        usersListAdapter = new UsersListAdapter(usersSecTrees, UsersList.this);
                        users_list.setAdapter(usersListAdapter);

                    }else{
                        TextView my_text = getActivity().findViewById(R.id.user_list_page_empty);
                        my_text.setVisibility(View.VISIBLE);
                    }
                }else {
                    TextView my_text = getActivity().findViewById(R.id.user_list_page_empty);
                    my_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private ArrayList<UsersSecTree> getData(DataSnapshot dataSnapshot){
        ArrayList<UsersSecTree> myUsers = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                UsersSecTree myData = ds.getValue(UsersSecTree.class);
                myUsers.add(myData);
            }
        }
        return myUsers;
    }

    private String chat_tag;
    int home_layout_to_replace;
    @Override
    public void onUsersListClicked(View view) {
        int tag = (int)view.getTag();
        final UsersSecTree usersSecTree = usersSecTrees.get(tag);

        switch (Utils.list_view_tag) {
            case "Patient": {
                chat_tag = usersSecTree.getUniqueId() + "_" + ownUniqueId;
                home_layout_to_replace = R.id.content_main_patient;
                break;
            }
            case "Caregiver": {
                home_layout_to_replace = R.id.content_main_caregiver;
                if (Utils.list_to_display_tag.equals("Patients")) {
                    chat_tag = ownUniqueId + "_" + usersSecTree.getUniqueId();
                } else if (Utils.list_to_display_tag.equals("Physicians")) {
                    chat_tag = usersSecTree.getUniqueId() + "_" + ownUniqueId;
                }
                break;
            }
            case "Physician": {
                home_layout_to_replace = R.id.content_main_physician;
                chat_tag = ownUniqueId + "_" + usersSecTree.getUniqueId();
                break;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to chat with "+ usersSecTree.getName())
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.community_type = chat_tag;

                        FragmentManager fragmentManager = getFragmentManager();
                        if(getFragmentManager().getBackStackEntryCount() != 0){
                            fragmentManager.popBackStack();
                        }
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(home_layout_to_replace, new CommunityMessenger(), "messenger");
                        fragmentTransaction.commit();
                        switch (Utils.list_view_tag) {
                            case "Patient":
                                ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle(usersSecTree.getName());
                                break;
                            case "Caregiver":
                                ((HomeActivityCaregiver) getActivity()).getSupportActionBar().setTitle(usersSecTree.getName());
                                break;
                            case "Physician":
                                ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle(usersSecTree.getName());
                                break;
                        }
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
