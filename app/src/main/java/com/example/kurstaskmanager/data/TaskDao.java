package com.example.kurstaskmanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insertTask(Task task);
    @Update
    void updateTask(Task task);
    @Query("DELETE FROM task_table WHERE id = :taskId")
    void deleteTask(long taskId);
    @Query("SELECT * FROM task_table ORDER BY createdAt DESC")
    LiveData<List<Task>> getAllTasks();
    @Query("SELECT * FROM task_table")
    List<Task> getAllTasksSync();
}