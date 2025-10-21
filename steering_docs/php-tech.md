# PHP Technology Stack & Build System

## PHP 8.1+ Development Environment

### Build Tools & Dependencies
- **Runtime**: PHP 8.1 or higher
- **Package Manager**: Composer
- **Testing Framework**: PHPUnit
- **Code Formatting**: PHP_CodeSniffer (phpcs/phpcbf)
- **Linting**: PHP_CodeSniffer with custom standards
- **SDK Version**: AWS SDK for PHP v3

### Common Build Commands

```bash
# Dependencies
composer install                   # Install dependencies
composer update                    # Update dependencies

# Testing
phpunit                           # Run all tests
phpunit --testsuite <testsuite>   # Run specific test suite
phpunit --group unit              # Run unit tests
phpunit --group integ             # Run integration tests

# Code Quality
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor /path/to/lint
vendor/bin/phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor /path/to/lint

# Execution
php Runner.php                    # Run example with Runner pattern
php HelloService.php              # Run hello scenario directly
```

### PHP-Specific Pattern Requirements

#### File Naming Conventions
- Use PascalCase for class files and main example files
- Service prefix pattern: `{Service}Actions.php` (e.g., `S3Actions.php`)
- Hello scenarios: `Hello{Service}.php` (e.g., `HelloS3.php`)
- Runner files: `Runner.php` (standard entry point)
- Test files: `{Service}Test.php`

#### Hello Scenario Structure
- **File naming**: `Hello{Service}.php`
- **Structure**: Class-based approach
- **Documentation**: Include comments explaining the example purpose

