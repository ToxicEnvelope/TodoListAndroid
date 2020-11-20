package com.example.justdoit.Model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Task implements Serializable {
    private static final String TAG = "Task";
    @SerializedName("taskDescription")
    private String mDescription;
    @SerializedName("taskCreationTime")
    private String mTaskTime;
    @SerializedName("taskCompletionStatus")
    private String isCompleted;
    @SerializedName("taskId")
    private String mTaskId;

    private boolean isSelected;


    public Task(String description) {
        this.mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;

    }

    public String getTaskTime() {
        return convertTime(mTaskTime);

    }

    private String convertTime(String mTaskTime) {
        Map<String, String> months = new HashMap<>();
        months.put("Jan", "01");
        months.put("Feb", "02");
        months.put("Mar", "03");
        months.put("Apr", "04");
        months.put("May", "05");
        months.put("Jun", "06");
        months.put("Jul", "07");
        months.put("Aug", "08");
        months.put("Sep", "09");
        months.put("Oct", "09");
        months.put("Nov", "11");
        months.put("Dec", "12");

        String[] month = mTaskTime.split(" ");


        return month[1] + "/" + months.get(month[2]) + "/" + month[3];


    }


    public void setmaskTime(String taskTime) {
        this.mTaskTime = taskTime;
    }

    public String getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(String isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId(String taskId) {
        this.mTaskId = taskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "mDescription='" + mDescription + '\'' +
                ", mTaskTime='" + mTaskTime + '\'' +
                ", isCompleted='" + isCompleted + '\'' +
                ", mTaskId='" + mTaskId + '\'' +
                '}';
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
