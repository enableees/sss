package com.example.kurstaskmanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BlockedItemDao {
    @Insert
    long insertBlockedItem(BlockedItem item);

    @Query("DELETE FROM blocked_item_table WHERE id = :id")
    void deleteBlockedItem(long id);

    @Query("SELECT * FROM blocked_item_table ORDER BY id ASC")
    LiveData<List<BlockedItem>> getAllBlockedItems();

    @Query("UPDATE blocked_item_table SET isActive = :active WHERE id = :id")
    void setActive(long id, boolean active);
}