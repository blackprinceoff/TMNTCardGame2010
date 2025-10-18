package org.author.demo.tmntcardgame.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.author.demo.tmntcardgame.model.cards.BonusCard;
import org.author.demo.tmntcardgame.model.game.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandView extends VBox {
    private Player player;
    private Label nameLabel;
    private Label scoreLabel;
    private HBox bonusCardsBox;
    private CardView currentCard;
    private StackPane cardHolder;
    private List<CardView> bonusCardViews;

    public PlayerHandView(Player player) {
        this.player = player;
        this.bonusCardViews = new ArrayList<>();
        initializeView();
        bindProperties();
    }

    private void initializeView() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        setPrefWidth(200);
        setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;");

        // Ім'я гравця
        nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Рахунок
        scoreLabel = new Label("Карт: " + player.getScore());
        scoreLabel.setFont(Font.font("Arial", 14));

        // Тримач для поточної карти
        cardHolder = new StackPane();
        cardHolder.setPrefSize(100, 140);
        cardHolder.setStyle("-fx-border-color: #333; -fx-border-width: 2; -fx-border-radius: 5;");

        // Бонусні карти в руці
        Label bonusLabel = new Label("Бонуси:");
        bonusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        bonusCardsBox = new HBox(5);
        bonusCardsBox.setAlignment(Pos.CENTER);
        bonusCardsBox.setPrefHeight(60);

        getChildren().addAll(nameLabel, scoreLabel, cardHolder, bonusLabel, bonusCardsBox);
    }

    private void bindProperties() {
        player.scoreProperty().addListener((obs, oldVal, newVal) -> {
            scoreLabel.setText("Карт: " + newVal);
        });

        player.activeProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                setStyle("-fx-background-color: #cccccc; -fx-background-radius: 10;");
                setOpacity(0.5);
            }
        });
    }

    public void setCurrentCard(CardView card) {
        cardHolder.getChildren().clear();
        if (card != null) {
            currentCard = card;
            cardHolder.getChildren().add(card);
        }
    }

    public void updateBonusCards() {
        System.out.println("Updating bonus cards for " + player.getName() + " (has " + player.getHand().size() + " bonuses)");
        bonusCardsBox.getChildren().clear();
        bonusCardViews.clear();

        for (BonusCard bonus : player.getHand()) {
            CardView bonusView = new CardView(bonus);
            bonusView.setScaleX(0.5);
            bonusView.setScaleY(0.5);

            // ПЕРЕВЕРТАЄМО БОНУСНУ КАРТУ ЩОБ ПОКАЗАТИ ЛИЦЬОВУ СТОРОНУ
            bonusView.flip();

            // Робимо карту "клікабельною"
            bonusView.setStyle("-fx-cursor: hand;");

            bonusCardViews.add(bonusView);
            bonusCardsBox.getChildren().add(bonusView);
        }
    }

    public void highlightAsActive(boolean active) {
        if (active) {
            setBorder(new Border(new BorderStroke(
                    Color.GOLD, BorderStrokeStyle.SOLID,
                    new CornerRadii(10), new BorderWidths(3)
            )));
        } else {
            setBorder(null);
        }
    }

    public Player getPlayer() { return player; }
    public CardView getCurrentCard() { return currentCard; }
    public List<CardView> getBonusCardViews() { return bonusCardViews; }
}