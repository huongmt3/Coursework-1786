package com.example.coursework_1786.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.coursework_1786.R;
import com.example.coursework_1786.activities.CreateYogaClassActivity;
import com.example.coursework_1786.adapters.YogaClassAdapter;
import com.example.coursework_1786.database.YogaDatabase;
import com.example.coursework_1786.models.YogaClass;

import java.util.List;

public class YogaClassFragment extends Fragment {
    YogaDatabase yogaDatabase;
    YogaClassAdapter yogaClassAdapter;
    RecyclerView recyclerView;
    Button navigateCreateClass;
    Button refreshBtn;
    Button searchClassesBtn;
    EditText searchText;
    Spinner daySpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yoga_class, container, false);

        //UI components
        navigateCreateClass = view.findViewById(R.id.navigateCreateClass);
        refreshBtn = view.findViewById(R.id.btnRefresh);
        searchText = view.findViewById(R.id.searchText);
        daySpinner = view.findViewById(R.id.daySpinner);
        searchClassesBtn = view.findViewById(R.id.btnSearchClass);

        //Initialise database
        yogaDatabase = Room
                .databaseBuilder(requireContext(), YogaDatabase.class, "yoga_database")
                .allowMainThreadQueries()
                .build();

        recyclerView = view.findViewById(R.id.yogaClassRc);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<YogaClass> yogaClasses;

        //Check whether a course ID has been passed in intent
        boolean hasCourseId = requireActivity().getIntent().hasExtra("course_id");
        Long courseId = requireActivity().getIntent().getLongExtra("course_id", 0L);
        //Get yoga class(es) for that course
        if (hasCourseId){
            yogaClasses = yogaDatabase.yogaClassDao().getByYogaCourseId(courseId);
            yogaClassAdapter = new YogaClassAdapter(yogaClasses, requireContext(), courseId, yogaDatabase);
            System.out.println(courseId);
        }
        //Get all yoga classes
        else {
            yogaClasses = yogaDatabase.yogaClassDao().getAll();
            yogaClassAdapter = new YogaClassAdapter(yogaClasses, requireContext(), yogaDatabase);
        }

        recyclerView.setAdapter(yogaClassAdapter);
        //Enable add class if the course ID has been passed and set button action
        navigateCreateClass.setEnabled(hasCourseId);
        navigateCreateClass.setOnClickListener(v -> setNavigateCreateClass(courseId));

        //Set buttons action
        refreshBtn.setOnClickListener(v -> {
            List<YogaClass> allYogaClasses = yogaDatabase.yogaClassDao().getAll();
            yogaClassAdapter = new YogaClassAdapter(allYogaClasses, requireContext(), yogaDatabase);
            recyclerView.setAdapter(yogaClassAdapter);
            clearCourseIdIntent();
            navigateCreateClass.setEnabled(false);
        });

        searchClassesBtn.setOnClickListener(v -> {
            List<YogaClass> searchedYogaClasses = searchYogaClasses();
            yogaClassAdapter = new YogaClassAdapter(searchedYogaClasses, requireContext(), yogaDatabase);
            recyclerView.setAdapter(yogaClassAdapter);
        });

        return view;
    }

    //Close database when activity is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearCourseIdIntent();
        if (yogaDatabase != null && yogaDatabase.isOpen()) {
            yogaDatabase.close();
        }
    }

    //Clear the course ID from the intent when it's no longer needed
    private void clearCourseIdIntent(){
        if (getActivity() != null) {
            getActivity().getIntent().removeExtra("course_id");
        }
    }

    //Navigate to add class activity and pass the data
    private void setNavigateCreateClass(Long courseId){
        Intent intent = new Intent(getActivity(), CreateYogaClassActivity.class);
        String dayOfTheWeek = requireActivity().getIntent().getStringExtra("day_of_the_week");
        System.out.println("YogaClassFragment " + courseId);
        System.out.println("YogaClassFragment " + dayOfTheWeek);
        intent.putExtra("course_id", courseId);
        intent.putExtra("day_of_the_week", dayOfTheWeek);
        startActivity(intent);
    }

    //Search based on teacher's name and date
    private List<YogaClass> searchYogaClasses(){
        String teacher = searchText.getText().toString().trim();
        String day = daySpinner.getSelectedItem().toString();
        return yogaDatabase.yogaClassDao().searchByTeacherAndDay(teacher, day);
    }
}