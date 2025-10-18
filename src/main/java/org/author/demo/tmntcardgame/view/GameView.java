package org.author.demo.tmntcardgame.view;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.author.demo.tmntcardgame.controller.MenuController;
import org.author.demo.tmntcardgame.model.cards.BattleCard;
import org.author.demo.tmntcardgame.model.cards.BonusCard;
import org.author.demo.tmntcardgame.model.game.Game;
import org.author.demo.tmntcardgame.model.game.GameState;
import org.author.demo.tmntcardgame.model.game.Player;
import org.author.demo.tmntcardgame.model.game.Round;
import org.author.demo.tmntcardgame.utils.AnimationUtils;

import java.util.*;

public class GameView extends BorderPane {
    private Game game;
    private Map<Player, PlayerHandView> playerViews;
    private VBox playArea;
    private HBox characteristicButtons;
    private Label gameStateLabel;
    private Label roundInfoLabel;
    private Button nextRoundButton;
    private Button backToMenuButton;
    private boolean bonusSelectionActive = false;

    public GameView(Game game) {
        try {
            System.out.println("Creating GameView...");
            this.game = game;
            this.playerViews = new HashMap<>();

            System.out.println("Initializing view...");
            initializeView();

            System.out.println("Binding game state...");
            bindGameState();

            System.out.println("GameView created successfully");
        } catch (Exception e) {
            System.err.println("ERROR creating GameView:");
            e.printStackTrace();
            throw e;
        }
    }

    private void initializeView() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #2a4d3a;");

        // Верхня панель - інформація про гру
        VBox topPanel = new VBox(10);
        topPanel.setAlignment(Pos.CENTER);

        gameStateLabel = new Label("Підготовка до гри");
        gameStateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gameStateLabel.setStyle("-fx-text-fill: white;");

        roundInfoLabel = new Label("");
        roundInfoLabel.setFont(Font.font("Arial", 16));
        roundInfoLabel.setStyle("-fx-text-fill: white;");

        topPanel.getChildren().addAll(gameStateLabel, roundInfoLabel);
        setTop(topPanel);

        // Центральна зона - ігрове поле
        playArea = new VBox(20);
        playArea.setAlignment(Pos.CENTER);
        playArea.setStyle("-fx-background-color: #3a5d4a; -fx-background-radius: 20;");
        playArea.setPadding(new Insets(20));
        playArea.setMinHeight(300);

        ScrollPane scrollPane = new ScrollPane(playArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        setCenter(scrollPane);

        // Нижня панель - кнопки управління
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(10));

        characteristicButtons = new HBox(10);
        characteristicButtons.setAlignment(Pos.CENTER);
        createCharacteristicButtons();

        nextRoundButton = new Button("Наступний раунд");
        nextRoundButton.setPrefSize(150, 40);
        nextRoundButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nextRoundButton.setDisable(true);
        nextRoundButton.setOnAction(e -> handleNextRound());

        backToMenuButton = new Button("Меню");
        backToMenuButton.setPrefSize(100, 40);
        backToMenuButton.setStyle("-fx-font-size: 14px;");
        backToMenuButton.setOnAction(e -> returnToMenu());

        bottomPanel.getChildren().addAll(characteristicButtons, nextRoundButton, backToMenuButton);
        setBottom(bottomPanel);

