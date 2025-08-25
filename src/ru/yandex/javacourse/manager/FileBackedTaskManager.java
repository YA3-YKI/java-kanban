package ru.yandex.javacourse.manager;

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
        save(newTask);
    }

    @Override
    public void createSubtask(Subtask newSubtask) {
        super.createSubtask(newSubtask);
        save(newSubtask);
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save(newEpic);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save(task);
    }

    private void save(Task task) {
        String result;


        if (task.getClass().getSimpleName().equals("Subtask")) {

            result = task.getId() + "," + task.getClass().getSimpleName() + "," + task.getTitle() + "," + task.getStatus() + "," +
                    task.getDescription() + "," + subtasks.get(task.getId()).getEpicId() + "\n";
        } else {
            result = task.getId() + "," + task.getClass().getSimpleName() + "," + task.getTitle() + "," + task.getStatus() + "," +
                    task.getDescription() + "\n";
        }

        try (Writer fileWriter = new FileWriter(tempFilePath.toFile(), true)) {
            fileWriter.write(result);
        } catch (IOException e) {
            System.out.println("Ошибка записи первой строки");
        }
    }

    private static Path startedOperation() {
        String filePath = "";
        try {
            File temp = File.createTempFile("temp", ".csv");
            filePath = temp.getPath();
            try (Writer fileWriter = new FileWriter(temp)) {
                fileWriter.write("id,type,name,status,description,epic\n");
            } catch (IOException e) {
                System.out.println("Ошибка записи первой строки");
            }
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

    public void lineReader(FileBackedTaskManager fileBackedTaskManager) {
        int lineCount;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileBackedTaskManager.tempFilePath.toFile()))) {
            lineCount = (int) reader.lines().count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileBackedTaskManager.tempFilePath.toFile()))) {
            reader.readLine();
            for (int i = 1; i < lineCount; i++) {
                String line = reader.readLine();
                Task newTask = fromTempFile(line);
                fileBackedTaskManager.addTask(newTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        Task task = new Task(0, "Задача 1", "Описание Задачи1", Status.NEW);
        fileBackedTaskManager.createTask(task);
        Epic epic = new Epic(0, "Эпик 1", "Описание Эпика1", Status.NEW);
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание Подзадачи1", Status.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);

        fileBackedTaskManager.deleteAllTasks();
        fileBackedTaskManager.deleteAllEpics();
        fileBackedTaskManager.deleteAllSubtasks();

        fileBackedTaskManager.lineReader(fileBackedTaskManager);

        System.out.println(fileBackedTaskManager.tempFilePath);
    }
}