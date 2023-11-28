package me.tomsavage.vamify;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Service
@State(name = "myState", storages = {@Storage("VamifyGameState.xml")})
public final class VamifyProgress implements PersistentStateComponent<VamifyProgress.State> {
    public static class State {
        public int score = 0;
        public int level = 1;
        public int health = 100;
        public ArrayList<String> world;
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
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
        player = new Player(myState.score, myState.level, myState.health);

        world = new World(player);
        world.setWorld(state.world);
    }
}
