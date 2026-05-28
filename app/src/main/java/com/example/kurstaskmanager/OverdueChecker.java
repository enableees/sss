package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.Task;

import java.util.List;

public class OverdueChecker {

    public static boolean isOverdueAndShouldFail(Task task, long currentTimeMillis) {
        return task.getDeadline() != null
                && task.getDeadline() < currentTimeMillis
                && task.getStatus() != 1
                && task.getStatus() != 2;
    }
}