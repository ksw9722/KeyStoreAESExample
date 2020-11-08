package com.example.ksw97.myapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


public class MainActivity extends AppCompatActivity {

    Button makeKey;
    Button encrypt;
    Button decrypt;

    EditText alias;
    EditText plain;
    EditText encryptedView;
    EditText decryptedText;
    KeyStore keystore;
    byte[] ivv = {31,42,22,58,27,81,33,42,88,98,11,13,10,37,92,25};
    byte[] ivvv;
    String ivv2 ="1234567812345678";


    void keyStoreStart(){
        try {
            keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"KEY STORE ERROR T_T",Toast.LENGTH_SHORT).show();
        }
    }

    void decryptFunction(){
        String enc = encryptedView.getText().toString();
        byte[] encB = Base64.decode(enc.getBytes(),0);

        try{
            String aliasString = alias.getText().toString();

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)keystore.getEntry(aliasString,null);
            SecretKey secretKey = secretKeyEntry.getSecretKey();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            IvParameterSpec iv = new IvParameterSpec(ivv);

            cipher.init(Cipher.DECRYPT_MODE,secretKey,iv);

            byte[] decB = cipher.doFinal(encB);
            decryptedText.setText(new String(decB));

        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"decrypt Key Error",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void encryptFunction(){
        String pl = plain.getText().toString();
        try {
            String aliasString = alias.getText().toString();
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)keystore.getEntry(aliasString,null);
            SecretKey secretKey = secretKeyEntry.getSecretKey();
            IvParameterSpec iv = new IvParameterSpec(ivv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,iv);
            //ivvv = cipher.getIV();  for random iv

            byte[] cipherText = cipher.doFinal(pl.getBytes());

            String encodedCipher = Base64.encodeToString(cipherText,0);
            encryptedView.setText(encodedCipher);


        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"cipher Key Error",Toast.LENGTH_SHORT).show();

        }
    }

    void makeKey(){
        String aliasString = alias.getText().toString();
        if(aliasString.isEmpty()){
            aliasString = "kswtest";
        }
        try {
            if (keystore.containsAlias(aliasString)){
                Toast.makeText(getApplicationContext(), "alias key Exist..!!", Toast.LENGTH_SHORT).show();
            }else{

                final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                KeyGenParameterSpec aesSpec = new KeyGenParameterSpec.Builder(aliasString, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).setRandomizedEncryptionRequired(false).build(); // no random iv
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                keyGenerator.init(aesSpec,random);
                keyGenerator.generateKey();

                KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)keystore.getEntry(aliasString,null);
                SecretKey secretKey = secretKeyEntry.getSecretKey();
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "makeKey Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeKey = (Button)findViewById(R.id.makeKey);
        encrypt = (Button)findViewById(R.id.encryptButton);
        decrypt = (Button)findViewById(R.id.decryptButton);

        alias = (EditText)findViewById((R.id.alias));
        plain = (EditText)findViewById(R.id.plainText);
        encryptedView = (EditText)findViewById(R.id.encryptedText);
        decryptedText = (EditText)findViewById(R.id.decryptedText);

        makeKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeKey();
            }
        });
        encrypt.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                encryptFunction();
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                decryptFunction();
            }
        });

        keyStoreStart();

    }



}
