package com.example.dekutstudentregistration;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Spinner spinnerIsbn = findViewById(R.id.spinnerIsbn);
        Spinner spinnerAuthor = findViewById(R.id.spinnerAuthor);
        Spinner spinnerTitle = findViewById(R.id.spinnerTitle);
        EditText inputDateBorrowed = findViewById(R.id.inputDateBorrowed);
        EditText inputDateReturn = findViewById(R.id.inputDateReturn);
        Button btnNextToHostel = findViewById(R.id.btnNextToHostel);

        // Dummy Data Arrays for remaining Spinners
        String[] isbns = {"978-0134685991", "978-0262033848", "978-0123704900"};
        String[] authors = {"Joshua Bloch", "Thomas H. Cormen", "David A. Patterson"};
        String[] titles = {"Effective Java", "Introduction to Algorithms", "Computer Organization"};

        spinnerIsbn.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, isbns));
        spinnerAuthor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, authors));
        spinnerTitle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titles));

        // Set up Calendar Popups
        inputDateBorrowed.setOnClickListener(v -> showDatePicker(inputDateBorrowed));
        inputDateReturn.setOnClickListener(v -> showDatePicker(inputDateReturn));

        btnNextToHostel.setOnClickListener(v -> {
            EditText inputLibStudentId = findViewById(R.id.inputLibraryStudentId);

            Intent intent = new Intent(LibraryActivity.this, HostelActivity.class);
            // Carry forward the data from Screen 1
            intent.putExtras(getIntent());

            // Add Screen 2 data
            intent.putExtra("isbn", spinnerIsbn.getSelectedItem().toString());
            intent.putExtra("author", spinnerAuthor.getSelectedItem().toString());
            intent.putExtra("title", spinnerTitle.getSelectedItem().toString());
            intent.putExtra("dateBorrowed", inputDateBorrowed.getText().toString());
            intent.putExtra("dateReturn", inputDateReturn.getText().toString());
            intent.putExtra("libStudentId", inputLibStudentId.getText().toString());

            startActivity(intent);
        });
    }

    private void showDatePicker(EditText targetField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Months are 0-indexed in Java Calendar
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    targetField.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}