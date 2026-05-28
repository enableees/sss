package com.example.kurstaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kurstaskmanager.data.AppDatabase;
import com.example.kurstaskmanager.data.Task;
import com.example.kurstaskmanager.data.TaskDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvProductivityPercent;
    private SimpleChartView chartView;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_stats);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_tasks) {
                startActivity(new Intent(StatisticsActivity.this, TasksActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.nav_blocked) {
                startActivity(new Intent(StatisticsActivity.this, BlockedAppsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.nav_stats) {
                return true;
            }
            return false;
        });

        AppDatabase db = AppDatabase.getInstance(this);
        taskDao = db.taskDao();

        tvProductivityPercent = findViewById(R.id.tvProductivityPercent);
        chartView = findViewById(R.id.chartView);

        loadStatistics();
    }

    private void loadStatistics() {
        List<Task> allTasks = taskDao.getAllTasksSync();
        int total = allTasks.size();
        int completed = 0;
        for (Task t : allTasks) {
            if (t.getStatus() == 1) completed++;
        }

        int percent = total == 0 ? 0 : (completed * 100) / total;
        tvProductivityPercent.setText(percent + " %");

        chartView.setData(completed, total - completed);
    }
}