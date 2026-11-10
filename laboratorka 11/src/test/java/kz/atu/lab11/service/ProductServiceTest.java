package kz.atu.lab11.service;

import kz.atu.lab11.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @Test void shouldAddProduct() {
        ProductService s = new ProductService();
        s.addProduct("Ноутбук", "Электроника", 350000);
        assertEquals(1, s.getProducts().size());
    }

    @Test void shouldTrimNameAndCategory() {
        ProductService s = new ProductService();
        s.addProduct("  Ноутбук ", " Электроника  ", 350000);
        assertEquals("Ноутбук", s.getProducts().get(0).getName());
        assertEquals("Электроника", s.getProducts().get(0).getCategory());
    }

    @Test void shouldRejectInvalidPrice() {
        ProductService s = new ProductService();
        assertThrows(IllegalArgumentException.class, () -> s.addProduct("Ноутбук", "Электроника", 2_000_000));
        assertThrows(IllegalArgumentException.class, () -> s.addProduct("Ноутбук", "Электроника", -1));
        assertThrows(IllegalArgumentException.class, () -> s.addProduct("Ноутбук", "Электроника", Double.NaN));
    }

    @Test void shouldRejectBlankNameAndCategory() {
        ProductService s = new ProductService();
        assertThrows(IllegalArgumentException.class, () -> s.addProduct("  ", "Мебель", 5000));
        assertThrows(IllegalArgumentException.class, () -> s.addProduct("Стул", "", 5000));
    }

    @Test void shouldRejectDuplicate() {
        ProductService s = new ProductService();
        s.addProduct("Ноутбук", "Электроника", 350000);
        assertThrows(IllegalArgumentException.class, () -> s.addProduct("ноутбук", "электроника", 300000));
    }

    @Test void shouldUpdateProduct() {
        ProductService s = new ProductService();
        s.addProduct("Ноутбук", "Электроника", 350000);
        Product p = s.getProducts().get(0);
        s.updateProduct(p, "Ноутбук Lenovo", "Компьютеры", 320000);
        assertEquals("Ноутбук Lenovo", p.getName());
        assertEquals(320000, p.getPrice());
    }

    @Test void shouldAllowUpdateWithoutChangingKey() {
        ProductService s = new ProductService();
        s.addProduct("Ноутбук", "Электроника", 350000);
        Product p = s.getProducts().get(0);
        assertDoesNotThrow(() -> s.updateProduct(p, "Ноутбук", "Электроника", 340000));
    }

    @Test void shouldFailUpdateOrDeleteWithoutSelection() {
        ProductService s = new ProductService();
        assertThrows(IllegalArgumentException.class, () -> s.updateProduct(null, "A", "B", 5));
        assertThrows(IllegalArgumentException.class, () -> s.deleteProduct(null));
    }

    @Test void shouldDeleteProduct() {
        ProductService s = new ProductService();
        s.addProduct("Ноутбук", "Электроника", 350000);
        s.deleteProduct(s.getProducts().get(0));
        assertTrue(s.getProducts().isEmpty());
    }

    @Test void shouldCalculateAveragePrice() {
        ProductService s = new ProductService();
        assertEquals(0.0, s.averagePrice());
        s.addProduct("A", "Мебель", 8000);
        s.addProduct("B", "Мебель", 6000);
        assertEquals(7000.0, s.averagePrice(), 1e-9);
    }

    @Test void shouldBuildDynamicCategoryList() {
        ProductService s = new ProductService();
        s.addProduct("A", "Мебель", 5);
        s.addProduct("B", "Канцелярия", 5);
        s.addProduct("C", "мебель", 5);
        assertEquals(2, s.getCategories().size()); // «Мебель» и «мебель» — одна категория
        assertTrue(s.getCategories().contains("Канцелярия"));
    }
}
