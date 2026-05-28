package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskFilterUtils {

    public static List<Task> filterByPriority(List<Task> tasks, int priority) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getPriority() == priority) result.add(t);
        }
        return result;
    }

    public static List<Task> filterByStatus(List<Task> tasks, int status) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus() == status) result.add(t);
        }
        return result;
    }

    public static List<Task> filterByDeadline(List<Task> tasks, boolean hasDeadline) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if ((t.getDeadline() != null) == hasDeadline) result.add(t);
        }
        return result;
    }
}