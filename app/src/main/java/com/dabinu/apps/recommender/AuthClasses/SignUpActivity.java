package com.dabinu.apps.recommender.AuthClasses;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityCaregiver;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Firebase_trees.CaregiverTree;
import com.dabinu.apps.recommender.Firebase_trees.UsersSecTree;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianSecTree;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity{

    String title;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    EditText name, email, password;
    CardView done;
    private String country, consent;
    private static TextView dob_selector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.nameField);
        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passwordField);
        done = findViewById(R.id.next);

        dob_selector = findViewById(R.id.date_of_birth);
        dob_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        final Spinner consents = findViewById(R.id.consent);
        consents.setAdapter(ArrayAdapter.createFromResource(this, R.array.consents, R.layout.support_simple_spinner_dropdown_item));
        consents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        consent = "Nuller";
                        break;
                    case 1:
                        consent = "Always share";
                        break;
                    case 2:
                        consent = "Ask before Share";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        final Spinner countries = findViewById(R.id.countries);
        countries.setAdapter(ArrayAdapter.createFromResource(this, R.array.countries, R.layout.support_simple_spinner_dropdown_item));
        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        country = "Nigeria (NG)";
                        break;
                    case 1:
                        country = "Finland (FL)";
                        break;
                    case 2:
                        country = "United States of America (US)";
                        break;
                    case 3:
                        country = "China (CH)";
                        break;
                    case 4:
                        country = "Mexico (MX)";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        final Spinner option = findViewById(R.id.option);
        option.setAdapter(ArrayAdapter.createFromResource(this, R.array.options, R.layout.support_simple_spinner_dropdown_item));
        option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                switch(i){
                    case 0:
                        title = "nuller";
                        break;
                    case 1:
                        title = "PHYSICIAN";
                        break;
                    case 2:
                        title = "PATIENT";
                        break;
                    case 3:
                        title = "CAREGIVER";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                SignUpActivity.this.finish();
            }
        });

        findViewById(R.id.entireOption).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option.performClick();
            }
        });

        done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                } else{
                    if(title.equals("nuller")){
                        Toast.makeText(getApplicationContext(), "You have to select a sign-in category", Toast.LENGTH_LONG).show();
                    } else{
                        if(name.getText().toString().trim().equals("")){
                            name.setError("This field is required");
                            Toast.makeText(getApplicationContext(), "Name field is required", Toast.LENGTH_LONG).show();
                        } else if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())){
                            email.setError("Invalid email");
                            Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_LONG).show();
                        } else if (dob_selector.getText().toString().equals("Date of Birth")){
                            Toast.makeText(SignUpActivity.this, "Date of Birth must be selected", Toast.LENGTH_SHORT).show();
                        } else if(password.getText().toString().trim().length() < 6){
                            password.setError("Password must be at least 6 characters");
                            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
                        } else if (consent.equals("Nuller")){
                            Toast.makeText(SignUpActivity.this, "Please select a consent type", Toast.LENGTH_SHORT).show();
                        }else{
                            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                            findViewById(R.id.ourScroller).setAlpha(0.3f);
                            done.setClickable(false);

                                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task){
                                        if(task.isSuccessful()){
                                            switch (title) {
                                                case "PHYSICIAN":
                                                    try {
                                                        PhysicianTree newUser = new PhysicianTree(name.getText().toString().trim(), email.getText().toString().trim(),
                                                                title, country, dob_selector.getText().toString().trim(), consent, "null", "not set yet", "not set yet",
                                                                new ArrayList<String>(), new ArrayList<String>(), new ArrayList<PhyPatient>(), new ArrayList<UsersSecTree>());
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        databaseReference.child("users").child(user.getUid()).setValue(newUser);
                                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                        startActivity(new Intent(getApplicationContext(), HomeActivityPhysician.class));
                                                        SignUpActivity.this.finish();
                                                    } catch (Exception e) {
                                                        findViewById(R.id.ourScroller).setAlpha(1);
                                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                        Log.e("CRASH_BECAUSE", e.toString());
                                                        Toast.makeText(getApplicationContext(), "Failed, try again", Toast.LENGTH_LONG).show();
                                                        done.setClickable(true);

                                                    }
                                                    break;
                                                case "PATIENT":
                                                    try {
                                                        PatientTree newUser = new PatientTree(name.getText().toString().trim(), email.getText().toString().trim(),
                                                                title, country, dob_selector.getText().toString().trim(), consent, "null", "NO", new ArrayList<String>(),
                                                                new ArrayList<String>(), new ArrayList<String>(), new ArrayList<PhysicianSecTree>(), new ArrayList<UsersSecTree>());
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        databaseReference.child("users").child(user.getUid()).setValue(newUser);
                                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                        startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
                                                        SignUpActivity.this.finish();
                                                    } catch (Exception e) {
                                                        findViewById(R.id.ourScroller).setAlpha(1);
                                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                        Log.e("CRASH_BECAUSE", e.toString());
                                                        Toast.makeText(getApplicationContext(), "Failed, try again", Toast.LENGTH_LONG).show();
                                                        done.setClickable(true);
                                                    }
                                                    break;
                                                case "CAREGIVER":
                                                    try {
                                                        CaregiverTree newUser = new CaregiverTree(name.getText().toString().trim(), email.getText().toString().trim(),
                                                                title, country, dob_selector.getText().toString().trim(), consent, "null", new ArrayList<String>(), new ArrayList<String>(),
                                                                new ArrayList<UsersSecTree>(), new ArrayList<UsersSecTree>());
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        databaseReference.child("users").child(user.getUid()).setValue(newUser);
                                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                        startActivity(new Intent(getApplicationContext(), HomeActivityCaregiver.class));
                                                        SignUpActivity.this.finish();
                                                    } catch (Exception e) {
                                                        findViewById(R.id.ourScroller).setAlpha(1);
                                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                        Log.e("CRASH_BECAUSE", e.toString());
                                                        Toast.makeText(getApplicationContext(), "Failed, try again", Toast.LENGTH_LONG).show();
                                                        done.setClickable(true);
                                                    }
                                                    break;
                                            }
                                        }
                                        else{
                                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                                            findViewById(R.id.ourScroller).setAlpha(1);
                                            done.setClickable(true);
                                            Toast.makeText(getApplicationContext(), String.format("Account creation unsuccessful because "+ task.getException().toString()), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                        }
                    }
                }
                }
        });
    }

    public void showDatePicker() {
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "Date");
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        SignUpActivity.this.finish();
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @TargetApi(Build.VERSION_CODES.N)

        @Override
        public Dialog onCreateDialog(Bundle saveInstanceState){

            //This is for default date .
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        //The date selected by the user would be assigned by this.
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Make use of the date selected by user here.
            setDob(year, month+1, day);
        }

        public static void setDob(int year, int mon, int dy){
            String day, month;

            if(dy<10) day = "0"+dy;
            else day = ""+dy;

            if(mon<10) month = "0"+mon;
            else month = ""+mon;

            String date = day+" / "+month+" / "+year;
            dob_selector.setText(date);
        }
    }
}