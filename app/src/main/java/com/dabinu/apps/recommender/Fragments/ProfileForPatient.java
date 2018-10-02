package com.dabinu.apps.recommender.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Activities.ProfileCompleteForPatient;
import com.dabinu.apps.recommender.Activities.ProfileCompleteForPhysician;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class ProfileForPatient extends android.app.Fragment {


    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    TextView name, email, location, condition, dob, user_id;
    ProgressBar progressBar;
    private AlertDialog.Builder builder;


    public ProfileForPatient(){ }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_profile_for_patient, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(getActivity());

        progressBar = getView().findViewById(R.id.progressBar);
        name = getView().findViewById(R.id.nameField);
        email = getView().findViewById(R.id.emailField);
        location = getView().findViewById(R.id.locationField);
        condition = getView().findViewById(R.id.conditionField);
        dob = getView().findViewById(R.id.dobField);
        user_id = getView().findViewById(R.id.user_idField);

        mCheckAndSetValues(mAuth, databaseReference);

    }


    public interface MyPersonalListener{
        public void onStart();
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
    }

    private void mReadDataOnce(final ProfileForPatient.MyPersonalListener listener, DatabaseReference databaseReference, FirebaseAuth mAuth){
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

    private void mCheckAndSetValues(FirebaseAuth mAut, DatabaseReference databaseReferenc){

        new ProfileForPatient().mReadDataOnce(new MyPersonalListener(){

            @Override
            public void onStart(){
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot){
                progressBar.setVisibility(View.GONE);

                PatientTree userTree = dataSnapshot.getValue(PatientTree.class);


                if (userTree != null) {
                    name.setText(userTree.getName());
                    email.setText(userTree.getEmail());
                    if(userTree.getListOfConditions() != null)
                        condition.setText(userTree.getListOfConditions().get(0));
                    else
                        condition.setText("null");
                    location.setText(userTree.getLocation());
                    dob.setText(userTree.getDateOfBirth());
                    if (userTree.getCanUseId().equals("YES"))
                        user_id.setText(userTree.getUniqueId());
                    else user_id.setText("null");
                }else{
                    Log.d("ProfileForPatient", "Men this Tree didnt fetch oo!");
                }

                boolean hereafter = false;
                final ArrayList<String> lifeAfterDeath = new ArrayList<>();

                if(condition.getText().toString().trim().equals("null")){
                    hereafter = true;
                    lifeAfterDeath.add("CONDITION");
                }
                if(location.getText().toString().trim().equals("not set yet")){
                    hereafter = true;
                    lifeAfterDeath.add("LOCATION");
                }
                if(hereafter){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Your profile is incomplete, do you want to fill it up now?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity().getApplicationContext(), ProfileCompleteForPatient.class).putExtra("DETAILS", lifeAfterDeath));
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

                new AlertDialog.Builder(getActivity())
                        .setTitle("Check your internet connection")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPatient.class));
                            }
                        })
                        .show();
            }
        }, databaseReferenc, mAut);

    }
}
