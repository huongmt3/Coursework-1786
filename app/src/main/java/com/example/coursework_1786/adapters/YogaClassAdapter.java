package com.example.coursework_1786.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework_1786.R;
import com.example.coursework_1786.activities.EditYogaCourseActivity;
import com.example.coursework_1786.models.YogaClass;

import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.YogaClassViewHolder> {

    List<YogaClass> yogaClasses;
    Context context;

    public YogaClassAdapter(List<YogaClass> yogaClasses, Context context) {
        this.yogaClasses = yogaClasses;
        this.context = context;
    }

    @NonNull
    @Override
    public YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_yoga_class, parent, false);
        return new YogaClassAdapter.YogaClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        YogaClass yogaClass = yogaClasses.get(position);
        holder.classDate.setText(yogaClass.date);
        holder.classTeacher.setText(yogaClass.teacher);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditYogaCourseActivity.class);
            intent.putExtra("class_id", yogaClass.id);
            intent.putExtra("course_id", yogaClass.yoga_course_id);
            intent.putExtra("date", yogaClass.date);
            intent.putExtra("teacher", yogaClass.teacher);
            intent.putExtra("additional_comments", yogaClass.additional_comments);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }

    public static class YogaClassViewHolder extends RecyclerView.ViewHolder {
        TextView classDate, classTeacher;

        public YogaClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classDate = itemView.findViewById(R.id.classDate);
            classTeacher = itemView.findViewById(R.id.classTeacher);
        }
    }
}
