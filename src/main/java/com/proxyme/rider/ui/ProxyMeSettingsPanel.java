package com.proxyme.rider.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.proxyme.rider.ProxyMeModelsConfigService;
import com.proxyme.rider.ProxyMeProjectService;
import com.proxyme.rider.ProxyMeSettings;
import com.proxyme.rider.Template;
import com.proxyme.rider.TemplateService;
import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * ProxyMe Settings Panel
 * Main UI for project-specific proxy configuration
 */
public class ProxyMeSettingsPanel {

    private final Project project;
    private JPanel mainPanel;

    // Auto-launch settings
    private JBCheckBox autoLaunchCheckBox;

    // Proxy configuration
    private JBTextField proxyPortField;
    private JBTextField proxyHostField;

    // Logging settings
    private JBCheckBox showLogsInTerminalCheckBox;
    private JBCheckBox saveLogsToFileCheckBox;
    private JBTextField logFilePathField;

    // Proxy control
    private JButton launchProxyButton;
    private JButton stopProxyButton;
    private JButton restartProxyButton;
    private JButton healthCheckButton;
    private StatusIndicator statusIndicator;

    // Model configuration
    private ModelConfigPanel modelConfigPanel;

    // Template management
    private JComboBox<String> templateComboBox;
    private JButton loadTemplateButton;
    private JButton saveTemplateButton;

    // Dependency installation (inline)
    private DependencyInstallPanel dependencyPanel;
    private JPanel dependencySection;
    private boolean showingInstaller = false;

    public ProxyMeSettingsPanel(Project project) {
        this.project = project;
        createUI();
    }

    private void createUI() {
        // Initialize components
        autoLaunchCheckBox = new JBCheckBox(
            "Launch proxy on Rider IDE startup"
        );

        proxyPortField = new JBTextField();
        proxyPortField.setColumns(10);
        proxyHostField = new JBTextField();
        proxyHostField.setColumns(20);

        showLogsInTerminalCheckBox = new JBCheckBox("Show logs in Terminal");
        saveLogsToFileCheckBox = new JBCheckBox("Save logs to file");
        logFilePathField = new JBTextField();
        logFilePathField.setColumns(40);

        // Proxy control buttons
        launchProxyButton = new JButton("Launch Proxy");
        stopProxyButton = new JButton("Stop Proxy");
        restartProxyButton = new JButton("Restart Proxy");
        healthCheckButton = new JButton("Check Health");
        statusIndicator = new StatusIndicator();

        // Model configuration panel
        modelConfigPanel = new ModelConfigPanel();

        // Template management
        templateComboBox = new JComboBox<>();
        loadTemplateButton = new JButton("Load Template");
        saveTemplateButton = new JButton("Save as Template");

        // Build the form
        mainPanel = FormBuilder.createFormBuilder()
            // Dependency installation section (collapsible)
            .addComponent(createDependencySection())
            .addVerticalGap(10)
            // Instructions section
            .addComponent(new JBLabel("<html><b>Quick Start Guide</b></html>"))
            .addComponent(
                new JBLabel(
                    "<html><small>" +
                        "1. Configure port and host below<br>" +
                        "2. Click 'Launch Proxy' to start<br>" +
                        "3. Click 'Check Health' to verify it's running<br>" +
                        "4. Configure Rider AI Assistant: Settings → AI Assistant → Add OpenAI API<br>" +
                        "&nbsp;&nbsp;&nbsp;URL: http://localhost:3000/v1<br>" +
                        "&nbsp;&nbsp;&nbsp;API Key: (leave empty or any text)<br>" +
                        "5. View logs in Terminal or check ~/.proxyme/logs/proxyme.log" +
                        "</small></html>"
                )
            )
            .addVerticalGap(10)
            // Auto-launch section
            .addComponent(new JBLabel("<html><b>Startup Settings</b></html>"))
            .addComponent(autoLaunchCheckBox)
            .addVerticalGap(10)
            // Proxy configuration section
            .addComponent(
                new JBLabel("<html><b>Proxy Configuration</b></html>")
            )
            .addLabeledComponent("Port:", proxyPortField)
            .addLabeledComponent("Host:", proxyHostField)
            .addVerticalGap(10)
            // Proxy control section
            .addComponent(new JBLabel("<html><b>Proxy Control</b></html>"))
            .addComponent(createProxyControlPanel())
            .addVerticalGap(10)
            // Logging section
            .addComponent(new JBLabel("<html><b>Logging Settings</b></html>"))
            .addComponent(showLogsInTerminalCheckBox)
            .addComponent(saveLogsToFileCheckBox)
            .addLabeledComponent("Log file path:", logFilePathField)
            .addVerticalGap(10)
            // Model configuration section
            .addComponent(
                new JBLabel("<html><b>Model Configuration</b></html>")
            )
            .addComponent(modelConfigPanel.getPanel())
            .addVerticalGap(10)
            // Template management section
            .addComponent(
                new JBLabel("<html><b>Template Management</b></html>")
            )
            .addComponent(createTemplatePanel())
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
    }

