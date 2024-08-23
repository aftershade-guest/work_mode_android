package com.aftershade.workmode.users.workout_posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.ImageFragmentAdapter;
import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.HelperClasses.Models.KeyValueClass;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ViewPhotoFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PhotosFragmentFragment extends Fragment {

    String courseName, trainerId;
    RecyclerView photoRecyclerView;
    DatabaseReference photoRef;
    ArrayList<KeyValueClass> photoArray;
    ErrorCatching errorCatching;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            courseName = getArguments().getString("courseName");
            trainerId = getArguments().getString("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos_fragment, container, false);

        photoRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs")
                .child(courseName).child("Images");
        errorCatching = new ErrorCatching(getContext());
        photoArray = new ArrayList<>();

        photoRecyclerView = view.findViewById(R.id.photos_fragment_recycler_view);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        loadPhotos();

        return view;
    }

    private void loadPhotos() {

        photoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    photoArray.clear();

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        photoArray.add(new KeyValueClass(snap.getKey(), snap.getValue().toString()));
                    }

                    Click_Listerner click_listerner = new Click_Listerner() {
                        @Override
                        public void onClick(View v, int position, boolean isLongClick) {

                            Bundle bundle = new Bundle();
                            bundle.putString("url", photoArray.get(position).getValue());

                            getParentFragmentManager().beginTransaction().replace(R.id.workout_posts_container,
                                    ViewPhotoFragment.class, bundle).addToBackStack("").commit();

                        }
                    };

                    photoRecyclerView.setAdapter(new ImageFragmentAdapter(photoArray, click_listerner));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
            }
        });

    }
}