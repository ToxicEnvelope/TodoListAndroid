package com.example.cotherapist.Model;

public class User{
    private String mName;
    private String mPassword;
    private String mEmail;


    public User(){}

    public User(String name, String email, String password) {
        this.mName = name;
        this.mPassword = password;
        this.mEmail = email;
    }

    public User(String email, String password) {
        this.mPassword = password;
        this.mEmail = email;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
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
