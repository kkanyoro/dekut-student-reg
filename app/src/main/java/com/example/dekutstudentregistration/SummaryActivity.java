package com.example.dekutstudentregistration;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        TextView textSummaryData = findViewById(R.id.textSummaryData);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Students");

        String targetRegNo = getIntent().getStringExtra("targetRegNo");

        if (targetRegNo != null) {
            // VIEW SINGLE STUDENT
            textSummaryData.setText("Loading registration details...");

            dbRef.child(targetRegNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Fetch the original string containing the slash from the database
                        String originalRegNo = snapshot.child("regNo").getValue(String.class);
                        String name = snapshot.child("fullName").getValue(String.class);
                        String course = snapshot.child("course").getValue(String.class);
                        String idNo = snapshot.child("idNo").getValue(String.class);

                        String summary = "SUCCESSFUL REGISTRATION\n\n" +
                                "Reg No: " + originalRegNo + "\n" +
                                "Name: " + name + "\n" +
                                "ID No: " + idNo + "\n" +
                                "Course: " + course + "\n";
                        textSummaryData.setText(summary);
                    } else {
                        textSummaryData.setText("Record not found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    textSummaryData.setText("Error: " + error.getMessage());
                }
            });

        } else {
            // VIEW ALL STUDENTS
            textSummaryData.setText("Loading all registrations...");

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        textSummaryData.setText("No students registered yet.");
                        return;
                    }

                    StringBuilder summary = new StringBuilder();
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        String regNo = studentSnapshot.child("regNo").getValue(String.class);

                        // Skip ghost records or corrupted offline cache entries
                        if (regNo == null) {
                            continue;
                        }

                        String name = studentSnapshot.child("fullName").getValue(String.class);
                        String course = studentSnapshot.child("course").getValue(String.class);

                        summary.append("Reg No: ").append(regNo).append("\n");
                        summary.append("Name: ").append(name).append("\n");
                        summary.append("Course: ").append(course).append("\n");
                        summary.append("-----------------------------------\n");
                    }

                    if (summary.length() == 0) {
                        textSummaryData.setText("No valid students found.");
                    } else {
                        textSummaryData.setText(summary.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    textSummaryData.setText("Error: " + error.getMessage());
                }
            });
        }
    }
}