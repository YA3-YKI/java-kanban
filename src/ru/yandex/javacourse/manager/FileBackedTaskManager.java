package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.errors.ManagerSaveException;
import ru.yandex.javacourse.tasks.*;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private void save() {
        try (Writer writer = new FileWriter(filePath.toFile())) {
            writer.write("id,type,title,status,description,duration,startTime,endTime,epic\n");

            for (Task task : tasks.values()) writer.write(toCSV(task) + "\n");
            for (Epic epic : epics.values()) writer.write(toCSV(epic) + "\n");
            for (Subtask subtask : subtasks.values()) writer.write(toCSV(subtask) + "\n");

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    private String toCSV(Task task) {
        String type = task instanceof Epic ? "EPIC" : (task instanceof Subtask ? "SUBTASK" : "TASK");
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String start = task.getStartTime() != null ? task.getStartTime().format(Task.FORMATTER) : "";
        String end = task.getEndTime() != null ? task.getEndTime().format(Task.FORMATTER) : "";
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.join(",", String.valueOf(task.getId()), type, task.getTitle(), task.getStatus().name(),
                task.getDescription(), duration, start, end, epicId);
    }

    public static FileBackedTaskManager loadFromFile(Path filePath) {
        FileBackedTaskManager manager = new FileBackedTaskManager(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            reader.readLine(); // пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = fromCSV(line);
                if (task instanceof Epic) manager.addEpic((Epic) task);
                else if (task instanceof Subtask) manager.createSubtask((Subtask) task);
                else manager.addTask(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }

        // пересчитываем время для эпиков
        for (Epic epic : manager.getAllEpics()) {
            List<Subtask> epicSubtasks = manager.getAllSubtasks().stream()
                    .filter(s -> s.getEpicId() == epic.getId())
                    .collect(Collectors.toList());
            epic.updateTimeFromSubtasks(epicSubtasks);
        }

        return manager;
    }

    private static Task fromCSV(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        Status status = Status.parse(parts[3]);
        String description = parts[4];

        Duration duration = (parts.length > 5 && !parts[5].isEmpty()) ? Duration.ofMinutes(Long.parseLong(parts[5])) : null;
        LocalDateTime startTime = (parts.length > 6 && !parts[6].isEmpty()) ? LocalDateTime.parse(parts[6], Task.FORMATTER) : null;

        switch (type) {
            case "TASK":
                return new Task(id, title, description, status, duration, startTime);
            case "EPIC":
                return new Epic(id, title, description, status);
            case "SUBTASK":
                int epicId = (parts.length > 8 && !parts[8].isEmpty()) ? Integer.parseInt(parts[8]) : -1;
                return new Subtask(id, title, description, status, epicId, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
    }
}
