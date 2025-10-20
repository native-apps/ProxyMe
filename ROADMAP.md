# ProxyMe - Comprehensive Development Roadmap

**Version:** 2.0.0 (Planning)  
**Current Version:** 1.0.3  
**Last Updated:** October 20, 2025  
**Status:** 🚀 Active Development

---

## 📋 Table of Contents

1. [Project Vision](#-project-vision)
2. [Current Status](#-current-status-v103)
3. [Version 2.0.0 - Major Redesign](#-version-200---major-redesign)
4. [Future Versions](#-future-versions)
5. [Long-Term Vision](#-long-term-vision-jetbrains-marketplace)
6. [Known Issues](#-known-issues)
7. [Ideas & Explorations](#-ideas--explorations)

---

## 🎯 Project Vision

**Mission:** Provide a free, open-source alternative to expensive JetBrains AI subscriptions by enabling developers to use their own AI API keys (DeepSeek, Perplexity, etc.) directly in Rider IDE.

**Core Values:**
- 🆓 **Free & Open Source**
- 🔐 **Privacy First** (API keys stored locally, encrypted)
- 🎨 **Native Integration** (No custom UI, works with Rider AI Assistant)
- 🚀 **Easy Setup** (Preset templates, one-click configuration)
- 🌍 **Community Driven** (Open to contributions)

---

## ✅ Current Status (v1.0.3)

### What's Working

#### ✅ Core Functionality
- [x] Node.js proxy server launching and management
- [x] Shared dependency architecture (`~/.proxyme/proxy/`)
- [x] Health check endpoint with model status
- [x] OpenAI-compatible API endpoints
- [x] Streaming support for real-time responses
- [x] Automatic Node.js path detection

#### ✅ Rider IDE Integration
- [x] Native AI Assistant Chat mode integration
- [x] 7 AI models available (DeepSeek + Perplexity)
- [x] Settings panel with proxy controls
- [x] Status bar widget
- [x] Tool window with live logs
- [x] Launch/Stop/Restart/Health Check buttons

#### ✅ Models Supported
1. `deepseek-chat` - General purpose (DeepSeek-V3.1)
2. `deepseek-reasoner` - Advanced reasoning with CoT
3. `sonar` - Lightweight search with grounding
4. `sonar-pro` - Advanced search
5. `sonar-reasoning` - Real-time reasoning
6. `sonar-reasoning-pro` - Precise reasoning (DeepSeek-R1)
7. `sonar-deep-research` - Exhaustive research

#### ✅ UX Features
- [x] Inline dependency installer with progress
- [x] One-time shared dependency setup
- [x] Status bar widget (green/red indicator)
- [x] Real-time log viewer in tool window
- [x] Auto-launch on Rider startup (optional)
- [x] Proxy helper script for debugging

#### ✅ Documentation
- [x] Comprehensive README.md
- [x] DEBUGGING_GUIDE.md
- [x] SUCCESS_AND_NEXT_STEPS.md
- [x] DEEPSEEK_API_ENDPOINTS.md
- [x] API_ENDPOINT_SUMMARY.md
- [x] RIDER_INTEGRATION_MODES.md
- [x] CHEATSHEET.md
- [x] Proxy helper script

### ⚠️ Known Limitations
- Quick Edit mode has patch format issues (AI returns conversational text instead of XML patches)
- FIM (Fill-In-Middle) code completion not supported yet
- Template system exists but not fully functional
- API keys stored in separate fields (not integrated with model configuration)
- No preset templates for easy setup
- Can't assign models to specific Rider AI contexts

---

## 🚀 Version 2.0.0 - Major Redesign

**Target Release:** Q1 2025  
**Status:** Planning Phase  
**Breaking Changes:** Yes (settings format migration required)

### 🎨 Settings UI Redesign

#### Move API Keys Into Model Configuration
- ✨ **NEW:** API Key column in Model Configuration table
- ✨ **NEW:** Each model has its own API key (encrypted)
- 🗑️ **REMOVE:** Separate API key fields at top of settings

#### Template System Overhaul
- ✨ **NEW:** Working Load/Save template buttons
- ✨ **NEW:** Template storage in `~/.proxyme/templates/`
- ✨ **NEW:** 7 preset templates packaged with plugin:
  1. **Recommended** (DeepSeek Chat + Sonar) ⭐
  2. All DeepSeek Models
  3. All Perplexity Models
  4. DeepSeek Only
  5. Research Setup (Reasoner + Deep Research)
  6. Fast Setup (optimized for speed)
  7. Complete (all 7 models)
- ✨ **NEW:** Template preview before loading
- ✨ **NEW:** Include/exclude API keys when saving templates
- ✨ **NEW:** Share templates with team (export/import)

#### Rider AI Context Assignment
- ✨ **NEW:** Assign models to specific Rider AI contexts:
  - **Chat** - General conversations in AI Assistant
  - **Inline Edit** - Quick Edit / Modify Selected Code
  - **Auto Apply** - Automatic suggestions
  - **Commits** - Git commit message generation
  - **Naming** - Variable/function naming suggestions
- ✨ **NEW:** Multi-select checkboxes for context assignment
- ✨ **NEW:** Default assignments based on model capabilities
- ✨ **NEW:** Validation (at least one model per context)

#### Enhanced Model Configuration Table
- ✨ **NEW:** Columns:
  - Enabled (checkbox)
  - Model Name
  - Provider (DeepSeek/Perplexity)
  - API Key (encrypted, shown as `sk-••••••`)
  - Assigned Contexts (Chat, Edit, +2)
  - Category (Core/Research/Fast)
- ✨ **NEW:** Inline editing for API keys
- ✨ **NEW:** Context assignment dialog
- ✨ **NEW:** Drag-and-drop row reordering
- ✨ **NEW:** "Load Preset..." dropdown for quick setup

### 🔐 Security Enhancements
- ✨ **NEW:** API key encryption using Rider's PasswordSafe
- ✨ **NEW:** Never log API keys (show as masked)
- ✨ **NEW:** Encrypted template storage (optional)
- ✨ **NEW:** Secure env file generation (chmod 600)

### 📦 Template System Details

#### Template File Format (JSON)
```json
{
  "name": "Recommended Setup",
  "description": "DeepSeek Chat + Sonar",
  "version": "2.0",
  "isPreset": true,
  "models": [
    {
      "modelName": "deepseek-chat",
      "apiProvider": "deepseek",
      "apiKey": "",
      "enabled": true,
      "assignedContexts": ["chat", "edit", "commits", "naming"]
    }
  ],
  "proxySettings": {
    "port": 3000,
    "autoLaunch": true
  }
}
```

#### Template Storage Structure
```
~/.proxyme/
├── proxy/                    # Shared proxy files
├── logs/                     # Log files
└── templates/                # NEW
    ├── presets/              # Built-in (read-only)
    │   ├── recommended.json
    │   ├── all-deepseek.json
    │   ├── all-perplexity.json
    │   ├── deepseek-only.json
    │   ├── research-setup.json
    │   ├── fast-setup.json
    │   └── complete.json
    └── user/                 # User-saved
        ├── my-setup.json
        └── work-config.json
```

### 🔧 Implementation Phases

#### Phase 1: Data Model Refactoring (1-2 days)
- [ ] Update `ProxyMeSettings.ModelConfig`:
  - Add `apiKey` field (encrypted String)
  - Add `assignedContexts` field (List<String>)
  - Remove `apiKeyName` field
- [ ] Remove top-level API key fields from `ProxyMeSettings`
- [ ] Create `RiderAIContext` enum
- [ ] Create `TemplateService` class
- [ ] Implement `APIKeyEncryptionService`
- [ ] Update `ProxyMeEnvFileService` to read keys from models

#### Phase 2: Template System (2-3 days)
- [ ] Create template directory structure
- [ ] Implement `TemplateService` methods:
  - `List<Template> loadPresetTemplates()`
  - `List<Template> loadUserTemplates()`
  - `void saveTemplate(Template, String name)`
  - `Template loadTemplate(String name)`
  - `void deleteTemplate(String name)`
- [ ] Create 7 preset template JSON files
- [ ] Package presets in plugin resources
- [ ] Copy presets to `~/.proxyme/templates/` on first launch

#### Phase 3: UI Redesign (3-4 days)
- [ ] Remove API key fields from top of settings panel
- [ ] Add API Key column to model table
- [ ] Add Contexts column to model table
- [ ] Create `ModelEditDialog` with:
  - Model name, provider, endpoint fields
  - API key field (masked input)
  - Context assignment checkboxes
- [ ] Create `TemplateLoadDialog`:
  - Show preset templates section
  - Show user templates section
  - Template preview panel
  - Search/filter functionality
- [ ] Create `TemplateSaveDialog`:
  - Name and description fields
  - "Include API keys" checkbox
  - Save location selection
- [ ] Wire up "Load Template", "Save Template", "Delete Template" buttons
- [ ] Add "Load Preset..." dropdown button

#### Phase 4: Context Assignment (2-3 days)
- [ ] Research Rider AI context configuration API
- [ ] Implement model-to-context mapping
- [ ] Test context switching in Rider AI Assistant
- [ ] Add context validation logic
- [ ] Document which contexts work with ProxyMe

#### Phase 5: Migration & Testing (2-3 days)
- [ ] Implement automatic settings migration from v1.x
- [ ] Test all 7 preset templates
- [ ] Test template save/load
- [ ] Test API key encryption/decryption
- [ ] Test "Modify Selected Code" with context assignments
- [ ] Update all documentation
- [ ] Create migration guide

#### Phase 6: Documentation (1-2 days)
- [ ] Create `TEMPLATE_GUIDE.md`
- [ ] Create `MIGRATION_GUIDE_V2.md`
- [ ] Create `CONTEXT_ASSIGNMENT.md`
- [ ] Update `README.md` with new screenshots
- [ ] Update `SUCCESS_AND_NEXT_STEPS.md`
- [ ] Create video tutorial for template system

### 📊 Success Metrics (v2.0.0)
- [ ] Settings migration works for 100% of v1.x users
- [ ] All 7 preset templates load successfully
- [ ] Template save/load roundtrip preserves configuration
- [ ] API keys encrypted and never logged
- [ ] Models correctly assigned to Rider AI contexts
- [ ] "Modify Selected Code" works with assigned models
- [ ] User documentation covers all new features

### 🎯 Timeline
**Estimated Duration:** 10-15 days  
**Target Completion:** December 2025  

---

## 🔮 Future Versions

### Version 2.1.0 - Quick Edit Fix

**Focus:** Solve the Quick Edit patch format issue

#### Planned Features
- [ ] **Response Post-Processing**
  - Detect Quick Edit requests (check for patch instructions in system prompt)
  - Parse AI conversational responses
  - Extract code changes
  - Convert to XML patch format: `<llm-patch path="...">...</llm-patch>`
  - Return formatted patch to Rider
- [ ] **Enhanced System Prompts**
  - Add stronger XML format examples
  - Include sample patches in prompt
  - Test with DeepSeek-Reasoner (better instruction following)
- [ ] **Model-Specific Handling**
  - Identify which models naturally follow patch format
  - Route Quick Edit to compatible models
  - Fallback to Chat mode if needed
- [ ] **Documentation**
  - Document which models work best for Quick Edit
  - Provide workarounds for incompatible models

#### Success Metrics
- [ ] Quick Edit works with at least 3 models
- [ ] Patch format conversion success rate > 80%
- [ ] Clear user guidance on model selection

---

### Version 2.2.0 - FIM Support (Code Completion)

**Focus:** Add Fill-In-Middle code completion support

**Status:** 🔜 Future (Low priority, user-requested feature)

#### What Is FIM?
- **FIM** = Fill-In-Middle code completion
- Inline autocomplete as you type (like GitHub Copilot)
- Uses DeepSeek `/beta/completions` endpoint
- Different from Chat mode (not conversational)

#### Planned Features
- [ ] **FIM Endpoint Integration**
  - Add `/beta/completions` endpoint support to proxy
  - Implement FIM request/response format
  - Handle prefix + suffix completion
- [ ] **Rider IDE Integration**
  - Research Rider's inline completion API
  - Determine if third-party completion providers supported
  - May require custom plugin architecture
- [ ] **Model Support**
  - `deepseek-chat` (FIM-enabled)
  - Optional: Other FIM-compatible models
- [ ] **Settings Configuration**
  - Enable/disable FIM feature
  - Assign model for FIM completions
  - Configure max tokens (4K limit)
- [ ] **Alternative: Inception Labs Mercury Editor**
  - Investigate Mercury Editor integration
  - Compare with DeepSeek FIM
  - Evaluate as alternative option

#### Implementation Challenges
- **Unknown:** Does Rider support third-party inline completion?
- **Complex:** Different architecture from Chat mode
- **Alternative:** May need custom completion UI
- **Priority:** Low (Chat mode is more valuable)

#### Decision Points
- [ ] Research Rider completion plugin API
- [ ] Evaluate user demand for FIM
- [ ] Determine if separate plugin needed
- [ ] Consider focusing on Chat/Quick Edit instead

#### Why Not Prioritized?
1. ✅ Chat mode already works perfectly
2. ✅ Native Rider integration is our strength
3. ⚠️ FIM requires complex IDE integration
4. ⚠️ Rider may not support third-party completion
5. ⚠️ Limited to DeepSeek models only
6. 💡 Chat mode covers most use cases

---

### Version 2.3.0 - Advanced Features

#### Model Management
- [ ] Custom model addition (any OpenAI-compatible API)
- [ ] Model testing (validate API key and endpoint)
- [ ] Model usage statistics (requests, tokens, costs)
- [ ] Model performance metrics (latency, error rates)

#### Template Enhancements
- [ ] Template marketplace (share with community)
- [ ] Template versioning
- [ ] Template diff (compare configurations)
- [ ] Automatic template updates

#### Proxy Features
- [ ] Multiple proxy instances (different ports)
- [ ] Proxy performance dashboard
- [ ] Request/response logging (optional, secure)
- [ ] Request rate limiting
- [ ] Cost tracking per model

---

### Version 3.0.0 - Multi-IDE Support

**Focus:** Expand beyond Rider to other IDEs

#### Target IDEs
- [ ] **IntelliJ IDEA** (same plugin base as Rider)
- [ ] **VS Code** (separate extension)
- [ ] **Zed IDE** (extension system)
- [ ] **Theia IDE** (extension system)
- [ ] **WebStorm, PyCharm, etc.** (JetBrains family)

#### Architecture
- **Shared proxy** (same Node.js proxy for all IDEs)
- **IDE-specific plugins** (different integration per IDE)
- **Common configuration** (settings sync across IDEs)

---

## 🎯 Long-Term Vision: JetBrains Marketplace

### Mission Statement

Make AI coding assistance affordable and accessible for all developers by creating a free, open-source alternative to expensive proprietary AI subscriptions.

### The Problem Today
- JetBrains AI costs $10/month
- OpenAI API subscriptions are expensive
- Vendor lock-in with proprietary models
- No control over API keys or privacy

### The Solution Tomorrow
- ✅ **ProxyMe Plugin** on JetBrains Marketplace
- ✅ Use your own API keys (DeepSeek, Perplexity, OpenAI)
- ✅ Pay only for what you use
- ✅ Full control over data and privacy
- ✅ Support for multiple AI providers
- ✅ Native Rider integration (no custom UI)

### JetBrains Marketplace Roadmap

#### Phase 1: Plugin Preparation (v2.0-2.2)
- Complete settings redesign
- Implement template system
- Fix Quick Edit mode
- Comprehensive documentation
- Video tutorials

#### Phase 2: JetBrains Submission (v2.5)
- [ ] Create plugin listing
- [ ] Prepare marketing materials
- [ ] Screenshots and videos
- [ ] Submit to JetBrains Marketplace
- [ ] Address review feedback

#### Phase 3: Launch & Growth (v3.0+)
- [ ] Public launch announcement
- [ ] Community feedback collection
- [ ] Regular updates and bug fixes
- [ ] Add more AI providers
- [ ] Expand to other JetBrains IDEs

### Target Goals (First Year)
- 🎯 1,000+ downloads
- 🎯 100+ active users
- 🎯 4.5+ star rating
- 🎯 Active community contributions
- 🎯 Support for 10+ AI models

---

## 🐛 Known Issues

### Critical (Must Fix for v2.0)
- [ ] **Quick Edit Mode Patch Format**
  - AI returns conversational text instead of XML patches
  - Rider shows "The patch is incomplete" error
  - **Solution:** Implement response post-processing (v2.1)
- [ ] **Template Load/Save Buttons Don't Work**
  - Currently no implementation
  - **Solution:** Implement TemplateService (v2.0)

### High Priority
- [ ] **API Keys Not Integrated with Models**
  - Separate fields, disconnected UX
  - **Solution:** Move keys into model configuration (v2.0)
- [ ] **No Preset Templates**
  - Users must manually configure everything
  - **Solution:** Package 7 preset templates (v2.0)
- [ ] **Context Assignment Missing**
  - Can't assign models to specific Rider features
  - **Solution:** Implement context assignment (v2.0)

### Medium Priority
- [ ] Settings migration from v1.x to v2.0
- [ ] API key encryption implementation
- [ ] Template sharing/export functionality
- [ ] Model performance metrics
- [ ] Usage cost tracking

### Low Priority
- [ ] FIM (Fill-In-Middle) code completion
- [ ] Multiple proxy instances
- [ ] Dark/light theme icons
- [ ] Plugin keyboard shortcuts
- [ ] Advanced debugging tools

---

## 💡 Ideas & Explorations

### Security Enhancements
- [ ] **Encrypted API Key Storage** (High Priority for v2.0)
  - Use Rider's PasswordSafe
  - Or implement dotenvx-style encryption
  - Never commit `.env` to git
  - Consider HashiCorp Vault integration
  - AWS Secrets Manager support
  - SOPS + git-crypt alternative

### Advanced Features
- [ ] **Perplexity MCP Support**
  - Advanced search tools
  - Similar to Cline integration
- [ ] **Headless Plugin Architecture**
  - Run ProxyMe as background service
  - IDE-independent proxy
  - Multiple IDE connections
- [ ] **Usage Monitoring**
  - Fetch real usage from cloud providers
  - Don't self-log (unreliable metrics)
  - DeepSeek usage API
  - Perplexity usage API
- [ ] **Distributed Proxy**
  - Investigate libP2P framework
  - LocalAI.io for distributed web
  - (Currently LocalAI doesn't support cloud API proxying)

### UX Improvements
- [ ] In-IDE troubleshooting panel
  - Mirror DEBUGGING_GUIDE.md in native UI
  - Interactive diagnostics
  - One-click fixes
- [ ] Onboarding wizard
  - First-time setup guide
  - API key acquisition help
  - Model recommendations
- [ ] Model recommendations
  - Suggest best model for task
  - Cost vs performance analysis
  - Real-time model status

### Community Features
- [ ] Template marketplace
  - Share configurations
  - Community ratings
  - Template search
- [ ] Plugin contribution guide
  - Clear coding standards
  - Testing requirements
  - PR templates
- [ ] Sponsor/support links
  - GitHub Sponsors
  - Ko-fi integration
  - Community funding

---

## 📅 Version History

### v1.0.3 (Current) - October 20, 2025
- ✅ Shared dependency architecture
- ✅ Inline dependency installer
- ✅ Status bar widget
- ✅ Tool window with logs
- ✅ Node.js path detection
- ✅ 7 AI models supported
- ✅ Comprehensive documentation
- ✅ API endpoint investigation completed

### v1.0.2 - October 19, 2025
- ✅ Fixed npm install PATH issues
- ✅ Improved dependency installation UX
- ✅ Added startup notifications

### v1.0.1 - October 19, 2025
- ✅ Basic plugin functionality
- ✅ Manual dependency installation
- ✅ Settings panel
- ✅ Proxy launch controls

### v1.0.0 - October 17, 2025
- ✅ Initial release
- ✅ Node.js proxy server
- ✅ DeepSeek and Perplexity support
- ✅ OpenAI-compatible API

---

## 🤝 Contributing

We welcome contributions! Areas where help is needed:

### High Priority
- 🎨 UI/UX design for settings panel
- 🔐 Security review (API key encryption)
- 📝 Documentation improvements
- 🧪 Testing across different environments

### Medium Priority
- 🐛 Bug fixes and issue resolution
- 🎨 Icon design (dark/light themes)
- 📹 Video tutorial creation
- 🌍 Internationalization (i18n)

### Exploration
- 🔬 Rider completion plugin API research
- 🔬 FIM integration feasibility
- 🔬 Alternative AI provider support
- 🔬 Distributed proxy architecture

---

## 📞 Questions & Decisions Needed

### Immediate (v2.0)
- [ ] Approve settings redesign plan?
- [ ] Priority order for implementation phases?
- [ ] API key encryption method (PasswordSafe vs custom)?
- [ ] Template JSON format finalized?

### Short Term (v2.1-2.2)
- [ ] Implement Quick Edit fix in v2.1 or defer?
- [ ] Add FIM support in v2.2 or skip?
- [ ] Focus on Rider only or expand to IntelliJ?

### Long Term (v3.0+)
- [ ] Multi-IDE support timeline?
- [ ] JetBrains Marketplace submission timeline?
- [ ] Community template marketplace?
- [ ] Paid features or fully free?

---

## 🎯 Next Immediate Steps

1. ✅ **Review Settings Redesign Plan** (SETTINGS_REDESIGN_PLAN.md)
2. ✅ **Merge ROADMAP files** (This file!)
3. ⏳ **Get approval to begin v2.0 development**
4. ⏳ **Start Phase 1: Data model refactoring**
5. ⏳ **Create 7 preset template JSON files**
6. ⏳ **Begin UI redesign implementation**

---

## 🎊 Summary

**ProxyMe** is evolving from a functional prototype (v1.0.3) into a polished, user-friendly plugin (v2.0.0) with:

- ✨ Redesigned settings UI
- ✨ Working template system with 7 presets
- ✨ API keys integrated with model configuration
- ✨ Model assignment to Rider AI contexts
- ✨ Enhanced security (encrypted keys)
- ✨ Better UX (one-click setup with presets)

**Long-term vision:** A free, open-source alternative to expensive AI coding assistants, available on JetBrains Marketplace, supporting multiple IDEs and AI providers.

---

**Status:** 🚀 Ready to begin v2.0.0 development  
**Timeline:** 10-15 days for v2.0.0 completion  
**Next Milestone:** Complete settings redesign and template system  

**Let's make AI coding assistance accessible for everyone! 🎉**