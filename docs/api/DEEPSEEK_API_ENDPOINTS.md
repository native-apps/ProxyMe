# DeepSeek API Endpoints & Rider IDE Integration

**Date:** October 20, 2025  
**Status:** Investigation & Analysis  
**Version:** ProxyMe 1.0.3

---

## 🎯 Overview

This document clarifies which DeepSeek API endpoints to use for different Rider IDE AI Assistant modes and explains the differences between **Chat Completions** and **FIM (Fill-In-Middle) Completions**.

---

## 📚 DeepSeek API Endpoints

DeepSeek provides **two main API endpoint types**:

### 1. **Chat Completions API** (Production - Stable)

**Endpoint:** `https://api.deepseek.com/chat/completions`

**OpenAI-Compatible Path:** `POST /v1/chat/completions`

**Purpose:**
- Conversational AI interactions
- Multi-turn dialogues
- General chat-based tasks
- AI Assistant conversations
- Code explanations
- Documentation generation

**Request Format:**
```json
{
  "model": "deepseek-chat",
  "messages": [
    {"role": "system", "content": "You are a helpful assistant."},
    {"role": "user", "content": "Hello!"}
  ],
  "stream": true,
  "temperature": 0.7,
  "max_tokens": 2048
}
```

**Models Available:**
- `deepseek-chat` - General purpose chat model (DeepSeek-V3.1-Terminus, non-thinking mode)
- `deepseek-reasoner` - Advanced reasoning with Chain of Thought (DeepSeek-V3.1-Terminus, thinking mode)

**✅ Currently Used in ProxyMe:** YES

---

### 2. **FIM Completions API** (Beta - Code Completion)

**Endpoint:** `https://api.deepseek.com/beta/completions`

**Path:** `POST /completions` (requires `base_url="https://api.deepseek.com/beta"`)

**Purpose:**
- **FIM = Fill-In-Middle code completion**
- Inline code autocomplete
- Code generation within existing code blocks
- Content completion (like GitHub Copilot)
- Used by IDE plugins for real-time code suggestions

**Request Format:**
```json
{
  "model": "deepseek-chat",
  "prompt": "def fib(a):",
  "suffix": " return fib(a-1) + fib(a-2)",
  "max_tokens": 128,
  "stream": false
}
```

**Response Format:**
```json
{
  "choices": [
    {
      "text": "    if a <= 1:\n        return a\n"
    }
  ]
}
```

**Key Characteristics:**
- Max tokens: **4K limit**
- Beta feature (requires beta base URL)
- Designed for **inline code completion**
- Returns raw text, not message objects
- Used by VSCode plugins like Continue

**❌ Currently Used in ProxyMe:** NO (not implemented yet)

---

## 🔍 Key Differences

| Feature | Chat Completions | FIM Completions |
|---------|------------------|-----------------|
| **Endpoint** | `/chat/completions` | `/beta/completions` |
| **Base URL** | `https://api.deepseek.com` | `https://api.deepseek.com/beta` |
| **Status** | Production (Stable) | Beta |
| **Use Case** | Conversations, Q&A | Inline code completion |
| **Request Format** | Messages array | Prompt + Suffix |
| **Response Format** | Message objects | Raw text |
| **Max Tokens** | Model-dependent (64K+) | 4K limit |
| **Stream Support** | ✅ Yes | ✅ Yes |
| **Rider Chat Mode** | ✅ Perfect fit | ❌ Not suitable |
| **Rider Quick Edit** | 🟡 Requires patch format | 🟡 Requires patch format |
| **Inline Autocomplete** | ❌ Not designed for this | ✅ Perfect fit |

---

## 🎨 Rider IDE AI Assistant Modes

### Mode 1: **Chat Mode** ✅ WORKING

**What It Does:**
- User types questions in AI Assistant panel
- AI responds conversationally
- Multi-turn dialogue
- Code explanations, suggestions, etc.

**What ProxyMe Uses:**
- ✅ **Chat Completions API** (`/v1/chat/completions`)
- ✅ Works perfectly with both DeepSeek and Perplexity models

**Example Request from Rider:**
```json
{
  "model": "sonar",
  "messages": [
    {"role": "user", "content": "what time is it in london right now?"}
  ],
  "stream": true
}
```

**Result:** ✅ **WORKS PERFECTLY**

---

### Mode 2: **Quick Edit Mode** ⚠️ PARTIAL SUPPORT

**What It Does:**
- User selects code in editor
- Right-click → AI Assistant → Quick Edit
- Requests code modifications
- Expects structured patch format

**What Rider Expects:**
- Response in **XML patch format**:
  ```xml
  <llm-patch path="README.md" matcher="BeforeAfter">
  Remove selected text
  <!--Separator-->
  Before:
  ```markdown
  [original code]
  ```
  
  After:
  ```markdown
  [modified code]
  ```
  </llm-patch>
  ```

