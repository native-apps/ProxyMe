package com.proxyme.rider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * Service for managing models.json configuration file
 * Generates dynamic model list based on ProxyMe settings
 *
 * Version 2.1.0: New service to enable dynamic model loading in proxy.js
 */
@Service(Service.Level.APP)
public final class ProxyMeModelsConfigService {

    private static final Logger LOG = Logger.getInstance(
        ProxyMeModelsConfigService.class
    );

    private static final String PROXYME_DIR = ".proxyme";
    private static final String PROXY_DIR = "proxy";
    private static final String MODELS_CONFIG_FILE = "models.json";

    public static ProxyMeModelsConfigService getInstance() {
        return ApplicationManager.getApplication().getService(
            ProxyMeModelsConfigService.class
        );
    }

    /**
     * Generate models.json from project settings
     * Only enabled models will be included
     */
    public boolean generateModelsConfig(@NotNull Project project) {
        try {
            ProxyMeSettings settings = ProxyMeSettings.getInstance(project);

            if (settings.models == null || settings.models.isEmpty()) {
                LOG.warn("No models configured in ProxyMe settings");
                return false;
            }

            // Filter enabled models only
            List<ProxyMeSettings.ModelConfig> enabledModels = settings.models
                .stream()
                .filter(model -> model.enabled)
                .toList();

            if (enabledModels.isEmpty()) {
                LOG.warn("No enabled models found in ProxyMe settings");
                showNotification(
                    "ProxyMe: No Enabled Models",
                    "No models are enabled. Please enable at least one model in ProxyMe settings.",
                    NotificationType.WARNING
                );
                return false;
            }

            // Build models configuration
            ModelsConfig config = new ModelsConfig();
            config.version = "2.1.0";
            config.generatedAt = new Date().toString();
            config.models = new ArrayList<>();

            for (ProxyMeSettings.ModelConfig model : enabledModels) {
                ModelEntry entry = new ModelEntry();
                entry.id = model.modelName;
                entry.provider = model.apiProvider;
                entry.endpoint = model.apiEndpoint;
                entry.enabled = model.enabled;
                entry.temperature = model.temperature;
                entry.stream = model.stream;
                entry.category = model.modelCategory;

                // Add custom headers (excluding Authorization which is handled separately)
                if (model.customHeaders != null && !model.customHeaders.isEmpty()) {
                    entry.customHeaders = new HashMap<>(model.customHeaders);
                    entry.customHeaders.remove("Authorization"); // Never include auth in config
                }

                // Add custom body parameters
                if (model.customBodyParams != null && !model.customBodyParams.isEmpty()) {
                    entry.customBodyParams = new HashMap<>(model.customBodyParams);
                }

                config.models.add(entry);
            }

            // Write to models.json
            File configFile = getModelsConfigFile();

            // Ensure parent directory exists
            File parentDir = configFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                LOG.error("Failed to create proxy directory: " + parentDir.getAbsolutePath());
                return false;
            }

            // Write JSON with pretty printing
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(config);

            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(json);
            }

            // Set file permissions (read/write for owner only)
            setSecurePermissions(configFile.toPath());

            LOG.info("Generated models.json with " + config.models.size() + " enabled models");

            showNotification(
                "ProxyMe: Models Config Updated",
                "Generated models.json with " + config.models.size() + " enabled model(s). Restart proxy to apply changes.",
                NotificationType.INFORMATION
            );

            return true;

        } catch (Exception e) {
            LOG.error("Failed to generate models.json", e);
            showNotification(
                "ProxyMe: Config Generation Failed",
                "Failed to generate models.json: " + e.getMessage(),
                NotificationType.ERROR
            );
            return false;
        }
    }

    /**
     * Get the models.json file location
     */
    @NotNull
    public File getModelsConfigFile() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, PROXYME_DIR, PROXY_DIR, MODELS_CONFIG_FILE).toFile();
    }

    /**
     * Check if models.json exists
     */
    public boolean modelsConfigExists() {
        return getModelsConfigFile().exists();
    }

    /**
     * Delete models.json (for cleanup)
     */
    public boolean deleteModelsConfig() {
        File configFile = getModelsConfigFile();
        if (configFile.exists()) {
            boolean deleted = configFile.delete();
            if (deleted) {
                LOG.info("Deleted models.json");
            }
            return deleted;
        }
        return true;
    }

    /**
     * Set secure file permissions (Unix-like systems only)
     */
    private void setSecurePermissions(@NotNull Path path) {
        try {
            if (path.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(path, perms);
                LOG.info("Set secure permissions (600) on: " + path);
            }
        } catch (IOException e) {
            LOG.warn("Could not set secure permissions on " + path + ": " + e.getMessage());
        }
    }

    /**
     * Show notification to user
     */
    private void showNotification(
        String title,
        String content,
        NotificationType type
    ) {
        Notification notification = new Notification(
            "ProxyMe.Notifications",
            title,
            content,
            type
        );
        Notifications.Bus.notify(notification);
    }

    /**
     * Models configuration structure for JSON serialization
     */
    private static class ModelsConfig {
        String version;
        String generatedAt;
        List<ModelEntry> models;
    }

    /**
     * Individual model entry for JSON serialization
     */
    private static class ModelEntry {
        String id;
        String provider;
        String endpoint;
        boolean enabled;
        double temperature;
        boolean stream;
        String category;
        Map<String, String> customHeaders;
        Map<String, String> customBodyParams;
    }
}
