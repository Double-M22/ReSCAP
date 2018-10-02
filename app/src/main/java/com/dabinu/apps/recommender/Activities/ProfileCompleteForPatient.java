package com.dabinu.apps.recommender.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class ProfileCompleteForPatient extends AppCompatActivity{


    TextView skip, next;
    LinearLayout locationLayout, conditionLayout;
    Spinner locationSpinner, conditionSpinner;
    int i = 0;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_complete_for_patient);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        locationLayout = findViewById(R.id.locationLayout);
        conditionLayout = findViewById(R.id.conditionLayout);

        locationSpinner = findViewById(R.id.spinnerForLocation);
        locationSpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.countries, android.R.layout.simple_spinner_item));
        conditionSpinner = findViewById(R.id.spinnerForSpecialization);
        conditionSpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.expertise, android.R.layout.simple_spinner_item));

        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);

        final ArrayList<String> details = getIntent().getStringArrayListExtra("DETAILS");

        switch(details.get(i)){
            case "LOCATION":
                locationLayout.setVisibility(View.VISIBLE);
                conditionLayout.setVisibility(View.GONE);
                break;
            case "CONDITION":
                locationLayout.setVisibility(View.GONE);
                conditionLayout.setVisibility(View.VISIBLE);
                break;
        }



        skip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ++i;
                if(i == details.size()){
                    new AlertDialog.Builder(ProfileCompleteForPatient.this)
                            .setMessage("Complete!")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
                                }
                            })
                            .show();
                }
                else{
                    switch(details.get(i)){
                        case "LOCATION":
                            locationLayout.setVisibility(View.VISIBLE);
                            conditionLayout.setVisibility(View.GONE);
                            break;
                        case "CONDITION":
                            locationLayout.setVisibility(View.GONE);
                            conditionLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(details.get(i)){
                    case "LOCATION":
                        databaseReference.child(mAuth.getCurrentUser().getUid()).child("location").setValue(((String) locationSpinner.getSelectedItem()));
                        break;
                    case "CONDITION":
                        ArrayList<String> s = new ArrayList<>();
                        s.add(((String) conditionSpinner.getSelectedItem()));
                        databaseReference.child(mAuth.getCurrentUser().getUid()).child("listOfConditions").setValue(s);
                        break;
                }

                ++i;

                if(i == details.size()){
                    new AlertDialog.Builder(ProfileCompleteForPatient.this)
                            .setMessage("Complete!")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
                                }
                            })
                            .show();
                }
                else{
                    switch(details.get(i)){
                        case "LOCATION":
                            locationLayout.setVisibility(View.VISIBLE);
                            conditionLayout.setVisibility(View.GONE);
                            break;
                        case "CONDITION":
                            locationLayout.setVisibility(View.GONE);
                            conditionLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });
    }
}
