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
    LinearLayout conditionLayout, consentLayout;
    Spinner conditionSpinner, consentSpinner;
    int i = 0;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_complete_for_patient);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        conditionLayout = findViewById(R.id.conditionLayout);
        conditionSpinner = findViewById(R.id.spinnerForCondition);
        consentLayout = findViewById(R.id.consentLayout);
        consentSpinner = findViewById(R.id.spinnerForConsent);
        conditionSpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.conditions, android.R.layout.simple_spinner_item));
        consentSpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.consents, android.R.layout.simple_spinner_item));

        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);

        final ArrayList<String> details = getIntent().getStringArrayListExtra("DETAILS");

        switch(details.get(i)){
            case "CONDITION":
                consentLayout.setVisibility(View.GONE);
                conditionLayout.setVisibility(View.VISIBLE);
                break;
            case "CONSENT":
                conditionLayout.setVisibility(View.GONE);
                consentLayout.setVisibility(View.VISIBLE);
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
                                    ProfileCompleteForPatient.this.finish();
                                }
                            })
                            .show();
                }
                else{
                    switch(details.get(i)){
                        case "CONDITION":
                            consentLayout.setVisibility(View.GONE);
                            conditionLayout.setVisibility(View.VISIBLE);
                            break;
                        case "CONSENT":
                            conditionLayout.setVisibility(View.GONE);
                            consentLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(details.get(i)){
                    case "CONDITION":
                        ArrayList<String> s = new ArrayList<>();
                        s.add(((String) conditionSpinner.getSelectedItem()));
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfConditions").setValue(s);
                        break;
                    case "CONSENT":
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("consent").setValue(consentSpinner.getSelectedItem());
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
                                    ProfileCompleteForPatient.this.finish();
                                }
                            })
                            .show();
                }
                else{
                    switch(details.get(i)){
                        case "CONDITION":
                            consentLayout.setVisibility(View.GONE);
                            conditionLayout.setVisibility(View.VISIBLE);
                            break;
                        case "CONSENT":
                            conditionLayout.setVisibility(View.GONE);
                            consentLayout.setVisibility(View.VISIBLE);
                            break;

                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
        ProfileCompleteForPatient.this.finish();
    }
}
