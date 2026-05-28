package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.Task;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class OverdueCheckerTest {

    @Test
    public void isOverdue_deadlinePassedAndStatusInProgress_returnsTrue() {
        Task task = new Task("t", 1000L, 1, "", "", "", 0, 0, new ArrayList<>());
        assertTrue(OverdueChecker.isOverdueAndShouldFail(task, 2000L));
    }

    @Test
    public void isOverdue_deadlinePassedButAlreadyCompleted_returnsFalse() {
        Task task = new Task("t", 1000L, 1, "", "", "", 1, 0, new ArrayList<>());
        assertFalse(OverdueChecker.isOverdueAndShouldFail(task, 2000L));
    }

    @Test
    public void isOverdue_noDeadline_returnsFalse() {
        Task task = new Task("t", null, 1, "", "", "", 0, 0, new ArrayList<>());
        assertFalse(OverdueChecker.isOverdueAndShouldFail(task, 2000L));
    }

    @Test
    public void isOverdue_deadlineInFuture_returnsFalse() {
        Task task = new Task("t", 3000L, 1, "", "", "", 0, 0, new ArrayList<>());
        assertFalse(OverdueChecker.isOverdueAndShouldFail(task, 2000L));
    }
}