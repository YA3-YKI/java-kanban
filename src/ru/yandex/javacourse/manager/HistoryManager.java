package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

    Map<Integer, Node> getNodeMap();
}