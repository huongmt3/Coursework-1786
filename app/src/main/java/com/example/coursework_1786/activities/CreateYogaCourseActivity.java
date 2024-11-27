package com.example.coursework_1786.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.example.coursework_1786.models.FirebaseYogaCourse;
import com.example.coursework_1786.models.YogaCourse;
import com.example.coursework_1786.utils.NetworkUtils;
import com.example.coursework_1786.utils.YogaFirebaseDbUtils;

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
        setContentView(R.layout.activity_create_yoga_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.labelDate), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialise the database
        yogaDatabase = Room
                .databaseBuilder(getApplicationContext(), YogaDatabase.class, "yoga_database")
                .allowMainThreadQueries()
                .build();

        //UI components
        backToCourse = findViewById(R.id.backToCourse);
        timeText = findViewById(R.id.labelDisplayDate);
        pickTimeBtn = findViewById(R.id.btnPickDate);
        submitCreateCourseBtn = findViewById(R.id.submitCreateClass);

        //Set buttons action
        backToCourse.setOnClickListener(v -> setBackToCourse());

        pickTimeBtn.setOnClickListener(v -> showTimePickerDialog());

        submitCreateCourseBtn.setOnClickListener(v -> submitCreateCourse());
    }

    //Navigate back to the course list
    private void setBackToCourse(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaCourseFragment");
        startActivity(intent);
    }

    //Show time picker
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

    //add new course
    private void submitCreateCourse(){
        Spinner dayOfTheWeeksSpinner = findViewById(R.id.spinnerDayOfTheWeek);
        String selectedDay = dayOfTheWeeksSpinner.getSelectedItem().toString();
        String timeOfCourse = ((TextView) findViewById(R.id.labelDisplayDate)).getText().toString();
        String capacityText = ((EditText) findViewById(R.id.textCapacity)).getText().toString().trim();
        String duration = ((TextView) findViewById(R.id.textTeacher)).getText().toString().trim();
        String pricePerClass = ((TextView) findViewById(R.id.textPricePerClass)).getText().toString().trim();
        RadioGroup typeOfClassRadio = findViewById(R.id.radioTypeOfClass);
        String description = ((TextView) findViewById(R.id.textComments)).getText().toString();

        int selectedTypeOfClass = typeOfClassRadio.getCheckedRadioButtonId();

        //Validation
        if (selectedDay.equals("Select day") || timeOfCourse.isEmpty() || capacityText.isEmpty() ||
            duration.isEmpty() || pricePerClass.isEmpty() || selectedTypeOfClass == -1){
            displayRequiredFieldsAlert();
            return;
        }

        int capacity = Integer.parseInt(capacityText);

        //Create new YogaCourse object
        YogaCourse yogaCourse = new YogaCourse();
        yogaCourse.day_of_the_week = selectedDay;
        yogaCourse.time_of_course = timeOfCourse;
        yogaCourse.capacity = capacity;
        yogaCourse.duration = duration;
        yogaCourse.type_of_class = selectedTypeOfClass;
        yogaCourse.price_per_class = pricePerClass;
        yogaCourse.description = description;

        //Show dialog to confirm information
        new AlertDialog.Builder(this)
                .setTitle("Details Entered")
                .setMessage(
                        "Day of the week: " + yogaCourse.day_of_the_week + "\n" +
                        "Time of course: " + yogaCourse.time_of_course + "\n" +
                        "Capacity: " + yogaCourse.capacity + "persons\n" +
                        "Duration: " + yogaCourse.duration + "\n" +
                        "Type of class: " + yogaCourse.type_of_class + "\n" +
                        "Price per class: " + yogaCourse.price_per_class + "\n" +
                        "Description: " + yogaCourse.description
                )
                //Update to the database
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    long courseId = yogaDatabase.yogaCourseDao().create(yogaCourse);
                    yogaCourse.id = courseId;
                    Toast.makeText(this, "Yoga course has been created with id: " + courseId,
                            Toast.LENGTH_LONG
                    ).show();
                    setBackToCourse();
                    syncToFirebaseDb(yogaCourse);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    //Require user to enter all fields
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

    //Firebase
    private void syncToFirebaseDb(YogaCourse yogaCourse){
        RadioButton selectedRadioBtn = findViewById(yogaCourse.type_of_class);
        String selectedTypeOfClass = selectedRadioBtn.getText().toString();

        FirebaseYogaCourse firebaseYogaCourse = new FirebaseYogaCourse();
        firebaseYogaCourse.id = yogaCourse.id.toString();
        firebaseYogaCourse.dayOfTheWeek = yogaCourse.day_of_the_week;
        firebaseYogaCourse.timeOfCourse = yogaCourse.time_of_course;
        firebaseYogaCourse.capacity = yogaCourse.capacity + " persons";
        firebaseYogaCourse.duration = yogaCourse.duration;
        firebaseYogaCourse.pricePerClass = yogaCourse.price_per_class;
        firebaseYogaCourse.typeOfClass = selectedTypeOfClass;
        firebaseYogaCourse.description = yogaCourse.description;

        YogaFirebaseDbUtils.firebaseYogaCourses.add(firebaseYogaCourse);

        if (NetworkUtils.isNetworkAvailable(this)) {
            YogaFirebaseDbUtils.syncYogaCoursesToFirebaseDb();
        } else {
            System.out.println("No network connection. Sync canceled.");
        }
    }

    //Close database when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (yogaDatabase != null && yogaDatabase.isOpen()) {
            yogaDatabase.close();
        }
    }
}