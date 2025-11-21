# Go README/WRITEME and Documentation Generation

## Purpose
Generate and update README files and documentation using the writeme tool to ensure consistency and completeness.

## Requirements
- **Automated Generation**: Use writeme tool for README generation
- **Metadata Dependency**: Requires complete metadata files
- **Virtual Environment**: Run writeme in isolated environment
- **Validation**: Ensure all documentation is up-to-date

## File Structure
```
gov2/{service}/
├── README.md                   # Generated service README
├── go.mod                      # Module dependencies
└── {service}_metadata.yaml     # Metadata (in .doc_gen/metadata/)
```

## README Generation Process

### Step 1: Setup Writeme Environment
```bash
cd .tools/readmes

# Create virtual environment
python -m venv .venv

# Activate environment (Linux/macOS)
source .venv/bin/activate

# Activate environment (Windows)
.venv\Scripts\activate

# Install dependencies
python -m pip install -r requirements_freeze.txt
```

### Step 2: Generate README
```bash
# Generate README for specific service
python -m writeme --languages Go:2 --services {service}
```

### Step 3: Validate Generation
- ✅ **README.md created/updated** in service directory
- ✅ **No generation errors** in writeme output
- ✅ **All examples listed** in README
- ✅ **Proper formatting** and structure
- ✅ **Working links** to code files

## README Content Structure

### Generated README Sections
1. **Service Overview**: Description of AWS service
2. **Code Examples**: List of available examples
3. **Prerequisites**: Setup requirements
4. **Installation**: Dependency installation
5. **Usage**: How to run examples
6. **Tests**: Testing instructions
7. **Additional Resources**: Links to documentation

## README Template Structure
```markdown
# AWS SDK for Go V2 {Service Name} examples

## Purpose

Shows how to use the AWS SDK for Go V2 to work with {AWS Service Name} ({Service Abbreviation}).

{Brief description of what the service does and why it's useful}

## Code examples

### Scenarios

{List of interactive scenarios with descriptions}

* [Get started with {service} basics](scenarios/scenario_basics.go)

### Actions

The [actions](actions/) package contains a wrapper that calls {AWS Service} functions.
The wrapper is used in the scenarios and examples to unit test the functions without 
calling AWS directly.

{List of available actions with brief descriptions}

* `Create{Resource}` - Creates a {resource description}.
* `Delete{Resource}` - Deletes a {resource description}.
* `Get{Resource}` - Gets information about a {resource description}.
* `List{Resources}` - Lists {resources description}.
* `Update{Resource}` - Updates a {resource description}.

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

### Hello {Service}

This example shows you how to get started using {AWS Service}.

```
go run ./hello
```

### Scenarios

#### Get started with {service} basics

This interactive scenario runs at a command prompt and shows you how to use {AWS Service} to do the following:

{List of scenario steps based on SPECIFICATION.md}

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

## Documentation Dependencies

### Required Files for README Generation
- ✅ **Metadata file**: `.doc_gen/metadata/{service}_metadata.yaml`
- ✅ **Code files**: All referenced Go files must exist
- ✅ **Snippet tags**: All snippet tags in metadata must exist in code
- ✅ **Module file**: `go.mod` with dependencies

### Metadata Integration
The writeme tool uses metadata to:
- Generate example lists and descriptions
- Create links to specific code sections
- Include proper service information
- Format documentation consistently

## Troubleshooting README Generation

### Common Issues
- **Missing metadata**: Ensure metadata file exists and is valid
- **Broken snippet tags**: Verify all snippet tags exist in code
- **File not found**: Check all file paths in metadata
- **Invalid YAML**: Validate metadata YAML syntax

### Error Resolution
```bash
# Check for metadata errors
python -m writeme --languages Go:2 --services {service} --verbose

# Validate specific metadata file
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Check for missing snippet tags
grep -r "snippet-start" gov2/{service}/
```

## README Maintenance

### When to Regenerate README
- ✅ **After adding new examples**
- ✅ **After updating metadata**
- ✅ **After changing code structure**
- ✅ **Before committing changes**
- ✅ **During regular maintenance**

### README Quality Checklist
- ✅ **All examples listed** and properly linked
- ✅ **Prerequisites accurate** and complete
- ✅ **Installation instructions** work correctly
- ✅ **Usage examples** are clear and correct
- ✅ **Links functional** and point to right locations
- ✅ **Formatting consistent** with other services

## Service-Specific README Examples

### S3 Service README
```markdown
# AWS SDK for Go V2 Amazon S3 examples

## Purpose

Shows how to use the AWS SDK for Go V2 to work with Amazon Simple Storage Service (Amazon S3).

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance.

## Code examples

### Scenarios

* [Get started with buckets and objects](scenarios/scenario_basics.go)

### Actions

The [actions](actions/) package contains a wrapper that calls Amazon S3 functions.
The wrapper is used in the scenarios and examples to unit test the functions without 
calling AWS directly.

