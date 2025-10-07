# Technology Stack & Build System

## Multi-Language Architecture
This repository supports 12+ programming languages, each with their own build systems and conventions:

**Note for validation**: Focus validation efforts on the primary languages (Python, JavaScript, Java, Go, .NET, PHP, Kotlin, C++, Rust). Skip validation for Swift, Ruby, and SAP ABAP as they require specialized environments or have version compatibility issues.

### Primary Languages & Build Tools (Validation Priority)
- **Python 3.6+**: pip, venv, pytest, black, pylint, flake8
- **Java v2**: Maven, JUnit 5, Apache Maven Shade Plugin
- **.NET 3.5+**: dotnet CLI, NuGet, dotnet-format, xUnit
- **JavaScript/Node.js**: npm, Jest, Prettier, Biome
- **Go v2**: go mod, go test, go fmt
- **Kotlin**: Gradle, JUnit
- **PHP**: Composer, PHPUnit
- **C++**: CMake, custom build scripts
- **Rust**: Cargo, cargo test, cargo check

### Secondary Languages (Skip During Validation)
- **Ruby**: Bundler, RSpec (version compatibility issues)
- **Swift**: Swift Package Manager (requires macOS-specific setup)
- **SAP ABAP**: ABAP development tools (requires specialized SAP environment)

## Common Build Commands

### Python
```bash
python -m venv .venv
source .venv/bin/activate  # Linux/macOS
.venv\Scripts\activate     # Windows
pip install -r requirements.txt
python -m pytest -m "not integ"  # Unit tests
python -m pytest -m "integ"      # Integration tests
black .                           # Format code
pylint --rcfile=.github/linters/.python-lint .
```

### Java
```bash
mvn package                # Build with dependencies
mvn test                   # Run tests
java -cp target/PROJECT-1.0-SNAPSHOT.jar com.example.Main
```

### .NET
```bash
dotnet build SOLUTION.sln  # Build solution
dotnet run                 # Run project
dotnet test                # Run tests
dotnet format              # Format code
```

#### AWS Credentials Testing
- **CRITICAL**: Before assuming AWS credential issues, always test credentials first with `aws sts get-caller-identity`
- **NEVER** assume credentials are incorrect without verification
- If credentials test passes but .NET SDK fails, investigate SDK-specific credential chain issues
- Common .NET SDK credential issues: EC2 instance metadata service conflicts, credential provider chain order

#### DotNetV4 Build Troubleshooting
- **CRITICAL**: When you get a response that the project file does not exist, use `listDirectory` to find the correct project/solution file path before trying to build again
- **NEVER** repeatedly attempt the same build command without first locating the actual file structure
- Always verify file existence with directory listing before executing build commands

### JavaScript
```bash
npm install                # Install dependencies
npm test                   # Run tests
npm run lint               # Lint code
```

### Rust
```bash
cargo check                # Check compilation
cargo test                 # Run tests
cargo build                # Build project
cargo run --bin getting-started  # Run getting started scenario
```

#### Rust Project Structure Pattern
**CRITICAL**: Rust examples follow a specific directory structure pattern. Always examine existing Rust examples (like EC2) before creating new ones:

**Correct Structure for Rust Scenarios:**
```
rustv1/examples/{service}/
├── Cargo.toml
├── README.md
├── src/
│   ├── lib.rs
│   ├── {service}.rs              # Service wrapper
│   ├── bin/
│   │   ├── hello.rs              # MANDATORY: Hello scenario
│   │   ├── {action-one}.rs       # Individual action file
│   │   ├── {action-two}.rs       # Individual action file, etc.
│   │   └── {scenario-name}.rs    # Other scenario entry points
│   └── {scenario_name}/
│       ├── mod.rs
│       ├── scenario.rs           # Main scenario logic
│       └── tests/
│           └── mod.rs            # Integration tests
```

