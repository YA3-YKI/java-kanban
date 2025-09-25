package ru.yandex.javacourse.httpApi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
                    sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                } else if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Subtask sub = taskManager.getSubtaskById(id);
                    if (sub == null) {
                        sendText(exchange, "Подзадача не найдена", 404);
                        return;
                    }
                    sendText(exchange, gson.toJson(sub), 200);
                }
                break;

            case "POST":
                Subtask subtask = readRequest(exchange, Subtask.class);
                if (taskManager.intersectsWithAny(subtask)) {
                    sendText(exchange, "Пересечение с другой задачей", 406);
                    return;
                }
                if (subtask.getId() == 0) {
                    taskManager.createSubtask(subtask);
                    sendText(exchange, "Подзадача создана", 201);
                } else {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "Подзадача обновлена", 200);
                }
                break;

            case "DELETE":
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Subtask toDelete = taskManager.getSubtaskById(id);
                    if (toDelete == null) {
                        sendText(exchange, "Подзадача не найдена", 404);
                        return;
                    }
                    taskManager.deleteSubtask(taskManager.getSubtaskById(id));
                    sendText(exchange, "Подзадача удалена", 200);
                } else {
                    taskManager.deleteAllSubtasks();
                    sendText(exchange, "Все подзадачи удалены", 200);
                }
                break;

            default:
                sendText(exchange, "Метод не поддерживается", 405);
        }
    }
}