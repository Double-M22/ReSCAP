package com.dabinu.apps.recommender.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabinu.apps.recommender.Firebase_trees.PhyPatient;
import com.dabinu.apps.recommender.R;

import java.util.ArrayList;

public class PhyPatientListAdapter extends RecyclerView.Adapter<PhyPatientListAdapter.PhyPatientListViewHolder> {

    private ArrayList<PhyPatient> patients;
    private final PhyPatientListClickListener phyPatientListClickListener;
    private Activity activity;

    public interface PhyPatientListClickListener{
        void onPhyPatientListClicked(View view);
    }

    public PhyPatientListAdapter(Activity activity, ArrayList<PhyPatient> patients, PhyPatientListClickListener phyPatientListClickListener){
        this.activity = activity;
        this.patients = patients;
        this.phyPatientListClickListener = phyPatientListClickListener;
    }

    @Override
    public PhyPatientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.my_patient_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new PhyPatientListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhyPatientListViewHolder holder, int position) {
        PhyPatient patient = patients.get(position);
        holder.itemView.setTag(position);
        holder.name.setText(patient.getName());
        holder.illness.setText(patient.getIllness() + "  :");
        holder.severity.setText(patient.getSeverity());
        int isConfirmed = patient.getPhyResponse();
        if(isConfirmed == 0){
            holder.confirmation.setTextColor(activity.getResources().getColor(R.color.yellow));
            holder.confirmation.setText("pending");
        }else if (isConfirmed == 1){
            holder.confirmation.setTextColor(activity.getResources().getColor(R.color.green));
            holder.confirmation.setText("confirmed");
        }else if (isConfirmed == 2){
            holder.confirmation.setTextColor(activity.getResources().getColor(R.color.red));
            holder.confirmation.setText("rejected");
        }
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    class PhyPatientListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, illness, severity, confirmation;

        PhyPatientListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.my_patient_name);
            illness = itemView.findViewById(R.id.my_patient_illness);
            severity = itemView.findViewById(R.id.my_patient_severity);
            confirmation = itemView.findViewById(R.id.my_patient_confirmation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            phyPatientListClickListener.onPhyPatientListClicked(view);
        }

    }
}