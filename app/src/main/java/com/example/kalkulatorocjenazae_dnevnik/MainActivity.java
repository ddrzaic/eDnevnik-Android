package com.example.kalkulatorocjenazae_dnevnik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    String password="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsername=findViewById(R.id.usr);
        etPassword=findViewById(R.id.pass);
    }


    public void login(View v){

        username=etUsername.getText().toString();
        password=etPassword.getText().toString();


        new Thread(new Runnable() {
            public void run() {
                HTTPSConnection http = new HTTPSConnection();

                CookieHandler.setDefault(new CookieManager());
                String page = null;
                try {
                    page = http.GetPageContent(loginFormUrl);
                    System.out.println(page);
                    //String postParams = http.getFormParams(page, username, password);
                    String postParams = http.getFormParams(page, "dino.drzaic@skole.hr", "zUS7kU9B");
                    http.sendPost(loginFormUrl, postParams);
                    String result = http.GetPageContent(eDnevnik);

                    Intent intent=new Intent(getApplicationContext(),ClassesActivity.class);
                    intent.putExtra("resultExtra",result);

                    startActivity(intent);





/*
                    Document doc= Jsoup.parse(result);
                    Element coursesDiv=doc.getElementById("courses");
                    Elements courses=coursesDiv.getElementsByTag("a");
                    ArrayList<String> href=new ArrayList<>();
                    for(int i=0;i<courses.size();i++){
                        href.add(courses.get(i).attr("href"));
                        System.out.println(String.valueOf(i) +"   "+ href.get(i));
                    }
                    for(int i=0;i<href.size();i++){
                        String predmet = http.GetPageContent(baseUrl+href.get(i)+"/sve");
                        System.out.println(String.valueOf(i) +"   "+ predmet);
                    }

*/




                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
