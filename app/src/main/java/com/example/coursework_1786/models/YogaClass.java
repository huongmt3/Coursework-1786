package com.example.coursework_1786.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "yoga_classes",
        foreignKeys = @ForeignKey(
                entity = YogaCourse.class,
                parentColumns = "id",
                childColumns = "yoga_course_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("yoga_course_id")}
)
public class YogaClass {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public Long yoga_course_id;
    public String date;
    public String teacher;
    public String additional_comments;
}
