package kz.atu.lab12.repository;

/**
 * Настройки подключения к SQLite.
 * По умолчанию файл students.db создаётся в рабочей папке проекта.
 * Для проверки «недоступный файл БД» путь можно изменить VM-опцией:
 * -Dlab12.db=Z:/no_such_folder/students.db
 */
public final class DbConfig {

    private DbConfig() {
    }

    public static String url() {
        return "jdbc:sqlite:" + System.getProperty("lab12.db", "students.db");
    }
}
