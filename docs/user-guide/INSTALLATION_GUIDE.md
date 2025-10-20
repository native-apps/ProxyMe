# ProxyMe Plugin - Installation & Setup Guide

## Prerequisites

Before installing the ProxyMe plugin, ensure you have:

✅ **JetBrains Rider 2024.3 or later**
✅ **Java 17+** (for plugin development)
✅ **Gradle 8.0+** (for building from source)
✅ **Node.js 18+** (for running the proxy server)

## Installation Methods

### Method 1: Build from Source (Recommended for Development)

#### Step 1: Clone the Repository
```bash
git clone https://github.com/yourusername/proxyme-rider-plugin.git
cd proxyme-rider-plugin
```

#### Step 2: Build the Plugin
```bash
# On Windows
.\gradlew buildPlugin

# On macOS/Linux
./gradlew buildPlugin
```

This will create `build/distributions/ProxyMe-1.0.0.zip`

#### Step 3: Install in Rider
1. Open Rider IDE
2. Navigate to `File → Settings → Plugins` (Windows/Linux) or `Rider → Preferences → Plugins` (macOS)
3. Click the ⚙️ gear icon → `Install Plugin from Disk...`
4. Select `build/distributions/ProxyMe-1.0.0.zip`
5. Click **OK**
6. **Restart Rider** when prompted

### Method 2: Install from Zip (Pre-built)

If you received a pre-built `.zip` file:

1. Open Rider IDE
2. Go to `File → Settings → Plugins`
3. Click ⚙️ → `Install Plugin from Disk...`
4. Select the `ProxyMe-1.0.0.zip` file
5. Restart Rider

### Method 3: JetBrains Marketplace (Future)

Once published:
1. Open `Settings → Plugins → Marketplace`
2. Search for "ProxyMe"
3. Click **Install**
4. Restart Rider

## First-Time Setup

### 1. Verify Installation

After restart, verify the plugin is installed:
- Go to `Settings → Plugins → Installed`
- Look for "ProxyMe" in the list
- Status should show "Enabled"

### 2. Access Settings

Navigate to: `File → Settings → Tools → ProxyMe`

You should see the ProxyMe settings panel with:
- ✅ Startup Settings
- ✅ Proxy Configuration
- ✅ Proxy Control buttons
- ✅ Logging Settings
- ✅ Model Configuration table
- ✅ Template Management

### 3. Configure Basic Settings

**Proxy Configuration:**
- Port: `3000` (default, can be changed)
- Host: `localhost`

**Logging Settings:**
- ☑ Show logs in Terminal (recommended)
- ☑ Save logs to file
- Log file path: Leave default or customize

### 4. Add Your First Model

Click **Add Model** and configure:

**Example: DeepSeek Chat**
- Model name: `deepseek-chat`
- Provider: `deepseek`
- API endpoint: `https://api.deepseek.com/chat/completions`
- API key name: `DEEPSEEK_API_KEY`
- Category: `core`
- ☑ Enabled

**Example: Perplexity Sonar**
- Model name: `sonar-pro`
- Provider: `perplexity`
- API endpoint: `https://api.perplexity.ai/chat/completions`
- API key name: `PERPLEXITY_API_KEY`
- Category: `core`
- ☑ Enabled

### 5. Configure .env File

The plugin creates `.env` files at: `~/.proxyme/<project-name>/.env`

**Important:** After adding models, you must edit the `.env` file to add actual API keys:

```bash
# On Windows
notepad %USERPROFILE%\.proxyme\<project-name>\.env

# On macOS/Linux
nano ~/.proxyme/<project-name>/.env
```

Replace `your_api_key_here` with actual API keys:
```env
PORT=3000
DEEPSEEK_API_KEY=sk-xxxxxxxxxxxx
PERPLEXITY_API_KEY=pplx-xxxxxxxxxxxx
```

## Verification

### Test the Settings Panel

1. Make changes to settings
2. Click **Apply**
3. Settings should persist
4. Reopen settings to verify

### Check File Locations

**Project Settings:**
```
<project-root>/.idea/proxyme-settings.xml
```

**User Templates:**
```
<IDE-config>/options/proxyme-templates.xml
```

**.env Files:**
```
~/.proxyme/<project-name>/.env
```

## Troubleshooting Installation

### Plugin Not Appearing

**Issue:** ProxyMe doesn't appear in Tools menu or Settings

**Solutions:**
1. Verify plugin is enabled in `Settings → Plugins → Installed`
2. Check compatibility: Rider 2024.3+ required
3. Check IDE logs: `Help → Show Log in Explorer`
4. Reinstall plugin from zip

### Build Failures

**Issue:** `./gradlew buildPlugin` fails

**Solutions:**
1. Ensure Java 17+ is installed: `java -version`
2. Clear Gradle cache: `./gradlew clean`
3. Update Gradle wrapper: `./gradlew wrapper --gradle-version=8.0`
4. Check `build.gradle.kts` for errors

### Settings Not Saving

**Issue:** Changes don't persist after restart

**Solutions:**
1. Check write permissions on `.idea/` directory
2. Look for errors in IDE logs
3. Try "Invalidate Caches / Restart"

### .env File Not Created

**Issue:** No .env file in `~/.proxyme/`

**Solutions:**
1. Check home directory permissions
2. Manually create `~/.proxyme/` directory
3. Click "Apply" in settings to trigger generation

## Next Steps

After successful installation:

1. **Add Proxy Code:** Integrate your Node.js proxy (see INTEGRATION_GUIDE.md)
2. **Configure Models:** Add all AI models you want to use
3. **Test Launch:** Click "Launch Proxy" button
4. **Configure AI Assistant:** Point Rider's AI Assistant to your proxy
5. **Save Template:** Save your configuration as a template

## Uninstallation

To remove the plugin:

1. Go to `Settings → Plugins → Installed`
2. Find "ProxyMe"
3. Click dropdown arrow → `Uninstall`
4. Restart Rider

**Note:** This does NOT delete:
- `.env` files in `~/.proxyme/`
- Template files
- Project settings in `.idea/`

To completely remove all data:
```bash
# Remove all ProxyMe data
rm -rf ~/.proxyme
```

## Getting Help

- 📖 Documentation: See README.md
- 🐛 Issues: https://github.com/yourusername/proxyme-rider-plugin/issues
- 💬 Discussions: https://github.com/yourusername/proxyme-rider-plugin/discussions
