package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование менеджера задач")
public class InMemoryTaskManagerTest {

    private TaskManager manager;

    //Константы для тестов
    private static final int DEFAULT_ID = 0;
    private static final int DIFFERENT_ID = 1;
    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final String TASK_TITLE_2 = "Задача 2";
    private static final String TASK_DESCRIPTION_2 = "Описание задачи 2";

    private static final String EPIC_TITLE = "Эпик 1";
    private static final String EPIC_DESCRIPTION = "Описание эпика 1";
    private static final String EPIC_TITLE_2 = "Эпик 2";
    private static final String EPIC_DESCRIPTION_2 = "Описание эпика 2";

    private static final String SUBTASK_TITLE = "Подзадача 1";
    private static final String SUBTASK_DESCRIPTION = "Описание подзадачи 1";
    private static final String SUBTASK_TITLE_2 = "Подзадача 2";
    private static final String SUBTASK_DESCRIPTION_2 = "Описание подзадачи 2";

    private static final Status DEFAULT_STATUS = Status.NEW;

    @BeforeEach
    public void beforeEach(){
        manager = new InMemoryTaskManager();
    }

    @DisplayName("Тестирование эквивалентности двух задач с одинаковыми id")
    @Test
    public void equals_TasksWithSameIdButDifferentFields_ReturnsTrue() {
        // Given: Добавляем две задачи с одинаковыми id
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        Task task2 = new Task(DEFAULT_ID, TASK_TITLE_2, TASK_DESCRIPTION_2, DEFAULT_STATUS);

        // Then: Проверяем что две задачи эквивалентны
        assertEquals(task1, task2, "Тест не пройдет - экземпляры не соответствуют");
    }

    @DisplayName("Тестирование не эквивалентности двух задач с разными id")
    @Test
    public void equals_TasksWithSameIdButDifferentFields_ReturnsFalse() {
        // Given: Добавляем две задачи с разными id
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        Task task2 = new Task(DIFFERENT_ID, TASK_TITLE_2, TASK_DESCRIPTION_2, DEFAULT_STATUS);

        // Then: Проверяем что две задачи не эквивалентны
        assertNotEquals(task1, task2, "Тест не пройдет - экземпляры соответствуют");
    }

    @DisplayName("Тестирование эквивалентности двух подзадач с одинаковыми id")
    @Test
    public void equals_SubtasksWithSameIdButDifferentFields_ReturnsTrue() {
        // Given: Подготовка эпика и два Subtask с одинаковым id
        Epic epic1 = new Epic(DEFAULT_ID, EPIC_TITLE, EPIC_DESCRIPTION, DEFAULT_STATUS);
        manager.addTask(epic1);

        Subtask subtask1 = new Subtask(DEFAULT_ID, SUBTASK_TITLE, SUBTASK_DESCRIPTION
                , DEFAULT_STATUS, epic1.getId());
        Subtask subtask2 = new Subtask(DEFAULT_ID, SUBTASK_TITLE_2, SUBTASK_DESCRIPTION_2
                , DEFAULT_STATUS, epic1.getId());

        // Then: Проверяем что две подзадачи эквивалентны
        assertEquals(subtask1, subtask2, "Тест не пройдет - экземпляры не соответствуют");
    }

    @DisplayName("Тестирование не эквивалентности двух подзадач с разными id")
    @Test
    public void equals_SubtasksWithSameIdButDifferentFields_ReturnsFalse() {
        // Given: Подготовка эпика и два Subtask с разными id
        Epic epic1 = new Epic(DEFAULT_ID, EPIC_TITLE, EPIC_DESCRIPTION, DEFAULT_STATUS);
        manager.addTask(epic1);

        Subtask subtask1 = new Subtask(DEFAULT_ID, SUBTASK_TITLE, SUBTASK_DESCRIPTION
                , DEFAULT_STATUS, epic1.getId());
        Subtask subtask2 = new Subtask(DIFFERENT_ID, SUBTASK_TITLE_2, SUBTASK_DESCRIPTION_2
                , DEFAULT_STATUS, epic1.getId());

        // Then: Проверяем что две подзадачи не эквивалентны
        assertNotEquals(subtask1, subtask2, "Тест не пройдет - экземпляры соответствуют");
    }

    @DisplayName("Тестирование эквивалентности двух Эпиков с одинаковыми id")
    @Test
    public void equals_EpicsWithSameIdButDifferentFields_ReturnsTrue() {
        // Given: Добавляем два Эпика с одинаковыми id
        Epic epic1 = new Epic(DEFAULT_ID, EPIC_TITLE, EPIC_DESCRIPTION, DEFAULT_STATUS);
        Epic epic2 = new Epic(DEFAULT_ID, EPIC_TITLE_2, EPIC_DESCRIPTION_2, DEFAULT_STATUS);

        // Then: Проверяем что два Эпика эквивалентны
        assertEquals(epic1, epic2, "Тест не пройдет - экземпляры не соответствуют");
    }

