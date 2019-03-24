package com.example.kalkulatorocjenazae_dnevnik;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class ExamsActivity extends AppCompatActivity {
    ArrayList<ExamInfo> examInfos;
    ListView lvExams;
    HTTPSConnection http = new HTTPSConnection();
    String eDnevnik = "https://ocjene.skole.hr";
    String examsHref="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
        lvExams=findViewById(R.id.examsListView);
        examsHref=getIntent().getStringExtra("examsHref");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    examInfos=new ArrayList<>();
                    String examHtml=http.GetPageContent(eDnevnik+examsHref);
                    Document parsedExamHtml= Jsoup.parse(examHtml);
                    Element examTable=parsedExamHtml.getElementsByTag("table").first();
                    Elements tr=examTable.getElementsByTag("tr");

                    for(Element el : tr){
                        Elements td=el.getElementsByTag("td");
                        if(!td.isEmpty()) examInfos.add(new ExamInfo(
                                td.first().text(),
                                td.get(1).text(),
                                td.get(2).text()));
                    }
                }catch (NullPointerException e){
                    examInfos.add(new ExamInfo(
                            "Nema ispita",
                            "",
                            ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customArrayAdapterExams adater=new customArrayAdapterExams(
                                getApplicationContext(),
                                0,
                                examInfos);
                        lvExams.setAdapter(adater);
                    }
                });
            }
        }).start();
    }
}


