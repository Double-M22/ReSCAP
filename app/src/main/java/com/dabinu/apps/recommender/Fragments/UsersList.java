package com.dabinu.apps.recommender.Fragments;

import android.app.AlertDialog;
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
import android.widget.TextView;

import com.dabinu.apps.recommender.Activities.HomeActivityCaregiver;
import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Adapter.UsersListAdapter;
import com.dabinu.apps.recommender.Firebase_trees.CaregiverTree;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianSecTree;
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
    private CaregiverTree caregiverTree;

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
                            if(myPhysicians != null && !myPhysicians.isEmpty()) {
                                for (PhysicianSecTree physician : myPhysicians) {
                                    UsersSecTree usersSecTree = new UsersSecTree(physician.getName(), physician.getUniqueId(), physician.getUserId(),
                                            physician.getSpecialization());
                                    usersSecTrees.add(usersSecTree);
                                }
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
        }

        if(Utils.list_view_tag.equals("Patient") && Utils.list_to_display_tag.equals("Physicians")){
            new AlertDialog.Builder(getActivity())
                    .setMessage("Do you want to chat with "+ usersSecTree.getName())
                    .setCancelable(false)
                    .setNeutralButton("Private", (dialog, which) -> {
                        Utils.community_type = chat_tag;

                        FragmentManager fragmentManager = getFragmentManager();
                        if(getFragmentManager().getBackStackEntryCount() != 0){
                            fragmentManager.popBackStack();
                        }
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(home_layout_to_replace, new CommunityMessenger(), "messenger");
                        fragmentTransaction.commit();
                        ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle(usersSecTree.getName());

                    })
                    .setNegativeButton("Public", (dialogInterface, i) -> {
                        Utils.community_type = usersSecTree.getUniqueId()+"_myPatients";

                        FragmentManager fragmentManager = getFragmentManager();
                        if(getFragmentManager().getBackStackEntryCount() != 0){
                            fragmentManager.popBackStack();
                        }
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(home_layout_to_replace, new CommunityMessenger(), "messenger");
                        fragmentTransaction.commit();
                        ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle(usersSecTree.getName()+" Patients");
                    })
                    .setPositiveButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        }else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Do you want to chat with " + usersSecTree.getName())
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Utils.community_type = chat_tag;

                        FragmentManager fragmentManager = getFragmentManager();
                        if (getFragmentManager().getBackStackEntryCount() != 0) {
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
                        }
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        }
    }
}
