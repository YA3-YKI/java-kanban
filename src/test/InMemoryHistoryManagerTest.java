package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.Node;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

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
    public void getHistory_AfterUpdatingTask_ReturnsNewVersion() {
        // Given: Подготовка менеджера и задачи
        Task task1 = new Task(TASK_ID, TASK_TITLE, TASK_DESCRIPTION, TASK_STATUS);
        manager.addTask(task1);

        // When: Изменяем задачу и добавляем её снова
        task1.setDescription("Новое описание задачи");
        manager.addTask(task1);

        // Then: Проверяем что история сохранила первоначальное значение
        assertEquals("Новое описание задачи", manager.getHistory().getFirst().getDescription(),
                "История должна хранить обновленное описание ");
    }

    @Test
    @DisplayName("История должна удалить запись при обращении к задаче с тем же id")
    public void getHistory_WhenAdded11Tasks_FirstItemIsTask2() {
        // Given: Создание и добавление в manager 10 задач
        for (int i = 0; i <= 9; i++) {
            Task task = new Task(i, "Задача " + i, "Описание " + i, Status.NEW);
            // When: Добавляем задачу в менеджер
            manager.addTask(task);
        }

        // When: Получаем задачу по id, это так же обновит историю
        int idFirst = manager.getHistory().getFirst().getId();
        Task temp = manager.getTaskById(idFirst);

        // Then: Проверяем что: Вторая задача c id 2 последней в истории
        assertEquals(temp, manager.getHistory().getLast(),
                "После добавления 10 задач и вызова метода getTaskById, первая задача должна стать последней");

    }

    @Test
    @DisplayName("История не должна содержать дубликаты задач")
    public void getHistory_ShouldNotContainDuplicates() {
        // Given: Создание и добавление в manager 10 задач
        for (int i = 0; i <= 9; i++) {
            Task task = new Task(i, "Задача " + i, "Описание " + i, Status.NEW);
            // When: Добавляем задачу в менеджер
            manager.addTask(task);
        }
        // When: Получаем задачу по id и вводим счетчик подсчета задач из истории
        int idFirst = manager.getHistory().getFirst().getId();
        Task temp = manager.getTaskById(idFirst);

        int counter = 0;
        for (Task task : manager.getHistory()) {
            if (task.getId() == idFirst) {
                counter++;
            }
        }

        // Then: Проверяем что: Вторая задача с id 2 встречается в истории только раз
        assertEquals(1, counter, "Задача с id(2)" +
                " должна встречаться в истории только один раз");
    }

    @Test
    @DisplayName("При удалении ноды должны поменять поля last у предыдущей ноды и first e следующей")
    public void getNode_AfterRemoveTask_ReturnsNewPrevAndNewNextNode() {
        // Given: Создание и добавление в manager 10 задач
        for (int i = 0; i <= 9; i++) {
            Task task = new Task(i, "Задача " + i, "Описание " + i, Status.NEW);
            // When: Добавляем задачу в менеджер
            manager.addTask(task);
        }

        Node prev = manager.getNode().get(3).getPrev();
        Node next = manager.getNode().get(3).getNext();

        manager.deleteTask(manager.getTaskById(3));

        // Then: Проверяем что: соседние ноды, после удаления указывают друг на друга
        assertAll(
                () -> assertEquals(prev, manager.getNode().get(4).getPrev(), "prev следующей ноды должен указывать на prev удалённой"),
                () -> assertEquals(next, manager.getNode().get(2).getNext(), "next предыдущей ноды должен указывать на next удалённой"),
                () -> assertNull(manager.getNode().get(3), "удалённая нода должна отсутствовать в manager")
        );
    }

    @Test
    @DisplayName("При удалении задачи удаляется и нода и мапы")
    public void getNode_AfterRemoveTask_ReturnNodeMapSize() {
        // Given: Создание и добавление в manager всех типов задач
        Epic epic1 = new Epic(1, "Эпик 1", "Описание Эпика1", Status.NEW);
        manager.addTask(epic1);

        Subtask subtask1 = new Subtask(3, "Подзадача1", "Описание подзадачи1",
                Status.NEW, epic1.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(4, "Подзадача2", "Описание подзадачи2",
                Status.NEW, epic1.getId());
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic(2, "Эпик 2", "Описание Эпика2", Status.NEW);
        manager.addTask(epic2);

        Subtask subtask3 = new Subtask(5, "Подзадача3", "Описание подзадачи3",
                Status.NEW, epic2.getId());
        manager.createSubtask(subtask3);

        Task task = new Task(6, TASK_TITLE, TASK_DESCRIPTION, Status.NEW);
        manager.addTask(task);

        // When: Удаляем задачи
        manager.deleteTask(task);
        manager.deleteEpic(epic2);
        manager.deleteSubtask(subtask2);

        // Then: Проверяем что, После удаления задач размер nodeMap = 2
        assertEquals(2, manager.getNode().size(), "После удаления должно остаться всего 2 Ноды");
    }
}