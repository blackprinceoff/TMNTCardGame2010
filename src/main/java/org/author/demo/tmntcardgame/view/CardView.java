package org.author.demo.tmntcardgame.view;

import org.author.demo.tmntcardgame.model.cards.*;
import org.author.demo.tmntcardgame.utils.AnimationUtils;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.io.InputStream;

public class CardView extends StackPane {
    private static final double CARD_WIDTH = 100;
    private static final double CARD_HEIGHT = 140;

    private Card cardModel;
    private Node frontView;
    private Node backView;
    private boolean isFlipped = false;

    public CardView(Card card) {
        this.cardModel = card;
        initializeView();
        setupInteractions();
    }

    private void initializeView() {
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        setAlignment(Pos.CENTER);

        // Завантажуємо зображення
        backView = loadCardImage(cardModel.getBackImagePath(), "CARD\nBACK", Color.DARKRED);
        frontView = loadCardImage(cardModel.getImagePath(), cardModel.getName(), Color.LIGHTBLUE);

        frontView.setVisible(false);

        getChildren().addAll(backView, frontView);

        // Ефект тіні
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetX(3.0);
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        setEffect(shadow);
    }

    private Node loadCardImage(String imagePath, String fallbackText, Color fallbackColor) {
        try {
            System.out.println("Trying to load image: " + imagePath);

            // Спробуємо завантажити зображення
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream != null) {
                System.out.println("✓ Successfully found image: " + imagePath);
                Image image = new Image(imageStream);

                // Перевіряємо, чи завантажилось зображення
                if (image.isError()) {
                    System.err.println("✗ Error loading image: " + image.getException());
                    return createPlaceholderCard(fallbackText, fallbackColor);
                }

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(CARD_WIDTH);
                imageView.setFitHeight(CARD_HEIGHT);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);

                imageStream.close();
                return imageView;
            } else {
                System.out.println("✗ Image not found at path: " + imagePath);
                System.out.println("  Looking in classpath...");

                // Виводимо доступні ресурси для діагностики
                try {
                    var resources = getClass().getResource("/images/cards/");
                    if (resources != null) {
                        System.out.println("  Found /images/cards/ directory");
                    } else {
                        System.out.println("  /images/cards/ directory NOT found");
                    }
                } catch (Exception e) {
                    System.out.println("  Cannot check resources: " + e.getMessage());
                }

                return createPlaceholderCard(fallbackText, fallbackColor);
            }
        } catch (Exception e) {
            System.err.println("✗ Exception loading image " + imagePath + ": " + e.getMessage());
            e.printStackTrace();
            return createPlaceholderCard(fallbackText, fallbackColor);
        }
    }

    private Node createPlaceholderCard(String text, Color color) {
        StackPane placeholder = new StackPane();

        Rectangle rect = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        rect.setFill(color);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        // Для бойових карт
        if (cardModel instanceof BattleCard) {
            BattleCard battleCard = (BattleCard) cardModel;
            VBox infoBox = new VBox(2);
            infoBox.setAlignment(Pos.CENTER);
            infoBox.setMaxWidth(CARD_WIDTH - 10);

            // Ім'я карти
            Text nameText = new Text(battleCard.getName());
            nameText.setFill(Color.WHITE);
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            nameText.setWrappingWidth(CARD_WIDTH - 10);
            nameText.setTextAlignment(TextAlignment.CENTER);

            // Тип карти
            Text typeText = new Text(battleCard.getType().getDisplayName());
            typeText.setFill(Color.YELLOW);
            typeText.setFont(Font.font("Arial", 8));

            // Характеристики
            VBox statsBox = new VBox(1);
            statsBox.setAlignment(Pos.CENTER);

            Text statsText = new Text(String.format(
                    "С: %d\nЛ: %d\nМ: %d\nЗ: %d",
                    battleCard.getCharacteristics().getStrength(),
                    battleCard.getCharacteristics().getAgility(),
                    battleCard.getCharacteristics().getMastery(),
                    battleCard.getCharacteristics().getCunning()
            ));
            statsText.setFill(Color.WHITE);
            statsText.setFont(Font.font("Arial", 9));
            statsText.setTextAlignment(TextAlignment.CENTER);

            statsBox.getChildren().add(statsText);
            infoBox.getChildren().addAll(nameText, typeText, statsBox);
            placeholder.getChildren().addAll(rect, infoBox);

            // Для бонусних карт
        } else if (cardModel instanceof BonusCard) {
            BonusCard bonusCard = (BonusCard) cardModel;
            VBox infoBox = new VBox(3);
            infoBox.setAlignment(Pos.CENTER);
            infoBox.setMaxWidth(CARD_WIDTH - 10);

            Text nameText = new Text(bonusCard.getName());
            nameText.setFill(Color.WHITE);
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 9));
            nameText.setWrappingWidth(CARD_WIDTH - 10);
            nameText.setTextAlignment(TextAlignment.CENTER);

            Text effectText = new Text("+" + bonusCard.getBonusValue());
            effectText.setFill(Color.LIME);
            effectText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            infoBox.getChildren().addAll(nameText, effectText);
            placeholder.getChildren().addAll(rect, infoBox);

            // Для інших карт
        } else {
            Text cardText = new Text(text);
            cardText.setFill(Color.WHITE);
            cardText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            cardText.setTextAlignment(TextAlignment.CENTER);
            placeholder.getChildren().addAll(rect, cardText);
        }

        return placeholder;
    }

    private void setupInteractions() {
        setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), this);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), this);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    public void flip() {
        AnimationUtils.flipCard(this, () -> {
            isFlipped = !isFlipped;
            frontView.setVisible(isFlipped);
            backView.setVisible(!isFlipped);
        });
    }

    public void highlight(Color color) {
        DropShadow glow = new DropShadow();
        glow.setRadius(20.0);
        glow.setColor(color);
        setEffect(glow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(glow.radiusProperty(), 5))
        );
        timeline.play();
    }

    public void shake() {
        AnimationUtils.shakeNode(this);
    }

    public Card getCardModel() { return cardModel; }
    public boolean isFlipped() { return isFlipped; }
}