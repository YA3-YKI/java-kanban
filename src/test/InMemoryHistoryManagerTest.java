package test;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void addNewVersion() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTask(task1);

        task1.setDescription("Новое описание задачи");
        manager.addTask(task1);

        Task fromHistory = manager.getHistory().get(0);

        assertEquals("Описание задачи 1", fromHistory.getDescription(),
                "История должна хранить первоначальное значение");
    }

    @Test
    public void checkFirstElementInHistory () {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTask(task1);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Status.NEW);
        manager.addTask(task2);
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3", Status.NEW);
        manager.addTask(task3);
        Task task4 = new Task(4, "Задача 4", "Описание задачи 4", Status.NEW);
        manager.addTask(task4);
        Task task5 = new Task(5, "Задача 5", "Описание задачи 5", Status.NEW);
        manager.addTask(task5);
        Task task6 = new Task(6, "Задача 6", "Описание задачи 6", Status.NEW);
        manager.addTask(task6);
        Task task7 = new Task(7, "Задача 7", "Описание задачи 7", Status.NEW);
        manager.addTask(task7);
        Task task8 = new Task(8, "Задача 8", "Описание задачи 8", Status.NEW);
        manager.addTask(task8);
        Task task9 = new Task(9, "Задача 9", "Описание задачи 9", Status.NEW);
        manager.addTask(task9);
        Task task10 = new Task(10, "Задача 10", "Описание задачи 10", Status.NEW);
        manager.addTask(task10);
        Task task11 = new Task(11, "Задача 11", "Описание задачи 11", Status.NEW);
        manager.addTask(task11);

        assertEquals(task2,manager.getHistory().getFirst(),"После добавления 11 задач," +
                " 2 строка должна быть первой в истории");

    }
}