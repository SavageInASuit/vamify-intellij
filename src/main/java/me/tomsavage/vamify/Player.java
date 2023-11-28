package me.tomsavage.vamify;

public class Player {
    public int score;
    public int level;
    public int health;

    public Player() {
        this(0, 1, 100);
    }

    public Player(int score, int level, int health) {
        this.score = score;
        this.level = level;
        this.health = health;
    }

    public int maxHealthForLevel(int level) {
        return 100 + ((level - 1) * 10);
    }

    public int scoreForLevel(int level) {
        double DIFFICULTY_RAMP = 1.5;
        double LEVEL_MULTIPLIER = 100;
        return (int) (LEVEL_MULTIPLIER * Math.pow((double) level, DIFFICULTY_RAMP));
    }

    public int getHitPoints() {
        return getScoreIncrement();
    }

    public void stepScore() {
        score += getScoreIncrement();
    }

    public void restart() {
        score = 0;
        level = 1;
        health = maxHealthForLevel(level);
    }

    public boolean levelUpIfNeeded() {
        if (needsLevelUp()) {
            incrementLevel();
            health = maxHealthForLevel(level);
            return true;
        }
        return false;
    }

    private boolean needsLevelUp() {
        return score > scoreForLevel(level);
    }

    private int getScoreIncrement() {
        // TODO: Make this more interesting
        return (int) (level * Math.random()) + 1;
    }

    private void incrementLevel() {
        level++;
    }
}
