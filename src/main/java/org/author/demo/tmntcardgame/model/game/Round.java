package org.author.demo.tmntcardgame.model.game;

import org.author.demo.tmntcardgame.model.cards.BattleCard;
import org.author.demo.tmntcardgame.model.cards.BonusCard;
import org.author.demo.tmntcardgame.model.cards.Card;

import java.util.*;

public class Round {
    private Map<Player, BattleCard> playedCards;
    private Map<Player, BonusCard> activeBonuses;
    private String selectedCharacteristic;
    private Player winner;
    private List<Card> cardsPool; // Всі карти раунду для переможця

    public Round() {
        this.playedCards = new HashMap<>();
        this.activeBonuses = new HashMap<>();
        this.cardsPool = new ArrayList<>();
    }

    public void addPlayerCard(Player player, BattleCard card) {
        System.out.println("  Round: Adding card " + card.getName() + " for player " + player.getName());
        playedCards.put(player, card);
        cardsPool.add(card);
        System.out.println("  Cards pool now has " + cardsPool.size() + " cards");
    }

    public void addBonusCard(Player player, BonusCard bonus) {
        System.out.println("  Round: Adding bonus " + bonus.getName() + " for player " + player.getName());
        activeBonuses.put(player, bonus);
        cardsPool.add(bonus);
        System.out.println("  Cards pool now has " + cardsPool.size() + " cards");
    }

    public void setSelectedCharacteristic(String characteristic) {
        System.out.println("  Round: Selected characteristic set to: " + characteristic);
        this.selectedCharacteristic = characteristic;
    }

    public Player determineWinner() {
        System.out.println("\n  === Determining Winner ===");

        if (playedCards.isEmpty()) {
            System.out.println("  ERROR: No cards played!");
            return null;
        }

        if (selectedCharacteristic == null) {
            System.out.println("  ERROR: No characteristic selected!");
            return null;
        }

        System.out.println("  Comparing characteristic: " + selectedCharacteristic);
        System.out.println("  Number of cards: " + playedCards.size());

        int maxValue = Integer.MIN_VALUE;
        List<Player> potentialWinners = new ArrayList<>();

        for (Map.Entry<Player, BattleCard> entry : playedCards.entrySet()) {
            Player player = entry.getKey();
            BattleCard card = entry.getValue();

            int value = card.getCharacteristics().getValueByType(selectedCharacteristic);

            System.out.println("    " + player.getName() + " (" + card.getName() + "): " + value);

            if (value > maxValue) {
                maxValue = value;
                potentialWinners.clear();
                potentialWinners.add(player);
                System.out.println("      -> New leader!");
            } else if (value == maxValue) {
                potentialWinners.add(player);
                System.out.println("      -> Tied with leader!");
            }
        }

        // Якщо є кілька переможців - нічия
        if (potentialWinners.size() == 1) {
            winner = potentialWinners.get(0);
            System.out.println("  WINNER: " + winner.getName() + " with value " + maxValue);
            return winner;
        } else {
            System.out.println("  TIE between " + potentialWinners.size() + " players!");
            for (Player p : potentialWinners) {
                System.out.println("    - " + p.getName());
            }
            return null; // Нічия
        }
    }

    public void resetBonuses() {
        System.out.println("  Resetting bonuses for all cards in round...");
        for (BattleCard card : playedCards.values()) {
            card.getCharacteristics().resetTempBonuses();
        }
    }

    // Getters
    public Map<Player, BattleCard> getPlayedCards() { return playedCards; }
    public Map<Player, BonusCard> getActiveBonuses() { return activeBonuses; }
    public String getSelectedCharacteristic() { return selectedCharacteristic; }
    public Player getWinner() { return winner; }
    public List<Card> getCardsPool() { return cardsPool; }
}