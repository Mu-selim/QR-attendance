package com.devselim.qrattendace;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.devselim.qrattendace.databinding.ActivityAttendaceBinding;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import java.io.OutputStream;
import java.util.ArrayList;

public class AttendaceActivity extends AppCompatActivity {

    private static final String SHEET_NAME = "Sheet1";
    private static final String NAME_COL_HEADER = "Name";
    private static final String DATE_COL_HEADER = "Date";
    private static final String CONCAT_COL_HEADER = "Concatenation of All data";
    private static final String FILE_NAME = "attendance.xls";
    HSSFWorkbook workbook;
    HSSFSheet sheet;
    HSSFRow row;
    HSSFCell cell;
    private static final int REQUEST_CODE = 1;
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

        binding.attendaceBox.setOnItemLongClickListener((adapterView, view, i, l) -> {
            copyText(arrayList.get(i));
            return false;
        });

        binding.exportBtn.setOnClickListener(View -> {
            if (ContextCompat.checkSelfPermission(AttendaceActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportExcel();
            } else {
                ActivityCompat.requestPermissions(AttendaceActivity.this, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportExcel();
            } else {
                Toast.makeText(AttendaceActivity.this, "Please provide required permissions", Toast.LENGTH_LONG).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copyText(String text) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("AttendanceItem", text);
        manager.setPrimaryClip(clipData);
        Toast.makeText(AttendaceActivity.this, "Copied to clipboard", Toast.LENGTH_LONG).show();
    }

    private void exportExcel() {
        // arrayList = [arrayOfNames, arrayOfDates, arrayOfAllData]
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        arrayList.add(databaseHelper.getName());
        arrayList.add(databaseHelper.getDate());
        arrayList.add(databaseHelper.getAllText());

        /*
           reforming arrayList to be as following:
           reformed = [arrayOfRecored1, arrayOfRecored2, arrayOfRecored3, ..... ]
        */
        ArrayList<ArrayList<String>> reformed = new ArrayList<>();
        for (int i = 0; i < arrayList.get(0).size(); i++) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(arrayList.get(0).get(i));
            temp.add(arrayList.get(1).get(i));
            temp.add(arrayList.get(2).get(i));
            reformed.add(temp);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (createXLS(reformed)) {
                makeToast("XLS file was exported successfully.");
            } else {
                makeToast("Failed to export XLS file.");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean createXLS(ArrayList<ArrayList<String>> list) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(SHEET_NAME);
        row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.AQUA.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        cell = row.createCell(0);
        cell.setCellValue(NAME_COL_HEADER);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(DATE_COL_HEADER);
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue(CONCAT_COL_HEADER);
        cell.setCellStyle(style);

        CellStyle nameStyle = workbook.createCellStyle();
        nameStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        nameStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        dateStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        CellStyle concatStyle = workbook.createCellStyle();
        concatStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        concatStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < list.size(); i++) {
            ArrayList<String> temp = list.get(i);
            row = sheet.createRow(i+1);

            cell = row.createCell(0);
            cell.setCellValue(temp.get(0));
            cell.setCellStyle(nameStyle);

            cell = row.createCell(1);
            cell.setCellValue(temp.get(1));
            cell.setCellStyle(dateStyle);

            cell = row.createCell(2);
            cell.setCellValue(temp.get(2));
            cell.setCellStyle(concatStyle);
        }

        Uri externalUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        String relativeLoaction = Environment.DIRECTORY_DOWNLOADS;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, FILE_NAME);
        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/excel");
        values.put(MediaStore.Files.FileColumns.TITLE, FILE_NAME);
        values.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relativeLoaction);
        values.put(MediaStore.Files.FileColumns.DATE_TAKEN, System.currentTimeMillis());

        Uri fileUri = getContentResolver().insert(externalUri, values);
        try {
            OutputStream stream = getContentResolver().openOutputStream(fileUri);
            workbook.write(stream);
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void makeToast(String toast) {
        Toast.makeText(AttendaceActivity.this, toast, Toast.LENGTH_LONG).show();
    }
}