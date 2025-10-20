package com.proxyme.rider.ui;

import com.proxyme.rider.ProxyMeTemplateSettings;
import javax.swing.*;

/**
 * ProxyMe Templates Panel
 * UI for managing user templates
 */
public class ProxyMeTemplatesPanel {

    private JPanel mainPanel;

    public ProxyMeTemplatesPanel() {
        mainPanel = new JPanel();
        mainPanel.add(new JLabel("Templates Panel - To be implemented"));
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified(ProxyMeTemplateSettings settings) {
        return false;
    }

    public void apply(ProxyMeTemplateSettings settings) {
        // To be implemented
    }

    public void reset(ProxyMeTemplateSettings settings) {
        // To be implemented
    }
}
