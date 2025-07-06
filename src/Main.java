public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addTask(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1"
                , Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2"
                , Status.NEW, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addTask(epic2);

        Subtask subtask3 = new Subtask(0, "Подзадача 3", "Описание подзадачи 3"
                , Status.NEW, epic2.getId());
        manager.createSubtask(subtask3);

        System.out.println("=== Задачи ===");
        manager.printAllTasks();

        System.out.println("=== Эпики ===");
        manager.printAllEpics();

        System.out.println("=== Подзадачи ===");
        manager.printAllSubtasks();

        System.out.println("\n=== Обновляем статусы ===");
        manager.updateTaskStatus(task1.getId(), Status.DONE);
        manager.updateSubtaskStatus(subtask1.getId(), Status.DONE);
        manager.updateSubtaskStatus(subtask2.getId(), Status.IN_PROGRESS);
        manager.updateSubtaskStatus(subtask3.getId(), Status.DONE);

        System.out.println("Задача 1: " + manager.getTaskById(task1.getId()));
        System.out.println("Подзадача 1: " + manager.getTaskById(subtask1.getId()));
        System.out.println("Подзадача 2: " + manager.getTaskById(subtask2.getId()));
        System.out.println("Подзадача 3: " + manager.getTaskById(subtask3.getId()));

        System.out.println("Эпик 1 (статус рассчитывается): " + manager.getTaskById(epic1.getId()));
        System.out.println("Эпик 2 (статус рассчитывается): " + manager.getTaskById(epic2.getId()));

        System.out.println("\n=== Удаляем задачу и эпик ===");
        manager.deleteTask(task2);
        manager.deleteEpic(epic1);

        System.out.println("Задачи после удаления:");
        manager.printAllTasks();

        System.out.println("Эпики после удаления:");
        manager.printAllEpics();

        System.out.println("Подзадачи после удаления:");
        manager.printAllSubtasks();
    }
}