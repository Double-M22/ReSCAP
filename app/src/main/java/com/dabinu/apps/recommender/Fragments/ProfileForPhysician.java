package com.dabinu.apps.recommender.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.dabinu.apps.recommender.Activities.ProfileCompleteForPhysician;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ProfileForPhysician extends android.app.Fragment {


    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    TextView name, email, location, license, affiliation, unique_id, dob;
    TextView specialization;
    ProgressBar progressBar;
    ListView listView;


    public ProfileForPhysician() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_profile_for_physician, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();

        progressBar = getView().findViewById(R.id.progressBar);
        name = getView().findViewById(R.id.nameField);
        email = getView().findViewById(R.id.emailField);
        location = getView().findViewById(R.id.locationField);
        specialization = getView().findViewById(R.id.specializationField);
        affiliation = getView().findViewById(R.id.affiliationField);
        license = getView().findViewById(R.id.licenseField);
        unique_id = getView().findViewById(R.id.uniqueIdField);
        listView = getView().findViewById(R.id.specializationFiledItems);
        dob = getView().findViewById(R.id.dobField);

        mCheckAndSetValues(mAuth, databaseReference, activity);

        Button button = getView().findViewById(R.id.add_specialization);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dayalog);
                dialog.setTitle("Add Specialization");
                final ListView listView = dialog.findViewById(R.id.List);
                listView.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.populate, R.layout.show));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        final String communityIntent = ((String) listView.getItemAtPosition(position));
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                PhysicianTree physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                                ArrayList<String> specialization = physicianTree.getListOfSpecialization();
                                if (specialization.contains(communityIntent)){
                                    Toast.makeText(getActivity(), "It is already part of your specialization", Toast.LENGTH_SHORT).show();
                                }else {
                                    specialization.add(communityIntent);
                                    if (specialization.contains("null"))
                                        specialization.remove(specialization.indexOf("null"));

                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfSpecialization").setValue(specialization);
                                    Toast.makeText(getActivity(), "Specialization added!", Toast.LENGTH_SHORT).show();
                                }
                                dialog.hide();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                    }
                });

                dialog.show();
            }
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

        new ProfileForPhysician().mReadDataOnce(new MyPersonalListener(){

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot){
                progressBar.setVisibility(View.GONE);

                PhysicianTree userTree = dataSnapshot.getValue(PhysicianTree.class);

                name.setText(userTree.getName());
                email.setText(userTree.getEmail());
                license.setText(userTree.getLicense());
                unique_id.setText(userTree.getUniqueId());
                dob.setText(userTree.getDateOfBirth());
                if(userTree.getListOfSpecialization() != null){
                    ArrayAdapter<String> harry = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, userTree.getListOfSpecialization());
                    listView.setAdapter(harry);
                    listView.setVisibility(View.VISIBLE);
                    specialization.setVisibility(View.GONE);
                }else {
                    specialization.setVisibility(View.VISIBLE);
                    specialization.setText("null");
                }
                affiliation.setText(userTree.getAffiliation());
                location.setText(userTree.getLocation());

                boolean hereafter = false;
                final ArrayList<String> lifeAfterDeath = new ArrayList<>();

                if(license.getText().toString().trim().equals("not set yet")){
                    hereafter = true;
                    lifeAfterDeath.add("LICENSE");
                }
                if(userTree.getListOfSpecialization() == null){
                    hereafter = true;
                    lifeAfterDeath.add("SPECIALIZATION");
                }
                if(affiliation.getText().toString().trim().equals("not set yet")){
                    hereafter = true;
                    lifeAfterDeath.add("AFFILIATION");
                }
                if(location.getText().toString().trim().equals("not set yet")){
                    hereafter = true;
                    lifeAfterDeath.add("LOCATION");
                }

                if(hereafter){
                    new AlertDialog.Builder(activity)
                            .setTitle("Your profile is incomplete, do you want to fill it up now?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity().getApplicationContext(), ProfileCompleteForPhysician.class).putExtra("DETAILS", lifeAfterDeath));
                                }
                            })
                            .setNegativeButton("Later", null)
                            .setCancelable(false)
                            .show();
                }
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

}