    private JPanel createProxyControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(launchProxyButton);
        panel.add(stopProxyButton);
        panel.add(restartProxyButton);
        panel.add(healthCheckButton);
        panel.add(new JBLabel("Status:"));
        panel.add(statusIndicator);

        // Wire up button actions
        launchProxyButton.setToolTipText("Start the proxy server");
        launchProxyButton.addActionListener(e -> {
            ProxyMeProjectService service = project.getService(
                ProxyMeProjectService.class
            );
            if (service != null) {
                service.launchProxy();
                updateStatusIndicator();
            }
        });

        stopProxyButton.setToolTipText("Stop the proxy server");
        stopProxyButton.addActionListener(e -> {
            ProxyMeProjectService service = project.getService(
                ProxyMeProjectService.class
            );
            if (service != null) {
                service.stopProxy();
                updateStatusIndicator();
            }
        });

        restartProxyButton.setToolTipText("Restart the proxy server");
        restartProxyButton.addActionListener(e -> {
            ProxyMeProjectService service = project.getService(
                ProxyMeProjectService.class
            );
            if (service != null) {
                service.restartProxy();
                updateStatusIndicator();
            }
        });

        // Health Check Button
        healthCheckButton.setToolTipText("Open health check in browser");
        healthCheckButton.addActionListener(e -> {
            ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
            String healthUrl =
                "http://" +
                settings.proxyHost +
                ":" +
                settings.proxyPort +
                "/health";
            try {
                java.awt.Desktop.getDesktop().browse(
                    new java.net.URI(healthUrl)
                );
            } catch (Exception ex) {
                notifyUser(
                    "Could not open browser. URL: " + healthUrl,
                    com.intellij.notification.NotificationType.WARNING
                );
            }
        });

