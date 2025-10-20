package com.proxyme.rider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.diagnostic.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service for managing ProxyMe configuration templates.
 *
 * Templates are stored in:
 * - Preset templates: ~/.proxyme/templates/presets/ (read-only, shipped with plugin)
 * - User templates: ~/.proxyme/templates/user/ (user-created, editable)
 *
 * Version 2.0.0 implementation.
 */
public class TemplateService {

    private static final Logger LOG = Logger.getInstance(TemplateService.class);
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private final Path templatesDir;
    private final Path presetsDir;
    private final Path userDir;

    public TemplateService() {
        String homeDir = System.getProperty("user.home");
        this.templatesDir = Paths.get(homeDir, ".proxyme", "templates");
        this.presetsDir = templatesDir.resolve("presets");
        this.userDir = templatesDir.resolve("user");

        // Ensure directories exist
        initializeDirectories();
    }

    /**
     * Initialize template directory structure.
     */
    private void initializeDirectories() {
        try {
            Files.createDirectories(presetsDir);
            Files.createDirectories(userDir);
            LOG.info("Template directories initialized: " + templatesDir);
        } catch (IOException e) {
            LOG.error("Failed to create template directories", e);
        }
    }

    /**
     * Load all preset templates (shipped with plugin).
     *
     * @return List of preset templates
     */
    @NotNull
    public List<Template> loadPresets() {
        List<Template> templates = new ArrayList<>();

        try {
            File presetsFolder = presetsDir.toFile();
            if (!presetsFolder.exists() || !presetsFolder.isDirectory()) {
                LOG.warn("Presets directory does not exist: " + presetsDir);
                return templates;
            }

            File[] files = presetsFolder.listFiles((dir, name) ->
                name.endsWith(".json")
            );

            if (files != null) {
                for (File file : files) {
                    try {
                        String json = Files.readString(file.toPath());
                        Template template = GSON.fromJson(json, Template.class);
                        if (template != null) {
                            template.setPreset(true);
                            template.setFilePath(file.getAbsolutePath());
                            templates.add(template);
                        }
                    } catch (Exception e) {
                        LOG.warn(
                            "Failed to load preset template: " + file.getName(),
                            e
                        );
                    }
                }
            }

            LOG.info("Loaded " + templates.size() + " preset templates");
        } catch (Exception e) {
            LOG.error("Error loading preset templates", e);
        }

        return templates;
    }

    /**
     * Load all user templates (user-created).
     *
     * @return List of user templates
     */
    @NotNull
    public List<Template> loadUserTemplates() {
        List<Template> templates = new ArrayList<>();

        try {
            File userFolder = userDir.toFile();
            if (!userFolder.exists() || !userFolder.isDirectory()) {
                LOG.info(
                    "User templates directory does not exist yet: " + userDir
                );
                return templates;
            }

            File[] files = userFolder.listFiles((dir, name) ->
                name.endsWith(".json")
            );

            if (files != null) {
                for (File file : files) {
                    try {
                        String json = Files.readString(file.toPath());
                        Template template = GSON.fromJson(json, Template.class);
                        if (template != null) {
                            template.setPreset(false);
                            template.setFilePath(file.getAbsolutePath());
                            templates.add(template);
                        }
                    } catch (Exception e) {
                        LOG.warn(
                            "Failed to load user template: " + file.getName(),
                            e
                        );
                    }
                }
            }

            LOG.info("Loaded " + templates.size() + " user templates");
        } catch (Exception e) {
            LOG.error("Error loading user templates", e);
        }

        return templates;
    }

    /**
     * Load all templates (presets + user).
     *
     * @return List of all templates
     */
    @NotNull
    public List<Template> loadAllTemplates() {
        List<Template> allTemplates = new ArrayList<>();
        allTemplates.addAll(loadPresets());
        allTemplates.addAll(loadUserTemplates());
        return allTemplates;
    }

    /**
     * Load a specific template by name.
     *
     * @param name Template name (without .json extension)
     * @return The template, or null if not found
     */
    @Nullable
    public Template loadTemplate(@NotNull String name) {
        // Clean the name
        String cleanName = name.replace(".json", "");

        // Try user templates first
        Path userFile = userDir.resolve(cleanName + ".json");
        if (Files.exists(userFile)) {
            try {
                String json = Files.readString(userFile);
                Template template = GSON.fromJson(json, Template.class);
                if (template != null) {
                    template.setPreset(false);
                    template.setFilePath(userFile.toString());
                    return template;
                }
            } catch (Exception e) {
                LOG.error("Failed to load user template: " + name, e);
            }
        }

        // Try preset templates
        Path presetFile = presetsDir.resolve(cleanName + ".json");
        if (Files.exists(presetFile)) {
            try {
                String json = Files.readString(presetFile);
                Template template = GSON.fromJson(json, Template.class);
                if (template != null) {
                    template.setPreset(true);
                    template.setFilePath(presetFile.toString());
                    return template;
                }
            } catch (Exception e) {
                LOG.error("Failed to load preset template: " + name, e);
            }
        }

        LOG.warn("Template not found: " + name);
        return null;
    }

    /**
     * Save a template to user templates directory.
     *
     * @param template The template to save
     * @return true if saved successfully
     */
    public boolean saveTemplate(@NotNull Template template) {
        try {
            // Clean the name
            String cleanName = template
                .getName()
                .replace(".json", "")
                .replaceAll("[^a-zA-Z0-9-_]", "_");

            // Save to user directory
            Path filePath = userDir.resolve(cleanName + ".json");
            String json = GSON.toJson(template);
            Files.writeString(filePath, json);

            LOG.info("Template saved: " + filePath);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to save template: " + template.getName(), e);
            return false;
        }
    }

