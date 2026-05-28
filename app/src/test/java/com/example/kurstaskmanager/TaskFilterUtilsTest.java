package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.Task;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TaskFilterUtilsTest {

    private Task createTask(long id, int priority, int status, Long deadline) {
        return new Task("Task " + id, deadline, priority, "desc", "", "",
                status, System.currentTimeMillis(), new ArrayList<>());
    }

    @Test
    public void filterByPriority_filtersCorrectly() {
        List<Task> tasks = Arrays.asList(
                createTask(1, 0, 0, null),
                createTask(2, 2, 1, null),
                createTask(3, 0, 2, null)
        );
        List<Task> result = TaskFilterUtils.filterByPriority(tasks, 0);
        assertEquals(2, result.size());
    }

    @Test
    public void filterByStatus_filtersCorrectly() {
        List<Task> tasks = Arrays.asList(
                createTask(1, 1, 0, null),
                createTask(2, 1, 1, null),
                createTask(3, 1, 1, null)
        );
        List<Task> result = TaskFilterUtils.filterByStatus(tasks, 1);
        assertEquals(2, result.size());
    }

    @Test
    public void filterByDeadline_hasDeadline_returnsOnlyTasksWithDeadline() {
        List<Task> tasks = Arrays.asList(
                createTask(1, 0, 0, 1000L),
                createTask(2, 0, 0, null)
        );
        List<Task> result = TaskFilterUtils.filterByDeadline(tasks, true);
        assertEquals(1, result.size());
    }

    @Test
    public void filterByDeadline_noDeadline_returnsOnlyTasksWithoutDeadline() {
        List<Task> tasks = Arrays.asList(
                createTask(1, 0, 0, 1000L),
                createTask(2, 0, 0, null)
        );
        List<Task> result = TaskFilterUtils.filterByDeadline(tasks, false);
        assertEquals(1, result.size());
        assertNull(result.get(0).getDeadline());
    }

    @Test
    public void filterByPriority_emptyList_returnsEmpty() {
        List<Task> result = TaskFilterUtils.filterByPriority(new ArrayList<>(), 1);
        assertTrue(result.isEmpty());
    }
}