# PHP Hello Examples Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct AWS SDK for PHP client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use AWS SDK for PHP client directly, no wrapper classes needed

## File Structure
```
example_code/{service}/
├── Hello{Service}.php          # Hello example file
```

## Hello Scenario File Pattern
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

## Hello Examples by Service Type

### List-Based Services (S3, DynamoDB, etc.)
- **Operation**: List primary resources (buckets, tables, etc.)
- **Message**: Show count and names of resources

### Status-Based Services (GuardDuty, Config, etc.)  
- **Operation**: Check service status or list detectors/configurations
- **Message**: Show service availability and basic status

### Compute Services (EC2, Lambda, etc.)
- **Operation**: List instances/functions or describe regions
- **Message**: Show available resources or regions

## Validation Requirements
- ✅ **Must run without errors** (with proper credentials)
- ✅ **Must handle credential issues gracefully**
- ✅ **Must display meaningful output**
- ✅ **Must use direct AWS SDK for PHP client calls**
- ✅ **Must include proper copyright header**

## Common Patterns
- Always use `new {Service}Client()` directly
- Include comprehensive error handling with try-catch blocks
- Provide user-friendly output messages using echo
- Handle both service-specific and general exceptions
- Keep it as simple as possible - no additional classes or complexity
- Use appropriate AWS SDK for PHP v3 patterns