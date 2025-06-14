package com.example.coursework_1786.activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.coursework_1786.database.YogaDatabase;
import com.example.coursework_1786.fragments.YogaClassFragment;
import com.example.coursework_1786.fragments.YogaCourseFragment;
import com.example.coursework_1786.R;
import com.example.coursework_1786.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainMenu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        replaceFragment(new YogaCourseFragment());

        //Set up the bottom navigation menu
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.yogaCourse:
                    replaceFragment(new YogaCourseFragment());
                    break;
                case R.id.yogaClass:
                    replaceFragment(new YogaClassFragment());
                    break;
            }
            return true;
        });

        //Set up Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("testing");
    }

    //Replace the current fragment in the frame layout
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    //Called when the activity is resumed
    @Override
    protected void onResume(){
        super.onResume();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("target_fragment")) {
            String targetFragment = intent.getStringExtra("target_fragment");

            if ("YogaCourseFragment".equals(targetFragment)) {
                binding.bottomNavigationView.setSelectedItemId(R.id.yogaCourse);
                replaceFragment(new YogaCourseFragment());
            } else if ("YogaClassFragment".equals(targetFragment)) {
                binding.bottomNavigationView.setSelectedItemId(R.id.yogaClass);
                replaceFragment(new YogaClassFragment());
            }

            //Remove the extra after handling it
            intent.removeExtra("target_fragment");
        }
    }
}