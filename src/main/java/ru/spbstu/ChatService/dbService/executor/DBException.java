package ru.spbstu.ChatService.dbService.executor;

public class DBException extends Exception {

    public DBException(Throwable throwable) {
        super(throwable);
    }
}
