# PHP Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive test suites including unit tests and integration tests using PHPUnit framework with proper mocking and AWS data structures.

## Requirements
- **Complete Data**: Use complete AWS data structures in tests
- **Proper Attributes**: Use PHPUnit annotations for test categorization
- **Error Coverage**: Test all error conditions from specification
- **Mock Framework**: Use AWS MockHandler for unit tests

## File Structure
```
example_code/{service}/tests/
├── {Service}Test.php           # Unit and integration tests
└── phpunit.xml                 # PHPUnit configuration
```

## PHPUnit Configuration
**MANDATORY phpunit.xml structure:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<phpunit bootstrap="../vendor/autoload.php"
         colors="true"
         convertErrorsToExceptions="true"
         convertNoticesToExceptions="true"
         convertWarningsToExceptions="true"
         processIsolation="false"
         stopOnFailure="false">
    <testsuites>
        <testsuite name="unit">
            <directory>.</directory>
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

## Test Class Pattern
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

## Unit Test Pattern
```php
/**
 * @group unit
 * @dataProvider errorCodeProvider
 */
public function testActionWithVariousConditions(?string $errorCode): void
{
    // Arrange
    $paramValue = 'test-value';
    $expectedResponse = new Result([
        'ResponseKey' => 'response-value'
    ]);
    
    if ($errorCode === null) {
        $this->mockHandler->append($expectedResponse);
    } else {
        $this->mockHandler->append(function (CommandInterface $cmd, RequestInterface $req) use ($errorCode) {
            return new \Aws\Exception\AwsException($errorCode, $cmd);
        });
    }
    
    // Act & Assert
    if ($errorCode === null) {
        $result = $this->service->performAction($paramValue);
        $this->assertEquals('response-value', $result['ResponseKey']);
    } else {
        $this->expectException(\Aws\Exception\AwsException::class);
        $this->service->performAction($paramValue);
    }
}

public function errorCodeProvider(): array
{
    return [
        'success' => [null],
        'bad_request' => ['BadRequestException'],
        'internal_error' => ['InternalServerErrorException'],
    ];
}
```

## Integration Test Pattern
```php
/**
 * @group integ
 */
public function testServiceIntegration(): void
{
    // Arrange
    $service = new ServiceClass();
    
    // Act - This should not raise an exception
    $result = $service->listResources();
    
    // Assert - Verify result structure
    $this->assertIsArray($result);
}

/**
 * @group integ
 */
public function testResourceLifecycleIntegration(): void
{
    // Arrange
    $service = new ServiceClass();
    $resourceId = null;
    
    try {
        // Act - Create resource
        $resourceId = $service->createResource('test-resource');
        $this->assertNotNull($resourceId);
        
        // Use resource
        $result = $service->getResource($resourceId);
        $this->assertNotNull($result);
    } finally {
        // Clean up
        if ($resourceId !== null) {
            try {
                $service->deleteResource($resourceId);
            } catch (Exception $e) {
                // Ignore cleanup errors
            }
        }
    }
}
```

## Test Execution Commands

### Unit Tests
```bash
phpunit --group unit
```

### Integration Tests
```bash
phpunit --group integ
```

### All Tests
```bash
phpunit
```

## Test Requirements Checklist
- ✅ **PHPUnit configuration file created** with proper bootstrap
- ✅ **Mock framework setup** (AWS MockHandler for mocking clients)
- ✅ **Complete AWS data structures** in all tests
- ✅ **Proper PHPUnit annotations** (`@group unit`, `@group integ`)
- ✅ **Error condition coverage** per specification
- ✅ **Integration tests** for real AWS service calls
- ✅ **Data providers** for testing multiple scenarios
- ✅ **Proper cleanup** in integration tests

## Testing Standards
- **Test framework**: Use PHPUnit with appropriate annotations
- **Integration tests**: Mark with `@group integ` annotation
- **Unit tests**: Mark with `@group unit` annotation
- **Test naming**: Use descriptive method names with `test` prefix
- **Test types**: Prefer Integration test coverage
- **Resource management**: Proper cleanup in `tearDown()` methods
- **Testable Readline**: Use the `testable_readline` function instead of `readline` so tests can be passed values rather than read from stdin

## Common Test Failures to Avoid
- ❌ **Not using MockHandler** for unit tests
- ❌ **Using incomplete AWS data structures** in unit tests
- ❌ **Missing PHPUnit annotations** for test groups
- ❌ **Not testing all error conditions** from specification
- ❌ **Forgetting to set AWS region** in test clients
- ❌ **Not cleaning up resources** in integration tests
- ❌ **Missing data providers** for multiple test scenarios