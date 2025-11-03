# Kotlin Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Kotlin-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Kotlin-premium-KB", "Kotlin implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: USE readLine() for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Actions Classes**: MUST use service actions classes for all operations
- **Suspend Functions**: Use coroutines for all AWS operations

## File Structure
```
kotlin/services/{service}/
├── src/main/kotlin/com/kotlin/{service}/
│   ├── {Service}Scenario.kt        # Main scenario file
│   └── {Service}Actions.kt         # Actions class (if not exists)
```

## MANDATORY Pre-Implementation Steps

### Step 1: Read Service Specification
**CRITICAL**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-Step workflow description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Scenario Class Pattern
### Implementation Pattern Based on SPECIFICATION.md
```kotlin
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.{service}

import aws.sdk.kotlin.services.{service}.{Service}Client
import aws.sdk.kotlin.services.{service}.model.*
import aws.smithy.kotlin.runtime.ClientException
import kotlinx.coroutines.delay

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 *
 * This Kotlin code example performs the following tasks:
 *
 * 1. {Phase 1 description}
 * 2. {Phase 2 description}
 * 3. {Phase 3 description}
 * 4. {Phase 4 description}
 */

// snippet-start:[{service}.kotlin.{service}_scenario.main]
class {Service}Scenario {
    companion object {
        private const val DASHES = "".padEnd(80, '-')
        private val {service}Actions = {Service}Actions()
        private var resourceId: String? = null

        @JvmStatic
        suspend fun main(args: Array<String>) {
            val usage = """
                Usage:
                   <region>

                Where:
                   region - The AWS region (for example, us-east-1).
                """.trimIndent()

            if (args.size != 1) {
                println(usage)
                return
            }

            val region = args[0]
            
            {Service}Client { this.region = region }.use { {service}Client ->
                println(DASHES)
                println("Welcome to the {AWS Service} basics scenario!")
                println(DASHES)
                println("""
                    {Service description and what users will learn}
                    """.trimIndent())

                try {
                    runScenario({service}Client)
                } catch (e: Exception) {
                    println("❌ Scenario failed: ${e.message}")
                    e.printStackTrace()
                } finally {
                    cleanupPhase({service}Client)
                }
            }
        }

        private suspend fun runScenario({service}Client: {Service}Client) {
            try {
                setupPhase({service}Client)
                demonstrationPhase({service}Client)
                examinationPhase({service}Client)
            } catch (e: Exception) {
                println("❌ Error during scenario execution: ${e.message}")
                throw e
            }
        }

        private suspend fun setupPhase({service}Client: {Service}Client) {
            println(DASHES)
            println("Setting up {AWS Service}...")
            println(DASHES)

            try {
                // Example: Check for existing resources (from specification)
                val existingResources = {service}Actions.listResources({service}Client)
                if (existingResources.isNotEmpty()) {
                    println("Found ${existingResources.size} existing resource(s):")
                    existingResources.forEach { resource ->
                        println("  - ${resource.{resourceName}}")
                    }

                    print("Would you like to use an existing resource? (y/n): ")
                    val useExisting = readLine()?.lowercase()?.startsWith("y") ?: false
                    if (useExisting) {
                        resourceId = existingResources.first().{resourceId}
                        return
                    }
                }

                // Create new resource as specified
                println("Creating new resource...")
                resourceId = {service}Actions.createResource({service}Client)
                println("✅ Resource created successfully: $resourceId")

            } catch (e: {Service}Exception) {
                println("❌ Error during setup: ${e.message}")
                throw e
            }
        }

        private suspend fun demonstrationPhase({service}Client: {Service}Client) {
            println(DASHES)
            println("Demonstrating {AWS Service} capabilities...")
            println(DASHES)

            try {
                // Implement specific operations from specification
                // Example: Generate sample data if specified
                resourceId?.let { id ->
                    {service}Actions.createSampleData({service}Client, id)
                    println("✅ Sample data created successfully")

                    // Wait if specified in the specification
                    println("Waiting for data to be processed...")
                    delay(5000)
                }

            } catch (e: {Service}Exception) {
                println("❌ Error during demonstration: ${e.message}")
                throw e
            }
        }

        private suspend fun examinationPhase({service}Client: {Service}Client) {
            println(DASHES)
            println("Examining {AWS Service} data...")
            println(DASHES)

            try {
                resourceId?.let { id ->
                    // List and examine data as specified
                    val dataItems = {service}Actions.listData({service}Client, id)
                    if (dataItems.isEmpty()) {
                        println("No data found. Data may take a few minutes to appear.")
                        return
                    }

                    println("Found ${dataItems.size} data item(s)")

                    // Get detailed information as specified
                    val detailedData = {service}Actions.getDataDetails(
                        {service}Client, 
                        id, 
                        dataItems.take(5)
                    )
                    displayDataSummary(detailedData)

                    // Show detailed view if specified
                    if (detailedData.isNotEmpty()) {
                        print("Would you like to see detailed information? (y/n): ")
                        val showDetails = readLine()?.lowercase()?.startsWith("y") ?: false
                        if (showDetails) {
                            displayDataDetails(detailedData.first())
                        }
                    }

                    // Filter data as specified
                    filterDataByCriteria(dataItems)
                }

            } catch (e: {Service}Exception) {
                println("❌ Error during examination: ${e.message}")
                throw e
            }
        }

        private suspend fun cleanupPhase({service}Client: {Service}Client) {
            resourceId?.let { id ->
                println(DASHES)
                println("Cleanup options:")
                println("Note: Deleting the resource will stop all monitoring/processing.")
                println(DASHES)

                print("Would you like to delete the resource? (y/n): ")
                val deleteResource = readLine()?.lowercase()?.startsWith("y") ?: false

                if (deleteResource) {
                    try {
                        {service}Actions.deleteResource({service}Client, id)
                        println("✅ Deleted resource: $id")
                    } catch (e: {Service}Exception) {
                        println("❌ Error deleting resource: ${e.message}")
                    }
                } else {
                    println("Resource $id will continue running.")
                    println("You can manage it through the AWS Console or delete it later.")
                }
            }
        }

        private fun displayDataSummary(detailedData: List<{DetailedData}>) {
            println("\nData Summary:")
            detailedData.forEach { data ->
                println("  • ${data.{summaryField}}")
            }
        }

        private fun displayDataDetails(data: {DetailedData}) {
            println("\nDetailed Information:")
            println("  ID: ${data.{idField}}")
            println("  Status: ${data.{statusField}}")
            println("  Created: ${data.{createdField}}")
            // Add more fields as specified
        }

        private fun filterDataByCriteria(dataItems: List<{DataItem}>) {
            println("\nFiltering data by criteria...")
            // Implement filtering logic as specified in the specification
        }
    }
}
// snippet-end:[{service}.kotlin.{service}_scenario.main]
```

