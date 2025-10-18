package org.author.demo.tmntcardgame.model.game;

import javafx.beans.property.*;
import org.author.demo.tmntcardgame.model.cards.BattleCard;
import org.author.demo.tmntcardgame.model.cards.BonusCard;
import org.author.demo.tmntcardgame.model.cards.Card;
import org.author.demo.tmntcardgame.utils.CardLoader;

import java.util.*;

public class Game {
    private List<Player> players;
    private ObjectProperty<Player> currentPlayer;
    private ObjectProperty<GameState> gameState;
    private Round currentRound;
    private List<Card> tiePile; // Карти з нічиєї
    private List<Card> allCards;

    public Game() {
        this.players = new ArrayList<>();
        this.currentPlayer = new SimpleObjectProperty<>();
        this.gameState = new SimpleObjectProperty<>(GameState.NOT_STARTED);
        this.tiePile = new ArrayList<>();
    }

    public void initializeGame(List<String> playerNames) {
        System.out.println("=== Initializing Game ===");

        // Створюємо гравців
        for (String name : playerNames) {
            players.add(new Player(name));
            System.out.println("Created player: " + name);
        }

        // Завантажуємо карти
        allCards = CardLoader.loadAllCards();
        System.out.println("Loaded " + allCards.size() + " cards total");

        // Перемішуємо і роздаємо
        dealCards();

        // Встановлюємо першого гравця
        currentPlayer.set(players.get(0));
        System.out.println("Current player set to: " + currentPlayer.get().getName());

        gameState.set(GameState.WAITING_FOR_BONUS);
        System.out.println("Game state: WAITING_FOR_BONUS");
    }

    private void dealCards() {
        System.out.println("=== Dealing Cards ===");
        Collections.shuffle(allCards);

        int cardsPerPlayer = allCards.size() / players.size();
        int cardIndex = 0;

        for (Player player : players) {
            List<Card> playerCards = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer && cardIndex < allCards.size(); i++) {
                playerCards.add(allCards.get(cardIndex++));
            }
            player.getPlayerDeck().addCardsToBottom(playerCards);
            System.out.println(player.getName() + " received " + playerCards.size() + " cards");
        }

