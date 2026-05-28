package com.example.kurstaskmanager;

import com.example.kurstaskmanager.data.Converters;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ConvertersTest {

    @Test
    public void fromList_normalList_returnsCommaSeparated() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        String result = Converters.fromList(ids);
        assertEquals("1,2,3", result);
    }

    @Test
    public void fromList_emptyList_returnsEmptyString() {
        String result = Converters.fromList(new ArrayList<>());
        assertEquals("", result);
    }

    @Test
    public void fromList_null_returnsEmptyString() {
        String result = Converters.fromList(null);
        assertEquals("", result);
    }

    @Test
    public void toList_validString_returnsList() {
        List<Long> result = Converters.toList("10,20,30");
        assertEquals(3, result.size());
        assertEquals((Long) 10L, result.get(0));
        assertEquals((Long) 20L, result.get(1));
        assertEquals((Long) 30L, result.get(2));
    }

    @Test
    public void toList_emptyString_returnsEmptyList() {
        List<Long> result = Converters.toList("");
        assertTrue(result.isEmpty());
    }
}