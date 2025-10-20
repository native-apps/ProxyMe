package com.proxyme.rider;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.proxyme.rider.ProxyMeSettings;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * ProxyMe Project Service
 * Manages proxy server lifecycle for the project
 */
@Service(Service.Level.PROJECT)
public final class ProxyMeProjectService {

    private final Project project;
    private Process proxyProcess;
    private OSProcessHandler processHandler;
    private ProxyMeSettings.ProxyStatus currentStatus =
        ProxyMeSettings.ProxyStatus.INACTIVE;
    private File proxyDirectory;
    private File logFile;
    private BufferedWriter logWriter;

    public ProxyMeProjectService(Project project) {
        this.project = project;
    }

    public boolean isProxyRunning() {
        return proxyProcess != null && proxyProcess.isAlive();
    }

    public ProxyMeSettings.ProxyStatus getStatus() {
        return currentStatus;
    }

    public void setStatus(ProxyMeSettings.ProxyStatus status) {
        this.currentStatus = status;
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
        settings.lastStatus = status;
    }

    /**
     * Launch proxy server
     */
    public void launchProxy() {
        if (isProxyRunning()) {
            notifyUser("Proxy is already running", NotificationType.WARNING);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                notifyUser(
                    "Starting proxy server...",
                    NotificationType.INFORMATION
                );

                // Step 1: Extract proxy files
                extractProxyFiles();

                // Step 2: Check if Node.js is installed
                if (!isNodeJsInstalled()) {
                    notifyUser(
                        "Node.js is not installed. Please install Node.js from https://nodejs.org/",
                        NotificationType.ERROR
                    );
                    setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
                    return;
                }

                // Step 3: Run npm install if needed
                File nodeModules = new File(proxyDirectory, "node_modules");
                if (!nodeModules.exists()) {
                    notifyUser(
                        "Installing dependencies (this may take a minute)...",
                        NotificationType.INFORMATION
                    );
                    if (!runNpmInstall()) {
                        notifyUser(
                            "Failed to install dependencies",
                            NotificationType.ERROR
                        );
                        setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
                        return;
                    }
                }

                // Step 4: Generate .env file with API keys
                generateEnvFile();

                // Step 5: Start the proxy process
                startProxyProcess();

                // Step 6: Wait a moment and verify it started
                Thread.sleep(2000);
                if (isProxyRunning() && checkHealth()) {
                    notifyUser(
                        "Proxy server started successfully on port " +
                            ProxyMeSettings.getInstance(project).proxyPort,
                        NotificationType.INFORMATION
                    );
                    setStatus(ProxyMeSettings.ProxyStatus.ACTIVE);
                } else {
                    notifyUser(
                        "Proxy started but health check failed",
                        NotificationType.WARNING
                    );
                    setStatus(ProxyMeSettings.ProxyStatus.WARNING);
                }
            } catch (Exception e) {
                notifyUser(
                    "Failed to start proxy: " + e.getMessage(),
                    NotificationType.ERROR
                );
                setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
                e.printStackTrace();
            }
        });
    }

    /**
     * Extract proxy files from plugin resources to temp directory
     */
    private void extractProxyFiles() throws IOException {
        // Create proxy directory in user's home (SHARED across all projects)
        String userHome = System.getProperty("user.home");
        File proxymeDir = new File(userHome, ".proxyme");
        proxymeDir.mkdirs();

        // Use SHARED directory for all projects (not project-specific)
        proxyDirectory = new File(proxymeDir, "proxy");
        proxyDirectory.mkdirs();

        // Setup log file (only logs are project-specific)
        File logsDir = new File(proxymeDir, "logs");
        logsDir.mkdirs();
        String projectName = project
            .getName()
            .replaceAll("[^a-zA-Z0-9.-]", "_");
        logFile = new File(logsDir, "proxyme-" + projectName + ".log");

        // Extract proxy files from resources
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
                InputStream in = classLoader.getResourceAsStream(
                    "proxy/" + fileName
                )
            ) {
                if (in != null) {
                    File targetFile = new File(proxyDirectory, fileName);
                    Files.copy(
                        in,
                        targetFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }
        }
    }

    /**
     * Check if Node.js is installed and find its path
     */
    private boolean isNodeJsInstalled() {
        String nodePath = findNodePath();
        return nodePath != null;
    }

    /**
     * Find Node.js executable path from common locations
     */
    private String findNodePath() {
        // Common Node.js installation locations
        String[] possiblePaths = {
            "node", // Try PATH first
            "/opt/homebrew/bin/node", // Homebrew on Apple Silicon
            "/usr/local/bin/node", // Homebrew on Intel Mac
            "/usr/bin/node", // System installation
            System.getProperty("user.home") + "/.nvm/versions/node/*/bin/node", // NVM
            "/usr/local/opt/node/bin/node", // Another Homebrew location
        };

        for (String path : possiblePaths) {
            try {
                // Expand wildcards for NVM
                if (path.contains("*")) {
                    File nvmDir = new File(
                        System.getProperty("user.home") + "/.nvm/versions/node/"
                    );
                    if (nvmDir.exists()) {
                        File[] versions = nvmDir.listFiles();
                        if (versions != null && versions.length > 0) {
                            path =
                                versions[versions.length -
                                1].getAbsolutePath() +
                                "/bin/node";
                        }
                    }
                }

                Process process = new ProcessBuilder(path, "--version").start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    writeToLog("Found Node.js at: " + path);
                    return path;
                }
            } catch (Exception e) {
                // Try next path
            }
        }

        writeToLog("Node.js not found in any common location");
        return null;
    }

    /**
     * Run npm install in proxy directory
     */
    private boolean runNpmInstall() {
        try {
            String nodePath = findNodePath();
            if (nodePath == null) {
                writeToLog("Cannot find Node.js for npm install");
                return false;
            }

            // Derive npm path from node path
            String npmPath = nodePath.replace("/node", "/npm");
            if (!new File(npmPath).exists()) {
                npmPath = "npm"; // Fallback to PATH
            }

            ProcessBuilder pb = new ProcessBuilder(npmPath, "install");
            pb.directory(proxyDirectory);
            pb.redirectErrorStream(true);

            // Add Node.js bin directory to PATH
            String nodeDir = new File(nodePath).getParent();
            Map<String, String> env = pb.environment();
            String currentPath = env.get("PATH");
            if (currentPath != null) {
                env.put("PATH", nodeDir + ":" + currentPath);
            } else {
                env.put("PATH", nodeDir);
            }

            Process process = pb.start();

            // Capture output
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
                )
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writeToLog("npm install: " + line);
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            writeToLog("npm install failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate .env file with API keys from settings
     */
    private void generateEnvFile() throws IOException {
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

        // Generate .env file in shared proxy directory
        File envFile = new File(proxyDirectory, ".env");

        // If .env already exists with keys, don't overwrite
        if (envFile.exists()) {
            writeToLog(".env file already exists, keeping existing API keys");
        } else {
            // Use ProxyMeEnvFileService to generate .env file with API keys from model configs
            ProxyMeEnvFileService envFileService = new ProxyMeEnvFileService();
            envFileService.generateEnvFile(project, settings);
        }
    }

    /**
     * Start the proxy process
     */
    private void startProxyProcess()
        throws IOException, com.intellij.execution.ExecutionException {
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

        // Build command
        String nodePath = findNodePath();
        if (nodePath == null) {
            throw new IOException(
                "Node.js not found. Please install from https://nodejs.org/"
            );
        }

        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(nodePath);
        commandLine.addParameter("proxy.js");
        commandLine.setWorkDirectory(proxyDirectory);

        // Add environment variables
        commandLine.withEnvironment("PORT", String.valueOf(settings.proxyPort));
        commandLine.withEnvironment("DEBUG", "true");

        // Start process
        processHandler = new OSProcessHandler(commandLine);

        // Attach output listener
        processHandler.addProcessListener(
            new ProcessAdapter() {
                @Override
                public void onTextAvailable(
                    @NotNull ProcessEvent event,
                    @NotNull Key outputType
                ) {
                    String text = event.getText().trim();
                    if (!text.isEmpty()) {
                        writeToLog(text);
                    }
                }

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    writeToLog(
                        "Proxy process terminated with exit code: " +
                            event.getExitCode()
                    );
                    setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
                    proxyProcess = null;
                    closeLog();
                }
            }
        );

        // Start the process
        processHandler.startNotify();
        proxyProcess = processHandler.getProcess();

        writeToLog("=== Proxy Started ===");
        writeToLog("Running: node proxy.js");
        writeToLog("Port: " + settings.proxyPort);
        writeToLog("Log file: " + logFile.getAbsolutePath());
    }

    /**
     * Check proxy health
     */
    private boolean checkHealth() {
        try {
            ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
            String healthUrl =
                "http://" +
                settings.proxyHost +
                ":" +
                settings.proxyPort +
                "/health";

            URL url = new URL(healthUrl);
            HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Stop proxy server
     */
    public void stopProxy() {
        if (!isProxyRunning()) {
            notifyUser("Proxy is not running", NotificationType.WARNING);
            setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
            return;
        }

        try {
            writeToLog("=== Stopping Proxy ===");

            // Try graceful shutdown first
            if (processHandler != null) {
                processHandler.destroyProcess();
            } else if (proxyProcess != null) {
                proxyProcess.destroy();
            }

            // Wait up to 5 seconds for graceful shutdown
            if (proxyProcess != null) {
                boolean terminated = proxyProcess.waitFor(5, TimeUnit.SECONDS);

                if (!terminated) {
                    // Force kill if not stopped
                    writeToLog("Forcing process termination...");
                    proxyProcess.destroyForcibly();
                    proxyProcess.waitFor(2, TimeUnit.SECONDS);
                }
            }

            // Also kill any orphaned Node processes on this port
            killOrphanedProcesses();

            proxyProcess = null;
            processHandler = null;
            setStatus(ProxyMeSettings.ProxyStatus.INACTIVE);
            closeLog();

            notifyUser("Proxy server stopped", NotificationType.INFORMATION);
        } catch (Exception e) {
            notifyUser(
                "Error stopping proxy: " + e.getMessage(),
                NotificationType.ERROR
            );
            writeToLog("Error stopping proxy: " + e.getMessage());
        }
    }

    /**
     * Kill any orphaned proxy processes on our port
     */
    private void killOrphanedProcesses() {
        try {
            ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
            int port = settings.proxyPort;

            // macOS/Linux: Use lsof to find process using the port
            ProcessBuilder pb = new ProcessBuilder("lsof", "-ti", ":" + port);
            Process process = pb.start();

            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
                )
            ) {
                String pid = reader.readLine();
                if (pid != null && !pid.isEmpty()) {
                    writeToLog(
                        "Killing orphaned process on port " +
                            port +
                            " (PID: " +
                            pid +
                            ")"
                    );
                    new ProcessBuilder("kill", "-9", pid).start().waitFor();
                }
            }

            process.waitFor();
        } catch (Exception e) {
            // Silently fail - this is a best-effort cleanup
            writeToLog("Could not kill orphaned processes: " + e.getMessage());
        }
    }

    /**
     * Restart proxy server (completely async version to prevent IDE crashes)
     * Build 4: Enhanced with complete separation of stop and start
     */
    public void restartProxy() {
        writeToLog("=== Restarting Proxy (Build 4) ===");

        // Immediate user feedback
        notifyUser(
            "Restarting proxy... Please wait 10-15 seconds",
            NotificationType.INFORMATION
        );

        // Run entire restart in background thread
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            boolean stopSuccess = false;

            try {
                // PHASE 1: Stop existing proxy
                writeToLog("PHASE 1: Stopping proxy...");
                if (isProxyRunning()) {
                    try {
                        // Graceful stop with timeout
                        stopProxy();
                        writeToLog("Proxy stopped successfully");
                        stopSuccess = true;
                    } catch (Exception e) {
                        writeToLog("Warning during stop: " + e.getMessage());
                        stopSuccess = false;
                    }

                    // Wait for process cleanup
                    writeToLog("Waiting for process cleanup (7 seconds)...");
                    Thread.sleep(7000);
                } else {
                    writeToLog("Proxy was not running");
                    stopSuccess = true;
                }

                // PHASE 2: Cleanup
                writeToLog("PHASE 2: Cleanup...");
                try {
                    killOrphanedProcesses();
                    writeToLog("Orphaned processes cleaned up");
                } catch (Exception e) {
                    writeToLog("Note: " + e.getMessage());
                }

                // Wait between phases
                Thread.sleep(2000);

                // PHASE 3: Regenerate configuration
                writeToLog("PHASE 3: Regenerating configuration...");
                try {
                    ProxyMeModelsConfigService modelsConfigService =
                        ProxyMeModelsConfigService.getInstance();
                    modelsConfigService.generateModelsConfig(project);
                    writeToLog("Models configuration generated");
                } catch (Exception e) {
                    writeToLog("Warning: " + e.getMessage());
                }

                // Regenerate .env with all API keys
                try {
                    ProxyMeSettings settings = ProxyMeSettings.getInstance(
                        project
                    );
                    ProxyMeEnvFileService envService =
                        new ProxyMeEnvFileService();
                    envService.generateEnvFile(project, settings);
                    writeToLog(".env file regenerated with all API keys");
                } catch (Exception e) {
                    writeToLog(
                        "Warning: Could not regenerate .env: " + e.getMessage()
                    );
                }

                // Wait for file system sync
                writeToLog("Waiting for file system sync (2 seconds)...");
                Thread.sleep(2000);

                // PHASE 4: Start proxy
                writeToLog("PHASE 4: Starting proxy...");
                final boolean finalStopSuccess = stopSuccess;

                // Schedule start on UI thread
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        launchProxy();
                        writeToLog(
                            "=== Proxy restart completed successfully ==="
                        );

                        // Success notification
                        ApplicationManager.getApplication().invokeLater(() -> {
                            notifyUser(
                                "Proxy restarted successfully",
                                NotificationType.INFORMATION
                            );
                        });
                    } catch (Exception e) {
                        writeToLog("ERROR starting proxy: " + e.getMessage());
                        e.printStackTrace();

                        ApplicationManager.getApplication().invokeLater(() -> {
                            notifyUser(
                                "Failed to start proxy after restart: " +
                                    e.getMessage(),
                                NotificationType.ERROR
                            );
                        });
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                writeToLog("Restart interrupted: " + e.getMessage());

                ApplicationManager.getApplication().invokeLater(() -> {
                    notifyUser(
                        "Proxy restart was interrupted",
                        NotificationType.WARNING
                    );
                });
            } catch (Exception e) {
                writeToLog("CRITICAL ERROR during restart: " + e.getMessage());
                e.printStackTrace();

                ApplicationManager.getApplication().invokeLater(() -> {
                    notifyUser(
                        "Restart failed: " +
                            e.getMessage() +
                            ". Try Stop then Start manually.",
                        NotificationType.ERROR
                    );
                });
            }
        });
    }

    /**
     * Write to log file
     */
    private void writeToLog(String message) {
        if (logFile == null) return;

        try {
            if (logWriter == null) {
                logWriter = new BufferedWriter(new FileWriter(logFile, true));
            }

            String timestamp = java.time.LocalDateTime.now().toString();
            logWriter.write("[" + timestamp + "] " + message + "\n");
            logWriter.flush();
        } catch (IOException e) {
            // Fail silently - don't want logging errors to break the app
        }
    }

    /**
     * Close log writer
     */
    private void closeLog() {
        if (logWriter != null) {
            try {
                logWriter.close();
                logWriter = null;
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    /**
     * Get log file location
     */
    public File getLogFile() {
        if (logFile == null) {
            String userHome = System.getProperty("user.home");
            File logsDir = new File(userHome, ".proxyme/logs");
            logsDir.mkdirs();
            String projectName = project
                .getName()
                .replaceAll("[^a-zA-Z0-9.-]", "_");
            logFile = new File(logsDir, "proxyme-" + projectName + ".log");
        }
        return logFile;
    }

    /**
     * Get proxy directory
     */
    public File getProxyDirectory() {
        if (proxyDirectory == null) {
            String userHome = System.getProperty("user.home");
            // SHARED proxy directory for all projects
            proxyDirectory = new File(userHome, ".proxyme/proxy");
        }
        return proxyDirectory;
    }

    /**
     * Show notification to user
     */
    private void notifyUser(String message, NotificationType type) {
        Notification notification = new Notification(
            "ProxyMe",
            "ProxyMe",
            message,
            type
        );
        Notifications.Bus.notify(notification, project);
    }

    public Project getProject() {
        return project;
    }
}
