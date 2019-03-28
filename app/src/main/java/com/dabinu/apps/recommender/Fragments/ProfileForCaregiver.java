package com.dabinu.apps.recommender.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Firebase_trees.CaregiverTree;
import com.dabinu.apps.recommender.Firebase_trees.PhyCaregivers;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.Firebase_trees.UsersSecTree;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

//Todo create unique id when specialization is selected add the uniqueId created to the the ReSCAP Caregivers

public class ProfileForCaregiver extends android.app.Fragment {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference, uniqueIdRef;
    TextView name, email, location, unique_id_text, dob;
    TextView specialization;
    ProgressBar progressBar;
    ListView listView;
    private CaregiverTree userTree;
    private ArrayList<String> specializationList = new ArrayList<>();
    private ArrayAdapter<String> adapter = null;

    public ProfileForCaregiver() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_profile_for_cargiver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        uniqueIdRef = databaseReference.child("UniqueIds");
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();

        progressBar = getView().findViewById(R.id.progressBar);
        name = getView().findViewById(R.id.nameField);
        email = getView().findViewById(R.id.emailField);
        location = getView().findViewById(R.id.locationField);
        specialization = getView().findViewById(R.id.specializationField);
        unique_id_text = getView().findViewById(R.id.uniqueIdField);
        listView = getView().findViewById(R.id.specializationFiledItems);
        dob = getView().findViewById(R.id.dobField);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, specializationList);
        listView.setAdapter(adapter);

        mCheckAndSetValues(mAuth, databaseReference, activity);

        Button button = getView().findViewById(R.id.add_specialization);
        button.setOnClickListener(view12 -> {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dayalog);
            dialog.setTitle("Add Specialization");
            final ListView listView = dialog.findViewById(R.id.List);
            listView.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.populate, R.layout.show));
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                final String communityIntent = ((String) listView.getItemAtPosition(position));
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PhysicianTree physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                        if (physicianTree != null && physicianTree.getListOfSpecialization() != null) {

                            specializationList.clear();
                            specializationList.addAll(physicianTree.getListOfSpecialization());

                            if(!specializationList.isEmpty()) {
                                if (specializationList.contains(communityIntent)) {
                                    Toast.makeText(getActivity(), "It is already part of your specialization", Toast.LENGTH_SHORT).show();
                                } else {
                                    specializationList.add(communityIntent);
                                    if (specializationList.contains("null"))
                                        specializationList.remove(specializationList.indexOf("null"));
                                    if(userTree.getUniqueId().equals("null"))
                                        formUniqueId(communityIntent);
                                    else {
                                        PhyCaregivers caregiverSecTree = new PhyCaregivers(userTree.getName(), userTree.getUniqueId(),
                                                mAuth.getCurrentUser().getUid(), communityIntent, userTree.getMyPatients());
                                        databaseReference.child("ReSCAP Caregivers").child(communityIntent).child(userTree.getUniqueId())
                                                .setValue(caregiverSecTree);
                                    }

                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfSpecialization").setValue(specializationList);
                                    Toast.makeText(getActivity(), "Specialization added!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else{
                            specializationList.add(communityIntent);
                            formUniqueId(communityIntent);

                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfSpecialization").setValue(specializationList);
                            Toast.makeText(getActivity(), "Specialization added!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.hide();
                        listView.setVisibility(View.VISIBLE);
//                                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, specializationList);
                        adapter.notifyDataSetChanged();
                        specialization.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            });

            dialog.show();
        });

    }



    public interface MyPersonalListener{
        public void onStart();
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
    }

    private void mReadDataOnce(final MyPersonalListener listener, DatabaseReference databaseReference, FirebaseAuth mAuth){
        listener.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                listener.onFailed(databaseError);
            }
        });
    }

    private void mCheckAndSetValues(FirebaseAuth mAut, DatabaseReference databaseReferenc, final Activity activity){

        new ProfileForCaregiver().mReadDataOnce(new MyPersonalListener(){

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot){
                progressBar.setVisibility(View.GONE);

                userTree = dataSnapshot.getValue(CaregiverTree.class);

                name.setText(userTree.getName());
                email.setText(userTree.getEmail());
                unique_id_text.setText(userTree.getUniqueId());
                dob.setText(userTree.getDateOfBirth());
                if(userTree.getListOfSpecialization() != null){
//                    specializationList = userTree.getListOfSpecialization();
                    specializationList.addAll(userTree.getListOfSpecialization());
                    listView.setVisibility(View.VISIBLE);
//                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, specializationList);
                    adapter.notifyDataSetChanged();
                    specialization.setVisibility(View.GONE);
                }else {
                    specialization.setVisibility(View.VISIBLE);
                    specialization.setText("null");
                }
                location.setText(userTree.getLocation());
            }

            @Override
            public void onFailed(DatabaseError databaseError){
                progressBar.setVisibility(View.GONE);

                new AlertDialog.Builder(activity)
                        .setTitle("Check your internet connection")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPhysician.class));
                            }
                        })
                        .show();
            }
        }, databaseReferenc, mAut);

    }

    String code_start, mid_code;
    String code_number = "0000";
    String unique_id = null;
    private void formUniqueId(final String comm){

        code_start = userTree.getLocation().substring(userTree.getLocation().length()-3,
                userTree.getLocation().length()-1);
        mid_code = comm.substring(comm.length()-3, comm.length()-1);

        uniqueIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Caregiver")) {
                    uniqueIdRef.child("Caregiver").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(userTree.getLocation())){
                                uniqueIdRef.child("Caregiver").child(userTree.getLocation()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(comm)){
                                            uniqueIdRef.child("Caregiver").child(userTree.getLocation()).child(comm).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    ArrayList<String> ids = getDataList(dataSnapshot);
                                                    int id_count = ids.size();
                                                    unique_id = null;
                                                    if(!ids.isEmpty()){
                                                        boolean isUnique = false;
                                                        Random r = new Random();
                                                        Set<Integer> uniqueNumbers = new HashSet<>();

                                                        while (uniqueNumbers.size() < 9999) {
                                                            uniqueNumbers.add(r.nextInt(9999));
                                                            int random = 0;
                                                            for (int i : uniqueNumbers) {
                                                                random = i;
                                                            }
                                                            if (random < 10 && random > 0)
                                                                code_number = "000" + random;
                                                            else if (random < 100 && random > 9)
                                                                code_number = "00" + random;
                                                            else if (random < 1000 && random > 99)
                                                                code_number = "0" + random;
                                                            else if (random > 1000)
                                                                code_number = "" + random;
                                                            unique_id = code_start+"C"+mid_code+"V"+code_number;
                                                            for (String id : ids) {
                                                                if (id.equals(unique_id)) {
                                                                    isUnique = false;
                                                                    break;
                                                                } else isUnique = true;
                                                            }
                                                            if (isUnique)
                                                                break;
                                                        }
                                                        if (isUnique) {
                                                            UsersSecTree caregiverSecTree = new UsersSecTree(userTree.getName(), unique_id,
                                                                    mAuth.getCurrentUser().getUid(), comm);
                                                            databaseReference.child("ReSCAP Caregivers").child(comm)
                                                                    .child(unique_id)
                                                                    .setValue(caregiverSecTree);
                                                            uniqueIdRef.child("Caregiver").child(userTree.getLocation()).child(comm)
                                                                    .child(""+id_count).setValue(unique_id);
                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                                    .child("uniqueId").setValue(unique_id);
                                                        }

                                                    }else {
                                                        unique_id = code_start+"C"+mid_code+"V"+code_number;
                                                        UsersSecTree caregiverSecTree = new UsersSecTree(userTree.getName(), unique_id,
                                                                mAuth.getCurrentUser().getUid(), comm);
                                                        databaseReference.child("ReSCAP Caregivers").child(comm)
                                                                .child(unique_id)
                                                                .setValue(caregiverSecTree);
                                                        uniqueIdRef.child("Caregiver").child(userTree.getLocation()).child(comm)
                                                                .child("0").setValue(unique_id);
                                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                                .child("uniqueId").setValue(unique_id);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) { }
                                            });

                                        } else {
                                            unique_id = code_start+"C"+mid_code+"V"+code_number;
                                            UsersSecTree caregiverSecTree = new UsersSecTree(userTree.getName(), unique_id,
                                                    mAuth.getCurrentUser().getUid(), comm);
                                            databaseReference.child("ReSCAP Caregivers").child(comm).child(unique_id).setValue(caregiverSecTree);
                                            uniqueIdRef.child("Caregiver").child(userTree.getLocation()).child(comm)
                                                    .child("0").setValue(unique_id);
                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                    .child("uniqueId").setValue(unique_id);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });

                            } else {
                                unique_id = code_start+"C"+mid_code+"V"+code_number;
                                UsersSecTree caregiverSecTree = new UsersSecTree(userTree.getName(), unique_id,
                                        mAuth.getCurrentUser().getUid(), comm);
                                databaseReference.child("ReSCAP Caregivers").child(comm).child(unique_id).setValue(caregiverSecTree);
                                uniqueIdRef.child("Caregiver").child(userTree.getLocation()).child(comm)
                                        .child("0").setValue(unique_id);
                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                        .child("uniqueId").setValue(unique_id);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                }else {
                    unique_id = code_start+"C"+mid_code+"V"+code_number;
                    UsersSecTree caregiverSecTree = new UsersSecTree(userTree.getName(), unique_id,
                            mAuth.getCurrentUser().getUid(), comm);
                    databaseReference.child("ReSCAP Caregivers").child(comm).child(unique_id).setValue(caregiverSecTree);
                    uniqueIdRef.child("Caregiver").child(userTree.getLocation()).child(comm)
                            .child("0").setValue(unique_id);
                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                            .child("uniqueId").setValue(unique_id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public ArrayList<String> getDataList(DataSnapshot dataSnapshot){
        ArrayList<String> ids = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String myData = (String) ds.getValue();
                ids.add(myData);
            }
        }
        return ids;
    }

}