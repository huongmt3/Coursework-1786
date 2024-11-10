package com.example.coursework_1786.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
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

import java.util.Arrays;
import java.util.Calendar;

public class EditYogaCourseActivity extends AppCompatActivity {
    private YogaDatabase yogaDatabase;
    Button backToCourseBtn;
    TextView timeText;
    Button pickTimeBtn;
    Button submitUpdateCourseBtn;
    Spinner daysOfTheWeekSpinner;
    EditText capacityText;
    EditText durationText;
    EditText pricePerClassText;
    EditText descriptionText;
    RadioGroup typeOfClassRadio;
    Button saveCourseBtn;
    Button deleteCourseBtn;

    @SuppressLint({"MissingInflatedId", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_yoga_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editCourse), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        yogaDatabase = Room
                .databaseBuilder(getApplicationContext(), YogaDatabase.class, "yoga_db")
                .allowMainThreadQueries()
                .build();

        Intent intent = getIntent();
        Long courseId = intent.getLongExtra("course_id", 0L);
        String dayOfTheWeek = intent.getStringExtra("day_of_the_week");
        String timeOfCourse = intent.getStringExtra("time_of_course");
        int capacity = intent.getIntExtra("capacity", 0);
        String duration = intent.getStringExtra("duration");
        String pricePerClass = intent.getStringExtra("price_per_class");
        int typeOfClass = intent.getIntExtra("type_of_class", 0);
        String description = intent.getStringExtra("description");

        System.out.printf("%d %s %s %d %s %s %d %s%n", courseId, dayOfTheWeek, timeOfCourse,
                capacity, duration, pricePerClass, typeOfClass, description);

        daysOfTheWeekSpinner = findViewById(R.id.spinnerDayOfTheWeek);
        timeText = findViewById(R.id.labelDisplayTime);
        backToCourseBtn = findViewById(R.id.backToCourse);
        pickTimeBtn = findViewById(R.id.btnPickTime);
        submitUpdateCourseBtn = findViewById(R.id.submitCreateCourse);
        capacityText = findViewById(R.id.textCapacity);
        durationText = findViewById(R.id.textDuration);
        pricePerClassText = findViewById(R.id.textPricePerClass);
        descriptionText = findViewById(R.id.textDescription);
        typeOfClassRadio = findViewById(R.id.radioTypeOfClass);
        saveCourseBtn = findViewById(R.id.submitEditCourse);
        deleteCourseBtn = findViewById(R.id.btnDeleteCourse);

        String[] daysOfTheWeek = getResources().getStringArray(R.array.daysOfTheWeek);
        int dayPosition = Arrays.asList(daysOfTheWeek).indexOf(dayOfTheWeek);
        daysOfTheWeekSpinner.setSelection(dayPosition);

        timeText.setText(timeOfCourse);
        capacityText.setText(String.valueOf(capacity));
        durationText.setText(duration);
        pricePerClassText.setText(pricePerClass);
        descriptionText.setText(description);
        typeOfClassRadio.check(typeOfClass);

        backToCourseBtn.setOnClickListener(v -> setBackToCourse());

        pickTimeBtn.setOnClickListener(v -> showTimePickerDialog());

        saveCourseBtn.setOnClickListener(v -> saveYogaCourse(courseId));

        deleteCourseBtn.setOnClickListener(v -> displayConfirmDeleteAlert(courseId));
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

    private void saveYogaCourse(Long courseId){
        String dayOfTheWeek = daysOfTheWeekSpinner.getSelectedItem().toString();
        String timeOfCourse = timeText.getText().toString();
        String capacity = capacityText.getText().toString().trim();
        String duration = durationText.getText().toString().trim();
        String pricePerClass = pricePerClassText.getText().toString().trim();
        int typeOfClass = typeOfClassRadio.getCheckedRadioButtonId();
        String description = descriptionText.getText().toString().trim();

        if (dayOfTheWeek.isEmpty() || timeOfCourse.isEmpty() || capacity.isEmpty() ||
            duration.isEmpty() || pricePerClass.isEmpty() || typeOfClass == -1)
        {
            displayRequiredFieldsAlert();
            return;
        }

        YogaCourse yogaCourse = new YogaCourse();
        yogaCourse.id = courseId;
        yogaCourse.day_of_the_week = dayOfTheWeek;
        yogaCourse.time_of_course = timeOfCourse;
        yogaCourse.capacity = Integer.parseInt(capacity);
        yogaCourse.duration = duration;
        yogaCourse.price_per_class = pricePerClass;
        yogaCourse.type_of_class = typeOfClass;
        yogaCourse.description = description;

        long updatedCourseId = yogaDatabase.yogaCourseDao().update(yogaCourse);

        Toast.makeText(this, "Yoga course has been updated, id: " + updatedCourseId,
                Toast.LENGTH_LONG
        ).show();

        setBackToCourse();
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

    private void displayConfirmDeleteAlert(Long courseId) {
        new AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage("Are you sure to delete this course?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    deleteYogaCourse(courseId);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void deleteYogaCourse(Long courseId){
        String dayOfTheWeek = daysOfTheWeekSpinner.getSelectedItem().toString();
        String timeOfCourse = timeText.getText().toString();
        String capacity = capacityText.getText().toString().trim();
        String duration = durationText.getText().toString().trim();
        String pricePerClass = pricePerClassText.getText().toString().trim();
        int typeOfClass = typeOfClassRadio.getCheckedRadioButtonId();
        String description = descriptionText.getText().toString().trim();

        YogaCourse yogaCourse = new YogaCourse();
        yogaCourse.id = courseId;
        yogaCourse.day_of_the_week = dayOfTheWeek;
        yogaCourse.time_of_course = timeOfCourse;
        yogaCourse.capacity = !capacity.isEmpty() ? Integer.parseInt(capacity) : 0;
        yogaCourse.duration = duration;
        yogaCourse.price_per_class = pricePerClass;
        yogaCourse.type_of_class = typeOfClass;
        yogaCourse.description = description;

        long deletedCourseId = yogaDatabase.yogaCourseDao().delete(yogaCourse);

        Toast.makeText(this, "Yoga course has been deleted, id: " + deletedCourseId,
                Toast.LENGTH_LONG
        ).show();

        setBackToCourse();
    }
}