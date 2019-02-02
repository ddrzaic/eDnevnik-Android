package com.example.kalkulatorocjenazae_dnevnik;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class customArrayAdapterExams extends ArrayAdapter {
    private Context context;
    private List<ExamInfo> exams;
    public customArrayAdapterExams(Context context, int resource, ArrayList<ExamInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.exams = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listexam, null);

        TextView ExamName = view.findViewById(R.id.tvExamName);
        TextView ExamDate = view.findViewById(R.id.tvExamDate);
        TextView ExamCourse = view.findViewById(R.id.tvExamCourse);


        ExamCourse.setText(exams.get(position).course);
        ExamDate.setText(exams.get(position).date);
        ExamName.setText(exams.get(position).examName);

        return view;
    }
}
