package ru.yandex.javacourse.httpApi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        switch (method) {
            case "GET":
                if (parts.length == 2) {
                    sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                } else if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Task task = taskManager.getTaskById(id);
                    if (task == null) {
                        sendText(exchange, "Задача не найдена", 404);
                        return;
                    }
                    sendText(exchange, gson.toJson(task), 200);
                }
                break;

            case "POST":
                Task task = readRequest(exchange, Task.class);
                if (taskManager.intersectsWithAny(task)) {
                    sendText(exchange, "Пересечение с другой задачей", 406);
                    return;
                }
                if (task.getId() == 0) {
                    taskManager.addTask(task);
                    sendText(exchange, "Задача создана", 201);
                } else {
                    taskManager.updateTask(task);
                    sendText(exchange, "Задача обновлена", 200);
                }
                break;

            case "DELETE":
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Task toDelete = taskManager.getTaskById(id);
                    if (toDelete == null) {
                        sendText(exchange, "Задача не найдена", 404);
                        return;
                    }
                    taskManager.deleteTask(taskManager.getTaskById(id));
                    sendText(exchange, "Задача удалена", 200);
                } else {
                    taskManager.deleteAllTasks();
                    sendText(exchange, "Все задачи удалены", 200);
                }
                break;

            default:
                sendText(exchange, "Метод не поддерживается", 405);
        }
    }
}