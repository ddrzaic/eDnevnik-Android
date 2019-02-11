package com.example.kalkulatorocjenazae_dnevnik;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class customArrayAdapterNewGrade extends ArrayAdapter {
    private Context context;
    private List<NewGradeInfo> newGradeInfos;
    public customArrayAdapterNewGrade(Context context, int resource, ArrayList<NewGradeInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.newGradeInfos = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listnewgrade, null);

        TextView NewGradeCourse = view.findViewById(R.id.newGradeCourse);
        TextView NewGradeDate = view.findViewById(R.id.newGradeDate);
        TextView NewGradeNote = view.findViewById(R.id.newGradeNote);
        TextView NewGradeGrade = view.findViewById(R.id.newGradeGrade);


        NewGradeCourse.setText(newGradeInfos.get(position).course);
        NewGradeDate.setText(newGradeInfos.get(position).date);
        NewGradeNote.setText(newGradeInfos.get(position).note);
        NewGradeGrade.setText(newGradeInfos.get(position).grade);

        return view;
    }
}
