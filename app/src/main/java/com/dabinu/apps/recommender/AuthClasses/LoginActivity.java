package com.dabinu.apps.recommender.AuthClasses;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityCaregiver;
import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Activities.SplashActivity;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    int exitCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(0, 0);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final EditText email = findViewById(R.id.textEmail);
        final EditText password = findViewById(R.id.textPassword);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.openSignUp).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                LoginActivity.this.finish();
            }
        });


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())){
                    email.setError("Invalid email");
                }
                else if(password.getText().toString().trim().length() < 6){
                    password.setError("Invalid password");
                }
                else{
                    findViewById(R.id.mamamat).setAlpha(0.6f);
                    progressBar.setVisibility(View.VISIBLE);

                    if(!isNetworkAvailable()){
                        Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                    }
                    else{
                        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task){
                                if(task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //todo add the child("users") to the value event listener
                                    databaseReference.child("users").child(user.getUid()).child("type").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot){
                                            String category = (String)dataSnapshot.getValue();
                                            if(category == null){
                                                startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                                                LoginActivity.this.finish();
                                            }else {
                                                switch (category) {
                                                    case "PHYSICIAN":
                                                        startActivity(new Intent(getApplicationContext(), HomeActivityPhysician.class));
                                                        LoginActivity.this.finish();
                                                        break;
                                                    case "PATIENT":
                                                        startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
                                                        LoginActivity.this.finish();
                                                        break;
                                                    case "CAREGIVER":
                                                        startActivity(new Intent(getApplicationContext(), HomeActivityCaregiver.class));
                                                        LoginActivity.this.finish();
                                                        break;
                                                    default:
                                                        Toast.makeText(getApplicationContext(), "There's an error somewhere, try again", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else{
                                    try{
                                        progressBar.setVisibility(View.GONE);
                                        findViewById(R.id.mamamat).setAlpha(1);
                                        throw task.getException();
                                    } catch(FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException iv){
                                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                                    } catch(Exception e){
                                        Toast.makeText(getApplicationContext(), "Something went wrong, try again", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });
                    }

                }

            }
        });


    }


    @Override
    public void onBackPressed(){

        //todo: think of an efficient way to shutdown app. Consider using oga's library.
        exitCount++;

        switch(exitCount){
            case 1:
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show();
                new CountDownTimer(3000, 1000){
                    @Override
                    public void onTick(long millisUntilFinished){
                    }

                    @Override
                    public void onFinish(){
                        exitCount = 0;
                    }
                }.start();
                break;
            case 2:
                startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }

    }


    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}