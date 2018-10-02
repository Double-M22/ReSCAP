package com.dabinu.apps.recommender.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabinu.apps.recommender.Activities.HomeActivityPhysician;
import com.dabinu.apps.recommender.Firebase_trees.DateTree;
import com.dabinu.apps.recommender.Firebase_trees.ForumChatObjects;
import com.dabinu.apps.recommender.Firebase_trees.PhysicianTree;
import com.dabinu.apps.recommender.R;
import com.dabinu.apps.recommender.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CommunityMessenger extends android.app.Fragment {

    private EditText entered_message;
    private DatabaseReference date_ref, community_messages;
    private FirebaseAuth mAuth;
    private RecyclerView messenger_list;

    public static final String MONTHS[] = new String[]{ "null",
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December" };

    public CommunityMessenger() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_messenger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        entered_message = view.findViewById(R.id.entered_message);

        messenger_list = view.findViewById(R.id.messenger_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        messenger_list.setHasFixedSize(true);
        messenger_list.setLayoutManager(layoutManager);

        //This sets the community type
        String community_type = Utils.community_type;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().
                child("CommunityMessages").child(community_type);
        mAuth = FirebaseAuth.getInstance();
        date_ref = mDatabase.child("Previous_Date");
        community_messages = mDatabase.child("Community_messages");

        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <ForumChatObjects, CommunityMessengerAdapterViewHolder>(
                ForumChatObjects.class,
                R.layout.messenger_design,
                CommunityMessengerAdapterViewHolder.class,
                community_messages
        ) {
            @Override
            protected void populateViewHolder(final CommunityMessengerAdapterViewHolder viewHolder,
                                              final ForumChatObjects model, int position) {
                if(model.getChatPerDay() <= 1){
                    viewHolder.setMessageDate(model.getDate());
                    viewHolder.date.setVisibility(View.VISIBLE);
                    viewHolder.date_separator.setVisibility(View.VISIBLE);
                } else{
                    viewHolder.date.setVisibility(View.GONE);
                    viewHolder.date_separator.setVisibility(View.GONE);
                }

                viewHolder.setMessage(model.getMessage());
                viewHolder.setSenderName(model.getSanderName());
                viewHolder.setSenderImage(R.drawable.messenger_profile);
                viewHolder.setMessageTime(model.getTime());
            }
        };
        messenger_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messenger_list.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount());
            }
        });

        ImageView send = view.findViewById(R.id.message_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage(){
        final String message = entered_message.getText().toString().trim();
        if (!TextUtils.isEmpty(message)){
            entered_message.setText(null);
            FirebaseUser user = mAuth.getCurrentUser();
            final DatabaseReference newPost = community_messages.push();
            DatabaseReference user_data = FirebaseDatabase.getInstance().getReference();
            user_data.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    final PhysicianTree userTree = dataSnapshot.getValue(PhysicianTree.class);

                    date_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("message").setValue(message);
                            assert userTree != null;
                            newPost.child("sanderName").setValue(userTree.getName());

                            Calendar c = Calendar.getInstance();
                            int year = c.get(Calendar.YEAR);
                            int mon= c.get(Calendar.MONTH);
                            int dy = c.get(Calendar.DAY_OF_MONTH);
                            int hr = c.get(Calendar.HOUR_OF_DAY);
                            int mi = c.get(Calendar.MINUTE);
                            String day, month;
                            if(dy == 1)
                                day = "1st";
                            else if(dy == 2)
                                day = "2nd";
                            else if(dy == 3)
                                day = "3rd";
                            else{
                                day = dy+"th";
                            }
                            month = MONTHS[mon];
                            String date = day+" "+month+", "+year;
                            newPost.child("date").setValue(date);

                            String hour, minute;
                            if(hr < 10)
                                hour = "0"+hr;
                            else
                                hour = ""+hr;

                            if(mi < 10)
                                minute = "0"+mi;
                            else
                                minute = ""+mi;
                            newPost.child("time").setValue(hour+":"+minute);

                            int chat_count;
                            if(dataSnapshot.hasChildren()){
                                DateTree dateTree = dataSnapshot.getValue(DateTree.class);
                                chat_count = dateTree.getCount();
                                String previous_date = dateTree.getDate();
                                if(previous_date != null) {
                                    if (previous_date.equals(date))
                                        chat_count = chat_count + 1;
                                    else
                                        chat_count = 1;
                                }else
                                    chat_count = 1;
                            }else
                                chat_count = 1;


                            newPost.child("chatPerDay").setValue(chat_count);
                            date_ref.child("Count").setValue(chat_count);
                            date_ref.child("Date").setValue(date);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError){ }
            });
        }
    }

    public static class CommunityMessengerAdapterViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView date;
        private View date_separator;

        CommunityMessengerAdapterViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            date = view.findViewById(R.id.messenger_date);
            date_separator = view.findViewById(R.id.messenger_date_separator);
        }

        private void setSenderName(String senderName){
            TextView name = view.findViewById(R.id.messenger_name);
            name.setText(senderName);
        }

        private void setMessageTime(String messageTime){
            TextView time = view.findViewById(R.id.messenger_time);
            time.setText(messageTime);
        }

        private void setMessage(String message){
            TextView messenger_message = view.findViewById(R.id.messenger_message);
            messenger_message.setText(message);
        }

        private void setSenderImage(int drawable){
            ImageView senderImage = view.findViewById(R.id.messenger_profile);
            senderImage.setImageResource(drawable);
        }

        private void setMessageDate(String messageDate){
            date.setText(messageDate);
        }
    }

}
