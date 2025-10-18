package org.author.demo.tmntcardgame.model.cards;

public class BonusCard extends Card {
    private BonusType bonusType;
    private int bonusValue;
    private String targetCharacteristic;

    public BonusCard(String id, String name, String imagePath, BonusType bonusType, int bonusValue, String targetCharacteristic) {
        super(id, name, imagePath);
        this.bonusType = bonusType;
        this.bonusValue = bonusValue;
        this.targetCharacteristic = targetCharacteristic;
    }

    public void applyBonus(BattleCard targetCard) {
        CardCharacteristics chars = targetCard.getCharacteristics();

        switch (bonusType){
            case ADD_STRENGTH:
                chars.applyTempBonus("strength", bonusValue);
                break;
            case ADD_AGILITY:
                chars.applyTempBonus("agility", bonusValue);
                break;
            case ADD_MASTERY:
                chars.applyTempBonus("mastery", bonusValue);
                break;
            case ADD_CUNNING:
                chars.applyTempBonus("cunning", bonusValue);
                break;
            case MULTIPLY_STRENGTH:
                chars.multiplyCharacteristics("strength", bonusValue);
                break;
            case MULTIPLY_AGILITY:
                chars.multiplyCharacteristics("agility", bonusValue);
                break;
            case MULTIPLY_MASTERY:
                chars.multiplyCharacteristics("mastery", bonusValue);
                break;
            case MULTIPLY_CUNNING:
                chars.multiplyCharacteristics("cunning", bonusValue);
                break;
        }
    }

    @Override
    public String getCardInfo() {
        return String.format("%s\n%s: %d", getName(), bonusType.getDescription(), bonusValue);
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public int getBonusValue() {
        return bonusValue;
    }

    public String getTargetCharacteristic() {
        return targetCharacteristic;
    }
}
