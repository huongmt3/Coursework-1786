package com.example.coursework_1786.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.coursework_1786.models.YogaClass;

import java.util.List;

@Dao
public interface YogaClassDao {
    @Insert
    long create(YogaClass yogaClass);

    @Query("SELECT * FROM yoga_classes")
    List<YogaClass> getAll();

    @Query("SELECT * FROM yoga_classes WHERE yoga_course_id = :yogaCourseId")
    List<YogaClass> getByYogaCourseId(long yogaCourseId);

    @Query("SELECT * FROM yoga_classes WHERE id = :id")
    YogaClass getById(long id);

    @Update
    int update(YogaClass yogaClass);

    @Delete
    int delete(YogaClass yogaClass);
}
