package com.proxyme.rider.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import javax.swing.*;

/**
 * Dependency Installation Panel
 * Shows progress of Node.js dependency installation
 */
public class DependencyInstallPanel extends JPanel {

    private final Project project;
    private JBTextArea logArea;
    private JButton installButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Process installProcess;
    private boolean isInstalling = false;
    private boolean isInstalled = false;

    public DependencyInstallPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createUI();
        checkIfAlreadyInstalled();
    }

    private void createUI() {
        // Top panel - What will be installed
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JBLabel infoLabel = new JBLabel(
            "<html><b>Required Node.js Packages:</b><br>" +
                "The following will be installed to: ~/.proxyme/proxy/node_modules/<br>" +
                "<i>(Shared across all Rider projects)</i></html>"
        );

        // Package list
        JBLabel packagesLabel = new JBLabel(
            "<html><div style='margin-top: 5px; font-family: monospace; font-size: 10px;'>" +
                "✓ express - Web server framework<br>" +
                "✓ axios - HTTP client for API requests<br>" +
                "✓ dotenv - Environment configuration<br>" +
                "✓ cors - Cross-origin resource sharing<br>" +
                "✓ Plus ~15-20 dependency packages<br><br>" +
                "<b>Size:</b> ~30-50 MB | <b>Time:</b> 30-60 seconds" +
                "</div></html>"
        );

        topPanel.add(infoLabel, BorderLayout.NORTH);
        topPanel.add(packagesLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Center panel - Log output
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        JBLabel logLabel = new JBLabel("Installation Progress:");
        logLabel.setFont(logLabel.getFont().deriveFont(Font.BOLD));

        logArea = new JBTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        logArea.setBackground(new Color(43, 43, 43));
        logArea.setForeground(new Color(169, 183, 198));
        logArea.setLineWrap(false);
        logArea.setWrapStyleWord(false);
        logArea.setText(
            "Click 'Install Dependencies' button below to begin installation.\n" +
                "You will see each step and package as it's installed.\n"
        );

        JBScrollPane scrollPane = new JBScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(650, 200));
        scrollPane.setBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1)
        );

        centerPanel.add(logLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Progress and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // Progress section
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));

        statusLabel = new JLabel("Ready to install dependencies");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Not started");

        progressPanel.add(statusLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        // Button section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        installButton = new JButton("Install Dependencies");
        installButton.setFont(installButton.getFont().deriveFont(Font.BOLD));
        installButton.addActionListener(e -> startInstallation());

        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelInstallation());

        buttonPanel.add(installButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(progressPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void checkIfAlreadyInstalled() {
        String userHome = System.getProperty("user.home");
        String projectName = project
            .getName()
            .replaceAll("[^a-zA-Z0-9.-]", "_");
        File nodeModules = new File(
            userHome,
            ".proxyme/proxy-" + projectName + "/node_modules"
        );

        if (nodeModules.exists() && nodeModules.isDirectory()) {
            File[] contents = nodeModules.listFiles();
            if (contents != null && contents.length > 5) {
                isInstalled = true;
                statusLabel.setText(
                    "✓ Dependencies already installed (" +
                        contents.length +
                        " packages)"
                );
                statusLabel.setForeground(new Color(76, 175, 80));
                progressBar.setValue(100);
                progressBar.setString("Installed");
                installButton.setText("Reinstall Dependencies");
                appendLog(
                    "Dependencies already installed at: " +
                        nodeModules.getAbsolutePath() +
                        "\n"
                );
                appendLog(
                    "Found " + contents.length + " installed packages.\n"
                );
                appendLog(
                    "\nYou can now launch the proxy from the settings panel.\n"
                );
            }
        }
    }

    private void startInstallation() {
        if (isInstalling) {
            return;
        }

        isInstalling = true;
        installButton.setEnabled(false);
        cancelButton.setEnabled(true);
        logArea.setText("");
        progressBar.setIndeterminate(true);
        progressBar.setString("Installing...");
        statusLabel.setText("Installing dependencies...");
        statusLabel.setForeground(new Color(255, 152, 0));

        // Run installation in background thread
        SwingWorker<Boolean, String> worker = new SwingWorker<
            Boolean,
            String
        >() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Step 1: Find Node.js
                    publish("Step 1/4: Checking Node.js installation...\n");
                    String nodePath = findNodePath();
                    if (nodePath == null) {
                        publish(
                            "ERROR: Node.js not found!\n" +
                                "Please install Node.js from https://nodejs.org/\n"
                        );
                        return false;
                    }
                    publish("✓ Found Node.js at: " + nodePath + "\n\n");

                    // Step 2: Create proxy directory (SHARED for all projects)
                    publish("Step 2/4: Creating shared proxy directory...\n");
                    String userHome = System.getProperty("user.home");
                    File proxymeDir = new File(userHome, ".proxyme");
                    proxymeDir.mkdirs();

                    // Use SHARED directory for all projects
                    File proxyDir = new File(proxymeDir, "proxy");
                    proxyDir.mkdirs();
                    publish(
                        "✓ Created shared directory: " +
                            proxyDir.getAbsolutePath() +
                            "\n"
                    );
                    publish("  (This will be used by all Rider projects)\n\n");

                    // Step 3: Extract proxy files
                    publish("Step 3/4: Extracting proxy files...\n");
                    extractProxyFiles(proxyDir);
                    publish(
                        "✓ Proxy files extracted (proxy.js, package.json)\n\n"
                    );

                    // Step 4: Run npm install
                    publish("Step 4/4: Installing npm packages...\n");
                    publish(
                        "This may take 30-60 seconds depending on your internet speed.\n\n"
                    );

                    String npmPath = nodePath.replace("/node", "/npm");
                    if (!new File(npmPath).exists()) {
                        npmPath = "npm";
                    }

                    ProcessBuilder pb = new ProcessBuilder(npmPath, "install");
                    pb.directory(proxyDir);
                    pb.redirectErrorStream(true);

                    // Add Node.js bin directory to PATH so npm can find node
                    String nodeDir = new File(nodePath).getParent();
                    Map<String, String> env = pb.environment();
                    String currentPath = env.get("PATH");
                    if (currentPath != null) {
                        env.put("PATH", nodeDir + ":" + currentPath);
                    } else {
                        env.put("PATH", nodeDir);
                    }

                    publish("  Using Node.js from: " + nodeDir + "\n");

                    installProcess = pb.start();

                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(installProcess.getInputStream())
                    );

                    String line;
                    int packageCount = 0;
                    while ((line = reader.readLine()) != null) {
                        String trimmedLine = line.trim();

                        // Filter and format output for clarity
                        if (
                            trimmedLine.startsWith("added") ||
                            trimmedLine.contains("package") ||
                            trimmedLine.contains("GET") ||
                            trimmedLine.contains("npm") ||
                            !trimmedLine.isEmpty()
                        ) {
                            // Count packages
                            if (trimmedLine.contains("added")) {
                                String[] parts = trimmedLine.split(" ");
                                for (String part : parts) {
                                    if (part.matches("\\d+")) {
                                        try {
                                            packageCount = Integer.parseInt(
                                                part
                                            );
                                        } catch (
                                            NumberFormatException ignored
                                        ) {}
                                    }
                                }
                            }

                            // Clean up and publish
                            if (trimmedLine.length() > 0) {
                                publish("  " + trimmedLine + "\n");
                            }

                            // Show progress updates
                            if (packageCount > 0 && packageCount % 10 == 0) {
                                publish(
                                    "\n→ Installed " +
                                        packageCount +
                                        " packages so far...\n\n"
                                );
                            }
                        }
                    }

                    if (packageCount > 0) {
                        publish(
                            "\n✓ Total packages installed: " +
                                packageCount +
                                "\n"
                        );
                    }

                    int exitCode = installProcess.waitFor();

                    if (exitCode == 0) {
                        publish("\n✓ Installation completed successfully!\n");
                        publish(
                            "\nAll dependencies are now installed.\n" +
                                "You can now launch the proxy from the settings panel.\n"
                        );
                        return true;
                    } else {
                        publish(
                            "\n✗ Installation failed with exit code: " +
                                exitCode +
                                "\n"
                        );
                        return false;
                    }
                } catch (Exception e) {
                    publish("\n✗ Installation error: " + e.getMessage() + "\n");
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String text : chunks) {
                    appendLog(text);
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    isInstalling = false;
                    progressBar.setIndeterminate(false);

                    if (success) {
                        isInstalled = true;
                        statusLabel.setText(
                            "✓ Dependencies installed successfully"
                        );
                        statusLabel.setForeground(new Color(76, 175, 80));
                        progressBar.setValue(100);
                        progressBar.setString("Complete");
                        installButton.setText("Reinstall Dependencies");
                    } else {
                        statusLabel.setText("✗ Installation failed");
                        statusLabel.setForeground(new Color(244, 67, 54));
                        progressBar.setValue(0);
                        progressBar.setString("Failed");
                    }

                    installButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void cancelInstallation() {
        if (installProcess != null && installProcess.isAlive()) {
            installProcess.destroy();
            appendLog("\n\n--- Installation cancelled by user ---\n");
            statusLabel.setText("Installation cancelled");
            statusLabel.setForeground(new Color(255, 152, 0));
            progressBar.setIndeterminate(false);
            progressBar.setString("Cancelled");
            isInstalling = false;
            installButton.setEnabled(true);
            cancelButton.setEnabled(false);
        }
    }

    private String findNodePath() {
        String[] possiblePaths = {
            "node",
            "/opt/homebrew/bin/node",
            "/usr/local/bin/node",
            "/usr/bin/node",
            System.getProperty("user.home") + "/.nvm/versions/node/*/bin/node",
            "/usr/local/opt/node/bin/node",
        };

        for (String path : possiblePaths) {
            try {
                Process process = new ProcessBuilder(path, "--version").start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return path;
                }
            } catch (Exception e) {
                // Try next path
            }
        }
        return null;
    }

    private void extractProxyFiles(File proxyDir) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String[] filesToExtract = {
            "proxy.js",
            "package.json",
            "package-lock.json",
            "README.md",
            ".env.template",
        };

        for (String fileName : filesToExtract) {
            try (
                var in = classLoader.getResourceAsStream("proxy/" + fileName)
            ) {
                if (in != null) {
                    File targetFile = new File(proxyDir, fileName);
                    java.nio.file.Files.copy(
                        in,
                        targetFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }
        }
    }

    private void appendLog(String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public boolean isInstalling() {
        return isInstalling;
    }
}
