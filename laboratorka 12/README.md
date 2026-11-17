# Лабораторная работа №12 — CRUD-приложение (JavaFX + SQLite)

Каталог студентов + индивидуальный вариант №3 «Сотрудники».
Автор: Анваров Акрам, группа ИС 24-22.

## Требования
* JDK 17 или новее (проверено на структуре под JDK 21)
* IntelliJ IDEA (Maven встроен) и доступ в интернет при первой загрузке зависимостей

## Как запустить (IntelliJ IDEA)
1. `File → Open…` → выбрать папку проекта (где лежит `pom.xml`) → «Open as Project».
2. Дождаться загрузки Maven-зависимостей (JavaFX 21, sqlite-jdbc).
3. Открыть `src/main/java/kz/atu/lab12/Launcher.java` → зелёный треугольник рядом с `main` → **Run**.
   (Либо в панели Maven: `Plugins → javafx → javafx:run`.)
4. При первом запуске рядом с проектом создаётся файл `students.db`, таблицы создаются автоматически.

## Как сразу увидеть данные
Скопируйте `sample-data/students_demo.db` в корень проекта и переименуйте в `students.db`
(пока приложение закрыто), затем запустите — в таблицах будут демонстрационные записи.
Если файла нет — приложение создаст пустую базу само.

## Просмотр содержимого SQLite-файла (для защиты)
Откройте `students.db` в DB Browser for SQLite (или плагин Database в IntelliJ IDEA Ultimate),
вкладка «Данные» → таблицы `students`, `employees`. Схема: `sql/schema.sql`.

## Проверка «недоступный файл БД»
Run → Edit Configurations → Modify options → Add VM options:
`-Dlab12.db=Z:/no_such_folder/students.db` — приложение покажет «Не удалось открыть базу данных».

## Структура
```
src/main/java/kz/atu/lab12/
  Launcher.java, StudentApplication.java
  model/        Student.java, Employee.java
  repository/   StudentRepository.java, EmployeeRepository.java, DbConfig.java
  controller/   StudentController.java, EmployeeController.java
  util/         AsyncRunner.java   (выполнение SQL вне потока JavaFX)
src/main/resources/kz/atu/lab12/
  main-view.fxml, student-view.fxml, employee-view.fxml
sql/            schema.sql, demo-data.sql
sample-data/    students_demo.db
```
