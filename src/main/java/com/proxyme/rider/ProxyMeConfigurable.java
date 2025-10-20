package com.proxyme.rider;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.proxyme.rider.ProxyMeSettings;
import com.proxyme.rider.ui.ProxyMeSettingsPanel;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * ProxyMe Settings Configurable
 * Provides the UI for project-specific proxy settings
 */
public class ProxyMeConfigurable implements Configurable {

    private final Project project;
    private ProxyMeSettingsPanel settingsPanel;

    public ProxyMeConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ProxyMe";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsPanel = new ProxyMeSettingsPanel(project);
        reset(); // Initialize panel with current settings
        return settingsPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        if (settingsPanel == null) {
            return false;
        }
        ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
        if (settings == null) {
            return false;
        }
        return settingsPanel.isModified(settings);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
            settingsPanel.apply(settings);
        }
    }

    @Override
    public void reset() {
        if (settingsPanel != null) {
            ProxyMeSettings settings = ProxyMeSettings.getInstance(project);
            settingsPanel.reset(settings);
        }
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }
}
