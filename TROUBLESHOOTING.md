# Troubleshooting Guide

Common issues and solutions for ProxyMe plugin.

---

## Installation Issues

### Plugin Won't Install

**Symptom:** Error when installing plugin from disk

**Solutions:**

1. **Check Rider version:**
   ```
   Help → About
   ```
   Required: Rider 2024.3 or later

2. **Verify ZIP file:**
   ```bash
   unzip -t ProxyMe-2.1.0.zip
   ```
   Should show no errors

3. **Try clean install:**
   - Uninstall old version
   - Restart Rider
   - Install new version
   - Restart again

### Plugin Doesn't Appear in Tools Menu

**Solutions:**

1. **Verify installation:**
   ```
   Settings → Plugins → Installed
   ```
   ProxyMe should be listed and enabled

2. **Check plugin is enabled:**
   - Find ProxyMe in plugin list
   - Ensure checkbox is checked

3. **Restart Rider:**
   ```
   File → Exit → Reopen Rider
   ```

---

## Proxy Server Issues

### Proxy Won't Start

**Symptom:** "Failed to start proxy" error

**Check Node.js:**
```bash
node --version
# Required: v18 or later
```

**Install Node.js if missing:**
- macOS: `brew install node`
- Windows: Download from nodejs.org
- Linux: `sudo apt install nodejs npm`

**Check port availability:**
```bash
# macOS/Linux
lsof -i :3000

# Windows
netstat -ano | findstr :3000
```

**If port is in use:**
```bash
# Kill the process using port 3000
# macOS/Linux
kill -9 <PID>

# Windows
taskkill /PID <PID> /F
```

**Check proxy logs:**
```bash
tail -50 ~/.proxyme/logs/proxyme.log
```

### Proxy Starts But Models Don't Work

**Check configuration files:**
```bash
# Verify models.json exists
cat ~/.proxyme/proxy/models.json

# Check .env file has API keys
cat ~/.proxyme/proxy/.env
```

**Verify API keys are valid:**
- Test keys directly with provider's API
- Check for typos or expired keys
- Ensure keys have proper permissions

**Test proxy endpoint:**
```bash
# Check health
curl http://localhost:3000/health

# List available models
curl http://localhost:3000/v1/models
```

### Proxy Crashes on Restart

**Symptom:** Rider crashes when clicking "Restart Proxy"

**Solution:** This is a known issue in older versions. Update to v2.1.0 Build 2 or later.

**Workaround for older versions:**
1. Stop proxy manually
2. Wait 5 seconds
3. Start proxy again
4. Don't use "Restart Proxy" button

---

## Model Configuration Issues

### Models Don't Appear in Rider AI Assistant

**Symptom:** Rider shows wrong models or no models

**Solution 1: Restart the proxy**
```
Tools → ProxyMe → Restart Proxy Server
```
Models are loaded only on startup.

**Solution 2: Refresh Rider AI Assistant**
1. Go to `Settings → Tools → AI Assistant → Models`
2. Click "Test Connection"
3. Close and reopen AI Assistant
4. Check model dropdown

**Solution 3: Full Rider restart**
1. Save all work
2. Exit Rider completely
3. Reopen Rider
4. Check AI Assistant again

**Verify models.json is generated:**
```bash
cat ~/.proxyme/proxy/models.json | jq '.models[].id'
```

Should show your enabled models.

### Wrong Models Show Up

**Symptom:** See default models instead of your configured models

**Cause:** Proxy hasn't restarted since you made changes

**Solution:**
1. Save settings in ProxyMe
2. Restart proxy
3. Wait 5 seconds
4. Refresh Rider AI Assistant

### Can't Add Model

**Symptom:** "Add Model" button doesn't work or dialog doesn't open

**Solutions:**

1. **Check for modal dialogs:**
   - Look for hidden error dialogs
   - Press Escape to close any modals

2. **Restart Rider:**
   - Close Rider completely
   - Reopen and try again

3. **Check logs for errors:**
   ```bash
   tail -50 ~/.proxyme/logs/proxyme.log
   ```

---

## API Key Issues

### "Invalid API Key" Error

**Check key format:**

Different providers use different key formats:

