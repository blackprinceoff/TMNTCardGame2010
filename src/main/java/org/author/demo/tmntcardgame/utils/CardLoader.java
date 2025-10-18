package org.author.demo.tmntcardgame.utils;

import com.google.gson.*;
import org.author.demo.tmntcardgame.model.cards.*;
import java.io.*;
import java.util.*;

public class CardLoader {
    private static final String CARDS_DATA_PATH = "/data/cards_data.json";

    public static List<Card> loadAllCards() {
        List<Card> cards = new ArrayList<>();

        try {
            InputStream is = CardLoader.class.getResourceAsStream(CARDS_DATA_PATH);
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Gson gson = new Gson();
                CardsData data = gson.fromJson(reader, CardsData.class);

                if (data != null && data.battleCards != null) {
                    for (BattleCardData battleData : data.battleCards) {
                        CardCharacteristics chars = new CardCharacteristics(
                                battleData.characteristics.strength,
                                battleData.characteristics.agility,
                                battleData.characteristics.mastery,
                                battleData.characteristics.cunning
                        );

                        // НЕ додаємо "/images/" якщо вже є в JSON
                        String imagePath = battleData.imagePath.startsWith("/") ?
                                battleData.imagePath : "/" + battleData.imagePath;

                        BattleCard card = new BattleCard(
                                battleData.id,
                                battleData.name,
                                imagePath,  // Використовуємо шлях як є
                                CardType.valueOf(battleData.type),
                                chars
                        );
                        cards.add(card);

                        System.out.println("Loaded card: " + battleData.name + " with image: " + imagePath);
                    }
                }

                if (data != null && data.bonusCards != null) {
                    for (BonusCardData bonusData : data.bonusCards) {
                        String imagePath = bonusData.imagePath.startsWith("/") ?
                                bonusData.imagePath : "/" + bonusData.imagePath;

                        BonusCard card = new BonusCard(
                                bonusData.id,
                                bonusData.name,
                                imagePath,
                                BonusType.valueOf(bonusData.type),
                                bonusData.value,
                                bonusData.targetCharacteristic
                        );
                        cards.add(card);
                    }
                }
                reader.close();
            } else {
                System.out.println("cards_data.json not found, creating test cards");
                cards = createTestCards();
            }
        } catch (Exception e) {
            e.printStackTrace();
            cards = createTestCards();
        }

        return cards;
    }

    private static List<Card> createTestCards() {
        List<Card> cards = new ArrayList<>();

        // Створюємо CardCharacteristics для кожної карти
        CardCharacteristics heroChars = new CardCharacteristics(8000, 6000, 7000, 5000);
        CardCharacteristics villainChars = new CardCharacteristics(7500, 8000, 6500, 7000);
        CardCharacteristics creatureChars = new CardCharacteristics(9000, 5500, 6000, 6500);
        CardCharacteristics robotChars = new CardCharacteristics(8500, 4200, 9500, 6800);
        CardCharacteristics dragonChars = new CardCharacteristics(9900, 7100, 8800, 8400);

        // Тестові бойові карти
        cards.add(new BattleCard("test_hero_1", "Тестовий Герой 1",
                "/images/cards/battle/hero_1.png", CardType.HERO, heroChars));

        cards.add(new BattleCard("test_hero_2", "Тестовий Герой 2",
                "/images/cards/battle/hero_2.png", CardType.HERO,
                new CardCharacteristics(7800, 6200, 7500, 5100)));

        cards.add(new BattleCard("test_villain_1", "Тестовий Злодій 1",
                "/images/cards/battle/villain_1.png", CardType.VILLAIN, villainChars));

        cards.add(new BattleCard("test_villain_2", "Тестовий Злодій 2",
                "/images/cards/battle/villain_2.png", CardType.VILLAIN,
                new CardCharacteristics(7200, 8300, 6200, 7300)));

        cards.add(new BattleCard("test_creature_1", "Тестова Істота 1",
                "/images/cards/battle/creature_1.png", CardType.CREATURE, creatureChars));

        cards.add(new BattleCard("test_creature_2", "Тестова Істота 2",
                "/images/cards/battle/creature_2.png", CardType.CREATURE,
                new CardCharacteristics(8800, 5800, 6300, 6200)));

        cards.add(new BattleCard("test_robot_1", "Тестовий Робот 1",
                "/images/cards/battle/robot_1.png", CardType.ROBOT, robotChars));

        cards.add(new BattleCard("test_robot_2", "Тестовий Робот 2",
                "/images/cards/battle/robot_2.png", CardType.ROBOT,
                new CardCharacteristics(8200, 4500, 9200, 7000)));

        cards.add(new BattleCard("test_dragon_1", "Пурпурний Дракон",
                "/images/cards/battle/dragon_1.png", CardType.PURPLE_DRAGON, dragonChars));

        // Тестові бонусні карти
        cards.add(new BonusCard("test_bonus_1", "+400 до сили",
                "/images/cards/bonus/bonus_strength.png",
                BonusType.ADD_STRENGTH, 400, "strength"));

        cards.add(new BonusCard("test_bonus_2", "+300 до ловкості",
                "/images/cards/bonus/bonus_agility.png",
                BonusType.ADD_AGILITY, 300, "agility"));

        cards.add(new BonusCard("test_bonus_3", "Подвоєння майстерності",
                "/images/cards/bonus/bonus_mastery_x2.png",
                BonusType.MULTIPLY_MASTERY, 2, "mastery"));

        cards.add(new BonusCard("test_bonus_4", "+500 до смекалки",
                "/images/cards/bonus/bonus_cunning.png",
                BonusType.ADD_CUNNING, 500, "cunning"));

        cards.add(new BonusCard("test_bonus_5", "Подвоєння сили",
                "/images/cards/bonus/bonus_strength_x2.png",
                BonusType.MULTIPLY_STRENGTH, 2, "strength"));

        // Додамо більше карт для повноцінної гри
        for (int i = 0; i < 20; i++) {
            CardType type = CardType.values()[i % CardType.values().length];
            int baseValue = 5000 + (i * 200);

            cards.add(new BattleCard(
                    "test_card_" + i,
                    "Карта " + (i + 1),
                    "/images/cards/battle/default.png",
                    type,
                    new CardCharacteristics(
                            baseValue + (int)(Math.random() * 2000),
                            baseValue + (int)(Math.random() * 2000),
                            baseValue + (int)(Math.random() * 2000),
                            baseValue + (int)(Math.random() * 2000)
                    )
            ));
        }

        return cards;
    }

    // Внутрішні класи для парсингу JSON
    private static class CardsData {
        List<BattleCardData> battleCards;
        List<BonusCardData> bonusCards;
    }

    private static class BattleCardData {
        String id;
        String name;
        String type;
        String imagePath;
        CharacteristicsData characteristics;
    }

    private static class BonusCardData {
        String id;
        String name;
        String type;
        String imagePath;
        int value;
        String targetCharacteristic;
    }

    private static class CharacteristicsData {
        int strength;
        int agility;
        int mastery;
        int cunning;
    }
}
