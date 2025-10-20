# Changelog - ProxyMe Proxy Server

All notable changes to the ProxyMe proxy server will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-10-17

### 🎉 Major Improvements

#### Added
- **Comprehensive debugging system**
  - Request/response logging with unique request IDs
  - Detailed error messages with context
  - Debug mode toggle via environment variable
  - Request timing and performance metrics
  - Token usage tracking in logs

- **New Perplexity model**
  - Added `sonar-deep-research` for exhaustive research tasks
  - Now supports all 7 Perplexity Sonar models

- **Enhanced error handling**
  - Detailed error responses with troubleshooting hints
  - Better handling of network errors (ECONNREFUSED, ETIMEDOUT, ENOTFOUND)
  - Validation errors with clear messages
  - API provider error forwarding with full details

- **Improved logging**
  - Startup banner with configuration summary
  - Color-coded console output
  - Request/response preview in debug mode
  - API key status validation on startup
  - Formatted model list with descriptions

- **New endpoints**
  - `/v1/chat/completions/test` - Test endpoint for validation
  - Enhanced `/health` endpoint with API key status
  - Improved `/` root endpoint with Rider configuration

- **Testing infrastructure**
  - Comprehensive test suite (`test-setup.js`)
  - 8 automated tests covering all functionality
  - Colored output for better readability
  - Detailed test reports with timing
  - Error handling validation tests

- **Documentation**
  - Complete `TROUBLESHOOTING.md` guide
  - Enhanced `README.md` with step-by-step instructions
  - `quick-start.sh` script for automated setup
  - API reference documentation
  - Security best practices

- **Developer experience**
  - Request logging middleware
  - Graceful shutdown handling (SIGINT, SIGTERM)
  - Uncaught exception handling
  - 404 handler with helpful messages
  - Extended timeout for slow models (120s)

#### Changed
- **Model configurations**
  - Added descriptions for all models
  - Better model metadata in responses
  - Grouped models by provider in test output

- **Startup process**
  - Professional formatted startup banner
  - Clear configuration display
  - API key validation on startup
  - Formatted available models list
  - All endpoints listed with URLs

- **Error responses**
  - OpenAI-compatible error format
  - Detailed error codes and types
  - Helpful troubleshooting hints in messages
  - Debug details included when DEBUG=true

- **Response handling**
  - Better status code validation
  - Proper HTTP error forwarding
  - Enhanced error context in logs
  - Response preview in debug mode

#### Fixed
- Package.json syntax errors (duplicate fields)
- Improved JSON formatting in responses
- Better handling of provider-specific errors
- Timeout handling for long-running requests
- Port conflict detection and handling

### 📝 Configuration

#### New Environment Variables
- `DEBUG` - Enable/disable verbose logging (default: true)
- `PORT` - Server port configuration (default: 3000)

#### Updated .env.template
- Added DEBUG configuration
- Better comments and examples
- Clear API key placeholder format

### 🧪 Testing

#### Test Coverage
- Health check endpoint validation
- Models list endpoint validation
- Root info endpoint validation
- DeepSeek chat completion
- Perplexity Sonar completion
- Invalid model error handling
- Empty messages error handling
- DeepSeek Reasoner (optional)

#### Test Improvements
- Colored console output
- Request timing for each test
- Token usage reporting
- Pass/fail rate calculation
- Detailed failure diagnostics
- Helpful next steps on success

### 📚 Documentation Improvements

#### README.md
- Restructured with clear sections
- Added comprehensive troubleshooting section
- Step-by-step Rider IDE configuration
- Model comparison tables
- Performance tips
- Security best practices
- API reference
- Pricing information

#### New TROUBLESHOOTING.md
- 10 common issues with solutions
- Quick diagnosis commands
- Advanced debugging techniques
- Diagnostic checklist
- Performance optimization tips
- Complete reset instructions

#### New quick-start.sh
- Automated setup script
- Prerequisite checking
- Interactive configuration
- API key input prompts
- Optional server startup

### 🔧 Technical Improvements

#### Code Quality
- Better separation of concerns
- Consistent error handling patterns
- Improved code comments
- Enhanced logging throughout
- Better async/await usage

#### Performance
- Extended timeout for research models
- Better connection handling
- Reduced redundant logging
- Optimized JSON parsing

#### Reliability
- Graceful shutdown handling
- Better error recovery
- Process signal handling
- Uncaught exception handlers
- Connection retry logic

### 🎯 Rider IDE Compatibility

- Verified OpenAI API compatibility
- Tested connection with Rider IDE
- Confirmed model selection works
- Validated chat completions
- Tested all model types

### 📦 Dependencies

No dependency changes in this release. Using:
- express: ^4.18.2
- axios: ^1.6.0
- dotenv: ^16.3.1

---

## [1.0.0] - 2025-10-17

### Initial Release

#### Features
- OpenAI-compatible proxy server
- Support for DeepSeek models:
  - `deepseek-chat`
  - `deepseek-reasoner`
- Support for Perplexity Sonar models:
  - `sonar`
  - `sonar-pro`
  - `sonar-reasoning`
  - `sonar-reasoning-pro`
- Basic endpoints:
  - `/health` - Health check
  - `/v1/models` - List models
  - `/v1/chat/completions` - Chat completions
- Environment-based API key management
- Basic error handling
- Rider IDE integration support

---

## Unreleased Features

### Planned for 1.2.0
- [ ] Streaming support for real-time responses
- [ ] Request caching for improved performance
- [ ] Rate limiting and request queuing
- [ ] Usage statistics and analytics
- [ ] Multi-user support with separate API keys
- [ ] Web-based configuration UI
- [ ] Docker container support
- [ ] Kubernetes deployment configs

### Under Consideration
- [ ] Support for additional providers (Anthropic, Cohere, etc.)
- [ ] Prompt template system
- [ ] Request/response history logging
- [ ] Cost tracking and budgets
- [ ] Model fallback system
- [ ] Load balancing across multiple API keys
- [ ] Custom model aliases
- [ ] Plugin system for extensibility

---

## Migration Guide

### Upgrading from 1.0.0 to 1.1.0

1. **Update .env file:**
   ```env
   # Add new optional variable
   DEBUG=true
   ```

2. **Review new models:**
   - `sonar-deep-research` is now available
   - Update Rider model selections if desired

3. **Check new documentation:**
   - Review TROUBLESHOOTING.md for common issues
   - Check README.md for new features

4. **Run tests:**
   ```bash
   npm test
   ```

5. **Restart proxy:**
   ```bash
   npm start
   ```

No breaking changes - fully backward compatible!

---

## Contributors

- **ProxyMe Community** - Development and improvements

---

## Links

- [Repository](https://github.com/native-apps/proxyme)
- [Issue Tracker](https://github.com/native-apps/proxyme/issues)
- [DeepSeek API](https://api-docs.deepseek.com/)
- [Perplexity API](https://docs.perplexity.ai/)
- [Anthropic API](https://docs.anthropic.com/)
- [Rider IDE](https://www.jetbrains.com/rider/)

---

**Note:** This proxy is designed for personal use and development. For production deployments, consider additional security measures, monitoring, and scaling solutions.