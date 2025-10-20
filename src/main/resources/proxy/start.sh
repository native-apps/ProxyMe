#!/bin/bash

# Rider IDE AI Proxy - Quick Startup Script
# This script sets up and starts the proxy server

set -e

echo "🚀 Rider IDE AI Proxy - Quick Setup"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js first."
    echo "   Download from: https://nodejs.org/"
    exit 1
fi

# Check if we're in the right directory
if [ ! -f "package.json" ]; then
    echo "❌ Please run this script from the 'Node.js Proxy Cloud AI APIs' directory"
    exit 1
fi

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "📝 Creating .env file from template..."
    cp .env.template .env
    echo ""
    echo "⚠️  IMPORTANT: Please edit the .env file and add your API keys:"
    echo "   - DEEPSEEK_API_KEY: Get from https://platform.deepseek.com/"
    echo "   - PERPLEXITY_API_KEY: Get from https://www.perplexity.ai/settings/api"
    echo ""
    echo "💡 After adding your API keys, run this script again."
    exit 0
fi

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Check if API keys are set
source .env
if [ -z "$DEEPSEEK_API_KEY" ] || [ "$DEEPSEEK_API_KEY" = "your_deepseek_api_key_here" ]; then
    echo "❌ DEEPSEEK_API_KEY is not set in .env file"
    echo "   Please edit .env and add your actual DeepSeek API key"
    exit 1
fi

if [ -z "$PERPLEXITY_API_KEY" ] || [ "$PERPLEXITY_API_KEY" = "your_perplexity_api_key_here" ]; then
    echo "❌ PERPLEXITY_API_KEY is not set in .env file"
    echo "   Please edit .env and add your actual Perplexity API key"
    exit 1
fi

echo "✅ API keys configured"
echo "🚀 Starting Rider IDE AI Proxy..."

# Start the proxy server
npm start
