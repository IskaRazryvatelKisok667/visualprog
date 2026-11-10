package kz.atu.lab11.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.atu.lab11.model.Product;
import kz.atu.lab11.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Контроллер: только реакция на действия пользователя и обновление представления. */
public class ProductController {
    private static final String ALL = "Все";

    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, String> colName, colCategory;
    @FXML private TableColumn<Product, Number> colPrice;
    @FXML private TextField txtName, txtCategory, txtPrice, txtSearch;
    @FXML private ComboBox<String> cmbCategoryFilter;
    @FXML private Label lblCount, lblAverage;

    private final ProductService service = new ProductService();
    private final ObservableList<String> categoryItems = FXCollections.observableArrayList();
    private FilteredList<Product> filtered;

    @FXML
    private void initialize() {
        colName.setCellValueFactory(c -> c.getValue().nameProperty());
        colCategory.setCellValueFactory(c -> c.getValue().categoryProperty());
        colPrice.setCellValueFactory(c -> c.getValue().priceProperty());

        service.addProduct("Ноутбук Lenovo", "Электроника", 350000);
        service.addProduct("Смартфон Samsung", "Электроника", 280000);
        service.addProduct("Офисный стул", "Мебель", 45000);
        service.addProduct("Шариковая ручка", "Канцелярия", 150);

        filtered = new FilteredList<>(service.getProducts(), f -> true);
        SortedList<Product> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableProducts.comparatorProperty());
        tableProducts.setItems(sorted);

        cmbCategoryFilter.setItems(categoryItems);
        refreshCategories();

        txtSearch.textProperty().addListener((o, a, b) -> applyFilter());
        cmbCategoryFilter.valueProperty().addListener((o, a, b) -> applyFilter());

        // счётчик и средняя цена обновляются при любом изменении данных
        filtered.addListener((ListChangeListener<Product>) c -> updateStats());
        service.getProducts().addListener((ListChangeListener<Product>) c -> updateStats());

        // выбор строки заполняет поля ввода
        tableProducts.getSelectionModel().selectedItemProperty().addListener((o, old, f) -> {
            if (f != null) {
                txtName.setText(f.getName());
                txtCategory.setText(f.getCategory());
                txtPrice.setText(String.valueOf(f.getPrice()));
            }
        });
        applyFilter();
    }

    private void applyFilter() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        String category = cmbCategoryFilter.getValue();
        filtered.setPredicate(f -> f.getName().toLowerCase(Locale.ROOT).contains(q)
                && (category == null || category.equals(ALL) || f.getCategory().equalsIgnoreCase(category)));
        updateStats();
    }

    private void updateStats() {
        lblCount.setText("Найдено товаров: " + filtered.size());
        lblAverage.setText(String.format("Средняя цена товаров: %.2f", service.averagePrice()));
    }

    /** Динамический список категорий формируется из данных склада. */
    private void refreshCategories() {
        String current = cmbCategoryFilter.getValue();
        List<String> list = new ArrayList<>();
        list.add(ALL);
        list.addAll(service.getCategories());
        categoryItems.setAll(list);
        cmbCategoryFilter.setValue(list.contains(current) ? current : ALL);
    }

    private double readPrice() {
        try {
            return Double.parseDouble(txtPrice.getText().trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Введите числовую цену");
        }
    }

    @FXML
    private void onAdd() {
        try {
            service.addProduct(txtName.getText(), txtCategory.getText(), readPrice());
            refreshCategories();
            applyFilter();
            clearFields();
        } catch (IllegalArgumentException e) { showError(e.getMessage()); }
    }

    @FXML
    private void onUpdate() {
        try {
            Product f = tableProducts.getSelectionModel().getSelectedItem();
            service.updateProduct(f, txtName.getText(), txtCategory.getText(), readPrice());
            refreshCategories();
            applyFilter();
        } catch (IllegalArgumentException e) { showError(e.getMessage()); }
    }

    @FXML
    private void onDelete() {
        try {
            Product f = tableProducts.getSelectionModel().getSelectedItem();
            if (f == null) throw new IllegalArgumentException("Товар не выбран");
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Удалить товар «" + f.getName() + "»?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Подтверждение удаления");
            confirm.setHeaderText(null);
            if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
            service.deleteProduct(f);
            refreshCategories();
            applyFilter();
            clearFields();
        } catch (IllegalArgumentException e) { showError(e.getMessage()); }
    }

    private void clearFields() {
        txtName.clear(); txtCategory.clear(); txtPrice.clear();
        tableProducts.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
