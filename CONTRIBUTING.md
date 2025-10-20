# Contributing to ProxyMe

Thank you for your interest in contributing to ProxyMe! This document provides guidelines for contributing to the project.

---

## 🚨 Important Notice

**This project contains AI-generated code** (primarily created with Claude Sonnet 4.5) and is in **active development**. We welcome contributions to:

- Improve code quality and remove "AI slop"
- Fix bugs and security issues
- Add new features
- Improve documentation
- Write tests

**Known Issues:**
- Some code needs refactoring and optimization
- Security review needed for API key handling
- Occasional crashes when restarting proxy
- **Only tested with Rider IDE** - not verified with other JetBrains IDEs

---

## Getting Started

### Prerequisites

Before contributing, make sure you have:

- Java 17 or later
- Node.js v18 or later
- Gradle 8.0+ (included via wrapper)
- Git
- JetBrains Rider IDE (for testing)

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR-USERNAME/proxyme.git
   cd proxyme
   ```
3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/native-apps/proxyme.git
   ```

### Build and Test

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Test in sandbox IDE
./gradlew runIde
```

See [BUILD.md](BUILD.md) for detailed build instructions.

---

## How to Contribute

### Reporting Bugs

**Before submitting a bug report:**
- Check existing [issues](https://github.com/native-apps/proxyme/issues)
- Test with the latest version
- Collect relevant information (logs, screenshots, steps to reproduce)

**When reporting bugs, include:**
- ProxyMe version
- Rider IDE version
- Operating system
- Steps to reproduce
- Expected vs. actual behavior
- Relevant logs from `~/.proxyme/logs/`
- Screenshots if applicable

**Template:**
```markdown
## Bug Description
Clear description of the bug

## Steps to Reproduce
1. Step one
2. Step two
3. Step three

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- ProxyMe version: 2.1.0
- Rider version: 2024.3
- OS: macOS 14.5
- Node.js version: v20.0.0

## Logs
```
Paste relevant logs here
```
```

### Suggesting Features

We welcome feature suggestions! Please:

1. Check if the feature is already requested
2. Explain the use case and benefits
3. Provide examples or mockups if possible
4. Consider implementation complexity

**Use the feature request template on GitHub Issues.**

---

## Development Guidelines

### Code Style

**Java Code:**
- Follow standard Java conventions
- Use 4-space indentation (no tabs)
- Maximum line length: 120 characters
- Add JavaDoc comments for public classes and methods
- Use meaningful variable names

**Example:**
```java
/**
 * Manages the lifecycle of the proxy server.
 * Handles starting, stopping, and restarting the Node.js proxy process.
 */
public class ProxyMeProjectService {
    private Process proxyProcess;
    
    /**
     * Launches the proxy server with the current configuration.
     * 
     * @throws IOException if the proxy process fails to start
     */
    public void launchProxy() throws IOException {
        // Implementation
    }
}
```

**JavaScript/Node.js Code:**
- Use 2-space indentation
- Use ES6+ features
- Add JSDoc comments for functions
- Use `const` and `let`, avoid `var`

**Example:**
```javascript
/**
 * Handles chat completion requests for AI models.
 * @param {Object} req - Express request object
 * @param {Object} res - Express response object
 */
async function handleChatCompletion(req, res) {
  // Implementation
}
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code formatting (no logic changes)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks

**Examples:**
```
feat(models): Add support for Anthropic Claude models
fix(proxy): Resolve crash when restarting proxy server
docs(readme): Update installation instructions
refactor(ui): Simplify model configuration panel
test(settings): Add unit tests for settings persistence
```

### Branch Naming

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions/updates

**Examples:**
```
feature/add-gemini-support
fix/restart-crash
docs/update-troubleshooting
refactor/cleanup-ui-code
```

### Pull Request Process

1. **Create a feature branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes:**
   - Write clean, documented code
   - Add tests if applicable
   - Update documentation

3. **Test thoroughly:**
   ```bash
   # Run tests
   ./gradlew test
   
   # Test in sandbox
   ./gradlew runIde
   
   # Build plugin
   ./gradlew buildPlugin
   ```

