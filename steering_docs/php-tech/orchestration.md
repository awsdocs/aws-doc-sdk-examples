# PHP Build and Orchestration

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns orchestration")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Define build processes, dependency management, code quality checks, and execution workflows for PHP AWS SDK examples.

## Build Tools & Dependencies
- **Runtime**: PHP 8.1 or higher
- **Package Manager**: Composer
- **Testing Framework**: PHPUnit
- **Code Formatting**: PHP_CodeSniffer (phpcs/phpcbf)
- **Linting**: PHP_CodeSniffer with custom standards
- **SDK Version**: AWS SDK for PHP v3

## Common Build Commands

```bash
# Dependencies
composer install                   # Install dependencies
composer update                    # Update dependencies
composer dump-autoload            # Regenerate autoloader

# Testing
phpunit                           # Run all tests
phpunit --testsuite unit          # Run unit tests
phpunit --group unit              # Run unit tests by group
phpunit --group integ             # Run integration tests
composer test                     # Run tests via composer script
composer test-unit                # Run unit tests via composer script
composer test-integ               # Run integration tests via composer script

# Code Quality
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./
vendor/bin/phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# Execution
php Runner.php                    # Run example with Runner pattern
php Hello{Service}.php            # Run hello scenario directly
php {Service}Actions.php          # Run individual actions
```

## 🚨 MANDATORY COMPLETION CHECKLIST

**❌ WORK IS NOT COMPLETE UNTIL ALL THESE COMMANDS PASS:**

```bash
# 1. MANDATORY: Dependencies must be installed
composer install

# 2. MANDATORY: Composer configuration must be valid
composer validate

# 3. MANDATORY: Autoloading must work
composer dump-autoload

# 4. MANDATORY: Unit tests must pass
phpunit --group unit

# 5. MANDATORY: Integration tests must pass (if applicable)
phpunit --group integ

# 6. MANDATORY: Code formatting must be applied
vendor/bin/phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# 7. MANDATORY: Linting must pass
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# 8. MANDATORY: ALL EXAMPLE FILES MUST BE EXECUTED TO VALIDATE CREATION
php Hello{Service}.php
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

## Dependency Management

### Core Dependencies
```json
{
    "require": {
        "php": "^8.1",
        "aws/aws-sdk-php": "^3.209",
        "ext-readline": "*"
    }
}
```

### Development Dependencies
```json
{
    "require-dev": {
        "phpunit/phpunit": "^9.5",
        "squizlabs/php_codesniffer": "^3.6"
    }
}
```

### Composer Scripts
```json
{
    "scripts": {
        "test": "phpunit",
        "test-unit": "phpunit --group unit",
        "test-integ": "phpunit --group integ",
        "lint": "phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./",
        "lint-fix": "phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./"
    }
}
```

## Code Quality Standards

### PHPCS Configuration
- **Standard**: Custom phpcs.xml configuration in `.github/linters/`
- **Extensions**: PHP files only
- **Ignore**: vendor directories
- **Auto-fix**: Use phpcbf for automatic fixes

### Required Code Style
- **PSR-12**: Follow PSR-12 coding standard
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum
- **Braces**: Opening braces on same line for methods, new line for classes

### Linting Commands
```bash
# Check code style
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# Fix code style automatically
vendor/bin/phpcbf --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./
```

## Testing Orchestration

### Test Execution Order
1. **Unit Tests**: Fast, isolated tests with mocks
2. **Integration Tests**: Real AWS service calls
3. **Code Quality**: Linting and formatting checks

### Test Commands
```bash
# Run all tests
composer test

# Run specific test groups
composer test-unit
composer test-integ

# Run tests with coverage (if configured)
phpunit --coverage-html coverage/

# Run specific test file
phpunit tests/ServiceTest.php
```

## Execution Workflows

### Interactive Execution
```bash
# Primary entry point
php Runner.php

# Direct hello execution
php Hello{Service}.php

# Service class testing
php -r "
require 'vendor/autoload.php';
use {Service}\{Service}Service;
\$service = new {Service}Service();
var_dump(\$service->listResources());
"
```

### Automated Execution
```bash
# Non-interactive testing
PHPUNIT_TESTING=1 php Runner.php

# Batch execution with input
echo -e "1\n0\n" | php Runner.php
```

## Environment Configuration

### AWS Configuration
- **Credentials**: Support AWS credential chain (environment, profile, IAM roles)
- **Region**: Configurable through client constructor or environment
- **Endpoint**: Support custom endpoints for testing

### PHP Configuration
```bash
# Required PHP extensions
php -m | grep -E "(curl|json|mbstring|openssl|readline)"

# Memory and execution limits
php -d memory_limit=256M -d max_execution_time=300 Runner.php
```

## Build Validation

### Pre-commit Checks
```bash
#!/bin/bash
# Pre-commit validation script

echo "Running PHP validation..."

# 1. Syntax check
find . -name "*.php" -exec php -l {} \; | grep -v "No syntax errors"

# 2. Composer validation
composer validate --strict

# 3. Code style check
vendor/bin/phpcs --standard=../.github/linters/phpcs.xml --extensions=php --ignore=vendor ./

# 4. Unit tests
phpunit --group unit

echo "✅ All checks passed"
```

### Continuous Integration
```yaml
# Example GitHub Actions workflow
name: PHP Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup PHP
        uses: shivammathur/setup-php@v2
        with:
          php-version: '8.1'
          extensions: curl, json, mbstring, openssl, readline
      - name: Install dependencies
        run: composer install
      - name: Run tests
        run: composer test
      - name: Check code style
        run: composer lint
```

## Performance Considerations

### Memory Management
- Use appropriate memory limits for large datasets
- Implement pagination for list operations
- Clean up resources properly in finally blocks

### Execution Time
- Set appropriate timeout values for AWS operations
- Implement retry logic with exponential backoff
- Use async operations when available

## Error Handling in Build Process

### Common Build Failures
- **Composer install fails**: Check PHP version and extensions
- **Tests fail**: Verify AWS credentials and permissions
- **Linting fails**: Run phpcbf to auto-fix style issues
- **Autoloading fails**: Run composer dump-autoload

### Debugging Commands
```bash
# Debug composer issues
composer diagnose

# Debug autoloading
composer dump-autoload -v

# Debug PHP configuration
php --ini
php -m

# Debug AWS credentials
aws sts get-caller-identity
```