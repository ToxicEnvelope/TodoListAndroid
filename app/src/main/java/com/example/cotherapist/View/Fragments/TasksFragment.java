package com.example.cotherapist.View.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.cotherapist.Model.AlertRecevier;
import com.example.cotherapist.Model.Task;
import com.example.cotherapist.Model.TaskAdapter;
import com.example.cotherapist.Model.ViewModelEnum;
import com.example.cotherapist.Model.ViewModelFactory;
import com.example.cotherapist.R;
import com.example.cotherapist.ViewModel.TasksViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class TasksFragment extends Fragment {
    private final String TAG = "TasksFragment";
    private TasksViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private TaskAdapter mTasksAdapter;
    private boolean isCompleted;

    private Observer<List<Task>> mOnTasklistDownloadedSucceeded;
    private Observer<String> mOnTasklistDownloadedFailed;

    private Observer<Task> mOnTaskUploadSucceeded;
    private Observer<String> mOnTaskUploadFailed;

    private Observer<Boolean> mOnTaskUpdateSucceeded;
    private Observer<String> mOnTaskUpdateFailed;


    private Observer<Task> mOnTaskDeleteSucceeded;
    private Observer<String> mOnTaskDeleteFailed;






    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_fragment, container, false);
        final FloatingActionButton addTasksButton = view.findViewById(R.id.add_task);
        mRecyclerView = view.findViewById(R.id.task_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        addTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTaskDialog();
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Task)).get(TasksViewModel.class);
        Log.d(TAG, "onCreate: " + mViewModel);




        mOnTasklistDownloadedSucceeded = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                Log.d(TAG, "onTaskDownload: "+mViewModel.getTaskList().size());
                mTasksAdapter = new TaskAdapter(requireActivity(), mViewModel.getTaskList());
                mTasksAdapter.setTaskListener(new TaskAdapter.TaskAdapterInterface() {
                    @Override
                    public void onEditTaskClick(View v, int position) {
                        editTask(position);
                    }

                    @Override
                    public void onTaskCompleted(final int position, final boolean completed) {
                        mViewModel.updateTask(position,null,completed);
                        isCompleted=completed;
                        CancelAlarm(mViewModel.getTaskList().get(position));
                    }

                    @Override
                    public void onDeleteTask(List<Task> taskListToDelete) {
                        mViewModel.deleteTasksFromDB(taskListToDelete);
                    }
                });
                Log.d(TAG, "onChanged: "+mOnTaskUpdateSucceeded);
                mRecyclerView.setAdapter(mTasksAdapter);
            }
        };



        mOnTaskUpdateSucceeded = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "onChanged: update task"+mViewModel.getPosition());
                mTasksAdapter.notifyItemChanged(mViewModel.getPosition());
//                mRecyclerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }
        };

        mOnTasklistDownloadedFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Log.d(TAG, "onChanged: " + error);
            }
        };

        mOnTaskUploadSucceeded = new Observer<Task>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(Task task) {
                Log.d(TAG, "onChanged: " + "task added");
                int position=mViewModel.getTaskList().size() - 1;
                mTasksAdapter.notifyItemInserted(position);
                Calendar c=Calendar.getInstance();
                c.set(Calendar.SECOND, (LocalDateTime.now().getSecond()+5)%60);
                Log.d(TAG, "onChanged: "+c.get(Calendar.SECOND));
                try {
                    startAlarm(c,mViewModel.getTaskList().get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mOnTaskUploadFailed = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: error");
            }
        };

        mOnTaskDeleteSucceeded=new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                Log.d(TAG, "delete ");
                mTasksAdapter.notifyItemRemoved(mViewModel.getPosition());
            }
        };


        mViewModel.downLoadTasks();
        mViewModel.getTasksDeleteSucceed().observe(this,mOnTaskDeleteSucceeded);
        mViewModel.getTasksUpdateSucceed().observe(this,mOnTaskUpdateSucceeded);
        mViewModel.getTasksDownloadSucceed().observe(this, mOnTasklistDownloadedSucceeded);
        mViewModel.getTasksDownloadFailed().observe(this, mOnTasklistDownloadedFailed);
        mViewModel.getTasksUploadSucceed().observe(this, mOnTaskUploadSucceeded);
        mViewModel.getTasksUploadFailed().observe(this, mOnTaskUploadFailed);

    }

    private void startAlarm(Calendar c,Task task) throws IOException {
        AlarmManager alarmManager=(AlarmManager)requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(requireContext(), AlertRecevier.class);
        Log.d(TAG, "startAlarm:"+ task.getDescription());
        intent.putExtra("task",serialize(task));
        PendingIntent pendingIntent=PendingIntent.getBroadcast(requireContext(), 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
    }

    private void CancelAlarm(Task task){

        AlarmManager alarmManager=(AlarmManager)requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(requireContext(), AlertRecevier.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(requireContext(), 1, intent,0);
        alarmManager.cancel(pendingIntent);

    }

    private void addNewTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.add_task_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText taskContent = view.findViewById(R.id.new_task);
        final Button submitBtn = view.findViewById(R.id.post_btn);
        final Button cancelBtn = view.findViewById(R.id.cancel_btn);



        submitBtn.setEnabled(false);

        final AlertDialog alertDialog = builder.create();

        taskContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String taskDescription = taskContent.getText().toString();
                mViewModel.uploadNewTask(taskDescription);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void editTask(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.edit_task_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText taskContent = view.findViewById(R.id.edit_task);
        final Button submitBtn = view.findViewById(R.id.update_btn);
        final Button cancelBtn = view.findViewById(R.id.cancel_btn);

        submitBtn.setEnabled(false);
        taskContent.setText(mViewModel.getTaskList().get(position).getDescription());
        final AlertDialog alertDialog = builder.create();

        taskContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String taskDescription = taskContent.getText().toString();
                mViewModel.updateTask(position,taskDescription,false);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}