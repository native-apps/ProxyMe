const express = require("express");
const axios = require("axios");
require("dotenv").config();

const app = express();
const PORT = process.env.PORT || 3000;

// Enable detailed logging
const DEBUG = process.env.DEBUG === "true" || true;

// Middleware
app.use(express.json());

// Request logging middleware
app.use((req, res, next) => {
  if (DEBUG) {
    console.log(`\n📥 ${new Date().toISOString()} - ${req.method} ${req.path}`);
    if (req.body && Object.keys(req.body).length > 0) {
      console.log("📦 Request Body:", JSON.stringify(req.body, null, 2));
    }
  }
  next();
});

// API Keys from environment variables
const API_KEYS = {
  deepseek: process.env.DEEPSEEK_API_KEY,
  perplexity: process.env.PERPLEXITY_API_KEY,
};

// Model configurations
const MODEL_CONFIGS = {
  // DeepSeek models
  "deepseek-chat": {
    provider: "deepseek",
    url: "https://api.deepseek.com/chat/completions",
    displayName: "DeepSeek Chat",
    description: "General purpose chat model (DeepSeek-V3)",
  },
  "deepseek-reasoner": {
    provider: "deepseek",
    url: "https://api.deepseek.com/chat/completions",
    displayName: "DeepSeek Reasoner",
    description: "Advanced reasoning model with Chain of Thought",
  },

  // Perplexity Sonar models
  sonar: {
    provider: "perplexity",
    url: "https://api.perplexity.ai/chat/completions",
    displayName: "Sonar",
    description: "Lightweight, cost-effective search model with grounding",
  },
  "sonar-pro": {
    provider: "perplexity",
    url: "https://api.perplexity.ai/chat/completions",
    displayName: "Sonar Pro",
    description: "Advanced search offering with complex query support",
  },
  "sonar-reasoning": {
    provider: "perplexity",
    url: "https://api.perplexity.ai/chat/completions",
    displayName: "Sonar Reasoning",
    description: "Fast, real-time reasoning with search capabilities",
  },
  "sonar-reasoning-pro": {
    provider: "perplexity",
    url: "https://api.perplexity.ai/chat/completions",
    displayName: "Sonar Reasoning Pro",
    description: "Precise reasoning powered by DeepSeek-R1 with CoT",
  },
  "sonar-deep-research": {
    provider: "perplexity",
    url: "https://api.perplexity.ai/chat/completions",
    displayName: "Sonar Deep Research",
    description: "Expert-level research conducting exhaustive searches",
  },
};

// Available models list for OpenAI-compatible format
const AVAILABLE_MODELS = Object.keys(MODEL_CONFIGS).map((modelId) => ({
  id: modelId,
  object: "model",
  created: Math.floor(Date.now() / 1000),
  owned_by: MODEL_CONFIGS[modelId].provider,
  permission: [],
  root: modelId,
  parent: null,
}));

// Health check endpoint
app.get("/health", (req, res) => {
  const apiKeysStatus = Object.keys(API_KEYS).map((provider) => ({
    provider,
    configured: !!API_KEYS[provider],
  }));

  const response = {
    status: "healthy",
    service: "rider-ai-proxy",
    version: "1.1.0",
    timestamp: new Date().toISOString(),
    available_models: Object.keys(MODEL_CONFIGS),
    api_keys: apiKeysStatus,
    debug_mode: DEBUG,
  };

  console.log("✅ Health check successful");
  res.json(response);
});

// Models endpoint (OpenAI compatible)
app.get("/v1/models", (req, res) => {
  console.log("📋 Models list requested");
  res.json({
    object: "list",
    data: AVAILABLE_MODELS,
  });
});

