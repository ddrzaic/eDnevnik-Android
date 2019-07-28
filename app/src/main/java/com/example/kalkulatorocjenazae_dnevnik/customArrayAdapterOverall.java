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


public class customArrayAdapterOverall extends ArrayAdapter{
    private Context context;
    private List<CourseInfo> courses;
    public customArrayAdapterOverall(Context context, int resource, ArrayList<CourseInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.courses = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        CourseInfo course= courses.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listoverall, null);

        TextView tvCourseName = view.findViewById(R.id.courseNameTV);
        TextView tvTeacherName = view.findViewById(R.id.TeacherNameTV);
        TextView tvAverageGrade = view.findViewById(R.id.courseAverageTV);
        TextView tvUserGrade = view.findViewById(R.id.gradeTV);

        tvCourseName.setText(course.courseName);
        tvTeacherName.setText(course.teacherName);
        tvAverageGrade.setText(course.averageGrade);
        tvUserGrade.setText(course.userGrade);

        if(!OverallActivity.alUserGrade.get(position).equals(OverallActivity.alRealGrade.get(position))){
            tvUserGrade.setTextColor(Color.RED);
        }
        if(!OverallActivity.alUserFinal.get(position).equals("")){
            tvCourseName.setTextColor(Color.BLUE);
            tvAverageGrade.setText(course.averageGrade+"\nZaključeno "+OverallActivity.alUserFinal.get(position));
            tvAverageGrade.setTextColor(Color.BLUE);
            tvTeacherName.setTextColor(Color.BLUE);
        }
        return view;
    }
}