**What ProxyMe Sends:**
- ✅ **Chat Completions API** with system prompt containing patch instructions
- Rider includes detailed instructions in the system message

**The Problem:**
- 🔴 Most AI models return conversational text, not XML patches
- 🔴 Rider shows: "The patch is incomplete. Please re-generate"
- 🔴 The AI response is valid but not in the expected format

**Example Request from Rider (Quick Edit):**
```json
{
  "model": "sonar",
  "messages": [
    {
      "role": "user",
      "content": "You are an AI Coding Assistant integrated into JetBrains IDEs...\n\n**Generate Code Patches:**\n- Each patch MUST start with <llm-patch path=\"...\"> tags...\n- Provide 'Before' code snippet...\n- Provide 'After' code snippet...\n- Format as XML..."
    },
    {
      "role": "user",
      "content": "remove this selected text\n\nRelated information:\nFile: README.md\n[file content]"
    }
  ],
  "stream": true
}
```

**What We Currently Do:**
- Forward the request to AI model as-is
- AI returns conversational text (not XML)
- Rider can't parse it

**Result:** ⚠️ **NEEDS INVESTIGATION**

---

### Mode 3: **Inline Code Completion** 🔜 NOT IMPLEMENTED

**What It Does:**
- Type code → suggestions appear automatically
- Real-time autocomplete (like GitHub Copilot)
- Fill-in-middle completions

**What It Should Use:**
- ✅ **FIM Completions API** (`/beta/completions`)
- This is the **correct endpoint** for inline code completion

**Example FIM Request:**
```json
{
  "model": "deepseek-chat",
  "prompt": "function fibonacci(n) {",
  "suffix": "}",
  "max_tokens": 100
}
```

**Example FIM Response:**
```json
{
  "choices": [
    {
      "text": "\n  if (n <= 1) return n;\n  return fibonacci(n-1) + fibonacci(n-2);\n"
    }
  ]
}
```

**Current Status:** 🔜 **NOT IMPLEMENTED IN PROXYME**

**Notes:**
- Rider IDE may not support custom FIM completion providers
- This feature is typically built into IDEs (like JetBrains AI)
- Third-party plugins usually focus on Chat mode only

---

## 🚀 Recommendations for ProxyMe

### ✅ What We're Doing Right

1. **Chat Mode Integration:** Perfect ✅
   - Using Chat Completions API
   - Works with both DeepSeek and Perplexity
   - Streaming works
   - All models available

2. **OpenAI Compatibility:** Solid ✅
   - Standard `/v1/chat/completions` endpoint
   - Compatible with Rider's expectations
   - Easy to test and debug

### 🔧 What Needs Work

#### Issue 1: Quick Edit Mode Patch Format

**Problem:**
- AI models return conversational text
- Rider expects XML patch format
- Mismatch causes "incomplete patch" error

**Possible Solutions:**

**Option A: Add Response Post-Processing (Medium Effort)**
- Detect Quick Edit requests (look for patch instructions in system prompt)
- Parse AI conversational response
- Extract code changes
- Convert to XML patch format
- Return formatted patch to Rider

**Pros:**
- Universal solution for all models
- Works with DeepSeek, Perplexity, etc.

**Cons:**
- Complex parsing logic
- AI responses vary widely
- May not always work reliably

**Option B: Document Limitation (Easy)**
- Note that Quick Edit works best with certain models
- Recommend using Chat mode for complex requests
- Provide workaround instructions

**Pros:**
- No code changes needed
- Honest about limitations

**Cons:**
- Users can't use Quick Edit with our models

**Option C: Add "Patch Mode" System Prompt Enhancement (Easy)**
- Enhance system prompt to emphasize XML format
- Add examples of correct patch format
- Hope AI follows instructions better

**Pros:**
- Minimal code changes
- May improve success rate

**Cons:**
- Not guaranteed to work
- Model-dependent

**Recommended:** Start with **Option C**, document **Option B**, consider **Option A** for future

---

#### Issue 2: FIM Completions Not Supported

**Problem:**
- Screenshot shows ProxyAI plugin using `/beta/completions`
- This is the FIM endpoint for inline code completion
- ProxyMe doesn't support FIM yet

**Should We Add It?**

**Analysis:**
- Rider IDE has built-in AI completion (JetBrains AI)
- Third-party Chat mode is already a big win
- FIM requires different architecture
- May not integrate with Rider's completion system

**Recommendation:**
- ✅ **Focus on Chat Mode** (our strength)
- ✅ **Fix Quick Edit** if possible
- 🔜 **Consider FIM** only if users specifically request it
- 📝 **Document** that ProxyMe focuses on Chat, not inline completion

