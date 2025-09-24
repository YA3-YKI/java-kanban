import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.httpApi.DurationAdapter;
import ru.yandex.javacourse.httpApi.HttpTaskServer;
import ru.yandex.javacourse.httpApi.LocalDateTimeAdapter;
import ru.yandex.javacourse.manager.HistoryManager;
import ru.yandex.javacourse.manager.InMemoryHistoryManager;
import ru.yandex.javacourse.manager.InMemoryTaskManager;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Status;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    private TaskManager manager;
    private HistoryManager historyManager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
        taskServer = new HttpTaskServer(manager, historyManager);
        taskServer.start();

        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Добавление задачи")
    void testAddTask() throws IOException, InterruptedException {
        // given: новая задача
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        // when: отправляем POST-запрос на сервер
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем, что задача создана
        assertEquals(201, response.statusCode(), "Задача должна создаваться с кодом 201");
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Получение всех задач")
    void testGetTask() throws IOException, InterruptedException {
        // given: две задачи уже добавлены в менеджер
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task1);
        manager.addTask(task2);

        // when: отправляем GET-запрос на сервер для получения всех задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем, что возвращаются обе задачи
        assertEquals(200, response.statusCode(), "Список всех задач должен собираться с кодом 200");
        Task[] tasksFromResponse = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasksFromResponse, "Задачи не возвращаются");
        assertEquals(2, tasksFromResponse.length, "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromResponse[0].getTitle(), "Некорректное имя первой задачи");
        assertEquals("Задача 2", tasksFromResponse[1].getTitle(), "Некорректное имя второй задачи");
    }

    @Test
    @DisplayName("Получение задачи по ID")
    void testGetTaskById() throws IOException, InterruptedException {
        // given: две задачи уже добавлены в менеджер
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task1);
        manager.addTask(task2);
        int id = task2.getId();

        // when: отправляем GET-запрос на сервер для получения задачи по ID
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем, что возвращается правильная задача
        assertEquals(200, response.statusCode(), "Получение задачи по id должно выполняться с кодом 200");
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не возвращается");
        assertEquals("Задача 2", taskFromResponse.getTitle(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Удаление задачи по ID")
    void testDeleteTaskById() throws IOException, InterruptedException {
        // given: две задачи добавлены в менеджер
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task1);
        manager.addTask(task2);
        int id = task2.getId();

        // when: отправляем DELETE-запрос на сервер для удаления задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем, что задача удалена
        assertEquals(200, response.statusCode(), "Удаление задачи должно выполняться с кодом 200");
        assertEquals("Задача удалена", response.body(), "Неверное сообщение после удаления");
        assertEquals(1, manager.getAllTasks().size(), "После удаления должно остаться 1 задача");
    }

    @Test
    @DisplayName("Добавление эпика")
    void testAddEpic() throws IOException, InterruptedException {
        // given: новый эпик
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        String epicJson = gson.toJson(epic);

        // when: отправляем POST-запрос на сервер
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем, что эпик создан
        assertEquals(201, response.statusCode(), "Эпик должен создаваться с кодом 201");
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик 1", epicsFromManager.getFirst().getTitle(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Получение всех эпиков")
    void testGetEpics() throws IOException, InterruptedException {
        // given: два эпика добавлены
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // when: отправляем GET-запрос на сервер
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем, что возвращаются оба эпика
        assertEquals(200, response.statusCode(), "Список всех эпиков должен собираться с кодом 200");
        Epic[] epicsFromResponse = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(epicsFromResponse, "Эпики не возвращаются");
        assertEquals(2, epicsFromResponse.length, "Некорректное количество эпиков");
        assertEquals("Эпик 1", epicsFromResponse[0].getTitle(), "Некорректное имя первого эпика");
        assertEquals("Эпик 2", epicsFromResponse[1].getTitle(), "Некорректное имя второго эпика");
    }

    @Test
    @DisplayName("Получение эпика по ID")
    void testGetEpicById() throws IOException, InterruptedException {
        // given: два эпика добавлены
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        int id = epic2.getId();

        // when: GET-запрос по ID
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем правильный эпик
        assertEquals(200, response.statusCode(), "Получение эпика по id должно выполняться с кодом 200");
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse, "Эпик не возвращается");
        assertEquals("Эпик 2", epicFromResponse.getTitle(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Удаление эпика по ID вместе с подзадачами")
    void testDeleteEpicById() throws IOException, InterruptedException {
        // given: два эпика, один с подзадачей
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1", Status.NEW,
                epic2.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        manager.createSubtask(subtask1);
        int id = epic2.getId();

        // when: DELETE-запрос по ID
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем удаление эпика и подзадачи
        assertEquals(200, response.statusCode(), "Удаление эпика должно выполняться с кодом 200");
        assertEquals("Эпик удален", response.body(), "Неверное сообщение после удаления");
        assertEquals(1, manager.getAllEpics().size(), "После удаления должен остаться 1 эпик");
        assertEquals(0, manager.getAllSubtasks().size(), "После удаления эпика подзадачи должны быть удалены");
    }

    @Test
    @DisplayName("Добавление подзадачи")
    void testAddSubtask() throws IOException, InterruptedException {
        // given: эпик создан
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addEpic(epic);

        // given: новая подзадача
        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание подзадачи 1", Status.NEW,
                epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(subtask);

        // when: POST-запрос на сервер
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверка, что подзадача создана
        assertEquals(201, response.statusCode(), "Подзадача должна создаваться с кодом 201");
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1", subtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Получение истории")
    void testGetHistory() throws IOException, InterruptedException {
        // given: два эпика добавлены и добавлены в историю
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        historyManager.add(epic1);
        historyManager.add(epic2);

        // when: GET-запрос на сервер для получения истории
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверяем корректность истории
        assertEquals(200, response.statusCode(), "Список истории должен возвращаться с кодом 200");
        Task[] tasksArray = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasksArray, "История не возвращается");
        assertEquals(2, tasksArray.length, "Некорректное количество записей в истории");
        assertEquals("Эпик 1", tasksArray[0].getTitle(), "Некорректное имя первого эпика");
        assertEquals("Эпик 2", tasksArray[1].getTitle(), "Некорректное имя второго эпика");
    }

    @Test
    @DisplayName("Получение приоритетных задач")
    void testGetPrioritized() throws IOException, InterruptedException {
        // given: две задачи с разными датами
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusDays(12));
        manager.addTask(task1);
        manager.addTask(task2);

        // when: GET-запрос на сервер для получения приоритетных задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then: проверка порядка и количества задач
        assertEquals(200, response.statusCode(), "Приоритизация должна выполняться с кодом 200");
        Task[] tasksArray = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasksArray, "Список приоритетных задач не возвращается");
        assertEquals(2, tasksArray.length, "Некорректное количество задач");
        assertEquals("Задача 1", tasksArray[0].getTitle(), "Некорректное имя первой задачи");
        assertEquals("Задача 2", tasksArray[1].getTitle(), "Некорректное имя второй задачи");
    }
}