package ru.yandex.javacourse.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public String toCsv() {
        return String.join(",",
                String.valueOf(getId()),
                "SUBTASK",
                getTitle(),
                getStatus().name(),
                getDescription(),
                getDuration() != null ? String.valueOf(getDuration().toMinutes()) : "",
                getStartTime() != null ? getStartTime().format(FORMATTER) : "",
                getEndTime() != null ? getEndTime().format(FORMATTER) : "",
                String.valueOf(epicId)
        );
    }
}
