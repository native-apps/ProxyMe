package com.proxyme.rider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.components.Service;
import com.proxyme.rider.ProxyMeTemplateSettings;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * ProxyMe Template Service
 * Manages saving and loading of configuration templates
 */
@Service(Service.Level.APP)
public final class ProxyMeTemplateService {

    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    /**
     * Export template to JSON file
     */
    public void exportTemplate(
        ProxyMeTemplateSettings.ProxyTemplate template,
        File file
    ) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(template, writer);
        }
    }

    /**
     * Import template from JSON file
     */
    public ProxyMeTemplateSettings.ProxyTemplate importTemplate(File file)
        throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(
                reader,
                ProxyMeTemplateSettings.ProxyTemplate.class
            );
        }
    }

    /**
     * Get default templates directory in user home
     */
    public File getTemplatesDirectory() {
        String userHome = System.getProperty("user.home");
        File templatesDir = new File(userHome, ".proxyme/templates");
        if (!templatesDir.exists()) {
            templatesDir.mkdirs();
        }
        return templatesDir;
    }
}