// Chat completions endpoint (OpenAI compatible)
app.post("/v1/chat/completions", async (req, res) => {
  const requestId = Math.random().toString(36).substring(7);

  try {
    const { model, messages, stream = false, ...otherParams } = req.body;

    console.log(`\n🔵 [${requestId}] Received chat completion request`);
    console.log(`   Model: ${model}`);
    console.log(`   Messages count: ${messages?.length || 0}`);
    console.log(`   Stream: ${stream}`);
    console.log(
      `   Other params: ${Object.keys(otherParams).join(", ") || "none"}`,
    );

    // Fix message format: merge consecutive user messages for Perplexity
    let fixedMessages = messages;
    if (model?.includes("sonar")) {
      fixedMessages = [];
      for (let i = 0; i < messages.length; i++) {
        const currentMsg = messages[i];

        if (currentMsg.role === "user" && fixedMessages.length > 0) {
          const lastMsg = fixedMessages[fixedMessages.length - 1];

          // If last message was also user, merge them
          if (lastMsg.role === "user") {
            lastMsg.content = lastMsg.content + "\n\n" + currentMsg.content;
            continue;
          }
        }

        fixedMessages.push({ ...currentMsg });
      }

      if (fixedMessages.length !== messages.length) {
        console.log(
          `   ⚠️  Merged ${messages.length - fixedMessages.length} consecutive user messages`,
        );
      }
    }

    // Validate required parameters
    if (!model) {
      console.error(`❌ [${requestId}] Missing model parameter`);
      return res.status(400).json({
        error: {
          message: "Model parameter is required",
          type: "invalid_request_error",
          code: "missing_model",
        },
      });
    }

    if (!messages || !Array.isArray(messages) || messages.length === 0) {
      console.error(`❌ [${requestId}] Invalid messages parameter`);
      return res.status(400).json({
        error: {
          message:
            "Messages parameter is required and must be a non-empty array",
          type: "invalid_request_error",
          code: "missing_messages",
        },
      });
    }

    // Get model configuration
    const config = MODEL_CONFIGS[model];
    if (!config) {
      const availableModels = Object.keys(MODEL_CONFIGS).join(", ");
      console.error(`❌ [${requestId}] Model '${model}' not supported`);
      console.log(`   Available models: ${availableModels}`);
      return res.status(400).json({
        error: {
          message: `Model '${model}' is not supported. Available models: ${availableModels}`,
          type: "invalid_request_error",
          code: "model_not_found",
        },
      });
    }

    // Get API key for the provider
    const apiKey = API_KEYS[config.provider];
    if (!apiKey) {
      console.error(
        `❌ [${requestId}] No API key configured for ${config.provider}`,
      );
      return res.status(500).json({
        error: {
          message: `API key not configured for provider: ${config.provider}. Please check your .env file.`,
          type: "server_error",
          code: "missing_api_key",
        },
      });
    }

    console.log(`🔄 [${requestId}] Routing to ${config.provider} API`);
    console.log(`   Provider: ${config.provider}`);
    console.log(`   URL: ${config.url}`);
    console.log(`   API Key: ${apiKey.substring(0, 10)}...`);

    // Prepare request for the target API
    const requestBody = {
      model: model,
      messages: fixedMessages,
      stream: stream,
      ...otherParams,
    };

    if (DEBUG) {
      console.log(
        `📤 [${requestId}] Sending to provider:`,
        JSON.stringify(requestBody, null, 2),
      );
    }

    // Make request to the actual API
    const startTime = Date.now();

    // Handle streaming vs non-streaming
    if (stream) {
      console.log(`   📡 Streaming enabled`);

      // Set headers for SSE streaming
      res.setHeader("Content-Type", "text/event-stream");
      res.setHeader("Cache-Control", "no-cache");
      res.setHeader("Connection", "keep-alive");

      try {
        const response = await axios.post(config.url, requestBody, {
          headers: {
            Authorization: `Bearer ${apiKey}`,
            "Content-Type": "application/json",
            "User-Agent": "Rider-AI-Proxy/1.1",
          },
          timeout: 120000,
          responseType: "stream",
          validateStatus: (status) => status < 600,
        });

        if (response.status >= 400) {
          console.error(
            `❌ [${requestId}] Provider returned error ${response.status}`,
          );
          res.write(`data: ${JSON.stringify({ error: response.data })}\n\n`);
          res.end();
          return;
        }

        // Pipe the streaming response
        response.data.on("data", (chunk) => {
          res.write(chunk);
        });

        response.data.on("end", () => {
          const duration = Date.now() - startTime;
          console.log(`✅ [${requestId}] Stream completed in ${duration}ms`);
          res.end();
        });

        response.data.on("error", (error) => {
          console.error(`❌ [${requestId}] Stream error:`, error.message);
          res.end();
        });
      } catch (error) {
        console.error(`❌ [${requestId}] Streaming error:`, error.message);
        res.write(
          `data: ${JSON.stringify({ error: { message: error.message } })}\n\n`,
        );
        res.end();
      }
    } else {
      // Non-streaming request
      const response = await axios.post(config.url, requestBody, {
        headers: {
          Authorization: `Bearer ${apiKey}`,
          "Content-Type": "application/json",
          "User-Agent": "Rider-AI-Proxy/1.1",
        },
        timeout: 120000, // 2 minute timeout for deep research models
        validateStatus: (status) => status < 600, // Don't throw on any status
      });

      const duration = Date.now() - startTime;

      if (response.status >= 400) {
        console.error(
          `❌ [${requestId}] Provider returned error ${response.status}`,
        );
        console.error(`   Error data:`, JSON.stringify(response.data, null, 2));
        return res.status(response.status).json(response.data);
      }

      console.log(`✅ [${requestId}] Success! Response in ${duration}ms`);
      if (DEBUG && response.data.choices?.[0]?.message) {
        const content = response.data.choices[0].message.content;
        const preview = content?.substring(0, 100) || "(no content)";
        console.log(`   Response preview: ${preview}...`);
        console.log(
          `   Tokens used: prompt=${response.data.usage?.prompt_tokens}, completion=${response.data.usage?.completion_tokens}`,
        );
      }

      // Return the response as-is (already in OpenAI format)
      res.json(response.data);
    }
  } catch (error) {
    console.error(`\n❌ [${requestId}] Proxy error occurred:`);
    console.error(`   Error type: ${error.constructor.name}`);
    console.error(`   Error message: ${error.message}`);

    // Handle different types of errors
    if (error.response) {
      // Forward API provider errors
      console.error(`   HTTP Status: ${error.response.status}`);
      console.error(
        `   Response data:`,
        JSON.stringify(error.response.data, null, 2),
      );
      res.status(error.response.status).json(error.response.data);
    } else if (error.code === "ECONNREFUSED") {
      console.error(`   Connection refused to AI provider`);
      res.status(503).json({
        error: {
          message:
            "Unable to connect to AI provider API. Please check your internet connection.",
          type: "server_error",
          code: "provider_unavailable",
        },
      });
    } else if (error.code === "ETIMEDOUT" || error.code === "ECONNABORTED") {
      console.error(`   Request timeout`);
      res.status(504).json({
        error: {
          message:
            "Request timeout to AI provider. The request took too long to complete.",
          type: "server_error",
          code: "timeout",
        },
      });
    } else if (error.code === "ENOTFOUND") {
      console.error(`   DNS resolution failed`);
      res.status(503).json({
        error: {
          message:
            "Unable to resolve AI provider hostname. Please check your internet connection.",
          type: "server_error",
          code: "dns_error",
        },
      });
    } else {
      console.error(`   Stack trace:`, error.stack);
      res.status(500).json({
        error: {
          message:
            "Internal server error in proxy. Check proxy logs for details.",
          type: "server_error",
          code: "internal_error",
          details: DEBUG ? error.message : undefined,
        },
      });
    }
  }
});

