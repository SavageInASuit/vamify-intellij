package me.tomsavage.vamify;

import com.intellij.util.messages.Topic;

public interface GameWindowUpdateNotifier {
    Topic<GameWindowUpdateNotifier> TOPIC = Topic.create("GameWindowUpdate", GameWindowUpdateNotifier.class);

    void updateGameWindow();
}
