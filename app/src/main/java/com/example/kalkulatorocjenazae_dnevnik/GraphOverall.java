package com.example.kalkulatorocjenazae_dnevnik;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphOverall extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_overall);

        HorizontalBarChart chart = findViewById(R.id.chart);
        List<BarEntry> entries = new ArrayList<BarEntry>();


        for(int i=0;i<OverallActivity.alCourses.size();i++){
            if(!OverallActivity.alRealGrade.get(i).equals("0.00")){
                if(Float.parseFloat(OverallActivity.alUserGrade.get(i))>
                        Float.parseFloat(OverallActivity.alRealGrade.get(i))){
                    entries.add(new BarEntry(i,
                            new float[]{Float.parseFloat(
                                    OverallActivity.alRealGrade.get(i)),
                            Float.parseFloat(OverallActivity.alUserGrade.get(i))-
                                    Float.parseFloat(OverallActivity.alRealGrade.get(i)),
                                    0}));
                }else {
                    entries.add(new BarEntry(i,
                            new float[]{Float.parseFloat(OverallActivity.alUserGrade.get(i)),
                                    0,
                                    Float.parseFloat(OverallActivity.alRealGrade.get(i))-
                                            Float.parseFloat(OverallActivity.alUserGrade.get(i))}));
                }
            }

        }


        ArrayList<String> alXAxisDataGrades;
        alXAxisDataGrades = new ArrayList<>();
        for(int i=0;i<6;i++){
            alXAxisDataGrades.add(String.valueOf(i));
        }

        BarDataSet dataSet = new BarDataSet(entries,""); // add entries to dataset
        dataSet.setStackLabels(new String[]{"Stvarne ocjene","Veće ocjene","Manje ocjene"});
        dataSet.setColors(new int[]{Color.argb(255,58,113,252),
                Color.argb(255,100,200,100),
                Color.argb(255,200,100,100)});
        dataSet.setDrawValues(false);


        BarData barData = new BarData(dataSet);
        chart.setDragEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setHighlightPerDragEnabled(true);
        chart.setData(barData);
        chart.animateXY(2000, 2000);
        chart.setDrawGridBackground(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        chart.getXAxis().setLabelCount(OverallActivity.alCourses.size());
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(OverallActivity.alCourses));
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setGranularityEnabled(true);
        chart.getAxisLeft().setLabelCount(OverallActivity.alUserGrade.size());
        LimitLine avg=new LimitLine(Float.parseFloat(OverallActivity.getUserAverage().replace(",",".")),
                OverallActivity.getUserAverage());
        avg.setTextColor(Color.argb(180,0,0,0));
        chart.getAxisRight().addLimitLine(avg);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.invalidate(); // refresh
    }
}
