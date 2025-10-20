# Releasing ProxyMe to GitHub

This document explains how to create a fresh Git repository with a clean history and deploy to GitHub.

---

## ⚠️ Important: Why Fresh History?

The current Git history contains:
- Development logs and internal notes
- Absolute file paths from the development machine
- Old experimental code and iterations
- Private session summaries and debugging notes

A fresh Git history provides:
- Clean, professional commit history
- Only production-ready code
- No sensitive development artifacts
- Better onboarding for contributors

---

## 📋 Pre-Release Checklist

Before creating the fresh repository, verify:

- [x] All sensitive information removed
- [x] Old development markdown files deleted
- [x] Python scripts removed
- [x] Documentation updated and cleaned
- [x] .gitignore comprehensive and accurate
- [x] LICENSE file added
- [x] CONTRIBUTING.md created
- [x] README.md professional and complete
- [x] CHANGELOG.md created
- [x] Release ZIP file in `release/` directory
- [x] All package.json files updated with correct URLs
- [x] No hardcoded usernames or paths remain
- [x] Build tested and working

---

## 🚀 Step-by-Step Release Process

### Step 1: Create Fresh Git History

```bash
# Navigate to project directory
cd /path/to/ProxyMe

# Create a backup of current state (optional but recommended)
```
git tag -a backup-before-release -m "Backup before fresh history"

# Create a new orphan branch (no history)
git checkout --orphan fresh-main

# Add all files
git add -A

# Create initial commit
git commit -m "Initial release: ProxyMe v2.1.0

ProxyMe - AI Proxy Management Plugin for JetBrains Rider IDE

Features:
- Dynamic model control - only enabled models appear in Rider
- Per-model temperature and streaming configuration
- Secure API key management
- Template system for quick setup
- Real-time proxy status monitoring
- Support for multiple AI providers (DeepSeek, Perplexity, Anthropic, OpenAI)

This is an alpha release built with AI assistance (Claude Sonnet 4.5).
Contributions welcome for bug fixes, security improvements, and testing."

# Verify clean history
git log --oneline
# Should show only one commit
```

### Step 2: Prepare GitHub Repository

1. **Go to GitHub:** https://github.com/native-apps
2. **Create new repository:**
   - Name: `proxyme`
   - Description: `AI Proxy Management Plugin for JetBrains Rider IDE`
   - Visibility: **Public**
   - **DO NOT** initialize with README, .gitignore, or LICENSE (we already have them)
3. **Click "Create repository"**

### Step 3: Push to GitHub

```bash
# Add GitHub remote (replace with your actual URL)
git remote remove origin  # Remove old origin if exists
git remote add origin https://github.com/native-apps/proxyme.git

# Push fresh history to main branch
git push -u origin fresh-main:main --force

# Verify on GitHub
# Open: https://github.com/native-apps/proxyme
```

### Step 4: Configure GitHub Repository

1. **Add repository description:**
   ```
   AI Proxy Management Plugin for JetBrains Rider IDE - Configure multiple AI models, control which models are available, and manage API keys securely
   ```

2. **Add topics/tags:**
   - `rider`
   - `jetbrains`
   - `ai-assistant`
   - `plugin`
   - `proxy-server`
   - `deepseek`
   - `perplexity`
   - `anthropic`
   - `openai`
   - `ide-plugin`
   - `rider-plugin`

3. **Update repository settings:**
   - Enable Issues
   - Enable Discussions (recommended)
   - Enable Wiki (optional)
   - Enable Projects (optional)

4. **Create branch protection rules (Settings → Branches):**
   - Branch name pattern: `main`
   - ☑ Require pull request reviews before merging
   - ☑ Require status checks to pass
   - ☑ Require branches to be up to date

### Step 5: Create GitHub Release

1. **Go to Releases:** https://github.com/native-apps/proxyme/releases
2. **Click "Create a new release"**
3. **Create tag:** `v2.1.0`
4. **Release title:** `ProxyMe v2.1.0 - Dynamic Model Control`
5. **Description:**

```markdown
# ProxyMe v2.1.0 - Dynamic Model Control

**First Public Release** 🎉

ProxyMe is an AI Proxy Management Plugin for JetBrains Rider IDE that lets you configure multiple AI models, control which models are available to Rider's AI Assistant, and manage API keys securely.

## 🎯 Key Features

- **Dynamic Model Control** - Only enabled models appear in Rider AI Assistant
- **Per-Model Settings** - Temperature (0.0-2.0) and streaming control per model
- **Secure API Keys** - Stored in `~/.proxyme/` outside project directories
- **Template System** - Quick setup with built-in presets
- **Multiple Providers** - DeepSeek, Perplexity, Anthropic, OpenAI, and custom

## 📦 Installation

1. Download `ProxyMe-2.1.0.zip` below
2. In Rider: `File → Settings → Plugins → ⚙️ → Install Plugin from Disk`
3. Select the ZIP file and restart Rider
4. Configure via `Tools → ProxyMe`

