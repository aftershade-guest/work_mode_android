package com.aftershade.workmode.users.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.SearchTrainersAdapter;
import com.aftershade.workmode.HelperClasses.Listeners.TrainersClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.TrainerInfoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchContactsChatsActivity extends AppCompatActivity {

    ImageButton backBtn;
    SearchView searchView;
    RecyclerView searchRecycler;

    SearchTrainersAdapter searchTrainersAdapter;
    DatabaseReference trainerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts_chats);

        trainerRef = FirebaseDatabase.getInstance().getReference().child("Trainers");

        backBtn = findViewById(R.id.back_btn_add_chat);
        searchView = findViewById(R.id.search_view_add_chats);
        searchRecycler = findViewById(R.id.contacts_recycler_add_chat);

        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        setUpTrainersAdapter();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchTrainersAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchTrainersAdapter.getFilter().filter(newText);

                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setUpTrainersAdapter() {

        ArrayList<Instructor> instructorsArray = new ArrayList<>();

        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String key = snapshot1.getKey();

                        Instructor instructor = snapshot.child(key).getValue(Instructor.class);
                        instructorsArray.add(instructor);
                    }

                    TrainersClickListener trainersClickListener = new TrainersClickListener() {
                        @Override
                        public void onClick(Instructor instructor) {
                            Intent intent = new Intent(SearchContactsChatsActivity.this, ChatScreenActivity.class);
                            intent.putExtra("id", instructor.getUid());
                            intent.putExtra("name", instructor.getFullName());
                            startActivity(intent);
                        }
                    };

                    searchTrainersAdapter = new SearchTrainersAdapter(SearchContactsChatsActivity.this,
                            instructorsArray, trainersClickListener);

                    searchRecycler.setAdapter(searchTrainersAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(SearchContactsChatsActivity.this);
                errorCatching.onError(error);
            }
        });

    }
}