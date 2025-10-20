# Changelog

All notable changes to ProxyMe will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned
- Testing with other JetBrains IDEs (IntelliJ IDEA, WebStorm, PyCharm)
- Enhanced security review and hardening
- Comprehensive test suite
- UI/UX improvements
- More AI provider presets
- Plugin marketplace release

---

## [2.1.0] - 2024-10-20

### Added
- **Dynamic Model Control** - Only enabled models appear in Rider AI Assistant
- Per-model temperature control (0.0 - 2.0)
- Per-model streaming toggle
- Auto-generation of `models.json` configuration file
- Claude Sonnet 4.5 preset template
- Enhanced restart flow with better safety mechanisms
- Async restart with proper cleanup
- Better user feedback during restart operations
- Health check endpoints for monitoring
- Improved logging throughout the plugin

### Changed
- Simplified UI - removed Contexts column (handled by Rider natively)
- Removed Category field (Build 2+) - Rider assigns models to features
- Temperature now displayed with color-coded indicators
- Models table now shows 6 columns: Enabled, Model Name, Provider, Endpoint, API Key, Temperature, Stream
- Improved status indicator visibility
- Better error messages and user notifications
- Enhanced proxy startup and shutdown procedures

### Fixed
- **Critical:** Fixed Rider IDE crash when clicking "Restart Proxy" button
- Fixed models.json not being regenerated on restart
- Fixed API key environment variable generation
- Fixed Anthropic and OpenAI keys not being written to .env
- Fixed timing issues during proxy restart
- Fixed model caching issues in Rider AI Assistant
- Fixed null pointer exceptions in settings panel
- Improved error handling during proxy lifecycle operations

### Security
- API keys now stored securely in `~/.proxyme/proxy/.env`
- Environment files never committed to version control
- Proper file permissions enforced (600 for .env files)
- Keys masked in UI

---

## [2.0.0] - 2024-10-15

### Added
- Initial public release
- Project-specific settings with persistence
- Proxy server lifecycle management (launch, stop, restart)
- Real-time status monitoring with LED indicator (🟢🟠🔴)
- Model configuration with API endpoints and keys
- Template system (save/load configurations)
- Built-in presets for DeepSeek, Perplexity, Claude
- Logging to dedicated log files
- Auto-launch proxy on IDE startup option
- Custom port and host configuration
- Settings panel integrated into Rider Tools menu

### Changed
- Complete rewrite from v1.x prototype
- New UI design with tabbed interface
- Improved settings persistence
- Better integration with Rider AI Assistant

### Security
- Moved .env files outside project directories
- User home directory storage (`~/.proxyme/`)
- Project-specific isolation

---

## [1.0.0] - 2024-10-10

### Added
- Initial prototype release
- Basic UI structure
- Plugin manifest and configuration
- Gradle build setup
- Project skeleton

### Notes
- This was a proof-of-concept version
- Not released publicly
- Used for initial development and testing

---

## Release Notes

### v2.1.0 Highlights

**What's New:**
- ProxyMe now actually controls which models Rider can see
- Only enabled models appear in your AI Assistant
- Per-model temperature and streaming configuration
- Much more stable restart flow (no more crashes!)

**Upgrade Notes:**
- No breaking changes from v2.0.0
- Existing settings will be preserved
- May need to restart Rider after upgrade for model list to refresh
- Recommended: Re-save your settings to generate updated models.json

**Known Issues:**
- Only tested with Rider IDE (not other JetBrains IDEs)
- Occasional model caching in Rider (workaround: restart IDE)
- Some AI-generated code needs refactoring

### v2.0.0 Highlights

**What's New:**
- First stable public release
- Complete proxy management from within Rider
- Template system for quick configuration switching
- Secure API key storage

**Known Issues:**
- Restart Proxy button could cause IDE crash (fixed in v2.1.0)
- Models couldn't be dynamically controlled (fixed in v2.1.0)

---

## Migration Guides

### Migrating from v2.0.0 to v2.1.0

1. **Backup your templates** (optional):
   ```bash
   cp -r ~/.proxyme/templates/user/ ~/proxyme-templates-backup/
   ```

2. **Install new version** using Rider's plugin manager

3. **Restart Rider completely**

4. **Open ProxyMe settings** and verify your models are still there

5. **Click Save** to generate the new `models.json` file

6. **Restart the proxy** to load the new configuration

7. **Test in Rider AI Assistant** - only enabled models should appear

### Migrating from v1.x to v2.x

v2.0.0 was a complete rewrite. No direct migration path exists. You will need to:
- Uninstall v1.x completely
- Install v2.x as a fresh installation
- Reconfigure your models and settings

---

## Versioning Policy

ProxyMe uses [Semantic Versioning](https://semver.org/):

- **MAJOR** version (X.0.0) - Breaking changes
- **MINOR** version (0.X.0) - New features, backwards compatible
- **PATCH** version (0.0.X) - Bug fixes, backwards compatible

**Current Status:** Alpha (pre-1.0.0 features, but using 2.x versioning for clarity)

---

## Support

- **Issues:** [GitHub Issues](https://github.com/native-apps/proxyme/issues)
- **Discussions:** [GitHub Discussions](https://github.com/native-apps/proxyme/discussions)
- **Documentation:** [Full Docs](docs/README.md)

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for how to contribute to ProxyMe.

---

[Unreleased]: https://github.com/native-apps/proxyme/compare/v2.1.0...HEAD
[2.1.0]: https://github.com/native-apps/proxyme/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/native-apps/proxyme/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/native-apps/proxyme/releases/tag/v1.0.0