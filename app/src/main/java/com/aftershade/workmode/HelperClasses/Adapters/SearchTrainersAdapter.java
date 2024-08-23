package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Listeners.TrainersClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.ViewHolders.TrainersViewHolder;
import com.aftershade.workmode.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchTrainersAdapter extends RecyclerView.Adapter<TrainersViewHolder> implements Filterable {

    Context context;
    ArrayList<Instructor> trainerHelperArray;
    ArrayList<Instructor> filterListArray;
    DatabaseReference allTrainersRef;
    TrainersClickListener trainersClickListener;

    public SearchTrainersAdapter(Context context, ArrayList<Instructor> trainerHelperArray,
                                 TrainersClickListener trainersClickListener) {
        this.context = context;
        this.trainerHelperArray = trainerHelperArray;
        this.trainersClickListener = trainersClickListener;
        filterListArray = new ArrayList<>();
        filterListArray = trainerHelperArray;
        allTrainersRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
    }

    @NonNull
    @Override
    public TrainersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainers_card,
                parent, false);

        return new TrainersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainersViewHolder trainersHolder, int position) {

        Instructor instrutorSuper = filterListArray.get(position);

        trainersHolder.fullName.setText(instrutorSuper.getFullName());
        trainersHolder.rating.setRating(0);
        trainersHolder.type.setText(instrutorSuper.getTrainerType());

        allTrainersRef.child(instrutorSuper.getUid()).child("Images").child("profilePhoto")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            Picasso.get()
                                    .load(snapshot.getValue(String.class))
                                    .placeholder(R.drawable.image_placeholder_icon)
                                    .error(R.drawable.image_error_icon)
                                    .into(trainersHolder.profilePic);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ErrorCatching errorCatching = new ErrorCatching(context);
                        errorCatching.onError(error);
                    }
                });

        trainersHolder.itemView.setOnClickListener(v -> trainersClickListener.onClick(instrutorSuper));

    }

    @Override
    public int getItemCount() {
        return filterListArray.size();
    }

    public void sortData(String sort) {

        SortFilterTask sortFilterTask = new SortFilterTask();
        sortFilterTask.execute("sort", sort);

    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String query = constraint.toString();

                if (query.equals("all")) {
                    filterListArray = trainerHelperArray;
                } else if (query.isEmpty()) {
                    filterListArray = new ArrayList<>();
                } else {
                    List<Instructor> filteredList = new ArrayList<>();
                    query = query.toLowerCase();

                    for (Instructor instructor : trainerHelperArray) {

                        String name = instructor.getFullName().toLowerCase();
                        String gender = instructor.getGender().toLowerCase();
                        String type = instructor.getTrainerType().toLowerCase();

                        if (name.contains(query) || gender.contains(query) || type.contains(query)) {
                            filteredList.add(instructor);
                        }

                    }

                    filterListArray = (ArrayList<Instructor>) filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterListArray;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filterListArray = (ArrayList<Instructor>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class SortFilterTask extends AsyncTask<String, Integer, ArrayList<Instructor>> {

        @Override
        protected ArrayList<Instructor> doInBackground(String... strings) {

            String whatToDO = strings[0];
            String sortfilter = strings[1];

            ArrayList<Instructor> filteredList = new ArrayList<>();

            if (whatToDO.equals("sort")) {

                filteredList = filterListArray;

                if (sortfilter.equals("a to z")) {
                    filteredList.sort(new Comparator<Instructor>() {
                        @Override
                        public int compare(Instructor o1, Instructor o2) {
                            return o1.getFullName().compareToIgnoreCase(o2.getFullName());
                        }
                    });

                } else if (sortfilter.equals("z to a")) {
                    filteredList.sort(new Comparator<Instructor>() {
                        @Override
                        public int compare(Instructor o1, Instructor o2) {
                            return o2.getFullName().compareToIgnoreCase(o1.getFullName());
                        }
                    });

                } else if (sortfilter.equals("type")) {
                    filteredList.sort(new Comparator<Instructor>() {
                        @Override
                        public int compare(Instructor o1, Instructor o2) {
                            return o1.getTrainerType().compareToIgnoreCase(o2.getTrainerType());
                        }
                    });

                } else if (sortfilter.equals("rating_ascend")) {
                    filteredList.sort(new Comparator<Instructor>() {
                        @Override
                        public int compare(Instructor o1, Instructor o2) {
                            return o1.getTrainerType().compareToIgnoreCase(o2.getTrainerType());
                        }
                    });

                } else if (sortfilter.equals("rating_descend")) {
                    filteredList.sort(new Comparator<Instructor>() {
                        @Override
                        public int compare(Instructor o1, Instructor o2) {
                            return o2.getTrainerType().compareToIgnoreCase(o1.getTrainerType());
                        }
                    });

                }

            }

            return filteredList;
        }

        @Override
        protected void onPostExecute(ArrayList<Instructor> instructors) {
            filterListArray = instructors;
            notifyDataSetChanged();
        }
    }

}
