# Kotlin Hello Examples Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Kotlin-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Kotlin-premium-KB", "Kotlin implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct AWS SDK for Kotlin client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use AWS SDK client directly, no actions classes needed
- **Suspend Functions**: Use coroutines for all AWS operations

## File Structure
```
kotlin/services/{service}/
├── src/main/kotlin/com/kotlin/{service}/
│   └── Hello{Service}.kt           # Hello example file
```

## Hello Example Pattern
```kotlin
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.{service}

import aws.sdk.kotlin.services.{service}.{Service}Client
import aws.sdk.kotlin.services.{service}.model.*
import aws.smithy.kotlin.runtime.ClientException
import kotlin.system.exitProcess

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 *
 * This example shows how to get started with {AWS Service} by {basic operation description}.
 */

// snippet-start:[{service}.kotlin.hello.main]
suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
           <region>

        Where:
           region - The AWS region (for example, us-east-1).
        """.trimIndent()

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val region = args[0]
    hello{Service}(region)
}

/**
 * Invokes a {AWS Service} operation to {basic operation description}.
 *
 * @param region the AWS region to use for the operation
 */
suspend fun hello{Service}(region: String) {
    {Service}Client { this.region = region }.use { {service}Client ->
        try {
            // Perform the most basic operation for this service
            val request = {BasicOperation}Request {
                // Add any required parameters
            }

            val response = {service}Client.{basicOperation}(request)

            println("🔍 Hello, {AWS Service}!")
            
            // Display appropriate result information
            response.{resultField}?.let { results ->
                if (results.isNotEmpty()) {
                    println("Found ${results.size} {resources}:")
                    results.forEach { resource ->
                        println("  - ${resource.{resourceName}}")
                    }
                } else {
                    println("No {resources} found, but {AWS Service} is available and ready to use.")
                }
            } ?: run {
                println("{AWS Service} is available and ready to use.")
            }

        } catch (e: {Service}Exception) {
            println("{AWS Service} error occurred: ${e.message}")
            when (e.errorDetails?.errorCode) {
                "UnauthorizedOperation" -> {
                    println("You don't have permission to access {AWS Service}.")
                    println("   Please check your IAM permissions.")
                }
                "InvalidParameterValue" -> {
                    println("Invalid parameter provided to {AWS Service}.")
                    println("   Please check your input parameters.")
                }
                else -> {
                    println("Please check your AWS credentials and region configuration.")
                }
            }
            exitProcess(1)
        } catch (e: ClientException) {
            println("Client error occurred: ${e.message}")
            println("Please check your AWS credentials and network connectivity.")
            exitProcess(1)
        }
    }
}
// snippet-end:[{service}.kotlin.hello.main]
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

## Service-Specific Hello Examples

### For Services with List Operations
```kotlin
suspend fun hello{Service}(region: String) {
    {Service}Client { this.region = region }.use { {service}Client ->
        try {
            val request = List{Resources}Request { }
            val response = {service}Client.list{Resources}(request)

            println("Hello, {AWS Service}!")
            
            response.{resources}?.let { resources ->
                println("Found ${resources.size} {resources}:")
                resources.forEach { resource ->
                    println("  - ${resource.{resourceName}}")
                }
            }

        } catch (e: {Service}Exception) {
            println("{AWS Service} error: ${e.message}")
            exitProcess(1)
        }
    }
}
```

### For Services with Status Operations
```kotlin
suspend fun hello{Service}(region: String) {
    {Service}Client { this.region = region }.use { {service}Client ->
        try {
            val request = Get{Service}StatusRequest { }
            val response = {service}Client.get{Service}Status(request)

            println("Hello, {AWS Service}!")
            println("Service status: ${response.status}")
            println("{AWS Service} is ready to use.")

        } catch (e: {Service}Exception) {
            println("{AWS Service} error: ${e.message}")
            exitProcess(1)
        }
    }
}
```

### For Services with Describe Operations
```kotlin
suspend fun hello{Service}(region: String) {
    {Service}Client { this.region = region }.use { {service}Client ->
        try {
            val request = Describe{Resources}Request {
                maxResults = 10
            }
            val response = {service}Client.describe{Resources}(request)

            println("Hello, {AWS Service}!")
            
            response.{resources}?.let { resources ->
                println("Found ${resources.size} {resources}:")
                resources.forEach { resource ->
                    println("  - ${resource.{resourceId}} (${resource.state})")
                }
            }

        } catch (e: {Service}Exception) {
            println("{AWS Service} error: ${e.message}")
            exitProcess(1)
        }
    }
}
```

## Error Handling Requirements

### Standard Error Handling Pattern
```kotlin
try {
    // AWS operation
    val response = {service}Client.operation(request)
    
} catch (e: {Service}Exception) {
    // Service-specific errors
    when (e.errorDetails?.errorCode) {
        "UnauthorizedOperation" -> {
            println("You don't have permission to access {AWS Service}.")
            println("Please check your IAM permissions.")
        }
        "InvalidParameterValue" -> {
            println("Invalid parameter provided to {AWS Service}.")
            println("Please verify your input parameters.")
        }
        "ResourceNotFoundException" -> {
            println("Resource not found in {AWS Service}.")
            println("Please check if the resource exists.")
        }
        else -> {
            println("{AWS Service} error: ${e.message}")
            println("Please check your AWS configuration.")
        }
    }
    exitProcess(1)
    
} catch (e: ClientException) {
    // General client errors (network, credentials, etc.)
    println("Client error: ${e.message}")
    println("Please check your AWS credentials and network connectivity.")
    exitProcess(1)
}
```

## Validation Requirements
- ✅ **Must run without errors** (with proper credentials)
- ✅ **Must handle credential issues gracefully**
- ✅ **Must display meaningful output**
- ✅ **Must use direct AWS SDK client calls**
- ✅ **Must include proper snippet tags**
- ✅ **Must accept region as command line argument**
- ✅ **Must close client resources properly** (use .use{})
- ✅ **Must use suspend functions** for AWS operations
- ✅ **Must use Kotlin idioms** (null safety, string templates, etc.)

## Common Patterns
- Always use `{Service}Client { this.region = region }.use { }` for client creation and resource management
- Include comprehensive error handling for both service and client exceptions
- Provide user-friendly output messages with emojis for better UX
- Handle command line arguments properly with usage messages
- Use Kotlin's null safety features (`?.let`, `?:`)
- Keep it as simple as possible - no additional classes or complexity
- Use proper Kotlin naming conventions (camelCase for functions, PascalCase for classes)
- Use string templates for cleaner output formatting

## Build Configuration
Ensure the service dependency is included in `build.gradle.kts`:

```kotlin
dependencies {
    implementation("aws.sdk.kotlin:aws-core:+")
    implementation("aws.sdk.kotlin:{service}:+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

## Testing Requirements
- ✅ **Must compile without errors**
- ✅ **Must run with valid AWS credentials**
- ✅ **Must handle invalid regions gracefully**
- ✅ **Must display appropriate output for empty results**
- ✅ **Must exit cleanly on errors**
- ✅ **Must properly handle coroutines**

## Kotlin-Specific Features
- **Coroutines**: Use suspend functions for all AWS operations
- **Null Safety**: Leverage `?.let` and `?:` operators
- **String Templates**: Use `${}` for variable interpolation
- **Extension Functions**: Use `.use{}` for resource management
- **Data Classes**: Use for structured responses when needed
- **Smart Casts**: Leverage Kotlin's type inference
- **Trailing Lambdas**: Use DSL-style builders for requests