See [INSTALL.md](https://github.com/native-apps/proxyme/blob/main/INSTALL.md) for detailed instructions.

## ⚠️ Important Notices

- **Only tested with Rider IDE** - Use with other JetBrains IDEs at your own risk
- **Alpha software** - Contains AI-generated code, expect bugs
- **Security review needed** - Contributions welcome

## 📖 Documentation

- [Installation Guide](https://github.com/native-apps/proxyme/blob/main/INSTALL.md)
- [Build Instructions](https://github.com/native-apps/proxyme/blob/main/BUILD.md)
- [Troubleshooting](https://github.com/native-apps/proxyme/blob/main/TROUBLESHOOTING.md)
- [Contributing](https://github.com/native-apps/proxyme/blob/main/CONTRIBUTING.md)

## 🤝 Contributing

We need your help! This project needs:
- Bug fixes and stability improvements
- Security review
- Testing with other JetBrains IDEs
- Documentation improvements

See [CONTRIBUTING.md](https://github.com/native-apps/proxyme/blob/main/CONTRIBUTING.md)

## 📋 Changelog

See [CHANGELOG.md](https://github.com/native-apps/proxyme/blob/main/CHANGELOG.md) for detailed changes.

## 📄 License

MIT License - See [LICENSE](https://github.com/native-apps/proxyme/blob/main/LICENSE)

---

**Ready to get started?** Download the ZIP, install in Rider, and configure your first AI model!
```

6. **Upload release asset:**
   - Click "Attach binaries"
   - Upload `release/ProxyMe-2.1.0.zip`

7. **Set as latest release:** ☑ Check the box

8. **Click "Publish release"**

### Step 6: Update Repository Files on GitHub

After pushing, verify these files appear correctly:

- ✅ README.md displays with badges and proper formatting
- ✅ LICENSE file recognized (shows MIT in sidebar)
- ✅ CONTRIBUTING.md appears in contributor dropdown
- ✅ CHANGELOG.md accessible
- ✅ docs/ directory structure intact

### Step 7: Create Issue Templates

Create `.github/ISSUE_TEMPLATE/` directory with templates:

**Bug Report Template:**
```yaml
name: Bug Report
about: Report a bug or issue
title: '[BUG] '
labels: bug
assignees: ''

---

**Describe the bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What you expected to happen.

**Screenshots**
If applicable, add screenshots.

**Environment:**
 - ProxyMe version: [e.g. 2.1.0]
 - Rider version: [e.g. 2024.3]
 - OS: [e.g. macOS 14.5]
 - Node.js version: [e.g. v20.0.0]

**Logs**
Paste relevant logs from ~/.proxyme/logs/
```

**Feature Request Template:**
```yaml
name: Feature Request
about: Suggest a new feature
title: '[FEATURE] '
labels: enhancement
assignees: ''

---

**Is your feature request related to a problem?**
A clear description of the problem.

**Describe the solution you'd like**
What you want to happen.

**Describe alternatives you've considered**
Alternative solutions or features.

**Additional context**
Any other context or screenshots.
```

### Step 8: Configure GitHub Actions (Optional)

Create `.github/workflows/build.yml` for CI/CD:

```yaml
name: Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build plugin
      run: ./gradlew buildPlugin
    
    - name: Upload build artifacts
      uses: actions/upload-artifact@v3
      with:
        name: ProxyMe-Plugin
        path: build/distributions/*.zip
```

---

## 📢 Announcing the Release

### Update README badges

Add these to the top of README.md:

```markdown
[![Version](https://img.shields.io/github/v/release/native-apps/proxyme)](https://github.com/native-apps/proxyme/releases)
[![Downloads](https://img.shields.io/github/downloads/native-apps/proxyme/total)](https://github.com/native-apps/proxyme/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Rider%202024.3%2B-orange.svg)](https://www.jetbrains.com/rider/)
```

### Social Media (Optional)

Share on:
- Reddit (r/jetbrains, r/csharp, r/dotnet)
- Twitter/X
- LinkedIn
- Dev.to
- Hacker News

### JetBrains Marketplace (Future)

To publish on JetBrains Marketplace:
1. Create account at https://plugins.jetbrains.com/
2. Submit plugin for review
3. Follow their guidelines
4. Wait for approval

---

## 🔄 Future Releases

For subsequent releases:

```bash
# Create new version
git checkout main
git pull origin main

# Make changes, commit normally
git add .
git commit -m "feat: Add new feature"
git push origin main

# Tag new version
git tag -a v2.2.0 -m "Release v2.2.0"
git push origin v2.2.0

# Create GitHub release (repeat Step 5 above)
```

---

## 🧹 Cleanup Local Repository (Optional)

After successful deployment:

```bash
# Delete old branches
git branch -D main  # Delete old main if it exists

# Rename fresh-main to main locally
git branch -m fresh-main main

# Clean up old remotes
git remote prune origin
```

---

## ✅ Post-Release Checklist

- [ ] GitHub repository created and configured
- [ ] Fresh Git history pushed
- [ ] v2.1.0 release published with ZIP file
- [ ] README.md displays correctly on GitHub
- [ ] LICENSE recognized by GitHub
- [ ] Issues enabled
- [ ] Discussions enabled (optional)
- [ ] Branch protection rules set
- [ ] Issue templates created
- [ ] GitHub Actions configured (optional)
- [ ] Repository topics/tags added
- [ ] Release announced (optional)

---

## 📞 Support

If you encounter issues during release:

1. Check GitHub's documentation: https://docs.github.com/
2. Verify all files are properly formatted
3. Ensure no sensitive information remains
4. Test clone from GitHub to verify everything works

---

## 🎉 Congratulations!

Your project is now live and ready for community contributions!

**Repository:** https://github.com/native-apps/proxyme

**Next steps:**
- Monitor issues and discussions
- Review pull requests
- Update documentation as needed
- Plan next release features

**Thank you for open sourcing ProxyMe!** 🚀