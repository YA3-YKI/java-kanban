import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @Test
    void epicStatus_newAndDone() {
        manager = createManager();
        Epic epic = new Epic(0, "Epic", "Desc", Status.NEW);
        manager.addEpic(epic);

        Subtask sub1 = new Subtask(0, "Sub1", "Desc", Status.NEW, epic.getId());
        Subtask sub2 = new Subtask(0, "Sub2", "Desc", Status.DONE, epic.getId());

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        Epic updated = manager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void epicStatus_inProgress() {
        manager = createManager();
        Epic epic = new Epic(0, "Epic", "Desc", Status.NEW);
        manager.addEpic(epic);

        Subtask sub1 = new Subtask(0, "Sub1", "Desc", Status.IN_PROGRESS, epic.getId());
        manager.createSubtask(sub1);

        Epic updated = manager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void isIntersecting_check() {
        manager = createManager();
        Task t1 = new Task(0, "T1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 9, 13, 12, 0));
        Task t2 = new Task(0, "T2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 9, 13, 12, 30));
        Task t3 = new Task(0, "T3", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 9, 13, 13, 30));

        assertTrue(((InMemoryTaskManager) manager).isIntersecting(t1, t2));
        assertFalse(((InMemoryTaskManager) manager).isIntersecting(t1, t3));
    }
}
