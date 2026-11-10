package kz.atu.lab11.service;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kz.atu.lab11.model.Product;

import java.util.List;
import java.util.TreeSet;

/** Сервисный слой: вся бизнес-логика склада товаров (без классов GUI). */
public class ProductService {
    public static final double MIN_PRICE = 0.0;
    public static final double MAX_PRICE = 1_000_000.0;

    // extractor: список сообщает об изменении полей элемента (важно для FilteredList)
    private final ObservableList<Product> products = FXCollections.observableArrayList(
            f -> new Observable[]{f.nameProperty(), f.categoryProperty(), f.priceProperty()});

    public ObservableList<Product> getProducts() { return products; }

    public void addProduct(String name, String category, double price) {
        validate(name, category, price);
        checkDuplicate(name, category, null);
        products.add(new Product(name.trim(), category.trim(), price));
    }

    public void updateProduct(Product product, String name, String category, double price) {
        if (product == null) throw new IllegalArgumentException("Товар не выбран");
        validate(name, category, price);
        checkDuplicate(name, category, product);
        product.setName(name.trim());
        product.setCategory(category.trim());
        product.setPrice(price);
    }

    public void deleteProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Товар не выбран");
        products.remove(product);
    }

    /** Средняя цена всех товаров (0, если склад пуст). */
    public double averagePrice() {
        return products.stream().mapToDouble(Product::getPrice).average().orElse(0.0);
    }

    /** Динамический список категорий, сформированный из данных склада. */
    public List<String> getCategories() {
        TreeSet<String> categories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Product f : products) categories.add(f.getCategory());
        return List.copyOf(categories);
    }

    private void validate(String name, String category, double price) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Введите название товара");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Введите категорию");
        if (!Double.isFinite(price) || price < MIN_PRICE || price > MAX_PRICE)
            throw new IllegalArgumentException("Цена должна быть от 0 до 1 000 000");
    }

    /** Запрет дублей по паре «название + категория»; редактируемая запись игнорируется. */
    private void checkDuplicate(String name, String category, Product except) {
        String t = name.trim(), g = category.trim();
        for (Product f : products) {
            if (f != except && f.getName().equalsIgnoreCase(t) && f.getCategory().equalsIgnoreCase(g))
                throw new IllegalArgumentException("Такой товар уже есть на складе");
        }
    }
}
