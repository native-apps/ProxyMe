package com.proxyme.rider.ui;

import com.intellij.ui.table.JBTable;
import com.proxyme.rider.ProxyMeSettings;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Model Configuration Panel (v2.1.0)
 * Enhanced table with Temperature and Stream columns
 * Removed Contexts column (Rider AI Assistant handles this natively)
 */
public class ModelConfigPanel {

    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JBTable modelTable;
    private JButton addModelButton;
    private JButton removeModelButton;
    private JButton editModelButton;

    private final String[] columnNames = {
        "Enabled",
        "Model Name",
        "Provider",
        "API Endpoint",
        "API Key",
        "Temperature",
        "Stream",
    };

    public ModelConfigPanel() {
        createUI();
    }

    private void createUI() {
        mainPanel = new JPanel(new BorderLayout());

        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) {
                    // Enabled and Stream
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Only enabled checkbox is editable in table
                // Other fields require Edit dialog
                return column == 0;
            }
        };

        modelTable = new JBTable(tableModel);
        modelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelTable.setRowHeight(25);

        // Configure column widths
        modelTable.getColumnModel().getColumn(0).setPreferredWidth(70); // Enabled
        modelTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Model Name
        modelTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Provider
        modelTable.getColumnModel().getColumn(3).setPreferredWidth(300); // API Endpoint
        modelTable.getColumnModel().getColumn(4).setPreferredWidth(130); // API Key
        modelTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Temperature
        modelTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Stream

        // Custom renderer for API Key column (masked)
        modelTable
            .getColumnModel()
            .getColumn(4)
            .setCellRenderer(new ApiKeyRenderer());

        // Custom renderer for Temperature column
        modelTable
            .getColumnModel()
            .getColumn(5)
            .setCellRenderer(new TemperatureRenderer());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addModelButton = new JButton("Add Model");
        removeModelButton = new JButton("Remove");
        editModelButton = new JButton("Edit");

        buttonPanel.add(addModelButton);
        buttonPanel.add(editModelButton);
        buttonPanel.add(removeModelButton);

        // Add to main panel
        mainPanel.add(new JScrollPane(modelTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Wire up button actions
        addModelButton.setToolTipText("Add a new AI model configuration");
        addModelButton.addActionListener(e -> addModel());

        editModelButton.setToolTipText("Edit selected model configuration");
        editModelButton.addActionListener(e -> editModel());

        removeModelButton.setToolTipText("Remove selected model");
        removeModelButton.addActionListener(e -> removeModel());
    }

    private void addModel() {
        ModelEditDialog dialog = new ModelEditDialog(null, "Add Model", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ProxyMeSettings.ModelConfig config = dialog.getModelConfig();
            tableModel.addRow(createTableRow(config));
        }
    }

    private void editModel() {
        int selectedRow = modelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                mainPanel,
                "Please select a model to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Create a ModelConfig from the selected row
        ProxyMeSettings.ModelConfig config = new ProxyMeSettings.ModelConfig();
        config.enabled = (Boolean) tableModel.getValueAt(selectedRow, 0);
        config.modelName = (String) tableModel.getValueAt(selectedRow, 1);
        config.apiProvider = (String) tableModel.getValueAt(selectedRow, 2);
        config.apiEndpoint = (String) tableModel.getValueAt(selectedRow, 3);
        config.apiKey = (String) tableModel.getValueAt(selectedRow, 4);
        config.modelCategory = "core"; // Default, user can change in Rider AI Assistant

        // Parse temperature
        String tempStr = (String) tableModel.getValueAt(selectedRow, 5);
        try {
            config.temperature = Double.parseDouble(tempStr);
        } catch (NumberFormatException e) {
            config.temperature = 0.7;
        }

        config.stream = (Boolean) tableModel.getValueAt(selectedRow, 6);

        ModelEditDialog dialog = new ModelEditDialog(
            null,
            "Edit Model",
            config
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ProxyMeSettings.ModelConfig updated = dialog.getModelConfig();
            updateTableRow(selectedRow, updated);
        }
    }

    private void removeModel() {
        int selectedRow = modelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                mainPanel,
                "Please select a model to remove",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            mainPanel,
            "Remove model: " + tableModel.getValueAt(selectedRow, 1) + "?",
            "Confirm Remove",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
        }
    }

    private Object[] createTableRow(ProxyMeSettings.ModelConfig config) {
        return new Object[] {
            config.enabled,
            config.modelName,
            config.apiProvider,
            config.apiEndpoint,
            config.apiKey != null ? config.apiKey : "",
            String.format("%.2f", config.temperature),
            config.stream,
        };
    }

    private void updateTableRow(int row, ProxyMeSettings.ModelConfig config) {
        tableModel.setValueAt(config.enabled, row, 0);
        tableModel.setValueAt(config.modelName, row, 1);
        tableModel.setValueAt(config.apiProvider, row, 2);
        tableModel.setValueAt(config.apiEndpoint, row, 3);
        tableModel.setValueAt(
            config.apiKey != null ? config.apiKey : "",
            row,
            4
        );
        tableModel.setValueAt(
            String.format("%.2f", config.temperature),
            row,
            5
        );
        tableModel.setValueAt(config.stream, row, 6);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified(List<ProxyMeSettings.ModelConfig> models) {
        if (tableModel.getRowCount() != models.size()) {
            return true;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            ProxyMeSettings.ModelConfig model = models.get(i);
            if (
                model.enabled != (Boolean) tableModel.getValueAt(i, 0)
            ) return true;
            if (
                !model.modelName.equals(tableModel.getValueAt(i, 1))
            ) return true;
            if (
                !model.apiProvider.equals(tableModel.getValueAt(i, 2))
            ) return true;
            if (
                !model.apiEndpoint.equals(tableModel.getValueAt(i, 3))
            ) return true;

            String tableApiKey = (String) tableModel.getValueAt(i, 4);
            if (
                (model.apiKey == null && !tableApiKey.isEmpty()) ||
                (model.apiKey != null && !model.apiKey.equals(tableApiKey))
            ) {
                return true;
            }

            // Check temperature
            String tempStr = (String) tableModel.getValueAt(i, 5);
            try {
                double tableTemp = Double.parseDouble(tempStr);
                if (Math.abs(model.temperature - tableTemp) > 0.01) return true;
            } catch (NumberFormatException e) {
                return true;
            }

            // Check stream
            if (
                model.stream != (Boolean) tableModel.getValueAt(i, 6)
            ) return true;
        }

        return false;
    }

    public void apply(List<ProxyMeSettings.ModelConfig> models) {
        if (models == null) return;
        models.clear();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            ProxyMeSettings.ModelConfig model =
                new ProxyMeSettings.ModelConfig();
            model.enabled = (Boolean) tableModel.getValueAt(i, 0);
            model.modelName = (String) tableModel.getValueAt(i, 1);
            model.apiProvider = (String) tableModel.getValueAt(i, 2);
            model.apiEndpoint = (String) tableModel.getValueAt(i, 3);
            model.apiKey = (String) tableModel.getValueAt(i, 4);
            model.modelCategory = "core"; // Default, Rider AI Assistant handles assignment

            // Parse temperature
            String tempStr = (String) tableModel.getValueAt(i, 5);
            try {
                model.temperature = Double.parseDouble(tempStr);
            } catch (NumberFormatException e) {
                model.temperature = 0.7;
            }

            model.stream = (Boolean) tableModel.getValueAt(i, 6);

            models.add(model);
        }
    }

    public void reset(List<ProxyMeSettings.ModelConfig> models) {
        tableModel.setRowCount(0);

        if (models == null || models.isEmpty()) {
            // Add default models if empty
            addDefaultModels();
            return;
        }

        for (ProxyMeSettings.ModelConfig model : models) {
            tableModel.addRow(createTableRow(model));
        }
    }

    private void addDefaultModels() {
        // Add default recommended models
        ProxyMeSettings.ModelConfig deepseekChat =
            new ProxyMeSettings.ModelConfig();
        deepseekChat.enabled = true;
        deepseekChat.modelName = "deepseek-chat";
        deepseekChat.apiProvider = "deepseek";
        deepseekChat.apiEndpoint = "https://api.deepseek.com/chat/completions";
        deepseekChat.apiKey = "";
        deepseekChat.modelCategory = "core";
        deepseekChat.temperature = 0.7;
        deepseekChat.stream = true;

        tableModel.addRow(createTableRow(deepseekChat));
    }

    /**
     * Custom renderer for API Key column - masks the key
     */
    private static class ApiKeyRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
        ) {
            Component c = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            );

            if (value != null && !value.toString().isEmpty()) {
                String key = value.toString();
                String masked = ProxyMeSettings.ModelConfig.getMaskedApiKey(
                    key
                );
                ((JLabel) c).setText(masked);
                ((JLabel) c).setToolTipText(
                    "API Key configured (masked for security)"
                );
            } else {
                ((JLabel) c).setText("(not set)");
                ((JLabel) c).setForeground(Color.GRAY);
                ((JLabel) c).setToolTipText("No API key configured");
            }

            return c;
        }
    }

    /**
     * Custom renderer for Temperature column
     */
    private static class TemperatureRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
        ) {
            Component c = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            );

            if (value != null) {
                String tempStr = value.toString();
                ((JLabel) c).setText(tempStr);
                ((JLabel) c).setToolTipText(
                    "Temperature: " +
                        tempStr +
                        " (0.0 = deterministic, 2.0 = creative)"
                );

                // Color code based on temperature
                try {
                    double temp = Double.parseDouble(tempStr);
                    if (temp < 0.3) {
                        ((JLabel) c).setForeground(new Color(0, 100, 200)); // Blue for low
                    } else if (temp > 1.5) {
                        ((JLabel) c).setForeground(new Color(200, 100, 0)); // Orange for high
                    } else if (!isSelected) {
                        ((JLabel) c).setForeground(table.getForeground()); // Normal
                    }
                } catch (NumberFormatException e) {
                    // Keep default color
                }
            }

            return c;
        }
    }
}
