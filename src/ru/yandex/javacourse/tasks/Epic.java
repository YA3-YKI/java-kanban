package ru.yandex.javacourse.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateStatus(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }
        boolean allNew = subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE);

        if (allNew) {
            this.setStatus(Status.NEW);
        } else if (allDone) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateTimeFromSubtasks(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.duration = null;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        LocalDateTime start = subtasks.stream()
                .filter(s -> s.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = subtasks.stream()
                .filter(s -> s.getEndTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        this.startTime = start;
        this.endTime = end;
        this.duration = (start != null && end != null) ? Duration.between(start, end) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(duration, epic.duration) &&
                Objects.equals(startTime, epic.startTime) &&
                Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public String toCsv() {
        return String.join(",",
                String.valueOf(getId()),
                "EPIC",
                getTitle(),
                getStatus().name(),
                getDescription(),
                duration != null ? String.valueOf(duration.toMinutes()) : "",
                startTime != null ? startTime.format(FORMATTER) : "",
                endTime != null ? endTime.format(FORMATTER) : "",
                ""
        );
    }
}
