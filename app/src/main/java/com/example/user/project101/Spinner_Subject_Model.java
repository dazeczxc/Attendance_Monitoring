package com.example.user.project101;

/**
 * Created by USER on 10/07/2021.
 */
public class Spinner_Subject_Model {

     private String subject;
    private  String section;

    public Spinner_Subject_Model(String subject, String section) {

        this.subject = subject;
        this.section = section;
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

}
