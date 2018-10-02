package com.dabinu.apps.recommender.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Activities.ProfileCompleteForPhysician;
import com.dabinu.apps.recommender.Firebase_trees.ExceptionModel;
import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianSecTree;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class CommunitiesForPhysician extends android.app.Fragment{

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    RelativeLayout noInternetLayout, commExistsLayout, noCommsLayout;
    ListView myCommunities;
    PhysicianTree physicianTree;

    public CommunitiesForPhysician(){

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

        getView().findViewById(R.id.joinNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dayalog);
                dialog.setTitle("Join new community");
                final ListView listView = dialog.findViewById(R.id.List);
                listView.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.populate, R.layout.show));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        final String communityIntent = ((String) listView.getItemAtPosition(position));
                        dialog.hide();
                        joinNewComm(communityIntent);

                    }
                });

                dialog.show();

            }
        });

        FloatingActionButton floatingActionButton = getView().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dayalog);
                dialog.setTitle("Join new community");
                final ListView listView = dialog.findViewById(R.id.List);
                listView.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.populate, R.layout.show));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        final String communityIntent = ((String) listView.getItemAtPosition(position));
                        dialog.hide();
                        joinNewComm(communityIntent);

                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_communities_for_physician, container, false);
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

    private void mReadDataOnce(final CommunitiesForPhysician.MyPersonalListener listener, FirebaseAuth mAuth, DatabaseReference databaseReference1, final Context context, final Activity activity){
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

        new CommunitiesForPhysician().mReadDataOnce(new MyPersonalListener(){

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

                PhysicianTree userTree = dataSnapshot.getValue(PhysicianTree.class);

                if(userTree.getListOfSpecialization() == null){
                    new AlertDialog.Builder(getActivity())
                            .setMessage("You have to choose a specialization before you can join a community")
                            .setCancelable(false)
                            .setPositiveButton("Do that now", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    ArrayList<String> lifeAfterDeath = new ArrayList<>();
                                    lifeAfterDeath.add("SPECIALIZATION");
                                    lifeAfterDeath.add("LICENSE");
                                    lifeAfterDeath.add("AFFILIATION");
                                    startActivity(new Intent(getActivity().getApplicationContext(), ProfileCompleteForPhysician.class).putExtra("DETAILS", lifeAfterDeath));
                                }
                            })
                            .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    noCommsLayout.setVisibility(View.VISIBLE);
                                    commExistsLayout.setVisibility(View.GONE);
                                    noInternetLayout.setVisibility(View.GONE);
                                }
                            }).show();
                }

                else if(userTree.getListOfCommunities() == null){
                    noCommsLayout.setVisibility(View.VISIBLE);
                    commExistsLayout.setVisibility(View.GONE);
                    noInternetLayout.setVisibility(View.GONE);
                }

                else{
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
                            fragmentTransaction.replace(R.id.content_main_physician, new CommunityMessenger(), "messenger");
                            fragmentTransaction.commit();
                            ((HomeActivityPhysician) getActivity()).getSupportActionBar().setTitle(value);
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




    public interface JoiningANewCommunity{
        public void onStart();
        public void onSuccess(DataSnapshot data, String intendedComm);
        public void onFailed(String reason);
    }

    private void mReadTheData(final CommunitiesForPhysician.JoiningANewCommunity listener, FirebaseAuth mAuth, DatabaseReference databaseReference1, final String intendedComm){
        listener.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference1.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                listener.onSuccess(dataSnapshot, intendedComm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                listener.onFailed("databaseError");
            }
        });
    }

    private void joinNewComm(String comm){
        new CommunitiesForPhysician().mReadTheData(new JoiningANewCommunity(){

            @Override
            public void onStart(){
                progressBar.setVisibility(View.VISIBLE);
                if(!isNetworkAvailable(getActivity().getApplicationContext())){
                    onFailed("data");
                }
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot, String intendedComm){
                progressBar.setVisibility(View.GONE);

                boolean continum = true, continum2 = true;

                physicianTree = dataSnapshot.getValue(PhysicianTree.class);
                ArrayList<String> spec = physicianTree.getListOfSpecialization();
                ArrayList<String> previousCommunities = physicianTree.getListOfCommunities();
                ArrayList<String> newList = new ArrayList<>();


                //Todo make sure that the previous community isn't null
                if(previousCommunities != null) {
                    for (int i = 0; i < previousCommunities.size(); i++) {
                        if (previousCommunities.get(i).equals(intendedComm)) {
                            continum = false;
                            Date date = Calendar.getInstance().getTime();
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String datee = formatter.format(date);
                            String time = new SimpleDateFormat("HH:mm:ss").format(date);
                            ExceptionModel exceptionModel = new ExceptionModel(physicianTree.getName(), physicianTree.getEmail(), "User is already a member", datee, time);
                            databaseReference.child("allexceptions").push().setValue(exceptionModel);
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("You are already a part of this community")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", null)
                                    .show();
                        } else if (!(previousCommunities.get(i).equals("null"))) {
                            newList.add(previousCommunities.get(i));
                        }
                    }
                }


                if(continum){
                    someLabel:
                    for(int i = 0; i < spec.size(); i++){

                        if((spec.size() == 1) && (spec.get(0).equals("null"))){
                            Date date = Calendar.getInstance().getTime();
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String datee = formatter.format(date);
                            String time = new SimpleDateFormat("HH:mm:ss").format(date);
                            ExceptionModel exceptionModel = new ExceptionModel(physicianTree.getName(), physicianTree.getEmail(), "User not verified", datee, time);
                            databaseReference.child("allexceptions").push().setValue(exceptionModel);
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("You have to be verified before you can join a community.")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", null)
                                    .show();
                            break someLabel;
                        }

                        else if(spec.get(i).trim().equals(intendedComm)){
                            Date date = Calendar.getInstance().getTime();
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String datee = formatter.format(date);
                            String time = new SimpleDateFormat("HH:mm:ss").format(date);
                            ExceptionModel exceptionModel = new ExceptionModel(physicianTree.getName(), physicianTree.getEmail(), "User successfully initiated", datee, time);
                            databaseReference.child("allexceptions").push().setValue(exceptionModel);
                            newList.add(intendedComm);
                            formUniqueId(intendedComm, newList);
                            //Todo moved to formUniqueId
                            break someLabel;
                        }

                        else if((i == (spec.size() - 1)) && (!(spec.get(i).trim().equals(intendedComm)))){
                            Date date = Calendar.getInstance().getTime();
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String datee = formatter.format(date);
                            String time = new SimpleDateFormat("HH:mm:ss").format(date);
                            ExceptionModel exceptionModel = new ExceptionModel(physicianTree.getName(), physicianTree.getEmail(), "User doesn't meet specification requirements", datee, time);
                            databaseReference.child("allexceptions").push().setValue(exceptionModel);
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("You don't meet the requirements to join this community.")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", null)
                                    .show();
                            break someLabel;
                        }
                    }
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
        }, mAuth, databaseReference, comm);

    }

    public ArrayList<String> getDataList(DataSnapshot dataSnapshot){
        ArrayList<String> ids = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String myData = (String) ds.getValue();
                ids.add(myData);
            }
        }
        return ids;
    }

    public static String unique_id = null;
    public static String patientName;
    String code_start, mid_code;
    String code_number = "0000";
    private void formUniqueId(final String comm, final ArrayList<String> newList){

        patientName = physicianTree.getName();
        code_start = physicianTree.getLocation().substring(physicianTree.getLocation().length()-3,
                physicianTree.getLocation().length()-1);
        mid_code = comm.substring(comm.length()-3, comm.length()-1);

        final DatabaseReference uniqueIdRef = databaseReference.child("UniqueIds");

        uniqueIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Physician")) {
                    uniqueIdRef.child("Physician").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(physicianTree.getLocation())){
                                uniqueIdRef.child("Physician").child(physicianTree.getLocation()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(comm)){
                                            uniqueIdRef.child("Physician").child(physicianTree.getLocation()).child(comm).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    ArrayList<String> ids = getDataList(dataSnapshot);
                                                    int id_count = ids.size();
                                                    unique_id = null;
                                                    if(ids.isEmpty()){
                                                        boolean isUnique = false;
                                                        Random r = new Random();
                                                        Set<Integer> uniqueNumbers = new HashSet<>();

                                                        while (uniqueNumbers.size() < 9999) {
                                                            uniqueNumbers.add(r.nextInt(9999));
                                                            int random = 0;
                                                            for (int i : uniqueNumbers) {
                                                                random = i;
                                                            }
                                                            if (random < 10 && random > 0)
                                                                code_number = "000" + random;
                                                            else if (random < 100 && random > 9)
                                                                code_number = "00" + random;
                                                            else if (random < 1000 && random > 99)
                                                                code_number = "0" + random;
                                                            else if (random > 1000)
                                                                code_number = "" + random;
                                                            unique_id = code_start+"P"+mid_code+"V"+code_number;
                                                            for (String id : ids) {
                                                                if (id.equals(unique_id)) {
                                                                    isUnique = false;
                                                                    break;
                                                                } else isUnique = true;
                                                            }
                                                            if (isUnique)
                                                                break;
                                                        }
                                                        if (isUnique) {
                                                            if(physicianTree.getUniqueId().equals("null")) {
                                                                PhysicianSecTree physicianSecTree = new PhysicianSecTree(physicianTree.getName(), unique_id,
                                                                        mAuth.getCurrentUser().getUid(), null, "NO");
                                                                databaseReference.child("ReSCAP Physicians").child(unique_id).setValue(physicianSecTree);
                                                                uniqueIdRef.child("Physician").child(physicianTree.getLocation()).child(comm)
                                                                        .child("" + id_count).setValue(unique_id);
                                                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                                        .child("uniqueId").setValue(unique_id);
                                                            }
                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfCommunities").setValue(newList);
                                                            new AlertDialog.Builder(getActivity())
                                                                    .setMessage(String.format("Initiated into %s community successfully", comm))
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i){
                                                                            FragmentTransaction tx = getFragmentManager().beginTransaction();
                                                                            tx.replace(R.id.content_main_physician,  new CommunitiesForPhysician(), "communities");
                                                                            tx.commit();
                                                                            (getActivity()).setTitle("Communities");
                                                                        }
                                                                    })
                                                                    .show();
                                                        }

                                                    }else {
                                                        unique_id = code_start+"P"+mid_code+"V"+code_number;
                                                        if(physicianTree.getUniqueId().equals("null")) {
                                                            PhysicianSecTree physicianSecTree = new PhysicianSecTree(physicianTree.getName(), unique_id,
                                                                    mAuth.getCurrentUser().getUid(), null, "NO");
                                                            databaseReference.child("ReSCAP Physicians").child(unique_id).setValue(physicianSecTree);
                                                            uniqueIdRef.child("Physician").child(physicianTree.getLocation()).child(comm)
                                                                    .child("0").setValue(unique_id);
                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                                    .child("uniqueId").setValue(unique_id);
                                                        }
                                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfCommunities").setValue(newList);
                                                        new AlertDialog.Builder(getActivity())
                                                                .setMessage(String.format("Initiated into %s community successfully", comm))
                                                                .setCancelable(false)
                                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i){
                                                                        FragmentTransaction tx = getFragmentManager().beginTransaction();
                                                                        tx.replace(R.id.content_main_physician,  new CommunitiesForPhysician(), "communities");
                                                                        tx.commit();
                                                                        (getActivity()).setTitle("Communities");
                                                                    }
                                                                })
                                                                .show();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) { }
                                            });

                                        } else {
                                            unique_id = code_start+"P"+mid_code+"V"+code_number;
                                            if(physicianTree.getUniqueId().equals("null")) {
                                                PhysicianSecTree physicianSecTree = new PhysicianSecTree(physicianTree.getName(), unique_id,
                                                        mAuth.getCurrentUser().getUid(), null, "NO");
                                                databaseReference.child("ReSCAP Physicians").child(unique_id).setValue(physicianSecTree);
                                                uniqueIdRef.child("Physician").child(physicianTree.getLocation()).child(comm)
                                                        .child("0").setValue(unique_id);
                                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                        .child("uniqueId").setValue(unique_id);
                                            }
                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfCommunities").setValue(newList);
                                            new AlertDialog.Builder(getActivity())
                                                    .setMessage(String.format("Initiated into %s community successfully", comm))
                                                    .setCancelable(false)
                                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i){
                                                            FragmentTransaction tx = getFragmentManager().beginTransaction();
                                                            tx.replace(R.id.content_main_physician,  new CommunitiesForPhysician(), "communities");
                                                            tx.commit();
                                                            (getActivity()).setTitle("Communities");
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });

                            } else {
                                unique_id = code_start+"P"+mid_code+"V"+code_number;
                                if(physicianTree.getUniqueId().equals("null")) {
                                    PhysicianSecTree physicianSecTree = new PhysicianSecTree(physicianTree.getName(), unique_id,
                                            mAuth.getCurrentUser().getUid(), null, "NO");
                                    databaseReference.child("ReSCAP Physicians").child(unique_id).setValue(physicianSecTree);
                                    uniqueIdRef.child("Physician").child(physicianTree.getLocation()).child(comm)
                                            .child("0").setValue(unique_id);
                                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                            .child("uniqueId").setValue(unique_id);
                                }
                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfCommunities").setValue(newList);
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(String.format("Initiated into %s community successfully", comm))
                                        .setCancelable(false)
                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i){
                                                FragmentTransaction tx = getFragmentManager().beginTransaction();
                                                tx.replace(R.id.content_main_physician,  new CommunitiesForPhysician(), "communities");
                                                tx.commit();
                                                (getActivity()).setTitle("Communities");
                                            }
                                        })
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                }else {
                    unique_id = code_start+"P"+mid_code+"V"+code_number;
                    if(physicianTree.getUniqueId().equals("null")) {
                        PhysicianSecTree physicianSecTree = new PhysicianSecTree(physicianTree.getName(), unique_id,
                                mAuth.getCurrentUser().getUid(), null, "NO");
                        databaseReference.child("ReSCAP Physicians").child(unique_id).setValue(physicianSecTree);
                        uniqueIdRef.child("Physician").child(physicianTree.getLocation()).child(comm)
                                .child("0").setValue(unique_id);
                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                .child("uniqueId").setValue(unique_id);
                    }
                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfCommunities").setValue(newList);
                    new AlertDialog.Builder(getActivity())
                            .setMessage(String.format("Initiated into %s community successfully", comm))
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i){
                                    FragmentTransaction tx = getFragmentManager().beginTransaction();
                                    tx.replace(R.id.content_main_physician,  new CommunitiesForPhysician(), "communities");
                                    tx.commit();
                                    (getActivity()).setTitle("Communities");
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}