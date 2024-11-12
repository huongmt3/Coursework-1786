package com.example.coursework_1786.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coursework_1786.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateYogaClassActivity extends AppCompatActivity {

    Button backToClassBtn;
    Button pickDateBtn;
    TextView dateText;

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

        long courseId = getIntent().getLongExtra("course_id", 0L);
        String dayOfTheWeek = getIntent().getStringExtra("day_of_the_week");

        System.out.println(dayOfTheWeek);

        backToClassBtn = findViewById(R.id.backToClass);
        pickDateBtn = findViewById(R.id.btnPickDate);
        dateText = findViewById(R.id.labelDisplayDate);

        backToClassBtn.setOnClickListener(v -> setBackToClasses(courseId));

        pickDateBtn.setOnClickListener(v -> showDatePickerDialog(dayOfTheWeek));
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

    public static int getDayOfWeek(String dayString) {
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

}