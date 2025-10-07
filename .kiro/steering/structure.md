# Project Organization & Structure

## Top-Level Directory Structure

### Language-Specific Directories
Each programming language has its own top-level directory:
- `python/` - Python 3 examples (Boto3)
- `javav2/` - Java SDK v2 examples
- `dotnetv3/` - .NET SDK 3.5+ examples
- `javascriptv3/` - JavaScript SDK v3 examples
- `gov2/` - Go SDK v2 examples
- `kotlin/` - Kotlin examples
- `php/` - PHP examples
- `ruby/` - Ruby examples
- `rustv1/` - Rust SDK v1 examples
- `swift/` - Swift examples (preview)
- `cpp/` - C++ examples
- `sap-abap/` - SAP ABAP examples

### Legacy Directories
- `java/`, `javascript/`, `go/`, `.dotnet/`, `dotnetv3` - Legacy SDK versions (maintenance mode)

### Shared Resources
- `applications/` - Cross-service example applications
- `resources/` - Shared components (CDK, CloudFormation, sample files)
- `scenarios/` - Multi-service workflow examples
- `aws-cfn/` - CloudFormation templates
- `aws-cli/` - CLI examples

### Tooling & Infrastructure
- `.tools/` - Build tools, validation scripts, README generators
- `.doc_gen/` - Documentation generation metadata and cross-content
- `.github/` - GitHub Actions workflows and linters
- `.kiro/` - Kiro steering rules and configuration

## Within Each Language Directory

### Standard Structure Pattern
```
{language}/
├── README.md                    # Language-specific setup instructions
├── Dockerfile                   # Container image definition
├── example_code/               # Service-specific examples
│   ├── {service}/              # AWS service (e.g., s3, dynamodb)
│   │   ├── README.md           # Service examples documentation
│   │   ├── requirements.txt    # Dependencies (Python)
│   │   ├── pom.xml            # Maven config (Java)
│   │   ├── *.{ext}            # Example code files
│   │   └── test/              # Unit and integration tests
│   └── ...
├── cross_service/             # Multi-service applications
├── usecases/                  # Complete use case tutorials (Java)
└── test_tools/               # Testing utilities (Python)
```

## Code Organization Conventions

### File Naming
- Use descriptive names that indicate the AWS service and operation
- Follow language-specific naming conventions (snake_case for Python, PascalCase for C#)
- Include service prefix: `s3_list_buckets.py`, `DynamoDBCreateTable.java`

### Code Structure
- **Single-service actions**: Individual service operations in separate files
- **Scenarios**: Multi-step workflows within a single service
- **Cross-service examples**: Applications spanning multiple AWS services

### Testing Structure
- Unit tests use mocked/stubbed responses (no AWS charges)
- Integration tests make real AWS API calls (may incur charges)
- Test files located in `test/` subdirectories
- Use pytest markers: `@pytest.mark.integ` for integration tests

## Documentation Integration

### Metadata Files
- `.doc_gen/metadata/{service}_metadata.yaml` - Service-specific documentation metadata
- Defines code snippets, descriptions, and cross-references
- Used for AWS Documentation generation

### Snippet Tags
- Code examples include snippet tags for documentation extraction
- Format: `# snippet-start:[tag_name]` and `# snippet-end:[tag_name]`
- Tags referenced in metadata files for automated documentation

### Cross-Content
- `.doc_gen/cross-content/` - Shared content blocks across services
- XML files defining reusable documentation components

## Research and Discovery Best Practices

### Service Implementation Research
Before implementing examples for any AWS service, follow this mandatory sequence:
1. **AWS Knowledge MCP Server consultation**: Use `search_documentation` and `read_documentation` to understand service fundamentals, API operations, parameters, use cases, and best practices
2. **Amazon Bedrock Knowledge Base search**: Use `ListKnowledgeBases` and `QueryKnowledgeBases` to find existing service implementations and established patterns within this repository
3. **Cross-language analysis**: Examine how the service is implemented across different language directories (python/, javav2/, dotnetv3/, etc.)
4. **Pattern identification**: Look for consistent patterns across languages to understand service-specific requirements and conventions

**CRITICAL REQUIREMENT**: Every AWS service implementation must begin with comprehensive AWS Knowledge MCP Server research and Amazon Bedrock Knowledge Base consultation.

### Language Structure Guidelines
When working within a specific language directory:
1. **AWS Knowledge MCP Server for service context**: Use `search_documentation` and `read_documentation` for service-specific implementation considerations and guidance
2. **Amazon Bedrock Knowledge Base priority**: Use `ListKnowledgeBases` and `QueryKnowledgeBases` to consult both "coding-standards-KB" and "[language]-premium-KB" for language-specific structural guidance and exemplary implementations
3. **Template selection**: Use the best examples identified in the knowledge bases as templates for new code
4. **Consistency maintenance**: Follow the established patterns and conventions documented in the knowledge bases for the target language
5. **Structure validation**: Verify new implementations match the proven structures outlined in knowledge base examples

### Mandatory Knowledge Base Workflow
**BEFORE ANY CODE CREATION:**
1. **IMMEDIATELY** execute: `ListKnowledgeBases` and `QueryKnowledgeBases("coding-standards-KB", "[language]-code-example-standards")`
2. **IMMEDIATELY** execute: `QueryKnowledgeBases("[language]-premium-KB", "[language] implementation patterns")`
3. **Use AWS Knowledge MCP Server tools**: `search_documentation` and `read_documentation` for service understanding
4. **WAIT** for KB search results before proceeding
5. **DOCUMENT** the KB findings in your response
6. **USE** KB results as the single source of truth for all structural decisions

### Command Execution Standards
- **Single command execution**: Execute CLI commands individually rather than chaining them together
- **Step-by-step approach**: Break complex operations into discrete, sequential commands
- **Error isolation**: Individual command execution allows for better error tracking and resolution

## Configuration Files

### Root Level
- `.gitignore` - Git ignore patterns for all languages
- `.flake8` - Python linting configuration
- `abaplint.json` - SAP ABAP linting rules

### Language-Specific
- `requirements.txt` - Python dependencies
- `pom.xml` - Java Maven configuration
- `package.json` - JavaScript/Node.js dependencies
- `Cargo.toml` - Rust dependencies
- `*.csproj`, `*.sln` - .NET project files