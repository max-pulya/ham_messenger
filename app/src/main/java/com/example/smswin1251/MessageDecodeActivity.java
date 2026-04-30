package com.example.smswin1251;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import threegpp.charset.gsm.GSM7BitPackedCharset;
import threegpp.charset.gsm.GSMCharset;

public class MessageDecodeActivity extends AppCompatActivity {
    private EditText etMessage;
    private EditText etMessageNumber;
    private EditText etMessagePhoneNumber;

    private TextView decoded;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        etMessage = findViewById(R.id.EditText);
        etMessageNumber = findViewById(R.id.EditText2);
        etMessagePhoneNumber= findViewById(R.id.EditText3);
        decoded = findViewById(R.id.textView);
        TextWatcher t= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            @SuppressLint("NewApi")
            public void afterTextChanged(Editable s) {
                byte[] key;
                byte[] decryptedBytes;
                byte[] encryptedBytes;
                Charset gsm7bit = new GSM7BitPackedCharset();

                CheckBox cb=findViewById(R.id.suppress_encryption);
                boolean useEncryption=!cb.isChecked();

                try {

                    encryptedBytes = etMessage.getText().toString().getBytes(gsm7bit);

                    System.out.println(Arrays.toString(encryptedBytes));
                    if(useEncryption){
                        String filePath = "/storage/emulated/0/ham_keys/receive"+etMessagePhoneNumber.getText();
                        key = Files.readAllBytes(Paths.get(filePath));
                        decryptedBytes = Util.encrypt(Long.parseLong(etMessageNumber.getText().toString())*140,encryptedBytes,key);}
                    else decryptedBytes = encryptedBytes;
                    decoded.setText(new String(decryptedBytes, Charset.forName("Cp1251")));

                }
                catch (Exception e){
                    e.printStackTrace();
                    decoded.setText("Ключ отсутствует или закончился");
                }

            }
        };
        etMessage.addTextChangedListener(t);
        etMessageNumber.addTextChangedListener(t);
    }
}
