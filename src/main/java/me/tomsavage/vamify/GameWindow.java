package me.tomsavage.vamify;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.util.logging.Logger;

public class GameWindow implements ToolWindowFactory {

    private JLabel levelLabel;
    private JLabel scoreLabel;
    private JLabel enemyHealthLabel;
    private JLabel worldViewLabel;
    private JLabel currentTile;
    private JLabel healthText;

    private JProgressBar levelProgressBar;
    private JProgressBar playerHealthProgressBar;
    private JProgressBar enemyHealthProgressBar;

    private void updateWindow() {
        VamifyProgress progress = ApplicationManager.getApplication().getService(VamifyProgress.class);
        if (progress.getState() != null) {
            levelLabel.setText("Level " + progress.getState().level);
            scoreLabel.setText("Score " + progress.getState().score);

            worldViewLabel.setText(progress.world.getWorldRendered());

            // TODO: Don't update the min and max if we haven't levelled up etc.
            levelProgressBar.setMinimum(progress.player.scoreForLevel(progress.getState().level - 1));
            levelProgressBar.setMaximum(progress.player.scoreForLevel(progress.getState().level));
            levelProgressBar.setValue(progress.getState().score);

            playerHealthProgressBar.setMaximum(progress.player.maxHealthForLevel(progress.getState().level));
            playerHealthProgressBar.setValue(progress.getState().health);
            healthText.setText(progress.getState().health + "/" + progress.player.maxHealthForLevel(progress.getState().level));

            String nextTile = progress.world.getNextTile();
            if (progress.world.ENEMY_HEALTH.containsKey(nextTile)) {
                Logger.getLogger("GameWindow").info("nextTile: '" + nextTile + "' is an enemy");
                currentTile.setText(nextTile);
                enemyHealthLabel.setText(progress.world.currentEnemyHealth + "/" + progress.world.ENEMY_HEALTH.get(nextTile));
                enemyHealthLabel.setForeground(JBColor.RED);

                enemyHealthProgressBar.setMaximum(progress.world.ENEMY_HEALTH.get(nextTile));
                enemyHealthProgressBar.setValue(progress.world.currentEnemyHealth);
                enemyHealthProgressBar.setBackground(JBColor.RED);
            } else {
                Logger.getLogger("GameWindow").info("nextTile: '" + nextTile + "' is NOT an enemy");
                currentTile.setText(" ");
                enemyHealthLabel.setText("Safe");
                enemyHealthLabel.setForeground(JBColor.GRAY);

                enemyHealthProgressBar.setValue(0);
                enemyHealthProgressBar.setBackground(JBColor.GRAY);
            }
        }
    }

