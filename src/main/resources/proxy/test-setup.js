const axios = require("axios");

const baseUrl = process.env.BASE_URL || "http://localhost:3000";
const colors = {
  reset: "\x1b[0m",
  bright: "\x1b[1m",
  red: "\x1b[31m",
  green: "\x1b[32m",
  yellow: "\x1b[33m",
  blue: "\x1b[34m",
  cyan: "\x1b[36m",
};

function log(message, color = colors.reset) {
  console.log(`${color}${message}${colors.reset}`);
}

function logSection(title) {
  console.log("\n" + "=".repeat(60));
  log(title, colors.bright + colors.cyan);
  console.log("=".repeat(60));
}

function logTest(testName) {
  log(`\n${testName}`, colors.bright);
}

function logSuccess(message) {
  log(`✅ ${message}`, colors.green);
}

function logError(message) {
  log(`❌ ${message}`, colors.red);
}

function logWarning(message) {
  log(`⚠️  ${message}`, colors.yellow);
}

function logInfo(message) {
  log(`ℹ️  ${message}`, colors.blue);
}

async function testSetup() {
  logSection("🧪 Testing Rider IDE AI Proxy Setup");
  logInfo(`Base URL: ${baseUrl}\n`);

  let passedTests = 0;
  let failedTests = 0;
  const startTime = Date.now();

  try {
    // Test 1: Health Check
    logTest("Test 1: Health Check Endpoint");
    try {
      const healthResponse = await axios.get(`${baseUrl}/health`, {
        timeout: 5000,
      });

      if (healthResponse.status === 200) {
        logSuccess("Health check endpoint responding");
        logInfo(`   Service: ${healthResponse.data.service}`);
        logInfo(`   Version: ${healthResponse.data.version}`);
        logInfo(`   Status: ${healthResponse.data.status}`);
        logInfo(
          `   Available Models: ${healthResponse.data.available_models.length}`,
        );

        // Check API keys
        if (healthResponse.data.api_keys) {
          healthResponse.data.api_keys.forEach((key) => {
            const status = key.configured ? "✅" : "❌";
            logInfo(`   ${status} ${key.provider} API key`);
          });
        }

        passedTests++;
      }
    } catch (error) {
      logError("Health check failed");
      if (error.code === "ECONNREFUSED") {
        logError("   Connection refused - Is the proxy running?");
        logWarning("   Run: npm start");
      } else {
        logError(`   ${error.message}`);
      }
      failedTests++;
    }

    // Test 2: Models Endpoint
    logTest("Test 2: Models List Endpoint");
    try {
      const modelsResponse = await axios.get(`${baseUrl}/v1/models`, {
        timeout: 5000,
      });

      if (
        modelsResponse.status === 200 &&
        modelsResponse.data.object === "list"
      ) {
        logSuccess("Models endpoint responding correctly");
        logInfo(`   Total Models: ${modelsResponse.data.data.length}`);

        // Group by provider
        const byProvider = {};
        modelsResponse.data.data.forEach((model) => {
          if (!byProvider[model.owned_by]) {
            byProvider[model.owned_by] = [];
          }
          byProvider[model.owned_by].push(model.id);
        });

        Object.keys(byProvider).forEach((provider) => {
          logInfo(`   ${provider}:`);
          byProvider[provider].forEach((modelId) => {
            logInfo(`      - ${modelId}`);
          });
        });

        passedTests++;
      }
    } catch (error) {
      logError("Models endpoint failed");
      logError(`   ${error.message}`);
      failedTests++;
    }

    // Test 3: Root Info Endpoint
    logTest("Test 3: Root Info Endpoint");
    try {
      const rootResponse = await axios.get(`${baseUrl}/`, {
        timeout: 5000,
      });

      if (rootResponse.status === 200) {
        logSuccess("Root info endpoint responding");
        logInfo(`   Service: ${rootResponse.data.service}`);
        logInfo(`   Version: ${rootResponse.data.version}`);

        if (rootResponse.data.rider_configuration) {
          logInfo("   Rider IDE Configuration:");
          logInfo(
            `      Provider: ${rootResponse.data.rider_configuration.provider}`,
          );
          logInfo(`      URL: ${rootResponse.data.rider_configuration.url}`);
          logInfo(
            `      API Key: ${rootResponse.data.rider_configuration.api_key}`,
          );
        }

        passedTests++;
      }
    } catch (error) {
      logError("Root info endpoint failed");
      logError(`   ${error.message}`);
      failedTests++;
    }

    // Test 4: Test DeepSeek Chat
    logTest("Test 4: DeepSeek Chat Completion");
    try {
      const deepseekStart = Date.now();
      const deepseekResponse = await axios.post(
        `${baseUrl}/v1/chat/completions`,
        {
          model: "deepseek-chat",
          messages: [
            {
              role: "user",
              content: 'Say "DeepSeek is working!" and nothing else.',
            },
          ],
          max_tokens: 20,
          temperature: 0,
        },
        { timeout: 30000 },
      );
      const deepseekDuration = Date.now() - deepseekStart;

      if (
        deepseekResponse.status === 200 &&
        deepseekResponse.data.choices?.[0]?.message
      ) {
        logSuccess(`DeepSeek chat completed in ${deepseekDuration}ms`);
        const content = deepseekResponse.data.choices[0].message.content;
        logInfo(`   Response: "${content}"`);

        if (deepseekResponse.data.usage) {
          logInfo(
            `   Tokens: prompt=${deepseekResponse.data.usage.prompt_tokens}, completion=${deepseekResponse.data.usage.completion_tokens}`,
          );
        }

        passedTests++;
      }
    } catch (error) {
      logError("DeepSeek chat failed");
      if (error.response) {
        logError(
          `   HTTP ${error.response.status}: ${error.response.statusText}`,
        );
        if (error.response.data?.error) {
          logError(
            `   Error: ${error.response.data.error.message || JSON.stringify(error.response.data.error)}`,
          );
        }
      } else {
        logError(`   ${error.message}`);
      }
      logWarning("   Check if DEEPSEEK_API_KEY is set correctly in .env");
      failedTests++;
    }

    // Test 5: Test Perplexity Sonar
    logTest("Test 5: Perplexity Sonar Completion");
    try {
      const sonarStart = Date.now();
      const sonarResponse = await axios.post(
        `${baseUrl}/v1/chat/completions`,
        {
          model: "sonar",
          messages: [
            {
              role: "user",
              content: 'Say "Sonar is working!" and nothing else.',
            },
          ],
          max_tokens: 20,
          temperature: 0,
        },
        { timeout: 30000 },
      );
      const sonarDuration = Date.now() - sonarStart;

      if (
        sonarResponse.status === 200 &&
        sonarResponse.data.choices?.[0]?.message
      ) {
        logSuccess(`Perplexity Sonar completed in ${sonarDuration}ms`);
        const content = sonarResponse.data.choices[0].message.content;
        logInfo(`   Response: "${content}"`);

        if (sonarResponse.data.usage) {
          logInfo(
            `   Tokens: prompt=${sonarResponse.data.usage.prompt_tokens}, completion=${sonarResponse.data.usage.completion_tokens}`,
          );
        }

        passedTests++;
      }
    } catch (error) {
      logError("Perplexity Sonar failed");
      if (error.response) {
        logError(
          `   HTTP ${error.response.status}: ${error.response.statusText}`,
        );
        if (error.response.data?.error) {
          logError(
            `   Error: ${error.response.data.error.message || JSON.stringify(error.response.data.error)}`,
          );
        }
      } else {
        logError(`   ${error.message}`);
      }
      logWarning("   Check if PERPLEXITY_API_KEY is set correctly in .env");
      failedTests++;
    }

    // Test 6: Invalid Model Error Handling
    logTest("Test 6: Invalid Model Error Handling");
    try {
      await axios.post(
        `${baseUrl}/v1/chat/completions`,
        {
          model: "non-existent-model",
          messages: [{ role: "user", content: "test" }],
        },
        { timeout: 5000 },
      );
      logError("Should have returned 400 error for invalid model");
      failedTests++;
    } catch (error) {
      if (error.response && error.response.status === 400) {
        logSuccess("Invalid model correctly rejected with 400 error");
        logInfo(`   Error message: ${error.response.data.error?.message}`);
        passedTests++;
      } else {
        logError("Unexpected error response");
        failedTests++;
      }
    }

    // Test 7: Missing Messages Error Handling
    logTest("Test 7: Missing Messages Error Handling");
    try {
      await axios.post(
        `${baseUrl}/v1/chat/completions`,
        {
          model: "deepseek-chat",
          messages: [],
        },
        { timeout: 5000 },
      );
      logError("Should have returned 400 error for empty messages");
      failedTests++;
    } catch (error) {
      if (error.response && error.response.status === 400) {
        logSuccess("Empty messages correctly rejected with 400 error");
        logInfo(`   Error message: ${error.response.data.error?.message}`);
        passedTests++;
      } else {
        logError("Unexpected error response");
        failedTests++;
      }
    }

    // Test 8: Test DeepSeek Reasoner
    logTest("Test 8: DeepSeek Reasoner (Optional)");
    try {
      const reasonerStart = Date.now();
      const reasonerResponse = await axios.post(
        `${baseUrl}/v1/chat/completions`,
        {
          model: "deepseek-reasoner",
          messages: [
            {
              role: "user",
              content: "Calculate 15 + 27 and show your thinking.",
            },
          ],
          max_tokens: 100,
        },
        { timeout: 30000 },
      );
      const reasonerDuration = Date.now() - reasonerStart;

      if (
        reasonerResponse.status === 200 &&
        reasonerResponse.data.choices?.[0]?.message
      ) {
        logSuccess(`DeepSeek Reasoner completed in ${reasonerDuration}ms`);
        const content = reasonerResponse.data.choices[0].message.content;
        const preview = content.substring(0, 100);
        logInfo(`   Response preview: "${preview}..."`);

        if (reasonerResponse.data.usage) {
          logInfo(
            `   Tokens: prompt=${reasonerResponse.data.usage.prompt_tokens}, completion=${reasonerResponse.data.usage.completion_tokens}`,
          );
        }

        passedTests++;
      }
    } catch (error) {
      logWarning("DeepSeek Reasoner test skipped (optional)");
      logInfo(`   ${error.response?.data?.error?.message || error.message}`);
      // Don't count as failed
    }

    // Summary
    const totalTime = Date.now() - startTime;
    logSection("📊 Test Summary");

    const totalTests = passedTests + failedTests;
    const passRate =
      totalTests > 0 ? ((passedTests / totalTests) * 100).toFixed(1) : 0;

    logInfo(`Total Tests: ${totalTests}`);
    logSuccess(`Passed: ${passedTests}`);

    if (failedTests > 0) {
      logError(`Failed: ${failedTests}`);
    } else {
      logInfo(`Failed: ${failedTests}`);
    }

    logInfo(`Pass Rate: ${passRate}%`);
    logInfo(`Total Time: ${totalTime}ms`);

    if (failedTests === 0) {
      logSection("🎉 ALL TESTS PASSED!");
      console.log("\n📋 Rider IDE Configuration:");
      logSuccess("   Provider: OpenAI API");
      logSuccess(`   URL: ${baseUrl}/v1`);
      logSuccess("   API Key: (leave empty)");
      console.log("\n🚀 Your proxy is ready to use with Rider IDE!");
      console.log("\nNext steps:");
      logInfo("   1. Open Rider → Settings → Tools → AI Assistant → Models");
      logInfo('   2. Set Provider to "OpenAI API"');
      logInfo(`   3. Set Base URL to "${baseUrl}/v1"`);
      logInfo("   4. Leave API Key empty");
      logInfo('   5. Click "Test Connection"');
      logInfo("   6. Select your preferred models");
      console.log("");
      process.exit(0);
    } else {
      logSection("🔧 TESTS FAILED - Troubleshooting");
      console.log("\nCommon Issues:");
      logWarning("   1. Proxy not running → Run: npm start");
      logWarning("   2. API keys missing → Check .env file");
      logWarning("   3. Invalid API keys → Verify keys at provider websites");
      logWarning("   4. Network issues → Check internet connection");
      logWarning("   5. Port conflict → Change PORT in .env");
      console.log("\nFor more help:");
      logInfo("   • Check README.md troubleshooting section");
      logInfo("   • Review proxy console logs");
      logInfo(`   • Test health endpoint: curl ${baseUrl}/health`);
      console.log("");
      process.exit(1);
    }
  } catch (error) {
    logError("\n💥 CRITICAL ERROR");
    logError(error.message);

    if (error.code === "ECONNREFUSED") {
      console.log("\n🔧 Troubleshooting:");
      logWarning("   The proxy server is not running!");
      console.log("\nTo fix:");
      logInfo("   1. Open a terminal");
      logInfo("   2. Navigate to the proxy directory");
      logInfo("   3. Run: npm start");
      logInfo('   4. Wait for "Server running" message');
      logInfo("   5. Run tests again: node test-setup.js");
    }

    console.log("");
    process.exit(1);
  }
}

// Run the test
console.log("");
testSetup();
