package ru.yandex.javacourse.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private final String title;
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
        this.endTime = (duration != null && startTime != null) ? startTime.plus(duration) : null;
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

    public String getDescription() {
        return description;
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

    private void recalcEndTime() {
        if (duration != null && startTime != null) {
            this.endTime = startTime.plus(duration);
        } else {
            this.endTime = null;
        }
    }

    public String toCsv() {
        return String.join(",",
                String.valueOf(id),
                "TASK",
                title,
                status.name(),
                description,
                duration != null ? String.valueOf(duration.toMinutes()) : "",
                startTime != null ? startTime.format(FORMATTER) : "",
                endTime != null ? endTime.format(FORMATTER) : "",
                ""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public void setDescription(String updated) {
        this.description = updated;
    }
}
