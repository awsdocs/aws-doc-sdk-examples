# Technology Stack & Build System

## Multi-Language Architecture
This repository supports 12+ programming languages, each with their own build systems and conventions:

**NOTE for validation**: Focus validation efforts on the primary languages (Python, JavaScript, Java, Go, .NET, PHP, Kotlin, C++, Rust). Skip validation for Swift, Ruby, and SAP ABAP as they require specialized environments or have version compatibility issues.

## Language-Specific Guidelines

For detailed language-specific guidelines, refer to the individual steering files:

### Primary Languages & Build Tools (Validation Priority)
- **Python**: See `python-tech.md` for Python specific guidelines (pip, venv, pytest, black, pylint, flake8)
- **Java**: See `java-tech.md` for Java v2 specific guidelines (Maven, JUnit 5, Apache Maven Shade Plugin)
- **JavaScript/Node.js**: See `javascript-tech.md` for JavaScript v3 specific guidelines (npm, Jest, Prettier, Biome)
- **Rust**: See `rust-tech.md` for Rust v1 specific guidelines (Cargo, cargo test, cargo check)
- **.NET**: See `dotnet-tech.md` for .NET specific guidelines (dotnet CLI, NuGet, dotnet-format, xUnit)
- **Go v2**: go mod, go test, go fmt
- **Kotlin**: Gradle, JUnit
- **PHP**: Composer, PHPUnit
- **C++**: CMake, custom build scripts

### Secondary Languages (Skip During Validation)
- **Ruby**: Bundler, RSpec (version compatibility issues)
- **Swift**: Swift Package Manager (requires macOS-specific setup)
- **SAP ABAP**: ABAP development tools (requires specialized SAP environment)

## Development Tools & Standards

### Code Quality
- **Linting**: Language-specific linters (pylint, ESLint, etc.)
- **Formatting**: Automated formatters (black, prettier, dotnet-format)
- **Testing**: Unit and integration test suites for all examples
- **Type Safety**: Type annotations where supported (Python, TypeScript)

### Docker Support
- Multi-language Docker images available on Amazon ECR
- Pre-loaded dependencies and isolated environments
- Container-based integration testing framework

### Documentation Generation
- Metadata-driven documentation using `.doc_gen/metadata/*.yaml`
- Snippet extraction and validation
- Integration with AWS Documentation pipeline
- Cross-reference validation system

## Research and Discovery Guidelines

### Understanding AWS Services
When working with AWS services, follow this mandatory research sequence:
1. **Use AWS Knowledge MCP Server tools FIRST**: Use `search_documentation` and `read_documentation` to understand service fundamentals, API operations, parameters, and best practices
2. **Query Amazon Bedrock Knowledge Base**: Use `ListKnowledgeBases` and `QueryKnowledgeBases` to find existing implementation patterns and proven code structures
3. **Check other SDK implementations**: Look at how other language directories implement the same service for patterns and best practices
4. **Cross-reference examples**: Compare implementations across languages to identify common patterns and service-specific requirements

**CRITICAL**: Every code example must begin with AWS Knowledge MCP Server consultation and Amazon Bedrock Knowledge Base research before any implementation work begins.

### MANDATORY Tool Usage Sequence

**FIRST - Amazon Bedrock Knowledge Base Search for Standards:**
```
ListKnowledgeBases()
QueryKnowledgeBases("coding-standards-KB", "[language]-code-example-standards")
```
- **REQUIRED** before any other research
- **REQUIRED** before reading any existing files
- **REQUIRED** before making structural decisions

**SECOND - Amazon Bedrock Knowledge Base Search for Language Patterns:**
```
QueryKnowledgeBases("[language]-premium-KB", "[language] implementation patterns")
```
- Use for language-specific coding patterns and quality benchmarks
- Required for understanding language-specific requirements

**THIRD - AWS Service Understanding:**
```
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```
- Use for service fundamentals, API operations, parameters, and best practices
- Required for understanding service-specific requirements

### Understanding Language Structure
When working with a specific programming language, follow this MANDATORY sequence:

