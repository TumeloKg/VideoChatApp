package com.example.videochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationsActivity extends AppCompatActivity
{
    private RecyclerView notification_list;
    private DatabaseReference friendRequestRef, contactsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        notification_list = findViewById(R.id.notification_list);
        notification_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(friendRequestRef.child(currentUserID), Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, NotificationViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int i, @NonNull Contacts contacts)
            {
                holder.acceptBtn.setVisibility(View.VISIBLE);
                holder.cancelBtn.setVisibility(View.VISIBLE);

                final String listUserId = getRef(i).getKey();

                DatabaseReference requestTypeRef = getRef(i).child("request_type").getRef();
                requestTypeRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String type = dataSnapshot.getValue().toString();

                            if (type.equals("received"))
                            {
                                //code to display received request
                                holder.cardView.setVisibility(View.VISIBLE);

                                userRef.child(listUserId).addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.hasChild("image"))
                                        {
                                            final String imageStr = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(imageStr).into(holder.profileImageView);
                                        }
                                        //if image exists show image otherwise get name nd show name
                                        final String nameStr = dataSnapshot.child("name").getValue().toString();
                                        holder.userNameTxt.setText(nameStr);

                                        holder.acceptBtn.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                contactsRef.child(currentUserID).child(listUserId)
                                                        .child("Contact").setValue("Saved")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                        {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    contactsRef.child(listUserId).child(currentUserID)
                                                                            .child("Contact").setValue("Saved")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                            {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        friendRequestRef.child(currentUserID).child(listUserId)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if (task.isSuccessful())
                                                                                                        {
                                                                                                            friendRequestRef.child(listUserId).child(currentUserID)
                                                                                                                    .removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                                    {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                                                        {
                                                                                                                            if (task.isSuccessful())
                                                                                                                            {
                                                                                                                                Toast.makeText(NotificationsActivity.this, "New Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                                                            }

                                                                                                                        }
                                                                                                                    });

                                                                                                        }

                                                                                                    }
                                                                                                });
                                                                                    }

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        });
                                        holder.cancelBtn.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                friendRequestRef.child(currentUserID).child(listUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                        {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    friendRequestRef.child(currentUserID).child(listUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                            {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        Toast.makeText(NotificationsActivity.this, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();

                                                                                    }

                                                                                }
                                                                            });

                                                                }

                                                            }
                                                        });

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                            }
                            else
                            {
                                //code to display sent request
                                holder.cardView.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_design, parent, false);
                NotificationViewHolder viewHolder = new NotificationViewHolder(view);
                return viewHolder;

            }
        };

        notification_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static  class NotificationViewHolder extends  RecyclerView.ViewHolder
    {

        TextView userNameTxt;
        Button acceptBtn, cancelBtn;
        ImageView profileImageView;
        RelativeLayout cardView;



        public NotificationViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_notification);
            acceptBtn = itemView.findViewById(R.id.request_accept_btn);
            cancelBtn = itemView.findViewById(R.id.request_decline_btn);
            profileImageView = itemView.findViewById(R.id.image_notification);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

//    private  void CancelFriendRequest()
//    {
//
//    }


//    private void AcceptFriendRequest()
//    {
//
//    }
}
