# 🐛 ProxyMe - Debugging & Troubleshooting Guide

**Last Updated:** October 20, 2025  
**Plugin Version:** 1.0.0  

---

## 🎯 Quick Reference

### Common Issues Solved

| Problem | Quick Fix |
|---------|-----------|
| Proxy still running after clicking "Stop" | Use `./proxy-helper.sh kill` |
| Port 3000 in use | Use `./proxy-helper.sh check` to find culprit |
| Can't find log files | Check `~/.proxyme/logs/` |
| Multiple proxies running | Use `./proxy-helper.sh clean` |
| Proxy won't start | Check Node.js: `node --version` |

---

## 🔍 The Mystery of the Persistent Proxy

### What Happened?

**Symptom:** After closing Rider IDE and clicking "Stop Proxy", the proxy was STILL responding on `http://localhost:3000`

**Cause:** There were TWO different proxy processes:
1. **Old Standalone Proxy** - Running since Friday from a terminal
2. **Plugin Proxy** - The one managed by the Rider plugin

### How to Find What's Running

#### Method 1: Use the Helper Script (Recommended)
```bash
cd ProxyMe
./proxy-helper.sh check
```

**Output:**
```
✗ Port 3000 is IN USE by PID: 14811

Process Details:
PID   PPID  USER      %CPU %MEM  STARTED      TIME COMMAND
14811 14791 nativeapps 0.0  0.1   Fri06PM   0:08.18 node proxy.js

Working Directory:
/Users/nativeapps/Native Apps Dev/native-spark-orange/labs/ProxyMe/Node.js Proxy Cloud AI APIs
```

#### Method 2: Manual Commands

**Check what's on port 3000:**
```bash
lsof -i :3000
```

**Find all Node.js proxy processes:**
```bash
ps aux | grep -i "node.*server\|node.*proxy" | grep -v grep
```

**Find process working directory:**
```bash
lsof -p <PID> | grep cwd
```

---

## 🛠️ Proxy Helper Script

A comprehensive tool to manage and debug proxy processes.

### Installation

Already included in the ProxyMe directory:
```bash
cd ProxyMe
chmod +x proxy-helper.sh
```

### Usage

#### Interactive Mode (Recommended for Beginners)
```bash
./proxy-helper.sh
```

Shows a menu with options:
1. Check what's using port 3000
2. Kill process on port 3000
3. Find all Node.js proxy processes
4. Test proxy health (curl /health)
5. Check ProxyMe plugin logs
6. Clean up ALL proxy processes
7. Show help

#### Command Line Mode (For Scripts)

**Check port 3000:**
```bash
./proxy-helper.sh check
```

**Kill process on port 3000:**
```bash
./proxy-helper.sh kill
```

**List all proxy processes:**
```bash
./proxy-helper.sh list
```

**Test proxy health:**
```bash
./proxy-helper.sh test
```

**View logs:**
```bash
./proxy-helper.sh logs
```

**Clean up everything:**
```bash
./proxy-helper.sh clean
```

---

## 📁 Where Are Things Located?

### Plugin Files

```
~/.proxyme/
├── proxy-<project-name>/          # Extracted proxy server files
│   ├── server.js
│   ├── package.json
│   ├── node_modules/
│   └── .env                       # Generated from settings
│
├── logs/
│   └── proxyme-<project-name>.log # Proxy logs
│
└── <project-name>/
    └── .env                       # Another location (legacy)
```

### Settings Files

```
<project>/.idea/
└── proxyme-settings.xml           # Project settings (API keys!)
```

**⚠️ WARNING:** Never commit `.idea/proxyme-settings.xml` - it contains your API keys!

### Plugin Installation

```
~/Library/Application Support/JetBrains/Rider<version>/plugins/
└── ProxyMe/
```

---

## 🐛 Common Issues & Solutions

### Issue 1: "Proxy Won't Stop" ✅ SOLVED

**Symptom:**
- Clicked "Stop Proxy" in plugin
- Status shows red (stopped)
- BUT: `http://localhost:3000` still responds

**Cause:**
Multiple proxy processes running from different locations:
- Old terminal-launched proxy
- Rider plugin-launched proxy
- Manually started proxy

**Solution:**

