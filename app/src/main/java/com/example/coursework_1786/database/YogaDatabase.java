package com.example.coursework_1786.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.coursework_1786.daos.YogaClassDao;
import com.example.coursework_1786.daos.YogaCourseDao;
import com.example.coursework_1786.models.YogaClass;
import com.example.coursework_1786.models.YogaCourse;

@Database(entities = {YogaCourse.class, YogaClass.class}, version = 1)
public abstract class YogaDatabase extends RoomDatabase {
    public abstract YogaCourseDao yogaCourseDao();
    public abstract YogaClassDao yogaClassDao();
}
