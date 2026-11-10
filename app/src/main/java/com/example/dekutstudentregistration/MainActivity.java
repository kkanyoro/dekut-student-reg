package com.example.dekutstudentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerCourse = findViewById(R.id.spinnerCourse);
        Spinner spinnerDepartment = findViewById(R.id.spinnerDepartment);
        Button btnNextToLibrary = findViewById(R.id.btnNextToLibrary);

        // Populate Course Drop-down
        String[] courses = {"BSc Computer Science", "BSc Information Technology", "BSc Software Engineering", "BSc Telecommunication", "BBIT" };
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(courseAdapter);

        // Populate Department Drop-down
        String[] departments = {"Computer Science", "Information Technology", "Electrical Engineering"};
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(deptAdapter);

        // Navigate to Activity 2
        btnNextToLibrary.setOnClickListener(v -> {
            EditText inputFullName = findViewById(R.id.inputFullName);
            EditText inputIDNo = findViewById(R.id.inputIDNo);
            EditText inputRegNo = findViewById(R.id.inputRegNo);
            android.widget.RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);

            String fullName = inputFullName.getText().toString();
            String idNo = inputIDNo.getText().toString();
            String regNo = inputRegNo.getText().toString();

            // Get selected gender
            int selectedId = radioGroupGender.getCheckedRadioButtonId();
            String gender = selectedId == R.id.radioMale ? "Male" : "Female";

            String course = spinnerCourse.getSelectedItem().toString();
            String department = spinnerDepartment.getSelectedItem().toString();

            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            intent.putExtra("fullName", fullName);
            intent.putExtra("idNo", idNo);
            intent.putExtra("regNo", regNo);
            intent.putExtra("gender", gender);
            intent.putExtra("course", course);
            intent.putExtra("department", department);
            startActivity(intent);
        });

        Button btnViewSummary = findViewById(R.id.btnViewSummary);
        btnViewSummary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(intent);
        });
    }
}