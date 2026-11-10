package kz.atu.lab11.model;

import javafx.beans.property.*;

/** Модель данных: товар на складе. Поля хранятся в JavaFX Properties. */
public class Product {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();

    public Product(String name, String category, double price) {
        setName(name);
        setCategory(category);
        setPrice(price);
    }

    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }

    public String getCategory() { return category.get(); }
    public void setCategory(String v) { category.set(v); }
    public StringProperty categoryProperty() { return category; }

    public double getPrice() { return price.get(); }
    public void setPrice(double v) { price.set(v); }
    public DoubleProperty priceProperty() { return price; }
}
