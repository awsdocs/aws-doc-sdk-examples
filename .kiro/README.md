# CodeLoom-4-Bedrock

CodeLoom-4-Bedrock is configured to develop AWS SDK code examples across 12+ programming languages for AWS Documentation.

## How It Works

This setup uses two key components to ensure consistent, high-quality code:

### MCP Servers
- **Amazon Bedrock Knowledge Base**: Access to curated coding standards and premium implementation patterns
- **AWS Knowledge Server**: Direct access to AWS documentation and service information

### Steering Rules
Automated guidance that enforces:
- **Knowledge Base First**: Always consult knowledge bases before writing code
- **Language-Specific Patterns**: Each language has specific naming, structure, and testing requirements

## Quick Start

1. **Check MCP Status**: Ensure servers are running in Kiro MCP Server view
2. **Choose Language**: Each has specific patterns (see steering docs)
3. **Research Service**: The tool will automatically query knowledge bases and AWS docs
4. **Follow the Workflow**: KB consultation → Implementation → Testing → Documentation

## Key Requirements

- **Knowledge Base Consultation**: Mandatory before any code creation
- **All Tests Must Pass**: Zero failures before work is complete
- **Hello Scenarios First**: Simple examples before complex ones
- **Real AWS Integration**: Tests use actual AWS services with proper cleanup

## Configuration Files

- **MCP Setup**: `.kiro/settings/mcp.json` (requires `cex-ai-kb-access` AWS profile)
- **Detailed Rules**: `.kiro/steering/*.md` files contain comprehensive guidelines
- **Language Specifics**: See `tech.md`, `python-tech.md`, `java-tech.md`, etc.

## Need Help?

- Review steering files in `.kiro/steering/` for detailed guidance
- Check MCP server status if knowledge base queries fail
- All language-specific patterns are documented in the steering rules