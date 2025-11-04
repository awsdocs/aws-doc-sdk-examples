# PHP Metadata and Project Structure

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns metadata")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate proper project structure, Composer configuration, and metadata files for PHP AWS SDK examples.

## File Structure
```
example_code/{service}/
├── Hello{Service}.php          # Standalone hello scenario file
├── {Service}Actions.php        # Individual action examples
├── {Service}Service.php        # Service wrapper class
├── Runner.php                  # Interactive menu runner
├── composer.json               # Composer configuration
├── README.md                   # Service documentation
└── tests/
    ├── {Service}Test.php       # Unit and integration tests
    └── phpunit.xml             # PHPUnit configuration
```

## Composer Configuration Pattern
**MANDATORY for every service directory:**

```json
{
    "name": "awsdocs/{service}-examples",
    "description": "AWS SDK for PHP examples for {AWS Service}",
    "type": "library",
    "license": "Apache-2.0",
    "authors": [
        {
            "name": "AWS Documentation Team",
            "email": "aws-doc-sdk-examples@amazon.com"
        }
    ],
    "require": {
        "php": "^8.1",
        "aws/aws-sdk-php": "^3.209",
        "ext-readline": "*"
    },
    "require-dev": {
        "phpunit/phpunit": "^9.5"
    },
    "autoload": {
        "psr-4": {
            "{Service}\\": "./"
        }
    },
    "autoload-dev": {
        "psr-4": {
            "{Service}\\Tests\\": "tests/"
        }
    },
    "scripts": {
        "test": "phpunit",
        "test-unit": "phpunit --group unit",
        "test-integ": "phpunit --group integ"
    }
}
```

## File Naming Conventions
- Use PascalCase for class files and main example files
- Service prefix pattern: `{Service}Actions.php` (e.g., `S3Actions.php`)
- Hello scenarios: `Hello{Service}.php` (e.g., `HelloS3.php`)
- Runner files: `Runner.php` (standard entry point)
- Test files: `{Service}Test.php`
- Service wrapper: `{Service}Service.php`

## Code Structure Standards
- **Namespace naming**: Use PascalCase with backslashes (e.g., `S3\`, `DynamoDb\`)
- **Class structure**: One public class per file matching filename
- **Method naming**: Use camelCase for method names
- **Constants**: Use UPPER_SNAKE_CASE for constants
- **Properties**: Use camelCase for properties
- **PSR-4 autoloading**: Follow PSR-4 standards for autoloading

## Copyright Header Pattern
**MANDATORY for every PHP file:**

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
```

## Namespace Structure
```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace {Service};

use Aws\{Service}\{Service}Client;
use Aws\Exception\AwsException;
use Aws\{Service}\Exception\{Service}Exception;

class {Service}Service
{
    // Class implementation
}
```

## Composer Requirements
- ✅ **ALWAYS** specify minimum PHP version (8.1+)
- ✅ **ALWAYS** specify minimum AWS SDK version
- ✅ **ALWAYS** include ext-readline for interactive examples
- ✅ **ALWAYS** set up PSR-4 autoloading
- ✅ **ALWAYS** include PHPUnit for testing
- ✅ **ALWAYS** include test scripts for convenience

## Project Metadata Standards
- **Package naming**: Use `awsdocs/{service}-examples` format
- **License**: Always use Apache-2.0
- **Description**: Clear description of what the examples demonstrate
- **Authors**: Use AWS Documentation Team as author
- **Scripts**: Include convenience scripts for testing

## Directory Organization
- **Root level**: Main example files and configuration
- **tests/**: All test files and PHPUnit configuration
- **No subdirectories**: Keep structure flat for simplicity

## PSR-4 Autoloading Configuration
```json
"autoload": {
    "psr-4": {
        "{Service}\\": "./"
    }
},
"autoload-dev": {
    "psr-4": {
        "{Service}\\Tests\\": "tests/"
    }
}
```

## Version Requirements
- **PHP**: Minimum 8.1 for modern language features
- **AWS SDK**: Minimum 3.209 for latest features and security
- **PHPUnit**: Version 9.5+ for testing framework
- **Extensions**: ext-readline for interactive functionality

## Metadata Validation
- ✅ **composer.json validates** with `composer validate`
- ✅ **Autoloading works** with `composer dump-autoload`
- ✅ **Dependencies install** with `composer install`
- ✅ **Tests run** with `composer test`
- ✅ **PSR-4 compliance** verified