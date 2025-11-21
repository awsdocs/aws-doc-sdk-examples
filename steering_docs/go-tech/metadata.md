# Go Metadata and Module Configuration

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Go-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Go-premium-KB", "Go module configuration patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate proper Go module configuration, dependency management, and metadata files for AWS SDK examples in the gov2 directory structure.

## Requirements
- **Go Modules**: Use go.mod for dependency management
- **Version Compatibility**: Go 1.18+ (recommended Go 1.21+)
- **AWS SDK v2**: Use AWS SDK for Go v2 exclusively
- **Internal Dependencies**: Reference gov2/demotools and gov2/testtools
- **Build Tags**: Use build tags for integration tests

## File Structure
```
gov2/{service}/
├── go.mod                          # Module definition
├── go.sum                          # Module checksums (auto-generated)
├── README.md                       # Service documentation
└── .gitignore                      # Git ignore patterns
```

## Go Module Configuration (go.mod)
```go
module github.com/awsdocs/aws-doc-sdk-examples/gov2/{service}

go 1.21

require (
    github.com/aws/aws-sdk-go-v2 v1.30.5
    github.com/aws/aws-sdk-go-v2/config v1.27.33
    github.com/aws/aws-sdk-go-v2/service/{service} v1.x.x
    github.com/aws/smithy-go v1.20.4
    github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0
    github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0
)

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools => ../demotools

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools => ../testtools
```

## Common AWS SDK Dependencies

### Core SDK Dependencies
```go
// Always required
github.com/aws/aws-sdk-go-v2 v1.30.5
github.com/aws/aws-sdk-go-v2/config v1.27.33
github.com/aws/smithy-go v1.20.4

// Service-specific client
github.com/aws/aws-sdk-go-v2/service/{service} v1.x.x
```

### Additional Common Dependencies
```go
// For credential management
github.com/aws/aws-sdk-go-v2/credentials v1.17.32

// For AWS configuration
github.com/aws/aws-sdk-go-v2/aws v1.30.5

// For feature detection
github.com/aws/aws-sdk-go-v2/feature/ec2/imds v1.16.13

// For S3 specific features (if using S3)
github.com/aws/aws-sdk-go-v2/feature/s3/manager v1.17.18
github.com/aws/aws-sdk-go-v2/service/s3 v1.61.2

// For DynamoDB specific features (if using DynamoDB)
github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue v1.14.11
github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression v1.7.36
github.com/aws/aws-sdk-go-v2/service/dynamodb v1.34.9

// For STS operations (if needed)
github.com/aws/aws-sdk-go-v2/service/sts v1.30.7
```

### Internal Tool Dependencies
```go
// Demo utilities
github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0

// Testing utilities  
github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0
```

## Service-Specific go.mod Examples

### S3 Service Example
```go
module github.com/awsdocs/aws-doc-sdk-examples/gov2/s3

go 1.21

require (
    github.com/aws/aws-sdk-go-v2 v1.30.5
    github.com/aws/aws-sdk-go-v2/config v1.27.33
    github.com/aws/aws-sdk-go-v2/feature/s3/manager v1.17.18
    github.com/aws/aws-sdk-go-v2/service/s3 v1.61.2
    github.com/aws/smithy-go v1.20.4
    github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0
    github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0
)

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools => ../demotools

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools => ../testtools
```

### DynamoDB Service Example
```go
module github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb

go 1.21

require (
    github.com/aws/aws-sdk-go-v2 v1.30.5
    github.com/aws/aws-sdk-go-v2/config v1.27.33
    github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue v1.14.11
    github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression v1.7.36
    github.com/aws/aws-sdk-go-v2/service/dynamodb v1.34.9
    github.com/aws/smithy-go v1.20.4
    github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0
    github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0
)

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools => ../demotools

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools => ../testtools
```

### Lambda Service Example
```go
module github.com/awsdocs/aws-doc-sdk-examples/gov2/lambda

go 1.21

require (
    github.com/aws/aws-sdk-go-v2 v1.30.5
    github.com/aws/aws-sdk-go-v2/config v1.27.33
    github.com/aws/aws-sdk-go-v2/service/lambda v1.58.3
    github.com/aws/smithy-go v1.20.4
    github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0
    github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0
)

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools => ../demotools

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools => ../testtools
```

