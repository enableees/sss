package com.example.kurstaskmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurstaskmanager.data.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener clickListener;
    private OnTaskLongClickListener longClickListener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public void clearSelection() {
        int oldSelected = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyItemChanged(oldSelected);
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnTaskLongClickListener(OnTaskLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, clickListener, longClickListener, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDeadline, tvPriority, tvStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTaskName);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Task task, OnTaskClickListener clickListener,
                         OnTaskLongClickListener longClickListener, boolean isSelected) {
            tvName.setText(task.getName());

            if (task.getDeadline() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                tvDeadline.setText("Дедлайн: " + sdf.format(new Date(task.getDeadline())));
            } else {
                tvDeadline.setText("Без дедлайна");
            }

            String[] priorities = {"Низкий", "Средний", "Высокий"};
            tvPriority.setText(priorities[task.getPriority()]);

            String[] statuses = {"в процессе", "выполнена", "не выполнена"};
            tvStatus.setText(statuses[task.getStatus()]);

            itemView.setSelected(isSelected);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onTaskClick(task);
            });

            itemView.setOnLongClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                if (longClickListener != null) longClickListener.onTaskLongClick(task);
                return true;
            });
        }
    }
}