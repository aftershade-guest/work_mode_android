package com.aftershade.workmode.users.ui.home.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.HelperClasses.Listeners.HomeEventsClickListener;
import com.aftershade.workmode.HelperClasses.Models.ProgramOfflineModel;
import com.aftershade.workmode.R;

import java.util.ArrayList;

public class TodayEventAdapter extends RecyclerView.Adapter<TodayEventAdapter.TodayViewHolder>{

    Context context;
    ArrayList<ProgramOfflineModel> eventsModels;
    public HomeEventsClickListener eventsClickListener;
    int position;


    public TodayEventAdapter(Context context, ArrayList<ProgramOfflineModel> eventsModels, HomeEventsClickListener homeEventsClickListener) {
        this.context = context;
        this.eventsModels = eventsModels;
        this.eventsClickListener = homeEventsClickListener;
    }

    @NonNull
    @Override
    public TodayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tasks_programs_card, parent, false);

        return new TodayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayViewHolder holder, int position) {

        holder.title.setText(eventsModels.get(position).getTitle());
        holder.skillLevel.setText(eventsModels.get(position).getSkillLevel());
        holder.timeSpan.setText(eventsModels.get(position).getTimeSpan());

        holder.dailyTask.setText(getDailyTask(position, eventsModels.get(position).getDayCount()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgramOfflineModel model = eventsModels.get(position);
                eventsClickListener.onClick(model);
            }
        });

    }

    private String getDailyTask(int position, int dayCount) {

        String todayTask = "";

        switch (dayCount) {
            case 1:
                todayTask = eventsModels.get(position).getDay01();
                break;
            case 2:
                todayTask = eventsModels.get(position).getDay02();
                break;
            case 3:
                todayTask = eventsModels.get(position).getDay03();
                break;
            case 4:
                todayTask = eventsModels.get(position).getDay04();
                break;
            case 5:
                todayTask = eventsModels.get(position).getDay05();
                break;
            case 6:
                todayTask = eventsModels.get(position).getDay06();
                break;
            case 7:
                todayTask = eventsModels.get(position).getDay07();
                break;
        }

        return todayTask;
    }

    @Override
    public int getItemCount() {
        return eventsModels.size();
    }

    public static class TodayViewHolder extends RecyclerView.ViewHolder {

        TextView title, timeSpan, skillLevel, dailyTask;

        public TodayViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.program_name_programs_card);
            timeSpan = itemView.findViewById(R.id.time_programs_card);
            skillLevel = itemView.findViewById(R.id.skill_level_programs_card);
            dailyTask = itemView.findViewById(R.id.daily_tasks_programs_card);
        }
    }

}