        return panel;
    }

    /**
     * Show notification to user
     */
    private void notifyUser(
        String message,
        com.intellij.notification.NotificationType type
    ) {
        com.intellij.notification.Notification notification =
            new com.intellij.notification.Notification(
                "ProxyMe",
                "ProxyMe",
                message,
                type
            );
        com.intellij.notification.Notifications.Bus.notify(
            notification,
            project
        );
    }

    /**
     * Update the status indicator from current proxy state
     */
    private void updateStatusIndicator() {
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service != null) {
            statusIndicator.setStatus(service.getStatus());
        }
    }

    private JPanel createTemplatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        loadTemplateButton.setToolTipText("Load a template configuration");
        loadTemplateButton.addActionListener(e -> onLoadTemplate());

        saveTemplateButton.setToolTipText(
            "Save current configuration as a template"
        );
        saveTemplateButton.addActionListener(e -> onSaveTemplate());

        panel.add(loadTemplateButton);
        panel.add(saveTemplateButton);

        return panel;
    }

    private void onLoadTemplate() {
        TemplateService templateService = new TemplateService();
        LoadTemplateDialog dialog = new LoadTemplateDialog(
            null,
            templateService
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Template template = dialog.getSelectedTemplate();
            if (template != null) {
                applyTemplate(template);
                notifyUser(
                    "Template '" + template.getName() + "' loaded successfully",
                    com.intellij.notification.NotificationType.INFORMATION
                );
            }
        }
    }

    private void onSaveTemplate() {
        ProxyMeSettings currentSettings = ProxyMeSettings.getInstance(project);
        TemplateService templateService = new TemplateService();

        SaveTemplateDialog dialog = new SaveTemplateDialog(
            null,
            templateService,
            currentSettings
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Template saved = dialog.getSavedTemplate();
            if (saved != null) {
                notifyUser(
                    "Template '" + saved.getName() + "' saved successfully",
                    com.intellij.notification.NotificationType.INFORMATION
                );
            }
        }
    }

    private void applyTemplate(Template template) {
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

        // Apply proxy settings if present
        if (template.getProxySettings() != null) {
            Template.ProxySettings proxySettings = template.getProxySettings();
            settings.proxyHost = proxySettings.host;
            settings.proxyPort = proxySettings.port;
            settings.autoLaunchOnStartup = proxySettings.autoLaunch;

            // Update UI
            proxyHostField.setText(settings.proxyHost);
            proxyPortField.setText(String.valueOf(settings.proxyPort));
            autoLaunchCheckBox.setSelected(settings.autoLaunchOnStartup);
        }

        // Apply model configurations
        settings.models.clear();
        for (ProxyMeSettings.ModelConfig templateModel : template.getModels()) {
            ProxyMeSettings.ModelConfig settingsModel =
                new ProxyMeSettings.ModelConfig();
            settingsModel.modelName = templateModel.modelName;
            settingsModel.apiProvider = templateModel.apiProvider;
            settingsModel.apiEndpoint = templateModel.apiEndpoint;
            settingsModel.apiKey = templateModel.apiKey != null
                ? templateModel.apiKey
                : "";
            settingsModel.enabled = templateModel.enabled;
            settingsModel.modelCategory = templateModel.modelCategory;

            // Copy temperature and stream settings (v2.1.0)
            settingsModel.temperature = templateModel.temperature;
            settingsModel.stream = templateModel.stream;

            // Copy custom headers and body params if present
            if (templateModel.customHeaders != null) {
                settingsModel.customHeaders = new java.util.HashMap<>(
                    templateModel.customHeaders
                );
            }
            if (templateModel.customBodyParams != null) {
                settingsModel.customBodyParams = new java.util.HashMap<>(
                    templateModel.customBodyParams
                );
            }

            settings.models.add(settingsModel);
        }

        // Refresh UI
        modelConfigPanel.reset(settings.models);

        // Update template name
        settings.currentTemplateName = template.getName();
    }

    private JPanel createDependencySection() {
        dependencySection = new JPanel(new BorderLayout());
        dependencySection.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dependencies"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            )
        );

        // Check if dependencies are installed
        if (isDependencyInstalled()) {
            // Show installed status
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JBLabel statusLabel = new JBLabel(
                "✓ Dependencies installed and ready"
            );
            statusLabel.setForeground(new java.awt.Color(76, 175, 80));
            statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));

            JButton reinstallButton = new JButton("Reinstall");
            reinstallButton.addActionListener(e -> toggleDependencyInstaller());

            statusPanel.add(statusLabel);
            statusPanel.add(reinstallButton);
            dependencySection.add(statusPanel, BorderLayout.CENTER);
        } else {
            // Show installation required
            JPanel notInstalledPanel = new JPanel(new BorderLayout(10, 10));

            JBLabel warningLabel = new JBLabel(
                "<html><b>⚠ Dependencies Required</b><br>" +
                    "Node.js packages must be installed before launching the proxy.</html>"
            );
            warningLabel.setForeground(new java.awt.Color(255, 152, 0));

            JButton installButton = new JButton("Install Dependencies");
            installButton.setFont(
                installButton.getFont().deriveFont(Font.BOLD)
            );
            installButton.addActionListener(e -> toggleDependencyInstaller());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.add(installButton);

            notInstalledPanel.add(warningLabel, BorderLayout.NORTH);
            notInstalledPanel.add(buttonPanel, BorderLayout.CENTER);
            dependencySection.add(notInstalledPanel, BorderLayout.CENTER);
        }

        return dependencySection;
    }

    private boolean isDependencyInstalled() {
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

    private void toggleDependencyInstaller() {
        if (showingInstaller) {
            // Hide installer
            hideDependencyInstaller();
        } else {
            // Show installer inline
            showDependencyInstaller();
        }
    }

    private void showDependencyInstaller() {
        if (dependencyPanel == null) {
            dependencyPanel = new DependencyInstallPanel(project);
        }

        // Add installer panel to dependency section
        dependencySection.removeAll();

        // Create container with collapse button
        JPanel container = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton collapseButton = new JButton("▼ Hide Installer");
        collapseButton.addActionListener(e -> hideDependencyInstaller());
        headerPanel.add(collapseButton);

        container.add(headerPanel, BorderLayout.NORTH);
        container.add(dependencyPanel, BorderLayout.CENTER);

        dependencySection.add(container, BorderLayout.CENTER);
        dependencySection.revalidate();
        dependencySection.repaint();

        showingInstaller = true;

        // Scroll to top
        SwingUtilities.invokeLater(() -> {
            mainPanel.scrollRectToVisible(new java.awt.Rectangle(0, 0, 1, 1));
        });
    }

    private void hideDependencyInstaller() {
        showingInstaller = false;
        refreshDependencySection();
    }

    private void refreshDependencySection() {
        if (dependencySection != null && mainPanel != null) {
            dependencySection.removeAll();

            // Recreate the dependency status display
            if (isDependencyInstalled()) {
                JPanel statusPanel = new JPanel(
                    new FlowLayout(FlowLayout.LEFT)
                );
                JBLabel statusLabel = new JBLabel(
                    "✓ Dependencies installed and ready"
                );
                statusLabel.setForeground(new java.awt.Color(76, 175, 80));
                statusLabel.setFont(
                    statusLabel.getFont().deriveFont(Font.BOLD)
                );

                JButton reinstallButton = new JButton("Reinstall");
                reinstallButton.addActionListener(e ->
                    toggleDependencyInstaller()
                );

                statusPanel.add(statusLabel);
                statusPanel.add(reinstallButton);
                dependencySection.add(statusPanel, BorderLayout.CENTER);
            } else {
                JPanel notInstalledPanel = new JPanel(new BorderLayout(10, 10));

                JBLabel warningLabel = new JBLabel(
                    "<html><b>⚠ Dependencies Required</b><br>" +
                        "Node.js packages must be installed before launching the proxy.</html>"
                );
                warningLabel.setForeground(new java.awt.Color(255, 152, 0));

                JButton installButton = new JButton("Install Dependencies");
                installButton.setFont(
                    installButton.getFont().deriveFont(Font.BOLD)
                );
                installButton.addActionListener(e ->
                    toggleDependencyInstaller()
                );

                JPanel buttonPanel = new JPanel(
                    new FlowLayout(FlowLayout.LEFT)
                );
                buttonPanel.add(installButton);

                notInstalledPanel.add(warningLabel, BorderLayout.NORTH);
                notInstalledPanel.add(buttonPanel, BorderLayout.CENTER);
                dependencySection.add(notInstalledPanel, BorderLayout.CENTER);
            }

            dependencySection.revalidate();
            dependencySection.repaint();
        }
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified(ProxyMeSettings settings) {
        if (settings == null) {
            return false;
        }
        boolean modified = false;
        modified |=
            autoLaunchCheckBox.isSelected() != settings.autoLaunchOnStartup;
        modified |= !proxyPortField
            .getText()
            .equals(String.valueOf(settings.proxyPort));
        modified |= !proxyHostField.getText().equals(settings.proxyHost);

        modified |=
            showLogsInTerminalCheckBox.isSelected() !=
            settings.showLogsInTerminal;
        modified |=
            saveLogsToFileCheckBox.isSelected() != settings.saveLogsToFile;
        modified |= !logFilePathField.getText().equals(settings.logFilePath);
        modified |= modelConfigPanel.isModified(settings.models);
        return modified;
    }

    public void apply(ProxyMeSettings settings) {
        if (settings == null) {
            return;
        }
        settings.autoLaunchOnStartup = autoLaunchCheckBox.isSelected();

        // Parse port with error handling
        try {
            String portText = proxyPortField.getText().trim();
            if (!portText.isEmpty()) {
                settings.proxyPort = Integer.parseInt(portText);
            }
        } catch (NumberFormatException e) {
            // Keep existing port if invalid
        }

        settings.proxyHost = proxyHostField.getText().trim();
        if (settings.proxyHost.isEmpty()) {
            settings.proxyHost = "localhost";
        }

        settings.showLogsInTerminal = showLogsInTerminalCheckBox.isSelected();
        settings.saveLogsToFile = saveLogsToFileCheckBox.isSelected();
        settings.logFilePath = logFilePathField.getText().trim();
        modelConfigPanel.apply(settings.models);

        // Generate models.json for proxy to use (only enabled models)
        ProxyMeModelsConfigService modelsConfigService =
            ProxyMeModelsConfigService.getInstance();
        modelsConfigService.generateModelsConfig(project);
    }

    public void reset(ProxyMeSettings settings) {
        if (settings == null) {
            return;
        }
        autoLaunchCheckBox.setSelected(settings.autoLaunchOnStartup);

        // Set port with default if empty
        if (settings.proxyPort <= 0) {
            settings.proxyPort = 3000;
        }
        proxyPortField.setText(String.valueOf(settings.proxyPort));

        // Set host with default if empty
        if (settings.proxyHost == null || settings.proxyHost.isEmpty()) {
            settings.proxyHost = "localhost";
        }
        proxyHostField.setText(settings.proxyHost);

        showLogsInTerminalCheckBox.setSelected(settings.showLogsInTerminal);
        saveLogsToFileCheckBox.setSelected(settings.saveLogsToFile);
        logFilePathField.setText(
            settings.logFilePath != null ? settings.logFilePath : ""
        );
        modelConfigPanel.reset(settings.models);

        // Update status indicator
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service != null) {
            statusIndicator.setStatus(service.getStatus());
        } else {
            statusIndicator.setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
        }
    }

    /**
     * Status Indicator Component
     * Shows green/orange/red LED light for proxy status
     */
    private static class StatusIndicator extends JPanel {

        private ProxyMeSettings.ProxyStatus status =
            ProxyMeSettings.ProxyStatus.INACTIVE;
        private static final int SIZE = 16;

        public StatusIndicator() {
            setPreferredSize(new Dimension(SIZE + 4, SIZE + 4));
            setToolTipText("Proxy Status");
        }

        public void setStatus(ProxyMeSettings.ProxyStatus status) {
            this.status = status;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color color;
            switch (status) {
                case ACTIVE:
                    color = new Color(76, 175, 80); // Green
                    break;
                case WARNING:
                    color = new Color(255, 152, 0); // Orange
                    break;
                case INACTIVE:
                default:
                    color = new Color(244, 67, 54); // Red
                    break;
            }

            g2d.setColor(color);
            g2d.fillOval(2, 2, SIZE, SIZE);

            // Add highlight
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(4, 4, SIZE / 2, SIZE / 2);
        }
    }
}