**Option A: Use Helper Script (Easy)**
```bash
cd ProxyMe
./proxy-helper.sh clean
```

**Option B: Manual Cleanup**
```bash
# Find process on port 3000
lsof -ti :3000

# Kill it
kill -9 $(lsof -ti :3000)

# Verify
curl http://localhost:3000/health
# Should fail with "connection refused"
```

**Option C: Kill All Node Proxies**
```bash
pkill -f "node.*server.js"
pkill -f "node.*proxy.js"
```

---

### Issue 2: "Can't Find Log Files"

**Symptom:**
- Settings show path like `rs/nativeapps/.proxyme/logs/proxyme.log`
- File doesn't exist at that path
- Path looks truncated

**Cause:**
UI text field is too narrow, truncating the display.

**Actual Location:**
```bash
~/.proxyme/logs/proxyme-<project-name>.log
```

**How to Find:**
```bash
# List all logs
ls -lh ~/.proxyme/logs/

# View most recent log
tail -50 ~/.proxyme/logs/proxyme-*.log

# Use helper script
./proxy-helper.sh logs
```

**Fix in Next Version:**
- Wider text field in UI
- "Open Log" button to open directly
- Copy-to-clipboard button

---

### Issue 3: "Multiple Proxies Running"

**Symptom:**
- Started proxy from terminal
- Started proxy from plugin
- Both running on different ports... or same port causing conflicts

**How to Check:**
```bash
./proxy-helper.sh list
```

**Output Example:**
```
PID: 12345
Command: node server.js
Directory: /Users/you/.proxyme/proxy-ProxyMe

PID: 67890
Command: node proxy.js
Directory: /Users/you/projects/old-proxy
```

**Solution:**
```bash
# Kill all
./proxy-helper.sh clean

# OR kill specific PID
kill <PID>

# Then restart from plugin only
```

---

### Issue 4: "Rider Closed But Proxy Still Running"

**This is EXPECTED BEHAVIOR** (for now)

**Why?**
The proxy runs as a separate Node.js process. When Rider closes, the process doesn't automatically terminate.

**Current Workaround:**
```bash
./proxy-helper.sh kill
```

**Future Fix (Phase 8):**
We'll add a shutdown hook to kill the proxy when Rider closes.

**Manual Implementation:**
Add to `ProxyMeProjectService.java`:
```java
// In constructor
project.getMessageBus()
    .connect()
    .subscribe(ProjectManagerListener.TOPIC, new ProjectManagerListener() {
        @Override
        public void projectClosing(@NotNull Project project) {
            stopProxy();
        }
    });
```

---

### Issue 5: "Node.js Not Found"

**Symptom:**
```
Failed to start proxy: Cannot run program "node": error=2, No such file or directory
```

**Check Node.js:**
```bash
node --version
# Should show: v18.x.x or newer
```

**Solution A: Install Node.js**
```bash
# macOS with Homebrew
brew install node

# Or download from:
https://nodejs.org/
```

**Solution B: Fix PATH**
```bash
# Find where node is installed
which node

# Add to ~/.zshrc or ~/.bash_profile
export PATH="/opt/homebrew/bin:$PATH"

# Restart Rider
```

---

### Issue 6: "Port 3000 Already in Use"

**Symptom:**
```
Error: listen EADDRINUSE: address already in use :::3000
```

**Find What's Using It:**
```bash
./proxy-helper.sh check
```

**Solutions:**

**Option 1: Kill the Other Process**
```bash
./proxy-helper.sh kill
```

**Option 2: Use Different Port**
1. Settings → Tools → ProxyMe
2. Change Port to `3001` or `3002`
3. Click Apply
4. Launch Proxy
5. Update Rider AI settings:
   - URL: `http://localhost:3001/v1`

---

### Issue 7: "npm Install Failed"

**Symptom:**
```
Failed to install dependencies
npm ERR! ...
```

**Solution:**

**Check npm:**
```bash
npm --version
```

**Manual Install:**
```bash
cd ~/.proxyme/proxy-<project-name>
rm -rf node_modules
npm install
```

**Clear npm Cache:**
```bash
npm cache clean --force
cd ~/.proxyme/proxy-<project-name>
npm install
```

**Check Permissions:**
```bash
ls -la ~/.proxyme/proxy-<project-name>
# All files should be owned by you
```

