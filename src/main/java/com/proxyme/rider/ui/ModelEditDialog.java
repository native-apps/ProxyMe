package com.proxyme.rider.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.proxyme.rider.ProxyMeSettings;
import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

/**
 * Dialog for adding/editing model configurations
 * Version 2.1.0: Added Temperature and Stream settings, removed Contexts
 */
public class ModelEditDialog extends JDialog {

    private final ProxyMeSettings.ModelConfig originalConfig;
    private ProxyMeSettings.ModelConfig resultConfig;
    private boolean confirmed = false;

    // Form fields
    private JBTextField modelNameField;
    private JComboBox<String> providerComboBox;
    private JBTextField apiEndpointField;
    private JPasswordField apiKeyField;
    private JBCheckBox enabledCheckBox;

    // New in v2.1.0: ProxyAI-inspired settings
    private JSlider temperatureSlider;
    private JLabel temperatureValueLabel;
    private JBCheckBox streamCheckBox;

    // Buttons
    private JButton okButton;
    private JButton cancelButton;

    public ModelEditDialog(
        JFrame parent,
        String title,
        ProxyMeSettings.ModelConfig config
    ) {
        super(parent, title, true);
        this.originalConfig = config;
        initUI();
        if (config != null) {
            populateFields(config);
        } else {
            setDefaultValues();
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(650, 500));

        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Enabled checkbox
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        enabledCheckBox = new JBCheckBox(
            "Enabled (models not enabled will not appear in Rider AI Assistant)"
        );
        enabledCheckBox.setSelected(true);
        formPanel.add(enabledCheckBox, gbc);
        row++;

        // Model Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JBLabel("Model Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        modelNameField = new JBTextField(30);
        formPanel.add(modelNameField, gbc);
        row++;

        // Provider
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JBLabel("Provider:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        providerComboBox = new JComboBox<>(
            new String[] {
                "deepseek",
                "perplexity",
                "openai",
                "anthropic",
                "other",
            }
        );
        providerComboBox.setEditable(true);
        providerComboBox.addActionListener(e ->
            updateEndpointBasedOnProvider()
        );
        formPanel.add(providerComboBox, gbc);
        row++;

        // API Endpoint
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JBLabel("API Endpoint:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        apiEndpointField = new JBTextField(40);
        formPanel.add(apiEndpointField, gbc);
        row++;

        // API Key
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JBLabel("API Key:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        apiKeyField = new JPasswordField(40);
        formPanel.add(apiKeyField, gbc);
        row++;

        // Helper text for API key
        gbc.gridx = 1;
        gbc.gridy = row;
        JLabel keyHelpLabel = new JLabel(
            "<html><small><i>Per-model API key (optional, uses provider key from .env if empty)</i></small></html>"
        );
        keyHelpLabel.setForeground(Color.GRAY);
        formPanel.add(keyHelpLabel, gbc);
        row++;

        // Separator
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 5, 10, 5);
        formPanel.add(
            new JBLabel("<html><b>Model Parameters:</b></html>"),
            gbc
        );
        gbc.insets = new Insets(5, 5, 5, 5);
        row++;

        // Temperature slider
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JPanel tempLabelPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        tempLabelPanel.add(new JBLabel("Temperature:"));
        temperatureValueLabel = new JLabel("0.7");
        temperatureValueLabel.setFont(
            temperatureValueLabel.getFont().deriveFont(Font.BOLD)
        );
        temperatureValueLabel.setForeground(new Color(100, 150, 255));
        temperatureValueLabel.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        );
        tempLabelPanel.add(temperatureValueLabel);
        formPanel.add(tempLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        temperatureSlider = new JSlider(0, 200, 70); // 0.0 to 2.0, default 0.7
        temperatureSlider.setMajorTickSpacing(50);
        temperatureSlider.setMinorTickSpacing(10);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(false);
        temperatureSlider.addChangeListener(e -> {
            double value = temperatureSlider.getValue() / 100.0;
            temperatureValueLabel.setText(String.format("%.2f", value));
        });
        formPanel.add(temperatureSlider, gbc);
        row++;

        // Temperature help text
        gbc.gridx = 1;
        gbc.gridy = row;
        JLabel tempHelpLabel = new JLabel(
            "<html><small><i>Controls randomness: 0.0 = deterministic, 2.0 = very creative</i></small></html>"
        );
        tempHelpLabel.setForeground(Color.GRAY);
        formPanel.add(tempHelpLabel, gbc);
        row++;

        // Stream checkbox
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        streamCheckBox = new JBCheckBox(
            "Enable streaming responses (recommended)"
        );
        streamCheckBox.setSelected(true);
        streamCheckBox.setToolTipText(
            "Stream responses for real-time output in Rider AI Assistant"
        );
        formPanel.add(streamCheckBox, gbc);
        row++;

        // Info panel
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 5, 5, 5);
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        );
        infoPanel.setBackground(new Color(240, 245, 250));
        JLabel infoLabel = new JLabel(
            "<html><small><b>Note:</b> Rider AI Assistant handles model assignment to features " +
                "(Chat, Inline Edit, Commits, etc.) in its native settings. " +
                "This dialog only controls which models are available.</small></html>"
        );
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        formPanel.add(infoPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default button
        getRootPane().setDefaultButton(okButton);
    }

