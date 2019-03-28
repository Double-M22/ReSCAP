package com.dabinu.apps.recommender.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.dabinu.apps.recommender.AuthClasses.LoginActivity;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(0, 0);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        new SplashAsyntask().execute();

    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    private class SplashAsyntask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids){

            if(!isNetworkAvailable()){
                return "noNetwork";
            } else{
                if(mAuth.getCurrentUser() == null){
                    return "notSignedIn";
                } else{
                    return "signedIn";
                }
            }
        }

        @Override
        protected void onPostExecute(String result){
            switch(result){
                case "noNetwork":
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Try again", v -> {
                                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                            })
                            .setDuration(1200000)
                            .show();
                    break;

                case "notSignedIn":
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    SplashActivity.this.finish();
                    break;

                case "signedIn":
                    FirebaseUser user = mAuth.getCurrentUser();
                    databaseReference.child("users").child(user.getUid()).child("type").addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot){
                            switch(dataSnapshot.getValue(String.class)){
                                case "PHYSICIAN":
                                    startActivity(new Intent(getApplicationContext(), HomeActivityPhysician.class));
                                    break;
                                case "PATIENT":
                                    startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
                                    break;
                                case "CAREGIVER":
                                    startActivity(new Intent(getApplicationContext(), HomeActivityCaregiver.class));
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "There's an error somewhere, try again", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                        }
                    });
                    break;
            }
        }
    }


}