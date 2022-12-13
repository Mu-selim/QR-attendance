package com.devselim.qrattendace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

        binding.attendaceBox.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                copyText(sent.get(i));
                return false;
            }
        });
    }

    private void copyText(String text) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("AttendanceItem", text);
        manager.setPrimaryClip(clipData);

        Toast.makeText(AttendaceActivity.this, "Copied to clipboard", Toast.LENGTH_LONG).show();
    }
}