## Git Ignore Configuration (.gitignore)
```gitignore
# Binaries for programs and plugins
*.exe
*.exe~
*.dll
*.so
*.dylib

# Test binary, built with `go test -c`
*.test

# Output of the go coverage tool, specifically when used with LiteIDE
*.out

# Dependency directories (remove the comment below to include it)
# vendor/

# Go workspace file
go.work

# IDE files
.vscode/
.idea/
*.swp
*.swo
*~

# OS generated files
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Local environment files
.env
.env.local
.env.*.local

# Temporary files
*.tmp
*.temp
```

## README.md Template
```markdown
# AWS SDK for Go V2 {Service Name} examples

## Purpose

Shows how to use the AWS SDK for Go V2 to work with {AWS Service Name}.

## Code examples

### Scenarios

* [Get started with {service} basics](scenarios/scenario_basics.go)

### Actions

The [actions](actions/) package contains a wrapper that calls {AWS Service} functions.
The wrapper is used in the scenarios and examples to unit test the functions without 
calling AWS directly.

* `Create{Resource}` - Creates a {resource}.
* `Delete{Resource}` - Deletes a {resource}.
* `Get{Resource}` - Gets information about a {resource}.
* `List{Resources}` - Lists {resources}.
* `Update{Resource}` - Updates a {resource}.

### Hello {Service}

* [hello](hello/hello.go) - Get started with {AWS Service}.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Go V2](https://aws.github.io/aws-sdk-go-v2/docs/getting-started/).

## Running the code

### Prerequisites

* You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Go V2](https://aws.github.io/aws-sdk-go-v2/docs/getting-started/).

### Hello {Service}

This example shows you how to get started using {AWS Service}.

```
go run ./hello
```

### Scenarios

#### Get started with {service} basics

This interactive scenario runs at a command prompt and shows you how to use {AWS Service} to do the following:

1. {Step 1 description}
2. {Step 2 description}
3. {Step 3 description}
4. {Step 4 description}

Start the scenario by running the following at a command prompt:

```
go run ./scenarios
```

### Tests

⚠ Running tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.

#### Unit tests

You can run unit tests in this folder with the following:

```
go test ./...
```

#### Integration tests

Before running the integration tests, you must set the required resources using the AWS CLI.

You can run integration tests in this folder with the following:

```
go test ./... -tags=integration
```

## Additional resources

* [{AWS Service} User Guide](https://docs.aws.amazon.com/{service}/latest/userguide/)
* [{AWS Service} API Reference](https://docs.aws.amazon.com/{service}/latest/api/)
* [AWS SDK for Go V2 API Reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2)
```

## Build Tags for Integration Tests
```go
//go:build integration

// This build tag ensures integration tests only run when explicitly requested
```

## Version Management

### Checking Current Versions
```bash
# Check current Go version
go version

# Check module dependencies
go list -m all

# Check for available updates
go list -u -m all
```

### Updating Dependencies
```bash
# Update all dependencies to latest compatible versions
go get -u ./...

# Update specific dependency
go get -u github.com/aws/aws-sdk-go-v2@latest

# Tidy up dependencies
go mod tidy
```

## Module Requirements
- ✅ **ALWAYS** use Go 1.21 or later as the minimum version
- ✅ **ALWAYS** include AWS SDK for Go v2 core dependencies
- ✅ **ALWAYS** include service-specific SDK packages
- ✅ **ALWAYS** reference gov2/demotools and gov2/testtools with replace directives
- ✅ **ALWAYS** use semantic versioning for dependencies
- ✅ **ALWAYS** include .gitignore to exclude build artifacts
- ✅ **ALWAYS** provide comprehensive README.md documentation
- ✅ **ALWAYS** use build tags for integration tests

## Common Commands
```bash
# Initialize new module
go mod init github.com/awsdocs/aws-doc-sdk-examples/gov2/{service}

# Add dependencies
go get github.com/aws/aws-sdk-go-v2/service/{service}

# Clean up dependencies
go mod tidy

# Verify dependencies
go mod verify

# Download dependencies
go mod download

# Build all packages
go build ./...

# Run tests
go test ./...

# Run integration tests
go test ./... -tags=integration

# Run with verbose output
go test -v ./...

# Run with race detection
go test -race ./...
```

## Dependency Management Best Practices
- Keep dependencies up to date but test thoroughly
- Use replace directives for local development dependencies
- Minimize external dependencies beyond AWS SDK
- Use semantic versioning for all dependencies
- Regularly run `go mod tidy` to clean up unused dependencies
- Use `go mod verify` to ensure dependency integrity
- Document any special dependency requirements in README