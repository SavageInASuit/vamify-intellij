package me.tomsavage.vamify;

public class Enemy extends Entity {
    public int health;

    public int hitPoints;

    public static String ENTITY_PREFIX = "Enemy";

    public EnemyType type;

    public Enemy(EnemyType type) {
        this(type.ID, type.name, type.symbol, type.health, type.hitPoints);
        this.type = type;
    }

    private Enemy(int ID, String name, String symbol, int health, int hitPoints) {
        super(ID, name, symbol);

        this.health = health;
        this.hitPoints = hitPoints;
    }

    @Override
    public void takeTurn(Player player) {
        takeDamage(player.getHitPoints());
        int damage = getHitPoints();
        player.health -= damage;
    }

    public boolean shouldDespawn() {
        return health <= 0;
    }

    private void takeDamage(int damage) {
        health -= damage;
    }

    private int getHitPoints() {
        return (int) (hitPoints * Math.random());
    }
}
