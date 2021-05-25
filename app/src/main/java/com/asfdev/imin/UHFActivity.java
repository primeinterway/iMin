package com.asfdev.imin;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rscja.deviceapi.RFIDWithUHFUSB;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.util.ArrayList;
import java.util.List;

public class UHFActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    public static ArrayAdapter<String> adapter;
    public static ArrayList<String> arrayList = new ArrayList<>();
    RFIDWithUHFUSB reader;
    boolean work = false;

    Button btnIniciar;
    Button btnLer;
    Button btnParar;
    Button btnLimpar;
    Button btnVoltar;

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 101;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST=102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_h_f);

        btnIniciar = findViewById(R.id.uhfButton1);
        btnLer = findViewById(R.id.uhfButton2);
        btnParar = findViewById(R.id.uhfButton3);
        btnLimpar = findViewById(R.id.uhfButton4);
        btnVoltar = findViewById(R.id.uhfButton);

        listView = findViewById(R.id.listview);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        btnIniciar.setOnClickListener(this);
        btnLer.setOnClickListener(this);
        btnParar.setOnClickListener(this);
        btnLimpar.setOnClickListener(this);
        btnVoltar.setOnClickListener(this);

        reader = RFIDWithUHFUSB.getInstance();

        btnLer.setEnabled(false);
        btnParar.setEnabled(false);
        btnLimpar.setEnabled(false);

        List<UsbDevice> usbDeviceList = reader.getUsbDeviceList(UHFActivity.this);
        for(UsbDevice usb: usbDeviceList) {
            if(usb.getManufacturerName() != null && usb.getManufacturerName().contains("Chainway")) {
                reader.init(usb, UHFActivity.this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uhfButton1:
                init();
                break;
            case R.id.uhfButton2:
                reader.startInventoryTag();
                work = true;
                new startRead().start();
                break;
            case R.id.uhfButton3:
                work = false;
                reader.stopInventory();
                break;
            case R.id.uhfButton4:
                arrayList.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.uhfButton:
                finish();
                break;
        }
    }

    public void init() {
        List<UsbDevice> usbDeviceList = reader.getUsbDeviceList(UHFActivity.this);
        for(UsbDevice usb: usbDeviceList) {
            if(usb.getManufacturerName() != null && usb.getManufacturerName().contains("Chainway")) {
                if(reader.init(usb, UHFActivity.this)) {
                    Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
                    btnLer.setEnabled(true);
                    btnParar.setEnabled(true);
                    btnLimpar.setEnabled(true);
                } else {
                    Toast.makeText(this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class startRead extends Thread {
        public synchronized void run() {
            UHFTAGInfo info;
            Message msg;
            while(work) {
                UHFTAGInfo tag = reader.readTagFromBuffer();
                if(tag != null && !arrayList.contains(tag.getEPC())) {
                    Log.d("ASFDev", "work " + tag.getEPC());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayList.add(tag.getEPC());
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }
}