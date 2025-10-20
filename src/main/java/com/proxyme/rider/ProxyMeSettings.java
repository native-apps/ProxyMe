package com.proxyme.rider;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ProxyMe Project Settings
 * Stores project-specific configuration for ProxyMe plugin.
 *
 * Version 2.1.0 changes:
 * - Added temperature, stream, customHeaders, customBodyParams to ModelConfig
 * - Removed assignedContexts (Rider AI Assistant handles context assignment)
 * - Enhanced model control to filter which models appear in Rider
 *
 * Version 2.0.0 changes:
 * - API keys moved into ModelConfig (no longer separate fields)
 * - Added assignedContexts to ModelConfig for Rider AI integration
 * - Removed apiKeyName field (replaced with direct apiKey storage)
 */
@State(
    name = "ProxyMeSettings",
    storages = @Storage(
        value = "proxyme-settings.xml",
        roamingType = RoamingType.DISABLED
    )
)
public class ProxyMeSettings
    implements PersistentStateComponent<ProxyMeSettings> {

    // Auto-launch settings (disabled by default for first-time users)
    public boolean autoLaunchOnStartup = false;

    // Proxy configuration
    public int proxyPort = 3000;
    public String proxyHost = "localhost";

    // Logging settings
    public boolean showLogsInTerminal = true;
    public boolean saveLogsToFile = true;
    public String logFilePath =
        System.getProperty("user.home") + "/.proxyme/logs/proxyme.log";

    // Model configurations
    public List<ModelConfig> models = new ArrayList<>();

    // Template settings
    public String selectedTemplate = "";
    public String lastLoadedTemplate = "";
    public String currentTemplateName = "";

    // Status
    public ProxyStatus lastStatus = ProxyStatus.INACTIVE;

    public static ProxyMeSettings getInstance(@NotNull Project project) {
        return project.getService(ProxyMeSettings.class);
    }

    @Nullable
    @Override
    public ProxyMeSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProxyMeSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * Model Configuration
     *
     * Version 2.1.0: Added ProxyAI-inspired settings (temperature, stream, custom headers/body).
     *                Removed assignedContexts (Rider handles this natively).
     * Version 2.0.0: Now includes API key directly.
     */
    public static class ModelConfig {

        public String modelName = "";
        public String apiProvider = ""; // deepseek, perplexity, custom
        public String apiEndpoint = "";

        // API key stored directly with model (secured by file permissions)
        public String apiKey = "";

        public boolean enabled = true;
        public String modelCategory = "core"; // core, instant, completion

        // NEW in v2.1.0: ProxyAI-inspired per-model settings
        public double temperature = 0.3; // 0.0 - 2.0 (default: 0.3 for focused, precise responses)
        public boolean stream = true;

        // Custom headers and body parameters (like ProxyAI)
        public Map<String, String> customHeaders = new HashMap<>();
        public Map<String, String> customBodyParams = new HashMap<>();

        public ModelConfig() {
            // Initialize with default headers
            customHeaders.put("Content-Type", "application/json");
        }

        public ModelConfig(
            String modelName,
            String apiProvider,
            String apiEndpoint
        ) {
            this();
            this.modelName = modelName;
            this.apiProvider = apiProvider;
            this.apiEndpoint = apiEndpoint;
            this.apiKey = "";
        }

        public ModelConfig(
            String modelName,
            String apiProvider,
            String apiEndpoint,
            String apiKey,
            boolean enabled,
            String modelCategory,
            double temperature,
            boolean stream
        ) {
            this();
            this.modelName = modelName;
            this.apiProvider = apiProvider;
            this.apiEndpoint = apiEndpoint;
            this.apiKey = apiKey != null ? apiKey : "";
            this.enabled = enabled;
            this.modelCategory = modelCategory;
            this.temperature = temperature;
            this.stream = stream;
        }

        /**
         * Get masked API key for display (shows as sk-••••••).
         */
        public String getMaskedApiKey() {
            return getMaskedApiKey(this.apiKey);
        }

        /**
         * Get masked API key for display (shows as sk-••••••).
         * Static version for use in renderers.
         */
        public static String getMaskedApiKey(String apiKey) {
            if (apiKey == null || apiKey.isEmpty()) {
                return "(not set)";
            }

            // Show first 3 chars + masked + last 4 chars
            if (apiKey.length() <= 8) {
                return "••••••••";
            }

            String prefix = apiKey.substring(0, Math.min(4, apiKey.length()));
            String suffix = apiKey.substring(apiKey.length() - 4);
            return prefix + "••••" + suffix;
        }
    }

    /**
     * Proxy Status Enum
     */
    public enum ProxyStatus {
        ACTIVE, // Green - Running normally
        WARNING, // Orange - Running with warnings
        INACTIVE, // Red - Not running
    }
}
