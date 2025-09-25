package ru.yandex.javacourse.httpApi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
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
                    sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                } else if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Epic epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendText(exchange, "Эпик не найден", 404);
                        return;
                    }
                    sendText(exchange, gson.toJson(epic), 200);
                } else if (parts.length == 4 && "subtasks".equals(parts[3])) {
                    int id = Integer.parseInt(parts[2]);
                    sendText(exchange, gson.toJson(taskManager.getEpicSubtasks(id)), 200);
                }
                break;

            case "POST":
                Epic epic = readRequest(exchange, Epic.class);
                if (epic.getId() == 0) {
                    taskManager.addEpic(epic);
                    sendText(exchange, "Эпик создан", 201);
                } else {
                    taskManager.updateEpic(epic);
                    sendText(exchange, "Эпик обновлен", 200);
                }
                break;

            case "DELETE":
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Epic toDelete = taskManager.getEpicById(id);
                    if (toDelete == null) {
                        sendText(exchange, "Эпик не найден", 404);
                        return;
                    }
                    taskManager.deleteEpic(taskManager.getEpicById(id));
                    sendText(exchange, "Эпик удален", 200);
                } else {
                    taskManager.deleteAllEpics();
                    sendText(exchange, "Все эпики удалены", 200);
                }
                break;

            default:
                sendText(exchange, "Метод не поддерживается", 405);
        }
    }
}
