# Лабораторная работа №11 — «Склад товаров» (JavaFX, MVC)

Выполнил: Адильбаев Искандер, группа ИС 24-22. Вариант 4.

## Требования
- JDK 21
- IntelliJ IDEA (Community подойдёт). Maven встроен в IDEA, JavaFX и JUnit скачиваются автоматически (нужен интернет при первом открытии).

## Запуск в IntelliJ IDEA
1. File → Open → выбрать папку `lab11` (в ней лежит `pom.xml`) → Open as Project.
2. Дождаться загрузки Maven-зависимостей (индикатор внизу справа).
3. File → Project Structure → Project → SDK: выбрать JDK 21.
4. Открыть `src/main/java/kz/atu/lab11/Launcher.java` и нажать зелёный треугольник рядом с `main` → Run.
   Запуск через Maven: вкладка Maven → Plugins → javafx → `javafx:run`.

## Тесты
ПКМ по `src/test/java/.../ProductServiceTest.java` → Run, либо вкладка Maven → Lifecycle → `test`.

## Структура
- `model/Product.java` — модель (JavaFX Properties)
- `service/ProductService.java` — бизнес-логика, валидация, CRUD
- `controller/ProductController.java` — обработка событий интерфейса
- `resources/kz/atu/lab11/product-view.fxml` — интерфейс (можно открыть в Scene Builder)
