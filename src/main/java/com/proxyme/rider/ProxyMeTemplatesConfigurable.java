package com.proxyme.rider;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.proxyme.rider.ui.ProxyMeTemplatesPanel;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * ProxyMe Templates Configurable
 * Provides UI for managing user templates
 */
public class ProxyMeTemplatesConfigurable implements Configurable {

    private ProxyMeTemplatesPanel templatesPanel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ProxyMe Templates";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        templatesPanel = new ProxyMeTemplatesPanel();
        return templatesPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        ProxyMeTemplateSettings settings =
            ProxyMeTemplateSettings.getInstance();
        return templatesPanel != null && templatesPanel.isModified(settings);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (templatesPanel != null) {
            ProxyMeTemplateSettings settings =
                ProxyMeTemplateSettings.getInstance();
            templatesPanel.apply(settings);
        }
    }

    @Override
    public void reset() {
        if (templatesPanel != null) {
            ProxyMeTemplateSettings settings =
                ProxyMeTemplateSettings.getInstance();
            templatesPanel.reset(settings);
        }
    }

    @Override
    public void disposeUIResources() {
        templatesPanel = null;
    }
}
