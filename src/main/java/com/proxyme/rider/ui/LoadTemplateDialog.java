package com.proxyme.rider.ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.proxyme.rider.ProxyMeSettings;
import com.proxyme.rider.RiderAIContext;
import com.proxyme.rider.Template;
import com.proxyme.rider.TemplateService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog for loading templates (presets or user templates)
 */
public class LoadTemplateDialog extends JDialog {

    private final TemplateService templateService;
    private Template selectedTemplate;
    private boolean confirmed = false;

    // UI Components
    private JTabbedPane tabbedPane;
    private JBList<String> presetsList;
    private JBList<String> userTemplatesList;
    private JTextArea previewArea;
    private JButton loadButton;
    private JButton cancelButton;
    private JButton deleteButton;

    private List<Template> presets;
    private List<Template> userTemplates;

    public LoadTemplateDialog(JFrame parent, TemplateService templateService) {
        super(parent, "Load Template", true);
        this.templateService = templateService;
        initUI();
        loadTemplates();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(700, 500));

        // Create tabbed pane for Presets vs User Templates
        tabbedPane = new JTabbedPane();

        // Presets tab
        JPanel presetsPanel = createPresetsPanel();
        tabbedPane.addTab("Presets", presetsPanel);

        // User templates tab
        JPanel userTemplatesPanel = createUserTemplatesPanel();
        tabbedPane.addTab("My Templates", userTemplatesPanel);

