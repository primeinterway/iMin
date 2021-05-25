package com.asfdev.imin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import br.com.primeinterway.deviceapi.ScannerInterface;
import br.com.primeinterway.deviceapi.util.SerialInputOutputManager;

public class NLScannerActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    private static final String TAG = "ASFDev";

    TextView editTextTextMultiLine;
    Button btnHQR;
    Button btnDQR;
    Button btnHCB;
    Button btnDCB;
    Button btnClean;
    Button btnVoltar;
    FrameLayout llBarcode;

    ScannerInterface si;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

        btnClean = findViewById(R.id.scannerButton34);
        btnHQR = findViewById(R.id.scannerButton);
        btnDQR = findViewById(R.id.scannerButton1);
        btnHCB = findViewById(R.id.scannerButton2);
        btnDCB = findViewById(R.id.scannerButton3);
        btnVoltar = findViewById(R.id.scannerButton4);
        llBarcode = (FrameLayout) findViewById(R.id.snapi_barcode);

        editTextTextMultiLine.setEnabled(false);

        si = new ScannerInterface(NLScannerActivity.this);

        new Thread() {
            @Override
            public void run() {
                try {
                    int init = si.init();
                    Log.d(TAG, "getStatus(): " + init);
                    if(init == 0) {
                        runOnUiThread(() -> { Toast.makeText(getApplicationContext(), "init success", Toast.LENGTH_SHORT).show(); });
                        Log.d(TAG, "joinListener: " + si.joinListener(NLScannerActivity.this));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTextMultiLine.setText("");
            }
        });

        btnHQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                si.setSymbology(true, ScannerInterface.ALL2D);
            }
        });
        btnDQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                si.setSymbology(false, ScannerInterface.ALL2D);
            }
        });
        btnHCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                si.setSymbology(true, ScannerInterface.ALL1D);
            }
        });
        btnDCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                si.setSymbology(false, ScannerInterface.ALL1D);
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            si.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewData(byte[] data) {
        String string = new String(data);
        if(si.isConfigReturn(data)) {
            if(si.getConfigReturn(data)) {
                runOnUiThread(() -> { Toast.makeText(getApplicationContext(), "Config OK", Toast.LENGTH_SHORT).show(); });
                Log.d(TAG, "Success set");
            } else {
                runOnUiThread(() -> { Toast.makeText(getApplicationContext(), "Config Error", Toast.LENGTH_SHORT).show(); });
                Log.d(TAG, "Fail set");
            }
            return;
        } else {
            runOnUiThread(() -> {
                editTextTextMultiLine.setText(editTextTextMultiLine.getText() + System.lineSeparator() + string);
            });
            Log.d(TAG, "Receiving: " + string);
        }
    }

    @Override
    public void onRunError(Exception e) {
        runOnUiThread(() -> {
            editTextTextMultiLine.append("Error");
            e.printStackTrace();
        });
    }

}