**STEP 1 - MANDATORY FIRST ACTION**: 
- **IMMEDIATELY** use `ListKnowledgeBases` and `QueryKnowledgeBases` to search "coding-standards-KB" for "[language]-code-example-standards"
- **DO NOT** read any existing code files until this search is complete
- **DO NOT** make any assumptions about structure until KB search is done

**STEP 2 - Language Pattern Discovery**:
- Query "[language]-premium-KB" through `QueryKnowledgeBases` for implementation patterns and quality benchmarks

**STEP 3 - Service Context**:
- Use AWS Knowledge MCP Server tools for service-specific implementation guidance

**STEP 4 - Pattern Validation**:
- Use KB search results as the AUTHORITATIVE guide for all structure decisions
- Only examine existing code files to VERIFY the KB-documented patterns
- Never use existing code as the primary source of truth

**STEP 5 - Implementation**:
- Follow KB-documented patterns exactly
- Use KB examples as templates for new implementations

**CRITICAL FAILURE POINT**: Creating code without first searching the Amazon Bedrock Knowledge Base for language standards will result in incorrect structure.

**MANDATORY WORKFLOW ENFORCEMENT:**

**BEFORE ANY CODE CREATION - REQUIRED FIRST ACTIONS:**
1. **IMMEDIATELY** execute: `ListKnowledgeBases` and `QueryKnowledgeBases("coding-standards-KB", "[language]-code-example-standards")`
2. **IMMEDIATELY** execute: `QueryKnowledgeBases("[language]-premium-KB", "[language] implementation patterns")`
3. **WAIT** for KB search results before proceeding
4. **DOCUMENT** the KB findings in your response
5. **USE** KB results as the single source of truth for all structural decisions

**PROHIBITED ACTIONS UNTIL KB SEARCH IS COMPLETE:**
- ❌ **DO NOT** use `readFile` or `readMultipleFiles` on existing code examples
- ❌ **DO NOT** use `listDirectory` to examine existing project structures  
- ❌ **DO NOT** make assumptions about naming conventions
- ❌ **DO NOT** assume file organization patterns

**ONLY AFTER KB SEARCH:**
- ✅ Use existing code files to VERIFY KB-documented patterns
- ✅ Follow KB examples as authoritative templates
- ✅ Reference existing code to confirm KB guidance

**FAILURE TO FOLLOW THIS EXACT SEQUENCE WILL RESULT IN INCORRECT CODE STRUCTURE**

### CLI Command Execution
- **Single command preference**: Execute one CLI command at a time unless combining commands is explicitly required
- **Sequential execution**: Break complex operations into individual steps for better error handling and debugging
- **Clear command separation**: Avoid chaining commands with && or || unless necessary for the specific workflow

## Common Mistakes to Avoid

### Testing
- **NEVER accept an error as "expected." When you are given code that runs without errors, your result should run and test without errors.**

### Language-Specific Pattern Errors
- **.YML**: **ALWAYS end a .yml or .yaml file with a single new line character.**

### Pattern Discovery Failures
- **Root Cause**: Assuming patterns instead of consulting knowledge bases
- **Solution**: Always use Amazon Bedrock Knowledge Base tools before examining existing code
- **Verification**: Compare your structure against KB-documented patterns and at least 2 existing examples in the same language

## Service Example Requirements

### Mandatory Hello Scenario
**CRITICAL**: Every AWS service MUST include a "Hello" scenario as the simplest introduction to the service.

**Hello Scenario Requirements:**
- **Purpose**: Provide the simplest possible example of using the service
- **Scope**: Demonstrate basic service connectivity and one fundamental operation
- **Naming**: Always named "Hello {ServiceName}" (e.g., "Hello S3", "Hello Comprehend")
- **Implementation**: Should be the most basic, minimal example possible
- **Documentation**: Must include clear explanation of what the hello example does

**Hello Scenario Examples:**
- **S3**: List buckets or check if service is accessible
- **DynamoDB**: List tables or describe service limits
- **Lambda**: List functions or get account settings
- **Comprehend**: Detect language in a simple text sample
- **EC2**: Describe regions or availability zones

**When to Create Hello Scenarios:**
- **Always required**: Every service implementation must include a hello scenario
- **First priority**: Create the hello scenario before any complex scenarios
- **Standalone**: Hello scenarios should work independently of other examples
- **Minimal dependencies**: Should require minimal setup or configuration

