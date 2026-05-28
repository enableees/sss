package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.BlockedItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockedItemTest {

    @Test
    public void constructor_setsFields() {
        BlockedItem item = new BlockedItem(0, "TestApp", true);
        assertEquals(0, item.getType());
        assertEquals("TestApp", item.getName());
        assertTrue(item.isActive());
    }

    @Test
    public void setters_updateValues() {
        BlockedItem item = new BlockedItem(1, "Old", false);
        item.setName("New");
        item.setActive(true);
        assertEquals("New", item.getName());
        assertTrue(item.isActive());
    }
}