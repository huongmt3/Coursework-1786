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

    @Query("SELECT yc.* FROM yoga_classes yc " +
            "JOIN yoga_courses yco ON yc.yoga_course_id = yco.id " +
            "WHERE (:teacher = '' OR yc.teacher LIKE '%' || :teacher || '%') " +
            "AND (:day = 'Select day' OR yco.day_of_the_week = :day)")
    List<YogaClass> searchByTeacherAndDay(String teacher, String day);

    @Update
    int update(YogaClass yogaClass);

    @Delete
    int delete(YogaClass yogaClass);
}
