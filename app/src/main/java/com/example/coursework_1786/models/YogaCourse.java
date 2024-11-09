package com.example.coursework_1786.models;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "yoga_courses")
public class YogaCourse {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String day_of_the_week;
    public String time_of_course;
    public int capacity;
    public String duration;
    public String price_per_class;
    public int type_of_class;
    public String description;
}
