package me.tomsavage.vamify;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class World {

    private final String PLAYER = "🧍";

    public ArrayList<String> world;

    public final ArrayList<String> EMPTY_TILES = new ArrayList<>(Arrays.asList(".",",","_"));

    public final ArrayList<String> ENEMIES = new ArrayList<>(Arrays.asList( "🐖","🦁","🦛","👻","🤡","🤖","👹","👽","💀"));

    public final HashMap<String, Integer> ENEMY_HEALTH;

    public @Nullable Integer currentEnemyHealth = null;

    private final Player player;

    public World(Player player) {
        this.player = player;

        this.world = new ArrayList<>();

        ENEMY_HEALTH = new HashMap<>();
        ENEMY_HEALTH.put("🐖", 10);
        ENEMY_HEALTH.put("🦁", 15);
        ENEMY_HEALTH.put("🦛", 20);
        ENEMY_HEALTH.put("👻", 25);
        ENEMY_HEALTH.put("🤡", 40);
        ENEMY_HEALTH.put("👹", 50);
        ENEMY_HEALTH.put("👽", 60);
        ENEMY_HEALTH.put("🤖", 100);
        ENEMY_HEALTH.put("💀", 200);
    }

    public String getWorldRendered() {
        StringBuilder worldRendered = new StringBuilder();
        worldRendered.append(PLAYER);
        for (String entity : world) {
            worldRendered.append(getTileFromWorldEntity(entity));
        }
        return worldRendered.toString();
    }

    public String getNextTile() {
        return getTileFromWorldEntity(world.get(0));
    }

    public @Nullable Integer getCurrentEnemyHealthIfApplicable() {
        String nextTile = getTileFromWorldEntity(world.get(0));
        if (ENEMY_HEALTH.containsKey(nextTile)) {
            return ENEMY_HEALTH.get(nextTile);
        }
        return null;
    }

    public void setWorld(ArrayList<String> world) {
        this.world = world;
        currentEnemyHealth = getCurrentEnemyHealthIfApplicable();
    }

    private String getTileFromWorldEntity(String worldEntity) {
        try {
            // try to parse the tile to an integer
            int emojiIndex = Integer.parseInt(worldEntity);
            return ENEMIES.get(emojiIndex);
        } catch (NumberFormatException e) {
            // if it's not an integer, just add it to the string
            return worldEntity;
        }
    }

    private String generateNextTile() {
        String nextTile;
        if (Math.random() > getEnemySpawnProbability()) {
            nextTile = EMPTY_TILES.get((int) (Math.random() * EMPTY_TILES.size()));
        } else {
            // for enemies just want to return the index of the enemy
            nextTile = String.valueOf((int) (Math.random() * ENEMIES.size()));
        }

        return nextTile;
    }

    private float getEnemySpawnProbability() {
        final float CONSTANT = 100.0f; // Adjust this value to control the rate of increase
        float probability = (float) (1 - Math.exp(-player.level / CONSTANT));
        return Math.min(probability, 0.6f);
    }

    public void generateWorld() {
        generateWorld(false);
    }
    public void generateWorld(boolean force) {
        if (!force && world != null && !world.isEmpty()) {
            return;
        }
        final int WORLD_LENGTH = 20;
        ArrayList<String> world = new ArrayList<>();
        for (int i = 0; i < WORLD_LENGTH; i++) {
            world.add(generateNextTile());
        }
        this.world = world;
    }

    public void takeStep() {
        int damagePoints = player.getHitPoints();
        player.stepScore();

        String nextTile = getTileFromWorldEntity(world.get(0));
        // Check if nextTile is empty
        if (EMPTY_TILES.contains(nextTile)) {
            // Move the world one character to the left
            world.remove(0);
            world.add(generateNextTile());
            nextTile = getTileFromWorldEntity(world.get(0));
            if (ENEMY_HEALTH.containsKey(nextTile)) {
                currentEnemyHealth = ENEMY_HEALTH.get(nextTile);
            }
        } else if (ENEMY_HEALTH.containsKey(nextTile)) {
            if (currentEnemyHealth == null) {
                currentEnemyHealth = ENEMY_HEALTH.get(nextTile);
            }
            // Do damage to the enemy
            currentEnemyHealth -= damagePoints;

            if (currentEnemyHealth <= 0) {
                // Remove the enemy from the world and move the world one character to the left
                world.remove(0);
                world.add(generateNextTile());
                currentEnemyHealth = null;
                nextTile = getTileFromWorldEntity(world.get(0));
                if (ENEMY_HEALTH.containsKey(nextTile)) {
                    currentEnemyHealth = ENEMY_HEALTH.get(nextTile);
                }
            } else {
                // enemy attacks - this is okay for now, but will need tweaking
                player.health -= (int) (Math.random() * ENEMY_HEALTH.get(nextTile) * 0.02);
            }
        }

        if (player.health <= 0) {
            generateWorld(true);
            player.restart();
            // TODO: Fire an event that can be picked up by the UI
        } else {
            player.levelUpIfNeeded();
        }
    }
}