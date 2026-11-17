package kz.atu.lab12.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Выполняет операции с БД вне потока JavaFX Application Thread.
 * Результат (или ошибка) возвращается обратно в поток JavaFX,
 * поэтому в обработчиках onSuccess / onError можно менять интерфейс.
 * Один рабочий поток гарантирует, что запросы к SQLite идут по очереди.
 */
public final class AsyncRunner {

    private static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-worker");
        t.setDaemon(true);
        return t;
    });

    private AsyncRunner() {
    }

    public static <T> void run(Callable<T> work, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));
        DB_EXECUTOR.execute(task);
    }
}
