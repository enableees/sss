package com.example.kurstaskmanager.data;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<Long> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<Long> toList(String value) {
        if (value == null || value.trim().isEmpty()) return new ArrayList<>();
        List<Long> list = new ArrayList<>();
        for (String s : value.split(",")) {
            try {
                list.add(Long.parseLong(s.trim()));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }
}