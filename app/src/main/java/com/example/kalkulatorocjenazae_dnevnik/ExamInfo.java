package com.example.kalkulatorocjenazae_dnevnik;

public class ExamInfo {
    String examName="";
    String course="";
    String date="";

    public ExamInfo(String nameArg,String courseArg,String dateArg){
        examName=nameArg;
        course=courseArg;
        date=dateArg;
    }

    public String toStr(){
        return examName+"  "+course+"  "+date;
    }
}
