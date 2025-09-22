package ru.yandex.javacourse.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void updateStatus(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        boolean allNew = subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE);
        if (allNew) setStatus(Status.NEW);
        else if (allDone) setStatus(Status.DONE);
        else setStatus(Status.IN_PROGRESS);
    }

    public void updateTimeFromSubtasks(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStartTime(null);
            setEndTime(null);
            setDuration(null);
            return;
        }
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;
        Duration total = Duration.ZERO;
        boolean hasDuration = false;
        for (Subtask s : subtasks) {
            if (s.getStartTime() != null) {
                if (minStart == null || s.getStartTime().isBefore(minStart)) {
                    minStart = s.getStartTime();
                }
            }
            if (s.getEndTime() != null) {
                if (maxEnd == null || s.getEndTime().isAfter(maxEnd)) {
                    maxEnd = s.getEndTime();
                }
            }
            if (s.getDuration() != null) {
                hasDuration = true;
                total = total.plus(s.getDuration());
            }
        }
        setStartTime(minStart);
        setEndTime(maxEnd);
        setDuration(hasDuration ? total : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        String base = super.toString();
        return base;
    }
}