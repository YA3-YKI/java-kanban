import ru.yandex.javacourse.manager.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            tempFile = Files.createTempFile("tasks", ".csv").toFile();
            return new FileBackedTaskManager(Path.of(tempFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}