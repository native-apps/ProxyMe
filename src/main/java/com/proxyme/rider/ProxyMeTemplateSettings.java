package com.proxyme.rider;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ProxyMe Application-Level Template Settings
 * Stores user templates that can be loaded into projects
 */
@State(
    name = "ProxyMeTemplateSettings",
    storages = @Storage("proxyme-templates.xml")
)
public class ProxyMeTemplateSettings
    implements PersistentStateComponent<ProxyMeTemplateSettings> {

    public List<ProxyTemplate> templates = new ArrayList<>();
    public String defaultTemplateName = "";

    public static ProxyMeTemplateSettings getInstance() {
        return ApplicationManager.getApplication().getService(
            ProxyMeTemplateSettings.class
        );
    }

    @Nullable
    @Override
    public ProxyMeTemplateSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProxyMeTemplateSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * Proxy Template
     */
    public static class ProxyTemplate {

        public String name = "";
        public String description = "";
        public int proxyPort = 3000;
        public List<ProxyMeSettings.ModelConfig> models = new ArrayList<>();
        public boolean showLogsInTerminal = true;
        public boolean saveLogsToFile = true;

        public ProxyTemplate() {}

        public ProxyTemplate(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
