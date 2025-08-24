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

    private void linkLast(Task task) {
        Node node = new Node(task, last, null);

        if (last == null) {
            first = node;
        } else {
            last.next = node;
            node.prev = last;
        }

        last = node;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> temp = new ArrayList<>();

        Node current = first;
        while (current != null) {
            temp.add(current.task);
            current = current.next;
        }
        return temp;
    }

    private void removeNode(int id) {
        final Node node = nodeMap.remove(id);

        if (node == null) return;

        if (node == first && node == last) { // единственный элемент
            first = null;
            last = null;
            return;
        }

        if (node == first) {
            first = node.next;
            first.prev = null;
        } else if (node == last) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        final int id = task.getId();
        if (nodeMap.containsKey(id)) {
            removeNode(id);
        }
        linkLast(task);
        nodeMap.put(id, last);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public Map<Integer, Node> getNodeMap() {
        return nodeMap;
    }
}