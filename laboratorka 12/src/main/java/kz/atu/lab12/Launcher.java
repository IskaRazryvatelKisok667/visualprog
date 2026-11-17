package kz.atu.lab12;

import javafx.application.Application;

/**
 * Отдельный класс запуска. Он не наследует Application, поэтому приложение
 * запускается из IntelliJ IDEA без дополнительных VM-опций (--module-path).
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Application.launch(StudentApplication.class, args);
    }
}
