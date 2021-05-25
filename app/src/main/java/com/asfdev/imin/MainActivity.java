package com.asfdev.imin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnImpressora;
    Button btnScanner;
    Button btnUHF;
    Button btnPinpad;
    TextView tv;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnImpressora = findViewById(R.id.button);
        btnScanner = findViewById(R.id.button2);
        btnUHF = findViewById(R.id.button3);
        btnPinpad = findViewById(R.id.button4);
        tv = findViewById(R.id.textView3);

        btnImpressora.setOnClickListener(this);
        btnScanner.setOnClickListener(this);
        btnUHF.setOnClickListener(this);
        btnPinpad.setOnClickListener(this);

        //tv.setText(android.os.Build.getSerial());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button:
                intent = new Intent(this, ImpressoraActivity.class);
                startActivity(intent);
                break;
            case R.id.button2:
                intent = new Intent(this, NLScannerActivity.class);
                startActivity(intent);
                break;
            case R.id.button3:
                intent = new Intent(this, UHFActivity.class);
                startActivity(intent);
                break;
        }
    }
}