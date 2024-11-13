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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yoga_class, container, false);

        navigateCreateClass = view.findViewById(R.id.navigateCreateClass);
        refreshBtn = view.findViewById(R.id.btnRefresh);

        yogaDatabase = Room
                .databaseBuilder(requireContext(), YogaDatabase.class, "yoga_database")
                .allowMainThreadQueries()
                .build();

        recyclerView = view.findViewById(R.id.yogaClassRc);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<YogaClass> yogaClasses;

        boolean hasCourseId = requireActivity().getIntent().hasExtra("course_id");
        Long courseId = requireActivity().getIntent().getLongExtra("course_id", 0L);
        if (hasCourseId){
            yogaClasses = yogaDatabase.yogaClassDao().getByYogaCourseId(courseId);
            yogaClassAdapter = new YogaClassAdapter(yogaClasses, requireContext(), courseId);
            System.out.println(courseId);
        }
        else {
            yogaClasses = yogaDatabase.yogaClassDao().getAll();
            yogaClassAdapter = new YogaClassAdapter(yogaClasses, requireContext());
        }

        recyclerView.setAdapter(yogaClassAdapter);
        navigateCreateClass.setEnabled(hasCourseId);
        navigateCreateClass.setOnClickListener(v -> setNavigateCreateClass(courseId));

        refreshBtn.setOnClickListener(v -> {
            List<YogaClass> allYogaClasses = yogaDatabase.yogaClassDao().getAll();
            yogaClassAdapter = new YogaClassAdapter(allYogaClasses, requireContext());
            recyclerView.setAdapter(yogaClassAdapter);
            clearCourseIdIntent();
            navigateCreateClass.setEnabled(false);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearCourseIdIntent();
    }

    private void clearCourseIdIntent(){
        if (getActivity() != null) {
            getActivity().getIntent().removeExtra("course_id");
        }
    }

    private void setYogaClassAdapter(List<YogaClass> yogaClasses, RecyclerView recyclerView){
        YogaClassAdapter adapter = new YogaClassAdapter(yogaClasses, requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void setNavigateCreateClass(Long courseId){
        Intent intent = new Intent(getActivity(), CreateYogaClassActivity.class);
        String dayOfTheWeek = requireActivity().getIntent().getStringExtra("day_of_the_week");
        System.out.println("YogaClassFragment " + courseId);
        System.out.println("YogaClassFragment " + dayOfTheWeek);
        intent.putExtra("course_id", courseId);
        intent.putExtra("day_of_the_week", dayOfTheWeek);
        startActivity(intent);
    }
}