        // Ліва панель - гравці
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // Права панель - гравці
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);

        // Розподіляємо гравців по панелях
        List<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerHandView playerView = new PlayerHandView(player);
            playerViews.put(player, playerView);

            if (i % 2 == 0) {
                leftPanel.getChildren().add(playerView);
            } else {
                rightPanel.getChildren().add(playerView);
            }
        }

        setLeft(leftPanel);
        setRight(rightPanel);
    }

    private void returnToMenu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Вихід з гри");
        alert.setHeaderText("Ви впевнені, що хочете вийти?");
        alert.setContentText("Прогрес гри буде втрачено!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage stage = (Stage) getScene().getWindow();
                MenuController menuController = new MenuController();
                Parent menuRoot = menuController.createMenuView(stage);
                stage.setScene(new Scene(menuRoot, 1280, 720));
                stage.setTitle("Битва Карток");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void enableBonusSelection() {
        if (bonusSelectionActive) return;
        bonusSelectionActive = true;

        boolean anyBonusAvailable = false;

        for (Player player : game.getActivePlayers()) {
            if (!player.getHand().isEmpty()) {
                anyBonusAvailable = true;
                PlayerHandView playerView = playerViews.get(player);

                // Показуємо повідомлення для кожного гравця
                Label bonusPrompt = new Label(player.getName() + ", натисніть на бонусну карту для активації");
                bonusPrompt.setStyle("-fx-text-fill: yellow; -fx-font-size: 14px;");
                playArea.getChildren().add(bonusPrompt);

                for (CardView bonusCardView : playerView.getBonusCardViews()) {
                    bonusCardView.setOnMouseClicked(e -> {
                        if (!bonusCardView.isDisable()) {
                            BonusCard bonus = (BonusCard) bonusCardView.getCardModel();

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Використання бонусу");
                            alert.setHeaderText("Активувати бонус: " + bonus.getName() + "?");
                            alert.setContentText(bonus.getCardInfo());

                            Optional<ButtonType> res = alert.showAndWait();
                            if (res.isPresent() && res.get() == ButtonType.OK) {
                                game.playBonusCard(player, bonus);
                                bonusCardView.flip();
                                bonusCardView.setDisable(true);
                                bonusCardView.setOpacity(0.5);
                                playerView.updateBonusCards();

                                // Показуємо повідомлення про активацію
                                Label activatedLabel = new Label("✓ " + player.getName() + " активував " + bonus.getName());
                                activatedLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 12px;");
                                playArea.getChildren().add(activatedLabel);
                            }
                        }
                    });
                }
            }
        }

        // Якщо бонусів немає або після вибору, переходимо до гри
        if (!anyBonusAvailable) {
            game.setGameState(GameState.PLAYING_ROUND);
            bonusSelectionActive = false;
        } else {
            // Додаємо кнопку пропуску бонусів
            Button skipBonusButton = new Button("Пропустити бонуси");
            skipBonusButton.setStyle("-fx-font-size: 14px;");
            skipBonusButton.setOnAction(e -> {
                game.setGameState(GameState.PLAYING_ROUND);
                bonusSelectionActive = false;
                playArea.getChildren().clear();
            });
            playArea.getChildren().add(skipBonusButton);

            // Автоматичний перехід через 10 секунд
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> {
                if (bonusSelectionActive) {
                    game.setGameState(GameState.PLAYING_ROUND);
                    bonusSelectionActive = false;
                    playArea.getChildren().clear();
                }
            });
            pause.play();
        }
    }

    private void createCharacteristicButtons() {
        String[] characteristics = {"Сила", "Ловкість", "Майстерність", "Смекалка"};

        for (String char_name : characteristics) {
            Button btn = new Button(char_name);
            btn.setPrefSize(120, 35);
            btn.setStyle("-fx-font-size: 12px;");
            btn.setDisable(true);

            btn.setOnAction(e -> {
                game.selectCharacteristic(char_name.toLowerCase());
                disableCharacteristicButtons();

                // Показуємо вибрану характеристику
                Label selectedLabel = new Label("Обрана характеристика: " + char_name);
                selectedLabel.setStyle("-fx-text-fill: cyan; -fx-font-size: 16px; -fx-font-weight: bold;");
                playArea.getChildren().add(0, selectedLabel);
            });

            characteristicButtons.getChildren().add(btn);
        }
    }

    private void bindGameState() {
        game.gameStateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("Game state changed: " + oldState + " -> " + newState);

            updateGameStateLabel(newState);
            updateControls(newState);

            switch (newState) {
                case DEALING_CARDS:
                    dealCardsAnimation();
                    break;
                case WAITING_FOR_BONUS:
                    enableBonusSelection();
                    break;
                case PLAYING_ROUND:
                    showRoundCards();
                    break;
                case SHOWING_RESULTS:
                    showRoundResults();
                    break;
                case TIE_BREAK:
                    showTieBreak();
                    break;
                case GAME_OVER:
                    showGameOver();
                    break;
            }
        });

        game.currentPlayerProperty().addListener((obs, oldPlayer, newPlayer) -> {
            if (oldPlayer != null) {
                playerViews.get(oldPlayer).highlightAsActive(false);
            }
            if (newPlayer != null) {
                playerViews.get(newPlayer).highlightAsActive(true);
                roundInfoLabel.setText("Хід гравця: " + newPlayer.getName());
            }
        });
    }

    private void handleNextRound() {
        System.out.println("handleNextRound called, state: " + game.getGameState());

        if (game.getGameState() == GameState.SHOWING_RESULTS ||
                game.getGameState() == GameState.TIE_BREAK) {
            playArea.getChildren().clear();

            // Оновлюємо інформацію про гравців
            for (Player player : game.getPlayers()) {
                PlayerHandView playerView = playerViews.get(player);
                if (playerView != null) {
                    playerView.updateBonusCards();
                }
            }

            game.startNewRound();
        }
    }

    private void updateGameStateLabel(GameState state) {
        switch (state) {
            case NOT_STARTED:
                gameStateLabel.setText("Гра не почалася");
                break;
            case DEALING_CARDS:
                gameStateLabel.setText("Роздача карт...");
                break;
            case WAITING_FOR_BONUS:
                gameStateLabel.setText("Використання бонусів");
                break;
            case PLAYING_ROUND:
                gameStateLabel.setText("Раунд в процесі");
                break;
            case SHOWING_RESULTS:
                gameStateLabel.setText("Результати раунду");
                break;
            case TIE_BREAK:
                gameStateLabel.setText("Нічия! Додатковий раунд");
                break;
            case GAME_OVER:
                gameStateLabel.setText("Гра завершена!");
                break;
        }
    }

    private void updateControls(GameState state) {
        System.out.println("updateControls for state: " + state);

        boolean enableCharButtons = (state == GameState.PLAYING_ROUND &&
                game.getCurrentPlayer() != null);

        for (var node : characteristicButtons.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(!enableCharButtons);
            }
        }

        // ВАЖЛИВО: кнопка активна тільки після показу результатів
        nextRoundButton.setDisable(state != GameState.SHOWING_RESULTS && state != GameState.TIE_BREAK);

        System.out.println("Next round button disabled: " + nextRoundButton.isDisable());
    }

    private void dealCardsAnimation() {
        playArea.getChildren().clear();
        Label dealingLabel = new Label("Роздача карт...");
        dealingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        playArea.getChildren().add(dealingLabel);

        // Оновлюємо вигляд гравців
        for (Player player : game.getPlayers()) {
            PlayerHandView playerView = playerViews.get(player);
            playerView.updateBonusCards();
        }

        // НЕ ЗАПУСКАЄМО АВТОМАТИЧНО НАСТУПНИЙ СТАН!
        // Замість цього покажемо кнопку
        Button startButton = new Button("Почати гру");
        startButton.setPrefSize(150, 40);
        startButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        startButton.setOnAction(e -> {
            playArea.getChildren().clear();
            game.setGameState(GameState.WAITING_FOR_BONUS);
        });
        playArea.getChildren().add(startButton);
    }

    private void showRoundCards() {
        System.out.println("=== showRoundCards called ===");
        playArea.getChildren().clear();

        Label roundLabel = new Label("Раунд в процесі");
        roundLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        playArea.getChildren().add(roundLabel);

        HBox cardsBox = new HBox(20);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setPadding(new Insets(20));

        Round round = game.getCurrentRound();
        if (round != null) {
            System.out.println("Round has " + round.getPlayedCards().size() + " cards");

            for (Map.Entry<Player, BattleCard> entry : round.getPlayedCards().entrySet()) {
                System.out.println("Creating CardView for: " + entry.getValue().getName());

                VBox cardContainer = new VBox(5);
                cardContainer.setAlignment(Pos.CENTER);

                Label playerLabel = new Label(entry.getKey().getName());
                playerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                CardView cardView = new CardView(entry.getValue());
                System.out.println("CardView created, flipping...");
                cardView.flip(); // Відразу перевертаємо

                cardContainer.getChildren().addAll(playerLabel, cardView);
                cardsBox.getChildren().add(cardContainer);
            }
        } else {
            System.out.println("ERROR: Round is null!");
        }

        playArea.getChildren().add(cardsBox);
        System.out.println("Cards box added to playArea");

        // ========= ДОДАЙТЕ ЦЕЙ КОД =========
        // Показуємо повідомлення про бонуси
        boolean anyBonusAvailable = false;
        for (Player player : game.getActivePlayers()) {
            if (!player.getHand().isEmpty()) {
                anyBonusAvailable = true;
                break;
            }
        }

        if (anyBonusAvailable) {
            Label bonusHintLabel = new Label("💡 Натисніть на бонусну карту в панелі гравця, щоб використати");
            bonusHintLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 12px; -fx-font-style: italic;");
            playArea.getChildren().add(bonusHintLabel);

            // Активуємо обробники кліків на бонусні карти
            activateBonusCardHandlers();
        }
        // ===================================

        // Активуємо кнопки
        if (game.getCurrentPlayer() != null) {
            enableCharacteristicButtons();

            Label promptLabel = new Label(game.getCurrentPlayer().getName() + ", оберіть характеристику:");
            promptLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 14px; -fx-font-weight: bold;");
            playArea.getChildren().add(promptLabel);
        }
    }

    private void activateBonusCardHandlers() {
        System.out.println("Activating bonus card click handlers...");

        for (Map.Entry<Player, PlayerHandView> entry : playerViews.entrySet()) {
            Player player = entry.getKey();
            PlayerHandView playerView = entry.getValue();

            if (!player.getHand().isEmpty()) {
                System.out.println("  Setting up handlers for " + player.getName() + " with " + player.getHand().size() + " bonuses");

                for (int i = 0; i < playerView.getBonusCardViews().size(); i++) {
                    CardView bonusCardView = playerView.getBonusCardViews().get(i);
                    BonusCard bonusCard = player.getHand().get(i);

                    // Видаляємо старі обробники
                    bonusCardView.setOnMouseClicked(null);

                    // Додаємо новий обробник
                    bonusCardView.setOnMouseClicked(event -> {
                        System.out.println("Bonus card clicked: " + bonusCard.getName());

                        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmAlert.setTitle("Використати бонус");
                        confirmAlert.setHeaderText("Використати " + bonusCard.getName() + "?");
                        confirmAlert.setContentText(bonusCard.getCardInfo());

                        Optional<ButtonType> result = confirmAlert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            System.out.println("Player confirmed using bonus");

                            // Використовуємо бонус
                            game.playBonusCard(player, bonusCard);

                            // Візуально показуємо що бонус використано
                            bonusCardView.setOpacity(0.3);
                            bonusCardView.setDisable(true);

                            // Оновлюємо відображення
                            playerView.updateBonusCards();

                            // Показуємо повідомлення
                            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                            infoAlert.setTitle("Бонус активовано");
                            infoAlert.setHeaderText(player.getName() + " використав бонус!");
                            infoAlert.setContentText(bonusCard.getName() + " застосовано до поточної карти");
                            infoAlert.show();

                            // Закриваємо повідомлення автоматично через 2 секунди
                            PauseTransition pause = new PauseTransition(Duration.seconds(2));
                            pause.setOnFinished(e -> infoAlert.close());
                            pause.play();
                        }
                    });

                    // Додаємо візуальний ефект при наведенні
                    bonusCardView.setOnMouseEntered(e -> {
                        bonusCardView.setStyle("-fx-cursor: hand;");
                        bonusCardView.setScaleX(0.55);
                        bonusCardView.setScaleY(0.55);
                    });

                    bonusCardView.setOnMouseExited(e -> {
                        bonusCardView.setScaleX(0.5);
                        bonusCardView.setScaleY(0.5);
                    });
                }
            }
        }
    }

    private void enableCharacteristicButtons() {
        for (var node : characteristicButtons.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setDisable(false);

                // Анімація кнопок
                ScaleTransition scale = new ScaleTransition(Duration.millis(300), btn);
                scale.setFromX(0.9);
                scale.setFromY(0.9);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        }
    }

    private void showRoundResults() {
        Round round = game.getCurrentRound();
        if (round != null && round.getWinner() != null) {
            Label winnerLabel = new Label("🎉 Переможець раунду: " + round.getWinner().getName() + " 🎉");
            winnerLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 20px; -fx-font-weight: bold;");

            // Анімація появи результату
            winnerLabel.setOpacity(0);
            playArea.getChildren().add(winnerLabel);
            AnimationUtils.fadeIn(winnerLabel);

            // Показуємо скільки карт виграв
            int cardsWon = round.getCardsPool().size();
            Label cardsWonLabel = new Label("Виграно карт: " + cardsWon);
            cardsWonLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playArea.getChildren().add(cardsWonLabel);
        }
    }

    private void showTieBreak() {
        Label tieLabel = new Label("⚔️ Нічия! Карти йдуть у зону нічиї ⚔️");
        tieLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Анімація мигання
        tieLabel.setOpacity(0);
        playArea.getChildren().add(tieLabel);
        AnimationUtils.fadeIn(tieLabel);
    }

    private void showGameOver() {
        playArea.getChildren().clear();

        Player winner = game.getWinner();
        if (winner != null) {
            VBox gameOverBox = new VBox(20);
            gameOverBox.setAlignment(Pos.CENTER);

            Label winLabel = new Label("🏆 ПЕРЕМОЖЕЦЬ 🏆");
            winLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 32px; -fx-font-weight: bold;");

            Label winnerName = new Label(winner.getName());
            winnerName.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

            Label finalScore = new Label("Фінальний рахунок: " + winner.getScore() + " карт");
            finalScore.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

            Button newGameButton = new Button("Нова гра");
            newGameButton.setPrefSize(150, 40);
            newGameButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            newGameButton.setOnAction(e -> returnToMenu());

            gameOverBox.getChildren().addAll(winLabel, winnerName, finalScore, newGameButton);
            playArea.getChildren().add(gameOverBox);

            // Анімація появи
            gameOverBox.setOpacity(0);
            AnimationUtils.fadeIn(gameOverBox);
        }
    }

    private void disableCharacteristicButtons() {
        for (var node : characteristicButtons.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
            }
        }
    }
}