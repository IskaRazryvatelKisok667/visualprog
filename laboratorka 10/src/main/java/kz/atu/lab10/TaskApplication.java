package kz.atu.lab10;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class TaskApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(TaskApplication.class.getResource("task-view.fxml"));
        stage.setScene(new Scene(loader.load(), 850, 550));
        stage.setTitle("Дневник задач (Вариант 2)");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}