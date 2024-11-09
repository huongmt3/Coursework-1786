package com.example.coursework_1786.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.coursework_1786.R;
import com.example.coursework_1786.database.YogaDatabase;
import com.example.coursework_1786.models.YogaCourse;

import java.util.Calendar;

public class CreateYogaCourseActivity extends AppCompatActivity {
    private YogaDatabase yogaDatabase;
    Button backToCourse;
    TextView timeText;
    Button pickTimeBtn;
    Button submitCreateCourseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.labelTimeOfCourse), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        yogaDatabase = Room
                .databaseBuilder(getApplicationContext(), YogaDatabase.class, "yoga_db")
                .allowMainThreadQueries()
                .build();

        backToCourse = findViewById(R.id.backToCourse);
        timeText = findViewById(R.id.labelDisplayTime);
        pickTimeBtn = findViewById(R.id.btnPickTime);
        submitCreateCourseBtn = findViewById(R.id.submitCreateCourse);

        backToCourse.setOnClickListener(v -> setBackToCourse());

        pickTimeBtn.setOnClickListener(v -> showTimePickerDialog());

        submitCreateCourseBtn.setOnClickListener(v -> submitCreateCourse());
    }

    private void setBackToCourse(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaCourseFragment");
        startActivity(intent);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeText.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void submitCreateCourse(){
        Spinner dayOfTheWeeksSpinner = findViewById(R.id.spinnerDayOfTheWeek);
        String selectedDay = dayOfTheWeeksSpinner.getSelectedItem().toString();
        String timeOfCourse = ((TextView) findViewById(R.id.labelDisplayTime)).getText().toString();
        String capacityText = ((EditText) findViewById(R.id.textCapacity)).getText().toString();
        String duration = ((TextView) findViewById(R.id.textDuration)).getText().toString();
        String pricePerClass = ((TextView) findViewById(R.id.textPricePerClass)).getText().toString();
        RadioGroup typeOfClassRadio = findViewById(R.id.radioTypeOfClass);
        String description = ((TextView) findViewById(R.id.textDescription)).getText().toString();

        int selectedTypeOfClass = typeOfClassRadio.getCheckedRadioButtonId();

        if (selectedDay.isEmpty() || timeOfCourse.isEmpty() || capacityText.trim().isEmpty() ||
            duration.trim().isEmpty() || pricePerClass.trim().isEmpty() || selectedTypeOfClass == -1){
            displayRequiredFieldsAlert();
            return;
        }

        int capacity = Integer.parseInt(capacityText);

        YogaCourse yogaCourse = new YogaCourse();
        yogaCourse.day_of_the_week = selectedDay;
        yogaCourse.time_of_course = timeOfCourse;
        yogaCourse.capacity = capacity;
        yogaCourse.duration = duration;
        yogaCourse.type_of_class = selectedTypeOfClass;
        yogaCourse.price_per_class = pricePerClass;
        yogaCourse.description = description;

        long courseId = yogaDatabase.yogaCourseDao().createYogaCourse(yogaCourse);

        Toast.makeText(this, "Yoga course has been created with id: " + courseId,
                Toast.LENGTH_LONG
        ).show();
    }

    private void displayRequiredFieldsAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage(
                        "Please fill all required fields."
                )
                .setNeutralButton("OK", (dialogInterface, i) -> {

                })
                .show();
    }

    private void displayNextAlert(YogaCourse yogaCourse){
        new AlertDialog.Builder(this)
                .setTitle("Details Entered")
                .setMessage(
                        "Details: \n" +
                                yogaCourse.day_of_the_week + "\n" +
                                yogaCourse.time_of_course + "\n" +
                                yogaCourse.capacity + "\n" +
                                yogaCourse.duration + "\n" +
                                yogaCourse.type_of_class + "\n" +
                                yogaCourse.price_per_class + "\n" +
                                yogaCourse.description
                )
                .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}