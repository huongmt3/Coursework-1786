package com.example.coursework_1786.fragments;

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

import com.example.coursework_1786.activities.CreateYogaCourseActivity;
import com.example.coursework_1786.R;
import com.example.coursework_1786.adapters.YogaCourseAdapter;
import com.example.coursework_1786.database.YogaDatabase;
import com.example.coursework_1786.models.YogaCourse;

import java.util.List;


public class YogaCourseFragment extends Fragment {

    YogaDatabase yogaDatabase;
    RecyclerView recyclerView;
    YogaCourseAdapter adapter;
    Button navigateCreateCourse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yoga_course, container, false);

        navigateCreateCourse = view.findViewById(R.id.navigateCreateCourse);
        navigateCreateCourse.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateYogaCourseActivity.class);
            startActivity(intent);
        });

        yogaDatabase = Room
                .databaseBuilder(requireContext(), YogaDatabase.class, "yoga_db")
                .allowMainThreadQueries()
                .build();

        recyclerView = view.findViewById(R.id.yogaCourseRc);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<YogaCourse> yogaCourses = yogaDatabase.yogaCourseDao().getAllYogaCourses();

        adapter = new YogaCourseAdapter(yogaCourses);
        recyclerView.setAdapter(adapter);

        return view;
    }
}