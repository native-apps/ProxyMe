package com.proxyme.rider;

/**
 * Enum representing the different contexts where Rider AI Assistant can use models.
 * Each context corresponds to a specific feature in JetBrains Rider IDE.
 *
 * Based on analysis of ProxyAI plugin and Rider AI Assistant capabilities.
 */
public enum RiderAIContext {
    /**
     * General conversations in the AI Assistant panel.
     * Used for Q&A, code explanations, debugging help, etc.
     */
    CHAT("chat", "Chat", "General conversations and Q&A in AI Assistant panel"),

    /**
     * Code modifications including Quick Edit and "Modify Selected Code" feature.
     * Used when user selects code and requests modifications via right-click menu.
     */
    INLINE_EDIT(
        "inline_edit",
        "Inline Edit",
        "Quick Edit and Modify Selected Code"
    ),

    /**
     * Automatic code suggestions and completions.
     * Used for AI-powered suggestions that can be automatically applied.
     */
    AUTO_APPLY("auto_apply", "Auto Apply", "Automatic code suggestions"),

    /**
     * Git commit message generation.
     * Used to generate meaningful commit messages based on changes.
     */
    COMMITS("commits", "Commits", "Git commit message generation"),

    /**
     * Variable and function naming suggestions.
     * Used to suggest better names for code elements.
     */
    NAMING("naming", "Naming", "Variable and function naming suggestions");

    private final String id;
    private final String displayName;
    private final String description;

    RiderAIContext(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get context by ID string.
     *
     * @param id The context ID (e.g., "chat", "inline_edit")
     * @return The matching RiderAIContext, or null if not found
     */
    public static RiderAIContext fromId(String id) {
        if (id == null) {
            return null;
        }

        for (RiderAIContext context : values()) {
            if (context.id.equals(id)) {
                return context;
            }
        }

        return null;
    }

    /**
     * Get default contexts for a model based on its capabilities.
     *
     * @param modelName The name of the model (e.g., "deepseek-chat", "sonar")
     * @return Array of recommended contexts for this model
     */
    public static RiderAIContext[] getDefaultContextsForModel(
        String modelName
    ) {
        if (modelName == null) {
            return new RiderAIContext[] { CHAT };
        }

        // DeepSeek Chat - versatile, good for everything
        if (modelName.contains("deepseek-chat")) {
            return new RiderAIContext[] { CHAT, INLINE_EDIT, COMMITS, NAMING };
        }

        // DeepSeek Reasoner - powerful but slower, best for complex tasks
        if (modelName.contains("deepseek-reasoner")) {
            return new RiderAIContext[] { CHAT, INLINE_EDIT };
        }

        // Perplexity Sonar - fast, good for chat and quick edits
        if (modelName.equals("sonar")) {
            return new RiderAIContext[] { CHAT, INLINE_EDIT, NAMING };
        }

        // Perplexity Sonar Pro - enhanced capabilities
        if (modelName.equals("sonar-pro")) {
            return new RiderAIContext[] { CHAT, INLINE_EDIT, COMMITS, NAMING };
        }

        // Perplexity Sonar Reasoning - reasoning with search
        if (modelName.contains("sonar-reasoning")) {
            return new RiderAIContext[] { CHAT, INLINE_EDIT };
        }

        // Perplexity Deep Research - specialized for research, slow
        if (modelName.contains("deep-research")) {
            return new RiderAIContext[] { CHAT };
        }

        // Default: just chat
        return new RiderAIContext[] { CHAT };
    }

    /**
     * Get default context assignments for a model as a Set.
     * This is a convenience method that wraps getDefaultContextsForModel.
     *
     * @param modelName The name of the model (e.g., "deepseek-chat", "sonar")
     * @return Set of recommended contexts for this model
     */
    public static java.util.Set<RiderAIContext> getDefaultContexts(
        String modelName
    ) {
        RiderAIContext[] contexts = getDefaultContextsForModel(modelName);
        java.util.Set<RiderAIContext> contextSet = new java.util.HashSet<>();
        for (RiderAIContext context : contexts) {
            contextSet.add(context);
        }
        return contextSet;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
