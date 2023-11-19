package me.tomsavage.vamify;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class TypedHandler extends TypedHandlerDelegate {
    private static final Logger LOG = Logger.getLogger(TypedHandler.class.getName());

    @Override
    public @NotNull Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        VamifyProgress progress = ApplicationManager.getApplication().getService(VamifyProgress.class);

        progress.world.takeStep();

        MessageBus messageBus = project.getMessageBus();
        messageBus.syncPublisher(GameWindowUpdateNotifier.TOPIC).updateGameWindow();

        return super.charTyped(c, project, editor, file);
    }
}
