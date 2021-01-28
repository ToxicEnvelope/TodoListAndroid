package com.example.justdoit.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.justdoit.Model.Task;
import com.example.justdoit.Repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class TasksViewModel extends ViewModel {
    private final String TAG = "TasksViewModel";
    private Repository mRepository;
    private List<Task> mTaskList = new ArrayList<>();
    private int mPosition;

    private MutableLiveData<List<Task>> mTasksDownloadSucceed;
    private MutableLiveData<String> mTasksDownloadFailed;


    private MutableLiveData<Task> mTasksUploadSucceed;
    private MutableLiveData<String> mTasksUploadFailed;

    private MutableLiveData<Boolean> mTasksUpdateSucceed;
    private MutableLiveData<String> mTasksUpdateFailed;

    private MutableLiveData<Task> mTasksDeleteSucceed;
    private MutableLiveData<String> mTasksDeleteFailed;


    public TasksViewModel(final Context context) {
        mRepository = Repository.getInstance(context);
    }

    public MutableLiveData<List<Task>> getTasksDownloadSucceed() {
        if (mTasksDownloadSucceed == null) {
            mTasksDownloadSucceed = new MutableLiveData<>();
            attachDownloadTaskListener();
        }
        return mTasksDownloadSucceed;
    }

    public MutableLiveData<String> getTasksDownloadFailed() {
        if (mTasksDownloadFailed == null) {
            mTasksDownloadFailed = new MutableLiveData<>();
            attachDownloadTaskListener();
        }
        return mTasksDownloadFailed;
    }

    private void attachDownloadTaskListener() {
        mRepository.setDownloadTasksListener(new Repository.RepositoryDownloadTasksInterface() {
            @Override
            public void onUserDownloadTasksucceed(List<Task> taskList) {
                if (!mTaskList.isEmpty()) {
                    mTaskList.clear();
                }
                mTaskList.addAll(taskList);
                Log.d(TAG, "onUserDownloadTasksucceed: " + mTaskList.size());
                mTasksDownloadSucceed.setValue(mTaskList);
            }

            @Override
            public void onUserDownloadTasksSFailed(String error) {
                mTasksDownloadFailed.setValue(error);
            }
        });

    }

    public MutableLiveData<Task> getTasksUploadSucceed() {
        if (mTasksUploadSucceed == null) {
            mTasksUploadSucceed = new MutableLiveData<>();
            attachUploadTaskListener();
        }
        return mTasksUploadSucceed;
    }

    public MutableLiveData<String> getTasksUploadFailed() {
        if (mTasksUploadFailed == null) {
            mTasksUploadFailed = new MutableLiveData<>();
            attachUploadTaskListener();
        }
        return mTasksUploadFailed;
    }

    private void attachUploadTaskListener() {
        mRepository.setUploadTaskListener(new Repository.RepositoryUploadTaskInterface() {
            @Override
            public void onUploadTasksucceed(Task task) {
                mTaskList.add(task);
                mTasksUploadSucceed.setValue(task);

            }

            @Override
            public void onUploadTasksSFailed(String error) {
                mTasksUploadFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Boolean> getTasksUpdateSucceed() {
        if (mTasksUpdateSucceed == null) {
            mTasksUpdateSucceed = new MutableLiveData<>();
            attachUpdateTaskListener();
        }
        return mTasksUpdateSucceed;
    }

    public MutableLiveData<String> getTasksUpdateFailed() {
        if (mTasksUpdateFailed == null) {
            mTasksUpdateFailed = new MutableLiveData<>();
            attachUpdateTaskListener();
        }
        return mTasksUpdateFailed;
    }

    private void attachUpdateTaskListener() {
        mRepository.setUpdateTaskListener(new Repository.RepositoryUpdateTaskInterface() {
            @Override
            public void onUpdateTasksucceed(Task task,String description,boolean isComplete) {
                Log.d(TAG, "onUpdateTasksucceed: "+task.toString());
                Log.d(TAG, "onUpdateTasksucceed: "+mPosition);


                if(mTaskList.indexOf(task)!=-1) {
                    mTaskList.get(mTaskList.indexOf(task)).setDescription(description);
                    mTaskList.get(mTaskList.indexOf(task)).setIsCompleted(isComplete ? "completed" : "not started");
                }else{
                    mTaskList.get(mTaskList.size()-1).setIsCompleted("completed");
                    mPosition=mTaskList.size()-1;
                }
                mTasksUpdateSucceed.setValue(true);

            }

            @Override
            public void onUpdateTasksFailed(String error) {
//                mTasksUpdateFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Task> getTasksDeleteSucceed() {
        if (mTasksDeleteSucceed == null) {
            mTasksDeleteSucceed = new MutableLiveData<>();
            attachDeleteTaskListener();
        }
        return mTasksDeleteSucceed;
    }

    public MutableLiveData<String> getTasksDeleteailed() {
        if (mTasksDeleteFailed == null) {
            mTasksDeleteFailed = new MutableLiveData<>();
            attachDeleteTaskListener();
        }
        return mTasksDeleteFailed;
    }

    private void attachDeleteTaskListener() {
        mRepository.setDeleteTaskListener(new Repository.RepositoryDeleteTaskInterface() {
            @Override
            public void onDeleteTasksucceed(Task task) {
                mPosition = mTaskList.indexOf(task);
                mTaskList.remove(task);
                Log.d(TAG, "onDeleteTasksucceed: ");
                mTasksDeleteSucceed.setValue(task);
            }

            @Override
            public void onDeleteTasksSFailed() {

            }
        });
    }

    public void downLoadTasks() {
        mRepository.getAllTasks();
    }

    public List<Task> getTaskList() {
        return mTaskList;
    }

    public void uploadNewTask(String taskDescription) {
        mRepository.uploadNewTask(taskDescription);
    }

    public void updateTask(int position, String description,boolean isCompleted) {
        Task task = mTaskList.get(position);
        mRepository.updateTask(task, description,isCompleted);
        mPosition = position;

    }

    public int getPosition() {
        return mPosition;
    }

    public void deleteTasksFromDB(List<Task> taskListToDelete) {
        for (Task task : taskListToDelete) {
            mRepository.deleteTask(task);
        }
    }
}