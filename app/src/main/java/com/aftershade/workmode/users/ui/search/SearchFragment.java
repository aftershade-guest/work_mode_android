package com.aftershade.workmode.users.ui.search;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.SearchSuggestion.SuggestionDatabase;
import com.aftershade.workmode.HelperClasses.SearchSuggestion.SuggestionSimpleCursorAdapter;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.TrainerInfoActivity;
import com.aftershade.workmode.HelperClasses.ViewHolders.TrainersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    SearchView searchView;
    private SuggestionDatabase database;
    RecyclerView searchRecycler;
    ListView listView;
    FirebaseRecyclerAdapter<Instructor, TrainersViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_search_screen);
        searchRecycler = view.findViewById(R.id.search_recycler_view);
        listView = view.findViewById(R.id.suggestion_view);

        searchRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        database = new SuggestionDatabase(getContext());
        searchView.setOnQueryTextListener(this);
        listView.setOnItemClickListener(listener);

        searchFirebase(" ");

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        searchRecycler.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        database.insertSuggestion(query);
        searchFirebase(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (listView.getVisibility() == View.GONE) {
            listView.setVisibility(View.VISIBLE);
            searchRecycler.setVisibility(View.GONE);
        }

        Cursor cursor = database.getSuggestions(newText);

        if (cursor.getCount() != 0) {
            String[] columns = new String[]{SuggestionDatabase.FIELD_SUGGESTION};
            int[] columnTextId = new int[]{R.id.list_layout_textV};

            SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getContext(),
                    R.layout.list_layout_, cursor, columns, columnTextId, 0);

            listView.setAdapter(simple);
            //searchView.setSuggestionsAdapter(simple);
            return true;

        } else return false;
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SQLiteCursor cursor = (SQLiteCursor) listView.getAdapter().getItem(position);

            int indexColumnSuggestion = cursor.getColumnIndex(SuggestionDatabase.FIELD_SUGGESTION);

            searchView.setQuery(cursor.getString(indexColumnSuggestion), false);

        }
    };

    private void searchFirebase(String query) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Trainers");

        FirebaseRecyclerOptions<Instructor> options = new
                FirebaseRecyclerOptions.Builder<Instructor>().setQuery(ref
                .startAt(query.toLowerCase()).endAt(query.toUpperCase()), Instructor.class).build();

        adapter = new FirebaseRecyclerAdapter<Instructor, TrainersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TrainersViewHolder trainersViewHolder, int i, @NonNull Instructor instrutorSuper) {

                trainersViewHolder.fullName.setText(instrutorSuper.getFullName());
                trainersViewHolder.rating.setRating(2);
                trainersViewHolder.type.setText(instrutorSuper.getTrainerType());

                ref.child(instrutorSuper.getUid()).child("Images").child("profilePhoto")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Picasso.get()
                                        .load(snapshot.getValue(String.class))
                                        .placeholder(R.drawable.flag_guinea_bissau)
                                        .fit()
                                        .centerCrop()
                                        .into(trainersViewHolder.profilePic);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                ErrorCatching errorCatching = new ErrorCatching(getContext());
                                errorCatching.onError(error);
                            }
                        });

                trainersViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), TrainerInfoActivity.class);
                    intent.putExtra("id", instrutorSuper.getUid());
                    startActivity(intent);
                });

            }

            @NonNull
            @Override
            public TrainersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainers_card,
                        parent, false);

                return new TrainersViewHolder(view);
            }
        };

        searchRecycler.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }
}