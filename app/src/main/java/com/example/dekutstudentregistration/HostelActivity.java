package com.example.dekutstudentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class HostelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel);

        Spinner spinnerLocation = findViewById(R.id.spinnerLocation);
        Spinner spinnerRoomType = findViewById(R.id.spinnerRoomType);
        Button btnNextToPayment = findViewById(R.id.btnNextToPayment);

        // Populate Location Drop-down
        String[] locations = {"Internal Hostel", "Embassy", "Kahawa", "Nyeri Town", "Nyaribo", "Boma", "King'ong'o"};
        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locAdapter);

        // Populate Room Type Drop-down
        String[] roomTypes = {"Bedsitter", "1 Bedroom", "Single Room", "Studio"};
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypes);
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(roomAdapter);

        // Navigate to Activity 4
        btnNextToPayment.setOnClickListener(v -> {
            android.widget.CheckBox checkboxSharable = findViewById(R.id.checkboxSharable);
            EditText inputHostelStudentId = findViewById(R.id.inputHostelStudentId);

            Intent intent = new Intent(HostelActivity.this, PaymentActivity.class);
            // Carry forward data from Screens 1 & 2
            intent.putExtras(getIntent());

            // Add Screen 3 data
            intent.putExtra("hostelLocation", spinnerLocation.getSelectedItem().toString());
            intent.putExtra("roomType", spinnerRoomType.getSelectedItem().toString());
            intent.putExtra("isSharable", checkboxSharable.isChecked());
            intent.putExtra("hostelStudentId", inputHostelStudentId.getText().toString());

            startActivity(intent);
        });
    }
}