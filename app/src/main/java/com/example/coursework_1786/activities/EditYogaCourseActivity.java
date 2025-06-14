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
import com.example.coursework_1786.models.YogaClass;
import com.example.coursework_1786.models.YogaCourse;
import com.example.coursework_1786.utils.NetworkUtils;
import com.example.coursework_1786.utils.YogaFirebaseDbUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
    Button viewClassesBtn;
    YogaCourse yogaCourse = new YogaCourse();

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

        //Initialise the database
        yogaDatabase = Room
                .databaseBuilder(getApplicationContext(), YogaDatabase.class, "yoga_database")
                .allowMainThreadQueries()
                .build();

        //Retrieve course details from Intent
        Intent intent = getIntent();
        long courseId = intent.getLongExtra("course_id", 0L);
        String dayOfTheWeek = intent.getStringExtra("day_of_the_week");
        String timeOfCourse = intent.getStringExtra("time_of_course");
        int capacity = intent.getIntExtra("capacity", 0);
        String duration = intent.getStringExtra("duration");
        String pricePerClass = intent.getStringExtra("price_per_class");
        int typeOfClass = intent.getIntExtra("type_of_class", 0);
        String description = intent.getStringExtra("description");

        //Print the details for debugging
        System.out.printf("%d %s %s %d %s %s %d %s%n", courseId, dayOfTheWeek, timeOfCourse,
                capacity, duration, pricePerClass, typeOfClass, description);

        //Populate YogaCourse object with received details
        yogaCourse.id = courseId;
        yogaCourse.day_of_the_week = dayOfTheWeek;
        yogaCourse.time_of_course = timeOfCourse;
        yogaCourse.capacity = capacity;
        yogaCourse.duration = duration;
        yogaCourse.price_per_class = pricePerClass;
        yogaCourse.type_of_class = typeOfClass;
        yogaCourse.description = description;

        //UI components
        daysOfTheWeekSpinner = findViewById(R.id.spinnerDayOfTheWeek);
        timeText = findViewById(R.id.labelDisplayDate);
        backToCourseBtn = findViewById(R.id.backToCourse);
        pickTimeBtn = findViewById(R.id.btnPickDate);
        submitUpdateCourseBtn = findViewById(R.id.submitCreateClass);
        capacityText = findViewById(R.id.textCapacity);
        durationText = findViewById(R.id.textTeacher);
        pricePerClassText = findViewById(R.id.textPricePerClass);
        descriptionText = findViewById(R.id.textComments);
        typeOfClassRadio = findViewById(R.id.radioTypeOfClass);
        saveCourseBtn = findViewById(R.id.submitEditCourse);
        deleteCourseBtn = findViewById(R.id.btnDeleteCourse);
        viewClassesBtn = findViewById(R.id.btnViewClasses);

        //Set initial values
        String[] daysOfTheWeek = getResources().getStringArray(R.array.daysOfTheWeek);
        int dayPosition = Arrays.asList(daysOfTheWeek).indexOf(dayOfTheWeek);
        daysOfTheWeekSpinner.setSelection(dayPosition);

        timeText.setText(timeOfCourse);
        capacityText.setText(String.valueOf(capacity));
        durationText.setText(duration);
        pricePerClassText.setText(pricePerClass);
        descriptionText.setText(description);
        typeOfClassRadio.check(typeOfClass);

        //Set button actions
        backToCourseBtn.setOnClickListener(v -> setBackToCourse());

        pickTimeBtn.setOnClickListener(v -> showTimePickerDialog());

        saveCourseBtn.setOnClickListener(v -> saveYogaCourse(yogaCourse));

        deleteCourseBtn.setOnClickListener(v -> displayConfirmDeleteAlert(courseId));

        viewClassesBtn.setOnClickListener(v -> navigateToClasses(courseId, dayOfTheWeek));
    }

    //Go back to course
    private void setBackToCourse(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaCourseFragment");
        startActivity(intent);
    }

    //Show TimePicker to select date
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

    //Save to Fỉebase
    private void saveYogaCourse(YogaCourse yogaCourse){
        String dayOfTheWeek = daysOfTheWeekSpinner.getSelectedItem().toString();
        String timeOfCourse = timeText.getText().toString();
        String capacity = capacityText.getText().toString().trim();
        String duration = durationText.getText().toString().trim();
        String pricePerClass = pricePerClassText.getText().toString().trim();
        int typeOfClass = typeOfClassRadio.getCheckedRadioButtonId();
        String description = descriptionText.getText().toString().trim();

        //Validation
        if (dayOfTheWeek.equals("Select day") || timeOfCourse.isEmpty() || capacity.isEmpty() ||
            duration.isEmpty() || pricePerClass.isEmpty() || typeOfClass == -1)
        {
            //Show alert
            displayRequiredFieldsAlert();
            return;
        }

        //Set values in yogaCourse object
        yogaCourse.day_of_the_week = dayOfTheWeek;
        yogaCourse.time_of_course = timeOfCourse;
        yogaCourse.capacity = Integer.parseInt(capacity);
        yogaCourse.duration = duration;
        yogaCourse.price_per_class = pricePerClass;
        yogaCourse.type_of_class = typeOfClass;
        yogaCourse.description = description;

        //Show dialog to confirm the information
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
                    yogaDatabase.yogaCourseDao().update(yogaCourse);

                    Toast.makeText(this, "Yoga course has been updated, id: " + yogaCourse.id,
                            Toast.LENGTH_LONG
                    ).show();
                    //Firebase
                    syncToFirebaseDb(yogaCourse);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    //Show alert when data is missing
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

    //Delete confirm dialog
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

    //Delete course
    private void deleteYogaCourse(Long courseId){
        List<YogaClass> yogaClasses = yogaDatabase.yogaClassDao().getByYogaCourseId(courseId);
        if (!yogaClasses.isEmpty()){
            displayCannotDeleteCourseAlert();
            return;
        }

        //Delete and show Toast
        long deletedCourseId = yogaDatabase.yogaCourseDao().delete(yogaCourse);
        Toast.makeText(this, "Yoga course has been deleted, id: " + deletedCourseId,
                Toast.LENGTH_LONG
        ).show();

        setBackToCourse();
        //Add the course ID to deleted courses list
        YogaFirebaseDbUtils.deletedCourseIds.add(yogaCourse.id.toString());
        if (NetworkUtils.isNetworkAvailable(this)) {
            YogaFirebaseDbUtils.syncYogaCoursesToFirebaseDb();
        } else {
            System.out.println("No network connection. Sync canceled.");
        }
    }

    //Navigate to the list of class in the course
    private void navigateToClasses(Long courseId, String dayOfTheWeek){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaClassFragment");
        intent.putExtra("course_id", courseId);
        intent.putExtra("day_of_the_week", dayOfTheWeek);
        startActivity(intent);
    }

    //Show alert
    private void displayCannotDeleteCourseAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage("Cannot delete this course since there are still classes of it.")
                .setNeutralButton("OK", (dialogInterface, i) -> {})
                .show();
    }

    //Sync to Firebase
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