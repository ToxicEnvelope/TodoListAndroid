package com.example.justdoit.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justdoit.R;
import com.example.justdoit.ViewModel.AdapterViewModel;
import com.example.justdoit.ViewModel.TasksViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static final String TAG = "TaskAdapter";
    private List<Task> mTaskList;
    private List<Task> mSelectedTaskList;
    boolean isEnabled = false;
    boolean isSelected = false;
    private AdapterViewModel mViewModel;
    private Activity mActivity;


    public TaskAdapter(Activity activity, List<Task> taskList) {
        this.mTaskList = taskList;
        this.mActivity = activity;
        mSelectedTaskList = new ArrayList<>();
    }

    public interface TaskAdapterInterface {

        void onEditTaskClick(View v, int position);

        void onTaskCompleted(int position, boolean completed);

        void onDeleteTask(List<Task> taskListToDelete);
    }

    private TaskAdapterInterface listener;

    public void setTaskListener(TaskAdapterInterface taskListener) {
        this.listener = taskListener;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private ExtendedFloatingActionButton editButton;
        private CardView cardLayout;
        private RelativeLayout relativeLayout;
        private TextView descriptionTv;
        private TextView creationDateTv;
        private SwitchCompat completeSwitch;
        private ImageButton checkBtn;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBtn = itemView.findViewById(R.id.check_btn);
            relativeLayout = itemView.findViewById(R.id.card_realtive_layout);
            cardLayout = itemView.findViewById(R.id.card_layout);
            descriptionTv = itemView.findViewById(R.id.content);
            creationDateTv = itemView.findViewById(R.id.create_time);
            editButton = itemView.findViewById(R.id.edit_option);
            completeSwitch = itemView.findViewById(R.id.switch_toggle);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onEditTaskClick(view, getAdapterPosition());
                    }
                }
            });

            completeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        Log.d(TAG, "onCheckedChanged: " + getAdapterPosition());
                        listener.onTaskCompleted(getAdapterPosition(), completeSwitch.isChecked());
                    }
                }
            });


        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        mViewModel = new ViewModelProvider((FragmentActivity) mActivity, new ViewModelFactory(mActivity.getApplicationContext(),
                ViewModelEnum.Adapter)).get(AdapterViewModel.class);
        return new TaskViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {
        final Task task = mTaskList.get(holder.getAdapterPosition());
        holder.completeSwitch.setChecked(task.getIsCompleted().equals(StaticStringUtil.COMPLETED));
        if (task.getIsCompleted().equals(StaticStringUtil.COMPLETED)) {

            holder.completeSwitch.setEnabled(false);
            holder.editButton.setEnabled(false);
            Log.d(TAG, "onBindViewHolder: is checked"+position);
            holder.cardLayout.setCardBackgroundColor(Color.LTGRAY);
        }else{
            holder.completeSwitch.setEnabled(true);
            holder.editButton.setEnabled(true);
            holder.cardLayout.setCardBackgroundColor(Color.WHITE);

        }
        holder.completeSwitch.setText(task.getIsCompleted().equals(StaticStringUtil.COMPLETED) ? StaticStringUtil.COMPLETED : StaticStringUtil.NOT_STARTED);
        holder.descriptionTv.setText(task.getDescription());
        holder.creationDateTv.setText(task.getTaskTime());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEnabled) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMenMode, Menu menu) {
                            MenuInflater menuInflater = actionMenMode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu, menu);

                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(final ActionMode actionMode, Menu menu) {
                            isEnabled = true;
                            ClickItem(holder);
                            mViewModel.getTextView().observe((LifecycleOwner) mActivity, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    actionMode.setTitle(String.format("%s Selected", s));
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            if (id == R.id.menu_delete) {
                                if (listener != null) {
                                    listener.onDeleteTask(mSelectedTaskList);
                                }
                            }
                            actionMode.finish();
                            //here we will need to send it database
                            //notifyDataSetChanged();

                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnabled = false;
                            isSelected = false;
                            for(Task task:mSelectedTaskList){
                                task.setSelected(false);
                            }
                            mSelectedTaskList.clear();
                            notifyDataSetChanged();
                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);
                }
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnabled) {
                    ClickItem(holder);
                }
            }
        });
        if(task.isSelected()){
            holder.checkBtn.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }else{
            holder.checkBtn.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

//        if (!isSelected) {
//            Log.d(TAG, "onBindViewHolder: "+position);
//            holder.checkBtn.setVisibility(View.GONE);
//            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//        }else{
//            holder.checkBtn.setVisibility(View.VISIBLE);
//            holder.itemView.setBackgroundColor(Color.LTGRAY);
//        }
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    private String timestampToTimeAgo(Date date) {
        String language = Locale.getDefault().getLanguage();
        PrettyTime prettyTime = new PrettyTime(new Locale(language));
        return prettyTime.format(date);
    }

    private void ClickItem(TaskViewHolder holder) {
        Task selectedTask = mTaskList.get(holder.getAdapterPosition());
        Log.d(TAG, "ClickItem: "+holder.getAdapterPosition());
        if (holder.checkBtn.getVisibility() == View.GONE) {
            selectedTask.setSelected(true);
            holder.checkBtn.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            mSelectedTaskList.add(selectedTask);
        } else {
            selectedTask.setSelected(false);
            holder.checkBtn.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            mSelectedTaskList.remove(selectedTask);
        }
        mViewModel.setTextView(String.valueOf(mSelectedTaskList.size()));
        //size of selected size list.
    }


}