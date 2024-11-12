package com.example.coursework_1786.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.coursework_1786.R;
import com.example.coursework_1786.database.YogaDatabase;

public class EditYogaClassActivity extends AppCompatActivity {
    private YogaDatabase yogaDatabase;
    Button backToClassBtn;
    Button pickDateBtn;
    TextView dateText;
    EditText teacherText;
    EditText commentsText;
    Button submitUpdateClassBtn;

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
                .databaseBuilder(this, YogaDatabase.class, "comp1786_yoga_db")
                .allowMainThreadQueries()
                .build();

        backToClassBtn = findViewById(R.id.backToClass);
        pickDateBtn = findViewById(R.id.btnPickDate);
        dateText = findViewById(R.id.labelDisplayDate);
        teacherText = findViewById(R.id.textTeacher);
        commentsText = findViewById(R.id.textComments);
        submitUpdateClassBtn = findViewById(R.id.submitUpdateClass);

        Intent intent = getIntent();
        long classId = intent.getLongExtra("class_id", 0L);
        long courseId = intent.getLongExtra("course_id", 0L);
        String date = intent.getStringExtra("date");
        String teacher = intent.getStringExtra("teacher");
        String comments = intent.getStringExtra("additional_comments");

        dateText.setText(date);
        teacherText.setText(teacher);
        commentsText.setText(comments);

        backToClassBtn.setOnClickListener(v -> setBackToClasses(courseId));
    }

    private void setBackToClasses(long courseId){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("target_fragment", "YogaClassFragment");
        if (courseId != 0){
            intent.putExtra("course_id", courseId);
        }
        startActivity(intent);
    }
}