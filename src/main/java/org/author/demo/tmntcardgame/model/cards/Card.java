package org.author.demo.tmntcardgame.model.cards;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Card {
    protected String id;
    protected StringProperty name;
    protected String imagePath;
    protected String backImagePath = "/images/cards/back/card_back.jpg";
    protected BooleanProperty isFaceUp;

    public Card(String id, String name, String imagePath) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.imagePath = imagePath;
        this.isFaceUp = new SimpleBooleanProperty(false);
    }

    public abstract String getCardInfo();

    public void flip(){
        isFaceUp.set(!isFaceUp.get());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getBackImagePath() {
        return backImagePath;
    }

    public boolean isFaceUp() {
        return isFaceUp.get();
    }

    public BooleanProperty faceUpProperty() {
        return isFaceUp;
    }

    @Override
    public String toString() {
        return getName();
    }
}
