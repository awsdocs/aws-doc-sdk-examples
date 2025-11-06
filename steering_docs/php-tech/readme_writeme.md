# PHP README and Documentation Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns documentation")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive README documentation for PHP AWS SDK examples with setup instructions, usage examples, and troubleshooting guidance.

## README Structure Template

```markdown
# {AWS Service} examples for the SDK for PHP

## Overview

This section contains examples demonstrating how to use the AWS SDK for PHP with {AWS Service}.

{AWS Service} is {brief service description and primary use cases}.

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

For general prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.

### PHP Version
* PHP 8.1 or later
* Composer for dependency management

### AWS SDK for PHP
* AWS SDK for PHP v3.209 or later

### AWS Credentials
Configure your AWS credentials using one of the following methods:
* AWS credentials file
* Environment variables
* IAM roles (for EC2 instances)
* AWS CLI configuration

## Setup

1. **Install dependencies**:
   ```bash
   composer install
   ```

2. **Configure AWS credentials** (if not already configured):
   ```bash
   aws configure
   ```

3. **Verify setup**:
   ```bash
   php Hello{Service}.php
   ```

## Code examples

### Get started

* [Hello {Service}](Hello{Service}.php) - Demonstrates basic connectivity to {AWS Service}

### Scenarios

* [Run {Service} basics scenario](Runner.php) - Interactive scenario demonstrating common {AWS Service} operations

### Actions

The following examples show you how to perform individual {AWS Service} actions:

* [List {resources}]({Service}Actions.php#L{line}) - `{APIOperation}`
* [Create {resource}]({Service}Actions.php#L{line}) - `{APIOperation}`
* [Get {resource} details]({Service}Actions.php#L{line}) - `{APIOperation}`
* [Update {resource}]({Service}Actions.php#L{line}) - `{APIOperation}`
* [Delete {resource}]({Service}Actions.php#L{line}) - `{APIOperation}`

## Running the examples

### Hello {Service}
The simplest way to get started is with the Hello example:

```bash
php Hello{Service}.php
```

This example demonstrates basic connectivity and lists available {resources}.

### Interactive Runner
For a guided experience with multiple operations:

```bash
php Runner.php
```

This provides an interactive menu with the following options:
1. Hello {Service} - Basic connectivity test
2. Run {Service} basics scenario - Complete workflow demonstration
3. List {resources} - Show available resources
4. Create {resource} - Create a new resource
5. Delete {resource} - Remove a resource

### Individual Actions
You can also run specific actions directly:

```bash
# List resources
php -r "
require 'vendor/autoload.php';
use {Service}\{Service}Service;
\$service = new {Service}Service();
\$resources = \$service->listResources();
foreach (\$resources as \$resource) {
    echo \$resource['Name'] . \"\n\";
}
"
```

## Testing

### Unit Tests
Run unit tests with mocked AWS responses:

```bash
composer test-unit
# or
phpunit --group unit
```

### Integration Tests
Run integration tests against real AWS services:

```bash
composer test-integ
# or
phpunit --group integ
```

**Note**: Integration tests will make actual AWS API calls and may incur charges.

### All Tests
Run both unit and integration tests:

```bash
composer test
# or
phpunit
```

## Code Quality

### Linting
Check code style compliance:

```bash
composer lint
# or
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./
```

### Auto-fix Code Style
Automatically fix code style issues:

```bash
composer lint-fix
# or
vendor/bin/phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./
```

## Troubleshooting

### Common Issues

#### Composer Install Fails
```bash
# Check PHP version
php --version

# Check required extensions
php -m | grep -E "(curl|json|mbstring|openssl|readline)"

# Update Composer
composer self-update
```

#### AWS Credentials Not Found
```bash
# Check AWS configuration
aws configure list

# Test credentials
aws sts get-caller-identity

# Set environment variables
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_DEFAULT_REGION=us-east-1
```

#### Permission Denied Errors
Ensure your AWS credentials have the necessary permissions for {AWS Service}:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "{service}:List*",
                "{service}:Get*",
                "{service}:Describe*",
                "{service}:Create*",
                "{service}:Update*",
                "{service}:Delete*"
            ],
            "Resource": "*"
        }
    ]
}
```

#### Service-Specific Errors
* **ResourceNotFoundException**: The specified resource doesn't exist
* **InvalidParameterException**: Check parameter values and formats
* **ThrottlingException**: Reduce request rate or implement retry logic
* **AccessDeniedException**: Verify IAM permissions

### Debug Mode
Enable debug output for troubleshooting:

```bash
# Set debug environment variable
export AWS_DEBUG=1
php Hello{Service}.php

# Or enable in code
\$client = new {Service}Client([
    'region' => 'us-east-1',
    'version' => 'latest',
    'debug' => true
]);
```

## Additional resources

* [{AWS Service} User Guide](https://docs.aws.amazon.com/{service}/latest/userguide/)
* [{AWS Service} API Reference](https://docs.aws.amazon.com/{service}/latest/APIReference/)
* [AWS SDK for PHP Documentation](https://docs.aws.amazon.com/sdk-for-php/)
* [AWS SDK for PHP API Reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
```

## Documentation Requirements

### Essential Sections
- ✅ **Overview**: Clear service description and use cases
- ✅ **Prerequisites**: PHP version, SDK version, credentials
- ✅ **Setup**: Step-by-step installation and configuration
- ✅ **Code Examples**: Links to all example files with descriptions
- ✅ **Running Examples**: Clear execution instructions
- ✅ **Testing**: Unit and integration test instructions
- ✅ **Troubleshooting**: Common issues and solutions
- ✅ **Additional Resources**: Links to AWS documentation

### Code Example Documentation
- **Hello Examples**: Basic connectivity demonstration
- **Scenarios**: Complete workflow examples
- **Actions**: Individual API operation examples
- **Service Classes**: Wrapper class usage examples

### Setup Instructions
- **Dependencies**: Composer install process
- **Credentials**: Multiple configuration methods
- **Verification**: Test setup with Hello example
- **Environment**: PHP and extension requirements

### Troubleshooting Guide
- **Common Errors**: Installation, credentials, permissions
- **Debug Methods**: Environment variables, client configuration
- **Service Errors**: Specific error codes and solutions
- **Performance**: Memory and timeout considerations

### Testing Documentation
- **Unit Tests**: Mock-based testing approach
- **Integration Tests**: Real AWS service testing
- **Code Quality**: Linting and formatting tools
- **CI/CD**: Automated testing workflows

## README Generation Checklist
- ✅ **Service-specific content** customized for the AWS service
- ✅ **Working code examples** with actual file references
- ✅ **Complete setup instructions** from prerequisites to execution
- ✅ **Comprehensive troubleshooting** covering common issues
- ✅ **Testing instructions** for both unit and integration tests
- ✅ **Code quality guidelines** with linting commands
- ✅ **Resource links** to official AWS documentation
- ✅ **Copyright and license** information included