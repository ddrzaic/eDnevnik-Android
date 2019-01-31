package com.example.kalkulatorocjenazae_dnevnik;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    String loginFormUrl = "https://ocjene.skole.hr/pocetna/posalji/";
    String eDnevnik = "https://ocjene.skole.hr";
    EditText etUsername;
    EditText etPassword;

    String username="";
    String passwd="";
    ArrayList<String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsername=findViewById(R.id.usr);
        etPassword=findViewById(R.id.pass);

        try{
            user=FileIO.readArrayListFromFile("user",getApplicationContext());
            username=AESCrypt.decrypt(user.get(0));
            passwd=AESCrypt.decrypt(user.get(1));
            etUsername.setText(username);
            etPassword.setText(passwd);
        }catch(FileNotFoundException e){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            alert.setTitle("Prijava");
            final EditText ime = new EditText(MainActivity.this);
            ime.setHint("Korisničko ime:");
            layout.addView(ime);
            final EditText sifra= new EditText(MainActivity.this);
            sifra.setHint("Lozinka:");
            sifra.setTransformationMethod(PasswordTransformationMethod.getInstance());
            sifra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(sifra);
            alert.setView(layout);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    username = ime.getText().toString();
                    passwd = sifra.getText().toString();
                    etUsername.setText(username);
                    etPassword.setText(passwd);
                    user = new ArrayList<>();
                    try {
                        user.add(AESCrypt.encrypt(username));
                        user.add(AESCrypt.encrypt(passwd));
                        FileIO.writeArrayListToFile(user, "user", getApplicationContext());
                    } catch (Exception e) {
                        Log.e("AES encrypt error:", e.toString());
                    }
                }
            });
            alert.show();
        }catch (Exception f){
            Toast.makeText(getApplicationContext(),"Encryption error",Toast.LENGTH_SHORT);
        }


    }




    public void login(View v){

        new Thread(new Runnable() {
            public void run() {
                HTTPSConnection http = new HTTPSConnection();

                CookieHandler.setDefault(new CookieManager());
                String page = null;
                try {
                    page = http.GetPageContent(loginFormUrl);
                    String postParams = http.getFormParams(page, etUsername.getText().toString(), etPassword.getText().toString());
                    http.sendPost(loginFormUrl, postParams);
                    String result = http.GetPageContent(eDnevnik);
                    System.out.println(result);

                    if(!result.contains("Pristup je dozvoljen isključivo korisnicima registriranim u sustavu")) {
                        Intent intent = new Intent(getApplicationContext(), ClassesActivity.class);
                        intent.putExtra("resultExtra", result);
                        startActivity(intent);
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Neispravno korisničko ime ili lozinka.",Toast.LENGTH_LONG).show();
                                etUsername.getText().clear();
                                etPassword.getText().clear();
                                etUsername.requestFocus();
                            }
                        });

                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