**Key Points:**
- **MANDATORY**: Every service must include `src/bin/hello.rs` as the simplest example
- **Follow the EC2 example structure exactly** - it's the canonical pattern
- **Service wrapper goes in `src/{service}.rs`** (e.g., `src/comprehend.rs`)
- **Tests go in `getting_started/tests/mod.rs`** for integration testing
- **Hello scenario**: Should demonstrate the most basic service operation

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
1. **Query Tejas Knowledge Base FIRST**: Use `query_tejas_kb` to understand service fundamentals, API operations, parameters, and best practices
2. **Search local knowledge base**: Use `search` to find existing implementation patterns and proven code structures in this repository
3. **Check other SDK implementations**: Look at how other language directories implement the same service for patterns and best practices
4. **Cross-reference examples**: Compare implementations across languages to identify common patterns and service-specific requirements

**CRITICAL**: Every code example must begin with Tejas KB consultation using `query_tejas_kb` before any implementation work begins.

### MANDATORY Tool Usage Sequence

**FIRST - Quality Code Examples Knowledge Base Search:**
```
mcp_Quality_Code_Examples_Knowledge_Base_search(query="[language] code example standards")
```
- **REQUIRED** before any other research
- **REQUIRED** before reading any existing files
- **REQUIRED** before making structural decisions

**SECOND - AWS Service Understanding:**
```
query_tejas_kb("What is [AWS Service] and what are its key API operations?")
```
- Use for service fundamentals, API operations, parameters, and best practices
- Required for understanding service-specific requirements

**THIRD - Pattern Verification (ONLY AFTER KB SEARCHES):**
- Use `readFile` or `listDirectory` ONLY to verify KB-documented patterns
- Never use existing code as primary source of structural decisions

**TOOL USAGE RESTRICTIONS:**
- **NEVER** use `readFile`/`readMultipleFiles` on existing code before KB search
- **NEVER** use `listDirectory` to discover patterns before KB search  
- **NEVER** assume patterns without KB consultation first

### Understanding Language Structure
When working with a specific programming language, follow this MANDATORY sequence:

**STEP 1 - MANDATORY FIRST ACTION**: 
- **IMMEDIATELY** search Quality Code Examples Knowledge Base for "[language] code example standards"
- **DO NOT** read any existing code files until this search is complete
- **DO NOT** make any assumptions about structure until KB search is done

**STEP 2 - Service Context**:
- Query `query_tejas_kb` for service-specific implementation guidance

**STEP 3 - Pattern Validation**:
- Use KB search results as the AUTHORITATIVE guide for all structure decisions
- Only examine existing code files to VERIFY the KB-documented patterns
- Never use existing code as the primary source of truth

**STEP 4 - Implementation**:
- Follow KB-documented patterns exactly
- Use KB examples as templates for new implementations

**CRITICAL FAILURE POINT**: Creating code without first searching the Quality Code Examples Knowledge Base for language standards will result in incorrect structure.

**MANDATORY WORKFLOW ENFORCEMENT:**

**BEFORE ANY CODE CREATION - REQUIRED FIRST ACTIONS:**
1. **IMMEDIATELY** execute: `mcp_Quality_Code_Examples_Knowledge_Base_search` with query "[language] code example standards"
2. **WAIT** for KB search results before proceeding
3. **DOCUMENT** the KB findings in your response
4. **USE** KB results as the single source of truth for all structural decisions

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

- **Testing**
  - ** NEVER accept an error as "expected." When you are given code that runs without errors, your result should run and test without errors.**

### Language-Specific Pattern Errors
- **Rust**: 
  - ❌ **NEVER assume file naming patterns** without checking existing examples
  - ✅ **ALWAYS examine `rustv1/examples/ec2/` structure first**

- **Python**:
  - ❌ **NEVER create scenarios without checking existing patterns**
  - ✅ **ALWAYS follow the established `{service}_basics.py` or scenario patterns**

