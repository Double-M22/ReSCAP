package com.dabinu.apps.recommender.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabinu.apps.recommender.Firebase_trees.UsersSecTree;
import com.dabinu.apps.recommender.R;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {

    private ArrayList<UsersSecTree> usersSecTrees;
    private final UsersListClickListener usersListClickListener;

    public interface UsersListClickListener {
        void onUsersListClicked(View view);
    }

    public UsersListAdapter(ArrayList<UsersSecTree> usersSecTrees,
                            UsersListClickListener usersListClickListener) {
        this.usersSecTrees = usersSecTrees;
        this.usersListClickListener = usersListClickListener;
    }

    @Override
    public UsersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.users_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new UsersListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersListViewHolder holder, int position) {
        UsersSecTree usersSecTree = usersSecTrees.get(position);
        holder.itemView.setTag(position);
        holder.name.setText(usersSecTree.getName());
        holder.illness.setText(usersSecTree.getIllness());
    }

    @Override
    public int getItemCount() {
        return usersSecTrees.size();
    }

    class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, illness;

        UsersListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.users_name);
            illness = itemView.findViewById(R.id.users_illness);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            usersListClickListener.onUsersListClicked(view);
        }

    }
}

