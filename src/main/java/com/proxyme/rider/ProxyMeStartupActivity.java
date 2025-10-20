package com.proxyme.rider;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.proxyme.rider.ProxyMeProjectService;
import com.proxyme.rider.ProxyMeSettings;
import java.io.File;
import org.jetbrains.annotations.NotNull;

/**
 * ProxyMe Startup Activity
 * Checks dependencies and optionally launches proxy on IDE startup
 * Version 2.0: Also initializes preset templates on first launch
 */
public class ProxyMeStartupActivity implements StartupActivity {

    private static final Logger LOG = Logger.getInstance(
        ProxyMeStartupActivity.class
    );

    @Override
    public void runActivity(@NotNull Project project) {
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

        // Initialize preset templates (copy from resources if needed)
        initializeTemplates();

        // Check if dependencies are installed
        if (!isDependencyInstalled(project)) {
            // Show notification about missing dependencies
            showDependencyNotification(project);
        } else if (settings.autoLaunchOnStartup) {
            // Only auto-launch if dependencies are installed
            ProxyMeProjectService service = project.getService(
                ProxyMeProjectService.class
            );
            if (service != null && !service.isProxyRunning()) {
                // Launch proxy after a short delay to let IDE finish loading
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(
                    () -> {
                        service.launchProxy();
                    }
                );
            }
        }
    }

    /**
     * Initialize preset templates on first launch.
     * Copies bundled templates from plugin resources to ~/.proxyme/templates/presets/
     */
    private void initializeTemplates() {
        try {
            TemplateService templateService = new TemplateService();
            boolean success = templateService.ensurePresetsAvailable();
            if (success) {
                LOG.info("Preset templates initialized successfully");
            } else {
                LOG.warn("Failed to initialize some preset templates");
            }
        } catch (Exception e) {
            LOG.error("Error initializing preset templates", e);
        }
    }

    private boolean isDependencyInstalled(Project project) {
        String userHome = System.getProperty("user.home");
        // Check SHARED dependency directory (used by all projects)
        File nodeModules = new File(userHome, ".proxyme/proxy/node_modules");
        return (
            nodeModules.exists() &&
            nodeModules.isDirectory() &&
            nodeModules.listFiles() != null &&
            nodeModules.listFiles().length > 5
        );
    }

    private void showDependencyNotification(Project project) {
        Notification notification = new Notification(
            "ProxyMe",
            "ProxyMe Dependencies Required",
            "Node.js packages must be installed before using ProxyMe. Click 'Install Dependencies' to get started.",
            NotificationType.WARNING
        );

        notification.addAction(
            new NotificationAction("Install Dependencies") {
                @Override
                public void actionPerformed(
                    @NotNull AnActionEvent e,
                    @NotNull Notification notification
                ) {
                    notification.expire();
                    ShowSettingsUtil.getInstance().showSettingsDialog(
                        project,
                        ProxyMeConfigurable.class
                    );
                }
            }
        );

        Notifications.Bus.notify(notification, project);
    }
}
