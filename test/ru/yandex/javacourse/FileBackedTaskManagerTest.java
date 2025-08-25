package ru.yandex.javacourse.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.FileBackedTaskManager;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование файлового варианта менеджера задач")
class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    //Константы для тестов
    private static final int DEFAULT_ID = 0;
    private static final int DIFFERENT_ID = 1;
    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final String TASK_TITLE_2 = "Задача 2";
    private static final String TASK_DESCRIPTION_2 = "Описание задачи 2";

    private static final String EPIC_TITLE = "Эпик 1";
    private static final String EPIC_DESCRIPTION = "Описание эпика 1";


    private static final String SUBTASK_TITLE = "Подзадача 1";
    private static final String SUBTASK_DESCRIPTION = "Описание подзадачи 1";


    private static final Status DEFAULT_STATUS = Status.NEW;

    @BeforeEach
    void setUp() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager();
    }


    @DisplayName("После удаления и восстановления задачи из файла список сохраняет размер")
    @Test
    public void restoreFromFile_afterDeleteAll_restoresListSize() throws IOException {
        // Given: Добавляем задачи
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        Task task2 = new Task(DEFAULT_ID, TASK_TITLE_2, TASK_DESCRIPTION_2, DEFAULT_STATUS);
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);

        //When: Получаем размер List'а, очищаем список задач, восстанавливаем задачи из temp файл
        // и получаем новый размер списка всех задач
        int initialSize = fileBackedTaskManager.getAllTasks().size();
        fileBackedTaskManager.deleteAllTasks();

        fileBackedTaskManager.lineReader(fileBackedTaskManager);
        int restoredSize = fileBackedTaskManager.getAllTasks().size();

        // Then: Проверяeм что размер до удаления и после создания равны
        assertEquals(initialSize, restoredSize, "Тест не пройден - размеры не соответствуют");
    }

    @DisplayName("После удаления и восстановления задачи из файла данные совпадают с оригиналом")
    @Test
    void restoreFromFile_afterDeleteAll_restoresTaskCorrectly() throws IOException {
        // Given: Добавляем задачу
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        fileBackedTaskManager.addTask(task1);

        // When: Очищаем список задач и восстанавливаем их из temp файла
        fileBackedTaskManager.deleteAllTasks();
        fileBackedTaskManager.lineReader(fileBackedTaskManager);
        int newID = fileBackedTaskManager.getAllTasks().getFirst().getId();

        // Then: Проверяем, что восстановленная задача совпадает с исходными данными
        assertAll(
                () -> assertEquals(TASK_TITLE, fileBackedTaskManager.getTaskById(newID).getTitle(),
                        "Восстановленный title задачи не совпадает с оригиналом"),
                () -> assertEquals(TASK_DESCRIPTION, fileBackedTaskManager.getTaskById(newID).getDescription(),
                        "Восстановленное описание задачи не совпадает с оригиналом"),
                () -> assertEquals(DEFAULT_STATUS, fileBackedTaskManager.getTaskById(newID).getStatus(),
                        "Восстановленный статус задачи не совпадает с оригиналом")
        );
    }

    @DisplayName("Восстановление из пустого файла возвращает пустой список")
    @Test
    void restoreFromEmptyFile_returnsEmptyList() throws IOException {
        fileBackedTaskManager.deleteAllTasks();
        fileBackedTaskManager.lineReader(fileBackedTaskManager);

        assertTrue(fileBackedTaskManager.getAllTasks().isEmpty(),
                "После восстановления из пустого файла список должен быть пустым");
    }
}