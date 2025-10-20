package com.proxyme.rider.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.util.Consumer;
import com.proxyme.rider.ProxyMeConfigurable;
import com.proxyme.rider.ProxyMeProjectService;
import com.proxyme.rider.ProxyMeSettings;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.Timer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ProxyMe Status Bar Widget
 * Shows proxy status at bottom-right of IDE
 * Click to open settings
 */
public class ProxyMeStatusBarWidget
    implements StatusBarWidget, StatusBarWidget.IconPresentation {

    private static final String WIDGET_ID = "ProxyMeStatusBarWidget";
    private final Project project;
    private final Timer updateTimer;
    private StatusBar statusBar;

    public ProxyMeStatusBarWidget(@NotNull Project project) {
        this.project = project;

        // Update status every 2 seconds
        updateTimer = new Timer(2000, e -> {
            if (statusBar != null) {
                statusBar.updateWidget(ID());
            }
        });
        updateTimer.start();
    }

    @NotNull
    @Override
    public String ID() {
        return WIDGET_ID;
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation() {
        return this;
    }

    public void install(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;
        updateTimer.start();
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }

    @NotNull
    @Override
    public Icon getIcon() {
        if (project == null || project.isDisposed()) {
            return AllIcons.Actions.Lightning;
        }

        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service == null) {
            return AllIcons.Actions.Lightning;
        }

        ProxyMeSettings.ProxyStatus status = service.getStatus();

        // Return icon based on status
        switch (status) {
            case ACTIVE:
                return AllIcons.RunConfigurations.TestPassed; // Green
            case WARNING:
                return AllIcons.RunConfigurations.TestError; // Orange
            case INACTIVE:
            default:
                return AllIcons.RunConfigurations.TestFailed; // Red
        }
    }

    @NotNull
    @Override
    public String getTooltipText() {
        if (project == null || project.isDisposed()) {
            return "ProxyMe - Click to configure";
        }

        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

        if (service == null) {
            return "ProxyMe - Click to configure";
        }

        ProxyMeSettings.ProxyStatus status = service.getStatus();

        StringBuilder tooltip = new StringBuilder("ProxyMe: ");

        switch (status) {
            case ACTIVE:
                tooltip.append("Running on port ").append(settings.proxyPort);
                break;
            case WARNING:
                tooltip.append("Running with warnings");
                break;
            case INACTIVE:
            default:
                tooltip.append("Stopped");
                break;
        }

        tooltip.append("\nClick to open settings");
        return tooltip.toString();
    }

    @Nullable
    @Override
    public Consumer<MouseEvent> getClickConsumer() {
        return mouseEvent -> {
            if (project != null && !project.isDisposed()) {
                // Open ProxyMe settings when clicked
                ShowSettingsUtil.getInstance().showSettingsDialog(
                    project,
                    ProxyMeConfigurable.class
                );
            }
        };
    }

    // Factory for creating the widget
    public static class Factory implements StatusBarWidgetFactory {

        @Override
        @NotNull
        @NonNls
        public String getId() {
            return WIDGET_ID;
        }

        @Override
        @Nls
        @NotNull
        public String getDisplayName() {
            return "ProxyMe";
        }

        @Override
        public boolean isAvailable(@NotNull Project project) {
            return true;
        }

        @Override
        @NotNull
        public StatusBarWidget createWidget(@NotNull Project project) {
            return new ProxyMeStatusBarWidget(project);
        }

        @Override
        public void disposeWidget(@NotNull StatusBarWidget widget) {
            widget.dispose();
        }

        @Override
        public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
            return true;
        }
    }
}
