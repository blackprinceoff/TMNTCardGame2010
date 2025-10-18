package org.author.demo.tmntcardgame.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import org.author.demo.tmntcardgame.model.game.Game;
import org.author.demo.tmntcardgame.model.game.GameState;
import org.author.demo.tmntcardgame.view.GameView;

import java.net.URL;
import java.util.*;

public class GameController implements Initializable {
    @FXML
    private BorderPane rootPane;

    private Game game;
    private GameView gameView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("GameController initialized");
    }

    public void startNewGame(List<String> playerNames) {
        try {
            System.out.println("Starting new game with players: " + playerNames);

            game = new Game();
            game.initializeGame(playerNames);

            System.out.println("Game initialized, creating view...");
            gameView = new GameView(game);

            System.out.println("Setting game view to root pane...");
            rootPane.setCenter(gameView);

            setupEventHandlers();

            System.out.println("Starting first round...");
            game.startNewRound();

            System.out.println("Game started successfully!");

        } catch (Exception e) {
            System.err.println("Error starting game: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText("Не вдалося запустити гру");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupEventHandlers() {
        // Обробники подій для гри можна додати тут
        System.out.println("Event handlers set up");
    }

    @FXML
    private void handleNextRound() {
        if (game != null && game.getGameState() == GameState.SHOWING_RESULTS) {
            game.startNewRound();
        }
    }

    @FXML
    private void handleExitGame() {
        // Повернення до меню
        System.out.println("Exit game requested");
    }
}