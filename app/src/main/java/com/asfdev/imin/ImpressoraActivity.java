package com.asfdev.imin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.imin.printerlib.IminPrintUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class ImpressoraActivity extends AppCompatActivity {

    private IminPrintUtils iminUtils;

    Button btnCupom;
    Button btnVoltar;

    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressora);
        btnCupom = findViewById(R.id.impressoraButton);
        btnVoltar = findViewById(R.id.impressoraButton2);

        btnCupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imprimirCupom();
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iminUtils = IminPrintUtils.getInstance(ImpressoraActivity.this);
        new Thread() {
            @Override
            public void run() {
                iminUtils.initPrinter();
            }
        }.start();
    }

    public void imprimirCupom() {
        ArrayList<String[]> impressao = new ArrayList<>();
        impressao.add(new String[]{"Cod.", "Descrição", "Valor"});
        impressao.add(new String[]{"001", "Coca Cola 2L", "R$10,00"});
        impressao.add(new String[]{"002", "Batata Ruffles", "R$13,50"});
        impressao.add(new String[]{"003", "Chocolate Kit Kat", "R$5,90"});
        impressao.add(new String[]{"004", "Chocolate Lacta", "R$10,30"});
        try {
            iminUtils.setPageFormat(80);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            iminUtils.printSingleBitmap(bitmap, 1);
            iminUtils.setAlignment(1);
            iminUtils.printText("----------------------");
            int[] colsWidthArr3 = new int[]{2, 6, 2};
            int[] colsAlign3 = new int[]{0, 0, 2};
            int[] colsSize3 = new int[]{26, 26, 26};
            for (String[] string : impressao) {
                if (string.equals(impressao.get(0))) {
                    iminUtils.setTextStyle(1);
                    iminUtils.printColumnsText(string, colsWidthArr3,
                            colsAlign3, colsSize3);
                    iminUtils.printAndFeedPaper(1);
                    iminUtils.setTextStyle(0);
                } else {
                    iminUtils.printColumnsText(string, colsWidthArr3,
                            colsAlign3, colsSize3);
                }
            }
            iminUtils.setAlignment(1);
            iminUtils.printText("----------------------", 1);
            iminUtils.setAlignment(1);
            iminUtils.setTextSize(20);
            iminUtils.printText("43190833200056042577650020000002631000002964");
            iminUtils.setBitmapWidth(576);
            iminUtils.printSingleBitmap(createBarcodeBitmap("43190833200056042577650020000002631000002964", 576, 140), 1);
            iminUtils.printAndFeedPaper(10);
            iminUtils.setQrCodeSize(6);
            iminUtils.printQrCode("www.primeinterway.com.br", 0);
            iminUtils.printAndFeedPaper(100);
            iminUtils.fullCut();
            System.out.println("Finished");
        } catch (RuntimeException | WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();

        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }

        return imageBitmap;
    }
}