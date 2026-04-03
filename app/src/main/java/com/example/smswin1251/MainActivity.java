package com.example.smswin1251;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;

    private EditText etPhoneNumber;
    private EditText etMessage;
    private Button btnSendSms;
    private Button btnOpenDecrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etMessage = findViewById(R.id.etMessage);
        btnSendSms = findViewById(R.id.btnSendSms);
        btnOpenDecrypt = findViewById(R.id.open_decode);

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSmsPermission()) {
                    sendSmsWithPDU();
                } else {
                    requestSmsPermission();
                }
            }
        });
        btnOpenDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageDecodeActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;

    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);

    }
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) и выше
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        } else {
            // Android 8.0 (API 26) - Android 10 (API 29)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSmsWithPDU();
            } else {
                Toast.makeText(this, "Разрешение на отправку SMS не получено",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Разрешение на доступ к файлам не получено",
                            Toast.LENGTH_SHORT).show();                }
            } else {
                // Для Android 8, 9, 10 проверяем стандартный результат
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Доступ получен (Legacy mode)
                }
            }
        }
    }



    @SuppressLint("NewApi")
    private void sendSmsWithPDU() {
        checkStoragePermission();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String message = etMessage.getText().toString();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] win1251Bytes = message.getBytes("windows-1251");

            String filePathKey = "/storage/emulated/0/ham_keys/send"+phoneNumber;
            String filePathOffset = "/storage/emulated/0/ham_keys/offset"+phoneNumber;

            byte[] key = Files.readAllBytes(Paths.get(filePathKey));
            RandomAccessFile raf = new RandomAccessFile(filePathOffset, "r");
            raf.seek(raf.length()-4);
            int offset=raf.readInt();
            raf.close();

            DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePathOffset, true));
            dos.writeInt(offset+140);
            dos.close(); //Анти износ флеш памяти. В оригинальной версии сдвиг по ключу хранился в первых четырех байтах самого файла с ключом и перезаписывались одни и те же байты при каждой отправке смс.


            byte[] encrypted_bytes= Util.encrypt(offset,win1251Bytes,key);

            String convertedMessage = new String(encrypted_bytes, "windows-1251");

            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts = smsManager.divideMessage(convertedMessage);
            ArrayList<String> encodedParts = new ArrayList<>();

            for (String part : parts) {
                encodedParts.add(part);
            }

            smsManager.sendMultipartTextMessage(phoneNumber, null, encodedParts, null, null);

            Toast.makeText(this, "SMS отправлено (PDU mode) в кодировке Windows-1251",
                    Toast.LENGTH_LONG).show();

            etMessage.setText("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка кодировки: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка отправки: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}