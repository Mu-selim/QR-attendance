package com.devselim.qrattendace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.devselim.qrattendace.databinding.ActivityGenerateBinding;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateActivity extends AppCompatActivity {

    ActivityGenerateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.generateQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = binding.inputField.getText().toString();
                QRGEncoder encoder = new QRGEncoder(s, null, QRGContents.Type.TEXT, 800);
                binding.generatedQRImage.setImageBitmap(encoder.getBitmap());
            }
        });
    }
}