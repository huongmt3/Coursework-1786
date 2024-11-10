package com.example.coursework_1786.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.coursework_1786.models.YogaCourse;

import java.util.List;

@Dao
public interface YogaCourseDao {
    @Insert
    long create(YogaCourse yogaCourse);

    @Query("SELECT * FROM yoga_courses")
    List<YogaCourse> getAll();

    @Query("SELECT * FROM yoga_courses WHERE id = :id")
    YogaCourse getById(long id);

    @Update
    int update(YogaCourse yogaCourse);

    @Delete
    int delete(YogaCourse yogaCourse);
}