* `CreateBucket` - Creates an S3 bucket.
* `DeleteBucket` - Deletes an S3 bucket.
* `GetObject` - Gets an object from an S3 bucket.
* `ListBuckets` - Lists S3 buckets for your account.
* `ListObjects` - Lists objects in an S3 bucket.
* `PutObject` - Puts an object in an S3 bucket.

### Hello Amazon S3

* [hello](hello/hello.go) - Get started with Amazon S3.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Go V2](https://aws.github.io/aws-sdk-go-v2/docs/getting-started/).

## Running the code

### Hello Amazon S3

This example shows you how to get started using Amazon S3.

```
go run ./hello
```

### Scenarios

#### Get started with buckets and objects

This interactive scenario runs at a command prompt and shows you how to use Amazon S3 to do the following:

1. Create a bucket and upload a file to it.
2. Download an object from a bucket.
3. Copy an object to a subfolder in a bucket.
4. List objects in a bucket.
5. Delete all objects in a bucket.
6. Delete a bucket.

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

* [Amazon S3 User Guide](https://docs.aws.amazon.com/s3/latest/userguide/)
* [Amazon S3 API Reference](https://docs.aws.amazon.com/s3/latest/api/)
* [AWS SDK for Go V2 API Reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2)
```

### DynamoDB Service README
```markdown
# AWS SDK for Go V2 Amazon DynamoDB examples

## Purpose

Shows how to use the AWS SDK for Go V2 to work with Amazon DynamoDB.

Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability.

## Code examples

### Scenarios

* [Get started with tables, items, and queries](scenarios/scenario_basics.go)

### Actions

The [actions](actions/) package contains a wrapper that calls Amazon DynamoDB functions.
The wrapper is used in the scenarios and examples to unit test the functions without 
calling AWS directly.

* `CreateTable` - Creates a DynamoDB table.
* `DeleteItem` - Deletes an item from a DynamoDB table.
* `DeleteTable` - Deletes a DynamoDB table.
* `GetItem` - Gets an item from a DynamoDB table.
* `ListTables` - Lists DynamoDB tables for your account.
* `PutItem` - Puts an item in a DynamoDB table.
* `Query` - Queries a DynamoDB table.
* `Scan` - Scans a DynamoDB table.
* `UpdateItem` - Updates an item in a DynamoDB table.

### Hello Amazon DynamoDB

* [hello](hello/hello.go) - Get started with Amazon DynamoDB.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Go V2](https://aws.github.io/aws-sdk-go-v2/docs/getting-started/).

## Running the code

### Hello Amazon DynamoDB

This example shows you how to get started using Amazon DynamoDB.

```
go run ./hello
```

### Scenarios

#### Get started with tables, items, and queries

This interactive scenario runs at a command prompt and shows you how to use Amazon DynamoDB to do the following:

1. Create a table that can hold movie data.
2. Put, get, and update a single movie in the table.
3. Write movie data to the table from a sample JSON file.
4. Query for movies that were released in a given year.
5. Scan for movies that were released in a range of years.
6. Delete a movie from the table, then delete the table.

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

* [Amazon DynamoDB Developer Guide](https://docs.aws.amazon.com/dynamodb/latest/developerguide/)
* [Amazon DynamoDB API Reference](https://docs.aws.amazon.com/dynamodb/latest/api/)
* [AWS SDK for Go V2 API Reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2)
```

## README Generation Guidelines

### Service Description
- Start with the official AWS service name
- Include the service abbreviation if commonly used
- Provide a brief, clear description of what the service does
- Explain the primary use cases or benefits

### Code Examples Section
- List scenarios first (interactive examples)
- List actions second (wrapper functions)
- List hello examples last (basic connectivity)
- Use consistent formatting and descriptions
- Include file paths for easy navigation

### Prerequisites Section
- Always reference the official AWS SDK for Go V2 getting started guide
- Mention AWS account requirement
- Emphasize credential and region configuration
- Keep it concise but complete

### Running Instructions
- Provide exact commands to run each example
- Use code blocks for commands
- Include any special setup requirements
- Mention command-line arguments if needed

### Testing Section
- Always include the warning about potential charges
- Provide separate instructions for unit and integration tests
- Reference the main gov2 README for detailed testing instructions
- Include build tags information for integration tests

### Additional Resources
- Always include links to official AWS documentation
- Include both user guide and API reference
- Add AWS SDK for Go V2 API reference
- Use consistent link formatting

## Integration with CI/CD

### Automated README Validation
```bash
# In CI/CD pipeline, validate README is up-to-date
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Go:2 --services {service} --check

# Exit with error if README needs updates
if git diff --exit-code gov2/{service}/README.md; then
    echo "README is up-to-date"
else
    echo "README needs to be regenerated"
    exit 1
fi
```

This ensures documentation stays synchronized with code changes.