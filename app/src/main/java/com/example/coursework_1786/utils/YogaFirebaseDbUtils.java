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
    //Initialise firebase instance and references
    static FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();
    static DatabaseReference yogaCoursesRef = firebaseDb.getReference("yoga_courses");
    static DatabaseReference yogaClassesRef = firebaseDb.getReference("yoga_classes");

    //List to store data
    public static List<FirebaseYogaCourse> firebaseYogaCourses = new ArrayList<>();
    public static List<FirebaseYogaClass> firebaseYogaClasses = new ArrayList<>();
    public static List<String> deletedCourseIds = new ArrayList<>();
    public static List<String> deletedClassIds = new ArrayList<>();

    //Sync course to firebase
    public static void syncYogaCoursesToFirebaseDb(){
        //Check whether there are any new course to sync or not
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
                //Remove course from the list
                yogaCourseIterator.remove();
            }
        }

        //Check whether there are any deleted course to delete or not
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
                //Remove course from the list
                deletedCourseIdIterator.remove();
            }
        }
    }

    //Helper to convert course to hashmap
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

    //Sync class to the firebase
    public static void syncYogaClassesToFirebaseDb(){
        //Check whether there are any new class to sync or not
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
                //Remove class from the list
                iterator.remove();
            }
        }

        //Check whether there are any deleted class to delete or not
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
                //Remove class from the list
                deletedClassIdIterator.remove();
            }
        }
    }

    //Helper to convert class to hashmap
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
