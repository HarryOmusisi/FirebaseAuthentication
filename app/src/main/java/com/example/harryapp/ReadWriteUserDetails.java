package com.example.harryapp;

public class ReadWriteUserDetails {
    public String  dob, gender, mobile;

    //constructor
    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile){
        this.dob=textDoB;
        this.gender=textGender;
        this.mobile=textMobile;
    }
}
