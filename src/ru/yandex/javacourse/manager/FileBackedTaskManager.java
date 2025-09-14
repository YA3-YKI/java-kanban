package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.errors.ManagerSaveException;
import ru.yandex.javacourse.tasks.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        super();
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    private void save() {
        try (Writer writer = new FileWriter(filePath.toFile())) {
            writer.write("id,type,title,status,description,duration,startTime,endTime,epic\n");
            for (Task t : tasks.values()) writer.write(t.toString() + "\n");
            for (Epic e : epics.values()) writer.write(e.toString() + "\n");
            for (Subtask s : subtasks.values()) writer.write(s.toString() + "\n");
        } catch (IOException ex) {
            throw new ManagerSaveException("Save error", ex);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path filePath) {
        FileBackedTaskManager manager = new FileBackedTaskManager(filePath);
        List<Task> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String header = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task t = Task.fromString(line);
                loaded.add(t);
                if (t instanceof Epic) {
                    manager.addEpic((Epic) t);
                } else if (t instanceof Subtask) {
                    Subtask s = (Subtask) t;
                    manager.createSubtask(s);
                } else {
                    manager.addTask(t);
                }
            }
        } catch (FileNotFoundException e) {
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Load error", e);
        }
        manager.epics.values().forEach(e -> manager.updateEpicTimeAndStatus(e.getId()));
        return manager;
    }
}