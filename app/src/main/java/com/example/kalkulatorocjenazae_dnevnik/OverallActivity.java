package com.example.kalkulatorocjenazae_dnevnik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;

public class OverallActivity extends AppCompatActivity {

    String yearUrl="";
    ArrayList<String> alCourses = new ArrayList<>();
    ArrayList<String> alTeachers = new ArrayList<>();
    ArrayList<String> alAverageGrade = new ArrayList<>();
    ArrayList<String> alUserGrade = new ArrayList<>();
    ArrayList<String> alHref = new ArrayList<>();
    ArrayList<CourseInfo> alCourseInfo = new ArrayList<>();
    TextView tvRealAverage;
    TextView tvUserAverage;
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

        Intent intent= getIntent();
        yearUrl=intent.getStringExtra("yearUrlExtra");
        new Thread(new Runnable() {
            public void run() {

                try {
                    String result = http.GetPageContent(yearUrl);
                    Document doc=Jsoup.parse(result);
                    System.out.println(doc.html());
                    Element coursesDiv = doc.getElementById("courses");
                    Elements courses=coursesDiv.getElementsByClass("course");

                    for(Element course : courses){
                        alTeachers.add(course.select("span.course-info").text());
                        alCourses.add(course.text());
                    }
                   for(int i=0;i<alCourses.size();i++){
                       alCourses.set(i,alCourses.get(i).replace(alTeachers.get(i),"")); //micanje imena profesora iz imena predmeta
                   }
                   /*runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,alCourses));
                       }
                   });
*/
                   Elements aHref=coursesDiv.getElementsByTag("a");
                   for(Element el : aHref){
                       alHref.add(el.attr("href"));
                   }




                    for (int i = 0; i < alHref.size(); i++) {
                        String temp = http.GetPageContent(eDnevnik+alHref.get(i));
                        Document temp2 = Jsoup.parse(temp);
                        alAverageGrade.add(temp2.getElementsByClass("average").first().text());
                        alUserGrade.add(String.format("%.0f",Float.valueOf(alAverageGrade.get(i).substring(16,20).replace(",","."))));
                        alCourseInfo.add(new CourseInfo(alCourses.get(i),alTeachers.get(i),alAverageGrade.get(i),alUserGrade.get(i)));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<ClassInfo> adapter=new customArrayAdapterOverall(getApplicationContext(),0,alCourseInfo);
                            list.setAdapter(adapter);
                            tvRealAverage.setText("Stvarni prosjek: "+getRealAverage());
                            tvUserAverage.setText("Prosjek: "+getRealAverage());

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        TextView gr=view.findViewById(R.id.gradeTV);
                                        if(id==gr.getId()){gr.setText("kliknut");
                                            System.out.println("ovo je");}
                                            else{
                                            gr.setText("nije klik");
                                        }


                                }
                            });
                        }
                    });



                   list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           System.out.println(alHref.get(position));
                       }
                   });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }


    public String getRealAverage(){
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


}
