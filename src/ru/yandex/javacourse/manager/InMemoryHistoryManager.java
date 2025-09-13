package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) return;
        final int id = task.getId();
        if (nodeMap.containsKey(id)) removeNode(id);
        linkLast(task);
        nodeMap.put(id, last);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = first;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public Map<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    private void linkLast(Task task) {
        Node node = new Node(task, last, null);
        if (last == null) first = node;
        else last.next = node;
        if (last != null) last.next = node;
        node.prev = last;
        last = node;
    }

    private void removeNode(int id) {
        Node node = nodeMap.remove(id);
        if (node == null) return;

        if (node.prev != null) node.prev.next = node.next;
        else first = node.next;

        if (node.next != null) node.next.prev = node.prev;
        else last = node.prev;
    }
}