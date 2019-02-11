package com.example.kalkulatorocjenazae_dnevnik;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.utils.ViewPortHandler;

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

        ///////////////////////////////////////

        final ArrayList<String> gradeDate = new ArrayList<>();
        final ArrayList<String> gradeNote = new ArrayList<>();
        for(int i=CourseActivity.alGradeInfo.size()-1;i>=0;i--){
            gradeDate.add(CourseActivity.alGradeInfo.get(i).date);
            gradeNote.add(CourseActivity.alGradeInfo.get(i).note);
        }

        ///////////////////////////////

        LineDataSet dataSet = new LineDataSet(entries, "Ocjene"); // add entries to dataset
        dataSet.setColor(Color.argb(255,50,50,255));
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextSize(7);
        //lineData.setDrawValues(false);
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return gradeNote.get((int) entry.getX());
            }
        });

        chart.setData(lineData);
        chart.animateX(1000);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setGranularity(1f);
       // chart.getXAxis().setDrawLabels(false);
        chart.getAxisLeft().setAxisMinimum(1);
        chart.getAxisLeft().setLabelCount(6);
        chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(new String[]{"","1","2","3","4","5"}));
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return gradeDate.get((int) value);
            }
        });
        chart.getAxisLeft().setGranularityEnabled(true);
        //chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setGranularity(1.0f);
        chart.getXAxis().setLabelRotationAngle(-45);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setLabelCount(gradeDate.size());
        chart.setScaleMinima(3,1);
        chart.setExtraOffsets(50, 20, 10, 20);
        chart.setRenderer(new RotatedBarChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler()));
        chart.invalidate(); // refresh


    }
}
