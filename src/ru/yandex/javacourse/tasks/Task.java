package ru.yandex.javacourse.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private String title;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(int id, String title, String description, Status status) {
        this(id, title, description, status, null, null);
    }

    public Task(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        recalcEndTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        recalcEndTime();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        recalcEndTime();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private void recalcEndTime() {
        if (this.startTime != null && this.duration != null) {
            this.endTime = this.startTime.plus(this.duration);
        } else {
            this.endTime = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }

    @Override
    public String toString() {
        String dur = (duration != null) ? String.valueOf(duration.toMinutes()) : "";
        String st = (startTime != null) ? startTime.format(FORMATTER) : "";
        String et = (endTime != null) ? endTime.format(FORMATTER) : "";
        String type = this.getClass().getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(type).append(",")
                .append(escapeCsv(title)).append(",")
                .append(status != null ? status : "").append(",")
                .append(escapeCsv(description)).append(",")
                .append(dur).append(",")
                .append(st).append(",")
                .append(et).append(",");
        return sb.toString();
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ");
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", -1);
        String type = parts[1].trim().toUpperCase();
        int id = Integer.parseInt(parts[0].trim());
        String title = parts.length > 2 ? parts[2] : "";
        Status status = parts.length > 3 && !parts[3].isEmpty() ? Status.parse(parts[3]) : Status.NEW;
        String description = parts.length > 4 ? parts[4] : "";
        Duration duration = null;
        if (parts.length > 5 && !parts[5].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        }
        LocalDateTime start = null;
        if (parts.length > 6 && !parts[6].isEmpty()) {
            start = LocalDateTime.parse(parts[6], FORMATTER);
        }
        LocalDateTime end = null;
        if (parts.length > 7 && !parts[7].isEmpty()) {
            end = LocalDateTime.parse(parts[7], FORMATTER);
        }
        switch (type) {
            case "TASK":
                Task task = new Task(id, title, description, status, duration, start);
                task.setEndTime(end);
                return task;
            case "EPIC":
                Epic epic = new Epic(id, title, description, status);
                epic.setDuration(duration);
                epic.setStartTime(start);
                epic.setEndTime(end);
                return epic;
            case "SUBTASK":
                int epicId = parts.length > 8 && !parts[8].isEmpty() ? Integer.parseInt(parts[8]) : -1;
                Subtask subtask = new Subtask(id, title, description, status, epicId, duration, start);
                subtask.setEndTime(end);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}