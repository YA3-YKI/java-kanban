package ru.yandex.javacourse;

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
    private FileBackedTaskManager manager;

    private static final int DEFAULT_ID = 0;
    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final String TASK_TITLE_2 = "Задача 2";
    private static final String TASK_DESCRIPTION_2 = "Описание задачи 2";

    private static final Status DEFAULT_STATUS = Status.NEW;

    @BeforeEach
    void setUp() throws IOException {
        manager = new FileBackedTaskManager();
    }

    @DisplayName("После удаления и восстановления задачи из файла список сохраняет размер")
    @Test
    void restoreFromFile_afterDeleteAll_restoresListSize() throws IOException {
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        Task task2 = new Task(DEFAULT_ID, TASK_TITLE_2, TASK_DESCRIPTION_2, DEFAULT_STATUS);
        manager.addTask(task1);
        manager.addTask(task2);

        int initialSize = manager.getAllTasks().size();
        manager.deleteAllTasks();

        File file = manager.getFilePath().toFile();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        int restoredSize = loaded.getAllTasks().size();
        assertEquals(initialSize, restoredSize, "Размер списка задач после восстановления не совпадает");
    }

    @DisplayName("После удаления и восстановления задачи из файла данные совпадают с оригиналом")
    @Test
    void restoreFromFile_afterDeleteAll_restoresTaskCorrectly() throws IOException {
        Task task = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        manager.addTask(task);

        manager.deleteAllTasks();

        File file = manager.getFilePath().toFile();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Task restoredTask = loaded.getAllTasks().getFirst();
        assertAll(
                () -> assertEquals(TASK_TITLE, restoredTask.getTitle(), "Восстановленный title не совпадает"),
                () -> assertEquals(TASK_DESCRIPTION, restoredTask.getDescription(), "Восстановленное описание не совпадает"),
                () -> assertEquals(DEFAULT_STATUS, restoredTask.getStatus(), "Восстановленный статус не совпадает")
        );
    }

    @DisplayName("Восстановление из пустого файла возвращает пустой список")
    @Test
    void restoreFromEmptyFile_returnsEmptyList() throws IOException {
        manager.deleteAllTasks();
        File file = manager.getFilePath().toFile();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty(), "Список задач после восстановления из пустого файла не пустой");
    }
}