// Root endpoint with info
app.get("/", (req, res) => {
  res.json({
    service: "Rider IDE AI Proxy",
    version: "1.1.0",
    endpoints: {
      health: "/health",
      models: "/v1/models",
      chat: "/v1/chat/completions",
    },
    available_models: Object.keys(MODEL_CONFIGS).map((id) => ({
      id,
      provider: MODEL_CONFIGS[id].provider,
      display_name: MODEL_CONFIGS[id].displayName,
      description: MODEL_CONFIGS[id].description,
    })),
    rider_configuration: {
      provider: "OpenAI API",
      url: `http://localhost:${PORT}/v1`,
      api_key: "(leave empty)",
    },
  });
});

// Test endpoint for Rider IDE compatibility
app.post("/v1/chat/completions/test", async (req, res) => {
  console.log("\n🧪 Test endpoint called");
  res.json({
    id: "test-" + Date.now(),
    object: "chat.completion",
    created: Math.floor(Date.now() / 1000),
    model: "test-model",
    choices: [
      {
        index: 0,
        message: {
          role: "assistant",
          content:
            "Proxy is working correctly! You can now use this with Rider IDE.",
        },
        finish_reason: "stop",
      },
    ],
    usage: {
      prompt_tokens: 10,
      completion_tokens: 15,
      total_tokens: 25,
    },
  });
});

