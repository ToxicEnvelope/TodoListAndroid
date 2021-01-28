package com.example.cotherapist.Repository;

import com.example.cotherapist.Model.Task;
import com.example.cotherapist.Model.Therapist.Therapist;
import java.util.List;

public abstract class BaseRepository {

    protected static final String TAG = "Repository";
    protected static String BASE_URL = "http://cotherapist.ddns.net:5443/api/v1";
    private Therapist mTherapist;
    private Task mTask;
    private String authKey;

    //***--------------**//
    //SignUp Therapist

    public interface RepositorySignUpInterface {
        void onUserSignUpSucceed();
        void onUserSignUpFailed();
    }

    //***--------------**//
    //Login Therapist

    public interface RepositoryLoginInterface {
        void onUserLoginSucceed();
        void onUserLoginFailed();
    }

    //***--------------**//
    //Download Task

    public interface RepositoryDownloadTasksInterface {
        void onUserDownloadTasksSucceed(List<Task> taskList);
        void onUserDownloadTasksFailed(String error);
    }

    //***--------------**//
    //Upload Task

    public interface RepositoryUploadTaskInterface {
        void onUploadTasksSucceed(Task task);
        void onUploadTasksFailed(String error);
    }

    //***--------------**//
    //Update Task

    public interface RepositoryUpdateTaskInterface {
        void onUpdateTasksSucceed(Task task, String description, boolean isCompleted);
        void onUpdateTasksFailed(String error);
    }

    //***--------------**//
    //Delete Task

    public interface RepositoryDeleteTaskInterface {
        void onDeleteTasksSucceed(Task task);
        void onDeleteTasksFailed();
    }

    public void setAuthKey(String authKey) { this.authKey = authKey; }

    public String getAuthKey() {
        return authKey;
    }

    public void setTask(Task task) {
        this.mTask = task;
    }

    public Task getTask() {
        return mTask;
    }

    public void setTherapist(Therapist therapist) { this.mTherapist = therapist; }

    public Therapist getTherapist() {
        return mTherapist;
    }
}
