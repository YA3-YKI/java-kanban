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
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(Task::getId)
    );

    protected int idCounter = 1;

    protected int generateId() {
        return idCounter++;
    }

    protected void adjustNextId(int id) {
        if (id >= idCounter) idCounter = id + 1;
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() <= 0) task.setId(generateId());
        else adjustNextId(task.getId());
        if (task.getStartTime() != null && !intersectsWithAny(task)) prioritizedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() <= 0) epic.setId(generateId());
        else adjustNextId(epic.getId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic not found");
        }
        if (subtask.getId() <= 0) subtask.setId(generateId());
        else adjustNextId(subtask.getId());
        if (subtask.getStartTime() != null && !intersectsWithAny(subtask)) prioritizedTasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
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
        Subtask sub = subtasks.get(id);
        if (sub != null) historyManager.add(sub);
        return sub;
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
        if (!tasks.containsKey(task.getId())) return;
        prioritizedTasks.removeIf(t -> t.getId() == task.getId());
        if (task.getStartTime() != null && !intersectsWithAny(task)) prioritizedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return;
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) return;
        prioritizedTasks.removeIf(t -> t.getId() == subtask.getId());
        if (subtask.getStartTime() != null && !intersectsWithAny(subtask)) prioritizedTasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicTimeAndStatus(subtask.getEpicId());
    }

    @Override
    public void updateTaskStatus(int id, Status status) {
        Task task = tasks.get(id);
        if (task != null) task.setStatus(status);
    }

    @Override
    public void updateSubtaskStatus(int id, Status status) {
        Subtask s = subtasks.get(id);
        if (s != null) {
            s.setStatus(status);
            updateEpicTimeAndStatus(s.getEpicId());
        }
    }

    @Override
    public void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return;
        List<Subtask> epicSubs = subtasks.values().stream()
                .filter(s -> s.getEpicId() == id)
                .collect(Collectors.toList());
        epic.updateStatus(epicSubs);
    }

    @Override
    public void deleteTask(Task task) {
        tasks.remove(task.getId());
        prioritizedTasks.removeIf(t -> t.getId() == task.getId());
        historyManager.remove(task.getId());
    }

    @Override
    public void deleteEpic(Epic epic) {
        if (epic == null) return; // эпик не найден — ничего не делаем

        Collection<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds != null) {
            for (Integer sid : new ArrayList<>(subtaskIds)) {
                Subtask st = subtasks.remove(sid);
                prioritizedTasks.removeIf(t -> t.getId() == sid);
                historyManager.remove(sid);
            }
        }

        epics.remove(epic.getId());
        historyManager.remove(epic.getId());
    }

    @Override
    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        prioritizedTasks.removeIf(t -> t.getId() == subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) epic.removeSubtaskId(subtask.getId());
        historyManager.remove(subtask.getId());
        updateEpicTimeAndStatus(subtask.getEpicId());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : new ArrayList<>(tasks.keySet())) {
            historyManager.remove(id);
        }
        tasks.clear();
        prioritizedTasks.removeIf(t -> !(t instanceof Subtask));
    }

    @Override
    public void deleteAllEpics() {
        for (Epic e : new ArrayList<>(epics.values())) {
            deleteEpic(e);
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : new ArrayList<>(subtasks.keySet())) {
            historyManager.remove(id);
        }
        for (Epic e : epics.values()) {
            e.getSubtaskIds().clear();
        }
        subtasks.clear();
        prioritizedTasks.removeIf(t -> t instanceof Subtask);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isIntersecting(Task t1, Task t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.getStartTime() == null || t1.getEndTime() == null) return false;
        if (t2.getStartTime() == null || t2.getEndTime() == null) return false;
        return t1.getStartTime().isBefore(t2.getEndTime()) && t2.getStartTime().isBefore(t1.getEndTime());
    }

    protected boolean intersectsWithAny(Task task) {
        return prioritizedTasks.stream()
                .anyMatch(t -> t.getId() != task.getId() && isIntersecting(t, task));
    }

    protected void updateEpicTimeAndStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;
        List<Subtask> epicSubs = subtasks.values().stream()
                .filter(s -> s.getEpicId() == epicId)
                .collect(Collectors.toList());
        epic.updateStatus(epicSubs);
        epic.updateTimeFromSubtasks(epicSubs);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = getEpicById(id);
        return epic.getSubtaskIds().stream()
                .map(this::getSubtaskById)
                .toList();
    }
}