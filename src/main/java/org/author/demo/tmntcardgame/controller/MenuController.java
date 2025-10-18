package org.author.demo.tmntcardgame.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.author.demo.tmntcardgame.model.game.Game;
import org.author.demo.tmntcardgame.view.GameView;

import java.net.URL;
import java.util.*;

public class MenuController implements Initializable {
    @FXML private VBox menuBox;
    @FXML private Button startGameButton;
    @FXML private Button rulesButton;
    @FXML private Button exitButton;
    @FXML private Spinner<Integer> playersSpinner;
    @FXML private VBox playerNamesBox;

    private List<TextField> playerNameFields;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (playersSpinner != null) {
            playerNameFields = new ArrayList<>();

            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 2);
            playersSpinner.setValueFactory(valueFactory);

            playersSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                updatePlayerNameFields(newVal);
            });

            updatePlayerNameFields(2);
        }
    }

    public Parent createMenuView(Stage stage) {
        this.primaryStage = stage;
        playerNameFields = new ArrayList<>();

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2a4d3a;");

        Label titleLabel = new Label("БИТВА КАРТОК");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.GOLD);

        Label playersLabel = new Label("Кількість гравців:");
        playersLabel.setFont(Font.font("Arial", 16));
        playersLabel.setTextFill(Color.WHITE);

        playersSpinner = new Spinner<>(2, 6, 2);
        playersSpinner.setPrefWidth(100);

        Label namesLabel = new Label("Імена гравців:");
        namesLabel.setFont(Font.font("Arial", 16));
        namesLabel.setTextFill(Color.WHITE);

        playerNamesBox = new VBox(10);
        playerNamesBox.setAlignment(Pos.CENTER);

        startGameButton = new Button("Почати гру");
        startGameButton.setPrefSize(200, 40);
        startGameButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        startGameButton.setOnAction(e -> handleStartGame());

        rulesButton = new Button("Правила");
        rulesButton.setPrefSize(200, 40);
        rulesButton.setStyle("-fx-font-size: 16px;");
        rulesButton.setOnAction(e -> handleShowRules());

        exitButton = new Button("Вихід");
        exitButton.setPrefSize(200, 40);
        exitButton.setStyle("-fx-font-size: 16px;");
        exitButton.setOnAction(e -> handleExit());

        root.getChildren().addAll(
                titleLabel,
                playersLabel,
                playersSpinner,
                namesLabel,
                playerNamesBox,
                startGameButton,
                rulesButton,
                exitButton
        );

        playersSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePlayerNameFields(newVal);
        });
        updatePlayerNameFields(2);

        return root;
    }

    private void updatePlayerNameFields(int count) {
        playerNamesBox.getChildren().clear();
        playerNameFields.clear();

        for (int i = 1; i <= count; i++) {
            TextField field = new TextField();
            field.setPromptText("Ім'я гравця " + i);
            field.setPrefWidth(200);
            field.setMaxWidth(200);
            playerNameFields.add(field);
            playerNamesBox.getChildren().add(field);
        }
    }

    @FXML
    private void handleStartGame() {
        try {
            System.out.println("=== STARTING GAME ===");

            List<String> playerNames = new ArrayList<>();

            for (int i = 0; i < playerNameFields.size(); i++) {
                String name = playerNameFields.get(i).getText().trim();
                if (name.isEmpty()) {
                    name = "Гравець " + (i + 1);
                }
                playerNames.add(name);
            }

            System.out.println("Players: " + playerNames);

            // Створюємо гру НАПРЯМУ, без FXML
            Game game = new Game();
            game.initializeGame(playerNames);

            System.out.println("Game initialized");

            // Створюємо ігровий вид
            GameView gameView = new GameView(game);

            System.out.println("GameView created");

            // Отримуємо поточну сцену
            Stage stage = primaryStage != null ? primaryStage :
                    (Stage) startGameButton.getScene().getWindow();

            // Встановлюємо нову сцену
            Scene gameScene = new Scene(gameView, 1280, 720);
            stage.setScene(gameScene);
            stage.setTitle("Битва Карток - Гра");

            System.out.println("Scene set");

            // Починаємо гру
            game.startNewRound();

            System.out.println("First round started");
            System.out.println("=== GAME RUNNING ===");

        } catch (Exception e) {
            System.err.println("ERROR starting game:");
            e.printStackTrace();
            showError("Помилка запуску гри", e.getMessage());
        }
    }

    @FXML
    private void handleShowRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Правила гри");
        alert.setHeaderText("Битва Карток - Правила");
        alert.setContentText(
                "🎯 МЕТА ГРИ:\n" +
                        "Зібрати всі карти суперників.\n\n" +

                        "🃏 ТИПИ КАРТ:\n" +
                        "• Бойові карти - мають 4 характеристики\n" +
                        "• Бонусні карти - дають додаткові ефекти\n\n" +

                        "🎮 ХІД ГРИ:\n" +
                        "1. Гравці отримують рівну кількість карт\n" +
                        "2. Кожен раунд всі відкривають верхню карту\n" +
                        "3. Активний гравець обирає характеристику\n" +
                        "4. Виграє той, у кого найбільше значення\n" +
                        "5. Переможець забирає всі карти раунду\n\n" +

                        "⚡ БОНУСИ:\n" +
                        "• Бонусні карти зберігаються в руці\n" +
                        "• Можна використати 1 бонус за раунд\n" +
                        "• Бонус діє лише на поточний раунд\n\n" +

                        "🏆 ПЕРЕМОГА:\n" +
                        "Гра завершується, коли залишається 1 гравець з картами"
        );
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}