// 404 handler
app.use((req, res) => {
  console.log(`⚠️  404 - Path not found: ${req.method} ${req.path}`);
  res.status(404).json({
    error: {
      message: `Endpoint not found: ${req.method} ${req.path}`,
      type: "invalid_request_error",
      code: "endpoint_not_found",
      available_endpoints: ["/health", "/v1/models", "/v1/chat/completions"],
    },
  });
});

// Start server
const server = app.listen(PORT, () => {
  console.log("\n" + "=".repeat(60));
  console.log("🚀 Rider IDE AI Proxy Server Started");
  console.log("=".repeat(60));
  console.log(`📍 Server running on http://localhost:${PORT}`);
  console.log(`🔗 Configure Rider IDE with: http://localhost:${PORT}/v1`);
  console.log(`🐛 Debug mode: ${DEBUG ? "ENABLED" : "DISABLED"}`);
  console.log("\n📋 Available Models:");
  Object.keys(MODEL_CONFIGS).forEach((model) => {
    const config = MODEL_CONFIGS[model];
    console.log(`   ✓ ${model.padEnd(25)} (${config.provider})`);
    console.log(`     ${config.description}`);
  });
  console.log("\n🔑 API Keys Status:");
  Object.keys(API_KEYS).forEach((provider) => {
    const status = API_KEYS[provider] ? "✅ CONFIGURED" : "❌ MISSING";
    console.log(`   ${provider.padEnd(15)} ${status}`);
  });
  console.log("\n🔧 Endpoints:");
  console.log(`   🏥 Health check: http://localhost:${PORT}/health`);
  console.log(`   📖 API info: http://localhost:${PORT}/`);
  console.log(`   📋 Models list: http://localhost:${PORT}/v1/models`);
  console.log(`   💬 Chat: http://localhost:${PORT}/v1/chat/completions`);
  console.log("\n📝 Rider IDE Configuration:");
  console.log(`   Provider: OpenAI API`);
  console.log(`   URL: http://localhost:${PORT}/v1`);
  console.log(`   API Key: (leave empty)`);
  console.log("=".repeat(60) + "\n");
});

// Graceful shutdown
process.on("SIGINT", () => {
  console.log("\n\n🛑 Shutting down Rider IDE AI Proxy gracefully...");
  server.close(() => {
    console.log("✅ Server closed");
    process.exit(0);
  });

  // Force shutdown after 5 seconds
  setTimeout(() => {
    console.log("⚠️  Forcing shutdown...");
    process.exit(1);
  }, 5000);
});

process.on("SIGTERM", () => {
  console.log("\n🛑 SIGTERM received, shutting down...");
  server.close(() => {
    console.log("✅ Server closed");
    process.exit(0);
  });
});

// Handle uncaught exceptions
process.on("uncaughtException", (error) => {
  console.error("\n💥 Uncaught Exception:");
  console.error(error);
  process.exit(1);
});

process.on("unhandledRejection", (reason, promise) => {
  console.error("\n💥 Unhandled Rejection at:", promise);
  console.error("Reason:", reason);
});
