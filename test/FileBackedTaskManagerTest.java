import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.manager.FileBackedTaskManager;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование FileBackedTaskManager")
class FileBackedTaskManagerTest {

    private FileBackedTaskManager fileManager;

    private static final int DEFAULT_ID = 0;
    private static final String TASK_TITLE = "Задача 1";
    private static final String TASK_DESCRIPTION = "Описание задачи 1";
    private static final Status DEFAULT_STATUS = Status.NEW;

    @BeforeEach
    void setUp() throws IOException {
        fileManager = new FileBackedTaskManager(fileManagerPath());
    }

    private static java.nio.file.Path fileManagerPath() {
        try {
            return java.io.File.createTempFile("temp", ".csv").toPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("После удаления и восстановления задачи список сохраняет размер")
    void restoreFromFile_afterDeleteAll_restoresListSize() throws IOException {
        // Given
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        fileManager.addTask(task1);
        int initialSize = fileManager.getAllTasks().size();

        // When
        fileManager.deleteAllTasks();
        FileBackedTaskManager restoredManager =
                FileBackedTaskManager.loadFromFile(fileManager.getFilePath());
        int restoredSize = restoredManager.getAllTasks().size();

        // Then
        assertEquals(initialSize, restoredSize);
    }

    @Test
    @DisplayName("После удаления и восстановления задачи данные совпадают")
    void restoreFromFile_afterDeleteAll_restoresTaskCorrectly() throws IOException {
        // Given
        Task task1 = new Task(DEFAULT_ID, TASK_TITLE, TASK_DESCRIPTION, DEFAULT_STATUS);
        fileManager.addTask(task1);

        // When
        fileManager.deleteAllTasks();
        FileBackedTaskManager restoredManager =
                FileBackedTaskManager.loadFromFile(fileManager.getFilePath());
        List<Task> restoredTasks = restoredManager.getAllTasks();

        // Then
        assertEquals(TASK_TITLE, restoredTasks.get(0).getTitle());
        assertEquals(TASK_DESCRIPTION, restoredTasks.get(0).getDescription());
        assertEquals(DEFAULT_STATUS, restoredTasks.get(0).getStatus());
    }

    @Test
    @DisplayName("Восстановление из пустого файла возвращает пустой список")
    void restoreFromEmptyFile_returnsEmptyList() throws IOException {
        // Given
        fileManager.deleteAllTasks();

        // When
        FileBackedTaskManager restoredManager =
                FileBackedTaskManager.loadFromFile(fileManager.getFilePath());

        // Then
        assertTrue(restoredManager.getAllTasks().isEmpty());
    }
}