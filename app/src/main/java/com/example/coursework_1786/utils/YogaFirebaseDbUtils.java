package com.example.coursework_1786.utils;

import androidx.annotation.NonNull;

import com.example.coursework_1786.models.FirebaseYogaClass;
import com.example.coursework_1786.models.FirebaseYogaCourse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class YogaFirebaseDbUtils {
    static FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();
    static DatabaseReference yogaCoursesRef = firebaseDb.getReference("yoga_courses");
    static DatabaseReference yogaClassesRef = firebaseDb.getReference("yoga_classes");
    public static List<FirebaseYogaCourse> firebaseYogaCourses = new ArrayList<>();
    public static List<FirebaseYogaClass> firebaseYogaClasses = new ArrayList<>();
    public static List<String> deletedCourseIds = new ArrayList<>();
    public static List<String> deletedClassIds = new ArrayList<>();

    public static void syncYogaCoursesToFirebaseDb(){
        if (!firebaseYogaCourses.isEmpty()){
            Iterator<FirebaseYogaCourse> yogaCourseIterator = firebaseYogaCourses.iterator();
            while (yogaCourseIterator.hasNext()){
                FirebaseYogaCourse yogaCourse = yogaCourseIterator.next();
                HashMap<String, Object> yogaCourseHarshMap = getYogaCourseHashMap(yogaCourse);
                yogaCoursesRef.child(yogaCourse.id).setValue(yogaCourseHarshMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        System.out.println("Sync course successfully, id: " + yogaCourse.id);
                    }
                    else{
                        System.out.println("Sync course failed, id: " + yogaCourse.id);
                    }
                });
                yogaCourseIterator.remove();
            }
        }

        if (!deletedCourseIds.isEmpty()){
            Iterator<String> deletedCourseIdIterator = deletedCourseIds.iterator();
            while (deletedCourseIdIterator.hasNext()){
                String deletedCourseId = deletedCourseIdIterator.next();
                DatabaseReference deletedCourseIdRef = yogaCoursesRef.child(deletedCourseId);
                deletedCourseIdRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        System.out.println("Delete course successfully, id: " + deletedCourseId);
                    }
                    else{
                        System.out.println("Delete course failed, id: " + deletedCourseId);
                    }
                });
                deletedCourseIdIterator.remove();
            }
        }
    }

    @NonNull
    private static HashMap<String, Object> getYogaCourseHashMap(FirebaseYogaCourse yogaCourse) {
        HashMap<String, Object> yogaCourseHarshMap = new HashMap<>();
        yogaCourseHarshMap.put("day_of_the_week", yogaCourse.dayOfTheWeek);
        yogaCourseHarshMap.put("time_of_course", yogaCourse.timeOfCourse);
        yogaCourseHarshMap.put("capacity", yogaCourse.capacity);
        yogaCourseHarshMap.put("duration", yogaCourse.duration);
        yogaCourseHarshMap.put("price_per_class", yogaCourse.pricePerClass);
        yogaCourseHarshMap.put("type_of_class", yogaCourse.typeOfClass);
        yogaCourseHarshMap.put("description", yogaCourse.description);
        return yogaCourseHarshMap;
    }

    public static void syncYogaClassesToFirebaseDb(){
        if (!firebaseYogaClasses.isEmpty()){
            Iterator<FirebaseYogaClass> iterator = firebaseYogaClasses.iterator();
            while (iterator.hasNext()){
                FirebaseYogaClass yogaClass = iterator.next();
                HashMap<String, Object> yogaClassHarshMap = getYogaClassHashMap(yogaClass);
                yogaClassesRef.child(yogaClass.id).setValue(yogaClassHarshMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        System.out.println("Sync class successfully, id: " + yogaClass.id);
                    }
                    else{
                        System.out.println("Sync class failed, id: " + yogaClass.id);
                    }
                });;
                iterator.remove();
            }
        }

        if (!deletedClassIds.isEmpty()){
            Iterator<String> deletedClassIdIterator = deletedClassIds.iterator();
            while (deletedClassIdIterator.hasNext()){
                String deletedClassId = deletedClassIdIterator.next();
                DatabaseReference deletedCourseIdRef = yogaClassesRef.child(deletedClassId);
                deletedCourseIdRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        System.out.println("Sync class successfully, id: " + deletedClassId);
                    }
                    else{
                        System.out.println("Sync class failed, id: " + deletedClassId);
                    }
                });
                deletedClassIdIterator.remove();
            }
        }
    }

    @NonNull
    private static HashMap<String, Object> getYogaClassHashMap(FirebaseYogaClass yogaClass) {
        HashMap<String, Object> yogaClassHarshMap = new HashMap<>();
        yogaClassHarshMap.put("yoga_course_id", yogaClass.yogaCourseId);
        yogaClassHarshMap.put("date", yogaClass.date);
        yogaClassHarshMap.put("teacher", yogaClass.teacher);
        yogaClassHarshMap.put("additional_comments", yogaClass.additionalComments);
        return yogaClassHarshMap;
    }
}
