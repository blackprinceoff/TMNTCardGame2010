package org.author.demo.tmntcardgame.model.cards;

public enum BonusType {
    ADD_STRENGTH("Додати до сили"),
    ADD_AGILITY("Додати до спритності"),
    ADD_MASTERY("Додати до майстерності"),
    ADD_CUNNING("Додати до хитрості"),
    MULTIPLY_STRENGTH("Подвоїти силу"),
    MULTIPLY_AGILITY("Подвоїти спритність"),
    MULTIPLY_MASTERY("Подвоїти майстерність"),
    MULTIPLY_CUNNING("Подвоїти хитрість"),
    DODGE_ATTACK("Ухилення від атаки"),
    STEAL_BONUS("Вкрасти бонус");

    private final String description;

    BonusType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
