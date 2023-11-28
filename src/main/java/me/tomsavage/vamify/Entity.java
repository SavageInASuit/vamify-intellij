package me.tomsavage.vamify;

public abstract class Entity {
    public String name;
    public String symbol;
    public final int ID;
    public static String ENTITY_PREFIX = "Entity";

    public Entity(int ID, String name, String symbol) {
        this.ID = ID;
        this.name = name;
        this.symbol = symbol;
    }

    public boolean shouldDespawn() {
        return true;
    }

    public abstract void takeTurn(Player player);
}
