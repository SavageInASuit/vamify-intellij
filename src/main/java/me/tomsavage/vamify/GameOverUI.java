package me.tomsavage.vamify;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.Content;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import com.intellij.util.ui.JBUI;
import com.intellij.ui.JBColor;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import com.intellij.util.messages.MessageBus;
import com.intellij.openapi.project.Project;

public class GameOverUI {
    private JLabel gameOverLabel;

    public void update() {

    }

    public Content setup(@NotNull Project project) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(JBUI.Borders.empty(10));

        Font largeFont = new Font("Arial", Font.BOLD, 50);
        gameOverLabel = new JLabel();
        gameOverLabel.setFont(largeFont);
        gameOverLabel.setForeground(JBColor.RED);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameOverLabel.setText("GAME OVER");
        panel.add(gameOverLabel);

        JButton resetButton = new JButton("Restart!");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> {
            VamifyProgress progress = ApplicationManager.getApplication().getService(VamifyProgress.class);
            progress.resetProgress();
            MessageBus messageBus = project.getMessageBus();
            messageBus.syncPublisher(GameWindowUpdateNotifier.GAME_OVER_TOPIC).updateGameWindow();
        });
        panel.add(resetButton);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, null, false);
        return content;
    }
}