4. **Commit your changes:**
   ```bash
   git add .
   git commit -m "feat: Add your feature description"
   ```

5. **Push to your fork:**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create Pull Request:**
   - Go to GitHub and create a PR
   - Fill out the PR template
   - Link related issues
   - Request review

**PR Template:**
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Tested in sandbox IDE
- [ ] Added/updated tests
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Commented complex sections
- [ ] Updated documentation
- [ ] No new warnings
- [ ] Tests pass locally
```

---

## Areas That Need Help

### High Priority

- **Security Review:** API key storage and handling
- **Error Handling:** Improve error messages and recovery
- **Testing:** Add comprehensive unit and integration tests
- **Performance:** Optimize proxy startup and model loading
- **Stability:** Fix known crashes and edge cases

### Code Quality Improvements

- **Refactoring:** Clean up AI-generated code
- **Documentation:** JavaDoc and inline comments
- **Type Safety:** Add null checks and validation
- **Logging:** Improve debug and error logging

### Feature Additions

- **More AI Providers:** Add support for additional providers
- **Model Presets:** Expand built-in preset library
- **UI Improvements:** Better UX and visual feedback
- **Configuration:** Import/export settings
- **Monitoring:** Better health checks and diagnostics

### Documentation

- **Tutorials:** Step-by-step guides for common tasks
- **API Documentation:** Document internal APIs
- **Troubleshooting:** Expand common issues and solutions
- **Examples:** Add example configurations

---

## Testing Guidelines

### Unit Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "ProxyMeSettingsTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Integration Tests

Test the full workflow:
1. Install plugin in Rider
2. Configure models
3. Launch proxy
4. Test AI Assistant integration
5. Restart proxy
6. Load/save templates

### Manual Testing Checklist

- [ ] Install plugin from ZIP
- [ ] Plugin appears in Tools menu
- [ ] Settings panel opens
- [ ] Add model works
- [ ] Edit model works
- [ ] Delete model works
- [ ] Save settings generates files
- [ ] Launch proxy works
- [ ] Status indicator updates
- [ ] Stop proxy works
- [ ] Restart proxy works (no crash!)
- [ ] Load template works
- [ ] Save template works
- [ ] Models appear in Rider AI Assistant
- [ ] AI Assistant can use models

---

## Code Review Process

All PRs require review before merging. Reviewers will check:

- Code quality and style
- Test coverage
- Documentation updates
- Breaking changes
- Security implications
- Performance impact

**As a reviewer:**
- Be respectful and constructive
- Explain suggested changes
- Approve when satisfied
- Request changes if needed

**As a contributor:**
- Respond to feedback promptly
- Make requested changes
- Ask questions if unclear
- Be patient during review

---

## Security

### Reporting Security Issues

**DO NOT open public issues for security vulnerabilities.**

Instead:
1. Email security concerns to: [your-security-email]
2. Provide detailed description
3. Include steps to reproduce
4. Wait for response before disclosure

### Security Best Practices

When contributing:
- Never commit API keys or secrets
- Use environment variables for sensitive data
- Validate all user input
- Sanitize file paths
- Use secure communication (HTTPS)
- Follow principle of least privilege

---

## Community

### Code of Conduct

- Be respectful and inclusive
- Welcome newcomers
- Provide constructive feedback
- Focus on the code, not the person
- No harassment or discrimination

### Getting Help

- **Questions:** [GitHub Discussions](https://github.com/native-apps/proxyme/discussions)
- **Bugs:** [GitHub Issues](https://github.com/native-apps/proxyme/issues)
- **Chat:** [Community Discord/Slack] (if available)

### Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Credited in release notes
- Mentioned in documentation

---

## License

By contributing to ProxyMe, you agree that your contributions will be licensed under the MIT License.

---

## Questions?

If you have questions about contributing:
- Check existing documentation
- Search closed issues and PRs
- Ask in GitHub Discussions
- Reach out to maintainers

**Thank you for contributing to ProxyMe!** 🎉

---

**Note:** This is an evolving document. Suggestions for improvement are welcome!