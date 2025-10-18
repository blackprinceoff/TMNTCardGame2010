package org.author.demo.tmntcardgame.model.cards;

public class CardCharacteristics {
    private int strength;      // Сила
    private int agility;       // Ловкість
    private int mastery;       // Майстерність
    private int cunning;       // Смекалка

    // Для тимчасових бонусів під час раунду
    private int tempStrength = 0;
    private int tempAgility = 0;
    private int tempMastery = 0;
    private int tempCunning = 0;

    public CardCharacteristics(int strength, int agility, int mastery, int cunning) {
        this.strength = strength;
        this.agility = agility;
        this.mastery = mastery;
        this.cunning = cunning;
    }

    public int getValueByType(String type) {
        String normalizedType = type.toLowerCase().trim();

        int value;
        switch (normalizedType) {
            case "strength":
            case "сила":
                value = getTotalStrength();
                break;
            case "agility":
            case "ловкість":
                value = getTotalAgility();
                break;
            case "mastery":
            case "майстерність":
                value = getTotalMastery();
                break;
            case "cunning":
            case "смекалка":
                value = getTotalCunning();
                break;
            default:
                System.err.println("WARNING: Unknown characteristic type: '" + type + "'");
                value = 0;
                break;
        }

        System.out.println("        getValueByType('" + type + "') = " + value +
                " (base: " + getBaseValue(normalizedType) + ", temp: " + getTempValue(normalizedType) + ")");
        return value;
    }

    private int getBaseValue(String type) {
        switch (type) {
            case "strength":
            case "сила":
                return strength;
            case "agility":
            case "ловкість":
                return agility;
            case "mastery":
            case "майстерність":
                return mastery;
            case "cunning":
            case "смекалка":
                return cunning;
            default:
                return 0;
        }
    }

    private int getTempValue(String type) {
        switch (type) {
            case "strength":
            case "сила":
                return tempStrength;
            case "agility":
            case "ловкість":
                return tempAgility;
            case "mastery":
            case "майстерність":
                return tempMastery;
            case "cunning":
            case "смекалка":
                return tempCunning;
            default:
                return 0;
        }
    }

    public void applyTempBonus(String characteristic, int value) {
        System.out.println("      Applying bonus +" + value + " to " + characteristic);

        switch (characteristic.toLowerCase().trim()) {
            case "strength":
            case "сила":
                tempStrength += value;
                System.out.println("        Strength: " + strength + " + " + tempStrength + " = " + getTotalStrength());
                break;
            case "agility":
            case "ловkість":
                tempAgility += value;
                System.out.println("        Agility: " + agility + " + " + tempAgility + " = " + getTotalAgility());
                break;
            case "mastery":
            case "майстерність":
                tempMastery += value;
                System.out.println("        Mastery: " + mastery + " + " + tempMastery + " = " + getTotalMastery());
                break;
            case "cunning":
            case "смекалка":
                tempCunning += value;
                System.out.println("        Cunning: " + cunning + " + " + tempCunning + " = " + getTotalCunning());
                break;
        }
    }

    public void multiplyCharacteristics(String characteristic, int multiplier) {
        System.out.println("      Multiplying " + characteristic + " by " + multiplier);

        switch (characteristic.toLowerCase().trim()) {
            case "strength":
            case "сила":
                tempStrength = strength * (multiplier - 1);
                System.out.println("        Strength: " + strength + " * " + multiplier + " = " + getTotalStrength());
                break;
            case "agility":
            case "ловкість":
                tempAgility = agility * (multiplier - 1);
                System.out.println("        Agility: " + agility + " * " + multiplier + " = " + getTotalAgility());
                break;
            case "mastery":
            case "майстерність":
                tempMastery = mastery * (multiplier - 1);
                System.out.println("        Mastery: " + mastery + " * " + multiplier + " = " + getTotalMastery());
                break;
            case "cunning":
            case "смекалка":
                tempCunning = cunning * (multiplier - 1);
                System.out.println("        Cunning: " + cunning + " * " + multiplier + " = " + getTotalCunning());
                break;
        }
    }

    public void resetTempBonuses() {
        System.out.println("      Resetting temp bonuses");
        tempStrength = 0;
        tempAgility = 0;
        tempMastery = 0;
        tempCunning = 0;
    }

    // Getters з урахуванням тимчасових бонусів
    public int getTotalStrength() { return strength + tempStrength; }
    public int getTotalAgility() { return agility + tempAgility; }
    public int getTotalMastery() { return mastery + tempMastery; }
    public int getTotalCunning() { return cunning + tempCunning; }

    // Базові getters
    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public int getMastery() { return mastery; }
    public int getCunning() { return cunning; }
}