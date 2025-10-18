package org.author.demo.tmntcardgame;

import org.author.demo.tmntcardgame.controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {  // <-- Важливо! extends Application

    @Override
    public void start(Stage primaryStage) {
        try {
            // Спробуємо завантажити FXML, якщо не вийде - створимо програмно
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu_view.fxml"));

            if (loader.getLocation() == null) {
                // Якщо FXML не знайдено, створюємо меню програмно
                MenuController menuController = new MenuController();
                Parent root = menuController.createMenuView(primaryStage);

                primaryStage.setTitle("Битва Карток");
                primaryStage.setScene(new Scene(root, 1280, 720));
                primaryStage.setResizable(false);
                primaryStage.show();
            } else {
                Parent root = loader.load();
                primaryStage.setTitle("Битва Карток");
                primaryStage.setScene(new Scene(root, 1280, 720));
                primaryStage.setResizable(false);
                primaryStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Якщо виникла помилка, створюємо простий інтерфейс
            createSimpleUI(primaryStage);
        }
    }

    private void createSimpleUI(Stage primaryStage) {
        MenuController menuController = new MenuController();
        Parent root = menuController.createMenuView(primaryStage);

        primaryStage.setTitle("Битва Карток");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  // Запускаємо JavaFX додаток
    }
}
