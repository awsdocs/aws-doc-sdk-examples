# PHP Service Wrapper Classes

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns wrapper")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate service wrapper classes that encapsulate AWS SDK operations with proper error handling, logging, and user-friendly interfaces.

## Requirements
- **Encapsulation**: Wrap AWS SDK client operations
- **Error Handling**: Comprehensive exception handling with user-friendly messages
- **Logging**: Optional logging for debugging and monitoring
- **Testability**: Support dependency injection for testing
- **Documentation**: Complete PHPDoc documentation

## File Structure
```
example_code/{service}/
├── {Service}Service.php        # Service wrapper class
├── {Service}Actions.php        # Individual action examples (optional)
```

## Service Class Pattern
**MANDATORY for organized services:**

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace {Service};

use Aws\{Service}\{Service}Client;
use Aws\Exception\AwsException;
use Aws\{Service}\Exception\{Service}Exception;

/**
 * Service wrapper class for {AWS Service} operations.
 * 
 * This class provides a simplified interface for common {AWS Service} operations
 * with proper error handling and user-friendly messages.
 */
class {Service}Service
{
    private {Service}Client $client;

    /**
     * Constructor for {Service}Service.
     *
     * @param array $config Optional configuration array for the AWS client
     */
    public function __construct(array $config = [])
    {
        $defaultConfig = [
            'region' => 'us-east-1',
            'version' => 'latest'
        ];
        
        $this->client = new {Service}Client(array_merge($defaultConfig, $config));
    }

    /**
     * List all resources in the service.
     *
     * @return array Array of resource information
     * @throws {Service}Exception When the operation fails
     */
    public function listResources(): array
    {
        try {
            $result = $this->client->listResources();
            return $result['Resources'] ?? [];
        } catch ({Service}Exception $e) {
            $this->handleServiceException($e, 'listing resources');
            throw $e;
        } catch (AwsException $e) {
            $this->handleAwsException($e, 'listing resources');
            throw $e;
        }
    }

    /**
     * Create a new resource.
     *
     * @param string $resourceName Name for the new resource
     * @param array $options Additional options for resource creation
     * @return string The ID of the created resource
     * @throws {Service}Exception When the operation fails
     */
    public function createResource(string $resourceName, array $options = []): string
    {
        try {
            $params = array_merge([
                'ResourceName' => $resourceName
            ], $options);
            
            $result = $this->client->createResource($params);
            
            echo "✓ Created resource: {$resourceName}\n";
            return $result['ResourceId'];
        } catch ({Service}Exception $e) {
            $this->handleServiceException($e, "creating resource '{$resourceName}'");
            throw $e;
        } catch (AwsException $e) {
            $this->handleAwsException($e, "creating resource '{$resourceName}'");
            throw $e;
        }
    }

    /**
     * Get detailed information about a resource.
     *
     * @param string $resourceId The ID of the resource
     * @return array Resource details
     * @throws {Service}Exception When the operation fails
     */
    public function getResource(string $resourceId): array
    {
        try {
            $result = $this->client->getResource([
                'ResourceId' => $resourceId
            ]);
            
            return $result['Resource'];
        } catch ({Service}Exception $e) {
            $this->handleServiceException($e, "getting resource '{$resourceId}'");
            throw $e;
        } catch (AwsException $e) {
            $this->handleAwsException($e, "getting resource '{$resourceId}'");
            throw $e;
        }
    }

    /**
     * Delete a resource.
     *
     * @param string $resourceId The ID of the resource to delete
     * @return bool True if deletion was successful
     * @throws {Service}Exception When the operation fails
     */
    public function deleteResource(string $resourceId): bool
    {
        try {
            $this->client->deleteResource([
                'ResourceId' => $resourceId
            ]);
            
            echo "✓ Deleted resource: {$resourceId}\n";
            return true;
        } catch ({Service}Exception $e) {
            $this->handleServiceException($e, "deleting resource '{$resourceId}'");
            throw $e;
        } catch (AwsException $e) {
            $this->handleAwsException($e, "deleting resource '{$resourceId}'");
            throw $e;
        }
    }

