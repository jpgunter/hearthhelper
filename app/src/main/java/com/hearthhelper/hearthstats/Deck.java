package com.hearthhelper.hearthstats;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Model object to represent a hearthstone deck.
 */
@Value
@AllArgsConstructor(suppressConstructorProperties = true)
public class Deck {
    private static final List<Deck> allDecks = Arrays.asList(
            new Deck(-1, 1, "Druid"),
            new Deck(-1, 2, "Hunter"),
            new Deck(-1, 3, "Mage"),
            new Deck(-1, 4, "Paladin"),
            new Deck(-1, 5, "Priest"),
            new Deck(-1, 6, "Rogue"),
            new Deck(-1, 7, "Shaman"),
            new Deck(-1, 8, "Warlock"),
            new Deck(-1, 9, "Warrior")
    );
    private static final Map<Integer, Deck> classToDeck;

    static {
        classToDeck = new HashMap<>();
        for (Deck d : allDecks) {
            classToDeck.put(d.getClassId(), d);
        }
    }


    private final int id;
    private final int classId;
    private final String name;

    public String getClassName() {
        return classToDeck.get(classId).getName();
    }

    public String toString() {
        if (id == -1) {
            return name;
        }
        return String.format("[%s] %s", getClassName(), name);
    }

    public String toJson() {
        return "{id:" + id + ", class_id:" + classId + ", name:\"" + name + "\"}";
    }

    public static List<Deck> opponentDecks() {
        return Collections.unmodifiableList(allDecks);
    }

    public static Comparator<Deck> classThenNameComparator() {
        return new Comparator<Deck>() {
            @Override
            public int compare(Deck lhs, Deck rhs) {
                return lhs.toString().compareTo(rhs.toString());
            }
        };
    }
}