---

## 📊 Diagnostic Checklist

### When Proxy Won't Work

Run through this checklist:

- [ ] **Check port is free**
  ```bash
  ./proxy-helper.sh check
  ```

- [ ] **Node.js installed**
  ```bash
  node --version  # Should be v18+
  ```

- [ ] **npm installed**
  ```bash
  npm --version
  ```

- [ ] **Dependencies installed**
  ```bash
  ls ~/.proxyme/proxy-*/node_modules/
  # Should exist and contain packages
  ```

- [ ] **API keys configured**
  ```bash
  cat ~/.proxyme/proxy-*//.env | grep API_KEY
  # Should show your keys (not empty)
  ```

- [ ] **Proxy actually running**
  ```bash
  ps aux | grep "node.*server.js" | grep -v grep
  # Should show a process
  ```

- [ ] **Health check responds**
  ```bash
  curl http://localhost:3000/health
  # Should return JSON
  ```

- [ ] **Check logs for errors**
  ```bash
  tail -50 ~/.proxyme/logs/proxyme-*.log
  ```

---

## 🔧 Advanced Debugging

### Enable Debug Mode

**In plugin settings:**
1. Settings → Tools → ProxyMe
2. Enable "Show logs in Terminal"
3. Launch proxy
4. Watch terminal for detailed output

**Via environment variable:**
```bash
cd ~/.proxyme/proxy-<project-name>
DEBUG=* node server.js
```

### Monitor Real-Time Logs

```bash
# Follow log file
tail -f ~/.proxyme/logs/proxyme-*.log

# Or use watch
watch -n 1 'tail -20 ~/.proxyme/logs/proxyme-*.log'
```

### Test Individual Components

**Test Node.js:**
```bash
node -e "console.log('Node.js works!')"
```

**Test npm:**
```bash
npm list --depth=0
```

**Test Express (proxy framework):**
```bash
cd ~/.proxyme/proxy-<project-name>
node -e "const express = require('express'); console.log('Express loaded')"
```

**Test API keys loaded:**
```bash
cd ~/.proxyme/proxy-<project-name>
node -e "require('dotenv').config(); console.log('DEEPSEEK_API_KEY:', process.env.DEEPSEEK_API_KEY ? 'SET' : 'NOT SET')"
```

---

## 📝 Logging Best Practices

### Where to Look

**Plugin logs:**
```
~/Library/Logs/JetBrains/Rider<version>/idea.log
```

**Proxy logs:**
```
~/.proxyme/logs/proxyme-<project-name>.log
```

**System logs (macOS):**
```
/var/log/system.log
```

### Read Logs Efficiently

**Last 50 lines:**
```bash
tail -50 ~/.proxyme/logs/proxyme-*.log
```

**Search for errors:**
```bash
grep -i error ~/.proxyme/logs/proxyme-*.log
```

**Search for specific model:**
```bash
grep -i "deepseek" ~/.proxyme/logs/proxyme-*.log
```

**Show only timestamps and errors:**
```bash
grep -E '^\[|error|Error|ERROR' ~/.proxyme/logs/proxyme-*.log
```

---

## 🆘 Emergency Reset

If everything is broken and you want to start fresh:

```bash
#!/bin/bash
# Emergency reset script

echo "🚨 Emergency Reset - This will:"
echo "  • Kill all proxy processes"
echo "  • Delete all ProxyMe files"
echo "  • Clear logs"
echo "  • Reset settings"
echo ""
read -p "Continue? (y/N): " confirm

if [ "$confirm" = "y" ]; then
    # Kill all proxies
    pkill -f "node.*server.js"
    pkill -f "node.*proxy.js"
    
    # Delete ProxyMe directory
    rm -rf ~/.proxyme
    
    # Delete plugin settings (per project)
    # find ~/projects -name "proxyme-settings.xml" -delete
    
    echo "✓ Reset complete"
    echo ""
    echo "Next steps:"
    echo "  1. Restart Rider IDE"
    echo "  2. Re-configure API keys"
    echo "  3. Launch proxy"
else
    echo "Cancelled"
fi
```

Save as `emergency-reset.sh` and run if needed.

---

## 💡 Pro Tips

### 1. Always Check Port First

Before launching proxy:
```bash
./proxy-helper.sh check
```