    /**
     * Handle service-specific exceptions with user-friendly messages.
     *
     * @param {Service}Exception $e The exception to handle
     * @param string $operation Description of the operation that failed
     */
    private function handleServiceException({Service}Exception $e, string $operation): void
    {
        $errorCode = $e->getAwsErrorCode();
        $message = match ($errorCode) {
            'ResourceNotFoundException' => "The resource was not found while {$operation}.",
            'InvalidParameterException' => "Invalid parameters provided while {$operation}.",
            'AccessDeniedException' => "Access denied while {$operation}. Check your permissions.",
            'ThrottlingException' => "Request was throttled while {$operation}. Please retry.",
            default => "Service error while {$operation}: " . $e->getAwsErrorMessage()
        };
        
        echo "✗ Error: {$message}\n";
    }

    /**
     * Handle general AWS exceptions with user-friendly messages.
     *
     * @param AwsException $e The exception to handle
     * @param string $operation Description of the operation that failed
     */
    private function handleAwsException(AwsException $e, string $operation): void
    {
        echo "✗ AWS Error while {$operation}: " . $e->getMessage() . "\n";
    }
}
```

## Actions Class Pattern (Optional)
For services with many individual operations, create separate action classes:

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace {Service};

use Aws\{Service}\{Service}Client;
use Aws\Exception\AwsException;

/**
 * Individual action examples for {AWS Service}.
 * 
 * This class demonstrates specific operations that can be performed
 * with {AWS Service}.
 */
class {Service}Actions
{
    private {Service}Client $client;

    public function __construct({Service}Client $client = null)
    {
        $this->client = $client ?? new {Service}Client([
            'region' => 'us-east-1',
            'version' => 'latest'
        ]);
    }

    /**
     * Example action method.
     *
     * @param string $parameter Example parameter
     * @return array Result of the operation
     */
    public function exampleAction(string $parameter): array
    {
        try {
            $result = $this->client->someOperation([
                'Parameter' => $parameter
            ]);
            
            echo "✓ Operation completed successfully\n";
            return $result->toArray();
        } catch (AwsException $e) {
            echo "✗ Error: " . $e->getMessage() . "\n";
            throw $e;
        }
    }
}
```

## Error Handling Patterns

### Service-Specific Error Handling
```php
private function handleServiceException({Service}Exception $e, string $operation): void
{
    $errorCode = $e->getAwsErrorCode();
    
    switch ($errorCode) {
        case 'BadRequestException':
            echo "✗ Invalid request while {$operation}. Please check your parameters.\n";
            break;
        case 'InternalServerErrorException':
            echo "✗ Service temporarily unavailable while {$operation}. Please retry.\n";
            break;
        case 'UnauthorizedOperation':
            echo "✗ You don't have permission for {$operation}.\n";
            break;
        default:
            echo "✗ Service error while {$operation}: " . $e->getAwsErrorMessage() . "\n";
    }
}
```

### Retry Logic Pattern
```php
/**
 * Perform operation with retry logic.
 *
 * @param callable $operation The operation to perform
 * @param int $maxRetries Maximum number of retries
 * @return mixed Result of the operation
 */
private function performWithRetry(callable $operation, int $maxRetries = 3)
{
    $attempt = 0;
    
    while ($attempt < $maxRetries) {
        try {
            return $operation();
        } catch ({Service}Exception $e) {
            $attempt++;
            
            if ($e->getAwsErrorCode() === 'ThrottlingException' && $attempt < $maxRetries) {
                $delay = pow(2, $attempt); // Exponential backoff
                echo "⚠ Throttled. Retrying in {$delay} seconds...\n";
                sleep($delay);
                continue;
            }
            
            throw $e;
        }
    }
}
```

## Wrapper Class Requirements
- ✅ **ALWAYS** include comprehensive PHPDoc documentation
- ✅ **ALWAYS** use proper namespace matching service name
- ✅ **ALWAYS** implement constructor with configurable client
- ✅ **ALWAYS** include user-friendly error handling
- ✅ **ALWAYS** provide meaningful success/failure messages
- ✅ **ALWAYS** support dependency injection for testing
- ✅ **ALWAYS** follow PSR-4 autoloading standards
- ✅ **ALWAYS** include proper exception handling for all methods

## Method Naming Conventions
- Use camelCase for method names
- Use descriptive names that indicate the operation
- Prefix with action verb (create, get, list, delete, update)
- Include resource type in method name when appropriate

## Documentation Standards
- **Class documentation**: Describe the purpose and main functionality
- **Method documentation**: Include @param, @return, and @throws tags
- **Parameter documentation**: Describe each parameter's purpose and type
- **Exception documentation**: List all possible exceptions that can be thrown
- **Usage examples**: Include code examples in documentation when helpful