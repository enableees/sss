package com.example.kurstaskmanager;

import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationHelperTest {

    @Test
    public void channelId_notNull() {
        assertNotNull(NotificationHelper.CHANNEL_ID);
    }

    @Test
    public void channelId_notEmpty() {
        assertFalse(NotificationHelper.CHANNEL_ID.isEmpty());
    }

    @Test
    public void channelName_notEmpty() {
        assertFalse(NotificationHelper.CHANNEL_NAME.isEmpty());
    }
}