    /**
     * Save a template to user templates directory with options.
     *
     * @param template The template to save
     * @param name Template name (without .json extension)
     * @param includeApiKeys Whether to include API keys in the template
     * @return true if saved successfully
     */
    public boolean saveTemplate(
        @NotNull Template template,
        @NotNull String name,
        boolean includeApiKeys
    ) {
        try {
            // Clean the name
            String cleanName = name
                .replace(".json", "")
                .replaceAll("[^a-zA-Z0-9-_]", "_");

            // Create a copy of the template
            Template toSave = template.copy();
            toSave.setName(name);

            // Remove API keys if requested
            if (!includeApiKeys && toSave.getModels() != null) {
                for (ProxyMeSettings.ModelConfig model : toSave.getModels()) {
                    model.apiKey = "";
                }
            }

            // Save to user directory
            Path filePath = userDir.resolve(cleanName + ".json");
            String json = GSON.toJson(toSave);
            Files.writeString(filePath, json);

            LOG.info("Template saved: " + filePath);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to save template: " + name, e);
            return false;
        }
    }

    /**
     * Delete a user template.
     *
     * @param name Template name (without .json extension)
     * @return true if deleted successfully
     */
    public boolean deleteTemplate(@NotNull String name) {
        try {
            String cleanName = name.replace(".json", "");
            Path filePath = userDir.resolve(cleanName + ".json");

            if (!Files.exists(filePath)) {
                LOG.warn("Template does not exist: " + name);
                return false;
            }

            Files.delete(filePath);
            LOG.info("Template deleted: " + filePath);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to delete template: " + name, e);
            return false;
        }
    }

    /**
     * Check if a template exists.
     *
     * @param name Template name (without .json extension)
     * @return true if template exists
     */
    public boolean templateExists(@NotNull String name) {
        String cleanName = name.replace(".json", "");
        return (
            Files.exists(userDir.resolve(cleanName + ".json")) ||
            Files.exists(presetsDir.resolve(cleanName + ".json"))
        );
    }

    /**
     * Get the templates directory path.
     *
     * @return Path to templates directory
     */
    @NotNull
    public Path getTemplatesDirectory() {
        return templatesDir;
    }

    /**
     * Get the user templates directory path.
     *
     * @return Path to user templates directory
     */
    @NotNull
    public Path getUserTemplatesDirectory() {
        return userDir;
    }

    /**
     * Get the preset templates directory path.
     *
     * @return Path to preset templates directory
     */
    @NotNull
    public Path getPresetTemplatesDirectory() {
        return presetsDir;
    }

    /**
     * Copy preset templates from plugin resources to user directory.
     * This is called on first launch to populate preset templates.
     *
     * @return true if presets were copied successfully
     */
    public boolean copyPresetsFromResources() {
        LOG.info("Copying preset templates from plugin resources...");

        String[] presetFiles = {
            "recommended.json",
            "all-deepseek.json",
            "all-perplexity.json",
            "deepseek-only.json",
            "research-setup.json",
            "fast-setup.json",
            "complete.json",
        };

        int successCount = 0;
        int failCount = 0;

        for (String filename : presetFiles) {
            try {
                // Load from resources
                String resourcePath = "/templates/presets/" + filename;
                InputStream resourceStream = getClass().getResourceAsStream(
                    resourcePath
                );

                if (resourceStream == null) {
                    LOG.warn(
                        "Preset template not found in resources: " + filename
                    );
                    failCount++;
                    continue;
                }

                // Copy to presets directory
                Path targetPath = presetsDir.resolve(filename);

                // Only copy if file doesn't exist or is different
                if (
                    !Files.exists(targetPath) ||
                    shouldOverwritePreset(targetPath, resourceStream)
                ) {
                    // Re-open stream if we used it for comparison
                    if (Files.exists(targetPath)) {
                        resourceStream.close();
                        resourceStream = getClass().getResourceAsStream(
                            resourcePath
                        );
                    }

                    Files.copy(
                        resourceStream,
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING
                    );
                    LOG.info("Copied preset template: " + filename);
                    successCount++;
                } else {
                    LOG.debug(
                        "Preset template already exists and is up to date: " +
                            filename
                    );
                    successCount++;
                }

                resourceStream.close();
            } catch (Exception e) {
                LOG.error("Failed to copy preset template: " + filename, e);
                failCount++;
            }
        }

        LOG.info(
            "Preset templates copied: " +
                successCount +
                " succeeded, " +
                failCount +
                " failed"
        );

        return failCount == 0;
    }

    /**
     * Check if a preset template should be overwritten.
     * For now, always returns false to preserve existing presets.
     *
     * @param existingFile The existing preset file
     * @param resourceStream The resource stream (unused for now)
     * @return true if should overwrite
     */
    private boolean shouldOverwritePreset(
        Path existingFile,
        InputStream resourceStream
    ) {
        // For now, don't overwrite existing presets
        // In future versions, we could compare content or versions
        return false;
    }

    /**
     * Ensure preset templates are available.
     * Call this on plugin startup to copy presets if needed.
     *
     * @return true if presets are available
     */
    public boolean ensurePresetsAvailable() {
        // Check if any presets exist
        try {
            File presetsFolder = presetsDir.toFile();
            if (!presetsFolder.exists()) {
                LOG.info(
                    "Presets directory does not exist, creating and copying..."
                );
                return copyPresetsFromResources();
            }

            File[] files = presetsFolder.listFiles((dir, name) ->
                name.endsWith(".json")
            );

            if (files == null || files.length == 0) {
                LOG.info(
                    "No preset templates found, copying from resources..."
                );
                return copyPresetsFromResources();
            }

            LOG.debug("Preset templates available: " + files.length + " files");
            return true;
        } catch (Exception e) {
            LOG.error("Error checking preset templates", e);
            return false;
        }
    }
}
