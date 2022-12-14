package com.devselim.qrattendace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.devselim.qrattendace.databinding.ActivityAttendaceBinding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AttendaceActivity extends AppCompatActivity {

    ActivityAttendaceBinding binding;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(AttendaceActivity.this);
        ArrayList<String> arrayList = databaseHelper.getName();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        binding.attendaceBox.setAdapter(arrayAdapter);

        binding.attendaceBox.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                copyText(arrayList.get(i));
                return false;
            }
        });

        /*binding.exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportDB();
            }
        });*/
    }

    private void copyText(String text) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("AttendanceItem", text);
        manager.setPrimaryClip(clipData);

        Toast.makeText(AttendaceActivity.this, "Copied to clipboard", Toast.LENGTH_LONG).show();
    }

    /*private void exportDB() {
        final String TABLE_NAME = "attendance";
        final String ID = "id";
        final String NAME_COL = "name";
        final String DATE_COL = "date";

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        File expoertDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!expoertDir.exists()) {
            expoertDir.mkdirs();
        }

        File file = new File(expoertDir, "Attendance.csv");
        try {
            file.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            csvWriter.writeNext(cursor.getColumnNames());
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String arrExported[] = {
                        cursor.getString(cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(NAME_COL)),
                        cursor.getString(cursor.getColumnIndex(DATE_COL))
                };
                csvWriter.writeNext(arrExported);
            }
            csvWriter.close();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}