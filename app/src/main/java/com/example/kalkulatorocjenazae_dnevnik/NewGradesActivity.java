package com.example.kalkulatorocjenazae_dnevnik;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NewGradesActivity extends AppCompatActivity {

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grades);
        lv=findViewById(R.id.listViewNewGrades);
        customArrayAdapterNewGrade adapter=new customArrayAdapterNewGrade(getApplicationContext(),0,OverallActivity.newGradeInfos);
        lv.setAdapter(adapter);
    }
}
