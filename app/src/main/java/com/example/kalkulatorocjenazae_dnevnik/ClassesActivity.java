package com.example.kalkulatorocjenazae_dnevnik;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class ClassesActivity extends AppCompatActivity {

    String html="";
    ListView list;
    TextView tvStudentName;
    String studentName="";
    String eDnevnik = "https://ocjene.skole.hr";
    ArrayList<String> alClasses=new ArrayList<>();
    ArrayList<String> alHref=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);




        list=findViewById(R.id.listView);
        tvStudentName=findViewById(R.id.tvname);
        Intent intent=getIntent();
        html=intent.getStringExtra("resultExtra");
        Document doc= Jsoup.parse(html);
        Element classes=doc.getElementById("classes");
        System.out.println(doc.html());
        Elements elClasses=classes.select("span.school-class");
        for(Element Class : elClasses){
            alClasses.add(Class.text());
        }

        Elements elHref=classes.getElementsByTag("a");
        for(Element el : elHref){
                alHref.add(el.attr("href"));
        }

        FileIO.writeToFile(alHref.get(0),getApplicationContext(),"CurrentClassHref");



        createNotificationChannel();

        PeriodicWorkRequest.Builder WorkBuilder =
                new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15,
                        TimeUnit.MINUTES);

        PeriodicWorkRequest myWork = WorkBuilder.build();
        WorkManager.getInstance().enqueue(myWork);          //Start BackgroungWorker





        studentName=classes.getElementsByClass("studentName").first().text();
        tvStudentName.setText(studentName);

        list.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,alClasses));
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String yearUrl=eDnevnik+alHref.get(position);
                Intent intent = new Intent(getApplicationContext(),OverallActivity.class);
                intent.putExtra("yearUrlExtra",yearUrl);
                intent.putExtra("ClassExtra",alClasses.get(position));
                startActivity(intent);
            }
        });
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "eDnevnik";
            String description = "Kalkulator ocjena za eDnevnik";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("eDnevnik", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
