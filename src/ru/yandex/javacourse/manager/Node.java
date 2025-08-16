package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    public Node(Task task, Node prev, Node next){
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node{" +
                "task=" + task +
                ", prev=" + (prev == null ? null:prev.task) +
                ", next=" + (next == null ? null:next.task) +
                '}';
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }
}
