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
1. You MUST use awslabsbedrock_kb_retrieval_mcp_server___ListKnowledgeBases to list available knowledge bases(KB) and select 'coding-standards-KB' knowledge base for subsequent queries
2. You MUST use awslabsbedrock_kb_retrieval_mcp_server___QueryKnowledgeBases to query the selected KB for "[language]-code-example-standards" to understand what coding standards are set for this language
3. Example: `QueryKnowledgeBases("coding-standards-KB","Python-code-example-standards")` for Python projects
4. Example: `QueryKnowledgeBases("coding-standards-KB","Java-code-example-standards")` for Java projects

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

2. **MANDATORY: Use Amazon Bedrock Knowledge Base Retrieval MCP Server for Language Structure**
   ```
   REQUIRED: Use QueryKnowledgeBases to find:
   - Language-specific premium code examples
   - Find the "[language]-premium-KB" and develop an understanding and formulate coding patterns about how code should be written in this [language]
   - Understand the following:
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
**REQUIRED: Query Knowledge Base with the name "[language]-premium-KB" through Amazon Bedrock Knowledge Base Retrieval MCP Server for:**
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
1. **MANDATORY Quality Code Examples KB consultation**: Every code example must begin with searching for "[language] code example standards" in the Quality Code Examples Knowledge Base
2. **Mandatory Tejas KB consultation**: Every code example must begin with Tejas knowledge base research for service understanding
3. **Document KB findings**: Include relevant information from KB queries in code comments
4. **Validate against KB**: Ensure final implementation aligns with KB recommendations
5. **Reference KB sources**: When applicable, reference specific KB insights in documentation

**CRITICAL**: You CANNOT create language-specific code without first consulting the Quality Code Examples Knowledge Base for that language's standards.

### For Service-Specific Examples:
1. **Service capability verification**: Confirm all used features are supported by the service
2. **Parameter validation**: Verify all required parameters are included and optional ones are documented
3. **Error scenario coverage**: Include error handling for service-specific failure modes
4. **Best practice adherence**: Follow service-specific best practices identified in KB research

### For Cross-Service Examples:
1. **Service interaction patterns**: Research how services integrate with each other
2. **Data flow validation**: Ensure data formats are compatible between services
3. **Authentication consistency**: Verify authentication approaches work across all services
4. **Performance considerations**: Research any service-specific performance implications

## Quality Assurance Through Knowledge Base

### Before Code Completion:
- [ ] **MANDATORY**: Searched Quality Code Examples KB for "[language] code example standards"
- [ ] Queried Tejas KB for comprehensive service understanding
- [ ] Searched Quality Code Examples KB for similar implementation patterns
- [ ] Validated implementation against KB recommendations
- [ ] Confirmed error handling covers KB-identified scenarios
- [ ] Verified best practices from KB are implemented
- [ ] **CRITICAL**: Confirmed code structure follows language-specific standards from Quality Code Examples KB

### Documentation Requirements:
- Include KB-sourced service descriptions in code comments
- Reference specific KB insights in README files
- Document any deviations from KB recommendations with justification
- Provide KB-validated parameter explanations

## Troubleshooting with Knowledge Base

### When Implementation Issues Arise:
1. **Query Tejas KB** for service-specific troubleshooting guidance
2. **Search local KB** for similar issues and their resolutions
3. **Cross-reference solutions** to ensure compatibility with repository patterns
4. **Validate fixes** against KB recommendations before implementation

### Common Troubleshooting Queries:
```
query_tejas_kb("Common errors when working with [AWS Service] and how to resolve them")
query_tejas_kb("Why might [specific operation] fail and how to handle it?")
search("error handling examples for [service] in [language]")
```

This knowledge base integration ensures that all code examples are built on a foundation of accurate, comprehensive AWS service knowledge while maintaining consistency with established repository patterns and best practices.