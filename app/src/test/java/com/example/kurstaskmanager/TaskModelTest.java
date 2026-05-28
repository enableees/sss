package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.Task;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TaskModelTest {

    @Test
    public void constructor_setsAllFields() {
        List<Long> blocked = new ArrayList<>();
        blocked.add(1L);
        Task task = new Task("Test", 123L, 2, "desc", "reward", "punishment", 0, 456L, blocked);

        assertEquals("Test", task.getName());
        assertEquals((Long) 123L, task.getDeadline());
        assertEquals(2, task.getPriority());
        assertEquals("desc", task.getDescription());
        assertEquals("reward", task.getRewardText());
        assertEquals("punishment", task.getPunishmentText());
        assertEquals(0, task.getStatus());
        assertEquals(456L, task.getCreatedAt());
        assertEquals(blocked, task.getBlockedIds());
    }

    @Test
    public void setters_updateValuesCorrectly() {
        Task task = new Task("Old", null, 0, "", "", "", 1, 0, new ArrayList<>());
        task.setName("New");
        task.setStatus(2);
        assertEquals("New", task.getName());
        assertEquals(2, task.getStatus());
    }

    @Test
    public void blockedIds_initialization_withEmptyList() {
        Task task = new Task("t", null, 0, "", "", "", 0, 0, new ArrayList<>());
        assertNotNull(task.getBlockedIds());
        assertTrue(task.getBlockedIds().isEmpty());
    }
}