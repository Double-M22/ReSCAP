package com.dabinu.apps.recommender.Activities;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.dabinu.apps.recommender.AuthClasses.LoginActivity;
import com.dabinu.apps.recommender.Fragments.CommunitiesForPhysician;
import com.dabinu.apps.recommender.Fragments.HomeFragmentForPhysician;
import com.dabinu.apps.recommender.Fragments.ProfileForPhysician;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivityPhysician extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth mAuth;
    int exitCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_for_physicians);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.content_main_physician,  new HomeFragmentForPhysician(), "home");
        tx.commit();
        getSupportActionBar().setTitle("Home");
    }


    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            try{
                if(!(getFragmentManager().findFragmentByTag("home").isVisible())){
                    FragmentTransaction tx = getFragmentManager().beginTransaction();
                    tx.replace(R.id.content_main_physician,  new HomeFragmentForPhysician(), "home");
                    tx.commit();
                    getSupportActionBar().setTitle("Home");
                }
                else{
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
            }
            catch(Exception n){
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_main_physician,  new HomeFragmentForPhysician(), "home");
                tx.commit();
                getSupportActionBar().setTitle("Home");
            }

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.nav_communities){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_main_physician, new CommunitiesForPhysician(), "comm");
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Communities");
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        }


        else if(id == R.id.nav_home){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_main_physician, new HomeFragmentForPhysician(), "home");
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Home");
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        }

//        else if(id == R.id.nav_forum){
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.content_main_physician, new ForumForPhysician(), "forum");
//            fragmentTransaction.commit();
//            getSupportActionBar().setTitle("Forums");
//            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
//        }


        else if(id == R.id.nav_profile){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_main_physician, new ProfileForPhysician());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Profile");
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        }

        else if(id == R.id.nav_sign_out){

            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);

            new AlertDialog.Builder(this)
                    .setMessage("Sign out?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                            new CountDownTimer(2000, 1000){
                                @Override
                                public void onTick(long l){ }

                                @Override
                                public void onFinish(){
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    HomeActivityPhysician.this.finish();
                                }
                            }.start();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        else if(id == R.id.nav_exit) {
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return true;
    }
}