## Scenario Phase Structure (Based on Specification)

### Setup Phase
- **Read specification Setup section** for exact requirements
- Check for existing resources as specified
- Create necessary resources using actions methods
- Configure service settings per specification
- Verify setup completion as described

### Demonstration Phase  
- **Follow specification Demonstration section** exactly
- Perform core service operations using actions methods
- Generate sample data if specified in the specification
- Show service capabilities as outlined
- Provide educational context from specification

### Examination Phase
- **Implement specification Examination section** requirements
- List and examine results using actions methods
- Filter and analyze data as specified
- Display detailed information per specification format
- Allow user interaction as described in specification

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Offer cleanup options with warnings from specification
- Handle cleanup errors gracefully using actions methods
- Provide alternative management options as specified
- Confirm completion per specification

## User Interaction Patterns

### Input Types
```kotlin
// Yes/No questions
print("Use existing resource? (y/n): ")
val useExisting = readLine()?.lowercase()?.startsWith("y") ?: false

// Text input
print("Enter resource name: ")
val resourceName = readLine() ?: ""

// Numeric input
print("How many items? ")
val count = readLine()?.toIntOrNull() ?: 0
```

### Information Display
```kotlin
// Progress indicators
println("✅ Operation completed successfully")
println("⚠️ Warning message")
println("❌ Error occurred")

// Formatted output
println(DASHES)
println("Found ${items.size} items:")
items.forEach { item ->
    println("  • ${item.name}")
}
```

## Specification-Based Error Handling

### Error Handling from Specification
The specification includes an "Errors" section with specific error codes and handling:

```kotlin
// Example error handling based on specification
try {
    val response = {service}Actions.createResource({service}Client)
    return response
} catch (e: {Service}Exception) {
    when (e.errorDetails?.errorCode) {
        "BadRequestException" -> {
            // Handle as specified: "Validate input parameters and notify user"
            println("❌ Invalid configuration. Please check your parameters.")
        }
        "InternalServerErrorException" -> {
            // Handle as specified: "Retry operation with exponential backoff"
            println("⚠️ Service temporarily unavailable. Retrying...")
            // Implement retry logic
        }
        else -> {
            println("❌ Unexpected error: ${e.message}")
        }
    }
    throw e
} catch (e: ClientException) {
    println("❌ Client error: ${e.message}")
    throw e
}
```

## Scenario Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** include descriptive comment block at top explaining scenario steps from specification
- ✅ **ALWAYS** use readLine() for user interaction
- ✅ **ALWAYS** use service actions classes for all AWS operations
- ✅ **ALWAYS** implement proper cleanup in finally block
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification
- ✅ **ALWAYS** handle edge cases (no resources found, etc.) as specified
- ✅ **ALWAYS** use suspend functions for AWS operations
- ✅ **ALWAYS** use coroutines properly with delay() instead of Thread.sleep()

## Implementation Workflow

### Step-by-Step Implementation Process
1. **Read Specification**: Study `scenarios/basics/{service}/SPECIFICATION.md` thoroughly
2. **Extract API Actions**: Note all API actions listed in "API Actions Used" section
3. **Map to Actions Methods**: Ensure actions class has methods for all required actions
4. **Implement Phases**: Follow the "Proposed example structure" section exactly
5. **Add Error Handling**: Implement error handling per the "Errors" section
6. **Test Against Specification**: Verify implementation matches specification requirements

### Specification Sections to Implement
- **API Actions Used**: All operations must be available in actions class
- **Proposed example structure**: Direct mapping to scenario phases
- **Setup**: Exact setup steps and resource creation
- **Demonstration**: Specific operations to demonstrate
- **Examination**: Data analysis and filtering requirements
- **Cleanup**: Resource cleanup options and user choices
- **Errors**: Specific error codes and handling strategies

## Error Handling in Scenarios
- **Follow specification error table**: Implement exact error handling per specification
- Catch and display user-friendly error messages per specification guidance
- Continue scenario execution when possible as specified
- Provide guidance on resolving issues from specification
- Ensure cleanup runs even if errors occur

## Educational Elements
- **Use specification descriptions**: Explain operations using specification language
- Show before/after states as outlined in specification
- Provide context about service capabilities from specification
- Include tips and best practices mentioned in specification
- Follow the educational flow described in specification structure

## Kotlin-Specific Features
- **Coroutines**: Use suspend functions and proper coroutine handling
- **Null Safety**: Leverage Kotlin's null safety features
- **Extension Functions**: Use where appropriate for cleaner code
- **Data Classes**: Use for structured data representation
- **Smart Casts**: Leverage Kotlin's smart casting capabilities
- **String Templates**: Use for cleaner string formatting