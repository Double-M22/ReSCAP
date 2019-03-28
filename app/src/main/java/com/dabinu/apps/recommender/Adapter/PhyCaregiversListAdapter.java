package com.dabinu.apps.recommender.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dabinu.apps.recommender.Firebase_trees.PhyCaregivers;
import com.dabinu.apps.recommender.Firebase_trees.UsersSecTree;
import com.dabinu.apps.recommender.R;

import java.util.ArrayList;

public class PhyCaregiversListAdapter extends RecyclerView.Adapter<PhyCaregiversListAdapter.PhyCaregiversListViewHolder> {

    private ArrayList<PhyCaregivers> myCaregivers;
    private final PhyCaregiversListClickListener phyCaregiversListClickListener;
    private Context context;

    public interface PhyCaregiversListClickListener {
        void onPhyCaregiversListClicked(View view);
    }

    public PhyCaregiversListAdapter(ArrayList<PhyCaregivers> myCaregivers, Context context,
                                    PhyCaregiversListClickListener phyCaregiversListClickListener) {
        this.myCaregivers = myCaregivers;
        this.phyCaregiversListClickListener = phyCaregiversListClickListener;
        this.context = context;
    }

    @Override
    public PhyCaregiversListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.phy_caregivers_list_items;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new PhyCaregiversListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhyCaregiversListViewHolder holder, int position) {
        PhyCaregivers myCaregiver = myCaregivers.get(position);
        holder.itemView.setTag(position);
        holder.name.setText(myCaregiver.getName());
        ArrayList<UsersSecTree> caregiversPatients = myCaregiver.getCaregiversPatients();
        ArrayList<String> patients = new ArrayList<>();
        for(UsersSecTree caregiverPatient : caregiversPatients){
            String name = caregiverPatient.getName();
            String uniqueId = caregiverPatient.getUniqueId();

            patients.add(name +" | "+ uniqueId);
        }
        holder.caregiver_patients.setAdapter(new ArrayAdapter<>(context, R.layout.care_givers_patient_list_items, patients));
    }

    @Override
    public int getItemCount() {
        return myCaregivers.size();
    }

    class PhyCaregiversListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ListView caregiver_patients;

        PhyCaregiversListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.caregiver_name);
            caregiver_patients = itemView.findViewById(R.id.caregiver_patients);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            phyCaregiversListClickListener.onPhyCaregiversListClicked(view);
        }

    }
}


