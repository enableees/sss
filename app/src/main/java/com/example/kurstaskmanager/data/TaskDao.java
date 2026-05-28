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
    @Query("SELECT * FROM task_table WHERE id = :id")
    LiveData<Task> getTaskById(long id);
    @Query("SELECT COUNT(*) FROM task_table WHERE status = 1 AND createdAt BETWEEN :start AND :end")
    LiveData<Integer> getCompletedCount(long start, long end);
    @Query("SELECT COUNT(*) FROM task_table WHERE createdAt BETWEEN :start AND :end")
    LiveData<Integer> getTotalCount(long start, long end);
}