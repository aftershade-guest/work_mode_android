package com.aftershade.workmode.users.ui.chats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.ViewHolders.ChatsViewHolder;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.chat.ChatScreenActivity;
import com.aftershade.workmode.users.chat.SearchContactsChatsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Consumer;

public class ChatsFragment extends Fragment {

    RecyclerView chatsRecycler;
    DatabaseReference chatsRef, userRef, mutedRef;
    String currentUserId, saveCurrentDate;
    FirebaseRecyclerAdapter<String, ChatsViewHolder> adapter;
    ErrorCatching errorCatching;
    String[] strings;
    FloatingActionButton addChatBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        mutedRef = FirebaseDatabase.getInstance().getReference().child("Muted_Blocked_Contacts")
                .child(currentUserId);

        strings = new String[]{
                "Mark as Unread", "Delete", "Mute"
        };

        errorCatching = new ErrorCatching(getContext());

        chatsRecycler = root.findViewById(R.id.chats_recycler);
        addChatBtn = root.findViewById(R.id.chats_add_chat_fb);

        chatsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        loadChats();

        addChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchContactsChatsActivity.class));
            }
        });

        return root;
    }

    private void loadChats() {

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(chatsRef, new SnapshotParser<String>() {
                    @NonNull
                    @Override
                    public String parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot.getKey();
                    }
                })
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        adapter = new FirebaseRecyclerAdapter<String, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int i, @NonNull String receiverId) {

                holder.messageNumber.setVisibility(View.GONE);

                userRef.child(receiverId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            if (snapshot.hasChild("Images")) {
                                Picasso.get()
                                        .load(snapshot.child("Images").child("profilePhoto").getValue().toString())
                                        .placeholder(R.drawable.image_placeholder_icon)
                                        .error(R.drawable.image_error_icon)
                                        .into(holder.profileImage);
                            }

                            holder.fullName.setText(snapshot.child("fullName").getValue().toString());
                            String id = snapshot.child("uid").getValue().toString();

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ChatScreenActivity.class);
                                    intent.putExtra("id", id);
                                    intent.putExtra("name", snapshot.child("fullName").getValue().toString());

                                    startActivity(intent);

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorCatching.onError(error);
                    }
                });

                mutedRef.child(receiverId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.messageNumber.setVisibility(View.GONE);
                            holder.mutedIcon.setVisibility(View.VISIBLE);
                            strings[2] = "Unmute";
                        } else {
                            holder.messageNumber.setVisibility(View.VISIBLE);
                            holder.mutedIcon.setVisibility(View.GONE);
                            strings[2] = "Mute";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorCatching.onError(error);
                    }
                });

                chatsRef.child(receiverId).limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                                @Override
                                public void accept(DataSnapshot dataSnapshot) {

                                    String key = dataSnapshot.getKey();

                                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            showDialog(receiverId, key);
                                            return false;
                                        }
                                    });

                                    chatsRef.child(receiverId).child(key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            String message = dataSnapshot.child("message").getValue().toString();
                                            String time = dataSnapshot.child("time").getValue().toString();
                                            String date = dataSnapshot.child("date").getValue().toString();

                                            if (date.equals(saveCurrentDate)) {
                                                holder.timeTxtV.setText(time);
                                            } else {
                                                holder.timeTxtV.setText(date);
                                            }

                                            holder.chatMessage.setText(message);

                                            if (dataSnapshot.hasChild("state")) {
                                                String state = dataSnapshot.child("state").getValue().toString();

                                                if (state.equals("unread")) {
                                                    holder.chatMessage
                                                            .setTypeface(holder.messageNumber.getTypeface(), Typeface.BOLD);

                                                    holder.messageNumber.setVisibility(View.VISIBLE);
                                                    strings[0] = "Mark as Read";

                                                } else if (state.equals("read")) {
                                                    holder.chatMessage
                                                            .setTypeface(holder.messageNumber.getTypeface(), Typeface.NORMAL);
                                                    holder.messageNumber.setVisibility(View.GONE);
                                                    strings[0] = "Mark as Unread";
                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            errorCatching.onError(error);
                                        }
                                    });

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorCatching.onError(error);
                    }
                });

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_layout, parent, false);

                return new ChatsViewHolder(view);
            }
        };

        chatsRecycler.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showDialog(String s, String msgKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {

                    String st = strings[0];

                    if (st.equals("Mark as Read")) {
                        chatsRef.child(s).child(msgKey).child("state").setValue("read");
                    } else if (st.equals("Mark as Unread")) {
                        chatsRef.child(s).child(msgKey).child("state").setValue("unread");
                    }

                } else if (which == 1) {
                    deleteChat(s);
                } else if (which == 2) {

                    showMuteDialog(s);

                }

            }
        });

        builder.show();
    }

    private void showMuteDialog(String s) {

        if (strings[2].equals("Mute")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setItems(R.array.mute_chat, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    HashMap<String, Object> muteMap = new HashMap<>();

                    muteMap.put("duration", Array.get(R.array.mute_chat, which));
                    muteMap.put("id", s);
                    muteMap.put("stated", "muted");

                    mutedRef.child(currentUserId).child(s).updateChildren(muteMap);
                }
            });

            builder.show();

        } else if (strings[2].equals("Unmute")) {

            mutedRef.child(currentUserId).child(s).removeValue();

        }


    }

    private void deleteChat(String s) {

        chatsRef.child(s).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Chat removed", Toast.LENGTH_SHORT).show();
                } else {
                    errorCatching.onException(task.getException());
                }
            }
        });

    }
}