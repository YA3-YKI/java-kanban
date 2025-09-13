import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 10, 0));
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(120), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 12, 0));

        if (!manager.isIntersecting(task1)) manager.addTask(task1);
        if (!manager.isIntersecting(task2)) manager.addTask(task2);

        System.out.println("История после добавления задач:");
        System.out.println(manager.getHistory());

        // Повторное добавление для проверки истории
        if (!manager.isIntersecting(task1)) manager.addTask(task1);
        if (!manager.isIntersecting(task2)) manager.addTask(task2);

        System.out.println("История после повторного добавления задач:");
        System.out.println(manager.getHistory());

        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 14, 0));
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 16, 0));

        if (!manager.isIntersecting(subtask1)) manager.createSubtask(subtask1);
        if (!manager.isIntersecting(subtask2)) manager.createSubtask(subtask2);

        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask(0, "Подзадача 3", "Описание подзадачи 3",
                Status.NEW, epic2.getId(),
                Duration.ofMinutes(45), LocalDateTime.of(2025, Month.SEPTEMBER, 14, 10, 0));
        if (!manager.isIntersecting(subtask3)) manager.createSubtask(subtask3);

        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task + " | EndTime: " + task.getEndTime());
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask + " | EndTime: " + subtask.getEndTime());
        }

        System.out.println("\nОбновляем статусы:");
        manager.updateTaskStatus(task1.getId(), Status.DONE.toString());
        manager.updateSubtaskStatus(subtask1.getId(), Status.DONE.toString());
        manager.updateSubtaskStatus(subtask2.getId(), Status.IN_PROGRESS.toString());
        manager.updateSubtaskStatus(subtask3.getId(), Status.DONE.toString());

        System.out.println("Задача 1: " + manager.getTaskById(task1.getId()));
        System.out.println("Подзадача 1: " + manager.getSubtaskById(subtask1.getId()));
        System.out.println("Подзадача 2: " + manager.getSubtaskById(subtask2.getId()));
        System.out.println("Подзадача 3: " + manager.getSubtaskById(subtask3.getId()));

        System.out.println("Эпик 1: " + manager.getEpicById(epic1.getId()));
        System.out.println("Эпик 2: " + manager.getEpicById(epic2.getId()));

        System.out.println("\nИстория:");
        System.out.println(manager.getHistory());

        System.out.println("\nУдаляем задачу и эпик:");
        manager.deleteTask(task2);
        manager.deleteEpic(epic1);

        System.out.println("Задачи после удаления:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики после удаления:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("Подзадачи после удаления:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}