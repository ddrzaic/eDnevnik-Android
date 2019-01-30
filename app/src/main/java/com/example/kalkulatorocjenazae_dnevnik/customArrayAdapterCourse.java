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

public class customArrayAdapterCourse extends ArrayAdapter {
    private Context context;
    private List<GradeInfo> grades;
    public customArrayAdapterCourse(Context context, int resource, ArrayList<GradeInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.grades = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listcourse, null);

        TextView tvGrade = view.findViewById(R.id.gradeTV);
        TextView tvDate = view.findViewById(R.id.dateTV);
        TextView tvNote = view.findViewById(R.id.noteTV);


        tvDate.setText(grades.get(position).date);
        tvNote.setText(grades.get(position).note);
        tvGrade.setText(grades.get(position).grade);

        if(grades.get(position).note.equals("Dodana ocjena")) {
            tvGrade.setTextColor(Color.BLUE);
            tvNote.setTextColor(Color.BLUE);
        }

        return view;
    }


}
