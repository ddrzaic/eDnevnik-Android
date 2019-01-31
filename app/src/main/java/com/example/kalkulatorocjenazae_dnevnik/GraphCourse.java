package com.example.kalkulatorocjenazae_dnevnik;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphCourse extends AppCompatActivity {
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_course);

        chart=findViewById(R.id.chartCourse);
        List<Entry> entries = new ArrayList<Entry>();

        for(int i=CourseActivity.alGrades.size()-1;i>=0;i--){
            entries.add(new Entry(CourseActivity.alGrades.size()-(i+1),Float.parseFloat(CourseActivity.alGrades.get(i))));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Ocjene"); // add entries to dataset
        dataSet.setColor(Color.argb(255,100,100,255));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.argb(220,100,100,255));
        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);
        chart.setData(lineData);
        chart.animateX(1000);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getXAxis().setDrawLabels(false);
        chart.getAxisLeft().setAxisMinimum(1);
        chart.getAxisLeft().setLabelCount(6);
        chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(new String[]{"","1","2","3","4","5"}));
        chart.getAxisLeft().setGranularityEnabled(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate(); // refresh
    }
}
