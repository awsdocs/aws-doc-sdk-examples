# PHP README/WRITEME and Documentation Generation

## Purpose
Generate and update README files and documentation using the writeme tool to ensure consistency and completeness.

## Requirements
- **Automated Generation**: Use writeme tool for README generation
- **Metadata Dependency**: Requires complete metadata files
- **Virtual Environment**: Run writeme in isolated environment
- **Validation**: Ensure all documentation is up-to-date

## File Structure
```
php/example_code/{service}/
├── README.md                   # Generated service README
├── composer.json               # Dependencies
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
python -m writeme --languages PHP:3 --services {service}
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

## Documentation Dependencies

### Required Files for README Generation
- ✅ **Metadata file**: `.doc_gen/metadata/{service}_metadata.yaml`
- ✅ **Code files**: All referenced PHP files must exist
- ✅ **Snippet tags**: All snippet tags in metadata must exist in code
- ✅ **Composer file**: `composer.json` with dependencies

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
python -m writeme --languages PHP:3 --services {service} --verbose

# Validate specific metadata file
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Check for missing snippet tags
grep -r "snippet-start" php/example_code/{service}/
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

## Integration with CI/CD

### Automated README Validation
```bash
# In CI/CD pipeline, validate README is up-to-date
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages PHP:3 --services {service} --check

# Exit with error if README needs updates
if git diff --exit-code php/example_code/{service}/README.md; then
    echo "README is up-to-date"
else
    echo "README needs to be regenerated"
    exit 1
fi
```

This ensures documentation stays synchronized with code changes.