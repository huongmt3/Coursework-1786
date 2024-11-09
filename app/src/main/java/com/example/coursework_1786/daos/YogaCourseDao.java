package com.example.coursework_1786.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.coursework_1786.models.YogaCourse;

import java.util.List;

@Dao
public interface YogaCourseDao {
    @Insert
    long createYogaCourse(YogaCourse yogaCourse);

    @Query("SELECT * FROM yoga_courses")
    List<YogaCourse> getAllYogaCourses();
}
