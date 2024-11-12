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
import com.example.coursework_1786.models.YogaClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateYogaClassActivity extends AppCompatActivity {
    private YogaDatabase yogaDatabase;
    Button backToClassBtn;
    Button pickDateBtn;
    TextView dateText;
    EditText teacherText;
    EditText commentsText;
    Button submitCreateClassBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_yoga_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createClass), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        yogaDatabase = Room
                .databaseBuilder(getApplicationContext(), YogaDatabase.class, "comp1786_yoga_db")
                .allowMainThreadQueries()
                .build();

        long courseId = getIntent().getLongExtra("course_id", 0L);
        String dayOfTheWeek = getIntent().getStringExtra("day_of_the_week");

        System.out.println(dayOfTheWeek);

        backToClassBtn = findViewById(R.id.backToClass);
        pickDateBtn = findViewById(R.id.btnPickDate);
        dateText = findViewById(R.id.labelDisplayDate);
        teacherText = findViewById(R.id.textTeacher);
        commentsText = findViewById(R.id.textComments);
        submitCreateClassBtn = findViewById(R.id.submitCreateClass);

        backToClassBtn.setOnClickListener(v -> setBackToClasses(courseId));

        pickDateBtn.setOnClickListener(v -> showDatePickerDialog(dayOfTheWeek));

        submitCreateClassBtn.setOnClickListener(v -> submitCreateYogaClass());
    }

    private void setBackToClasses(long courseId){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaClassFragment");
        intent.putExtra("course_id", courseId);
        startActivity(intent);
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

    private void submitCreateYogaClass(){
        String date = dateText.getText().toString().trim();
        String teacher = teacherText.getText().toString().trim();

        if (date.isEmpty() || teacher.isEmpty()){
            displayRequiredFieldsAlert();
            return;
        }

        String comments = commentsText.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("Details Entered")
                .setMessage(
                        "Details: \n" +
                                "Date: " + date + "\n" +
                                "Teacher: " + teacher + "\n" +
                                "Additional comments: " + comments + "\n"
                )
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    YogaClass yogaClass = new YogaClass();
                    long courseId = getIntent().getLongExtra("course_id", 0L);
                    yogaClass.yoga_course_id = courseId;
                    yogaClass.date = date;
                    yogaClass.teacher = teacher;
                    yogaClass.additional_comments = comments;
                    long classId = yogaDatabase.yogaClassDao().create(yogaClass);
                    Toast.makeText(this, "Yoga class has been created with id: " + classId,
                            Toast.LENGTH_LONG
                    ).show();
                    setBackToClasses(courseId);
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
}