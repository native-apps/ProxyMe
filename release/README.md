# ProxyMe v2.1.0 - Release Package

**Official Release Build for JetBrains Rider IDE**

---

## 📦 What's Included

- `ProxyMe-2.1.0.zip` - Plugin installer for Rider IDE

---

## 🚀 Installation

### Step 1: Download

You already have the ZIP file! (`ProxyMe-2.1.0.zip`)

### Step 2: Install in Rider

1. Open **JetBrains Rider IDE**
2. Go to `File → Settings → Plugins`
3. Click the **gear icon** ⚙️ in the top right
4. Select **"Install Plugin from Disk..."**
5. Browse to `ProxyMe-2.1.0.zip`
6. Click **OK**
7. Click **"Restart IDE"** when prompted

### Step 3: Verify Installation

After Rider restarts:
1. Check the `Tools` menu
2. You should see **ProxyMe** listed
3. Click `Tools → ProxyMe` to open settings

---

## ⚙️ Quick Setup

### Prerequisites

- **Node.js v18+** is required
  ```bash
  node --version
  ```
  If not installed: [Download Node.js](https://nodejs.org/)

### Initial Configuration

1. **Open ProxyMe Settings:**
   ```
   Tools → ProxyMe
   ```

2. **Add your first AI model:**
   - Click `Add Model`
   - Enter:
     - Model Name: `deepseek-chat`
     - Provider: `deepseek`
     - API Endpoint: `https://api.deepseek.com/chat/completions`
     - API Key: `sk-your-api-key-here`
     - Temperature: `0.3`
     - Stream: ☑ Enabled
   - Click `OK`

3. **Enable the model:**
   - Check the box in the `Enabled` column
   - Click `Save`

4. **Launch the proxy:**
   ```
   Tools → ProxyMe → Launch Proxy Server
   ```
   - Status indicator should turn green 🟢

5. **Configure Rider AI Assistant:**
   ```
   Settings → Tools → AI Assistant → Models
   ```
   - Provider: `OpenAI API`
   - URL: `http://localhost:3000/v1`
   - API Key: (leave empty)
   - Click `Test Connection`

6. **Assign models:**
   - Core features: Select your model
   - Instant helpers: Select your model
   - Completion model: Select your model

**Done!** Start using AI features in Rider.

---

## 📖 Documentation

- **Installation Guide:** [INSTALL.md](../INSTALL.md)
- **Troubleshooting:** [TROUBLESHOOTING.md](../TROUBLESHOOTING.md)
- **Build from Source:** [BUILD.md](../BUILD.md)
- **Contributing:** [CONTRIBUTING.md](../CONTRIBUTING.md)
- **Full Documentation:** [docs/](../docs/)

---

## ⚠️ Important Notes

### Only Tested with Rider IDE

This plugin has **only been tested with JetBrains Rider**.

Other JetBrains IDEs (IntelliJ IDEA, WebStorm, PyCharm, etc.) are **not tested**. Use at your own risk.

### Known Issues

- Occasional proxy restart issues (improved in v2.1.0)
- Contains AI-generated code that needs review
- Security review recommended before production use

### Alpha Software

This is **alpha software** with known bugs and limitations. Always:
- Back up your work
- Test in development environments first
- Review AI-generated code carefully
- Keep API keys secure

---

## 🆘 Getting Help

- **Issues:** [GitHub Issues](https://github.com/native-apps/proxyme/issues)
- **Discussions:** [GitHub Discussions](https://github.com/native-apps/proxyme/discussions)
- **Documentation:** [Full Docs](https://github.com/native-apps/proxyme)

---

## 🎯 What's New in v2.1.0

### Dynamic Model Control
- Only enabled models appear in Rider AI Assistant
- No more cluttered model lists!

### Per-Model Settings
- Temperature control (0.0 - 2.0)
- Streaming toggle per model
- Custom API endpoints

### Stability Improvements
- Fixed Rider crash on proxy restart
- Better error handling
- Improved logging

### UI Improvements
- Simplified settings panel
- Removed confusing Category field
- Better status indicators

See [CHANGELOG.md](../CHANGELOG.md) for full release notes.

---

## 🔐 Security & Privacy

- API keys stored in `~/.proxyme/` (outside project directories)
- Never committed to version control
- Secure environment file generation
- Project-specific isolation

**Your API keys are safe and never leave your machine.**

---

## 📋 System Requirements

- **Operating System:** macOS, Linux, or Windows
- **IDE:** JetBrains Rider 2024.3 or later
- **Node.js:** v18 or later
- **Java:** 17+ (only for building from source)

---

## 🤝 Contributing

We welcome contributions! This project needs:
- Bug fixes and stability improvements
- Security review and hardening
- Testing with other JetBrains IDEs
- Documentation improvements
- Code refactoring

See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

---

## 📄 License

MIT License - See [LICENSE](../LICENSE) file for details.

---

**Ready to get started?** Install the plugin and configure your first AI model!

For detailed instructions, see [INSTALL.md](../INSTALL.md)

---

*ProxyMe v2.1.0 - AI Proxy Management for JetBrains Rider*