### 2. Use Different Ports for Different Projects

```
Project A → Port 3000
Project B → Port 3001
Project C → Port 3002
```

### 3. Bookmark These Commands

```bash
# Add to ~/.zshrc or ~/.bash_profile

alias proxy-check='lsof -i :3000'
alias proxy-kill='kill -9 $(lsof -ti :3000)'
alias proxy-test='curl http://localhost:3000/health | jq'
alias proxy-logs='tail -f ~/.proxyme/logs/proxyme-*.log'
```

### 4. Create a Launch Script

```bash
#!/bin/bash
# ~/bin/proxyme-start.sh

PORT=${1:-3000}

# Kill old process
kill -9 $(lsof -ti :$PORT) 2>/dev/null

# Start proxy
cd ~/.proxyme/proxy-ProxyMe
PORT=$PORT node server.js
```

### 5. Monitor with htop

```bash
brew install htop
htop -p $(lsof -ti :3000)
```

---

## 🎯 Quick Command Reference

### One-Liners

**Kill proxy on port 3000:**
```bash
kill -9 $(lsof -ti :3000)
```

**Test if proxy is responding:**
```bash
curl -f http://localhost:3000/health >/dev/null 2>&1 && echo "UP" || echo "DOWN"
```

**View last 20 log lines:**
```bash
tail -20 ~/.proxyme/logs/proxyme-*.log
```

**Find proxy directory:**
```bash
ls -d ~/.proxyme/proxy-*
```

**Test API keys are set:**
```bash
grep API_KEY ~/.proxyme/proxy-*/.env
```

**Count running Node processes:**
```bash
ps aux | grep -c "node.*server\|node.*proxy" | grep -v grep
```

---

## 📞 Getting Help

### Before Asking for Help

Gather this information:

1. **System info:**
   ```bash
   uname -a
   node --version
   npm --version
   ```

2. **Process info:**
   ```bash
   ./proxy-helper.sh list
   ```

3. **Port status:**
   ```bash
   ./proxy-helper.sh check
   ```

4. **Recent logs:**
   ```bash
   tail -50 ~/.proxyme/logs/proxyme-*.log
   ```

5. **Health check:**
   ```bash
   curl http://localhost:3000/health
   ```

### Support Channels

- GitHub Issues: [coming soon]
- Documentation: `ProxyMe/README.md`
- This guide: `ProxyMe/DEBUGGING_GUIDE.md`

---

## 🎓 Understanding the Architecture

### How Proxy Launching Works

```
1. User clicks "Launch" in Rider
2. Plugin extracts proxy files to ~/.proxyme/proxy-<project>
3. Plugin generates .env file with API keys
4. Plugin runs: npm install (if needed)
5. Plugin starts: node server.js
6. Proxy binds to port 3000
7. Plugin monitors process health
```

### Why Proxy Survives Rider Close

The proxy is a **child process** spawned by the plugin. When Rider closes:
- Plugin process terminates
- Child process becomes orphaned
- OS adopts the child (doesn't kill it)
- Proxy continues running

**Solution:** Add shutdown hook (planned for Phase 8)

---

## ✅ Success Indicators

### You Know It's Working When:

**1. Port Check:**
```bash
./proxy-helper.sh check
```
Shows: `✗ Port 3000 is IN USE by PID: <some-number>`

**2. Health Check:**
```bash
curl http://localhost:3000/health | jq
```
Returns JSON with `"status": "healthy"`

**3. Process Check:**
```bash
ps aux | grep "node.*server.js" | grep -v grep
```
Shows a running process

**4. Log Check:**
```bash
tail -5 ~/.proxyme/logs/proxyme-*.log
```
Shows recent activity, no errors

**5. Rider AI Check:**
- Settings → AI Assistant → Models
- "Test Connection" succeeds
- Can see model list

---

## 🎉 You're Now a ProxyMe Debug Expert!

Save this guide and the `proxy-helper.sh` script - they'll solve 99% of issues.

**Remember:**
- Always check what's using port 3000 first
- Use the helper script - it's your friend
- Logs tell the truth
- When in doubt, restart fresh

---

**Last Updated:** October 20, 2025  
**Version:** 1.0.0  
**Maintained by:** ProxyMe Team