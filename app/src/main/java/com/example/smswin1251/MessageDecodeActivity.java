package com.example.smswin1251;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import threegpp.charset.gsm.GSM7BitPackedCharset;

public class MessageDecodeActivity extends AppCompatActivity {
    private ArrayList<EditText> etMessages=new ArrayList<EditText>(1);
    private EditText etMessageNumber;
    private EditText etMessagePhoneNumber;
    private LinearLayoutCompat linearLayout;
    TextWatcher t;

    private TextView decoded;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        etMessages.add(findViewById(R.id.editTextMessagePart1));
        etMessageNumber = findViewById(R.id.EditText2);
        etMessagePhoneNumber= findViewById(R.id.EditText3);
        decoded = findViewById(R.id.textView);
        linearLayout=findViewById(R.id.linearLayout);
        t = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            @SuppressLint("NewApi")
            public void afterTextChanged(Editable s) {
                if(!etMessages.get(etMessages.size() - 1).getText().toString().isEmpty()){
                    EditText editTextNew = new EditText(MessageDecodeActivity.this);
                    editTextNew.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    editTextNew.setHint("Текст сообщения "+Integer.toString(etMessages.size()));
                    linearLayout.addView(editTextNew);
                    etMessages.add(editTextNew);
                    editTextNew.addTextChangedListener(t);
                }
                byte[] key;
                byte[] decryptedBytes;
                byte[] encryptedBytes;
                Charset gsm7bit = new GSM7BitPackedCharset();

                CheckBox cb=findViewById(R.id.suppress_encryption);
                boolean useEncryption=!cb.isChecked();

                try {
                    StringBuilder input= new StringBuilder();
                    for(EditText i:etMessages){
                        String part=i.getText().toString();
                        if(!part.isEmpty())input.append(part.subSequence(1,part.length()));
                    }
                    encryptedBytes = input.toString().getBytes(gsm7bit);

                    System.out.println(Arrays.toString(encryptedBytes));
                    if(useEncryption){
                        String filePath = "/storage/emulated/0/ham_keys/receive"+etMessagePhoneNumber.getText();
                        key = Files.readAllBytes(Paths.get(filePath));
                        decryptedBytes = Util.encrypt(Long.parseLong(etMessageNumber.getText().toString())*140,encryptedBytes,key);}
                    else decryptedBytes = encryptedBytes;
                    decryptedBytes=Util.decompress(decryptedBytes);
                    decoded.setText(new String(decryptedBytes, Charset.forName("Cp1251")));

                }
                catch (Exception e){
                    e.printStackTrace();
                    decoded.setText("Ключ отсутствует или закончился или ошибка при распаковке. Код ошибки:"+e.getMessage());
                }

            }
        };
        for(EditText i: etMessages)i.addTextChangedListener(t);
        etMessageNumber.addTextChangedListener(t);
    }
}
