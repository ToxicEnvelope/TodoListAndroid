package com.example.cotherapist.Model.Therapist;

public class Therapist {
    private String mFullName;
    private String mPassword;
    private String mEmail;


    public Therapist(){}

    public Therapist(String email, String password) {
        this.mFullName = null;
        this.mPassword = password;
        this.mEmail = email;
    }

    public Therapist(String fullName, String email, String password) {
        this.mFullName = fullName;
        this.mPassword = password;
        this.mEmail = email;
    }

    public String getFullName() { return mFullName;}

    public void setFullName(String fullName) {
        this.mFullName = fullName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }


}
