package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final ArrayList<Task> historyLastTasks = new ArrayList<>();

    @Override
    public void add(Task task) {

        if (historyLastTasks.size() >= MAX_HISTORY_SIZE) {
            historyLastTasks.removeFirst();
        }

        Task tempTask = createCopyTask(task);
        historyLastTasks.addLast(tempTask);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyLastTasks);
    }

    private Task createCopyTask(Task original) {
        if (original instanceof Epic) {
            Epic epic = (Epic) original;
            return new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus());
        } else if (original instanceof Subtask) {
            Subtask subtask = (Subtask) original;
            return new Subtask(
                    subtask.getId(),
                    subtask.getTitle(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicId()
            );
        } else {
            return new Task(
                    original.getId(),
                    original.getTitle(),
                    original.getDescription(),
                    original.getStatus()
            );
        }
    }
}