# Go README Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Go-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Go-premium-KB", "Go documentation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive README.md files for Go AWS SDK examples that provide clear documentation, setup instructions, and usage examples.

## Requirements
- **Service Overview**: Clear description of the AWS service and its purpose
- **Code Examples**: List all available examples with descriptions
- **Prerequisites**: Setup and credential configuration instructions
- **Usage Instructions**: Step-by-step instructions for running examples
- **Testing**: Instructions for running unit and integration tests
- **Resources**: Links to relevant documentation

## File Structure
```
gov2/{service}/
└── README.md                       # Main service documentation
```

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

## README Requirements
- ✅ **ALWAYS** include service name and abbreviation in title
- ✅ **ALWAYS** provide clear service description and purpose
- ✅ **ALWAYS** list all available code examples with descriptions
- ✅ **ALWAYS** include prerequisites and setup instructions
- ✅ **ALWAYS** provide exact commands for running examples
- ✅ **ALWAYS** include testing instructions with warnings
- ✅ **ALWAYS** add links to relevant AWS documentation
- ✅ **ALWAYS** use consistent formatting and structure
- ✅ **ALWAYS** include important warnings about charges and permissions
- ✅ **ALWAYS** reference the main gov2 README for additional details

## Common Patterns
- Use consistent section headers across all service READMEs
- Include the same warning section in all READMEs
- Use code blocks for all commands and file paths
- Maintain consistent link formatting
- Include scenario step descriptions based on SPECIFICATION.md
- Use bullet points for action lists
- Keep descriptions concise but informative