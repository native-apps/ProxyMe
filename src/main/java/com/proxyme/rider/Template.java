package com.proxyme.rider;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Template data class for storing ProxyMe configuration templates.
 *
 * Templates can be:
 * - Preset templates: Shipped with plugin, read-only, no API keys
 * - User templates: Created by user, editable, optionally includes API keys
 *
 * Version 2.0.0 implementation.
 */
public class Template {

    private String name;
    private String description;
    private String version = "2.0";
    private boolean isPreset;
    private transient String filePath; // Not serialized to JSON

    // Model configurations
    private List<ProxyMeSettings.ModelConfig> models;

    // Proxy settings
    private ProxySettings proxySettings;

    /**
     * Default constructor for JSON deserialization.
     */
    public Template() {
        this.models = new ArrayList<>();
        this.proxySettings = new ProxySettings();
    }

    /**
     * Constructor with basic info.
     */
    public Template(String name, String description) {
        this.name = name;
        this.description = description;
        this.models = new ArrayList<>();
        this.proxySettings = new ProxySettings();
    }

    /**
     * Constructor with all fields.
     */
    public Template(
        String name,
        String description,
        boolean isPreset,
        List<ProxyMeSettings.ModelConfig> models,
        ProxySettings proxySettings
    ) {
        this.name = name;
        this.description = description;
        this.isPreset = isPreset;
        this.models = models != null ? models : new ArrayList<>();
        this.proxySettings = proxySettings != null
            ? proxySettings
            : new ProxySettings();
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isPreset() {
        return isPreset;
    }

    public void setPreset(boolean preset) {
        isPreset = preset;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<ProxyMeSettings.ModelConfig> getModels() {
        return models;
    }

    public void setModels(List<ProxyMeSettings.ModelConfig> models) {
        this.models = models;
    }

    public ProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(ProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    /**
     * Create a copy of this template.
     *
     * @return A new Template instance with copied values
     */
    @NotNull
    public Template copy() {
        Template copy = new Template(name, description, isPreset, null, null);
        copy.version = this.version;

        // Deep copy models
        if (this.models != null) {
            copy.models = new ArrayList<>();
            for (ProxyMeSettings.ModelConfig model : this.models) {
                ProxyMeSettings.ModelConfig modelCopy =
                    new ProxyMeSettings.ModelConfig(
                        model.modelName,
                        model.apiProvider,
                        model.apiEndpoint,
                        model.apiKey,
                        model.enabled,
                        model.modelCategory,
                        model.temperature,
                        model.stream
                    );
                // Copy custom headers and body params
                if (model.customHeaders != null) {
                    modelCopy.customHeaders = new java.util.HashMap<>(
                        model.customHeaders
                    );
                }
                if (model.customBodyParams != null) {
                    modelCopy.customBodyParams = new java.util.HashMap<>(
                        model.customBodyParams
                    );
                }
                copy.models.add(modelCopy);
            }
        }

        // Copy proxy settings
        if (this.proxySettings != null) {
            copy.proxySettings = new ProxySettings();
            copy.proxySettings.port = this.proxySettings.port;
            copy.proxySettings.host = this.proxySettings.host;
            copy.proxySettings.autoLaunch = this.proxySettings.autoLaunch;
        }

        return copy;
    }

    /**
     * Get display name for UI (shows preset indicator).
     *
     * @return Display name
     */
    public String getDisplayName() {
        return isPreset ? "⭐ " + name : name;
    }

    /**
     * Count how many models have API keys configured.
     *
     * @return Number of models with API keys
     */
    public int getModelsWithApiKeysCount() {
        if (models == null) {
            return 0;
        }

        int count = 0;
        for (ProxyMeSettings.ModelConfig model : models) {
            if (model.apiKey != null && !model.apiKey.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check if this template has any API keys configured.
     *
     * @return true if at least one model has an API key
     */
    public boolean hasApiKeys() {
        return getModelsWithApiKeysCount() > 0;
    }

    @Override
    public String toString() {
        return name + (isPreset ? " (preset)" : "");
    }

    /**
     * Proxy settings within a template.
     */
    public static class ProxySettings {

        public int port = 3000;
        public String host = "localhost";
        public boolean autoLaunch = false;

        public ProxySettings() {}

        public ProxySettings(int port, String host, boolean autoLaunch) {
            this.port = port;
            this.host = host;
            this.autoLaunch = autoLaunch;
        }
    }
}
