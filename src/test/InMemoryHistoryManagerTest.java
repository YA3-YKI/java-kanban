package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестирование истории просмотров задач")
class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private static final int TASK_ID = 0;
    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final Status TASK_STATUS = Status.NEW;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @Test
    @DisplayName("История должна сохранять первоначальную версию задачи после её изменения")
    public void getHistory_AfterUpdatingTask_ReturnsOriginalVersion() {
        // Given: Подготовка менеджера и задачи
        Task task1 = new Task(TASK_ID, TASK_TITLE, TASK_DESCRIPTION, TASK_STATUS);
        manager.addTask(task1);

        // When: Изменяем задачу и добавляем её снова
        task1.setDescription("Новое описание задачи");
        manager.addTask(task1);

        // Then: Проверяем что история сохранила первоначальное значение
        Task fromHistory = manager.getHistory().getFirst();
        assertEquals(TASK_DESCRIPTION, fromHistory.getDescription(),
                "История должна хранить первоначальное значение");
    }

    @Test
    @DisplayName("История должна сохранять только последние 10 задач при превышении лимита")
    public void getHistory_WhenAdded11Tasks_FirstItemIsTask2() {
        // Given: Создание и добавление в manager 11 задач
        for (int i = 1; i <= 11; i++) {
            Task task = new Task(i, "Задача " + i, "Описание " + i, Status.NEW);
            // When: Добавляем задачу в менеджер
            manager.addTask(task);
        }

        // Then: Проверяем что: Вторая задача стала первой в истории
        assertEquals("Описание 2", manager.getHistory().getFirst().getDescription(), "После добавления 11 задач," +
                " 2 задача должна быть первой в истории");

    }
}