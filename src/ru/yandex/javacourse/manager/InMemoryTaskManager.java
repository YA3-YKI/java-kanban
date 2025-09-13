package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
            .thenComparing(Task::getId));

    protected int idCounter = 0;

    @Override
    public void addTask(Task task) {
        if (task.getId() == 0) task.setId(generateId());
        if (task.getStartTime() != null && !isIntersecting(task)) prioritizedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic с таким id не найден");
        }
        if (subtask.getId() == 0) subtask.setId(generateId());
        if (subtask.getStartTime() != null && !isIntersecting(subtask)) prioritizedTasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicTimeAndStatus(subtask.getEpicId());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicTimeAndStatus(subtask.getEpicId());
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);
    }

    @Override
    public void updateTaskStatus(int id, String status) {
        Task task = tasks.get(id);
        if (task != null) task.setStatus(Status.parse(status));
    }

    @Override
    public void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return;
        List<Subtask> epicSubtasks = subtasks.values().stream()
                .filter(s -> s.getEpicId() == id)
                .collect(Collectors.toList());
        epic.updateStatus(epicSubtasks);
    }

    @Override
    public void updateSubtaskStatus(int id, String status) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtask.setStatus(Status.parse(status));
            updateEpicTimeAndStatus(subtask.getEpicId());
        }
    }

    @Override
    public void deleteTask(Task task) {
        tasks.remove(task.getId());
        prioritizedTasks.remove(task);
        historyManager.remove(task.getId());
    }

    @Override
    public void deleteEpic(Epic epic) {
        List<Integer> subtaskIds = subtasks.values().stream()
                .filter(s -> s.getEpicId() == epic.getId())
                .map(Subtask::getId)
                .toList();
        subtaskIds.forEach(subtasks::remove);
        prioritizedTasks.removeIf(t -> t instanceof Subtask && ((Subtask) t).getEpicId() == epic.getId());
        epics.remove(epic.getId());
        historyManager.remove(epic.getId());
    }

    @Override
    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        prioritizedTasks.remove(subtask);
        historyManager.remove(subtask.getId());
        updateEpicTimeAndStatus(subtask.getEpicId());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.removeIf(t -> t instanceof Task);
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        prioritizedTasks.removeIf(t -> t instanceof Subtask);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isIntersecting(Task task) {
        for (Task t : prioritizedTasks) {
            if (isIntersecting(task, t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isIntersecting(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) return false;
        return t1.getStartTime().isBefore(t2.getEndTime()) && t2.getStartTime().isBefore(t1.getEndTime());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void updateEpicTimeAndStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = subtasks.values().stream()
                .filter(s -> s.getEpicId() == epicId)
                .collect(Collectors.toList());
        epic.updateStatus(epicSubtasks);
        epic.updateTimeFromSubtasks(epicSubtasks);
    }

    protected int generateId() {
        return ++idCounter;
    }
}