package me.tomsavage.vamify;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.pom.Navigatable;

public class PopupGameWindowAction extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        VamifyProgress progress = VamifyProgress.getInstance();
        // Using the event, create and show a dialog
        Project currentProject = e.getProject();
        StringBuilder message =
                new StringBuilder(e.getPresentation().getText() + " Progress");

        if (progress.getState() != null) {
            message.append("\nLevel: ").append(progress.getState().level);
            message.append("\nScore: ").append(progress.getState().score);
        }
        String title = "Vamify Progress";
        Messages.showMessageDialog(
                currentProject,
                message.toString(),
                title,
                Messages.getInformationIcon());
    }
}
