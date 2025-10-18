package org.author.demo.tmntcardgame.model.cards;

public enum CardType {
    HERO("Герой"),
    HEROINE("Героїня"),
    CREATURE("Істота"),
    VILLAIN("Злодій"),
    VILLAINESS("Злодійка"),
    ROBOT("Робот"),
    PURPLE_DRAGON("Пурпурний дракон");


    private final String displayName;

    CardType(String displayName)
    {this.displayName=displayName;}

    public String getDisplayName()
    {return displayName;}
}


