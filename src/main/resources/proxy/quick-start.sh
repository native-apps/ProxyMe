#!/bin/bash

# Rider IDE AI Proxy - Quick Start Script
# This script helps you set up and test the proxy in minutes

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Helper functions
print_header() {
    echo ""
    echo -e "${BOLD}${CYAN}========================================${NC}"
    echo -e "${BOLD}${CYAN}$1${NC}"
    echo -e "${BOLD}${CYAN}========================================${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_step() {
    echo -e "${BOLD}$1${NC}"
}

# Check if Node.js is installed
check_node() {
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed"
        echo ""
        echo "Please install Node.js from: https://nodejs.org/"
        echo "Minimum required version: 16.0.0"
        exit 1
    fi

    NODE_VERSION=$(node -v)
    print_success "Node.js is installed: $NODE_VERSION"
}

# Check if npm is installed
check_npm() {
    if ! command -v npm &> /dev/null; then
        print_error "npm is not installed"
        exit 1
    fi

    NPM_VERSION=$(npm -v)
    print_success "npm is installed: $NPM_VERSION"
}

# Install dependencies
install_dependencies() {
    print_step "Installing dependencies..."

    if [ ! -d "node_modules" ]; then
        npm install
        print_success "Dependencies installed successfully"
    else
        print_info "Dependencies already installed"
        read -p "Re-install dependencies? (y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            npm install
            print_success "Dependencies re-installed"
        fi
    fi
}

# Setup environment file
setup_env() {
    print_step "Setting up environment configuration..."

    if [ -f ".env" ]; then
        print_info ".env file already exists"
        read -p "Do you want to reconfigure it? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            return
        fi
    fi

    # Copy template
    cp .env.template .env
    print_success "Created .env file from template"

    echo ""
    print_warning "You need to configure your API keys!"
    echo ""

    # DeepSeek API Key
    print_info "Get your DeepSeek API key from: https://platform.deepseek.com/api_keys"
    read -p "Enter your DeepSeek API key (or press Enter to skip): " DEEPSEEK_KEY

    if [ ! -z "$DEEPSEEK_KEY" ]; then
        sed -i.bak "s/DEEPSEEK_API_KEY=.*/DEEPSEEK_API_KEY=$DEEPSEEK_KEY/" .env
        rm .env.bak 2>/dev/null || true
        print_success "DeepSeek API key configured"
    else
        print_warning "DeepSeek API key not configured - you'll need to edit .env manually"
    fi

    echo ""

    # Perplexity API Key
    print_info "Get your Perplexity API key from: https://www.perplexity.ai/settings/api"
    read -p "Enter your Perplexity API key (or press Enter to skip): " PERPLEXITY_KEY

    if [ ! -z "$PERPLEXITY_KEY" ]; then
        sed -i.bak "s/PERPLEXITY_API_KEY=.*/PERPLEXITY_API_KEY=$PERPLEXITY_KEY/" .env
        rm .env.bak 2>/dev/null || true
        print_success "Perplexity API key configured"
    else
        print_warning "Perplexity API key not configured - you'll need to edit .env manually"
    fi

    echo ""

    # Port configuration
    read -p "Enter server port (default: 3000): " PORT
    if [ ! -z "$PORT" ]; then
        sed -i.bak "s/PORT=.*/PORT=$PORT/" .env
        rm .env.bak 2>/dev/null || true
        print_success "Port configured: $PORT"
    fi
}

# Run tests
run_tests() {
    print_step "Running setup tests..."
    echo ""
    print_warning "Make sure the proxy server is running in another terminal!"
    echo ""
    read -p "Is the proxy server running? (y/N) " -n 1 -r
    echo

    if [[ $REPLY =~ ^[Yy]$ ]]; then
        npm test
    else
        print_warning "Skipping tests - start the server first with: npm start"
    fi
}

# Show next steps
show_next_steps() {
    print_header "🎉 Setup Complete!"

    echo "Next steps:"
    echo ""
    print_step "1. Start the proxy server:"
    echo "   ${CYAN}npm start${NC}"
    echo ""
    print_step "2. In another terminal, run tests:"
    echo "   ${CYAN}npm test${NC}"
    echo ""
    print_step "3. Configure Rider IDE:"
    echo "   • Open Rider → Settings → Tools → AI Assistant → Models"
    echo "   • Provider: ${GREEN}OpenAI API${NC}"
    echo "   • Base URL: ${GREEN}http://localhost:3000/v1${NC}"
    echo "   • API Key: ${GREEN}(leave empty)${NC}"
    echo "   • Click 'Test Connection'"
    echo ""
    print_step "4. Select your preferred models:"
    echo "   • Core Features: deepseek-chat or sonar-pro"
    echo "   • Instant Helpers: sonar or deepseek-chat"
    echo "   • Completion: deepseek-reasoner"
    echo ""
    print_info "For help: Check README.md or run: curl http://localhost:3000/health"
    echo ""
}

# Main script
main() {
    clear
    print_header "🚀 Rider IDE AI Proxy - Quick Start"

    print_info "This script will help you set up the proxy in a few minutes"
    echo ""

    # Step 1: Check prerequisites
    print_header "Step 1: Checking Prerequisites"
    check_node
    check_npm

    # Step 2: Install dependencies
    echo ""
    print_header "Step 2: Installing Dependencies"
    install_dependencies

    # Step 3: Configure environment
    echo ""
    print_header "Step 3: Configure Environment"
    setup_env

    # Check if API keys are configured
    if grep -q "your.*key.*here" .env 2>/dev/null; then
        echo ""
        print_warning "API keys still need to be configured!"
        print_info "Edit .env file and add your actual API keys"
        read -p "Open .env file now? (y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            ${EDITOR:-nano} .env
        fi
    fi

    # Step 4: Show next steps
    echo ""
    show_next_steps

    # Optional: Start server now
    read -p "Start the proxy server now? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_info "Starting proxy server..."
        echo ""
        npm start
    fi
}

# Run main function
main
