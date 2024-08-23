package com.aftershade.workmode.HelperClasses.Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Models.ScheduleHelper;
import com.aftershade.workmode.R;

import java.util.ArrayList;

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ScheduleHolder>{

    ArrayList<ScheduleHelper> scheduleArray;

    public ScheduleRecyclerAdapter(ArrayList<ScheduleHelper> scheduleHelpers) {
        this.scheduleArray = scheduleHelpers;
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_card, parent, false);

        return new ScheduleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {

        int day = position + 1;

        holder.dayText.append(String.valueOf(day));
        holder.descText.setText(scheduleArray.get(position).getDesc());

    }

    @Override
    public int getItemCount() {
        return scheduleArray.size();
    }

    public static class ScheduleHolder extends RecyclerView.ViewHolder {
        public TextView dayText, descText;

        public ScheduleHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.day_number_schedule_card);
            descText = itemView.findViewById(R.id.description_schedule_card);
        }
    }


}
