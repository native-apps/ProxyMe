# Building ProxyMe from Source

This guide is for contributors who want to build the ProxyMe plugin from source.

---

## Prerequisites

### Required Software

- **Java Development Kit (JDK) 17 or later**
  ```bash
  java -version
  # Should show version 17 or higher
  ```

- **Gradle 8.0+** (included via wrapper)
  ```bash
  ./gradlew --version
  ```

- **Node.js v18 or later**
  ```bash
  node --version
  npm --version
  ```

- **Git**
  ```bash
  git --version
  ```

### Recommended IDE

- IntelliJ IDEA 2024.3+ or JetBrains Rider 2024.3+
- Gradle plugin enabled
- Kotlin plugin enabled

---

## Clone the Repository

```bash
git clone https://github.com/native-apps/proxyme.git
cd proxyme
```

---

## Build the Plugin

### Quick Build

```bash
# Build the plugin ZIP file
./gradlew buildPlugin
```

The compiled plugin will be located at:
```
build/distributions/ProxyMe-2.1.0.zip
```

### Build with Tests

```bash
# Run tests and build
./gradlew test buildPlugin
```

### Clean Build

```bash
# Clean previous builds
./gradlew clean

# Build from scratch
./gradlew clean buildPlugin
```

---

## Development Workflow

### Open Project in IDE

1. Open IntelliJ IDEA or Rider
2. `File → Open`
3. Select the `ProxyMe` directory
4. Wait for Gradle to sync

### Run in Development Mode

```bash
# Launch a development instance of Rider with the plugin
./gradlew runIde
```

This opens a sandboxed Rider instance with your plugin installed for testing.

### Run Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "com.proxyme.rider.ProxyMeSettingsTest"

# Run with verbose output
./gradlew test --info
```

### Verify Code Quality

```bash
# Check for compilation errors
./gradlew compileJava

# Run code inspections
./gradlew check
```

---

## Project Structure

```
ProxyMe/
├── src/
│   ├── main/
│   │   ├── java/com/proxyme/rider/     # Java source code
│   │   │   ├── actions/                # Menu actions
│   │   │   ├── services/               # Background services
│   │   │   ├── settings/               # Settings classes
│   │   │   ├── ui/                     # UI components
│   │   │   └── *.java                  # Core classes
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── plugin.xml          # Plugin manifest
│   │       ├── proxy/                  # Node.js proxy code
│   │       │   ├── proxy.js
│   │       │   └── package.json
│   │       └── templates/
│   │           └── presets/            # Default templates
│   └── test/
│       └── java/                       # Test files
├── build.gradle.kts                    # Gradle build script
├── settings.gradle.kts                 # Gradle settings
├── gradle.properties                   # Gradle properties
└── README.md                           # Project overview
```

---

## Install Node.js Dependencies

The proxy server requires Node.js packages:

```bash
cd "Node.js Proxy Cloud AI APIs"
npm install
```

### Required npm Packages

The proxy requires these packages (defined in `package.json`):

- `express` - Web server framework
- `axios` - HTTP client
- `dotenv` - Environment variable management
- `cors` - CORS middleware

---

## Install Plugin for Testing

### Option 1: Install from Build

1. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

2. Open your regular Rider IDE (not the sandbox)

3. Install the built plugin:
   ```
   File → Settings → Plugins → ⚙️ → Install Plugin from Disk
   ```

4. Select: `build/distributions/ProxyMe-2.1.0.zip`

5. Restart Rider

### Option 2: Run in Sandbox (Development)

```bash
./gradlew runIde
```

This is safer for development - changes won't affect your main IDE.

---

## Making Changes

### Modify Java Code

1. Edit files in `src/main/java/com/proxyme/rider/`
2. Build:
   ```bash
   ./gradlew compileJava
   ```
3. Test in sandbox:
   ```bash
   ./gradlew runIde
   ```

### Modify UI Components

UI components are in `src/main/java/com/proxyme/rider/ui/`:

- `ProxyMeSettingsPanel.java` - Main settings panel
- `ModelConfigPanel.java` - Model table
- `ModelEditDialog.java` - Model editor dialog
- `LoadTemplateDialog.java` - Template loader
- `SaveTemplateDialog.java` - Template saver

After changes, rebuild and test:
```bash
./gradlew buildPlugin runIde
```

### Modify Proxy Code

The Node.js proxy is in `src/main/resources/proxy/`:

1. Edit `proxy.js` or other proxy files
2. Test locally:
   ```bash
   cd "Node.js Proxy Cloud AI APIs"
   node proxy.js
   ```
3. Test with plugin:
   ```bash
   ./gradlew buildPlugin runIde
   ```

### Update Plugin Manifest

To add menu items, settings pages, or services, edit:
```
src/main/resources/META-INF/plugin.xml
```

After changes:
```bash
./gradlew buildPlugin
```

---

## Common Build Issues

### Issue: Gradle Sync Fails

**Solution:**
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Re-sync
./gradlew --refresh-dependencies
```