    public void wobbleCurrentTile() {
        int wobbleDuration = 500; // duration of the wobble effect in milliseconds
        int wobbleSpeed = 50; // speed of each wobble movement in milliseconds
        int wobbleDistance = 10; // distance of each wobble movement in pixels

        Timer timer = new Timer(wobbleSpeed, null);
        timer.addActionListener(e -> {
            Point location = currentTile.getLocation();
            int yChange = (int) ((wobbleDistance / 2) - (Math.random() * wobbleDistance));
            if (timer.getDelay() == wobbleSpeed) {
                // randomly change the y location based on wobbleDistance
                currentTile.setLocation(location.x + wobbleDistance, location.y + yChange);
                timer.setDelay(wobbleDuration - wobbleSpeed);
            } else {
                currentTile.setLocation(location.x - wobbleDistance, location.y + yChange);
                timer.stop();
            }
        });
        timer.start();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // TODO: Show a game over screen when the player dies - maybe the player must reset to continue playing?
        // TODO: Add a potion button that works once per level - half their health?
        // TODO: Add some max stats fields at the bottom
        // TODO: Add rogue-like elements - e.g. a random chance of a potion appearing on the map
        //  - Rebirths factor in to how much damage you do
        // TODO: REFACTOR ME!

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(JBUI.Borders.empty(10));

        levelLabel = new JLabel();
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel = new JLabel();
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Font largeFont = new Font("Arial", Font.BOLD, 50);
        Font lessLargeFont = new Font("Arial", Font.BOLD, 48);
        Font mediumFont = new Font("Arial", Font.PLAIN, 18);
        enemyHealthLabel = new JLabel();
        enemyHealthLabel.setFont(lessLargeFont);
        enemyHealthLabel.setForeground(JBColor.GRAY);
        enemyHealthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        worldViewLabel = new JLabel();
        worldViewLabel.setFont(mediumFont);
        worldViewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentTile = new JLabel();
        currentTile.setFont(largeFont);
        currentTile.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Extract this to its own class and use a custom UI to make the progress bar look nicer
        levelProgressBar = new JProgressBar() {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicProgressBarUI() {
                    @Override
                    protected void paintDeterminate(Graphics g, JComponent c) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(JBColor.BLUE); // Set the color of the filled part
                        int width = (int) (getPercentComplete() * c.getWidth());
                        g2d.fillRect(0, 0, width, c.getHeight());
                    }
                });
            }
        };
        levelProgressBar.setMaximum(100);
        levelProgressBar.setValue(70);
        levelProgressBar.setStringPainted(true);
        levelProgressBar.setForeground(JBColor.BLUE);
        levelProgressBar.setBackground(JBColor.GRAY);
        levelProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(levelLabel);
        panel.add(levelProgressBar);
        levelProgressBar.setPreferredSize(new Dimension(100, 10));

        playerHealthProgressBar = new JProgressBar() {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicProgressBarUI() {
                    @Override
                    protected void paintDeterminate(Graphics g, JComponent c) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(JBColor.GREEN); // Set the color of the filled part
                        int width = (int) (getPercentComplete() * c.getWidth());
                        g2d.fillRect(0, 0, width, c.getHeight());
                    }
                });
            }
        };
        playerHealthProgressBar.setMaximum(100);
        playerHealthProgressBar.setValue(70);
        playerHealthProgressBar.setStringPainted(true);
        playerHealthProgressBar.setForeground(JBColor.GREEN);
        playerHealthProgressBar.setBackground(JBColor.RED);
        playerHealthProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel healthLabel = new JLabel();
        healthLabel.setText("Health");
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(healthLabel);
        healthText = new JLabel();
        healthText.setText("100/100");
        healthText.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(healthText);
        panel.add(playerHealthProgressBar);



        panel.add(scoreLabel);
        panel.add(worldViewLabel);

        panel.add(Box.createVerticalGlue());

        panel.add(currentTile);
        enemyHealthProgressBar = new JProgressBar() {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicProgressBarUI() {
                    @Override
                    protected Color getSelectionBackground() {
                        return JBColor.RED;
                    }
                });
            }
        };
        enemyHealthProgressBar.setMaximum(100);
        enemyHealthProgressBar.setValue(0);
        enemyHealthProgressBar.setStringPainted(true);
        enemyHealthProgressBar.setForeground(JBColor.GREEN);
        enemyHealthProgressBar.setBackground(JBColor.RED);
//        enemyHealthProgressBar.setBorder(JBUI.Borders.empty(10, 0, 0, 0));
        enemyHealthProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(enemyHealthProgressBar);
        panel.add(enemyHealthLabel);



        JButton resetButton = new JButton("Reset progress");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> {
            VamifyProgress progress = ApplicationManager.getApplication().getService(VamifyProgress.class);
            progress.resetProgress();
            updateWindow();
        });

        panel.add(Box.createVerticalGlue());
        panel.add(resetButton);

        // Update the label with the value from your persisted data class
        updateWindow();

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GameWindowUpdateNotifier.TOPIC, (GameWindowUpdateNotifier) () -> {
            wobbleCurrentTile();
            updateWindow();
        });

        // Create a Content instance for the tool window
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