#### Code Structure Standards
- **Namespace naming**: Use PascalCase with backslashes (e.g., `S3\`, `DynamoDb\`)
- **Class structure**: One public class per file matching filename
- **Method naming**: Use camelCase for method names
- **Constants**: Use UPPER_SNAKE_CASE for constants
- **Properties**: Use camelCase for properties
- **PSR-4 autoloading**: Follow PSR-4 standards for autoloading

#### Error Handling Patterns
```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use Aws\S3\S3Client;
use Aws\Exception\AwsException;
use Aws\S3\Exception\S3Exception;

class ExampleClass
{
    private S3Client $s3Client;

    public function __construct()
    {
        $this->s3Client = new S3Client([
            'region' => 'us-east-1',
            'version' => 'latest'
        ]);
    }

    public function exampleMethod(): array
    {
        try {
            $result = $this->s3Client->listBuckets();
            return $result['Buckets'];
        } catch (S3Exception $e) {
            // Handle S3-specific exceptions
            echo "S3 Error: " . $e->getAwsErrorMessage() . "\n";
            echo "Error Code: " . $e->getAwsErrorCode() . "\n";
            throw $e;
        } catch (AwsException $e) {
            // Handle general AWS exceptions
            echo "AWS Error: " . $e->getMessage() . "\n";
            throw $e;
        } catch (Exception $e) {
            // Handle general exceptions
            echo "Error: " . $e->getMessage() . "\n";
            throw $e;
        }
    }
}
```

#### Testing Standards
- **Test framework**: Use PHPUnit with appropriate annotations
- **Integration tests**: Mark with `@group integ` annotation
- **Unit tests**: Mark with `@group unit` annotation
- **Test naming**: Use descriptive method names with `test` prefix
- **Test types**: Prefer Integration test coverage
- **Resource management**: Proper cleanup in `tearDown()` methods
- **Testable Readline**: Use the `testable_readline` function instead of `readline` so tests can be passed values rather than read from stdin.

#### Project Structure
```
example_code/{service}/
├── Hello{Service}.php          # Standalone hello scenario file
├── {Service}Actions.php        # Individual action examples
├── {Service}Service.php        # Service wrapper class
├── Runner.php                  # Interactive menu runner
├── composer.json
├── README.md
└── tests/
    ├── {Service}Test.php
    └── phpunit.xml
```

#### Runner Pattern Structure
**MANDATORY for most services:**

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

require 'vendor/autoload.php';

use {Service}\{Service}Service;

echo "Welcome to the {AWS Service} examples!\n";
echo "Choose an option:\n";
echo "1. Hello {Service}\n";
echo "2. List {Resources}\n";
echo "3. Create {Resource}\n";
// ... more options

$choice = testable_readline("Enter your choice: ");

switch ($choice) {
    case '1':
        require 'Hello{Service}.php';
        break;
    case '2':
        $service = new {Service}Service();
        $service->list{Resources}();
        break;
    // ... more cases
    default:
        echo "Invalid choice\n";
}
```

**Runner Requirements:**
- ✅ **ALWAYS** provide interactive menu for multiple examples
- ✅ **ALWAYS** include hello scenario as first option
- ✅ **ALWAYS** handle user input gracefully
- ✅ **ALWAYS** include proper error handling
- ✅ **ALWAYS** provide clear output and feedback
- ✅ **ALWAYS** use `testable_readline()` instead of `readline()` for testability

#### Hello Scenario File Pattern
**MANDATORY standalone hello file:**

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

require 'vendor/autoload.php';

use Aws\{Service}\{Service}Client;
use Aws\Exception\AwsException;

echo "Hello {AWS Service}!\n";

try {
    $client = new {Service}Client([
        'region' => 'us-east-1',
        'version' => 'latest'
    ]);
    
    // Simple service operation
    $result = $client->someBasicOperation();
    echo "Successfully connected to {AWS Service}\n";
    // Display basic result
    
} catch (AwsException $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
```

#### Service Class Pattern
**MANDATORY for organized services:**

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace {Service};

use Aws\{Service}\{Service}Client;
use Aws\Exception\AwsException;

class {Service}Service
{
    private {Service}Client $client;

    public function __construct(array $config = [])
    {
        $defaultConfig = [
            'region' => 'us-east-1',
            'version' => 'latest'
        ];
        
        $this->client = new {Service}Client(array_merge($defaultConfig, $config));
    }

    // Service action methods (NOT hello method)...
}
```

#### Documentation Requirements
- **File headers**: MANDATORY copyright header: `<?php\n// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n// SPDX-License-Identifier: Apache-2.0`
- **Class documentation**: Use PHPDoc blocks for classes
- **Method documentation**: Document parameters, return values, and exceptions
- **Inline comments**: Explain complex AWS service interactions
- **README sections**: Include Composer setup and execution instructions

#### Composer Configuration Pattern
**MANDATORY for every service directory:**

```json
{
    "require": {
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
    }
}
```

**Composer Requirements:**
- ✅ **ALWAYS** specify minimum AWS SDK version
- ✅ **ALWAYS** include ext-readline for interactive examples
- ✅ **ALWAYS** set up PSR-4 autoloading
- ✅ **ALWAYS** include PHPUnit for testing

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER omit the mandatory copyright header from PHP files**
- ❌ **NEVER use snake_case for PHP class names**
- ❌ **NEVER forget to include proper autoloading**
- ❌ **NEVER ignore proper exception handling for AWS operations**
- ❌ **NEVER skip Composer dependency management**
- ❌ **NEVER create examples without Runner.php pattern**

### Best Practices
- ✅ **ALWAYS include the mandatory copyright header in every PHP file**
- ✅ **ALWAYS follow the established Runner.php pattern for interactive examples**
- ✅ **ALWAYS use PascalCase for class names and camelCase for methods**
- ✅ **ALWAYS include proper exception handling for AWS service calls**
- ✅ **ALWAYS use Composer for dependency management**
- ✅ **ALWAYS include comprehensive PHPDoc documentation**
- ✅ **ALWAYS follow PSR-4 autoloading standards**
- ✅ **ALWAYS provide interactive user experience through Runner pattern**

### AWS SDK v3 Specific Patterns

#### Client Configuration
```php
use Aws\S3\S3Client;
use Aws\Credentials\CredentialProvider;

// Basic client setup
$client = new S3Client([
    'region' => 'us-east-1',
    'version' => 'latest'
]);

// With explicit credentials
$client = new S3Client([
    'region' => 'us-east-1',
    'version' => 'latest',
    'credentials' => CredentialProvider::defaultProvider()
]);
```

#### Async Operations
```php
use GuzzleHttp\Promise;

// Async operation example
$promise = $client->listBucketsAsync();
$promise->then(
    function ($result) {
        echo "Success: " . count($result['Buckets']) . " buckets found\n";
    },
    function ($reason) {
        echo "Error: " . $reason->getMessage() . "\n";
    }
);

$promise->wait();
```

### Testing Infrastructure

#### PHPUnit Configuration
**MANDATORY phpunit.xml structure:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<phpunit bootstrap="vendor/autoload.php"
         colors="true"
         convertErrorsToExceptions="true"
         convertNoticesToExceptions="true"
         convertWarningsToExceptions="true"
         processIsolation="false"
         stopOnFailure="false">
    <testsuites>
        <testsuite name="unit">
            <directory>tests</directory>
        </testsuite>
    </testsuites>
    <groups>
        <include>
            <group>unit</group>
            <group>integ</group>
        </include>
    </groups>
</phpunit>
```

#### Test Class Pattern
```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CommandInterface;
use Psr\Http\Message\RequestInterface;

class ServiceTest extends TestCase
{
    private $mockHandler;
    private $service;

    protected function setUp(): void
    {
        $this->mockHandler = new MockHandler();
        $this->service = new ServiceClass([
            'handler' => $this->mockHandler,
            'region' => 'us-east-1'
        ]);
    }

    /**
     * @group unit
     */
    public function testHelloService(): void
    {
        $this->mockHandler->append(new Result(['Buckets' => []]));
        
        $result = $this->service->helloService();
        
        $this->assertIsArray($result);
    }

    /**
     * @group integ
     */
    public function testIntegrationExample(): void
    {
        // Integration test with real AWS service
        $result = $this->service->realServiceCall();
        $this->assertNotNull($result);
    }
}
```

### Code Quality Standards

#### PHPCS Configuration
- **Standard**: Custom phpcs.xml configuration in `.github/linters/`
- **Extensions**: PHP files only
- **Ignore**: vendor directories
- **Auto-fix**: Use phpcbf for automatic fixes

#### Required Code Style
- **PSR-12**: Follow PSR-12 coding standard
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum
- **Braces**: Opening braces on same line for methods, new line for classes

### Integration with Knowledge Base
Before creating PHP code examples:
1. Use `ListKnowledgeBases` to discover available knowledge bases
2. Query `coding-standards-KB` for "PHP-code-example-standards" (if available)
3. Query available language-specific knowledge bases for "PHP implementation patterns"
4. Follow KB-documented patterns for Composer structure and class organization
5. Validate against existing PHP examples only after KB consultation

### Environment Configuration
- **AWS Region**: Support region configuration through client constructor
- **Credentials**: Support AWS credential chain (environment, profile, IAM roles)
- **Configuration**: Use environment variables and configuration arrays

### 🚨 MANDATORY COMPLETION CHECKLIST

**❌ WORK IS NOT COMPLETE UNTIL ALL THESE COMMANDS PASS:**

```bash
# 1. MANDATORY: Dependencies must be installed
composer install

# 2. MANDATORY: Unit tests must pass
phpunit --group unit

# 3. MANDATORY: Integration tests must pass (if applicable)
phpunit --group integ

# 4. MANDATORY: Code formatting must be applied
vendor/bin/phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# 5. MANDATORY: Linting must pass
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# 6. MANDATORY: ALL EXAMPLE FILES MUST BE EXECUTED TO VALIDATE CREATION
php hello{Service}.php
php Runner.php
# Test service class instantiation
php -r "require 'vendor/autoload.php'; use {Service}\{Service}Service; \$service = new {Service}Service(); echo '✅ Service class working\n';"
```

**🚨 CRITICAL**: If ANY of these commands fail, the work is INCOMPLETE and must be fixed.

**🚨 MANDATORY VALIDATION REQUIREMENT**: 
- **ALL generated example files MUST be executed successfully to validate their creation**
- **Hello examples MUST run without errors and display expected output**
- **Runner examples MUST run interactively and provide menu options**
- **Service classes MUST be instantiable and functional**
- **Any runtime errors or autoloading failures indicate incomplete implementation**

### Common PHP Patterns to Follow

#### Interactive Examples
- ✅ **ALWAYS** use `testable_readline()` for user input in interactive examples (enables testing)
- ✅ **ALWAYS** provide clear prompts and instructions
- ✅ **ALWAYS** validate user input appropriately
- ✅ **ALWAYS** handle user cancellation gracefully

#### Output Formatting
- ✅ **ALWAYS** provide clear, formatted output
- ✅ **ALWAYS** use appropriate formatting for different data types
- ✅ **ALWAYS** include success/failure indicators
- ✅ **ALWAYS** provide meaningful error messages

#### Resource Management
- ✅ **ALWAYS** properly handle AWS resource cleanup
- ✅ **ALWAYS** provide options to clean up created resources
- ✅ **ALWAYS** warn users about potential AWS charges
- ✅ **ALWAYS** implement proper error recovery
