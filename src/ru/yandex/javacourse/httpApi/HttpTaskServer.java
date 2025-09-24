package ru.yandex.javacourse.httpApi;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.manager.HistoryManager;
import ru.yandex.javacourse.manager.Managers;
import ru.yandex.javacourse.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final HistoryManager historyManager;

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) throws IOException {
        this.taskManager = taskManager;
        this.historyManager = historyManager;

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        // регистрируем все контексты
        httpServer.createContext("/tasks", new BaseHttpHandler(taskManager, historyManager));
        httpServer.createContext("/subtasks", new BaseHttpHandler(taskManager, historyManager));
        httpServer.createContext("/epics", new BaseHttpHandler(taskManager, historyManager));
        httpServer.createContext("/history", new BaseHttpHandler(taskManager, historyManager));
        httpServer.createContext("/prioritized", new BaseHttpHandler(taskManager, historyManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP сервер остановлен");
    }


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new BaseHttpHandler(taskManager, historyManager));
            httpServer.createContext("/subtasks", new BaseHttpHandler(taskManager, historyManager));
            httpServer.createContext("/epics", new BaseHttpHandler(taskManager, historyManager));
            httpServer.createContext("/history", new BaseHttpHandler(taskManager, historyManager));
            httpServer.createContext("/prioritized", new BaseHttpHandler(taskManager, historyManager));
            httpServer.start();

            System.out.println("HTTP сервер запущен на порту " + PORT);

        } catch (IOException e) {
            System.out.println("Ошибка 1");
        }
    }
}