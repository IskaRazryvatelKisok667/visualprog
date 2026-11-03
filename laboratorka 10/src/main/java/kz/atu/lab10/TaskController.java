package kz.atu.lab10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

public class TaskController {
    @FXML private ListView<Task> listTasks;
    @FXML private TextField txtTitle;
    @FXML private TextArea txtBody;
    @FXML private ComboBox<String> cmbPriority;
    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private FilteredList<Task> filteredTasks;
    private final TaskStorage storage = new TaskStorage();

    @FXML
    public void initialize() {
        cmbPriority.setItems(FXCollections.observableArrayList("Высокий", "Средний", "Низкий"));
        cmbPriority.setValue("Средний");

        initDemoData();

        filteredTasks = new FilteredList<>(tasks, p -> true);
        listTasks.setItems(filteredTasks);

        listTasks.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, task) -> {
            if (task != null) {
                txtTitle.setText(task.getTitle());
                txtBody.setText(task.getBody());
                cmbPriority.setValue(task.getPriority());
            }
        });

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredTasks.setPredicate(task -> {
                if (newVal == null || newVal.isBlank()) return true;
                return task.getTitle().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        lblStatus.setText("Готово");
    }

    private void initDemoData() {
        tasks.add(new Task("Подготовка к СРСП", "Ижучить основы сериализации в Java", "Высокий"));
        tasks.add(new Task("Купить продукты", "Молоко, хлеб, фрукты", "Низкий"));
        tasks.add(new Task("Лабораторная 10", "Дописать контроллер для заметок", "Высокий"));
        tasks.add(new Task("Спортзал", "Кардио тренировка 45 минут", "Средний"));
        tasks.add(new Task("Почитать книгу", "Глава 4 по паттернам проектирования", "Средний"));
        tasks.add(new Task("Проверить почту", "Ответить на письма от куратора", "Низкий"));
        tasks.add(new Task("Уборка", "Навести порядок на рабочем столе", "Низкий"));
        tasks.add(new Task("Повторить английский", "30 новых слов на тему IT", "Средний"));
        tasks.add(new Task("Обновить резюме", "Добавить проект на JavaFX", "Высокий"));
        tasks.add(new Task("Планирование недели", "Расписать задачи на следующий месяц", "Средний"));
    }

    @FXML
    private void onAdd() {
        String title = txtTitle.getText().trim();
        String body = txtBody.getText().trim();
        String priority = cmbPriority.getValue();

        if (title.isBlank() || body.isBlank() || priority == null) {
            error("Заполните все поля (заголовок, текст и приоритет).");
            return;
        }

        Task task = new Task(title, body, priority);
        tasks.add(task);
        listTasks.getSelectionModel().select(task);
        lblStatus.setText("Добавлена задача: " + title);
    }

    @FXML
    private void onDelete() {
        Task selected = listTasks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            error("Выберите задачу для удаления.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить выбранную задачу?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            tasks.remove(selected);
            txtTitle.clear();
            txtBody.clear();
            lblStatus.setText("Задача удалена");
        }
    }

    @FXML
    private void onEdit() {
        Task selected = listTasks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            error("Выберите задачу для изменения.");
            return;
        }

        String title = txtTitle.getText().trim();
        String body = txtBody.getText().trim();
        String priority = cmbPriority.getValue();

        if (title.isBlank() || body.isBlank() || priority == null) {
            error("Поля не могут быть пустыми.");
            return;
        }

        selected.setTitle(title);
        selected.setBody(body);
        selected.setPriority(priority);

        int index = tasks.indexOf(selected);
        if (index != -1) {
            tasks.set(index, selected);
        }
        lblStatus.setText("Задача изменена: " + title);
    }

    @FXML
    private void onSave() {
        FileChooser chooser = chooser();
        chooser.setTitle("Сохранить задачи");
        File file = chooser.showSaveDialog(listTasks.getScene().getWindow());
        if (file == null) return;

        try {
            storage.save(file.toPath(), tasks);
            lblStatus.setText("Сохранено: " + file.getName());
        } catch (IOException ex) {
            error("Не удалось сохранить файл.");
        }
    }

    @FXML
    private void onOpen() {
        FileChooser chooser = chooser();
        chooser.setTitle("Открыть задачи");
        File file = chooser.showOpenDialog(listTasks.getScene().getWindow());
        if (file == null) return;

        try {
            var loaded = storage.load(file.toPath());
            tasks.setAll(loaded);
            lblStatus.setText("Загружено задач: " + tasks.size());
        } catch (Exception ex) {
            error("Не удалось прочитать JSON. Файл поврежден или неверного формата.");
        }
    }

    private FileChooser chooser() {
        FileChooser c = new FileChooser();
        c.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        return c;
    }

    private void error(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }
}