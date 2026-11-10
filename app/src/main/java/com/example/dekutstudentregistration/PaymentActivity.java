package com.example.dekutstudentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements MpesaListener {

    private String currentPhoneNumber;
    private String currentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        EditText inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        EditText inputAmount = findViewById(R.id.inputAmount);
        Button btnPayAndRegister = findViewById(R.id.btnPayAndRegister);

        MpesaManager mpesaManager = new MpesaManager(this);

        btnPayAndRegister.setOnClickListener(v -> {
            currentPhoneNumber = inputPhoneNumber.getText().toString().trim();
            currentAmount = inputAmount.getText().toString().trim();

            if (currentPhoneNumber.isEmpty() || currentAmount.isEmpty()) {
                Toast.makeText(this, "Please enter phone number and amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Initiating M-Pesa prompt...", Toast.LENGTH_SHORT).show();
            mpesaManager.initiatePayment(currentPhoneNumber, currentAmount);
        });
    }

    @Override
    public void onMpesaSuccess() {
        // OkHttp runs on a background thread, so UI updates must be routed back to the main thread
        runOnUiThread(() -> {
            saveToFirebaseAndFinish(currentPhoneNumber, currentAmount);
        });
    }

    @Override
    public void onMpesaError(String errorMessage) {
        runOnUiThread(() -> {
            Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        });
    }

    private void saveToFirebaseAndFinish(String phoneNumber, String amount) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Map<String, Object> studentData = new HashMap<>();

            studentData.put("fullName", extras.getString("fullName"));
            studentData.put("idNo", extras.getString("idNo"));
            studentData.put("regNo", extras.getString("regNo"));
            studentData.put("gender", extras.getString("gender"));
            studentData.put("course", extras.getString("course"));
            studentData.put("department", extras.getString("department"));

            Map<String, Object> libraryData = new HashMap<>();
            libraryData.put("isbn", extras.getString("isbn"));
            libraryData.put("author", extras.getString("author"));
            libraryData.put("title", extras.getString("title"));
            libraryData.put("dateBorrowed", extras.getString("dateBorrowed"));
            libraryData.put("dateReturn", extras.getString("dateReturn"));
            libraryData.put("studentIdNo", extras.getString("libStudentId"));
            studentData.put("libraryRequest", libraryData);

            Map<String, Object> hostelData = new HashMap<>();
            hostelData.put("location", extras.getString("hostelLocation"));
            hostelData.put("roomType", extras.getString("roomType"));
            hostelData.put("isSharable", extras.getBoolean("isSharable"));
            hostelData.put("studentIdNo", extras.getString("hostelStudentId"));
            studentData.put("hostelSelection", hostelData);

            studentData.put("paymentNumber", phoneNumber);
            studentData.put("paymentAmount", amount);

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Students");
            String regNumber = extras.getString("regNo");

            // Replace slash with dash so Firebase creates a single valid folder
            String safeFirebaseKey = regNumber.replace("/", "-");

            dbRef.child(safeFirebaseKey).setValue(studentData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Registration Confirmed!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PaymentActivity.this, SummaryActivity.class);
                        // Pass the safe key to the summary page
                        intent.putExtra("targetRegNo", safeFirebaseKey);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show());
        }
    }
}