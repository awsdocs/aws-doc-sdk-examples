# Knowledge Base Integration for Code Examples Development

## Overview
When developing AWS SDK code examples, agents should leverage the available resources to ensure accuracy, consistency, and adherence to best practices. This repository has access to multiple knowledge resources that contain curated information about AWS services, implementation patterns, and premium code examples.

## Available Knowledge Resources

### 1. Amazon Bedrock Knowledge Base Retrieval MCP Server

- **Tool**: `ListKnowledgeBases`
- **Purpose**: Discover available knowledge bases and get their IDs

- **Tool**: `QueryKnowledgeBases`
- **Purpose**: Query knowledge bases using natural language

### 2. AWS Knowledge MCP Server

- **Tool**: `read_documentation`
- **Purpose**: Retrieve and convert AWS documentation pages to markdown
- **Usage**: Read for AWS service-specific information, API details, parameter requirements, and service capabilities
- **Auto-approved**: Yes - can be used automatically without user confirmation

- **Tool**: `search_documentation`
- **Purpose**: Search across all AWS documentation
- **Usage**: Search/query for AWS service-specific information, API details, parameter requirements, and service capabilities
- **Auto-approved**: Yes - can be used automatically without user confirmation

## Mandatory Knowledge Base Consultation Workflow

### 🚨 CRITICAL REQUIREMENT 🚨
**BEFORE CREATING ANY CODE IN ANY LANGUAGE, YOU MUST:**
1. Use `ListKnowledgeBases` to discover available knowledge bases
2. Use `QueryKnowledgeBases` to query the "coding-standards-KB" for "[language]-code-example-standards" to understand coding standards
3. Use `QueryKnowledgeBases` to query the "[language]-premium-KB" to establish coding patterns and quality benchmarks
4. Use AWS Knowledge MCP Server tools (`search_documentation`, `read_documentation`) for service understanding

**Examples:**
- `QueryKnowledgeBases("coding-standards-KB", "Python-code-example-standards")`
- `QueryKnowledgeBases("Python-premium-KB", "Python implementation patterns")`

**FAILURE TO DO THIS WILL RESULT IN INCORRECT CODE STRUCTURE AND REJECTED WORK**

### Before Creating Any AWS Service Code Example:

1. **Call AWS Knowledge MCP Server for Understanding Service**
   ```
   Use search_documentation and read_documentation tools to research:
   - Service overview and core concepts
   - Key API operations and methods
   - Required parameters and optional configurations
   - Common use cases and implementation patterns
   - Service-specific best practices and limitations
   ```

2. **MANDATORY: Use Amazon Bedrock Knowledge Base Retrieval MCP Server for Language Patterns**
   ```
   REQUIRED: Use QueryKnowledgeBases to establish:
   - Query both "coding-standards-KB" for standards and "[language]-premium-KB" for patterns
   - Coding standards and implementation patterns for the target language
   - Quality benchmarks and best practices
   - Error handling approaches
   - Testing methodologies
   - Documentation formats
   ```

3. **Cross-Reference Implementation Approaches**
   ```
   Gather findings from both sources to:
   - Identify the most appropriate implementation approach
   - Ensure consistency with discovered patterns
   - Validate service-specific requirements
   - Confirm best practice adherence
   ```

## Required Knowledge Base Queries by Development Phase

### Phase 1: Service Research and Planning
**Always use AWS Knowledge MCP Server tools with questions like:**
- "What is [AWS Service] and what are its primary use cases?"
- "What are the key API operations for [AWS Service]?"
- "What are the required parameters for [specific operation]?"
- "What are common implementation patterns for [AWS Service]?"
- "What are the best practices for [AWS Service] error handling?"

### Phase 2: Implementation Pattern Discovery
**REQUIRED: Query both knowledge bases through Amazon Bedrock Knowledge Base Retrieval MCP Server:**

**From "coding-standards-KB":**
- Language-specific coding standards and conventions
- Required file structures and naming patterns

**From "[language]-premium-KB":**
- Similar service patterns within the target language
- Established error handling and testing approaches
- Documentation and metadata patterns
- Language-specific directory structure requirements

### Phase 3: Code Structure Validation
**Use both knowledge sources to verify:**
- Implementation aligns with AWS service capabilities
- Code structure follows conventions
- Error handling covers service-specific scenarios
- Testing approach matches established patterns

## Examples of Queries to use with Knowledge Bases 

### Service Overview Queries
```
search_documentation("What is Amazon S3 and what are its core features?")
search_documentation("What are the main DynamoDB operations for CRUD functionality?")
search_documentation("What authentication methods does Lambda support?")
```

