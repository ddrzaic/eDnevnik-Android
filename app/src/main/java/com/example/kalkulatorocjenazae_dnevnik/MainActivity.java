package com.example.kalkulatorocjenazae_dnevnik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String loginFormUrl = "https://ocjene.skole.hr/pocetna/posalji/";
    String eDnevnik = "https://ocjene.skole.hr";
    AutoCompleteTextView etUsername;
    EditText etPassword;
    String username="";
    String passwd="";
    ArrayList<String> user;
    static ArrayList<String> users=new ArrayList<>();
    Switch swSaveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsername=findViewById(R.id.usr);
        etPassword=findViewById(R.id.pass);
        swSaveUser=findViewById(R.id.saveUser);
        swSaveUser.setChecked(true);
        try{
            user=FileIO.readArrayListFromFile("user",getApplicationContext());
            users=FileIO.readArrayListFromFile("users",getApplicationContext());
            username=AESCrypt.decrypt(user.get(0));
            passwd=AESCrypt.decrypt(user.get(1));
            etUsername.setText(username);
            etPassword.setText(passwd);
        }catch(FileNotFoundException e){
            etUsername.setText("");
            etPassword.setText("");
        }catch (Exception f){
            Toast.makeText(getApplicationContext(),"Greška enkripcije",Toast.LENGTH_SHORT).show();
        }
        etUsername.setAdapter(getUsersAdapter(this));
        etUsername.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    passwd=AESCrypt.decrypt(FileIO.readFromFile(getApplicationContext(),parent.getItemAtPosition(position).toString()));
                    etPassword.setText(passwd);
                } catch (Exception e) { }
            }
        });
    }




    public void login(View v){
        try {
            if(swSaveUser.isChecked()){
            user=new ArrayList<>();
            user.add(AESCrypt.encrypt(etUsername.getText().toString()));
            user.add(AESCrypt.encrypt( etPassword.getText().toString()));
            if(!users.contains(etUsername.getText().toString()))users.add(etUsername.getText().toString());
            FileIO.writeArrayListToFile(user, "user", getApplicationContext());
            FileIO.writeArrayListToFile(users, "users", getApplicationContext());
            FileIO.writeToFile(AESCrypt.encrypt( etPassword.getText().toString()),this,etUsername.getText().toString());
            }


        } catch (Exception e) {
            Toast.makeText(this,"Greška pri pisanju podataka!",Toast.LENGTH_LONG).show();
        }
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
                    if(!result.contains("Pristup je dozvoljen isključivo korisnicima registriranim u sustavu") && result.contains("Odaberite razred i školsku godinu")) {
                        Intent intent = new Intent(getApplicationContext(), ClassesActivity.class);
                        intent.putExtra("resultExtra", result);
                        startActivity(intent);
                    }else if(result.contains("Prijavljeni ste kao")){
                        String yearUrl="prvaGodina";
                        Intent intent = new Intent(getApplicationContext(),OverallActivity.class);
                        intent.putExtra("yearUrlExtra",yearUrl);
                        intent.putExtra("html",result);
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

    public static ArrayAdapter<String> getUsersAdapter(Context context){
        String[] temp=new String[users.size()];
        for(int i=0;i<temp.length;i++){
            temp[i]=users.get(i);
        }
        return new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,temp);
    }
}


