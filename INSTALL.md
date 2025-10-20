# Installation Guide

**ProxyMe** - AI Proxy Management Plugin for JetBrains Rider IDE

---

## Quick Install

### Prerequisites
- JetBrains Rider IDE (2024.3 or later)
- Node.js (v18 or later)
- Java 17+ (for building from source)

### Option 1: Install Pre-built Plugin (Recommended)

1. **Download the plugin:**
   - Get the latest release: `ProxyMe-2.1.0.zip` from the [releases page](https://github.com/native-apps/proxyme/releases)

2. **Install in Rider:**
   - Open Rider IDE
   - Go to `File → Settings → Plugins`
   - Click the gear icon ⚙️ → `Install Plugin from Disk...`
   - Select the downloaded `ProxyMe-2.1.0.zip` file
   - Click `OK`
   - Click `Restart IDE` when prompted

3. **Verify installation:**
   - After restart, check `Tools` menu
   - You should see `ProxyMe` listed

### Option 2: Build from Source

See [BUILD.md](BUILD.md) for detailed build instructions.

---

## Initial Configuration

### Step 1: Configure Your AI Models

1. **Open ProxyMe Settings:**
   ```
   Tools → ProxyMe
   ```

2. **Add your first AI model:**
   - Click `Add Model` button
   - Fill in the details:
     - **Model Name:** `deepseek-chat` (or your preferred model)
     - **Provider:** `deepseek` (or `perplexity`, `anthropic`, etc.)
     - **API Endpoint:** Your provider's API endpoint
     - **API Key:** Your API key from the provider
     - **Temperature:** `0.3` (recommended for coding tasks)
     - **Stream:** ☑ Enabled (shows responses in real-time)
   - Click `OK`

3. **Enable the model:**
   - Check the checkbox in the `Enabled` column
   - Click `Save`

### Step 2: Launch the Proxy

1. **Start the proxy server:**
   ```
   Tools → ProxyMe → Launch Proxy Server
   ```

2. **Check the status indicator:**
   - 🟢 Green = Running normally
   - 🟠 Orange = Running with warnings
   - 🔴 Red = Not running

### Step 3: Configure Rider AI Assistant

1. **Open Rider AI Assistant settings:**
   ```
   Settings → Tools → AI Assistant → Models
   ```

2. **Configure the OpenAI-compatible provider:**
   - Provider: `OpenAI API`
   - URL: `http://localhost:3000/v1`
   - API Key: (leave empty)
   - Click `Test Connection` to verify (**!important:** Clicking `Test Connection` will refresh the Models defined in the ProxyMe Models list.)

3. **Assign models to features:**
   - **Core features** (chat, code generation): Select your preferred model
   - **Instant helpers** (quick edits, suggestions): Select a fast model
   - **Completion model** (inline completion): Select a precise model

---

## Verify Installation

### Test the Setup

1. **Open AI Assistant:**
   ```
   Tools → AI Assistant
   ```

2. **Select your model** from the dropdown

3. **Send a test message:**
   ```
   Hello, can you respond?
   ```

4. **Verify:**
   - ✅ Model responds correctly
   - ✅ Response streams in real-time (if enabled)
   - ✅ No error messages

### Check Configuration Files

The plugin stores configuration in your home directory:

```bash
~/.proxyme/
├── proxy/
│   ├── .env              # API keys (never committed to git)
│   ├── models.json       # Your enabled models
│   └── proxy.js          # Proxy server code
├── logs/
│   └── proxyme.log       # Log files
└── templates/
    ├── presets/          # Built-in templates
    └── user/             # Your custom templates
```

### View Logs

Check that everything is working:

```bash
# View recent logs
tail -f ~/.proxyme/logs/proxyme.log

# Check loaded models
cat ~/.proxyme/proxy/models.json | jq '.models[].id'

# Test the proxy endpoint
curl http://localhost:3000/v1/models
```

---

## Troubleshooting

### Proxy Won't Start

**Check Node.js installation:**
```bash
node --version
# Should show v18 or later
```

**Check if port 3000 is in use:**
```bash
lsof -i :3000
# Kill any conflicting process
```

**Check proxy logs:**
```bash
tail -50 ~/.proxyme/logs/proxyme.log
```

### Models Don't Appear in Rider

**Solution:** Restart the proxy after making changes

```
Tools → ProxyMe → Restart Proxy Server
```

The proxy loads models only on startup. After adding or enabling models, always restart.

### API Key Errors

**Check your .env file:**
```bash
cat ~/.proxyme/proxy/.env
```

Make sure your API keys are correctly formatted:
```
DEEPSEEK_API_KEY=sk-...
PERPLEXITY_API_KEY=pplx-...
ANTHROPIC_API_KEY=sk-ant-...
OPENAI_API_KEY=sk-...
```

### Temperature Not Working

Temperature settings only apply when you restart the proxy. After changing temperature:

1. Save settings in ProxyMe
2. Restart the proxy
3. Test again in AI Assistant

---

## Next Steps

- Read the [Quick Start Guide](docs/user-guide/QUICK_START.md)
- Check the [Troubleshooting Guide](TROUBLESHOOTING.md)
- Review [Recommended Settings](README.md#recommended-settings)
- Join the community and contribute!

---

## Support

- 🐛 **Bug Reports:** [GitHub Issues](https://github.com/native-apps/proxyme/issues)
- 💬 **Discussions:** [GitHub Discussions](https://github.com/native-apps/proxyme/discussions)
- 📖 **Documentation:** [Full Docs](docs/README.md)

---

**Installation complete!** 🎉 Start using AI-powered coding with Rider!