- **DeepSeek:** `sk-...`
- **Perplexity:** `pplx-...`
- **Anthropic:** `sk-ant-...`
- **OpenAI:** `sk-...`

**Verify key in .env file:**
```bash
cat ~/.proxyme/proxy/.env
```

Should show:
```
DEEPSEEK_API_KEY=sk-your-key-here
PERPLEXITY_API_KEY=pplx-your-key-here
ANTHROPIC_API_KEY=sk-ant-your-key-here
```

**Test key directly:**
```bash
# Test DeepSeek key
curl https://api.deepseek.com/v1/models \
  -H "Authorization: Bearer sk-your-key-here"

# Should return list of models
```

**Regenerate .env:**
1. Edit model in ProxyMe
2. Re-enter API key
3. Click OK and Save
4. Restart proxy

### API Keys Not Being Used

**Check environment file exists:**
```bash
ls -la ~/.proxyme/proxy/.env
```

**Check file permissions:**
```bash
# Should be readable
chmod 600 ~/.proxyme/proxy/.env
```

**Force regeneration:**
1. Delete .env file:
   ```bash
   rm ~/.proxyme/proxy/.env
   ```
2. Open ProxyMe settings
3. Click Save
4. Restart proxy

---

## Temperature and Settings Issues

### Temperature Changes Don't Apply

**Symptom:** Model behavior doesn't change when adjusting temperature

**Solution:** Temperature is applied only when proxy starts

**Steps:**
1. Edit model temperature in ProxyMe
2. Click OK and Save
3. **Restart proxy** ← Important!
4. Test model again

**Verify temperature in models.json:**
```bash
cat ~/.proxyme/proxy/models.json | jq '.models[] | {id, temperature}'
```

### Streaming Not Working

**Check stream setting:**
1. Edit model in ProxyMe
2. Ensure "Stream" is checked
3. Save settings
4. Restart proxy

**Test streaming:**
```bash
curl -X POST http://localhost:3000/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek-chat",
    "messages": [{"role": "user", "content": "Hi"}],
    "stream": true
  }'
```

Should show incremental responses.

---

## Template Issues

### Can't Load Template

**Check template exists:**
```bash
ls -la ~/.proxyme/templates/presets/
ls -la ~/.proxyme/templates/user/
```

**Check template format:**
```bash
cat ~/.proxyme/templates/user/your-template.json | jq .
```

Should be valid JSON.

**Recreate template directory:**
```bash
mkdir -p ~/.proxyme/templates/presets
mkdir -p ~/.proxyme/templates/user
```

### Template Won't Save

**Check directory permissions:**
```bash
ls -ld ~/.proxyme/templates/user/
# Should be writable
```

**Fix permissions:**
```bash
chmod 755 ~/.proxyme/templates/user/
```

**Check disk space:**
```bash
df -h ~
```

---

## Performance Issues

### Slow Response Times

**Check network connection:**
- Test provider API directly
- Check for network issues
- Try different provider

**Check temperature:**
- Lower temperature = faster, more focused
- Higher temperature = slower, more creative

**Check model size:**
- Smaller models respond faster
- Consider using faster models for quick tasks

### High CPU Usage

**Check proxy logs for errors:**
```bash
tail -f ~/.proxyme/logs/proxyme.log
```

**Restart proxy:**
```
Tools → ProxyMe → Restart Proxy Server
```

**Check for runaway processes:**
```bash
# macOS/Linux
ps aux | grep node

# Kill if needed
kill -9 <PID>
```

---

## Rider Integration Issues

### AI Assistant Can't Connect to Proxy

**Verify proxy is running:**
- Check status indicator (should be green)
- Test endpoint: `curl http://localhost:3000/health`

**Check Rider AI Assistant configuration:**
```
Settings → Tools → AI Assistant → Models
```

Should be:
- Provider: `OpenAI API`
- URL: `http://localhost:3000/v1`
- API Key: (empty)

**Test connection:**
1. Click "Test Connection" button
2. Should show: ✅ Connected
3. If fails, check proxy logs

### Models Don't Work in Quick Edit

**Symptom:** Quick Edit doesn't use expected model

**Solution:** Assign model in Rider AI Assistant

```
Settings → Tools → AI Assistant → Models
→ Instant helpers → Select your model
```

