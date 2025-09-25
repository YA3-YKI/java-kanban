package ru.yandex.javacourse.httpApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.httpApi.handlers.*;
import ru.yandex.javacourse.manager.Managers;
import ru.yandex.javacourse.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/tasks", new TaskHandler(manager, gson));
        server.createContext("/epics", new EpicHandler(manager, gson));
        server.createContext("/subtasks", new SubtaskHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    class Main {
        private static final int PORT = 8080;

        public static void main(String[] args) {
            TaskManager taskManager = Managers.getDefault();

            try {
                HttpTaskServer httpServer = new HttpTaskServer(taskManager);
                httpServer.start();
                System.out.println("HTTP сервер запущен на порту " + PORT);
            } catch (Exception e) {
                System.out.println("Ошибка запуска сервера: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}