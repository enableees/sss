package com.example.kurstaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurstaskmanager.data.AppDatabase;
import com.example.kurstaskmanager.data.BlockedItem;
import com.example.kurstaskmanager.data.BlockedItemDao;
import com.example.kurstaskmanager.data.Task;
import com.example.kurstaskmanager.data.TaskDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {

    private Task selectedTask = null;
    private Button btnEditTask, btnDeleteTask;
    private TaskAdapter taskAdapter;
    private List<Task> allTasks = new ArrayList<>();
    private List<BlockedItem> allBlockedItems = new ArrayList<>();

    private Spinner spinnerFilter, spinnerSort;
    private EditText editSearch;

    private int currentFilterPos = 0;
    private int currentSortPos = 0;

    private TaskDao taskDao;
    private BlockedItemDao blockedItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tasks);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_tasks);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_tasks) {
                return true;
            } else if (id == R.id.nav_blocked) {
                startActivity(new Intent(TasksActivity.this, BlockedAppsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(TasksActivity.this, StatisticsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        AppDatabase db = AppDatabase.getInstance(this);
        taskDao = db.taskDao();
        blockedItemDao = db.blockedItemDao();
        checkOverdueTasks();

        loadBlockedItems();

        RecyclerView recyclerTasks = findViewById(R.id.recyclerTasks);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        recyclerTasks.setAdapter(taskAdapter);

        btnEditTask = findViewById(R.id.btnEditTask);
        btnDeleteTask = findViewById(R.id.btnDeleteTask);
        btnEditTask.setEnabled(false);
        btnDeleteTask.setEnabled(false);

        spinnerFilter = findViewById(R.id.spinnerFilter);
        spinnerSort = findViewById(R.id.spinnerSort);
        editSearch = findViewById(R.id.editSearch);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                getResources().getStringArray(R.array.filter_options));
        filterAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                getResources().getStringArray(R.array.sort_options));
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentFilterPos = pos;
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentSortPos = pos;
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        taskAdapter.setOnTaskLongClickListener(task -> {
            selectedTask = task;
            btnEditTask.setEnabled(true);
            btnDeleteTask.setEnabled(true);
        });

        taskAdapter.setOnTaskClickListener(task -> showTaskDetailDialog(task));

        taskDao.getAllTasks().observe(this, tasks -> {
            allTasks.clear();
            allTasks.addAll(tasks);
            applyFilters();
        });

        btnDeleteTask.setOnClickListener(v -> {
            if (selectedTask == null) return;
            new AlertDialog.Builder(this)
                    .setTitle("Удаление")
                    .setMessage("Точно удалить задачу \"" + selectedTask.getName() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        taskDao.deleteTask(selectedTask.getId());
                        taskAdapter.clearSelection();
                        selectedTask = null;
                        btnEditTask.setEnabled(false);
                        btnDeleteTask.setEnabled(false);
                        Toast.makeText(TasksActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        btnEditTask.setOnClickListener(v -> {
            if (selectedTask == null) return;
            showEditTaskDialog(selectedTask);
        });

        Button btnCreateTask = findViewById(R.id.btnCreateTask);
        btnCreateTask.setOnClickListener(v -> showCreateTaskDialog());
    }

    private void checkOverdueTasks() {
        List<Task> all = taskDao.getAllTasksSync();
        long now = System.currentTimeMillis();
        for (Task t : all) {
            if (t.getDeadline() != null && t.getDeadline() < now
                    && t.getStatus() != 1 && t.getStatus() != 2) {
                t.setStatus(2);
                taskDao.updateTask(t);
                List<Long> blockedIds = t.getBlockedIds();
                if (blockedIds != null) {
                    for (long id : blockedIds) {
                        blockedItemDao.setActive(id, false);
                    }
                }
            }
        }
    }
    private void loadBlockedItems() {
        allBlockedItems = blockedItemDao.getAllBlockedItemsSync();
    }

    private void showCreateTaskDialog() {
        loadBlockedItems();

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_task, null);

        EditText editName = dialogView.findViewById(R.id.editTaskName);
        EditText editDescription = dialogView.findViewById(R.id.editTaskDescription);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        EditText editPunishment = dialogView.findViewById(R.id.editPunishment);
        EditText editDeadline = dialogView.findViewById(R.id.editDeadline);
        LinearLayout containerBlocked = dialogView.findViewById(R.id.containerBlockedCheckboxes);

        containerBlocked.removeAllViews();
        for (BlockedItem item : allBlockedItems) {
            CheckBox cb = new CheckBox(this);
            String typeStr = item.getType() == 0 ? "Прил." : "Сайт";
            cb.setText(typeStr + " " + item.getName());
            cb.setTag(item.getId());
            cb.setTextColor(getResources().getColor(R.color.black));
            containerBlocked.addView(cb);
        }

        new AlertDialog.Builder(this)
                .setTitle("Новая задача")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(TasksActivity.this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String description = editDescription.getText().toString().trim();
                    int priority = spinnerPriority.getSelectedItemPosition();
                    String punishment = editPunishment.getText().toString().trim();

                    List<Long> blockedIds = new ArrayList<>();
                    for (int i = 0; i < containerBlocked.getChildCount(); i++) {
                        View child = containerBlocked.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            if (cb.isChecked()) {
                                blockedIds.add((Long) cb.getTag());
                            }
                        }
                    }

                    Long deadline = null;
                    String deadlineStr = editDeadline.getText().toString().trim();
                    if (!deadlineStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            Date date = sdf.parse(deadlineStr);
                            deadline = date.getTime();
                        } catch (Exception e) {
                            Toast.makeText(TasksActivity.this, "Неверный формат даты", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Task task = new Task(name, deadline, priority, description,
                            "", punishment, 0, System.currentTimeMillis(), blockedIds);

                    long id = taskDao.insertTask(task);
                    if (id != -1) {
                        if (deadline != null) {
                            NotificationHelper.showInstantNotification(TasksActivity.this, name, deadlineStr);
                        }
                        Toast.makeText(TasksActivity.this, "Задача создана", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TasksActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditTaskDialog(Task task) {
        loadBlockedItems();

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_task, null);

        EditText editName = dialogView.findViewById(R.id.editTaskName);
        EditText editDescription = dialogView.findViewById(R.id.editTaskDescription);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        EditText editPunishment = dialogView.findViewById(R.id.editPunishment);
        EditText editDeadline = dialogView.findViewById(R.id.editDeadline);
        LinearLayout containerBlocked = dialogView.findViewById(R.id.containerBlockedCheckboxes);

        editName.setText(task.getName());
        editDescription.setText(task.getDescription());
        spinnerPriority.setSelection(task.getPriority());
        editPunishment.setText(task.getPunishmentText());
        if (task.getDeadline() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            editDeadline.setText(sdf.format(new Date(task.getDeadline())));
        }

        containerBlocked.removeAllViews();
        List<Long> currentBlockedIds = task.getBlockedIds();
        for (BlockedItem item : allBlockedItems) {
            CheckBox cb = new CheckBox(this);
            String typeStr = item.getType() == 0 ? "Прил." : "Сайт";
            cb.setText(typeStr + " " + item.getName());
            cb.setTag(item.getId());
            cb.setTextColor(getResources().getColor(R.color.black));
            if (currentBlockedIds != null && currentBlockedIds.contains((Long) item.getId())) {
                cb.setChecked(true);
            }
            containerBlocked.addView(cb);
        }

        new AlertDialog.Builder(this)
                .setTitle("Редактирование задачи")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(TasksActivity.this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    task.setName(name);
                    task.setDescription(editDescription.getText().toString().trim());
                    task.setPriority(spinnerPriority.getSelectedItemPosition());
                    task.setPunishmentText(editPunishment.getText().toString().trim());

                    List<Long> blockedIds = new ArrayList<>();
                    for (int i = 0; i < containerBlocked.getChildCount(); i++) {
                        View child = containerBlocked.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            if (cb.isChecked()) {
                                blockedIds.add((Long) cb.getTag());
                            }
                        }
                    }
                    task.setBlockedIds(blockedIds);

                    String deadlineStr = editDeadline.getText().toString().trim();
                    if (!deadlineStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            Date date = sdf.parse(deadlineStr);
                            task.setDeadline(date.getTime());
                        } catch (Exception e) {
                            Toast.makeText(TasksActivity.this, "Неверный формат даты", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        task.setDeadline(null);
                    }

                    taskDao.updateTask(task);
                    taskAdapter.clearSelection();
                    selectedTask = null;
                    btnEditTask.setEnabled(false);
                    btnDeleteTask.setEnabled(false);
                    Toast.makeText(TasksActivity.this, "Задача обновлена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showTaskDetailDialog(Task task) {
        loadBlockedItems();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(task.getName());

        String desc = task.getDescription() != null && !task.getDescription().isEmpty() ?
                task.getDescription() : "Нет описания";
        String deadlineStr = task.getDeadline() != null ?
                new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(task.getDeadline())) :
                "Без дедлайна";
        String[] priorities = {"Низкий", "Средний", "Высокий"};
        String priority = priorities[task.getPriority()];
        String[] statuses = {"в процессе", "выполнена", "не выполнена"};
        String status = statuses[task.getStatus()];
        String punishment = task.getPunishmentText() != null && !task.getPunishmentText().isEmpty() ?
                task.getPunishmentText() : "Не указано";
        String reward = task.getRewardText() != null && !task.getRewardText().isEmpty() ?
                task.getRewardText() : "Не получена";

        StringBuilder blockedStr = new StringBuilder();
        List<Long> blockedIds = task.getBlockedIds();
        if (blockedIds != null && !blockedIds.isEmpty()) {
            for (long id : blockedIds) {
                for (BlockedItem bi : allBlockedItems) {
                    if (bi.getId() == id) {
                        if (blockedStr.length() > 0) blockedStr.append(", ");
                        blockedStr.append(bi.getName());
                        break;
                    }
                }
            }
        } else {
            blockedStr.append("Нет");
        }

        String message = "Описание: " + desc + "\n\n" +
                "Дедлайн: " + deadlineStr + "\n" +
                "Приоритет: " + priority + "\n" +
                "Статус: " + status + "\n" +
                "Наказание: " + punishment + "\n" +
                "Награда: " + reward + "\n" +
                "Блокируемые: " + blockedStr.toString();

        builder.setMessage(message);
        builder.setNeutralButton("Статус", (dialog, which) -> showStatusDialog(task));
        builder.setPositiveButton("Редактировать", (dialog, which) -> showEditTaskDialog(task));
        builder.setNegativeButton("Закрыть", null);
        builder.show();
    }

    private void showStatusDialog(Task task) {
        String[] statuses = {"В процессе", "Выполнена", "Не выполнена"};
        new AlertDialog.Builder(this)
                .setTitle("Изменить статус")
                .setItems(statuses, (dialog, which) -> {
                    task.setStatus(which);

                    List<Long> blockedIds = task.getBlockedIds();
                    if (blockedIds != null) {
                        boolean activate = (which == 0);
                        for (long id : blockedIds) {
                            blockedItemDao.setActive(id, activate);
                        }
                    }

                    String message = null;
                    if (which == 1) {
                        String reward = "Молодец! Задача выполнена!";
                        task.setRewardText(reward);
                        message = reward;
                    } else if (which == 2) {
                        String punishment = task.getPunishmentText();
                        message = (punishment != null && !punishment.isEmpty()) ?
                                "Наказание: " + punishment : "Задача провалена";
                    }

                    taskDao.updateTask(task);

                    if (message != null) {
                        new AlertDialog.Builder(TasksActivity.this)
                                .setTitle(which == 1 ? "Поощрение" : "Наказание")
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .show();
                    }

                    Toast.makeText(TasksActivity.this, "Статус обновлён", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void applyFilters() {
        List<Task> filtered = new ArrayList<>(allTasks);

        String query = editSearch.getText().toString().trim().toLowerCase();
        if (!query.isEmpty()) {
            List<Task> matching = new ArrayList<>();
            for (Task t : filtered) {
                if (t.getName().toLowerCase().contains(query) ||
                        (t.getDescription() != null && t.getDescription().toLowerCase().contains(query))) {
                    matching.add(t);
                }
            }
            filtered = matching;
        }

        switch (currentFilterPos) {
            case 0: break;
            case 1: filtered = filterByPriority(filtered, 2); break;
            case 2: filtered = filterByPriority(filtered, 1); break;
            case 3: filtered = filterByPriority(filtered, 0); break;
            case 4: filtered = filterByStatus(filtered, 0); break;
            case 5: filtered = filterByStatus(filtered, 1); break;
            case 6: filtered = filterByStatus(filtered, 2); break;
            case 7: filtered = filterByDeadline(filtered, true); break;
            case 8: filtered = filterByDeadline(filtered, false); break;
        }

        switch (currentSortPos) {
            case 0:
                Collections.sort(filtered, (t1, t2) -> Long.compare(t2.getCreatedAt(), t1.getCreatedAt()));
                break;
            case 1:
                Collections.sort(filtered, (t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));
                break;
            case 2:
                Collections.sort(filtered, (t1, t2) -> {
                    Long d1 = t1.getDeadline();
                    Long d2 = t2.getDeadline();
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return Long.compare(d1, d2);
                });
                break;
            case 3:
                Collections.sort(filtered, (t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()));
                break;
        }

        taskAdapter.setTasks(filtered);
    }

    private List<Task> filterByPriority(List<Task> tasks, int priority) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getPriority() == priority) result.add(t);
        }
        return result;
    }

    private List<Task> filterByStatus(List<Task> tasks, int status) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus() == status) result.add(t);
        }
        return result;
    }

    private List<Task> filterByDeadline(List<Task> tasks, boolean hasDeadline) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if ((t.getDeadline() != null) == hasDeadline) result.add(t);
        }
        return result;
    }
}