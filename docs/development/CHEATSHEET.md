# 🚀 ProxyMe - Quick Reference Cheat Sheet

**One-page reference for common tasks and commands**

---

## 🎯 Quick Commands

### Check Port Status
```bash
cd ProxyMe && ./proxy-helper.sh check
```

### Kill Proxy Process
```bash
cd ProxyMe && ./proxy-helper.sh kill
```

### View Logs
```bash
tail -f ~/.proxyme/logs/proxyme-*.log
```

### Test Health
```bash
curl http://localhost:3000/health | jq
```

---

## 📁 Important Locations

| What | Where |
|------|-------|
| **Logs** | `~/.proxyme/logs/proxyme-<project>.log` |
| **Proxy Files** | `~/.proxyme/proxy-<project>/` |
| **Settings** | `<project>/.idea/proxyme-settings.xml` |
| **Plugin** | `~/Library/Application Support/JetBrains/Rider*/plugins/ProxyMe/` |

---

## 🔧 Common Tasks

### Launch Proxy
1. Open Rider IDE
2. Open "ProxyMe" tool window (bottom of IDE)
3. Click **Launch** button
4. Wait for green status ✅

### Stop Proxy
1. Open "ProxyMe" tool window
2. Click **Stop** button
3. Verify: `./proxy-helper.sh check` → "Port 3000 is FREE"

### Configure API Keys
1. Settings → Tools → ProxyMe
2. Scroll to "API Keys" section
3. Enter DeepSeek key: https://platform.deepseek.com/
4. Enter Perplexity key: https://www.perplexity.ai/settings/api
5. Click **Apply** → **OK**

### Configure Rider AI
1. Settings → AI Assistant → Models
2. Click **+** → Add OpenAI API
3. **URL:** `http://localhost:3000/v1`
4. **API Key:** (leave empty)
5. Click **Test Connection** → Should succeed!
6. Select models from dropdown

---

## 🐛 Troubleshooting

### Proxy Won't Stop
```bash
cd ProxyMe
./proxy-helper.sh kill
```

### Port Already in Use
```bash
cd ProxyMe
./proxy-helper.sh check  # Find what's using it
./proxy-helper.sh kill   # Kill it
```

### Can't Find Logs
```bash
ls ~/.proxyme/logs/
# OR use helper:
./proxy-helper.sh logs
```

### Multiple Proxies Running
```bash
cd ProxyMe
./proxy-helper.sh list   # See all proxies
./proxy-helper.sh clean  # Kill all
```

### Proxy Won't Start
1. Check Node.js: `node --version`
2. Check port: `./proxy-helper.sh check`
3. View logs: `tail -50 ~/.proxyme/logs/proxyme-*.log`

---

## 🔍 Diagnostic Commands

### One-Liners

**Is proxy running?**
```bash
curl -f http://localhost:3000/health >/dev/null 2>&1 && echo "✅ UP" || echo "❌ DOWN"
```

**What's on port 3000?**
```bash
lsof -i :3000
```

**Kill process on port 3000:**
```bash
kill -9 $(lsof -ti :3000)
```

**Show recent logs:**
```bash
tail -20 ~/.proxyme/logs/proxyme-*.log
```

**Check API keys are set:**
```bash
grep API_KEY ~/.proxyme/proxy-*/.env
```

---

## 📊 Helper Script Menu

```bash
cd ProxyMe && ./proxy-helper.sh
```

**Options:**
1. Check what's using port 3000
2. Kill process on port 3000
3. Find all Node.js proxy processes
4. Test proxy health
5. Check ProxyMe plugin logs
6. Clean up ALL proxy processes
7. Show help

---

## 🎯 Tool Window (Bottom of Rider)

**Buttons:**
- **Launch** - Start proxy
- **Stop** - Stop proxy  
- **Restart** - Restart proxy
- **Health** - Open health check in browser
- **Clear** - Clear log display
- **Open Log** - Open full log file

**Features:**
- ● Status indicator (Green/Red)
- Live log updates (every 2 seconds)
- Auto-scroll option

---

## ⚡ Quick Tests

### Test DeepSeek
```bash
curl http://localhost:3000/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{"model":"deepseek-chat","messages":[{"role":"user","content":"Hi"}]}'
```

### Test Perplexity
```bash
curl http://localhost:3000/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{"model":"sonar-pro","messages":[{"role":"user","content":"Hi"}]}'
```

### List Available Models
```bash
curl http://localhost:3000/v1/models | jq '.data[].id'
```

---

## 🚨 Emergency Reset

**If everything is broken:**
```bash
# Kill all proxies
pkill -f "node.*server.js"
pkill -f "node.*proxy.js"

# Delete ProxyMe files
rm -rf ~/.proxyme

# Restart Rider and reconfigure
```

---

## 💡 Pro Tips

### Useful Aliases (add to ~/.zshrc)
```bash
alias proxy-check='lsof -i :3000'
alias proxy-kill='kill -9 $(lsof -ti :3000)'
alias proxy-test='curl http://localhost:3000/health | jq'
alias proxy-logs='tail -f ~/.proxyme/logs/proxyme-*.log'
```

### Multiple Projects
Use different ports:
- Project A → Port 3000
- Project B → Port 3001
- Project C → Port 3002

### Before Launching
Always check port is free:
```bash
./proxy-helper.sh check
```

---

## 📋 Model List

**DeepSeek (2):**
- `deepseek-chat` - Main chat
- `deepseek-reasoner` - Reasoning

**Perplexity (5):**
- `sonar` - Standard search
- `sonar-pro` - Advanced search
- `sonar-reasoning` - Fast reasoning
- `sonar-reasoning-pro` - Precise reasoning
- `sonar-deep-research` - Expert research

---

## 🎓 Getting Help

1. **Check this cheatsheet** - You are here!
2. **Read debugging guide** - `DEBUGGING_GUIDE.md`
3. **Check full docs** - `README.md`
4. **Run helper script** - `./proxy-helper.sh`
5. **Check logs** - `~/.proxyme/logs/`

---

## ✅ Success Checklist

- [ ] Node.js installed (`node --version`)
- [ ] Port 3000 is free (`./proxy-helper.sh check`)
- [ ] API keys configured (Settings → ProxyMe)
- [ ] Proxy launched (green status in tool window)
- [ ] Health check passes (`curl localhost:3000/health`)
- [ ] Rider AI configured (Settings → AI Assistant)
- [ ] Test connection succeeds
- [ ] Models appear in dropdown
- [ ] Can ask questions in AI chat

---

**Print this page and keep it handy!** 📄

**Version:** 1.0.0  
**Updated:** 2025-10-20