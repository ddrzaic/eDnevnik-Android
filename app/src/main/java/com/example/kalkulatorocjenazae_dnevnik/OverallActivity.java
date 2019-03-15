package com.example.kalkulatorocjenazae_dnevnik;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class OverallActivity extends AppCompatActivity {
    public static ArrayList<ExamInfo> examInfos;
    String yearUrl="";
    String year="";
    public static ArrayList<String> alCourses = new ArrayList<>();
    ArrayList<String> alTeachers = new ArrayList<>();
    ArrayList<String> alCourseHTML = new ArrayList<>();
    public static ArrayList<String> alAverageGrade = new ArrayList<>();
    public static ArrayList<String> alRealGrade = new ArrayList<>();
    public static ArrayList<String> alUserGrade = new ArrayList<>();
    public static ArrayList<NewGradeInfo> newGradeInfos;
    ArrayList<String> alHref = new ArrayList<>();
    ArrayList<CourseInfo> alCourseInfo = new ArrayList<>();
    TextView tvRealAverage;
    TextView tvUserAverage;
    ImageButton graphButton;
    ImageButton examsButton;
    ImageButton resetButton;
    customArrayAdapterOverall adapter;
    ListView list;
    String eDnevnik = "https://ocjene.skole.hr";
    HTTPSConnection http = new HTTPSConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);
        list=findViewById(R.id.listView);
        tvRealAverage=findViewById(R.id.realAverageTV);
        tvUserAverage=findViewById(R.id.userAverageTV);
        graphButton=findViewById(R.id.graphButtonView);
        examsButton=findViewById(R.id.examsButtonView);
        resetButton=findViewById(R.id.resetButtonView);
        emptyArraylist();
        Intent intent= getIntent();
        yearUrl=intent.getStringExtra("yearUrlExtra");
        year=intent.getStringExtra("ClassExtra");
        graphButton.setVisibility(View.INVISIBLE);
        examsButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);



        new Thread(new Runnable() {
            public void run() {

                try {
                    String result = http.GetPageContent(yearUrl);
                    Document doc=Jsoup.parse(result);

                    Element coursesDiv = doc.getElementById("courses");
                    Elements courses=coursesDiv.getElementsByClass("course");


                    try {
                        String examHref=doc.getElementsByAttributeValueContaining("href","ispiti").attr("href");
                        String examHtml=http.GetPageContent(eDnevnik+examHref);
                        Document parsedExamHtml=Jsoup.parse(examHtml);
                        Element examTable=parsedExamHtml.getElementsByTag("table").first();
                        Elements tr=examTable.getElementsByTag("tr");

                        for(Element el : tr){
                            Elements td=el.getElementsByTag("td");
                            if(!td.isEmpty()) examInfos.add(new ExamInfo(td.first().text(),td.get(1).text(),td.get(2).text()));
                        }
                    }catch (NullPointerException e){
                        examInfos.add(new ExamInfo("Nema ispita","",""));
                    }





                    Log.e("year   ",year);
                    for(Element course : courses){
                        alTeachers.add(course.select("span.course-info").text());
                        alCourses.add(course.text());
                    }
                   for(int i=0;i<alCourses.size();i++){
                       alCourses.set(i,alCourses.get(i).replace(alTeachers.get(i),"")); //micanje imena profesora iz imena predmeta
                   }

                   Elements aHref=coursesDiv.getElementsByTag("a");
                   for(Element el : aHref){
                       alHref.add(el.attr("href"));
                   }


                    for (int i = 0; i < alHref.size(); i++) {
                        String temp = http.GetPageContent(eDnevnik+alHref.get(i));
                        alCourseHTML.add(temp);
                        Document temp2 = Jsoup.parse(temp);
                        alAverageGrade.add(temp2.getElementsByClass("average").first().text());
                        alRealGrade.add(String.format("%.0f",Float.valueOf(alAverageGrade.get(i).substring(16,20).replace(",","."))));
                    }
                    try{
                        alUserGrade=FileIO.readArrayListFromFile(year,getApplicationContext());
                    }catch (FileNotFoundException e){
                        for(int i = 0; i < alHref.size(); i++){
                            alUserGrade.add(String.format("%.0f",Float.valueOf(alAverageGrade.get(i).substring(16,20).replace(",","."))));
                        }
                    }
                    for(int i=0;i<alHref.size();i++){
                        alCourseInfo.add(new CourseInfo(alCourses.get(i),alTeachers.get(i),alAverageGrade.get(i),alUserGrade.get(i)));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter=new customArrayAdapterOverall(getApplicationContext(),0,alCourseInfo);
                            list.setAdapter(adapter);
                            tvRealAverage.setText("Stvarni prosjek: "+getRealAverage());
                            tvUserAverage.setText("Prosjek: "+getUserAverage());
                            examsButton.setVisibility(View.VISIBLE);
                            graphButton.setVisibility(View.VISIBLE);
                            resetButton.setVisibility(View.VISIBLE);

                            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                    final TextView grade=view.findViewById(R.id.gradeTV);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OverallActivity.this);
                                    builder.setTitle(alCourseInfo.get(position).courseName);
                                    builder.setItems(new CharSequence[]
                                                    {"1", "2", "3", "4","5","Neocijenjen"},
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case 0:
                                                            setGrade(position,"1",grade);
                                                            break;
                                                        case 1:
                                                            setGrade(position,"2",grade);
                                                            break;
                                                        case 2:
                                                            setGrade(position,"3",grade);
                                                            break;
                                                        case 3:
                                                            setGrade(position,"4",grade);
                                                            break;
                                                        case 4:
                                                            setGrade(position,"5",grade);
                                                            break;
                                                        case 5:
                                                            setGrade(position,"0",grade);
                                                            break;
                                                    }
                                                }
                                            });
                                    builder.create().show();


                                    return true;
                                }
                            });

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent=new Intent(getApplicationContext(),CourseActivity.class);
                                    intent.putExtra("CourseHTMLExtra",alCourseHTML.get(position));
                                    startActivity(intent);
                                }
                            });

                        }
                    });



                    ArrayList<String> alNewGrades=new ArrayList<>();
                    newGradeInfos=new ArrayList<>();
                    alNewGrades=FileIO.readArrayListFromFile("newGrades",getApplicationContext());
                    if(alNewGrades.size()>0){
                        for(int i=0;i<alNewGrades.size()-3;i+=4){
                            newGradeInfos.add(new NewGradeInfo(alNewGrades.get(i),alNewGrades.get(i+1),alNewGrades.get(i+2),alNewGrades.get(i+3)));
                        }
                    }


                    if(!doc.getElementsByAttributeValueContaining("href","nove").isEmpty()){
                        final String newGradesHref=doc.getElementsByAttributeValueContaining("href","nove").attr("href");
                        String table=http.GetPageContent(eDnevnik+newGradesHref);
                        final Document parsedNoveOcjene=Jsoup.parse(table);



                        Elements tableRows=parsedNoveOcjene.getElementsByTag("tr");
                        tableRows.remove(0);
                        ArrayList<Elements> tableColumns=new ArrayList<>();
                        for(int i=0;i<tableRows.size();i++){
                            tableColumns.add(tableRows.get(i).getElementsByTag("td"));
                            newGradeInfos.add(new NewGradeInfo(tableColumns.get(i).get(0).text(),tableColumns.get(i).get(1).text(),tableColumns.get(i).get(2).text(),tableColumns.get(i).get(3).text()));
                        }


                    }
                    if(newGradeInfos.size()>0){
                        Snackbar.make(findViewById(android.R.id.content), "Nove ocjene", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Pregledaj", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                            startActivity(new Intent(getApplicationContext(),NewGradesActivity.class));
                                    }
                                }).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),GraphOverall.class));
            }
        });

        examsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ExamsActivity.class));
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alUserGrade=new ArrayList<>(alRealGrade);
                System.out.println(alUserGrade);
                for(int i=0;i<alCourseInfo.size();i++){
                    alCourseInfo.set(i,new CourseInfo(alCourses.get(i),alTeachers.get(i),alAverageGrade.get(i),alUserGrade.get(i)));
                }
                FileIO.writeArrayListToFile(alUserGrade,year,getApplicationContext());
                adapter.notifyDataSetChanged();
                tvUserAverage.setText("Prosjek: "+getUserAverage());
            }
        });


    }


    private void setGrade(int pos,String grade,TextView tv){
        alUserGrade.set(pos,grade);
        alCourseInfo.set(pos,new CourseInfo(alCourseInfo.get(pos).courseName,alCourseInfo.get(pos).teacherName,alCourseInfo.get(pos).averageGrade,grade));
        adapter.notifyDataSetChanged();
        FileIO.writeArrayListToFile(alUserGrade,year,getApplicationContext());
        tvUserAverage.setText("Prosjek: "+getUserAverage());
    }

    public String getRealAverage(){
        int sum=0;
        int counter=0;
        for(int i=0;i<alRealGrade.size();i++){
            if(!alRealGrade.get(i).equals("0")){
                sum+=Integer.valueOf(alRealGrade.get(i));
                counter++;
            }
        }
        return String.format("%.2f",(float)sum/counter);
    }

    public static String getUserAverage(){
        int sum=0;
        int counter=0;
        for(int i=0;i<alUserGrade.size();i++){
            if(!alUserGrade.get(i).equals("0")){
                sum+=Integer.valueOf(alUserGrade.get(i));
                counter++;
            }
        }
        return String.format("%.2f",(float)sum/counter);
    }

    public void emptyArraylist(){
        alRealGrade=new ArrayList<>();
        alCourses=new ArrayList<>();
        alUserGrade=new ArrayList<>();
        alCourseInfo=new ArrayList<>();
        alHref=new ArrayList<>();
        alAverageGrade=new ArrayList<>();
        alCourses=new ArrayList<>();
        examInfos = new ArrayList<>();
    }




}
