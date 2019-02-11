package com.example.kalkulatorocjenazae_dnevnik;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {
    String courseHTML="";
    String courseName="";
    public static ArrayList<String> alGrades = new ArrayList<>();
    ArrayList<String> alNotes = new ArrayList<>();
    ArrayList<String> alDates = new ArrayList<>();
    public static ArrayList<GradeInfo> alGradeInfo = new ArrayList<>();
    ListView list;
    ArrayAdapter adapter;
    TextView tvAverage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        emptyArraylists();


        Intent intent = getIntent();
        TextView tvCourseName=findViewById(R.id.courseNameTV);
        list = findViewById(R.id.listGrades);
        tvAverage=findViewById(R.id.tvAvg);
        FloatingActionButton fab=findViewById(R.id.fabCourse);
        courseHTML=intent.getStringExtra("CourseHTMLExtra");

        Document parsedCourseHtml= Jsoup.parse(courseHTML);
        System.out.println(parsedCourseHtml.html());

        String teacherName=parsedCourseHtml.select("span.course-info").text();

        courseName=parsedCourseHtml.getElementsByClass("course-name").first().text().replace(teacherName,"");
        tvCourseName.setText(courseName);

        Elements parsedGrades = parsedCourseHtml.getElementsByClass("ocjena");
        Elements parsedDates = parsedCourseHtml.getElementsByClass("datum");
        Elements parsedNotes = parsedCourseHtml.getElementsByClass("biljeska");

        for(Element element : parsedGrades){
            alGrades.add(element.text());
        }
        for(Element element : parsedDates){
            alDates.add(element.text());
        }
        for(Element element : parsedNotes){
            if(!element.text().equals(""))  alNotes.add(element.text());
            else alNotes.add("Nema bilješke");
        }

        for(int i=0;i<alGrades.size();i++){
            alGradeInfo.add(new GradeInfo(alGrades.get(i),alNotes.get(i),alDates.get(i)));
        }

        adapter=new customArrayAdapterCourse(getApplicationContext(),0,alGradeInfo);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeGrade(position);
                return false;
            }
        });

        setAverage();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),GraphCourse.class));
            }
        });


    }


    public void buttonAdd(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivity.this);

        builder.setItems(new CharSequence[]
                        {"1", "2", "3", "4","5"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                addGrade("1");
                                break;
                            case 1:
                                addGrade("2");
                                break;
                            case 2:
                                addGrade("3");
                               break;
                            case 3:
                                addGrade("4");
                                break;
                            case 4:
                                addGrade("5");
                                break;

                        }
                    }
                });
        builder.create().show();
    }

    public void removeGrade(int pos){
        alGrades.remove(pos);
        alNotes.remove(pos);
        alDates.remove(pos);
        alGradeInfo.remove(pos);
        adapter.notifyDataSetChanged();
        setAverage();
    }

    public void addGrade(String grade){
        alGrades.add(0,grade);
        alNotes.add("Dodana ocjena");
        alDates.add("Dodana ocjena");
        alGradeInfo.add(0,new GradeInfo(grade,"Dodana ocjena","Dodana ocjena"));
        adapter.notifyDataSetChanged();
        setAverage();
    }

    public void setAverage(){
        if(alGrades.size()==0){
            tvAverage.setText("Nema ocjena");
        }else{
            int sum=0;
            for(int i=0;i<alGrades.size();i++){
                sum+=Integer.parseInt(alGrades.get(i));
            }
            tvAverage.setText("Prosjek: "+String.format("%.2f",(float)sum/alGrades.size()));
        }

    }
    public void emptyArraylists(){
        courseHTML="";
        courseName="";
        alGrades = new ArrayList<>();
        alNotes = new ArrayList<>();
        alDates = new ArrayList<>();
        alGradeInfo = new ArrayList<>();
    }
}
