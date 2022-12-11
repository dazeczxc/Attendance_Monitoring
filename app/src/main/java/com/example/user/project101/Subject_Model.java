package com.example.user.project101;

/**
 * Created by USER on 10/07/2021.
 */
public class Subject_Model {

    private String subject;
    private  String section;
    private  String day;
    private String time;
    private String timeto;


    public Subject_Model(String subject, String section, String day, String time, String timeto) {
        this.subject = subject;
        this.section = section;
        this.day = day;
        this.time = time;
        this.timeto = timeto;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeto() {
        return timeto;
    }

    public void setTimeto(String timeto) {
        this.timeto = timeto;
    }
}
