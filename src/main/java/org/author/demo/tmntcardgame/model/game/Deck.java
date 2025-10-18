package org.author.demo.tmntcardgame.model.game;

import org.author.demo.tmntcardgame.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private Stack<Card> cards;

    public Deck() {
        this.cards = new Stack<>();
    }

    public Deck(List<Card> cardList) {
        this.cards = new Stack<>();
        this.cards.addAll(cardList);
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.pop();
        }
        return null;
    }

    public Card peekTopCard() {
        if (!cards.isEmpty()) {
            return cards.peek();
        }
        return null;
    }

    public void addCard(Card card) {
        cards.push(card);
    }

    public void addCardsToBottom(List<Card> newCards) {
        cards.addAll(0, newCards);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getAllCards() {
        return new ArrayList<>(cards);
    }

}
