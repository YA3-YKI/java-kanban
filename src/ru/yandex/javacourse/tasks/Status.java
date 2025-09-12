package ru.yandex.javacourse.tasks;

public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    public static Status parse(String str) {
        return Status.valueOf(str.toUpperCase());
    }
}
