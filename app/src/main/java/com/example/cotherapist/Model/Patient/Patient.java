package com.example.cotherapist.Model.Patient;

public class Patient {
    private String mName;
    private String mDow;
    private String mHour;


    public Patient(){}

    public Patient(String name, String hour, String dow) {
        this.mName = name;
        this.mDow = dow;
        this.mHour = hour;
    }

    public Patient(String hour, String dow) {
        this.mDow = dow;
        this.mHour = hour;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPreferredDayOfWeek() {
        return mDow;
    }

    public void setPreferredDayOfWeek(String dob) {
        this.mDow = dob;
    }

    public String getPreferredHour() {
        return mHour;
    }

    public void setPreferredHour(String hour) {
        this.mHour = hour;
    }


}
