package com.example.kurstaskmanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String description;
    private Long deadline;
    private int priority;
    private String rewardText;
    private String punishmentText;
    private int status;
    private long createdAt;
    @TypeConverters(Converters.class)
    private List<Integer> blockedIds;

    public Task(String name, Long deadline, int priority, String description,
                String rewardText, String punishmentText, int status, long createdAt,
                List<Integer> blockedIds) {
        this.name = name;
        this.deadline = deadline;
        this.priority = priority;
        this.description = description;
        this.rewardText = rewardText;
        this.punishmentText = punishmentText;
        this.status = status;
        this.createdAt = createdAt;
        this.blockedIds = blockedIds;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getDeadline() { return deadline; }
    public void setDeadline(Long deadline) { this.deadline = deadline; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRewardText() { return rewardText; }
    public void setRewardText(String rewardText) { this.rewardText = rewardText; }
    public String getPunishmentText() { return punishmentText; }
    public void setPunishmentText(String punishmentText) { this.punishmentText = punishmentText; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public List<Integer> getBlockedIds() { return blockedIds; }
    public void setBlockedIds(List<Integer> blockedIds) { this.blockedIds = blockedIds; }
}