import ru.yandex.javacourse.manager.InMemoryTaskManager;

class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}