**Why Chat Mode > FIM for ProxyMe:**
1. Chat mode integrates natively with Rider AI Assistant
2. Users get full conversational AI
3. More use cases (explanations, refactoring, Q&A)
4. Works across all our models (DeepSeek + Perplexity)
5. Inline completion is complex and IDE-specific

---

## 📊 Current Status Summary

| Feature | Status | Endpoint Used | Notes |
|---------|--------|---------------|-------|
| **Chat Mode** | ✅ Working | `/v1/chat/completions` | Perfect integration |
| **Streaming** | ✅ Working | Same | Real-time responses |
| **Multiple Models** | ✅ Working | Same | 7 models available |
| **Quick Edit** | ⚠️ Partial | `/v1/chat/completions` | Patch format issue |
| **Inline Completion** | ❌ Not Implemented | N/A | Would need `/beta/completions` |
| **Code Explanation** | ✅ Working | `/v1/chat/completions` | Via Chat mode |
| **Documentation** | ✅ Working | `/v1/chat/completions` | Via Chat mode |

---

## 🎯 Action Items

### Immediate (Today)
- [x] Document API endpoint differences
- [ ] Test Quick Edit with different models
- [ ] Try adding patch format hints to system prompt
- [ ] Document Quick Edit limitations if unfixable

### Short Term (This Week)
- [ ] Investigate AI response parsing for patch format
- [ ] Test if DeepSeek-Reasoner handles patch format better
- [ ] Create user guide for Quick Edit workarounds
- [ ] Add more detailed error messages for Quick Edit failures

### Future (Nice to Have)
- [ ] Explore FIM completions API
- [ ] Evaluate demand for inline code completion
- [ ] Research Rider's inline completion plugin architecture
- [ ] Consider adding FIM if there's user demand

---

## 💡 Answer to Your Question

### "Should we use `/beta/completions` for code completions?"

**Short Answer:** It depends on the use case.

**For Rider Chat Mode (what we have):**
- ❌ **NO** - Use `/chat/completions` (what we're already doing)
- This is correct for conversational AI in the AI Assistant panel

**For Inline Code Completion (like GitHub Copilot):**
- ✅ **YES** - Use `/beta/completions` (FIM endpoint)
- But this requires different integration architecture
- Rider may not support third-party inline completion providers

**For Quick Edit Mode:**
- 🟡 **Use `/chat/completions`** (current approach)
- The endpoint is correct
- The issue is response format, not endpoint choice

### What ProxyAI Plugin Is Doing

Looking at your screenshot:
- ProxyAI has a **"Code Completions"** tab
- Uses `https://api.deepseek.com/beta/completions` (FIM endpoint)
- This is for **inline autocomplete**, not Chat mode
- They likely have their own completion UI, separate from Rider's native AI Assistant

**Key Difference:**
- **ProxyAI:** Custom UI + FIM completions
- **ProxyMe:** Native Rider AI Assistant integration + Chat completions

**Our Approach Is Better Because:**
1. ✅ Native integration with Rider AI Assistant
2. ✅ No custom UI needed
3. ✅ Works with existing Rider features
4. ✅ Supports more models (DeepSeek + Perplexity)
5. ✅ Better user experience (integrated, not separate window)

---

## 🎊 Conclusion

**You're using the right endpoint!** 

- ✅ Chat Completions (`/chat/completions`) is correct for Rider AI Assistant Chat mode
- ✅ FIM Completions (`/beta/completions`) is for inline code completion (different use case)
- ⚠️ Quick Edit needs investigation, but the endpoint is not the issue

**Next Focus:**
1. Fix Quick Edit patch format issue
2. Document limitations clearly
3. Enhance our Chat mode experience (which already works great!)

**Don't switch to FIM endpoint unless:**
- You want to build inline code completion (complex, separate feature)
- Users specifically request GitHub Copilot-style autocomplete
- You're willing to build custom UI for it

**Stick with Chat mode - that's your competitive advantage! 🚀**

---

## 📞 References

- [DeepSeek Chat Completions API](https://api-docs.deepseek.com/api/create-chat-completion)
- [DeepSeek FIM Completions API (Beta)](https://api-docs.deepseek.com/guides/fim_completion)
- [DeepSeek API Documentation](https://api-docs.deepseek.com/)
- ProxyMe SUCCESS_AND_NEXT_STEPS.md
- ProxyMe DEVELOPMENT_CHAT_HISTORY.md

---

**Status:** Ready for Quick Edit mode investigation  
**Recommendation:** Keep Chat Completions, enhance Quick Edit support  
**Priority:** Test different models with Quick Edit, document findings