        gameState.set(GameState.DEALING_CARDS);
    }

    public void startNewRound() {
        try {
            System.out.println("\n=== Starting new round ===");

            // ОЧИЩАЄМО ПОТОЧНІ КАРТИ ГРАВЦІВ ПЕРЕД НОВИМ РАУНДОМ
            for (Player player : players) {
                if (player.isActive()) {
                    player.clearCurrentBattleCard();
                }
            }

            // Перевіряємо чи є активні гравці
            List<Player> activePlayers = getActivePlayers();
            if (activePlayers.size() < 2) {
                System.out.println("Not enough active players to start round");
                gameState.set(GameState.GAME_OVER);
                return;
            }

            currentRound = new Round();

            // ⚠️ СПОЧАТКУ ВИТЯГУЄМО КАРТИ
            for (Player player : players) {
                if (player.isActive()) {
                    System.out.println("Player " + player.getName() + " drawing card...");
                    Card card = player.drawTopCard();

                    if (card instanceof BattleCard) {
                        currentRound.addPlayerCard(player, (BattleCard) card);
                        System.out.println("  -> Added to round: " + card.getName());
                    } else if (card != null) {
                        System.out.println("  -> Drew non-battle card: " + card.getName());
                    } else {
                        System.out.println("  -> No card! Player will be eliminated");
                    }
                }
            }

            System.out.println("Round started with " + currentRound.getPlayedCards().size() + " battle cards");

            // Перевіряємо стан після витягування карт
            printGameState();

            // ⚠️ ТІЛЬКИ ПІСЛЯ ЦЬОГО ЗМІНЮЄМО СТАН (щоб UI побачив карти)
            setGameState(GameState.PLAYING_ROUND);

        } catch (Exception e) {
            System.err.println("ERROR starting round:");
            e.printStackTrace();
        }
    }

    public void playBonusCard(Player player, BonusCard bonus) {
        System.out.println("=== Playing Bonus Card ===");
        System.out.println("Player: " + player.getName());
        System.out.println("Bonus: " + bonus.getName());

        if (currentRound != null && player.getHand().contains(bonus)) {
            player.useBonusCard(bonus);
            currentRound.addBonusCard(player, bonus);
            System.out.println("Bonus card applied successfully");
        } else {
            System.out.println("Cannot apply bonus: round=" + (currentRound != null) + ", has bonus=" + player.getHand().contains(bonus));
        }
    }

    public void selectCharacteristic(String characteristic) {
        System.out.println("\n=== selectCharacteristic: " + characteristic + " ===");

        if (currentRound == null) {
            System.err.println("ERROR: currentRound is null!");
            return;
        }

        // Переводимо характеристику в правильний формат
        String normalizedChar = characteristic.toLowerCase();

        currentRound.setSelectedCharacteristic(normalizedChar);
        System.out.println("Selected characteristic: " + normalizedChar);

        // Показуємо значення кожного гравця
        System.out.println("Comparing cards:");
        for (Map.Entry<Player, BattleCard> entry : currentRound.getPlayedCards().entrySet()) {
            int value = entry.getValue().getCharacteristics().getValueByType(normalizedChar);
            System.out.println("  " + entry.getKey().getName() + " - " + entry.getValue().getName() + ": " + value);
        }

        processRoundResult();
    }

    private void processRoundResult() {
        System.out.println("\n=== Processing Round Result ===");

        Player winner = currentRound.determineWinner();

        if (winner != null) {
            System.out.println("Winner: " + winner.getName());

            // Переможець забирає всі карти
            List<Card> wonCards = new ArrayList<>(currentRound.getCardsPool());
            System.out.println("Cards won in this round: " + wonCards.size());

            if (!tiePile.isEmpty()) {
                System.out.println("Adding " + tiePile.size() + " cards from tie pile");
                wonCards.addAll(tiePile);
            }

            System.out.println("Total cards won: " + wonCards.size());

            tiePile.clear();

            // Показуємо що за карти виграв
            System.out.println("Cards won:");
            for (Card card : wonCards) {
                System.out.println("  - " + card.getName());
            }

            // ДОДАЄМО КАРТИ ПЕРЕМОЖЦЮ
            System.out.println(winner.getName() + " had " + winner.getPlayerDeck().size() + " cards before");
            winner.addCardsToBottom(wonCards);
            System.out.println(winner.getName() + " now has " + winner.getPlayerDeck().size() + " cards");

            currentPlayer.set(winner);

            setGameState(GameState.SHOWING_RESULTS);
        } else {
            // Нічия
            System.out.println("TIE! Cards go to tie pile");
            int cardsBefore = tiePile.size();
            tiePile.addAll(currentRound.getCardsPool());
            System.out.println("Tie pile: " + cardsBefore + " -> " + tiePile.size() + " cards");

            setGameState(GameState.TIE_BREAK);
        }

        // Скидаємо бонуси
        currentRound.resetBonuses();

        // Перевіряємо, чи хтось вибув
        checkForEliminatedPlayers();

        // Виводимо стан гри
        printGameState();

        // Перевіряємо кінець гри
        List<Player> activePlayers = getActivePlayers();
        if (activePlayers.size() == 1) {
            System.out.println("\n!!! GAME OVER - Winner: " + activePlayers.get(0).getName() + " !!!");
            setGameState(GameState.GAME_OVER);
        } else if (activePlayers.isEmpty()) {
            System.out.println("\n!!! GAME OVER - No active players left !!!");
            setGameState(GameState.GAME_OVER);
        }
    }

    private void checkForEliminatedPlayers() {
        System.out.println("Checking for eliminated players...");
        for (Player player : players) {
            if (player.isActive() && !player.hasCards()) {
                System.out.println("  -> " + player.getName() + " is eliminated (no cards left)");
                player.eliminate();
            }
        }
    }

    private void printGameState() {
        System.out.println("\n--- Current Game State ---");
        for (Player player : players) {
            System.out.println(player.getName() + ": " +
                    player.getPlayerDeck().size() + " cards in deck, " +
                    player.getHand().size() + " bonus cards, " +
                    "active: " + player.isActive());
        }
        System.out.println("Tie pile: " + tiePile.size() + " cards");
        System.out.println("-------------------------\n");
    }

    public List<Player> getActivePlayers() {
        List<Player> active = new ArrayList<>();
        for (Player player : players) {
            if (player.isActive()) {
                active.add(player);
            }
        }
        return active;
    }

    public Player getWinner() {
        List<Player> activePlayers = getActivePlayers();
        if (activePlayers.size() == 1) {
            return activePlayers.get(0);
        }
        return null;
    }

    public void setGameState(GameState newState) {
        System.out.println("Game state changing: " + gameState.get() + " -> " + newState);
        gameState.set(newState);
    }

    // Getters
    public List<Player> getPlayers() { return players; }
    public Player getCurrentPlayer() { return currentPlayer.get(); }
    public ObjectProperty<Player> currentPlayerProperty() { return currentPlayer; }
    public GameState getGameState() { return gameState.get(); }
    public ObjectProperty<GameState> gameStateProperty() { return gameState; }
    public Round getCurrentRound() { return currentRound; }
}