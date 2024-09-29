package me.tomsavage.vamify;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.intellij.util.messages.MessageBus;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.Arrays;

public class World {

    public ArrayList<Entity> world;

    public final ArrayList<String> EMPTY_TILES = new ArrayList<>(Arrays.asList(".", ",", "_"));

    public final ArrayList<String> ENEMIES = new ArrayList<>(
            Arrays.asList("🐖", "🦁", "🦛", "👻", "🤡", "🤖", "👹", "👽", "💀"));

    public @Nullable Integer currentEnemyHealth = null;

    private final Player player;

    public World(Player player) {
        this.player = player;

        this.world = new ArrayList<>();
    }

    public String getWorldRendered() {
        StringBuilder worldRendered = new StringBuilder();
        String PLAYER = "🧍";
        worldRendered.append(PLAYER);
        for (Entity entity : world) {
            worldRendered.append(entity.symbol);
        }
        return worldRendered.toString();
    }

    public String getNextTile() {
        return world.get(0).symbol;
    }

    public Entity getNextEntity() {
        return world.get(0);
    }

    public @Nullable Integer getCurrentEnemyHealthIfApplicable() {
        Entity nextTile = world.get(0);
        return nextTile instanceof Enemy ? ((Enemy) nextTile).health : null;
    }

    public void setWorld(ArrayList<String> world) {
        this.world = new ArrayList<>();
        for (String worldEntity : world) {
            this.world.add(parseStringToEntityForState(worldEntity));
        }
        currentEnemyHealth = getCurrentEnemyHealthIfApplicable();
    }

    private Entity parseStringToEntityForState(String worldEntity) {
        if (worldEntity.startsWith(Enemy.ENTITY_PREFIX)) {
            int ID = Integer.parseInt(worldEntity.substring(Enemy.ENTITY_PREFIX.length()));
            EnemyType type = EnemyType.getByID(ID);
            assert type != null;
            return new Enemy(type);
        } else {
            return new EmptyEntity(); // Integer.parseInt(worldEntity.substring(EmptyEntity.getIDPrefix().length())));
        }
    }

    private String convertEntityToStringForState(Entity entity) {
        if (entity instanceof EmptyEntity) {
            return EmptyEntity.ENTITY_PREFIX + entity.ID;
        } else if (entity instanceof Enemy) {
            return Enemy.ENTITY_PREFIX + entity.ID;
        } else {
            throw new IllegalArgumentException("Entity must be an instance of EmptyEntity or Enemy");
        }
    }

    private Enemy getRandomEnemy() {
        EnemyType enemyType = EnemyType.values()[(int) (Math.random() * EnemyType.values().length)];
        return new Enemy(enemyType);
    }

    private Entity generateNextTile() {
        Entity nextTile;
        if (Math.random() > getEnemySpawnProbability()) {
            // for empty tiles just want to return a random empty tile
            nextTile = new EmptyEntity();
        } else {
            // for enemies just want to return the index of the enemy
            nextTile = getRandomEnemy();
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
        if (force) {
            getWorldForState();
        }
        final int WORLD_LENGTH = 20;
        ArrayList<Entity> world = new ArrayList<>();
        for (int i = 0; i < WORLD_LENGTH; i++) {
            world.add(generateNextTile());
        }
        this.world = world;
    }

    public void takeStep(@NotNull Project project) {
        player.stepScore();

        Entity nextEntity = world.get(0);
        // Check if nextTile is empty
        if (enemyEncountered()) {
            Enemy enemy = (Enemy) nextEntity;
            currentEnemyHealth = enemy.health;
            enemy.takeTurn(player);
            if (enemy.shouldDespawn()) {
                currentEnemyHealth = null;
                handleMoveRight();
            }
        } else if (nextTileIsEmpty()) {
            // Move the world one character to the left
            handleMoveRight();
        }

        if (player.health <= 0) {
            generateWorld(true);
            player.restart();
            MessageBus messageBus = project.getMessageBus();
            messageBus.syncPublisher(GameWindowUpdateNotifier.GAME_OVER_TOPIC).updateGameWindow();
        } else {
            player.levelUpIfNeeded();
        }
    }

    private void handleMoveRight() {
        world.remove(0);
        world.add(generateNextTile());
        Entity nextEntity = world.get(0);
        if (nextEntity instanceof Enemy) {
            currentEnemyHealth = ((Enemy) nextEntity).health;
        }
    }

    public boolean enemyEncountered() {
        return world.get(0) instanceof Enemy;
    }

    private boolean nextTileIsEmpty() {
        return EMPTY_TILES.contains(getNextTile());
    }

    public ArrayList<String> getWorldForState() {
        ArrayList<String> worldForState = new ArrayList<>();
        for (Entity entity : world) {
            worldForState.add(convertEntityToStringForState(entity));
        }
        return worldForState;
    }
}