package com.proxyme.rider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.proxyme.rider.ui.ProxyMeToolWindowContent;
import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating ProxyMe tool window
 * Displayed at the bottom of Rider IDE
 */
public class ProxyMeToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(
        @NotNull Project project,
        @NotNull ToolWindow toolWindow
    ) {
        ProxyMeToolWindowContent toolWindowContent = new ProxyMeToolWindowContent(
            project
        );
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(
            toolWindowContent.getContent(),
            "",
            false
        );
        toolWindow.getContentManager().addContent(content);
    }
}
