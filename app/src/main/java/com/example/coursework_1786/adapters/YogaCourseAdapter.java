package com.example.coursework_1786.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework_1786.R;
import com.example.coursework_1786.models.YogaCourse;

import java.util.List;

public class YogaCourseAdapter extends RecyclerView.Adapter<YogaCourseAdapter.YogaCourseViewHolder> {

    private List<YogaCourse> yogaCourses;

    public YogaCourseAdapter(List<YogaCourse> yogaCourses) {
        this.yogaCourses = yogaCourses;
    }

    @NonNull
    @Override
    public YogaCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_yoga_course, parent, false);
        return new YogaCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaCourseViewHolder holder, int position) {
        YogaCourse yogaCourse = yogaCourses.get(position);
        holder.courseDay.setText(yogaCourse.day_of_the_week);
        holder.courseTime.setText(yogaCourse.time_of_course);
    }

    @Override
    public int getItemCount() {
        return yogaCourses.size();
    }

    public static class YogaCourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseDay, courseTime;

        public YogaCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseDay = itemView.findViewById(R.id.courseDay);
            courseTime = itemView.findViewById(R.id.courseTime);
        }
    }
}