### Issue: Java Version Mismatch

**Solution:**
```bash
# Check Java version
java -version

# Set JAVA_HOME (macOS/Linux)
export JAVA_HOME=/path/to/jdk-17

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\path\to\jdk-17
```

### Issue: Build Fails with "Cannot find symbol"

**Solution:**
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Issue: Plugin Won't Load in Rider

**Check:**
1. Plugin is compatible with Rider version (2024.3+)
2. `plugin.xml` is correctly formatted
3. Rebuild plugin: `./gradlew clean buildPlugin`
4. Check Rider logs for errors

---

## Testing Your Changes

### Manual Testing

1. Build plugin: `./gradlew buildPlugin`
2. Install in Rider
3. Test these features:
   - [ ] Open ProxyMe settings (`Tools → ProxyMe`)
   - [ ] Add a model
   - [ ] Save settings
   - [ ] Launch proxy
   - [ ] Check status indicator
   - [ ] Stop proxy
   - [ ] Load a template
   - [ ] Save a template
   - [ ] Restart proxy

### Automated Testing

```bash
# Run all tests
./gradlew test

# View test report
open build/reports/tests/test/index.html
```

---

## Package for Release

### Create Release Build

```bash
# Clean build with tests
./gradlew clean test buildPlugin

# Verify the ZIP file
ls -lh build/distributions/ProxyMe-*.zip
```

### Verify Release Package

```bash
# Extract and inspect
unzip -l build/distributions/ProxyMe-2.1.0.zip

# Check that it includes:
# - lib/ directory (JAR files)
# - proxy/ directory (Node.js code)
# - templates/ directory (presets)
```

---

## Contributing Guidelines

### Before Submitting a Pull Request

1. **Format your code:**
   - Follow Java coding conventions
   - Use 4-space indentation
   - Add JavaDoc comments for public methods

2. **Test your changes:**
   ```bash
   ./gradlew test
   ./gradlew runIde  # Manual testing
   ```

3. **Update documentation:**
   - Update relevant `.md` files
   - Add comments to complex code
   - Update `CHANGELOG.md` if needed

4. **Create a clean commit:**
   ```bash
   git add .
   git commit -m "feat: Add new model provider support"
   ```

5. **Push and create PR:**
   ```bash
   git push origin feature-branch
   # Create PR on GitHub
   ```

### Commit Message Format

Follow conventional commits:

```
feat: Add support for new AI provider
fix: Resolve crash when restarting proxy
docs: Update installation guide
style: Format code according to style guide
refactor: Simplify model configuration logic
test: Add tests for template service
chore: Update dependencies
```

---

## Need Help?

- 📖 **Documentation:** [docs/README.md](docs/README.md)
- 💬 **Discussions:** [GitHub Discussions](https://github.com/native-apps/proxyme/discussions)
- 🐛 **Bug Reports:** [GitHub Issues](https://github.com/native-apps/proxyme/issues)

---

## Development Resources

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [Rider Plugin Development](https://plugins.jetbrains.com/docs/intellij/rider.html)
- [Gradle Plugin Portal](https://plugins.gradle.org/)
- [JetBrains Plugin Repository](https://plugins.jetbrains.com/)

---

**Happy coding!** 🚀