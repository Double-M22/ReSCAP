package com.dabinu.apps.recommender.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianSecTree;
import com.dabinu.apps.recommender.Fragments.CommunitiesForPatient;
import com.dabinu.apps.recommender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditTextDialog extends Dialog {

    private Activity myActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuthException e;
    private WindowManager windowManager;
    private View view;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressBar progressBar;

    public EditTextDialog(Activity context) {
        super(context);
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        myActivity = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private String phy_id_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_text_dialog);

        view = getWindow().getDecorView().getRootView();
        progressBar = findViewById(R.id.edit_text_dialog_progress_bar);
        final Button notify = findViewById(R.id.notifyPhy);
        final EditText phy_id = findViewById(R.id.patient_phy_id);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                notify.setEnabled(false);

                phy_id_text = phy_id.getText().toString().trim();
                if(!TextUtils.isEmpty(phy_id_text)){
                    mDatabase.child("ReSCAP Physicians")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> physicians = getDataList(dataSnapshot);
                            boolean gotPhy = false;
                            if (!physicians.isEmpty()){
                                for (final String physician : physicians){
                                    if(physician.equalsIgnoreCase(phy_id_text)){

                                        mDatabase.child("ReSCAP Physicians").child(physician).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final PhysicianSecTree physicianSecTree = dataSnapshot.getValue(PhysicianSecTree.class);
                                                final FirebaseUser user = mAuth.getCurrentUser();

                                                //set patient values
                                                ArrayList<PhysicianSecTree> myPhysicians = CommunitiesForPatient.patientTree.getMyPhysicians();
                                                if(myPhysicians == null)
                                                    myPhysicians = new ArrayList<>();
                                                PhysicianSecTree physician = new PhysicianSecTree(physicianSecTree.getName(), physicianSecTree.getUniqueId(),
                                                        physicianSecTree.getUserId(), CommunitiesForPatient.my_patient_illness, physicianSecTree.getIsConfirmed());
                                                myPhysicians.add(physician);
                                                mDatabase.child("users").child(user.getUid()).child("myPhysicians").setValue(myPhysicians);

                                                //set physicians values
                                                mDatabase.child("users").child(physicianSecTree.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        ArrayList<PhyPatient> newList = new ArrayList<>();
                                                        PhyPatient phyPatient = new PhyPatient(CommunitiesForPatient.patientName,
                                                                CommunitiesForPatient.my_patient_illness, CommunitiesForPatient.my_patient_severity,
                                                                CommunitiesForPatient.unique_id, user.getUid(), 0);
                                                        if(dataSnapshot.hasChild(myActivity.getString(R.string.my_patients))) {
                                                            newList = getPatientList(dataSnapshot.child(myActivity.getString(R.string.my_patients)));
                                                            newList.add(phyPatient);
                                                        }else{
                                                            newList.add(phyPatient);
                                                        }
                                                        mDatabase.child("users").child(physicianSecTree.getUserId()).child(myActivity.getString(R.string.my_patients)).setValue(newList);

                                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("listOfCommunities").setValue(CommunitiesForPatient.newList);
                                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("severities").setValue(CommunitiesForPatient.old_severities);

                                                        notify.setEnabled(true);
                                                        progressBar.setVisibility(View.GONE);
                                                        cancelDialog();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) { }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) { }
                                        });
                                        gotPhy = true;
                                        break;
                                    }
                                }

                                if(!gotPhy) {
                                    notify.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(myActivity, "No Physician with such ID", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                notify.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(myActivity, "No Physicians yet!", Toast.LENGTH_SHORT).show();
                                cancelDialog();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                }else {
                    notify.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(myActivity, "Please enter physicians ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<String> getDataList(DataSnapshot dataSnapshot){
        ArrayList<String> ids = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String myData = ds.getKey();
                ids.add(myData);
            }
        }
        return ids;
    }

    private ArrayList<PhyPatient> getPatientList(DataSnapshot dataSnapshot){
        ArrayList<PhyPatient> ids = new ArrayList<>();
        if(dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PhyPatient myData = ds.getValue(PhyPatient.class);
                ids.add(myData);
            }
        }
        return ids;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            cancelDialog();
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void cancelDialog(){
        windowManager.removeViewImmediate(view);
    }
}