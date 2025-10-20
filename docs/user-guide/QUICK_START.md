# ProxyMe Plugin - Quick Start

## What You Have

✅ **Ready-to-use Rider IDE plugin structure**
- Complete plugin.xml configuration
- Gradle build system
- Settings UI (project + application level)
- Service architecture
- Startup activity

✅ **UI Components**
- Auto-launch toggle
- Custom port configuration
- Launch/Stop/Restart buttons
- Green/Orange/Red status LED
- Model configuration table
- Template save/load system
- Log file settings

✅ **Core Features**
- Project-specific settings
- .env files in user home (~/.proxyme/)
- Template management system
- Service architecture for extensions

## What You Need to Add

⚠️ **Proxy Launch Logic**
Your working Node.js proxy code needs to be integrated into `ProxyMeProjectService.java`

⚠️ **Button Actions**
Connect UI buttons to service methods

⚠️ **Log Streaming**
Pipe proxy stdout/stderr to Terminal or log files

## File Structure Created

```
ProxyMe-Plugin/
├── build.gradle.kts                 ✅ Build configuration
├── settings.gradle.kts              ✅ Gradle settings
├── gradle.properties                ✅ Build properties
├── plugin.xml                       ✅ Plugin configuration
│
├── Settings Classes:
│   ├── ProxyMeConfigurable.java           ✅ Project settings UI interface
│   ├── ProxyMeSettings.java               ✅ Project settings storage
│   ├── ProxyMeTemplateSettings.java       ✅ Application template storage
│   └── ProxyMeTemplatesConfigurable.java  ✅ Template UI interface
│
├── UI Components:
│   ├── ProxyMeSettingsPanel.java          ✅ Main settings panel (UI only)
│   ├── ModelConfigPanel.java              ✅ Model table (UI only)
│   └── ProxyMeTemplatesPanel.java         ⚠️ Template panel (stub)
│
├── Services:
│   ├── ProxyMeProjectService.java         ⚠️ Proxy lifecycle (stub)
│   ├── ProxyMeTemplateService.java        ✅ Template import/export
│   └── ProxyMeEnvFileService.java         ✅ .env file generation
│
├── Startup:
│   └── ProxyMeStartupActivity.java        ✅ Auto-launch logic
│
└── Documentation:
    ├── README.md                     ✅ Overview
    ├── INSTALLATION_GUIDE.md         ✅ Installation steps
    └── INTEGRATION_GUIDE.md          ✅ Integration guide

```

## Build & Install

### 1. Build Plugin
```bash
./gradlew buildPlugin
```
Output: `build/distributions/ProxyMe-1.0.0.zip`

### 2. Install in Rider
1. Open Rider
2. Settings → Plugins
3. ⚙️ → Install Plugin from Disk
4. Select `ProxyMe-1.0.0.zip`
5. Restart

### 3. Verify Installation
- Check: Settings → Tools → ProxyMe
- Should see full settings panel
- All UI elements should be visible

## Next Steps

### Step 1: Add Your Proxy Code
Copy your Node.js proxy to:
```
src/main/resources/proxy/
├── proxy.js
├── package.json
└── package-lock.json
```

### Step 2: Implement Launch Logic
Edit `ProxyMeProjectService.java`:
- Extract proxy files from resources
- Build Node.js command
- Start process with environment
- Monitor logs

See `INTEGRATION_GUIDE.md` for detailed code

### Step 3: Connect Button Actions
Edit `ProxyMeSettingsPanel.java`:
- Add button click listeners
- Call service methods
- Update status indicator

### Step 4: Test Integration
1. Build plugin
2. Install in Rider
3. Configure model
4. Click Launch Proxy
5. Verify status turns green
6. Check logs in Terminal

### Step 5: Configure AI Assistant
1. Settings → AI Assistant → Models
2. Provider: OpenAI Compatible
3. URL: http://localhost:3000/v1
4. Test connection

## Key Features Implemented

✅ **Settings Location Control**
- AI Assistant settings CANNOT be extended
- Created standalone page: Tools → ProxyMe
- Both project-specific and application-level settings

✅ **Security**
- .env files stored in `~/.proxyme/<project-name>/`
- NEVER in project directory
- Never committed to Git

✅ **Template System**
- Save configurations as templates
- Load templates across projects
- JSON export/import
- Stored in user config directory

✅ **UI Ready**
- All components visible and laid out
- Status indicator with LED
- Model configuration table
- Button placeholders ready for logic

## Important Notes

📝 **Plugin Name:** ProxyMe
📝 **Plugin ID:** com.proxyme.rider
📝 **Minimum Rider Version:** 2024.3 (build 243)
📝 **JVM Target:** Java 17

📝 **Settings Storage:**
- Project: `.idea/proxyme-settings.xml`
- Templates: IDE config directory
- .env: `~/.proxyme/<project-name>/.env`

📝 **Not in AI Assistant Settings:**
Research confirmed AI Assistant settings cannot be extended by third-party plugins. We created a standalone settings page under Tools instead.

## Troubleshooting

**Build fails:**
- Check Java version: `java -version` (need 17+)
- Run: `./gradlew clean build`

**Plugin doesn't appear:**
- Check Rider version (need 2024.3+)
- Verify plugin is enabled in Settings → Plugins

**Settings don't save:**
- Check `.idea/` directory permissions
- Look for errors in IDE logs

## Support

📖 Full documentation in provided markdown files
🐛 All components documented with comments
💡 Integration guide provides code examples

---

**Ready to integrate your proxy code!**
See INTEGRATION_GUIDE.md for step-by-step instructions.
