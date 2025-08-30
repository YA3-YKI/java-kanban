package test;

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
    private static final int DEFAULT_ID = 0;
    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final Status DEFAULT_STATUS = Status.NEW;

    @BeforeEach
    void setUp() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager();
    }

    @DisplayName("После удаления и восстановления задачи из файла список сохраняет размер")
    @Test
    void restoreFromFile_afterDeleteAll_restoresListSize() throws IOException {
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        fileBackedTaskManager.addTask(task1);

        int initialSize = fileBackedTaskManager.getAllTasks().size();
        fileBackedTaskManager.deleteAllTasks();

        FileBackedTaskManager restoredManager =
                FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getFilePath().toFile());
        int restoredSize = restoredManager.getAllTasks().size();

        assertEquals(initialSize, restoredSize, "Размеры списков не совпадают после восстановления");
    }

    @DisplayName("После удаления и восстановления задачи данные совпадают")
    @Test
    void restoreFromFile_afterDeleteAll_restoresTaskCorrectly() throws IOException {
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        fileBackedTaskManager.addTask(task1);

        fileBackedTaskManager.deleteAllTasks();
        FileBackedTaskManager restoredManager =
                FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getFilePath().toFile());
        Task restoredTask = restoredManager.getAllTasks().getFirst();

        assertAll(
                () -> assertEquals(TASK_TITLE, restoredTask.getTitle()),
                () -> assertEquals(TASK_DESCRIPTION, restoredTask.getDescription()),
                () -> assertEquals(DEFAULT_STATUS, restoredTask.getStatus())
        );
    }

    @DisplayName("Восстановление из пустого файла возвращает пустой список")
    @Test
    void restoreFromEmptyFile_returnsEmptyList() throws IOException {
        fileBackedTaskManager.deleteAllTasks();
        FileBackedTaskManager restoredManager =
                FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getFilePath().toFile());

        assertTrue(restoredManager.getAllTasks().isEmpty(), "Список должен быть пустым");
    }
}