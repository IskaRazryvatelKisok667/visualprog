package kz.atu.lab12;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(StudentApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 660);
        stage.setTitle("Лабораторная работа №12 — CRUD-приложение (JavaFX + SQLite)");
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
