package com.dabinu.apps.recommender.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ProfileCompleteForPhysician extends AppCompatActivity{

    TextView skip, next;
    LinearLayout locationLayout, specializationLayout, licenseLayout, affiliationLayout;
    Spinner locationSpinner, specializationSpinner;
    EditText licenseEdit, affiliationEdit;
    int i = 0;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_complete_for_physician);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        locationLayout = findViewById(R.id.locationLayout);
        specializationLayout = findViewById(R.id.specializationLayout);
        licenseLayout = findViewById(R.id.licenseLayout);
        affiliationLayout = findViewById(R.id.affiliationLayout);

        locationSpinner = findViewById(R.id.spinnerForLocation);
        locationSpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.countries, android.R.layout.simple_spinner_item));
        specializationSpinner = findViewById(R.id.spinnerForSpecialization);
        specializationSpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.expertise, android.R.layout.simple_spinner_item));

        licenseEdit = findViewById(R.id.editTextForLicense);
        affiliationEdit = findViewById(R.id.editTextForAffiliation);

        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);

        final ArrayList<String> details = getIntent().getStringArrayListExtra("DETAILS");

        switch(details.get(i)){
            case "AFFILIATION":
                locationLayout.setVisibility(View.GONE);
                specializationLayout.setVisibility(View.GONE);
                licenseLayout.setVisibility(View.GONE);
                affiliationLayout.setVisibility(View.VISIBLE);
                break;
            case "SPECIALIZATION":
                locationLayout.setVisibility(View.GONE);
                specializationLayout.setVisibility(View.VISIBLE);
                licenseLayout.setVisibility(View.GONE);
                affiliationLayout.setVisibility(View.GONE);
                break;
            case "LICENSE":
                locationLayout.setVisibility(View.GONE);
                specializationLayout.setVisibility(View.GONE);
                licenseLayout.setVisibility(View.VISIBLE);
                affiliationLayout.setVisibility(View.GONE);
                break;
        }



        skip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ++i;
                if(i == details.size()){
                    new AlertDialog.Builder(ProfileCompleteForPhysician.this)
                            .setMessage("Complete!")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), HomeActivityPhysician.class));
                                    ProfileCompleteForPhysician.this.finish();
                                }
                            })
                            .show();
                }
                else{
                    switch(details.get(i)){
                        case "AFFILIATION":
                            locationLayout.setVisibility(View.GONE);
                            specializationLayout.setVisibility(View.GONE);
                            licenseLayout.setVisibility(View.GONE);
                            affiliationLayout.setVisibility(View.VISIBLE);
                            break;
                        case "SPECIALIZATION":
                            locationLayout.setVisibility(View.GONE);
                            specializationLayout.setVisibility(View.VISIBLE);
                            licenseLayout.setVisibility(View.GONE);
                            affiliationLayout.setVisibility(View.GONE);
                            break;
                        case "LICENSE":
                            locationLayout.setVisibility(View.GONE);
                            specializationLayout.setVisibility(View.GONE);
                            licenseLayout.setVisibility(View.VISIBLE);
                            affiliationLayout.setVisibility(View.GONE);
                            break;
                    }
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(details.get(i)){
                    case "SPECIALIZATION":
                        ArrayList<String> s = new ArrayList<>();
                        s.add(((String) specializationSpinner.getSelectedItem()));
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfSpecialization").setValue(s);
                        break;
                    case "LICENSE":
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("license").setValue(licenseEdit.getText().toString());
                        break;
                    case "AFFILIATION":
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("affiliation").setValue(affiliationEdit.getText().toString());
                        break;
                }

                ++i;

                if(i == details.size()){
                    new AlertDialog.Builder(ProfileCompleteForPhysician.this)
                            .setMessage("Complete!")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), HomeActivityPhysician.class));
                                    ProfileCompleteForPhysician.this.finish();
                                }
                            })
                            .show();
                }
                else{
                    switch(details.get(i)){
                        case "AFFILIATION":
                            locationLayout.setVisibility(View.GONE);
                            specializationLayout.setVisibility(View.GONE);
                            licenseLayout.setVisibility(View.GONE);
                            affiliationLayout.setVisibility(View.VISIBLE);
                            break;

                        case "SPECIALIZATION":
                            locationLayout.setVisibility(View.GONE);
                            specializationLayout.setVisibility(View.VISIBLE);
                            licenseLayout.setVisibility(View.GONE);
                            affiliationLayout.setVisibility(View.GONE);
                            break;

                        case "LICENSE":
                            locationLayout.setVisibility(View.GONE);
                            specializationLayout.setVisibility(View.GONE);
                            licenseLayout.setVisibility(View.VISIBLE);
                            affiliationLayout.setVisibility(View.GONE);
                            break;

                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ProfileCompleteForPhysician.this.finish();
    }
}