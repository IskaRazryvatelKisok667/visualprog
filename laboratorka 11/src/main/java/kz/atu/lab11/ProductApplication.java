package kz.atu.lab11;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProductApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ProductApplication.class.getResource("product-view.fxml"));
        stage.setTitle("Склад товаров");
        stage.setScene(new Scene(loader.load(), 720, 480));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
