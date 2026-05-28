package com.example.kurstaskmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class BlockedAppsActivity extends AppCompatActivity {

    private BlockedItem selectedBlockedItem = null;
    private Button btnDeleteBlocked;
    private BlockedAdapter blockedAdapter;
    private BlockedItemDao blockedItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blocked_apps);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_blocked);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_tasks) {
                startActivity(new Intent(BlockedAppsActivity.this, TasksActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.nav_blocked) {
                return true;
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(BlockedAppsActivity.this, StatisticsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        AppDatabase db = AppDatabase.getInstance(this);
        blockedItemDao = db.blockedItemDao();

        RecyclerView recyclerBlocked = findViewById(R.id.recyclerBlocked);
        recyclerBlocked.setLayoutManager(new LinearLayoutManager(this));
        blockedAdapter = new BlockedAdapter();
        recyclerBlocked.setAdapter(blockedAdapter);

        Button btnAddBlocked = findViewById(R.id.btnAddBlocked);
        btnDeleteBlocked = findViewById(R.id.btnDeleteBlocked);
        btnDeleteBlocked.setEnabled(false);

        blockedAdapter.setOnBlockedClickListener(item -> showBlockedDetailDialog(item));

        blockedAdapter.setOnBlockedLongClickListener(item -> {
            selectedBlockedItem = item;
            btnDeleteBlocked.setEnabled(true);
        });

        blockedItemDao.getAllBlockedItems().observe(this, items -> {
            blockedAdapter.setItems(items);
        });

        btnAddBlocked.setOnClickListener(v -> showAddBlockedDialog());

        btnDeleteBlocked.setOnClickListener(v -> {
            if (selectedBlockedItem == null) return;
            new AlertDialog.Builder(this)
                    .setTitle("Удаление")
                    .setMessage("Удалить блокировку \"" + selectedBlockedItem.getName() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        blockedItemDao.deleteBlockedItem(selectedBlockedItem.getId());
                        blockedAdapter.clearSelection();
                        selectedBlockedItem = null;
                        btnDeleteBlocked.setEnabled(false);
                        Toast.makeText(BlockedAppsActivity.this, "Блокировка удалена", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void showAddBlockedDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_blocked, null);
        EditText editName = dialogView.findViewById(R.id.editBlockedName);
        RadioGroup radioType = dialogView.findViewById(R.id.radioType);

        new AlertDialog.Builder(this)
                .setTitle("Новая блокировка")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(BlockedAppsActivity.this, "Введите название", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int type = radioType.getCheckedRadioButtonId() == R.id.radioApp ? 0 : 1;
                    BlockedItem item = new BlockedItem(type, name, true);
                    blockedItemDao.insertBlockedItem(item);
                    Toast.makeText(BlockedAppsActivity.this, "Добавлено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showBlockedDetailDialog(BlockedItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getName());
        String typeStr = item.getType() == 0 ? "Приложение" : "Сайт";
        String statusStr = item.isActive() ? "Активна" : "Неактивна";
        String message = "Тип: " + typeStr + "\nСтатус: " + statusStr;
        builder.setMessage(message);

        builder.setNeutralButton("Переключить статус", (dialog, which) -> {
            item.setActive(!item.isActive());
            blockedItemDao.setActive(item.getId(), item.isActive());
            Toast.makeText(BlockedAppsActivity.this, "Статус изменён", Toast.LENGTH_SHORT).show();
        });

        builder.setPositiveButton("Удалить", (dialog, which) -> {
            blockedItemDao.deleteBlockedItem(item.getId());
            Toast.makeText(BlockedAppsActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Закрыть", null);
        builder.show();
    }
}