**Recommended for Quick Edit:**
- Model: `deepseek-chat`
- Temperature: 0.1-0.3 (focused)
- Avoid: High temperature or search models

### AI Assistant Shows Errors

**Common errors:**

**"Model not found"**
- Restart proxy to reload models
- Check model is enabled in ProxyMe
- Verify models.json contains the model

**"Connection refused"**
- Proxy isn't running - start it
- Wrong URL in Rider settings
- Firewall blocking localhost:3000

**"Rate limit exceeded"**
- Provider API rate limit hit
- Wait and try again
- Check your API plan limits

---

## File and Directory Issues

### Can't Find Configuration Files

**Default locations:**

```
~/.proxyme/
├── proxy/
│   ├── .env              # API keys
│   ├── models.json       # Enabled models
│   ├── proxy.js          # Proxy server
│   └── package.json      # Node dependencies
├── logs/
│   └── proxyme.log       # Log files
└── templates/
    ├── presets/          # Built-in templates
    └── user/             # Your templates
```

**Windows equivalent:**
```
C:\Users\YourUsername\.proxyme\
```

**Create directories if missing:**
```bash
mkdir -p ~/.proxyme/proxy
mkdir -p ~/.proxyme/logs
mkdir -p ~/.proxyme/templates/presets
mkdir -p ~/.proxyme/templates/user
```

### Permission Denied Errors

**Fix directory permissions:**
```bash
chmod 755 ~/.proxyme
chmod 755 ~/.proxyme/proxy
chmod 755 ~/.proxyme/logs
chmod 755 ~/.proxyme/templates
```

**Fix file permissions:**
```bash
chmod 600 ~/.proxyme/proxy/.env
chmod 644 ~/.proxyme/proxy/models.json
```

---

## Logging and Diagnostics

### Enable Debug Logging

**Check current logs:**
```bash
tail -f ~/.proxyme/logs/proxyme.log
```

**View full log:**
```bash
cat ~/.proxyme/logs/proxyme.log
```

**Clear old logs:**
```bash
rm ~/.proxyme/logs/*.log
```

**Check Rider IDE logs:**
```
Help → Show Log in Finder/Explorer
```

Look for ProxyMe-related errors.

### Collect Diagnostic Info

For bug reports, collect:

```bash
# System info
node --version
java -version
echo "OS: $(uname -a)"

# ProxyMe version
cat ProxyMe/gradle.properties | grep version

# Configuration
cat ~/.proxyme/proxy/models.json | jq .
cat ~/.proxyme/proxy/.env | sed 's/=.*/=***REDACTED***/g'

# Logs (last 100 lines)
tail -100 ~/.proxyme/logs/proxyme.log

# Proxy status
curl http://localhost:3000/health
curl http://localhost:3000/v1/models
```

---

## Known Issues

### Rider Crashes on Restart (v2.0.x)

**Status:** Fixed in v2.1.0 Build 2

**Workaround:**
- Use Stop → Start instead of Restart
- Update to latest version

### Only Tested with Rider IDE

**Warning:** This plugin has only been tested with JetBrains Rider.

**Other JetBrains IDEs:**
- IntelliJ IDEA: Not tested
- WebStorm: Not tested
- PyCharm: Not tested

**Use at your own risk with other IDEs.**

### AI-Generated Code

**Notice:** This project contains AI-generated code that may need:
- Refactoring
- Security review
- Bug fixes
- Performance optimization

**Contributions welcome!** See [CONTRIBUTING.md](CONTRIBUTING.md)

---

## Still Having Issues?

### Get Help

1. **Check existing issues:**
   - [GitHub Issues](https://github.com/native-apps/proxyme/issues)
   - Search for similar problems

2. **Ask the community:**
   - [GitHub Discussions](https://github.com/native-apps/proxyme/discussions)
   - Post your question with details

3. **Report a bug:**
   - Use the bug report template
   - Include logs and system info
   - Describe steps to reproduce

### Before Reporting

- [ ] Read this troubleshooting guide
- [ ] Check closed issues
- [ ] Try latest version
- [ ] Collect diagnostic information
- [ ] Test with minimal configuration

---

**Need more help?** Visit our [documentation](docs/README.md) or open an issue on GitHub.