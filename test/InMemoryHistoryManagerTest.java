import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестирование истории задач")
class InMemoryHistoryManagerTest {

    private TaskManager manager;

    private static final int DEFAULT_ID = 0;
    private static final int DIFFERENT_ID = 1;

    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final String TASK_TITLE_2 = "Задача 2";
    private static final String TASK_DESCRIPTION_2 = "Описание задачи 2";

    private static final Status DEFAULT_STATUS = Status.NEW;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    @DisplayName("История сохраняет обновлённую задачу")
    void history_preservesUpdatedTask() {
        // Given
        Task task = new Task(0, "T1", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 9, 13, 12, 0));
        manager.addTask(task);

        // When
        task.setDescription("Updated");
        manager.updateTask(task);

        // Чтобы задача попала в историю, её нужно получить
        manager.getTaskById(task.getId());

        // Then
        assertEquals("Updated", manager.getHistory().get(0).getDescription());
    }


    @Test
    @DisplayName("История не содержит дубликатов")
    void history_noDuplicates() {
        // Given
        Task task = new Task(0, "T1", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 9, 13, 12, 0));
        manager.addTask(task);

        // When: вызываем getTaskById несколько раз, чтобы добавлять в историю
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        // Then
        long count = manager.getHistory().stream()
                .filter(t -> t.getId() == task.getId())
                .count();
        assertEquals(1, count);
    }
}