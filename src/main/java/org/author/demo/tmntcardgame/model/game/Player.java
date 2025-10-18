package org.author.demo.tmntcardgame.model.game;

import javafx.beans.property.*;
import org.author.demo.tmntcardgame.model.cards.BattleCard;
import org.author.demo.tmntcardgame.model.cards.BonusCard;
import org.author.demo.tmntcardgame.model.cards.Card;

import java.util.*;

public class Player {
    private StringProperty name;
    private Deck playerDeck;
    private List<BonusCard> hand;
    private BooleanProperty isActive;
    private IntegerProperty score;
    private BattleCard currentBattleCard;

    public Player(String name) {
        this.name = new SimpleStringProperty(name);
        this.playerDeck = new Deck();
        this.hand = new ArrayList<>();
        this.isActive = new SimpleBooleanProperty(true);
        this.score = new SimpleIntegerProperty(0);
    }

    public Card drawTopCard() {
        System.out.println("  " + getName() + " drawing card from deck of " + playerDeck.size());

        Card card = playerDeck.drawCard();

        // Якщо витягнули бонусну карту, кладемо в руку і тягнемо наступну
        while (card instanceof BonusCard && !playerDeck.isEmpty()) {
            System.out.println("    -> Got bonus card: " + card.getName() + ", adding to hand");
            hand.add((BonusCard) card);
            card = playerDeck.drawCard();
        }

        if (card instanceof BattleCard) {
            currentBattleCard = (BattleCard) card;
            System.out.println("    -> Drew battle card: " + card.getName());
        } else if (card == null) {
            System.out.println("    -> No more cards in deck!");
            currentBattleCard = null;
        }

        // Оновлюємо рахунок
        updateScore();

        return card;
    }

    public BattleCard getCurrentBattleCard() {
        return currentBattleCard;
    }

    // ДОДАНО: Метод для очищення поточної карти після раунду
    public void clearCurrentBattleCard() {
        System.out.println("  Clearing current battle card for " + getName());
        currentBattleCard = null;
    }

    public void useBonusCard(BonusCard bonus) {
        if (hand.contains(bonus) && currentBattleCard != null) {
            bonus.applyBonus(currentBattleCard);
            hand.remove(bonus);
            System.out.println("  " + getName() + " used bonus: " + bonus.getName());
        }
    }

    public void addCardsToBottom(List<Card> cards) {
        System.out.println("  " + getName() + " adding " + cards.size() + " cards to bottom of deck");
        System.out.println("    Deck size before: " + playerDeck.size());

        playerDeck.addCardsToBottom(cards);

        System.out.println("    Deck size after: " + playerDeck.size());
        updateScore();
    }

    private void updateScore() {
        int totalCards = playerDeck.size();
        if (currentBattleCard != null) {
            totalCards++; // Рахуємо поточну карту
        }
        score.set(totalCards);
        System.out.println("  " + getName() + " score updated: " + totalCards + " (deck: " + playerDeck.size() + ", current: " + (currentBattleCard != null ? 1 : 0) + ")");
    }

    public boolean hasCards() {
        boolean has = !playerDeck.isEmpty() || currentBattleCard != null;
        System.out.println("  " + getName() + " hasCards: " + has + " (deck: " + playerDeck.size() + ", current: " + (currentBattleCard != null) + ")");
        return has;
    }

    public void eliminate() {
        System.out.println("  *** " + getName() + " ELIMINATED ***");
        isActive.set(false);
    }

    // Getters
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public Deck getPlayerDeck() { return playerDeck; }
    public List<BonusCard> getHand() { return new ArrayList<>(hand); }
    public boolean isActive() { return isActive.get(); }
    public BooleanProperty activeProperty() { return isActive; }
    public int getScore() { return score.get(); }
    public IntegerProperty scoreProperty() { return score; }
}