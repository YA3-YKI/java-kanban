import ru.yandex.javacourse.manager.FileBackedTaskManager;
import ru.yandex.javacourse.tasks.*;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        Path file = Path.of("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 10, 0));
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(120), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 12, 0));

        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1", Status.NEW,
                epic1.getId(), Duration.ofMinutes(60), LocalDateTime.of(2025, Month.SEPTEMBER, 13, 14, 0));
        manager.createSubtask(subtask1);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}