## Development Workflow Requirements

### Pre-Push Validation Pipeline
When creating or modifying code examples, the following steps must be completed successfully before the work is considered complete:

1. **MANDATORY Amazon Bedrock Knowledge Base Research**: Use `ListKnowledgeBases` and `QueryKnowledgeBases` for both "coding-standards-KB" and "[language]-premium-KB" - REQUIRED FIRST STEP
2. **MANDATORY AWS Knowledge MCP Server Research**: Use `search_documentation` and `read_documentation` for comprehensive service understanding
3. **README Generation**: Run `.tools/readmes/writeme.py` to update documentation
4. **Integration Testing**: Execute integration tests to verify AWS service interactions
5. **Test Validation**: All tests must pass with zero errors before considering work complete
6. **Automatic Error Resolution**: If any step fails, automatically fix issues and re-run until success

**MANDATORY COMPLETION REQUIREMENTS**: 

**PRE-IMPLEMENTATION CHECKLIST (MUST BE COMPLETED FIRST):**
- [ ] **KB SEARCH COMPLETED**: Executed `ListKnowledgeBases` and `QueryKnowledgeBases` for both knowledge bases
- [ ] **KB RESULTS DOCUMENTED**: Included KB findings in response showing evidence of consultation
- [ ] **AWS KNOWLEDGE MCP SERVER CONSULTED**: Used `search_documentation` and `read_documentation` for service understanding
- [ ] **PATTERNS IDENTIFIED**: Used KB results to determine file structure, naming, and organization

**IMPLEMENTATION REQUIREMENTS:**
- **ALL TESTS MUST PASS COMPLETELY**: Both unit tests and integration tests must pass with zero failures before work is considered finished
- **NO WORK IS COMPLETE WITH FAILING TESTS**: If any test fails, the implementation must be fixed and re-tested until all tests pass
- **Every service must include a Hello scenario** - no exceptions
- Hello scenarios must be created first, before any complex scenarios

**FAILURE CONDITIONS:**
- **FAILURE TO SEARCH AMAZON BEDROCK KNOWLEDGE BASE FIRST WILL RESULT IN REJECTED CODE**
- **FAILURE TO DOCUMENT KB CONSULTATION WILL RESULT IN REJECTED CODE**
- **FAILURE TO ENSURE ALL TESTS PASS WILL RESULT IN INCOMPLETE WORK**

### Test Execution Requirements
- **Unit Tests**: Must pass completely with no failures or errors
- **Integration Tests**: Must pass completely (skipped tests are acceptable if properly documented)
- **Test Coverage**: All major code paths and error conditions must be tested
- **Error Handling**: All specified error conditions must be properly tested and handled
- **Real AWS Integration**: Integration tests should use real AWS services where possible

### Testing Standards
- **Integration-First Approach**: All code examples must use integration tests that make real AWS API calls
- **No Mocking/Stubbing**: Avoid mocked responses; tests should demonstrate actual AWS service interactions
- **Real Resource Testing**: Tests should create, modify, and clean up actual AWS resources
- **Cost Awareness**: Integration tests may incur AWS charges - ensure proper resource cleanup
- **Cleanup Requirements**: All integration tests must properly clean up created resources

### Update/Write READMEs

Use virtual environment to run the WRITEME tool. Navigate to .tools/readmes and run the below commands:

```
python -m venv .venv

# Windows
.venv\Scripts\activate
python -m pip install -r requirements_freeze.txt

# Linux or MacOS
source .venv/bin/activate

python -m writeme --languages [language]:[version] --services [service]
```

### Error Handling Protocol
- If WRITEME fails: Fix documentation issues, missing metadata, or file structure problems
- If tests fail: Debug AWS service interactions, fix credential issues, or resolve resource conflicts
- Re-run both steps until both pass successfully
- Only then is the example considered complete and ready for commit

## AWS SDK Versions
- Use latest stable SDK versions for each language
- Follow SDK-specific best practices and patterns
- Maintain backward compatibility where possible
- Credential configuration via AWS credentials file or environment variables