    private void setDefaultValues() {
        enabledCheckBox.setSelected(true);
        modelNameField.setText("");
        providerComboBox.setSelectedItem("deepseek");
        apiEndpointField.setText("https://api.deepseek.com/chat/completions");
        apiKeyField.setText("");
        temperatureSlider.setValue(30); // 0.3 (focused, precise responses)
        streamCheckBox.setSelected(true);
    }

    private void populateFields(ProxyMeSettings.ModelConfig config) {
        enabledCheckBox.setSelected(config.enabled);
        modelNameField.setText(config.modelName);
        providerComboBox.setSelectedItem(config.apiProvider);
        apiEndpointField.setText(config.apiEndpoint);
        apiKeyField.setText(config.apiKey != null ? config.apiKey : "");

        // Set temperature (0.0 - 2.0)
        int tempValue = (int) Math.round(config.temperature * 100);
        temperatureSlider.setValue(tempValue);
        temperatureValueLabel.setText(
            String.format("%.2f", config.temperature)
        );

        // Set stream
        streamCheckBox.setSelected(config.stream);
    }

    private void updateEndpointBasedOnProvider() {
        String provider = (String) providerComboBox.getSelectedItem();
        if (provider == null) return;

        // Only update if the field is empty or has a default value
        String currentEndpoint = apiEndpointField.getText().trim();

        switch (provider.toLowerCase()) {
            case "deepseek":
                if (
                    currentEndpoint.isEmpty() ||
                    isDefaultEndpoint(currentEndpoint)
                ) {
                    apiEndpointField.setText(
                        "https://api.deepseek.com/chat/completions"
                    );
                }
                break;
            case "perplexity":
                if (
                    currentEndpoint.isEmpty() ||
                    isDefaultEndpoint(currentEndpoint)
                ) {
                    apiEndpointField.setText(
                        "https://api.perplexity.ai/chat/completions"
                    );
                }
                break;
            case "openai":
                if (
                    currentEndpoint.isEmpty() ||
                    isDefaultEndpoint(currentEndpoint)
                ) {
                    apiEndpointField.setText(
                        "https://api.openai.com/v1/chat/completions"
                    );
                }
                break;
            case "anthropic":
                if (
                    currentEndpoint.isEmpty() ||
                    isDefaultEndpoint(currentEndpoint)
                ) {
                    apiEndpointField.setText(
                        "https://api.anthropic.com/v1/messages"
                    );
                }
                break;
        }
    }

    private boolean isDefaultEndpoint(String endpoint) {
        return (
            endpoint.contains("deepseek.com") ||
            endpoint.contains("perplexity.ai") ||
            endpoint.contains("openai.com") ||
            endpoint.contains("anthropic.com")
        );
    }

    private void onOk() {
        // Validate inputs
        String modelName = modelNameField.getText().trim();
        if (modelName.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Model name is required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String apiEndpoint = apiEndpointField.getText().trim();
        if (apiEndpoint.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "API endpoint is required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Build result config
        resultConfig = new ProxyMeSettings.ModelConfig();
        resultConfig.enabled = enabledCheckBox.isSelected();
        resultConfig.modelName = modelName;
        resultConfig.apiProvider = (String) providerComboBox.getSelectedItem();
        resultConfig.apiEndpoint = apiEndpoint;

        String apiKey = new String(apiKeyField.getPassword()).trim();
        resultConfig.apiKey = apiKey.isEmpty() ? "" : apiKey;

        resultConfig.modelCategory = "core"; // Default, Rider AI Assistant handles assignment

        // Set temperature and stream
        resultConfig.temperature = temperatureSlider.getValue() / 100.0;
        resultConfig.stream = streamCheckBox.isSelected();

        // Initialize custom headers with default Content-Type
        resultConfig.customHeaders = new HashMap<>();
        resultConfig.customHeaders.put("Content-Type", "application/json");

        // Initialize empty custom body params
        resultConfig.customBodyParams = new HashMap<>();

        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ProxyMeSettings.ModelConfig getModelConfig() {
        return resultConfig;
    }
}
