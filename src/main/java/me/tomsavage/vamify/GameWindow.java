package me.tomsavage.vamify;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import com.intellij.util.messages.MessageBusConnection;
import java.util.logging.Logger;

public class GameWindow implements ToolWindowFactory {
    PlayingUI playingUI;
    GameOverUI gameOverUI;
    private boolean inPlayingState;

    public GameWindow() {
        playingUI = new PlayingUI();
        gameOverUI = new GameOverUI();
        inPlayingState = true;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // TODO: Show a game over screen when the player dies - maybe the player must
        // reset to continue playing?
        // TODO: Add a potion button that works once per level - half their health?
        // TODO: Add some max stats fields at the bottom
        // TODO: Add rogue-like elements - e.g. a random chance of a potion appearing on
        // the map
        // - Rebirths factor in to how much damage you do

        toolWindow.getContentManager().removeAllContents(false);
        if (inPlayingState) {
            toolWindow.getContentManager().addContent(playingUI.setup(project));
        } else {
            toolWindow.getContentManager().addContent(gameOverUI.setup(project));
        }

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GameWindowUpdateNotifier.GAME_OVER_TOPIC, (GameWindowUpdateNotifier) () -> {
            Logger.getLogger("GameWindow").info("Passed GAME_OVER_TOPIC in GameWindow");
            inPlayingState = !inPlayingState;

            toolWindow.getContentManager().removeAllContents(false);
            if (inPlayingState) {
                // TODO: Change this to getContentOrSetup so that we don't just recreate
                // everything!
                toolWindow.getContentManager().addContent(playingUI.setup(project));
            } else {
                toolWindow.getContentManager().addContent(gameOverUI.setup(project));
            }
        });
    }
}