        // Preview panel
        JPanel previewPanel = createPreviewPanel();

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Layout
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            tabbedPane,
            previewPanel
        );
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default button
        getRootPane().setDefaultButton(loadButton);
    }

    private JPanel createPresetsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info label
        JBLabel infoLabel = new JBLabel(
            "<html><i>Preset templates are pre-configured for common use cases</i></html>"
        );
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, BorderLayout.NORTH);

        // List
        presetsList = new JBList<>();
        presetsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        presetsList.addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        onPresetSelected();
                    }
                }
            }
        );

        JScrollPane scrollPane = new JScrollPane(presetsList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUserTemplatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info label
        JBLabel infoLabel = new JBLabel(
            "<html><i>Your saved template configurations</i></html>"
        );
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, BorderLayout.NORTH);

        // List
        userTemplatesList = new JBList<>();
        userTemplatesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTemplatesList.addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        onUserTemplateSelected();
                    }
                }
            }
        );

        JScrollPane scrollPane = new JScrollPane(userTemplatesList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Label
        JBLabel previewLabel = new JBLabel("Preview:");
        panel.add(previewLabel, BorderLayout.NORTH);

        // Preview text area
        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setText("Select a template to preview");

        JScrollPane scrollPane = new JScrollPane(previewArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.setToolTipText("Delete selected user template");
        deleteButton.addActionListener(e -> onDelete());

        loadButton = new JButton("Load");
        loadButton.setEnabled(false);
        loadButton.addActionListener(e -> onLoad());

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> onCancel());

        panel.add(deleteButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(loadButton);
        panel.add(cancelButton);

        return panel;
    }

    private void loadTemplates() {
        // Load presets
        presets = templateService.loadPresets();
        DefaultListModel<String> presetsModel = new DefaultListModel<>();
        for (Template template : presets) {
            presetsModel.addElement(template.getName());
        }
        presetsList.setModel(presetsModel);

        // Load user templates
        userTemplates = templateService.loadUserTemplates();
        DefaultListModel<String> userModel = new DefaultListModel<>();
        for (Template template : userTemplates) {
            userModel.addElement(template.getName());
        }
        userTemplatesList.setModel(userModel);

        // Show info if no user templates
        if (userTemplates.isEmpty()) {
            userTemplatesList.setEmptyText("No saved templates yet");
        }
    }

    private void onPresetSelected() {
        int index = presetsList.getSelectedIndex();
        if (index >= 0 && index < presets.size()) {
            selectedTemplate = presets.get(index);
            updatePreview(selectedTemplate);
            loadButton.setEnabled(true);
            deleteButton.setEnabled(false);
            userTemplatesList.clearSelection();
        }
    }

    private void onUserTemplateSelected() {
        int index = userTemplatesList.getSelectedIndex();
        if (index >= 0 && index < userTemplates.size()) {
            selectedTemplate = userTemplates.get(index);
            updatePreview(selectedTemplate);
            loadButton.setEnabled(true);
            deleteButton.setEnabled(true);
            presetsList.clearSelection();
        }
    }

    private void updatePreview(Template template) {
        if (template == null) {
            previewArea.setText("Select a template to preview");
            return;
        }

        StringBuilder preview = new StringBuilder();
        preview.append("Template: ").append(template.getName()).append("\n");
        preview
            .append("Description: ")
            .append(template.getDescription())
            .append("\n");
        preview.append("Version: ").append(template.getVersion()).append("\n");
        preview
            .append("Preset: ")
            .append(template.isPreset() ? "Yes" : "No")
            .append("\n");
        preview.append("\n");

        preview
            .append("Models (")
            .append(template.getModels().size())
            .append("):\n");
        preview.append("─────────────────────────────────────\n");

        for (int i = 0; i < template.getModels().size(); i++) {
            ProxyMeSettings.ModelConfig model = template.getModels().get(i);
            preview.append((i + 1)).append(". ");
            preview.append(model.modelName);
            preview.append(" (").append(model.apiProvider).append(")");
            if (!model.enabled) {
                preview.append(" [DISABLED]");
            }
            preview.append("\n");

            preview
                .append("   Endpoint: ")
                .append(model.apiEndpoint)
                .append("\n");
            preview
                .append("   Category: ")
                .append(model.modelCategory)
                .append("\n");

            if (model.apiKey != null && !model.apiKey.isEmpty()) {
                preview
                    .append("   API Key: ")
                    .append(maskApiKey(model.apiKey))
                    .append("\n");
            } else {
                preview.append("   API Key: (uses provider default)\n");
            }

            // Show temperature and stream settings (v2.1.0)
            preview
                .append("   Temperature: ")
                .append(String.format("%.2f", model.temperature))
                .append("\n");
            preview
                .append("   Stream: ")
                .append(model.stream ? "Enabled" : "Disabled")
                .append("\n");

            if (i < template.getModels().size() - 1) {
                preview.append("\n");
            }
        }

        preview.append("\n");
        preview.append("Proxy Settings:\n");
        preview.append("─────────────────────────────────────\n");
        if (template.getProxySettings() != null) {
            preview
                .append("Host: ")
                .append(template.getProxySettings().host)
                .append("\n");
            preview
                .append("Port: ")
                .append(template.getProxySettings().port)
                .append("\n");
            preview
                .append("Auto-launch: ")
                .append(template.getProxySettings().autoLaunch ? "Yes" : "No")
                .append("\n");
        } else {
            preview.append("(uses current settings)\n");
        }

        previewArea.setText(preview.toString());
        previewArea.setCaretPosition(0);
    }

    private String maskApiKey(String key) {
        if (key == null || key.isEmpty()) {
            return "(not set)";
        }
        if (key.length() <= 8) {
            return "••••••••";
        }
        return key.substring(0, 4) + "••••" + key.substring(key.length() - 4);
    }

    private void onLoad() {
        if (selectedTemplate == null) {
            return;
        }

        confirmed = true;
        dispose();
    }

    private void onDelete() {
        int index = userTemplatesList.getSelectedIndex();
        if (index < 0 || index >= userTemplates.size()) {
            return;
        }

        Template template = userTemplates.get(index);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete template '" +
                template.getName() +
                "'?\nThis cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = templateService.deleteTemplate(
                template.getName()
            );
            if (success) {
                // Refresh the list
                userTemplates.remove(index);
                DefaultListModel<String> model = (DefaultListModel<
                    String
                >) userTemplatesList.getModel();
                model.remove(index);

                selectedTemplate = null;
                previewArea.setText("Template deleted successfully");
                loadButton.setEnabled(false);
                deleteButton.setEnabled(false);

                JOptionPane.showMessageDialog(
                    this,
                    "Template deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete template",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Template getSelectedTemplate() {
        return selectedTemplate;
    }
}
