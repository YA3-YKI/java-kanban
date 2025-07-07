package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    public static int id = 0;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Subtask> subtasks;
    public HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    public static int generateId(){
        return id++;
    }

    public void addTask(Task task){
        int newId = generateId();
        task.setId(newId);

        if (task.getClass().equals(Epic.class)){
            epics.put(newId,(Epic) task);
        } else if(task.getClass().equals(Subtask.class)){
            subtasks.put(newId,(Subtask) task);
        } else {
            tasks.put(newId,task);
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllTasks(){
        tasks.clear();
    }

    public void deleteAllSubtasks(){
        for (Epic epic : epics.values()){
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }

        subtasks.clear();
    }

    public void deleteAllEpics(){
        for (Epic epic : epics.values()){
            for (Integer subtaskId : epic.getSubtaskIds()){
                subtasks.remove(subtaskId);
            }
        }

        epics.clear();
    }

    public Task getTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public void createTask(Task newTask){
        int newId = generateId();
        newTask.setId(newId);
        tasks.put(generateId(),newTask);
    }

    public void createSubtask(Subtask newSubtask){
        int newId = generateId();
        newSubtask.setId(newId);

        subtasks.put(newId,newSubtask);

        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic != null){
            epic.getSubtaskIds().add(newId);
        } else {
            System.out.println("Ошибка: ru.yandex.javacourse.tasks.Epic с таким ID "+ newSubtask.getEpicId() + " не найден");
        }
    }

    public void createEpic(Epic newEpic){
        int newId = generateId();
        newEpic.setId(newId);
        epics.put(newId,newEpic);
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }

    public void deleteTask(Task task){
        tasks.remove(task.getId());
    }

    public void deleteSubtask(Subtask subtask){
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());

        if (epic != null) {
            epic.getSubtaskIds().remove(Integer.valueOf(subtask.getId()));
            updateEpicStatus(epic.getId());
        }

    }

    public void deleteEpic(Epic epic){
        for (Integer subtaskId : epic.getSubtaskIds()){
            subtasks.remove(subtaskId);
        }

        epics.remove(epic.getId());
    }

    public void updateTaskStatus(Integer id, Status status){
        Task task = tasks.get(id);
        if (task != null) {
            task.setStatus(status);
        } else {
            System.out.println("Задача с id " + id + " не найдена");
        }
    }

    public void updateSubtaskStatus(Integer id, Status status){
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtask.setStatus(status);
            updateEpicStatus(subtask.getEpicId());
        } else {
            System.out.println("Подзадача с id " + id + " не найдена");
        }
    }

    public void updateEpicStatus(Integer id){
        Epic epic = epics.get(id);

        if (epic != null) {
            int counterDone = 0;
            int counterNew = 0;

            if (epic.getSubtaskIds().isEmpty()) {
                epics.get(id).setStatus(Status.NEW);
            } else {
                for (Integer subtaskId : epic.getSubtaskIds()) {
                    if (subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                        counterDone++;
                    } else if (subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                        counterNew++;
                    }
                }
                if (counterDone == epic.getSubtaskIds().size()) {
                    epics.get(id).setStatus(Status.DONE);
                } else if (counterNew == epic.getSubtaskIds().size()) {
                    epics.get(id).setStatus(Status.NEW);
                } else {
                    epics.get(id).setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Эпик с id " + id + " не найден");
        }
    }
}