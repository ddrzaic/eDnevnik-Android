package com.example.kalkulatorocjenazae_dnevnik;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

public class OverallActivity extends AppCompatActivity {
    String yearUrl="";
    String year="";
    String name="";
    public static ArrayList<String> alCourses = new ArrayList<>();
    ArrayList<String> alTeachers = new ArrayList<>();
    ArrayList<String> alCourseHTML = new ArrayList<>();
    public static ArrayList<String> alAverageGrade = new ArrayList<>();
    public static ArrayList<String> alRealGrade = new ArrayList<>();
    public static ArrayList<String> alUserGrade = new ArrayList<>();
    public static ArrayList<String> alUserFinal = new ArrayList<>();
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
    String examsHref="";
    boolean workAlreadyDone=false;
    boolean workRunning=false;
    String intentHtml="";
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
        Intent intent = getIntent();
        yearUrl=intent.getStringExtra("yearUrlExtra");
        intentHtml=intent.getStringExtra("html");
        year=intent.getStringExtra("ClassExtra");
        workAlreadyDone=intent.getBooleanExtra("workDone",false);
        graphButton.setVisibility(View.INVISIBLE);
        examsButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);



        new Thread(new Runnable() {
            public void run() {
                http = new HTTPSConnection();
                try {
                    String result="";
                    if(yearUrl.equals("prvaGodina")){
                        result=intentHtml;
                        year="prvaGodina";
                        FileIO.writeToFile("prvaGodina",getApplicationContext(),"CurrentClassHref");
                        WorkManager instance;
                        ListenableFuture<List<WorkInfo>> statuses;
                        createNotificationChannel();
                        instance = WorkManager.getInstance();
                        statuses  = instance.getWorkInfosByTag("work");
                        PeriodicWorkRequest myWork=null;
                        PeriodicWorkRequest.Builder WorkBuilder =
                                new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15,
                                        TimeUnit.MINUTES).addTag("work");
                        myWork = WorkBuilder.build();
                        //Start BackgroungWorker
                        WorkManager.getInstance().enqueueUniquePeriodicWork("work", ExistingPeriodicWorkPolicy.KEEP,myWork);
                        WorkManager.getInstance().getWorkInfoByIdLiveData(myWork.getId())
                                .observe(OverallActivity.this, new Observer<WorkInfo>() {
                                    @Override
                                    public void onChanged(@Nullable WorkInfo workInfo) {
                                        if(workInfo.getState().equals(WorkInfo.State.RUNNING)){
                                            workRunning=true;
                                        }
                                    }
                                });
                    }else{
                        result = http.GetPageContent(yearUrl);
                    }

                    Document doc=Jsoup.parse(result);
                    name=doc.getElementsByTag("b").first().text();
                    Element coursesDiv = doc.getElementById("courses");
                    Elements courses=coursesDiv.getElementsByClass("course");
                    examsHref=doc.getElementsByAttributeValueContaining("href","ispiti").attr("href");



                    for(Element course : courses){
                        alTeachers.add(course.select("span.course-info").text());
                        alCourses.add(course.text());
                    }
                   for(int i=0;i<alCourses.size();i++){
                       alCourses.set(i,alCourses.get(i).replace(alTeachers.get(i),"")); //brisanje imena profesora iz imena predmeta
                   }

                   Elements aHref=coursesDiv.getElementsByTag("a");
                   for(Element el : aHref){
                       alHref.add(el.attr("href"));
                   }


                    for (int i = 0; i < alHref.size(); i++) {
                        String temp = http.GetPageContent(eDnevnik+alHref.get(i));
                        alCourseHTML.add(temp);
                        Document temp2 = Jsoup.parse(temp);
                        ArrayList<String> alGrades = new ArrayList<>();
                        Elements parsedGrades = temp2.getElementsByClass("ocjena");
                        int sum=0;
                        for(Element element : parsedGrades){
                            alGrades.add(element.text());
                        }
                        for(int g=0;g<alGrades.size();g++){
                            sum+=Integer.parseInt(alGrades.get(g));
                        }
                        if(sum==0){
                           alAverageGrade.add("0.00");
                        }else alAverageGrade.add(String.format("%.2f",(float)sum/alGrades.size()));

                        if(temp2.getElementsByTag("table").first().getElementsByTag("td").last().text().contains("(")){
                            alUserFinal.add(temp2.getElementsByTag("table").first().getElementsByTag("td").last().text());
                            String tempFinal=temp2.getElementsByTag("table").first().getElementsByTag("td").last().text();
                            tempFinal=tempFinal.substring(tempFinal.length()-2,tempFinal.length()-1);
                            alRealGrade.add(tempFinal);
                        }else{
                            alUserFinal.add("");
                            alRealGrade.add(String.valueOf(Math.round(Float.parseFloat(alAverageGrade.get(i).replace(",",".")))));
                        }

                    }

                    try{
                        alUserGrade=FileIO.readArrayListFromFile(year+name,getApplicationContext());
                    }catch (FileNotFoundException e){
                        alUserGrade=new ArrayList<>(alRealGrade);
                        FileIO.writeArrayListToFile(alUserGrade,year+name,getApplicationContext());

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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OverallActivity.this);
                                    builder.setTitle(alCourseInfo.get(position).courseName);
                                    builder.setItems(new CharSequence[]
                                                    {"1", "2", "3", "4","5","Neocijenjen"},
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case 0:
                                                            setGrade(position,"1");
                                                            break;
                                                        case 1:
                                                            setGrade(position,"2");
                                                            break;
                                                        case 2:
                                                            setGrade(position,"3");
                                                            break;
                                                        case 3:
                                                            setGrade(position,"4");
                                                            break;
                                                        case 4:
                                                            setGrade(position,"5");
                                                            break;
                                                        case 5:
                                                            setGrade(position,"0");
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

                    if(!workAlreadyDone){
                        final OneTimeWorkRequest checkForNewGrades =
                                new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                                        .addTag("checkNewGrades")
                                        .build();
                        WorkManager.getInstance().enqueue(checkForNewGrades);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                WorkManager.getInstance().getWorkInfoByIdLiveData(checkForNewGrades.getId())
                                        .observe(OverallActivity.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(@Nullable WorkInfo workInfo) {
                                                if(workInfo.getState().isFinished()){
                                                    ArrayList<String> alNewGrades=new ArrayList<>();
                                                    newGradeInfos=new ArrayList<>();
                                                    try {
                                                        alNewGrades=FileIO.readArrayListFromFile("newGrades",getApplicationContext());
                                                        if(alNewGrades.size()>0){
                                                            for(int i=0;i<alNewGrades.size()-3;i+=4){
                                                                newGradeInfos.add(new NewGradeInfo(alNewGrades.get(i),alNewGrades.get(i+1),
                                                                        alNewGrades.get(i+2),alNewGrades.get(i+3)));
                                                            }
                                                            Snackbar.make(findViewById(android.R.id.content), "Nove ocjene",
                                                                    Snackbar.LENGTH_INDEFINITE)
                                                                    .setAction("Pregledaj", new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            startActivity(new Intent(getApplicationContext(),
                                                                                    NewGradesActivity.class));
                                                                        }
                                                                    }).show();
                                                        }
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }).start();
                    }else{
                        ArrayList<String> alNewGrades=new ArrayList<>();
                        newGradeInfos=new ArrayList<>();
                        try {
                            alNewGrades=FileIO.readArrayListFromFile("newGrades",getApplicationContext());
                            if(alNewGrades.size()>0){
                                for(int i=0;i<alNewGrades.size()-3;i+=4){
                                    newGradeInfos.add(new NewGradeInfo(alNewGrades.get(i),alNewGrades.get(i+1),
                                            alNewGrades.get(i+2),alNewGrades.get(i+3)));
                                }
                                Snackbar.make(findViewById(android.R.id.content), "Nove ocjene", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Pregledaj", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(getApplicationContext(),NewGradesActivity.class));
                                            }
                                        }).show();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
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
                startActivity(new Intent(getApplicationContext(),ExamsActivity.class).putExtra("examsHref",examsHref));
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
                FileIO.writeArrayListToFile(alUserGrade,year+name,getApplicationContext());
                adapter.notifyDataSetChanged();
                tvUserAverage.setText("Prosjek: "+getUserAverage());
            }
        });


    }


    private void setGrade(int pos,String grade){
        alUserGrade.set(pos,grade);
        alCourseInfo.set(pos,new CourseInfo(alCourseInfo.get(pos).courseName,
                alCourseInfo.get(pos).teacherName,alCourseInfo.get(pos).averageGrade,grade));
        adapter.notifyDataSetChanged();
        FileIO.writeArrayListToFile(alUserGrade,year+name,getApplicationContext());
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
        alUserFinal = new ArrayList<>();
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
        finish();
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
