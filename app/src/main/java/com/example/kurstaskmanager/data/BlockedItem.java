package com.example.kurstaskmanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "blocked_item_table")
public class BlockedItem {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int type;
    private String name;
    private boolean isActive;

    public BlockedItem(int type, String name, boolean isActive) {
        this.type = type;
        this.name = name;
        this.isActive = isActive;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}