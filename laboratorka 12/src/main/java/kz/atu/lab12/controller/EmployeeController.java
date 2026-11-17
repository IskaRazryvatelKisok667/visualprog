package kz.atu.lab12.controller;

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
import kz.atu.lab12.model.Employee;
import kz.atu.lab12.repository.EmployeeRepository;
import kz.atu.lab12.util.AsyncRunner;

import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Индивидуальный вариант №3 «Сотрудники»: ФИО, отдел, стаж. */
public class EmployeeController {

    private static final Logger LOG = Logger.getLogger(EmployeeController.class.getName());

    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, Number> colEmpId;
    @FXML private TableColumn<Employee, String> colEmpName;
    @FXML private TableColumn<Employee, String> colEmpDept;
    @FXML private TableColumn<Employee, Number> colEmpExp;
    @FXML private TextField txtEmpName;
    @FXML private TextField txtEmpDept;
    @FXML private TextField txtEmpExp;
    @FXML private TextField txtEmpSearch;
    @FXML private Label lblEmpStatus;

    private final EmployeeRepository repository = new EmployeeRepository();
    private final ObservableList<Employee> items = FXCollections.observableArrayList();
    private FilteredList<Employee> filtered;

    private record Input(String name, String department, int experience) {
    }

    @FXML
    private void initialize() {
        colEmpId.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().id()));
        colEmpName.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().fullName()));
        colEmpDept.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().department()));
        colEmpExp.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().experience()));

        filtered = new FilteredList<>(items, e -> true);
        SortedList<Employee> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableEmployees.comparatorProperty());
        tableEmployees.setItems(sorted);

        txtEmpSearch.textProperty().addListener((obs, oldV, newV) -> applyFilter());

        tableEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldV, s) -> {
            if (s != null) {
                txtEmpName.setText(s.fullName());
                txtEmpDept.setText(s.department());
                txtEmpExp.setText(String.valueOf(s.experience()));
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

    @FXML
    private void onAdd() {
        try {
            Input in = readInput();
            AsyncRunner.run(
                    () -> {
                        repository.insert(in.name(), in.department(), in.experience());
                        return null;
                    },
                    ok -> {
                        clearFields();
                        reload("Сотрудник добавлен");
                    },
                    e -> fail("Ошибка сохранения в БД", e));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите запись в таблице");
            return;
        }
        try {
            Input in = readInput();
            AsyncRunner.run(
                    () -> repository.update(selected.id(), in.name(), in.department(), in.experience()),
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
        Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите запись в таблице");
            return;
        }
        ButtonType yes = new ButtonType("Удалить", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить сотрудника «" + selected.fullName() + "»?", yes, no);
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
        tableEmployees.getSelectionModel().clearSelection();
        setStatus("Поля очищены", false);
    }

    private Input readInput() {
        String name = txtEmpName.getText().trim();
        String dept = txtEmpDept.getText().trim();
        if (name.isBlank() || dept.isBlank()) {
            throw new IllegalArgumentException("Заполните ФИО и отдел");
        }
        if (name.length() > 100 || dept.length() > 60) {
            throw new IllegalArgumentException("Слишком длинное ФИО или название отдела");
        }
        String raw = txtEmpExp.getText().trim();
        if (!raw.matches("-?\\d{1,9}")) {
            throw new IllegalArgumentException("Стаж должен быть целым числом (лет)");
        }
        int exp = Integer.parseInt(raw);
        if (exp < 0 || exp > 60) {
            throw new IllegalArgumentException("Стаж должен быть от 0 до 60 лет");
        }
        return new Input(name, dept, exp);
    }

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
        String q = txtEmpSearch.getText() == null ? "" : txtEmpSearch.getText().trim().toLowerCase(Locale.ROOT);
        filtered.setPredicate(s -> q.isEmpty()
                || s.fullName().toLowerCase(Locale.ROOT).contains(q)
                || s.department().toLowerCase(Locale.ROOT).contains(q));
        setStatus("Показано: " + filtered.size() + " из " + items.size(), false);
    }

    private void clearFields() {
        txtEmpName.clear();
        txtEmpDept.clear();
        txtEmpExp.clear();
    }

    private void setStatus(String text, boolean error) {
        lblEmpStatus.setText(text);
        lblEmpStatus.setStyle(error ? "-fx-text-fill: #c62828;" : "-fx-text-fill: #2e7d32;");
    }

    private void showError(String message) {
        setStatus(message, true);
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void fail(String userMessage, Throwable e) {
        LOG.log(Level.SEVERE, userMessage, e);
        showError(userMessage);
    }
}
