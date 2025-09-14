import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Базовые тесты для TaskManager (абстрактный)")
public abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createManager();

    @Test
    @DisplayName("Epic: все подзадачи DONE -> Epic DONE")
    void epicStatus_allDone() {
        TaskManager manager = createManager();
        Epic epic = new Epic(0, "E", "d", Status.NEW);
        manager.addEpic(epic);
        manager.createSubtask(new Subtask(0, "a", "d", Status.DONE, epic.getId()));
        manager.createSubtask(new Subtask(0, "b", "d", Status.DONE, epic.getId()));
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    @DisplayName("Epic: подзадачи NEW + DONE -> Epic IN_PROGRESS")
    void epicStatus_newAndDone() {
        TaskManager manager = createManager();
        Epic epic = new Epic(0, "E", "d", Status.NEW);
        manager.addEpic(epic);
        manager.createSubtask(new Subtask(0, "a", "d", Status.NEW, epic.getId()));
        manager.createSubtask(new Subtask(0, "b", "d", Status.DONE, epic.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    @DisplayName("Epic: подзадача IN_PROGRESS -> Epic IN_PROGRESS")
    void epicStatus_inProgress() {
        TaskManager manager = createManager();
        Epic epic = new Epic(0, "E", "d", Status.NEW);
        manager.addEpic(epic);
        manager.createSubtask(new Subtask(0, "a", "d", Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    @DisplayName("История добавляет и не содержит дубликатов")
    void history_addAndNoDuplicates() {
        TaskManager manager = createManager();
        Task t1 = new Task(0, "T1", "d", Status.NEW);
        Task t2 = new Task(0, "T2", "d", Status.NEW);

        manager.addTask(t1);
        manager.addTask(t2);

        // When: заполняем историю просмотрами
        manager.getTaskById(t1.getId());
        manager.getTaskById(t2.getId());
        manager.getTaskById(t1.getId());

        // Then: история не должна содержать дубликатов и порядок должен быть обновлён (последний просмотр — в конце)
        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(t2.getId(), history.get(0).getId());
        assertEquals(t1.getId(), history.get(1).getId());
    }

    @Test
    @DisplayName("При создании предотвращаются пересечения в приоритетном списке")
    void creating_preventsIntersectionInPrioritized() {
        TaskManager manager = createManager();
        Task a = new Task(0, "A", "d", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 9, 13, 12, 0));
        Task b = new Task(0, "B", "d", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 9, 13, 12, 30));
        manager.addTask(a);
        manager.addTask(b);
        List<Task> pr = manager.getPrioritizedTasks();
        assertTrue(pr.stream().anyMatch(t -> t.getId() == a.getId()));
        assertFalse(pr.stream().anyMatch(t -> t.getId() == b.getId()));
    }

    @Test
    @DisplayName("При обновлении валидация пересечений соблюдается")
    void updating_respectsIntersectionValidation() {
        TaskManager manager = createManager();
        Task a = new Task(0, "A", "d", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 9, 13, 12, 0));
        Task b = new Task(0, "B", "d", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 9, 13, 13, 0));
        manager.addTask(a);
        manager.addTask(b);
        assertTrue(manager.getPrioritizedTasks().stream().anyMatch(t -> t.getId() == a.getId()));
        assertTrue(manager.getPrioritizedTasks().stream().anyMatch(t -> t.getId() == b.getId()));
        b.setStartTime(LocalDateTime.of(2025, 9, 13, 12, 30));
        b.setDuration(Duration.ofMinutes(60));
        manager.updateTask(b);
        assertFalse(manager.getPrioritizedTasks().stream().anyMatch(t -> t.getId() == b.getId()));
    }
}