package com.devselim.qrattendace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.devselim.qrattendace.databinding.ActivityAttendaceBinding;

import java.util.ArrayList;

public class AttendaceActivity extends AppCompatActivity {

    ActivityAttendaceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> sent = bundle.getStringArrayList("array_sent");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sent);
        binding.attendaceBox.setAdapter(arrayAdapter);
    }
}