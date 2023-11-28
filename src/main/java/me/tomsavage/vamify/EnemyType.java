package me.tomsavage.vamify;

public enum EnemyType {
    PIG(0, "Pig", "🐖", 10, 1),
    LION(1, "Lion", "🦁", 15, 1),
    HIPPO(2, "Hippo", "🦛", 20, 1),
    GHOST(3, "Ghost", "👻", 25, 2),
    Clown(4, "Clown", "🤡", 40, 2),
    Devil(5, "Devil", "👹", 50, 3),
    ALIEN(6, "Alien", "👽", 60, 4),
    ROBOT(7, "Robot", "🤖", 100, 5),
    DEATH(8, "Death", "💀", 200, 6);

    public final int ID;
    public final String name;
    public final String symbol;
    public final int health;
    public final int hitPoints;

    EnemyType(int ID, String name, String symbol, int health, int hitPoints) {
        this.ID = ID;
        this.name = name;
        this.symbol = symbol;
        this.health = health;
        this.hitPoints = hitPoints;
    }

    public static EnemyType getByID(int ID) {
        for (EnemyType enemyType : EnemyType.values()) {
            if (enemyType.ID == ID) {
                return enemyType;
            }
        }
        return null;
    }
}
