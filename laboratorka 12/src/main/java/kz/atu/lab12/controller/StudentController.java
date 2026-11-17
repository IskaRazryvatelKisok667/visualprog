package kz.atu.lab12.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import kz.atu.lab12.model.Student;
import kz.atu.lab12.repository.StudentRepository;
import kz.atu.lab12.util.AsyncRunner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentController {

    private static final Logger LOG = Logger.getLogger(StudentController.class.getName());

    @FXML private TableView<Student> tableStudents;
    @FXML private TableColumn<Student, Number> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colGroup;
    @FXML private TableColumn<Student, Number> colGpa;
    @FXML private TextField txtName;
    @FXML private TextField txtGroup;
    @FXML private TextField txtGpa;
    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;

    private final StudentRepository repository = new StudentRepository();
    private final ObservableList<Student> items = FXCollections.observableArrayList();
    private FilteredList<Student> filtered;

    /** Проверенные данные из полей ввода. */
    private record Input(String name, String group, double gpa) {
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().id()));
        colName.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().fullName()));
        colGroup.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().groupName()));
        colGpa.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().gpa()));

        // ObservableList -> FilteredList (поиск) -> SortedList (сортировка по заголовкам) -> TableView
        filtered = new FilteredList<>(items, s -> true);
        SortedList<Student> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableStudents.comparatorProperty());
        tableStudents.setItems(sorted);

        txtSearch.textProperty().addListener((obs, oldV, newV) -> applyFilter());

        // выбор строки таблицы -> значения попадают в поля ввода
        tableStudents.getSelectionModel().selectedItemProperty().addListener((obs, oldV, s) -> {
            if (s != null) {
                txtName.setText(s.fullName());
                txtGroup.setText(s.groupName());
                txtGpa.setText(String.valueOf(s.gpa()));
            }
        });

        AsyncRunner.run(
                () -> {
                    repository.initializeDatabase();
                    return repository.findAll();
                },
                list -> {
                    items.setAll(list);
                    setStatus("Записей: " + items.size(), false);
                },
                e -> fail("Не удалось открыть базу данных", e));
    }

    // ---------- обработчики кнопок ----------

    @FXML
    private void onAdd() {
        try {
            Input in = readInput();
            AsyncRunner.run(
                    () -> {
                        repository.insert(in.name(), in.group(), in.gpa());
                        return null;
                    },
                    ok -> {
                        clearFields();
                        reload("Запись добавлена");
                    },
                    e -> fail("Ошибка сохранения в БД", e));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Student selected = tableStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите запись в таблице");
            return;
        }
        try {
            Input in = readInput();
            AsyncRunner.run(
                    () -> repository.update(selected.id(), in.name(), in.group(), in.gpa()),
                    rows -> {
                        if (rows == 0) {
                            showError("Запись не найдена (возможно, она уже удалена)");
                            reload("");
                        } else {
                            reload("Запись обновлена");
                        }
                    },
                    e -> fail("Ошибка обновления записи", e));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Student selected = tableStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите запись в таблице");
            return;
        }
        ButtonType yes = new ButtonType("Удалить", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить студента «" + selected.fullName() + "»?", yes, no);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText(null);
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isEmpty() || answer.get() != yes) {
            setStatus("Удаление отменено", false);
            return;
        }
        AsyncRunner.run(
                () -> repository.delete(selected.id()),
                rows -> {
                    clearFields();
                    reload(rows == 0 ? "Запись уже была удалена" : "Запись удалена");
                },
                e -> fail("Ошибка удаления записи", e));
    }

    @FXML
    private void onRefresh() {
        reload("Данные обновлены");
    }

    @FXML
    private void onClear() {
        clearFields();
        tableStudents.getSelectionModel().clearSelection();
        setStatus("Поля очищены", false);
    }

    /** Дополнительное задание: экспорт видимых строк таблицы в CSV (UTF-8, разделитель «;»). */
    @FXML
    private void onExport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Экспорт в CSV");
        chooser.setInitialFileName("students.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        File file = chooser.showSaveDialog(tableStudents.getScene().getWindow());
        if (file == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("\uFEFFid;full_name;group_name;gpa\n");
        for (Student s : tableStudents.getItems()) {
            sb.append(s.id()).append(';')
              .append(csv(s.fullName())).append(';')
              .append(csv(s.groupName())).append(';')
              .append(s.gpa()).append('\n');
        }
        String content = sb.toString();
        AsyncRunner.run(
                () -> Files.writeString(file.toPath(), content, StandardCharsets.UTF_8),
                path -> setStatus("Экспортировано строк: " + tableStudents.getItems().size(), false),
                e -> fail("Не удалось сохранить CSV-файл", e));
    }

    // ---------- вспомогательные методы ----------

    private Input readInput() {
        String name = txtName.getText().trim();
        String group = txtGroup.getText().trim();
        if (name.isBlank() || group.isBlank()) {
            throw new IllegalArgumentException("Заполните ФИО и группу");
        }
        if (name.length() > 100 || group.length() > 30) {
            throw new IllegalArgumentException("Слишком длинное ФИО или название группы");
        }
        String raw = txtGpa.getText().trim().replace(',', '.');
        if (!raw.matches("-?\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("GPA должен быть числом");
        }
        double gpa = Double.parseDouble(raw);
        if (!Double.isFinite(gpa) || gpa < 0 || gpa > 4) {
            throw new IllegalArgumentException("GPA должен быть от 0 до 4");
        }
        return new Input(name, group, gpa);
    }

    /** Перечитывает таблицу из БД и обновляет ObservableList. */
    private void reload(String message) {
        AsyncRunner.run(
                repository::findAll,
                list -> {
                    items.setAll(list);
                    String prefix = message.isEmpty() ? "" : message + ". ";
                    setStatus(prefix + "Записей: " + items.size(), false);
                },
                e -> fail("Не удалось прочитать данные из БД", e));
    }

    private void applyFilter() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        filtered.setPredicate(s -> q.isEmpty()
                || s.fullName().toLowerCase(Locale.ROOT).contains(q)
                || s.groupName().toLowerCase(Locale.ROOT).contains(q));
        setStatus("Показано: " + filtered.size() + " из " + items.size(), false);
    }

    private void clearFields() {
        txtName.clear();
        txtGroup.clear();
        txtGpa.clear();
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void setStatus(String text, boolean error) {
        lblStatus.setText(text);
        lblStatus.setStyle(error ? "-fx-text-fill: #c62828;" : "-fx-text-fill: #2e7d32;");
    }

    private void showError(String message) {
        setStatus(message, true);
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /** Подробности исключения — только в лог, пользователю — короткое сообщение. */
    private void fail(String userMessage, Throwable e) {
        LOG.log(Level.SEVERE, userMessage, e);
        showError(userMessage);
    }
}
