package com.dabinu.apps.recommender.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.dabinu.apps.recommender.Activities.HomeActivityPatient;
import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Activities.ProfileCompleteForPhysician;
import com.dabinu.apps.recommender.Dialog.EditTextDialog;
import com.dabinu.apps.recommender.Firebase_trees.ExceptionModel;
import com.dabinu.apps.recommender.Firebase_trees.PatientTree;
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

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CommunitiesForPatient extends android.app.Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, uniqueIdRef;
    private ProgressBar progressBar;
    private RelativeLayout noInternetLayout, commExistsLayout, noCommsLayout;
    private ListView myCommunities;
    private FloatingActionButton floatingActionButton;
    private TextView drs_response;

    public static  PatientTree patientTree;


    public CommunitiesForPatient() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communities_for_patient, container, false);
    }

    private boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        progressBar = getView().findViewById(R.id.patient_progressBar);
        noInternetLayout = getView().findViewById(R.id.patient_noInternetLayout);
        noCommsLayout = getView().findViewById(R.id.patient_userHasNoCommunities);
        commExistsLayout = getView().findViewById(R.id.patient_userHasCommunities);
        drs_response = getView().findViewById(R.id.dr_response_text);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        uniqueIdRef = databaseReference.child("UniqueIds");
        mAuth = FirebaseAuth.getInstance();

        myCommunities = getView().findViewById(R.id.patient_listOfCommunities);

        mCheckAndSetValues(getActivity().getApplicationContext(), getActivity());

        getView().findViewById(R.id.patient_joinNew).setOnClickListener(new View.OnClickListener() {
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
                        //Todo my changes
                        final Dialog dialog1 = new Dialog(getActivity());
                        dialog1.setContentView(R.layout.dayalog);
                        dialog1.setTitle("Severity");
                        final ListView listView1 = dialog1.findViewById(R.id.List);
                        listView1.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.severity, R.layout.show));
                        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                dialog1.hide();
                                joinNewComm(communityIntent, i);
                            }
                        });
                        dialog1.show();

                    }
                });
                dialog.show();
            }
        });

        floatingActionButton = getView().findViewById(R.id.patient_fab);
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

                        //Todo my changes
                        final Dialog dialog1 = new Dialog(getActivity());
                        dialog1.setContentView(R.layout.dayalog);
                        dialog1.setTitle("Severity");
                        final ListView listView1 = dialog1.findViewById(R.id.List);
                        listView1.setAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.severity, R.layout.show));
                        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                dialog1.hide();

                                joinNewComm(communityIntent, i);
                            }
                        });

                    }
                });

                dialog.show();
            }
        });

    }

    public interface MyPersonalListener{
        public void onStart();
        public void onSuccess(DataSnapshot data, Context context, Activity activity);
        public void onFailed(String reason);
    }

    private void mReadDataOnce(final CommunitiesForPatient.MyPersonalListener listener, FirebaseAuth mAuth, DatabaseReference databaseReference1, final Context context, final Activity activity){
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

    boolean updatePatientTree = false;
    boolean showCommunityList = false;
    boolean waitingOnPhysician = false;
    private void mCheckAndSetValues(Context context, Activity activity){
        new CommunitiesForPatient().mReadDataOnce(new CommunitiesForPatient.MyPersonalListener(){

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
            public void onSuccess(DataSnapshot dataSnapshot, final Context context, final Activity activity){
                progressBar.setVisibility(View.GONE);

                patientTree = dataSnapshot.getValue(PatientTree.class);

                if(patientTree.getListOfCommunities() == null){
                    noCommsLayout.setVisibility(View.VISIBLE);
                    commExistsLayout.setVisibility(View.GONE);
                    noInternetLayout.setVisibility(View.GONE);
                } else{
                    if(patientTree.getMyPhysicians() != null) {
                        final ArrayList<String> con = patientTree.getListOfConditions();
                        final ArrayList<String> com = patientTree.getListOfCommunities();
                        final ArrayList<String> sev = patientTree.getListOfCommunities();
                        final ArrayList<PhysicianSecTree> physicians = patientTree.getMyPhysicians();

                        for (final PhysicianSecTree myPhysician : patientTree.getMyPhysicians()){
                            databaseReference.child("users").child(myPhysician.getUserId()).child(activity.getString(R.string.my_patients))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ArrayList<PhyPatient> drPatients = getPatientList(dataSnapshot);
                                            for (PhyPatient me : drPatients){
                                                if(me.getUniqueId().equals(patientTree.getUniqueId())){
                                                    if (me.getPhyResponse() == 0){
                                                        waitingOnPhysician = true;
                                                    }else if (me.getPhyResponse() == 1){
                                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("canUseId")
                                                                .setValue("YES");
                                                        showCommunityList = true;
                                                    }else if (me.getPhyResponse() == 2){
                                                        drPatients.remove(me);
                                                        databaseReference.child("users").child(myPhysician.getUserId()).child(activity.getString(R.string.my_patients))
                                                                .setValue(drPatients);

                                                        physicians.remove(physicians.indexOf(myPhysician));
                                                        com.remove(com.indexOf(me.getIllness()));
                                                        sev.remove(sev.indexOf(me.getSeverity()));
                                                        updatePatientTree = true;
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });
                        }
                        if (updatePatientTree) {
                            PatientTree newPatient = new PatientTree(patientTree.getName(), patientTree.getEmail(), patientTree.getType(),
                                    patientTree.getLocation(), patientTree.getDateOfBirth(), patientTree.getConsent(), patientTree.getUniqueId(),
                                    patientTree.getCanUseId(), con, sev, com, physicians, patientTree.getMyCareGivers());
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).setValue(newPatient);
                        }
                        if (showCommunityList)
                            setCommunityList(activity);
                        else if(waitingOnPhysician)
                            drs_response.setVisibility(View.VISIBLE);
                        else
                            mCheckAndSetValues(context, activity);
                    }
//                    if(patientTree.getCanUseId().equals("YES")){
//                        setCommunityList(activity);
//                    }else if(patientTree.getCanUseId().equals("NO")){
//                        if(patientTree.getMyPhysicians() != null) {
//
//                            final ArrayList<String> con = patientTree.getListOfConditions();
//                            final ArrayList<String> com = patientTree.getListOfCommunities();
//                            final ArrayList<String> sev = patientTree.getListOfCommunities();
//                            final ArrayList<PhysicianSecTree> physicians = patientTree.getMyPhysicians();
//
//                            for(final PhysicianSecTree myPhysician : patientTree.getMyPhysicians()){
//                                databaseReference.child("users").child(myPhysician.getUserId()).child(activity.getString(R.string.my_patients))
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                ArrayList<PhyPatient> drPatients = getPatientList(dataSnapshot);
//                                                for (PhyPatient me : drPatients){
//                                                    if(me.getUniqueId().equals(patientTree.getUniqueId())){
//                                                        if (me.getPhyResponse() == 0){
//                                                            waitingOnPhysician = true;
//                                                        }else if (me.getPhyResponse() == 1){
//                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("canUseId")
//                                                                    .setValue("YES");
//                                                            showCommunityList = true;
//                                                        }else if (me.getPhyResponse() == 2){
//                                                            drPatients.remove(me);
//                                                            databaseReference.child("users").child(myPhysician.getUserId()).child(activity.getString(R.string.my_patients))
//                                                                    .setValue(drPatients);
//
//                                                            physicians.remove(physicians.indexOf(myPhysician));
//                                                            com.remove(com.indexOf(me.getIllness()));
//                                                            sev.remove(sev.indexOf(me.getSeverity()));
//                                                            updatePatientTree = true;
//
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) { }
//                                        });
//                            }
//
//                            if (updatePatientTree) {
//                                PatientTree newPatient = new PatientTree(patientTree.getName(), patientTree.getEmail(), patientTree.getType(),
//                                        patientTree.getLocation(), patientTree.getDateOfBirth(), patientTree.getConsent(), patientTree.getUniqueId(),
//                                        con, sev, com, physicians, patientTree.getMyCareGivers(), patientTree.getCanUseId());
//                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).setValue(newPatient);
//                            }
//                            if (showCommunityList)
//                                mCheckAndSetValues(context, activity);
//                            else if(waitingOnPhysician)
//                                drs_response.setVisibility(View.VISIBLE);
//                            else
//                                mCheckAndSetValues(context, activity);
//
//                        }else{
//                            noCommsLayout.setVisibility(View.VISIBLE);
//                            commExistsLayout.setVisibility(View.GONE);
//                            noInternetLayout.setVisibility(View.GONE);
//                        }
//                    }
                }
            }

            @Override
            public void onFailed(String reason){
                progressBar.setVisibility(View.GONE);
                if(!(reason.equals("data"))){
                    Toast.makeText(getActivity().getApplicationContext(), "Failed, try again later", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPatient.class));
                }
                else{
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Check your internet connection")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPatient.class));
                                }
                            })
                            .show();
                }
            }
        }, mAuth, databaseReference, context, activity);
    }

    private void setCommunityList(Activity activity){
        ArrayList<PhysicianSecTree> myPhysicians = patientTree.getMyPhysicians();
        ArrayList<String> comms = new ArrayList<>();
        for(PhysicianSecTree myPhysician : myPhysicians){
            if(myPhysician.getIsConfirmed().equals("YES")){
                comms.add(myPhysician.getSpecialization());
            }
        }
        commExistsLayout.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        noCommsLayout.setVisibility(View.GONE);
        ArrayAdapter<String> harry = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, comms);
        myCommunities.setAdapter(harry);
        floatingActionButton.setVisibility(View.GONE);

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
                fragmentTransaction.replace(R.id.content_main_patient, new CommunityMessenger(), "messenger");
                fragmentTransaction.commit();
                ((HomeActivityPatient) getActivity()).getSupportActionBar().setTitle(value);
            }
        });
    }

    public interface JoiningANewCommunity{
        public void onStart();
        public void onSuccess(DataSnapshot data, String intendedComm);
        public void onFailed(String reason);
    }

    private void mReadTheData(final CommunitiesForPatient.JoiningANewCommunity listener,
                              FirebaseAuth mAuth, DatabaseReference databaseReference1, final String intendedComm){
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

    public static String my_patient_illness, my_patient_severity;
    public static ArrayList<String> old_severities, newList;
    public static String unique_id = null;
    public static String patientName;
    private void joinNewComm(final String comm, final int severity){
        new CommunitiesForPatient().mReadTheData(new CommunitiesForPatient.JoiningANewCommunity(){

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

                boolean create_new = false;
                Log.d("Community", "Success on listener creation");

                patientTree = dataSnapshot.getValue(PatientTree.class);
                assert patientTree != null;
                if(patientTree.getUniqueId().equals("null")){
                    formUniqueId(comm, severity);
                }else{
                    patientName = patientTree.getName();
                    unique_id = patientTree.getUniqueId();
                }
                ArrayList<String> previousCommunities = patientTree.getListOfCommunities();
                newList = new ArrayList<>();

                if(previousCommunities != null) {
                    for (int i = 0; i < previousCommunities.size(); i++) {
                        if (previousCommunities.get(i).equals(intendedComm)) {
                            create_new = false;
                            Date date = Calendar.getInstance().getTime();
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String datee = formatter.format(date);
                            String time = new SimpleDateFormat("HH:mm:ss").format(date);
                            ExceptionModel exceptionModel = new ExceptionModel(patientTree.getName(), patientTree.getEmail(),
                                    "User is already a member", datee, time);
                            databaseReference.child("allexceptions").push().setValue(exceptionModel);
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("You are already a part of this community")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", null)
                                    .show();
                            newList.add(previousCommunities.get(i));
                            break;
                        } else if ((previousCommunities.get(i).equals("null"))) {
                            newList.add(previousCommunities.get(i));
                            create_new = true;
                        } else {
                            newList.add(previousCommunities.get(i));
                            create_new = true;
                        }
                    }
                }else create_new = true;

                if (create_new){
                    Date date = Calendar.getInstance().getTime();
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String datee = formatter.format(date);
                    String time = new SimpleDateFormat("HH:mm:ss").format(date);
                    ExceptionModel exceptionModel = new ExceptionModel(patientTree.getName(), patientTree.getEmail(),
                            "User successfully initiated", datee, time);
                    databaseReference.child("allexceptions").push().setValue(exceptionModel);
                    newList.add(intendedComm);
                    Log.d("Community", "newList Added!");
                    old_severities = patientTree.getSeverities();
                    if(old_severities == null)
                        old_severities = new ArrayList<>();
                    switch (severity){
                        case 0:
                            old_severities.add("Early");
                            my_patient_severity = "Early";
                            break;
                        case 1:
                            old_severities.add("Mild");
                            my_patient_severity = "Mild";
                            break;
                        case 2:
                            old_severities.add("Severe");
                            my_patient_severity = "Severe";
                            break;
                    }
                    my_patient_illness = intendedComm;
                    showDialog();
                    //Todo moved the addition of community to the dialog
//                    new AlertDialog.Builder(getActivity())
//                            .setMessage(String.format("Initiated into %s community successfully", intendedComm))
//                            .setCancelable(false)
//                            //Todo notice is this crashes here
//                            .setPositiveButton("Okay", null)
//                            .show();
                }
            }

            @Override
            public void onFailed(String reason){
                progressBar.setVisibility(View.GONE);
                if(!(reason.equals("data"))){
                    Toast.makeText(getActivity().getApplicationContext(), "Failed, try again later", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPatient.class));
                }
                else{
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Check your internet connection")
                            .setCancelable(false)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity().getApplicationContext(), HomeActivityPatient.class));
                                }
                            })
                            .show();
                }
            }
        }, mAuth, databaseReference, comm);

    }

    private void showDialog(){
        EditTextDialog dialog = new EditTextDialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
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

    public ArrayList<PhyPatient> getPatientList(DataSnapshot dataSnapshot){
        ArrayList<PhyPatient> ids = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PhyPatient myData = ds.getValue(PhyPatient.class);
                ids.add(myData);
            }
        }
        return ids;
    }

    String code_start, mid_code;
    String code_number = "0000";
    private void formUniqueId(final String comm, final int severity){

        patientName = patientTree.getName();
        code_start = patientTree.getLocation().substring(patientTree.getLocation().length()-3,
                patientTree.getLocation().length()-1);
        mid_code = comm.substring(comm.length()-3, comm.length()-1);

        uniqueIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Patient")) {
                    uniqueIdRef.child("Patient").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(patientTree.getLocation())){
                                uniqueIdRef.child("Patient").child(patientTree.getLocation()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(comm)){
                                            uniqueIdRef.child("Patient").child(patientTree.getLocation()).child(comm).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    ArrayList<String> ids = getDataList(dataSnapshot);
                                                    int id_count = ids.size();
                                                    unique_id = null;
                                                    if(!ids.isEmpty()){
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
                                                            unique_id = code_start+severity+mid_code+"X"+code_number;
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
                                                            uniqueIdRef.child("Patient").child(patientTree.getLocation()).child(comm)
                                                                    .child(""+id_count).setValue(unique_id);
                                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                                    .child("uniqueId").setValue(unique_id);
                                                        }

                                                    }else {
                                                        unique_id = code_start+severity+mid_code+"X"+code_number;
                                                        uniqueIdRef.child("Patient").child(patientTree.getLocation()).child(comm)
                                                                .child("0").setValue(unique_id);
                                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                                .child("uniqueId").setValue(unique_id);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) { }
                                            });

                                        } else {
                                            unique_id = code_start+severity+mid_code+"X"+code_number;
                                            uniqueIdRef.child("Patient").child(patientTree.getLocation()).child(comm)
                                                    .child("0").setValue(unique_id);
                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                                    .child("uniqueId").setValue(unique_id);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });

                            } else {
                                unique_id = code_start+severity+mid_code+"X"+code_number;
                                uniqueIdRef.child("Patient").child(patientTree.getLocation()).child(comm)
                                        .child("0").setValue(unique_id);
                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                        .child("uniqueId").setValue(unique_id);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                }else {
                    unique_id = code_start+severity+mid_code+"X"+code_number;
                    uniqueIdRef.child("Patient").child(patientTree.getLocation()).child(comm)
                            .child("0").setValue(unique_id);
                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                            .child("uniqueId").setValue(unique_id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
