package com.proxyme.rider.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.proxyme.rider.ProxyMeSettings;
import com.proxyme.rider.Template;
import com.proxyme.rider.TemplateService;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.*;

/**
 * Dialog for saving current configuration as a template
 */
public class SaveTemplateDialog extends JDialog {

    private final TemplateService templateService;
    private final ProxyMeSettings currentSettings;
    private Template savedTemplate;
    private boolean confirmed = false;

    // UI Components
    private JBTextField templateNameField;
    private JTextArea descriptionArea;
    private JBCheckBox includeApiKeysCheckBox;
    private JBCheckBox includeProxySettingsCheckBox;
    private JLabel warningLabel;
    private JButton saveButton;
    private JButton cancelButton;

    public SaveTemplateDialog(
        JFrame parent,
        TemplateService templateService,
        ProxyMeSettings currentSettings
    ) {
        super(parent, "Save Template", true);
        this.templateService = templateService;
        this.currentSettings = currentSettings;
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(550, 400));

        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Template Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JBLabel("Template Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        templateNameField = new JBTextField(30);
        templateNameField.setToolTipText(
            "Enter a unique name for this template"
        );
        formPanel.add(templateNameField, gbc);
        row++;

        // Description
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JBLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setToolTipText(
            "Enter a description for this template (optional)"
        );
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        row++;

        // Reset constraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Separator
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 10, 5);
        formPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;
        row++;

        // Options section header
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(new JBLabel("<html><b>Options:</b></html>"), gbc);
        gbc.gridwidth = 1;
        row++;

        // Include API Keys checkbox
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        includeApiKeysCheckBox = new JBCheckBox("Include API Keys in template");
        includeApiKeysCheckBox.setSelected(false);
        includeApiKeysCheckBox.setToolTipText(
            "If checked, API keys will be saved in the template (use with caution)"
        );
        includeApiKeysCheckBox.addActionListener(e -> updateWarningLabel());
        formPanel.add(includeApiKeysCheckBox, gbc);
        row++;

        // Include Proxy Settings checkbox
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        includeProxySettingsCheckBox = new JBCheckBox(
            "Include Proxy Settings (host, port, auto-launch)"
        );
        includeProxySettingsCheckBox.setSelected(true);
        includeProxySettingsCheckBox.setToolTipText(
            "If checked, proxy configuration will be included"
        );
        formPanel.add(includeProxySettingsCheckBox, gbc);
        row++;

        // Warning label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        warningLabel = new JLabel();
        warningLabel.setForeground(new Color(200, 100, 0));
        warningLabel.setVisible(false);
        formPanel.add(warningLabel, gbc);
        row++;

        // Info section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel infoLabel = new JLabel(
            "<html><small><i>Templates are saved to: ~/.proxyme/templates/</i></small></html>"
        );
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default button
        getRootPane().setDefaultButton(saveButton);

        // Initial warning state
        updateWarningLabel();
    }

    private void updateWarningLabel() {
        if (includeApiKeysCheckBox.isSelected()) {
            warningLabel.setText(
                "<html><b>⚠ Warning:</b> API keys will be stored in plain text. " +
                    "Only use this for personal templates.</html>"
            );
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
        }
    }

    private void onSave() {
        // Validate template name
        String templateName = templateNameField.getText().trim();
        if (templateName.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Template name is required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if template already exists
        if (templateService.templateExists(templateName)) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "A template with this name already exists. Overwrite?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Build template from current settings
        savedTemplate = buildTemplate(templateName);

        // Save the template
        boolean success = templateService.saveTemplate(savedTemplate);

        if (success) {
            confirmed = true;
            JOptionPane.showMessageDialog(
                this,
                "Template saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save template. Check the logs for details.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Template buildTemplate(String templateName) {
        Template template = new Template();
        template.setName(templateName);
        template.setDescription(descriptionArea.getText().trim());
        template.setVersion("1.0.0");
        template.setPreset(false);

        // Convert model configs
        List<ProxyMeSettings.ModelConfig> templateModels = new ArrayList<>();
        for (ProxyMeSettings.ModelConfig settingsModel : currentSettings.models) {
            ProxyMeSettings.ModelConfig templateModel =
                new ProxyMeSettings.ModelConfig();
            templateModel.modelName = settingsModel.modelName;
            templateModel.apiProvider = settingsModel.apiProvider;
            templateModel.apiEndpoint = settingsModel.apiEndpoint;
            templateModel.enabled = settingsModel.enabled;
            templateModel.modelCategory = settingsModel.modelCategory;

            // Include API key if checkbox is selected
            if (includeApiKeysCheckBox.isSelected()) {
                templateModel.apiKey = settingsModel.apiKey;
            } else {
                templateModel.apiKey = ""; // Don't include key
            }

            // Copy temperature and stream settings (v2.1.0)
            templateModel.temperature = settingsModel.temperature;
            templateModel.stream = settingsModel.stream;

            // Copy custom headers and body params if present
            if (
                settingsModel.customHeaders != null &&
                !settingsModel.customHeaders.isEmpty()
            ) {
                templateModel.customHeaders = new HashMap<>(
                    settingsModel.customHeaders
                );
            }
            if (
                settingsModel.customBodyParams != null &&
                !settingsModel.customBodyParams.isEmpty()
            ) {
                templateModel.customBodyParams = new HashMap<>(
                    settingsModel.customBodyParams
                );
            }

            templateModels.add(templateModel);
        }
        template.setModels(templateModels);

        // Include proxy settings if checkbox is selected
        if (includeProxySettingsCheckBox.isSelected()) {
            Template.ProxySettings proxySettings = new Template.ProxySettings();
            proxySettings.host = currentSettings.proxyHost;
            proxySettings.port = currentSettings.proxyPort;
            proxySettings.autoLaunch = currentSettings.autoLaunchOnStartup;
            template.setProxySettings(proxySettings);
        }

        return template;
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Template getSavedTemplate() {
        return savedTemplate;
    }
}
