package test;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

public class InMemoryTaskManagerTest {

    @Test
    public void checkEqualsTask() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW);

        assertEquals(task1, task2, "Тест не пройдет - экземпляры не соответствуют");
    }

    @Test
    public void checkNotEqualsTask() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", Status.NEW);

        assertNotEquals(task1, task2, "Тест не пройдет - экземпляры соответствуют");
    }

    @Test
    public void checkEqualsSubtask() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addTask(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1"
                , Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2"
                , Status.NEW, epic1.getId());

        assertEquals(subtask1, subtask2, "Тест не пройдет - экземпляры не соответствуют");
    }

    @Test
    public void checkNotEqualsSubtask() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addTask(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1"
                , Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(1, "Подзадача 2", "Описание подзадачи 2"
                , Status.NEW, epic1.getId());

        assertNotEquals(subtask1, subtask2, "Тест не пройдет - экземпляры соответствуют");
    }

    @Test
    public void checkEqualsEpic() {

        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);

        assertEquals(epic1, epic2, "Тест не пройдет - экземпляры не соответствуют");
    }

    @Test
    public void checkNotEqualsEpic() {

        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Epic epic2 = new Epic(1, "Эпик 2", "Описание эпика 2", Status.NEW);

        assertNotEquals(epic1, epic2, "Тест не пройдет - экземпляры соответствуют");
    }

    @Test
    public void checkEqualsAll() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId());

        assertAll(
                () -> assertNotEquals(task, epic, "Task и Epic с одинаковым ID не должны быть равны"),
                () -> assertNotEquals(task, subtask, "Task и Subtask с одинаковым ID не должны быть равны"),
                () -> assertNotEquals(epic, subtask, "Epic и Subtask с одинаковым ID не должны быть равны")
        );
    }

    @Test
    public void searchForId() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addTask(epic);

        Subtask subtask = new Subtask(1, "Подзадача 1", "Описание подзадачи 1"
                , Status.NEW, epic.getId());
        manager.addTask(subtask);

        Task task = new Task(2, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTask(task);


        assertAll(
                () -> assertEquals("Эпик 1", manager.getTaskById(0).getTitle(),
                        "Полe title не соответствует полю экземпляра полученному по ID"),
                () -> assertEquals("Подзадача 1", manager.getTaskById(1).getTitle(),
                        "Полe title не соответствует полю экземпляра полученному по ID"),
                () -> assertEquals("Задача 1", manager.getTaskById(2).getTitle(),
                        "Полe title не соответствует полю экземпляра полученному по ID")
        );

    }

    @Test
    public void checkConflictId() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW);
        manager.addTask(task1);
        manager.createTask(task2);

        assertNotEquals(manager.getTaskById(0), manager.getTaskById(task2.getId()), "Конфликтуют ID");
    }

    @Test
    public void checkAllFieldsEquals() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTask(task1);

        assertAll(
                () -> assertEquals(0, manager.getTaskById(0).getId(),
                        "Поля ID отличаются"),
                () -> assertEquals("Задача 1", manager.getTaskById(0).getTitle(),
                        "Поля Title отличаются"),
                () -> assertEquals("Описание задачи 1", manager.getTaskById(0).getDescription(),
                        "Поля Description отличаются"),
                () -> assertEquals(Status.NEW,manager.getTaskById(0).getStatus(),
                        "Поля Status отличаются")
        );
    }
}