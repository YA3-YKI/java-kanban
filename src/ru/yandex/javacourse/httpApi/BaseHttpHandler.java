package ru.yandex.javacourse.httpApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.manager.HistoryManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;

        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        List<String> parts = parsePath(path);

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, parts);
                    break;
                case "POST":
                    handlePost(exchange, parts);
                    break;
                case "DELETE":
                    handleDelete(exchange, parts);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается: " + method, 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange exchange, List<String> parts) throws IOException {
        if (parts.isEmpty()) {
            sendText(exchange, "GET без endpoint", 400);
            return;
        }

        String endpoint = parts.get(0);

        switch (endpoint) {
            case "tasks":
                if (parts.size() == 1) {
                    sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                } else {
                    Task task = taskManager.getTaskById(Integer.parseInt(parts.get(1)));
                    if (task != null) sendText(exchange, gson.toJson(task), 200);
                    else sendText(exchange, "Задача не найдена", 404);
                }
                break;
            case "subtasks":
                if (parts.size() == 1) {
                    sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                } else {
                    Subtask subtask = taskManager.getSubtaskById(Integer.parseInt(parts.get(1)));
                    if (subtask != null) sendText(exchange, gson.toJson(subtask), 200);
                    else sendText(exchange, "Подзадача не найдена", 404);
                }
                break;
            case "epics":
                if (parts.size() == 1) {
                    sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                } else if (parts.size() == 2) {
                    Epic epic = taskManager.getEpicById(Integer.parseInt(parts.get(1)));
                    if (epic != null) sendText(exchange, gson.toJson(epic), 200);
                    else sendText(exchange, "Эпик не найден", 404);
                } else if (parts.size() == 3 && "subtasks".equals(parts.get(2))) {
                    Epic epic = taskManager.getEpicById(Integer.parseInt(parts.get(1)));
                    if (epic != null) {
                        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
                        sendText(exchange, gson.toJson(subtasks), 200);
                    } else {
                        sendText(exchange, "Эпик не найден", 404);
                    }
                }
                break;
            case "history":
                sendText(exchange, gson.toJson(historyManager.getHistory()), 200);
                break;
            case "prioritized":
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
                break;
            default:
                sendText(exchange, "Неизвестный endpoint: " + endpoint, 404);
        }
    }

    private void handlePost(HttpExchange exchange, List<String> parts) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        switch (parts.get(0)) {
            case "tasks":
                Task task = gson.fromJson(body, Task.class);
                if (task.getId() == 0) {
                    taskManager.addTask(task);
                    sendText(exchange, "Задача создана", 201);
                } else {
                    taskManager.updateTask(task);
                    sendText(exchange, "Задача обновлена", 200);
                }
                break;

            case "subtasks":
                Subtask subtask = gson.fromJson(body, Subtask.class);
                Epic epic = taskManager.getEpicById(subtask.getEpicId());
                if (epic == null) {
                    sendText(exchange, "Эпик не найден", 404);
                    return;
                }

                if (subtask.getId() == 0) {
                    taskManager.createSubtask(subtask);
                    epic.addSubtaskId(subtask.getId());
                    sendText(exchange, "Подзадача создана", 201);
                } else {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "Подзадача обновлена", 200);
                }
                break;

            case "epics":
                Epic newEpic = gson.fromJson(body, Epic.class);
                if (newEpic.getId() == 0) {
                    taskManager.addEpic(newEpic);
                    sendText(exchange, "Эпик создан", 201);
                } else {
                    taskManager.updateEpic(newEpic);
                    sendText(exchange, "Эпик обновлен", 200);
                }
                break;

            default:
                sendText(exchange, "Неизвестный endpoint: " + parts.get(0), 404);
        }
    }

    private void handleDelete(HttpExchange exchange, List<String> parts) throws IOException {
        if (parts.size() < 2) {
            sendText(exchange, "DELETE требует id", 400);
            return;
        }

        String endpoint = parts.get(0);
        int id = Integer.parseInt(parts.get(1));

        switch (endpoint) {
            case "tasks":
                Task task = taskManager.getTaskById(id);
                if (task != null) taskManager.deleteTask(task);
                sendText(exchange, "Задача удалена", 200);
                break;
            case "subtasks":
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) taskManager.deleteSubtask(subtask);
                sendText(exchange, "Подзадача удалена", 200);
                break;
            case "epics":
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) taskManager.deleteEpic(epic);
                sendText(exchange, "Эпик удален", 200);
                break;
            default:
                sendText(exchange, "Неизвестный endpoint: " + endpoint, 404);
        }
    }

    private List<String> parsePath(String path) {
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private void sendText(HttpExchange exchange, String text, int code) throws IOException {
        exchange.sendResponseHeaders(code, text.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }
}