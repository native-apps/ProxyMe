package com.proxyme.rider.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.proxyme.rider.ProxyMeProjectService;
import com.proxyme.rider.ProxyMeSettings;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.Timer;

/**
 * ProxyMe Tool Window Content
 * Shows status, logs, and quick actions at bottom of IDE
 */
public class ProxyMeToolWindowContent {

    private final Project project;
    private JPanel mainPanel;
    private JBTextArea logTextArea;
    private JButton launchButton;
    private JButton stopButton;
    private JButton restartButton;
    private JButton clearLogsButton;
    private JButton openLogFileButton;
    private JButton healthCheckButton;
    private JBLabel statusLabel;
    private JCheckBox autoScrollCheckBox;
    private Timer logUpdateTimer;
    private long lastLogPosition = 0;

    public ProxyMeToolWindowContent(Project project) {
        this.project = project;
        createToolWindowContent();
        startLogMonitoring();
    }

    private void createToolWindowContent() {
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top panel - Status and Controls
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));

        // Status panel (left)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusLabel = new JBLabel("● Proxy: Stopped");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        updateStatusLabel();
        statusPanel.add(statusLabel);

        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
        JBLabel portLabel = new JBLabel("Port: " + settings.proxyPort);
        portLabel.setForeground(JBColor.GRAY);
        statusPanel.add(portLabel);

        topPanel.add(statusPanel, BorderLayout.WEST);

        // Control buttons (center)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        launchButton = new JButton("Launch", AllIcons.Actions.Execute);
        launchButton.setToolTipText("Start proxy server");
        launchButton.addActionListener(e -> launchProxy());

        stopButton = new JButton("Stop", AllIcons.Actions.Suspend);
        stopButton.setToolTipText("Stop proxy server");
        stopButton.addActionListener(e -> stopProxy());

        restartButton = new JButton("Restart", AllIcons.Actions.Restart);
        restartButton.setToolTipText("Restart proxy server");
        restartButton.addActionListener(e -> restartProxy());

        healthCheckButton = new JButton("Health", AllIcons.Actions.Refresh);
        healthCheckButton.setToolTipText("Open health check in browser");
        healthCheckButton.addActionListener(e -> openHealthCheck());

        clearLogsButton = new JButton("Clear", AllIcons.Actions.GC);
        clearLogsButton.setToolTipText("Clear log display");
        clearLogsButton.addActionListener(e -> clearLogs());

        openLogFileButton = new JButton("Open Log", AllIcons.Actions.MenuOpen);
        openLogFileButton.setToolTipText("Open log file in editor");
        openLogFileButton.addActionListener(e -> openLogFile());

        controlPanel.add(launchButton);
        controlPanel.add(stopButton);
        controlPanel.add(restartButton);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(healthCheckButton);
        controlPanel.add(clearLogsButton);
        controlPanel.add(openLogFileButton);

        topPanel.add(controlPanel, BorderLayout.CENTER);

        // Auto-scroll checkbox (right)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        autoScrollCheckBox = new JCheckBox("Auto-scroll", true);
        autoScrollCheckBox.setToolTipText(
            "Automatically scroll to bottom when new logs appear"
        );
        rightPanel.add(autoScrollCheckBox);
        topPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Log text area (center)
        logTextArea = new JBTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logTextArea.setLineWrap(false);
        logTextArea.setText(getWelcomeMessage());

        JBScrollPane scrollPane = new JBScrollPane(logTextArea);
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        );
        scrollPane.setPreferredSize(new Dimension(800, 200));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Quick Help
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        bottomPanel.setBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.GRAY)
        );

        JBLabel helpLabel = new JBLabel("Quick Help:");
        helpLabel.setForeground(JBColor.GRAY);
        helpLabel.setFont(helpLabel.getFont().deriveFont(Font.BOLD));
        bottomPanel.add(helpLabel);

        addHelpLink(bottomPanel, "Settings: Tools → ProxyMe");
        bottomPanel.add(new JLabel("|"));
        addHelpLink(
            bottomPanel,
            "Rider AI: Settings → AI Assistant → Add OpenAI API"
        );
        bottomPanel.add(new JLabel("|"));
        addHelpLink(
            bottomPanel,
            "URL: http://localhost:" + settings.proxyPort + "/v1"
        );

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addHelpLink(JPanel panel, String text) {
        JBLabel label = new JBLabel(text);
        label.setForeground(JBColor.GRAY);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        panel.add(label);
    }

    private String getWelcomeMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss"
        );
        String timestamp = LocalDateTime.now().format(formatter);

        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        File logFile = service.getLogFile();
        File proxyDir = service.getProxyDirectory();
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

        return String.format(
            "╔═══════════════════════════════════════════════════════════════╗\n" +
                "║              ProxyMe - AI Proxy for Rider IDE               ║\n" +
                "╚═══════════════════════════════════════════════════════════════╝\n" +
                "\n" +
                "[%s] Tool Window Ready\n" +
                "\n" +
                "📍 Configuration:\n" +
                "   • Port: %d\n" +
                "   • Host: %s\n" +
                "   • Log File: %s\n" +
                "   • Proxy Dir: %s\n" +
                "\n" +
                "🚀 Quick Start:\n" +
                "   1. Click 'Launch' to start proxy\n" +
                "   2. Wait for green status indicator\n" +
                "   3. Click 'Health' to verify it's running\n" +
                "   4. Configure Rider AI Assistant:\n" +
                "      Settings → AI Assistant → Add OpenAI API\n" +
                "      URL: http://localhost:%d/v1\n" +
                "      API Key: (leave empty)\n" +
                "\n" +
                "📋 Available Commands:\n" +
                "   • Launch   - Start the proxy server\n" +
                "   • Stop     - Stop the proxy server\n" +
                "   • Restart  - Restart the proxy server\n" +
                "   • Health   - Open health check in browser\n" +
                "   • Clear    - Clear this log display\n" +
                "   • Open Log - Open full log file in editor\n" +
                "\n" +
                "═══════════════════════════════════════════════════════════════\n",
            timestamp,
            settings.proxyPort,
            settings.proxyHost,
            logFile.getAbsolutePath(),
            proxyDir.getAbsolutePath(),
            settings.proxyPort
        );
    }

    private void startLogMonitoring() {
        // Update status every 2 seconds
        logUpdateTimer = new Timer(2000, e -> {
            updateStatusLabel();
            updateLogDisplay();
        });
        logUpdateTimer.start();
    }

    private void updateStatusLabel() {
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service == null) return;

        ProxyMeSettings.ProxyStatus status = service.getStatus();

        SwingUtilities.invokeLater(() -> {
            switch (status) {
                case ACTIVE:
                    statusLabel.setText("● Proxy: Running");
                    statusLabel.setForeground(new Color(76, 175, 80)); // Green
                    launchButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    restartButton.setEnabled(true);
                    break;
                case WARNING:
                    statusLabel.setText("● Proxy: Warning");
                    statusLabel.setForeground(new Color(255, 152, 0)); // Orange
                    launchButton.setEnabled(true);
                    stopButton.setEnabled(true);
                    restartButton.setEnabled(true);
                    break;
                case INACTIVE:
                default:
                    statusLabel.setText("● Proxy: Stopped");
                    statusLabel.setForeground(new Color(244, 67, 54)); // Red
                    launchButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    restartButton.setEnabled(false);
                    break;
            }
        });
    }

    private void updateLogDisplay() {
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service == null) return;

        File logFile = service.getLogFile();
        if (!logFile.exists()) return;

        try {
            long currentLength = logFile.length();
            if (currentLength > lastLogPosition) {
                // Read new content
                try (
                    RandomAccessFile raf = new RandomAccessFile(logFile, "r")
                ) {
                    raf.seek(lastLogPosition);
                    StringBuilder newContent = new StringBuilder();
                    String line;
                    while ((line = raf.readLine()) != null) {
                        newContent.append(line).append("\n");
                    }
                    lastLogPosition = currentLength;

                    // Append to display
                    if (newContent.length() > 0) {
                        SwingUtilities.invokeLater(() -> {
                            logTextArea.append(newContent.toString());

                            // Auto-scroll if enabled
                            if (autoScrollCheckBox.isSelected()) {
                                logTextArea.setCaretPosition(
                                    logTextArea.getDocument().getLength()
                                );
                            }
                        });
                    }
                }
            }
        } catch (IOException e) {
            // Ignore - might be temporary file lock
        }
    }

    private void launchProxy() {
        appendLog("User clicked Launch button");
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service != null) {
            service.launchProxy();
        }
    }

    private void stopProxy() {
        appendLog("User clicked Stop button");
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service != null) {
            service.stopProxy();
        }
    }

    private void restartProxy() {
        appendLog("User clicked Restart button");
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service != null) {
            service.restartProxy();
        }
    }

    private void openHealthCheck() {
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
        String healthUrl =
            "http://" +
            settings.proxyHost +
            ":" +
            settings.proxyPort +
            "/health";
        try {
            Desktop.getDesktop().browse(new java.net.URI(healthUrl));
            appendLog("Opened health check in browser: " + healthUrl);
        } catch (Exception e) {
            appendLog("Failed to open browser: " + e.getMessage());
        }
    }

    private void clearLogs() {
        logTextArea.setText("");
        lastLogPosition = 0;
        logTextArea.append(getWelcomeMessage());
    }

    private void openLogFile() {
        ProxyMeProjectService service = project.getService(
            ProxyMeProjectService.class
        );
        if (service == null) return;

        File logFile = service.getLogFile();
        if (!logFile.exists()) {
            appendLog(
                "Log file does not exist yet: " + logFile.getAbsolutePath()
            );
            return;
        }

        try {
            // Open in system editor
            Desktop.getDesktop().open(logFile);
            appendLog("Opened log file: " + logFile.getAbsolutePath());
        } catch (Exception e) {
            appendLog("Failed to open log file: " + e.getMessage());
        }
    }

    private void appendLog(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss"
        );
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s\n", timestamp, message);

        SwingUtilities.invokeLater(() -> {
            logTextArea.append(logEntry);
            if (autoScrollCheckBox.isSelected()) {
                logTextArea.setCaretPosition(
                    logTextArea.getDocument().getLength()
                );
            }
        });
    }

    public JPanel getContent() {
        return mainPanel;
    }

    public void dispose() {
        if (logUpdateTimer != null) {
            logUpdateTimer.stop();
        }
    }
}