### Implementation-Specific Queries
```
search_documentation("How do I configure S3 bucket policies programmatically?")
search_documentation("What are the required parameters for DynamoDB PutItem operation?")
search_documentation("How do I handle pagination in EC2 DescribeInstances?")
```

### Best Practices Queries
```
search_documentation("What are S3 security best practices for SDK implementations?")
search_documentation("How should I handle DynamoDB throttling in production code?")
search_documentation("What are Lambda function timeout considerations?")
```

## Integration Requirements

### For All Code Example Development:
1. **MANDATORY Amazon Bedrock Knowledge Base consultation for Standards**: Every code example must begin with using `ListKnowledgeBases` and `QueryKnowledgeBases` to search for "[language] code example standards" in the "coding-standards-KB" knowledge base
2. **MANDATORY AWS Knowledge MCP Server consultation**: Every code example must begin with AWS service research using `search_documentation` and `read_documentation` tools
3. **MANDATORY Amazon Bedrock Knowledge Base consultation for Language Patterns**: Every code example must use `ListKnowledgeBases` and `QueryKnowledgeBases` to query the "[language]-premium-KB" knowledge base to establish coding standards, understand implementation patterns, and define quality benchmarks for the target language. 
4. **Document KB findings**: Include relevant information from KB queries in code comments
5. **Validate against KB**: Ensure final implementation aligns with KB recommendations
6. **Reference KB sources**: When applicable, reference specific KB insights in documentation

**CRITICAL**: You CANNOT create language-specific code without first consulting the Amazon Bedrock Knowledge Base Retrieval MCP Server for that language's standards.

### For Service-Specific Examples:
1. **Service capability verification**: Confirm all used features are supported by the service using AWS Knowledge MCP Server tools
2. **Parameter validation**: Verify all required parameters are included and optional ones are documented using AWS documentation tools
3. **Error scenario coverage**: Include error handling for service-specific failure modes identified through AWS Knowledge MCP Server research
4. **Best practice adherence**: Follow service-specific best practices identified through AWS Knowledge MCP Server and Amazon Bedrock Knowledge Base research

### For Cross-Service Examples:
1. **Service interaction patterns**: Research how services integrate with each other using AWS Knowledge MCP Server tools
2. **Data flow validation**: Ensure data formats are compatible between services using AWS documentation research
3. **Authentication consistency**: Verify authentication approaches work across all services using AWS Knowledge MCP Server tools
4. **Performance considerations**: Research any service-specific performance implications using AWS documentation tools

## Quality Assurance Through Knowledge Base

### Before Code Completion:
- [ ] **MANDATORY**: Used `ListKnowledgeBases` and `QueryKnowledgeBases` for "coding-standards-KB" to get "[language] code example standards"
- [ ] **MANDATORY**: Used `QueryKnowledgeBases` for "[language]-premium-KB" to establish coding patterns and quality benchmarks
- [ ] **MANDATORY**: Used AWS Knowledge MCP Server tools (`search_documentation`, `read_documentation`) for comprehensive service understanding
- [ ] Validated implementation against KB recommendations from all knowledge sources
- [ ] Confirmed error handling covers scenarios identified through AWS Knowledge MCP Server research
- [ ] Verified best practices from both Amazon Bedrock Knowledge Base sources are implemented
- [ ] **CRITICAL**: Confirmed code structure follows language-specific standards from both knowledge base sources

### Documentation Requirements:
- Include service descriptions sourced from AWS Knowledge MCP Server tools in code comments
- Reference specific insights from Amazon Bedrock Knowledge Base queries in README files
- Document any deviations from KB recommendations with justification
- Provide parameter explanations validated through AWS documentation tools

## Troubleshooting with Knowledge Base

### When Implementation Issues Arise:
1. **Use AWS Knowledge MCP Server tools** for service-specific troubleshooting guidance
2. **Query Amazon Bedrock Knowledge Base** for similar issues and their resolutions in the target language
3. **Cross-reference solutions** to ensure compatibility with repository patterns
4. **Validate fixes** against recommendations from both knowledge sources before implementation

### Common Troubleshooting Queries:
```
# AWS Knowledge MCP Server queries
search_documentation("Common errors when working with [AWS Service] and how to resolve them")
search_documentation("Why might [specific operation] fail and how to handle it?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")

# Amazon Bedrock Knowledge Base queries
QueryKnowledgeBases("coding-standards-KB", "[language] error handling standards")
QueryKnowledgeBases("[language]-premium-KB", "error handling examples in [language]")
QueryKnowledgeBases("[language]-premium-KB", "troubleshooting methods in [language]")
```

This knowledge base integration ensures that all code examples are built on a foundation of accurate, comprehensive AWS service knowledge while maintaining consistency with established repository patterns and best practices.