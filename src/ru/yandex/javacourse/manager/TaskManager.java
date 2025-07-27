package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(Integer id);

    void createTask(Task newTask);

    void createSubtask(Subtask newSubtask);

    void createEpic(Epic newEpic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(Task task);

    void deleteSubtask(Subtask subtask);

    void deleteEpic(Epic epic);

    void updateTaskStatus(Integer id, Status status);

    void updateSubtaskStatus(Integer id, Status status);

    void updateEpicStatus(Integer id);

    ArrayList<Task> getHistory();

}
