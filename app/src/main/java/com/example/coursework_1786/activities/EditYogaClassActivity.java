package com.example.coursework_1786.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.coursework_1786.R;
import com.example.coursework_1786.database.YogaDatabase;
import com.example.coursework_1786.models.FirebaseYogaClass;
import com.example.coursework_1786.models.YogaClass;
import com.example.coursework_1786.utils.NetworkUtils;
import com.example.coursework_1786.utils.YogaFirebaseDbUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditYogaClassActivity extends AppCompatActivity {
    private YogaDatabase yogaDatabase;
    Button backToClassBtn;
    Button pickDateBtn;
    TextView dateText;
    EditText teacherText;
    EditText commentsText;
    Button submitUpdateClassBtn;
    Button deleteClassBtn;
    YogaClass yogaClass = new YogaClass();
    long extraCourseId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_yoga_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editClass), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        yogaDatabase = Room
                .databaseBuilder(this, YogaDatabase.class, "yoga_database")
                .allowMainThreadQueries()
                .build();

        backToClassBtn = findViewById(R.id.backToClass);
        pickDateBtn = findViewById(R.id.btnPickDate);
        dateText = findViewById(R.id.labelDisplayDate);
        teacherText = findViewById(R.id.textTeacher);
        commentsText = findViewById(R.id.textComments);
        submitUpdateClassBtn = findViewById(R.id.submitUpdateClass);
        deleteClassBtn = findViewById(R.id.btnDeleteClass);

        Intent intent = getIntent();
        long classId = intent.getLongExtra("class_id", 0L);
        long courseId = intent.getLongExtra("course_id", 0L);
        extraCourseId = intent.getLongExtra("extra_course_id", 0L);
        String dayOfTheWeek = intent.getStringExtra("day_of_the_week");
        String date = intent.getStringExtra("date");
        String teacher = intent.getStringExtra("teacher");
        String comments = intent.getStringExtra("additional_comments");

        yogaClass.id = classId;
        yogaClass.yoga_course_id = courseId;
        yogaClass.date = date;
        yogaClass.teacher = teacher;
        yogaClass.additional_comments = comments;

        dateText.setText(date);
        teacherText.setText(teacher);
        commentsText.setText(comments);

        backToClassBtn.setOnClickListener(v -> setBackToClasses());

        pickDateBtn.setOnClickListener(v -> showDatePickerDialog(dayOfTheWeek));

        deleteClassBtn.setOnClickListener(v -> displayConfirmDeleteAlert());

        submitUpdateClassBtn.setOnClickListener(v -> updateYogaClass(classId, courseId));
    }

    private void setBackToClasses(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaClassFragment");
        if (extraCourseId != 0){
            intent.putExtra("course_id", extraCourseId);
        }
        startActivity(intent);
    }

    private void displayConfirmDeleteAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage("Are you sure to delete this class?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    deleteYogaClass();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void deleteYogaClass(){
        long deletedClassId = yogaDatabase.yogaClassDao().delete(yogaClass);

        Toast.makeText(this, "Yoga class has been deleted, id: " + deletedClassId,
                Toast.LENGTH_LONG
        ).show();

        setBackToClasses();
        YogaFirebaseDbUtils.deletedClassIds.add(yogaClass.id.toString());
        if (NetworkUtils.isNetworkAvailable(this)) {
            YogaFirebaseDbUtils.syncYogaClassesToFirebaseDb();
        } else {
            System.out.println("No network connection. Sync canceled.");
        }
    }

    private void updateYogaClass(long classId, long courseId){
        String date = dateText.getText().toString();
        String teacher = teacherText.getText().toString().trim();
        String comments = commentsText.getText().toString().trim();

        if (date.isEmpty() || teacher.isEmpty()){
            displayRequiredFieldsAlert();
            return;
        }

        yogaClass.date = date;
        yogaClass.teacher = teacher;
        yogaClass.additional_comments = comments;

        new AlertDialog.Builder(this)
                .setTitle("Details Entered")
                .setMessage(
                        "Course ID: " + courseId + "\n" +
                        "Date: " + date + "\n" +
                        "Teacher: " + teacher + "\n" +
                        "Additional comments: " + comments + "\n"
                )
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    YogaClass yogaClass = new YogaClass();
                    yogaClass.id = classId;
                    yogaClass.yoga_course_id = courseId;
                    yogaClass.date = date;
                    yogaClass.teacher = teacher;
                    yogaClass.additional_comments = comments;
                    yogaDatabase.yogaClassDao().update(yogaClass);
                    Toast.makeText(this, "Yoga class has been updated, id: " + classId,
                            Toast.LENGTH_LONG
                    ).show();
                    syncToFirebaseDb(yogaClass);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void displayRequiredFieldsAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage("Please fill all required fields.")
                .setNeutralButton("OK", (dialogInterface, i) -> {})
                .show();
    }

    private static int getDayOfWeek(String dayString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            Date date = format.parse(dayString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            return calendar.get(Calendar.DAY_OF_WEEK);

        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showDatePickerDialog(String dayOfTheWeek) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    if (selectedDate.get(Calendar.DAY_OF_WEEK) == getDayOfWeek(dayOfTheWeek)) {
                        String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateText.setText(formattedDate);
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Notification")
                                .setMessage(
                                        "You can only select " + dayOfTheWeek + "!"
                                )
                                .setNeutralButton("OK", (dialogInterface, i) -> {})
                                .show();
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void syncToFirebaseDb(YogaClass yogaClass){
        FirebaseYogaClass firebaseYogaClass = new FirebaseYogaClass();
        firebaseYogaClass.id = yogaClass.id.toString();
        firebaseYogaClass.yogaCourseId = yogaClass.yoga_course_id.toString();
        firebaseYogaClass.date = yogaClass.date;
        firebaseYogaClass.teacher = yogaClass.teacher;
        firebaseYogaClass.additionalComments = yogaClass.additional_comments;

        YogaFirebaseDbUtils.firebaseYogaClasses.add(firebaseYogaClass);

        if (NetworkUtils.isNetworkAvailable(this)) {
            YogaFirebaseDbUtils.syncYogaClassesToFirebaseDb();
        } else {
            System.out.println("No network connection. Sync canceled.");
        }
    }
}