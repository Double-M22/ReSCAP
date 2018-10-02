package com.dabinu.apps.recommender.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityCaregiver;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Firebase_trees.CaregiverTree;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunitiesForCaregiver extends android.app.Fragment{

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    RelativeLayout noInternetLayout, commExistsLayout, noCommsLayout;
    ListView myCommunities;

    public CommunitiesForCaregiver(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        progressBar = getView().findViewById(R.id.progressBar);
        noInternetLayout = getView().findViewById(R.id.noInternetLayout);
        noCommsLayout = getView().findViewById(R.id.userHasNoCommunities);
        commExistsLayout = getView().findViewById(R.id.userHasCommunities);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        myCommunities = getView().findViewById(R.id.listOfCommunities);

        mCheckAndSetValues(getActivity().getApplicationContext(), getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_communities_for_caregiver, container, false);
    }

    private boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public interface MyPersonalListener{
        public void onStart();
        public void onSuccess(DataSnapshot data, Context context, Activity activity);
        public void onFailed(String reason);
    }

    private void mReadDataOnce(final CommunitiesForCaregiver.MyPersonalListener listener, FirebaseAuth mAuth, DatabaseReference databaseReference1, final Context context, final Activity activity){
        listener.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference1.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                listener.onSuccess(dataSnapshot, context, activity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                listener.onFailed("databaseError");
            }
        });
    }

    private void mCheckAndSetValues(Context context, Activity activity){

        new CommunitiesForCaregiver().mReadDataOnce(new MyPersonalListener(){

            @Override
            public void onStart(){
                progressBar.setVisibility(View.VISIBLE);
                if(!isNetworkAvailable(getActivity().getApplicationContext())){
                    noInternetLayout.setVisibility(View.VISIBLE);
                    commExistsLayout.setVisibility(View.GONE);
                    noCommsLayout.setVisibility(View.GONE);
                    onFailed("data");
                }
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot, Context context, Activity activity){
                progressBar.setVisibility(View.GONE);

                CaregiverTree userTree = dataSnapshot.getValue(CaregiverTree.class);

                if(userTree.getListOfSpecialization() == null){
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Please go to your profile and pick your area of specialization")
                            .setCancelable(false)
                            .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    noCommsLayout.setVisibility(View.VISIBLE);
                                    commExistsLayout.setVisibility(View.GONE);
                                    noInternetLayout.setVisibility(View.GONE);
                                }
                            }).show();
                } else if(userTree.getListOfCommunities() == null){
                    noCommsLayout.setVisibility(View.VISIBLE);
                    commExistsLayout.setVisibility(View.GONE);
                    noInternetLayout.setVisibility(View.GONE);
                } else{
                    ArrayList<String> comms = userTree.getListOfCommunities();
                    commExistsLayout.setVisibility(View.VISIBLE);
                    noInternetLayout.setVisibility(View.GONE);
                    noCommsLayout.setVisibility(View.GONE);
                    ArrayAdapter<String> harry = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, comms);
                    myCommunities.setAdapter(harry);

//                    Todo mike addition
                    myCommunities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String value = (String)myCommunities.getItemAtPosition(i);
                            Utils.community_type = value;
                            Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();

                            FragmentManager fragmentManager = getFragmentManager();
                            if(getFragmentManager().getBackStackEntryCount() != 0){
                                fragmentManager.popBackStack();
                            }
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.content_main_caregiver, new CommunityMessenger(), "messenger");
                            fragmentTransaction.commit();
                            ((HomeActivityCaregiver) getActivity()).getSupportActionBar().setTitle(value);
                        }
                    });
                }

            }

            @Override
            public void onFailed(String reason){
                progressBar.setVisibility(View.GONE);
                if(!(reason.equals("data"))){
                    Toast.makeText(getActivity().getApplicationContext(), "Failed, try again later", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPhysician.class));
                }
                else{
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Check your internet connection")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPhysician.class));
                                }
                            })
                            .show();
                }
            }
        }, mAuth, databaseReference, context, activity);

    }

}
