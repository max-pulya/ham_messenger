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
import android.widget.CheckBox;
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
import java.nio.charset.Charset;

import threegpp.charset.gsm.GSM7BitPackedCharset;
import threegpp.charset.gsm.GSMCharset;

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
                    sendSms();
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
                sendSms();
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
    private void sendSms() {
        checkStoragePermission();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String message = etMessage.getText().toString();
        int offset=0;

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            CheckBox cb=findViewById(R.id.suppress_encryption);
            boolean useEncryption=!cb.isChecked();

            byte[] win1251Bytes = message.getBytes("windows-1251");
            byte[] encrypted_bytes;
            String filePathKey = "/storage/emulated/0/ham_keys/send"+phoneNumber;
            String filePathOffset = "/storage/emulated/0/ham_keys/offset"+phoneNumber;
            if(useEncryption) {
                byte[] key = Files.readAllBytes(Paths.get(filePathKey));

                if (!Files.exists(Paths.get(filePathOffset))) {
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePathOffset, false));
                    dos.writeInt(0);
                    dos.close();
                }

                RandomAccessFile raf = new RandomAccessFile(filePathOffset, "r");
                raf.seek(raf.length() - 4);
                offset = raf.readInt();
                raf.close();


                encrypted_bytes = Util.encrypt(offset, win1251Bytes, key);
            }
            else encrypted_bytes=win1251Bytes;
            Charset gsm7bit = new GSM7BitPackedCharset();
            String convertedMessage = new String(encrypted_bytes, gsm7bit);

            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts = smsManager.divideMessage(convertedMessage);
            //etMessage.setText(convertedMessage);
            System.out.println("Sms count");
            System.out.println(parts.size());
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

            Toast.makeText(this, "SMS отправлено (PDU mode) в кодировке Windows-1251",
                    Toast.LENGTH_LONG).show();

            if(useEncryption) {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePathOffset, true));
                dos.writeInt(offset + 140 * parts.size());
                dos.close(); //Анти износ флеш памяти. В оригинальной версии сдвиг по ключу хранился в первых четырех байтах самого файла с ключом и перезаписывались одни и те же байты при каждой отправке смс.
            }

            //etMessage.setText("");
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