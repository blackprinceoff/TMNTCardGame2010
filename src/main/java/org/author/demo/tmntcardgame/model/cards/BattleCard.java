package org.author.demo.tmntcardgame.model.cards;

public class BattleCard extends Card {
    private CardType type;
    private CardCharacteristics characteristics;

    public BattleCard(String id, String name, String imagePath, CardType type,
                      CardCharacteristics characteristics) {
        super(id, name, imagePath);
        this.type = type;
        this.characteristics = characteristics;
    }

    @Override
    public String getCardInfo() {
        return String.format("%s (%s)\nСила: %d\nЛовкість: %d\nМайстерність: %d\nСмекалка: %d",
                getName(), type.getDisplayName(),
                characteristics.getTotalStrength(),
                characteristics.getTotalAgility(),
                characteristics.getTotalMastery(),
                characteristics.getTotalCunning());
    }

    public CardType getType() { return type; }
    public CardCharacteristics getCharacteristics() { return characteristics; }
}