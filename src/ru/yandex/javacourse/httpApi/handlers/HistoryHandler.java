package ru.yandex.javacourse.httpApi.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
        } else {
            sendText(exchange, "Метод не поддерживается", 405);
        }
    }
}
