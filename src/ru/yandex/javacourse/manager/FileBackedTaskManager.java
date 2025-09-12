package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.errors.ManagerSaveException;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.io.*;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static int id = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    Path tempFilePath;


    public FileBackedTaskManager() throws IOException {
        super();
        this.tempFilePath = startedOperation();
    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        save();
    }

    @Override
    public void createSubtask(Subtask newSubtask) {
        super.createSubtask(newSubtask);
        save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    public Path getFilePath() {
        return this.tempFilePath;
    }

    private void save() {

        try (Writer writer = new FileWriter(tempFilePath.toFile())) {
            writer.write("id,type,title,status,description,epic\n"); // заголовок CSV
            for (Task task : tasks.values()) writer.write(task.toString() + "\n");
            for (Epic epic : epics.values()) writer.write(epic.toString() + "\n");
            for (Subtask subtask : subtasks.values()) writer.write(subtask.toString() + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    private static Path startedOperation() {
        String filePath = "";
        try {
            File temp = File.createTempFile("temp", ".csv");
            filePath = temp.getPath();
        } catch (IOException e) {
            System.out.println("Ошибка создания темпового файла");
        }

        return Path.of(filePath);
    }

    public static Task fromTempFile(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1].toUpperCase();
        String name = parts[2];
        String description = parts[4];
        Status status = Status.parse(parts[3]);

        return switch (type) {
            case "TASK" -> new Task(id, name, description, status);
            case "EPIC" -> new Epic(id, name, description, status);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new Subtask(id, name, description, status, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Task newTask = fromTempFile(line);
                manager.addTask(newTask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }
        return manager;
    }

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        Task task = new Task(0, "Задача 1", "Описание Задачи1", Status.NEW);
        manager.createTask(task);

        Epic epic = new Epic(0, "Эпик 1", "Описание Эпика1", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание Подзадачи1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        System.out.println("Файл сохранён в: " + manager.tempFilePath);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(manager.tempFilePath.toFile());
        System.out.println("Загруженные эпики': " + loaded.getAllEpics());
        System.out.println("Загруженные задачи: " + loaded.getAllTasks());
        System.out.println("Загруженные подзадачи: " + loaded.getAllSubtasks());
    }
}