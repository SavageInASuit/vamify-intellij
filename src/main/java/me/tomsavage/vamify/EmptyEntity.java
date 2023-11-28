package me.tomsavage.vamify;

import java.util.ArrayList;
import java.util.Arrays;

public class EmptyEntity extends Entity {
    private final ArrayList<String> EMPTY_TILES = new ArrayList<>(Arrays.asList(".",",","_"));

    public static String ENTITY_PREFIX = "Empty";

    public EmptyEntity() {
        super(0, "Empty", ".");
        this.symbol = EMPTY_TILES.get((int) (Math.random() * EMPTY_TILES.size()));
    }

    @Override
    public void takeTurn(Player player) {
        return;
    }
}
