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
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.AllProgramsHolder;
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

public class SearchProgramsAdapter extends RecyclerView.Adapter<AllProgramsHolder> implements Filterable {

    Context context;
    ArrayList<ProgramsHelperClass> searchProgramsList;
    ArrayList<ProgramsHelperClass> filterArrayList;
    ProgramClickListener itemClick_listener;
    DatabaseReference allProgramsRef;

    public SearchProgramsAdapter(Context context, ArrayList<ProgramsHelperClass> searchProgramsList, ProgramClickListener itemClick_listener) {
        this.context = context;
        this.searchProgramsList = searchProgramsList;
        this.itemClick_listener = itemClick_listener;
        filterArrayList = searchProgramsList;
        allProgramsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");
    }

    @NonNull
    @Override
    public AllProgramsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View programView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_program_card,
                parent, false);
        return new AllProgramsHolder(programView);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProgramsHolder allProgramsHolder, int position) {

        ProgramsHelperClass helperClass = (ProgramsHelperClass) filterArrayList.get(position);

        allProgramsHolder.favoriteBtn.setVisibility(View.INVISIBLE);
        allProgramsHolder.courseNameText.setText(helperClass.getCourseName());
        allProgramsHolder.ratingBar.setRating((float) helperClass.getRating());
        allProgramsHolder.descText.setText(helperClass.getDescription());
        allProgramsHolder.skillLevel.setText(helperClass.getSkillLevel());

        String key = helperClass.getCourseName() + helperClass.getTrainerId();

        allProgramsRef.child(key).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Picasso.get()
                                .load(snap.getValue().toString())
                                .placeholder(R.drawable.image_placeholder_icon)
                                .resize(1024, 720)
                                .error(R.drawable.image_error_icon)
                                .into(allProgramsHolder.imageView);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(context);
                errorCatching.onError(error);
            }
        });

        allProgramsHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick_listener.onClick(filterArrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return filterArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();

                if (query.equals("all")) {
                    filterArrayList = searchProgramsList;
                } else if (query.isEmpty()) {
                    filterArrayList = new ArrayList<>();
                } else {
                    List<ProgramsHelperClass> filteredList = new ArrayList<>();
                    query = query.toLowerCase();
                    for (ProgramsHelperClass helperClass : searchProgramsList) {

                        String courseName = helperClass.getCourseName().toLowerCase();
                        String skillLevel = helperClass.getSkillLevel().toLowerCase();
                        String type = helperClass.getType().toLowerCase();
                        String programType = helperClass.getType().toLowerCase();
                        String duration = helperClass.getDuration() + " " + helperClass.getDurationSpan();


                        if (courseName.contains(query) || skillLevel.contains(query)
                                || type.contains(query) || programType.contains(query) || duration.contains(query)) {
                            filteredList.add(helperClass);
                        }
                    }

                    filterArrayList = (ArrayList<ProgramsHelperClass>) filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterArrayList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterArrayList = (ArrayList<ProgramsHelperClass>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public void sortData(String sort) {

        Taskss taskss = new Taskss();
        taskss.execute("sort", sort);

    }

    public void filterProgramLength(String filter) {

        Taskss taskss = new Taskss();
        taskss.execute("filter", filter);

    }

    public class Taskss extends AsyncTask<String, Integer, ArrayList<ProgramsHelperClass>> {

        @Override
        protected ArrayList<ProgramsHelperClass> doInBackground(String... strings) {

            String whatToDo = strings[0];
            String sortfilter = strings[1];

            ArrayList<ProgramsHelperClass> filteredList = new ArrayList<>();

            if (whatToDo.equals("filter")) {
                if (sortfilter.equals("Less than 1 week")) {

                    for (ProgramsHelperClass helperClass : searchProgramsList) {

                        int duration = Integer.parseInt(helperClass.getDuration());
                        String span = helperClass.getDurationSpan();

                        if ((duration < 7 && span.equals("days"))) {
                            filteredList.add(helperClass);
                        }

                    }

                } else if (sortfilter.equals("1 to 2 weeks")) {
                    for (ProgramsHelperClass helperClass : searchProgramsList) {

                        int duration = Integer.parseInt(helperClass.getDuration());
                        String span = helperClass.getDurationSpan();

                        if (duration >= 1 && span.equals("Weeks") && duration <= 2) {
                            filteredList.add(helperClass);
                        }

                    }
                } else if (sortfilter.equals("2 to 4 weeks")) {
                    for (ProgramsHelperClass helperClass : searchProgramsList) {

                        int duration = Integer.parseInt(helperClass.getDuration());
                        String span = helperClass.getDurationSpan();

                        if (duration >= 2 && span.equals("Weeks") && duration <= 4) {
                            filteredList.add(helperClass);
                        }

                    }
                } else if (sortfilter.equals("More than 4 weeks")) {
                    for (ProgramsHelperClass helperClass : searchProgramsList) {

                        int duration = Integer.parseInt(helperClass.getDuration());
                        String span = helperClass.getDurationSpan();

                        if (duration > 4 && span.equals("Weeks")) {
                            filteredList.add(helperClass);
                        }

                    }
                }
            } else if (whatToDo.equals("sort")) {

                filteredList = filterArrayList;

                if (sortfilter.equals("a to z")) {
                    filteredList.sort(new Comparator<ProgramsHelperClass>() {
                        @Override
                        public int compare(ProgramsHelperClass o1, ProgramsHelperClass o2) {
                            return o1.getCourseName().compareToIgnoreCase(o2.getCourseName());
                        }
                    });

                } else if (sortfilter.equals("z to a")) {
                    filteredList.sort(new Comparator<ProgramsHelperClass>() {
                        @Override
                        public int compare(ProgramsHelperClass o1, ProgramsHelperClass o2) {
                            return o2.getCourseName().compareToIgnoreCase(o1.getCourseName());
                        }
                    });

                } else if (sortfilter.equals("recent")) {
                    filteredList.sort(new Comparator<ProgramsHelperClass>() {
                        @Override
                        public int compare(ProgramsHelperClass o1, ProgramsHelperClass o2) {

                            return o1.getDateTimeCreated().compareToIgnoreCase(o2.getDateTimeCreated());
                        }
                    });
                }
            }

            return filteredList;
        }

        @Override
        protected void onPostExecute(ArrayList<ProgramsHelperClass> programsHelperClasses) {
            filterArrayList = programsHelperClasses;
            notifyDataSetChanged();
        }
    }
}
