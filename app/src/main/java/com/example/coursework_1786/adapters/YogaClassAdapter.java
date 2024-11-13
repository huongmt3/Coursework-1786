package com.example.coursework_1786.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.coursework_1786.R;
import com.example.coursework_1786.activities.EditYogaClassActivity;
import com.example.coursework_1786.database.YogaDatabase;
import com.example.coursework_1786.models.YogaClass;
import com.example.coursework_1786.models.YogaCourse;

import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.YogaClassViewHolder> {
    private YogaDatabase yogaDatabase;
    List<YogaClass> yogaClasses;
    Context context;
    long courseId;

    public YogaClassAdapter(List<YogaClass> yogaClasses, Context context) {
        this.yogaClasses = yogaClasses;
        this.context = context;
    }

    public YogaClassAdapter(List<YogaClass> yogaClasses, Context context, long courseId) {
        this.yogaClasses = yogaClasses;
        this.context = context;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_yoga_class, parent, false);

        yogaDatabase = Room
                .databaseBuilder(context.getApplicationContext(), YogaDatabase.class, "yoga_database")
                .allowMainThreadQueries()
                .build();

        return new YogaClassAdapter.YogaClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        YogaClass yogaClass = yogaClasses.get(position);
        holder.classDate.setText(yogaClass.date);
        holder.classTeacher.setText(yogaClass.teacher);

        YogaCourse yogaCourse = yogaDatabase.yogaCourseDao().getById(yogaClass.yoga_course_id);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditYogaClassActivity.class);
            intent.putExtra("class_id", yogaClass.id);
            intent.putExtra("course_id", yogaClass.yoga_course_id);
            intent.putExtra("extra_course_id", this.courseId);
            intent.putExtra("day_of_the_week", yogaCourse.day_of_the_week);
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
