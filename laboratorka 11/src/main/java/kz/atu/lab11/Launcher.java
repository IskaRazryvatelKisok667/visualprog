package kz.atu.lab11;

/**
 * Точка входа для запуска из IntelliJ IDEA.
 * Класс не наследует Application, поэтому JavaFX запускается без ошибки
 * «JavaFX runtime components are missing» при подключении через Maven.
 */
public class Launcher {
    public static void main(String[] args) {
        ProductApplication.main(args);
    }
}