    @DisplayName("Тестирование не эквивалентности двух Эпиков с разными id")
    @Test
    public void equals_EpicsWithSameIdButDifferentFields_ReturnsFalse() {
        // Given: Добавляем два Эпика с разными id
        Epic epic1 = new Epic(DEFAULT_ID, EPIC_TITLE, EPIC_DESCRIPTION, DEFAULT_STATUS);
        Epic epic2 = new Epic(DIFFERENT_ID, EPIC_TITLE_2, EPIC_DESCRIPTION_2, DEFAULT_STATUS);

        // Then: Проверяем что два Эпика не эквивалентны
        assertNotEquals(epic1, epic2, "Тест не пройдет - экземпляры соответствуют");
    }

    @DisplayName("Тестирование не эквивалентности всех типов задач по id")
    @Test
    public void equals_DifferentTaskTypesWithSameId_ReturnsFalse() {
        // Given: Добавляем Задачу Эпика и подзадачу с одинаковыми id
        Epic epic = new Epic(DEFAULT_ID, EPIC_TITLE, EPIC_DESCRIPTION, DEFAULT_STATUS);
        Task task = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        Subtask subtask = new Subtask(DEFAULT_ID, SUBTASK_TITLE, SUBTASK_DESCRIPTION
                , DEFAULT_STATUS, epic.getId());

        // Then: Проверяем что между разными типами задач не может быть эквивалентности
        assertAll(
                () -> assertNotEquals(task, epic, "Task и Epic с одинаковым ID не должны быть равны"),
                () -> assertNotEquals(task, subtask, "Task и Subtask с одинаковым ID не должны быть равны"),
                () -> assertNotEquals(epic, subtask, "Epic и Subtask с одинаковым ID не должны быть равны")
        );
    }

    @DisplayName("Тестирование поиска по id и соответствия полученных данных")
    @Test
    public void equals_AllFieldsDifferentTaskInManager_ReturnTrue() {
        // Given: создаётся 3 задачи разного типа
        Epic epic = new Epic(DEFAULT_ID, EPIC_TITLE, EPIC_DESCRIPTION, DEFAULT_STATUS);
        manager.addTask(epic);

        Subtask subtask = new Subtask(DIFFERENT_ID, SUBTASK_TITLE, SUBTASK_DESCRIPTION
                , DEFAULT_STATUS, epic.getId());
        manager.addTask(subtask);

        Task task = new Task(2, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        manager.addTask(task);

        // When: Получаем разные типы задач из менеджера по id
        Task foundEpic = manager.getTaskById(epic.getId());
        Task foundSubtask = manager.getTaskById(subtask.getId());
        Task foundTask = manager.getTaskById(task.getId());

        // Then:
        // 1 - Проверяется эквивалентность созданных задач и задач из менеджера
        // 2 - Проверяется соответствие данных Эпика
        // 3 - проверяется соответствие ID у Epic и EpicID у Subtask
        assertAll(
                () -> assertEquals(epic, foundEpic),
                () -> assertEquals(subtask, foundSubtask),
                () -> assertEquals(task, foundTask),

                () -> assertEquals("Эпик 1", foundEpic.getTitle()),
                () -> assertEquals("Описание эпика 1", foundEpic.getDescription()),
                () -> assertEquals(Status.NEW, foundEpic.getStatus()),

                () -> assertEquals(epic.getId(), ((Subtask) foundSubtask).getEpicId())
        );

    }

    @DisplayName("Тестирование функционала по автоматическому изменению ID")
    @Test
    public void equals_TaskFieldAfterAddInManager_ReturnTrue() {
        // Given: создаётся 2 задачи
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        Task task2 = new Task(DEFAULT_ID, TASK_TITLE_2, TASK_DESCRIPTION_2, DEFAULT_STATUS);

        // When: добавляем задачи в manager
        manager.addTask(task1);
        manager.addTask(task2);

        // Then: Проверяем что задача по ID(1) в менеджере соответствует task2.
        // При создании task2 id задавалось как 0
        assertEquals("Задача 2", manager.getAllTasks().getLast().getTitle());
    }

    @DisplayName("Тестирование эквивалентности всех полей задачи после добавления в manager")
    @Test
    public void equals_DifferentTaskAllFields_ReturnTrue() {
        // Given: создаётся задача
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);

        // When: добавляем задачу в manager
        manager.addTask(task1);

        // Then: Проверяем поля task1 и задачи из менеджера
        // 1 - Проверка поля ID
        // 2 - Проверка поля title
        // 3 - Проверка поля description
        // 4 - Проверка поля Status
        assertAll(
                () -> assertEquals(task1.getId(), manager.getTaskById(task1.getId()).getId(),
                        "Поля ID отличаются"),
                () -> assertEquals("Задача 1", manager.getTaskById(task1.getId()).getTitle(),
                        "Поля Title отличаются"),
                () -> assertEquals("Описание задачи 1", manager.getTaskById(task1.getId()).getDescription(),
                        "Поля Description отличаются"),
                () -> assertEquals(Status.NEW, manager.getTaskById(task1.getId()).getStatus(),
                        "Поля Status отличаются")
        );
    }
}