package com.example.kalkulatorocjenazae_dnevnik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

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
        System.out.println(classes.html());
        Elements elClasses=classes.select("span.school-class");
        for(Element Class : elClasses){
            alClasses.add(Class.text());
        }

        Elements elHref=classes.getElementsByTag("a");
        for(Element el : elHref){
                alHref.add(el.attr("href"));
        }

        System.out.println("Hrefovi:"+alHref);



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
}
