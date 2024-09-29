package me.tomsavage.vamify;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Service
@State(name = "myState", storages = { @Storage("VamifyGameState.xml") })
public final class VamifyProgress implements PersistentStateComponent<VamifyProgress.State> {
    public static class State {
        public int score = 0;
        public int level = 1;
        public int health = 100;
        public ArrayList<String> world;

        public int highScore = 0;
        public int highLevel = 1;
    }

    private State myState = new State();

    public World world;

    public Player player;

    public VamifyProgress() {
        player = new Player();
        world = new World(player);
    }

    public static VamifyProgress getInstance() {
        return ApplicationManager.getApplication().getService(VamifyProgress.class);
    }

    public void resetProgress() {
        player.level = 1;
        player.score = 0;
        player.health = player.maxHealthForLevel(myState.level);
        world.generateWorld(true);
    }

    @Override
    public @Nullable State getState() {
        myState.health = player.health;
        myState.level = player.level;
        myState.score = player.score;
        myState.world = world.getWorldForState();

        myState.highScore = player.highScore;
        myState.highLevel = player.highLevel;
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
        player = new Player(myState.score, myState.level, myState.health, myState.highScore, myState.highLevel);

        world = new World(player);
        if (state.world.size() > 0) {
            world.setWorld(state.world);
        } else {
            world.generateWorld();
        }
    }
}
