package com.example.kalkulatorocjenazae_dnevnik;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ExamsActivity extends AppCompatActivity {

    ListView lvExams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        lvExams=findViewById(R.id.examsListView);

        customArrayAdapterExams adater=new customArrayAdapterExams(this,0,OverallActivity.examInfos);
        lvExams.setAdapter(adater);
    }
}