- **Java**:
  - ❌ **NEVER assume class naming without checking existing examples**
  - ✅ **ALWAYS follow the established Maven project structure**

- **DotNet**:
  -  **NEVER create examples for dotnetv3 UNLESS explicitly instructed to by the user**

- **.YML**:
  -  **ALWAYS end a .yml or .yaml file with a single new line character.**

### Pattern Discovery Failures
- **Root Cause**: Assuming patterns instead of examining existing code
- **Solution**: Always run `listDirectory` on existing examples in the target language before creating new ones
- **Verification**: Compare your structure against at least 2 existing examples in the same language

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

**Hello Scenario Structure by Language:**
- **Python**: `{service}_hello.py` or hello function in main module
- **Java**: `Hello{Service}.java` class with main method
- **Rust**: `hello.rs` in `src/bin/` directory or hello function in scenario
- **JavaScript**: `hello-{service}.js` or hello function in main module
- **.NET**: `Hello{Service}.cs` class with main method

## Development Workflow Requirements

### Pre-Push Validation Pipeline
When creating or modifying code examples, the following steps must be completed successfully before the work is considered complete:

1. **MANDATORY Quality Code Examples KB Research**: Search Quality Code Examples Knowledge Base for "[language] code example standards" - REQUIRED FIRST STEP
2. **Knowledge Base Research**: Query `query_tejas_kb` for comprehensive service understanding and search Quality Code Examples Knowledge Base for patterns
3. **README Generation**: Run `.tools/readmes/writeme.py` to update documentation
4. **Integration Testing**: Execute integration tests to verify AWS service interactions
5. **Test Validation**: All tests must pass with zero errors before considering work complete
6. **Automatic Error Resolution**: If any step fails, automatically fix issues and re-run until success

**MANDATORY COMPLETION REQUIREMENTS**: 

**PRE-IMPLEMENTATION CHECKLIST (MUST BE COMPLETED FIRST):**
- [ ] **KB SEARCH COMPLETED**: Executed `mcp_Quality_Code_Examples_Knowledge_Base_search` for "[language] code example standards"
- [ ] **KB RESULTS DOCUMENTED**: Included KB findings in response showing evidence of consultation
- [ ] **TEJAS KB CONSULTED**: Queried `query_tejas_kb` for service understanding
- [ ] **PATTERNS IDENTIFIED**: Used KB results to determine file structure, naming, and organization

**IMPLEMENTATION REQUIREMENTS:**
- **ALL TESTS MUST PASS COMPLETELY**: Both unit tests and integration tests must pass with zero failures before work is considered finished
- **NO WORK IS COMPLETE WITH FAILING TESTS**: If any test fails, the implementation must be fixed and re-tested until all tests pass
- **Every service must include a Hello scenario** - no exceptions
- Hello scenarios must be created first, before any complex scenarios

**FAILURE CONDITIONS:**
- **FAILURE TO SEARCH QUALITY CODE EXAMPLES KB FIRST WILL RESULT IN REJECTED CODE**
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

### Validation Commands by Language
```bash
# Python
python .tools/readmes/writeme.py
python -m pytest -m "integ"

# Java
python .tools/readmes/writeme.py
mvn test

# .NET
python .tools/readmes/writeme.py
dotnet test

# JavaScript
python .tools/readmes/writeme.py
npm test

# Rust
python .tools/readmes/writeme.py
cargo test
```

### Error Handling Protocol
- If `writeme.py` fails: Fix documentation issues, missing metadata, or file structure problems
- If tests fail: Debug AWS service interactions, fix credential issues, or resolve resource conflicts
- Re-run both steps until both pass successfully
- Only then is the example considered complete and ready for commit

## AWS SDK Versions
- Use latest stable SDK versions for each language
- Follow SDK-specific best practices and patterns
- Maintain backward compatibility where possible
- Credential configuration via AWS credentials file or environment variables

