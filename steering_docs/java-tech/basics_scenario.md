# Java Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Java-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Java-premium-KB", "Java implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: USE Scanner for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Actions Classes**: MUST use service actions classes for all operations

## File Structure
```
javav2/example_code/{service}/
├── src/main/java/com/example/{service}/
│   ├── {Service}Scenario.java      # Main scenario file
│   └── {Service}Actions.java       # Actions class (if not exists)
```

## MANDATORY Pre-Implementation Steps

### Step 1: Read Service Specification
**CRITICAL**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-step workflow description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Scenario Class Pattern
### Implementation Pattern Based on SPECIFICATION.md
```java
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.{service};

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.{service}.{Service}Client;
import software.amazon.awssdk.services.{service}.model.*;
import software.amazon.awssdk.core.exception.SdkException;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java code example performs the following tasks:
 *
 * 1. {Phase 1 description}
 * 2. {Phase 2 description}
 * 3. {Phase 3 description}
 * 4. {Phase 4 description}
 */

// snippet-start:[{service}.java2.{service}_scenario.main]
public class {Service}Scenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static {Service}Actions {service}Actions;
    private static Scanner scanner = new Scanner(System.in);
    private static String resourceId;

    public static void main(String[] args) {
        final String usage = """
                Usage:
                   <region>

                Where:
                   region - The AWS region (for example, us-east-1).
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String region = args[0];
        Region awsRegion = Region.of(region);
        {Service}Client {service}Client = {Service}Client.builder()
                .region(awsRegion)
                .build();

        {service}Actions = new {Service}Actions();

        System.out.println(DASHES);
        System.out.println("Welcome to the {AWS Service} basics scenario!");
        System.out.println(DASHES);
        System.out.println("""
                {Service description and what users will learn}
                """);

        try {
            runScenario({service}Client);
        } catch (Exception e) {
            System.err.println("Scenario failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            {service}Client.close();
        }
    }

    private static void runScenario({Service}Client {service}Client) throws InterruptedException {
        try {
            setupPhase({service}Client);
            demonstrationPhase({service}Client);
            examinationPhase({service}Client);
        } catch (Exception e) {
            System.err.println("Error during scenario execution: " + e.getMessage());
            throw e;
        } finally {
            cleanupPhase({service}Client);
        }
    }

    private static void setupPhase({Service}Client {service}Client) {
        System.out.println(DASHES);
        System.out.println("Setting up {AWS Service}...");
        System.out.println(DASHES);

        try {
            // Example: Check for existing resources (from specification)
            List<{Resource}> existingResources = {service}Actions.listResources({service}Client);
            if (!existingResources.isEmpty()) {
                System.out.println("Found " + existingResources.size() + " existing resource(s):");
                for ({Resource} resource : existingResources) {
                    System.out.println("  - " + resource.{resourceName}());
                }

                System.out.print("Would you like to use an existing resource? (y/n): ");
                String useExisting = scanner.nextLine();
                if (useExisting.toLowerCase().startsWith("y")) {
                    resourceId = existingResources.get(0).{resourceId}();
                    return;
                }
            }

            // Create new resource as specified
            System.out.println("Creating new resource...");
            resourceId = {service}Actions.createResource({service}Client);
            System.out.println("✓ Resource created successfully: " + resourceId);

        } catch (SdkException e) {
            System.err.println("Error during setup: " + e.getMessage());
            throw e;
        }
    }

    private static void demonstrationPhase({Service}Client {service}Client) throws InterruptedException {
        System.out.println(DASHES);
        System.out.println("Demonstrating {AWS Service} capabilities...");
        System.out.println(DASHES);

        try {
            // Implement specific operations from specification
            // Example: Generate sample data if specified
            {service}Actions.createSampleData({service}Client, resourceId);
            System.out.println("✓ Sample data created successfully");

            // Wait if specified in the specification
            System.out.println("Waiting for data to be processed...");
            TimeUnit.SECONDS.sleep(5);

        } catch (SdkException e) {
            System.err.println("Error during demonstration: " + e.getMessage());
            throw e;
        }
    }

    private static void examinationPhase({Service}Client {service}Client) {
        System.out.println(DASHES);
        System.out.println("Examining {AWS Service} data...");
        System.out.println(DASHES);

        try {
            // List and examine data as specified
            List<{DataItem}> dataItems = {service}Actions.listData({service}Client, resourceId);
            if (dataItems.isEmpty()) {
                System.out.println("No data found. Data may take a few minutes to appear.");
                return;
            }

            System.out.println("Found " + dataItems.size() + " data item(s)");

            // Get detailed information as specified
            List<{DetailedData}> detailedData = {service}Actions.getDataDetails({service}Client, resourceId, 
                    dataItems.subList(0, Math.min(5, dataItems.size())));
            displayDataSummary(detailedData);

            // Show detailed view if specified
            if (!detailedData.isEmpty()) {
                System.out.print("Would you like to see detailed information? (y/n): ");
                String showDetails = scanner.nextLine();
                if (showDetails.toLowerCase().startsWith("y")) {
                    displayDataDetails(detailedData.get(0));
                }
            }

            // Filter data as specified
            filterDataByCriteria(dataItems);

        } catch (SdkException e) {
            System.err.println("Error during examination: " + e.getMessage());
            throw e;
        }
    }

    private static void cleanupPhase({Service}Client {service}Client) {
        if (resourceId == null) {
            return;
        }

        System.out.println(DASHES);
        System.out.println("Cleanup options:");
        System.out.println("Note: Deleting the resource will stop all monitoring/processing.");
        System.out.println(DASHES);

        System.out.print("Would you like to delete the resource? (y/n): ");
        String deleteResource = scanner.nextLine();

        if (deleteResource.toLowerCase().startsWith("y")) {
            try {
                {service}Actions.deleteResource({service}Client, resourceId);
                System.out.println("✓ Deleted resource: " + resourceId);
            } catch (SdkException e) {
                System.err.println("Error deleting resource: " + e.getMessage());
            }
        } else {
            System.out.println("Resource " + resourceId + " will continue running.");
            System.out.println("You can manage it through the AWS Console or delete it later.");
        }
    }

    private static void displayDataSummary(List<{DetailedData}> detailedData) {
        System.out.println("\nData Summary:");
        for ({DetailedData} data : detailedData) {
            System.out.println("  • " + data.{summaryField}());
        }
    }

    private static void displayDataDetails({DetailedData} data) {
        System.out.println("\nDetailed Information:");
        System.out.println("  ID: " + data.{idField}());
        System.out.println("  Status: " + data.{statusField}());
        System.out.println("  Created: " + data.{createdField}());
        // Add more fields as specified
    }

    private static void filterDataByCriteria(List<{DataItem}> dataItems) {
        System.out.println("\nFiltering data by criteria...");
        // Implement filtering logic as specified in the specification
    }
}
// snippet-end:[{service}.java2.{service}_scenario.main]
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
```java
// Yes/No questions
System.out.print("Use existing resource? (y/n): ");
String useExisting = scanner.nextLine();
boolean shouldUse = useExisting.toLowerCase().startsWith("y");

// Text input
System.out.print("Enter resource name: ");
String resourceName = scanner.nextLine();

// Numeric input
System.out.print("How many items? ");
int count = Integer.parseInt(scanner.nextLine());
```

### Information Display
```java
// Progress indicators
System.out.println("✓ Operation completed successfully");
System.out.println("⚠ Warning message");
System.out.println("✗ Error occurred");

// Formatted output
System.out.println(DASHES);
System.out.println("Found " + items.size() + " items:");
for (Item item : items) {
    System.out.println("  • " + item.name());
}
```

## Specification-Based Error Handling

### Error Handling from Specification
The specification includes an "Errors" section with specific error codes and handling:

```java
// Example error handling based on specification
try {
    {Resource} response = {service}Actions.createResource({service}Client);
    return response;
} catch ({Service}Exception e) {
    String errorCode = e.awsErrorDetails().errorCode();
    switch (errorCode) {
        case "BadRequestException":
            // Handle as specified: "Validate input parameters and notify user"
            System.err.println("Invalid configuration. Please check your parameters.");
            break;
        case "InternalServerErrorException":
            // Handle as specified: "Retry operation with exponential backoff"
            System.err.println("Service temporarily unavailable. Retrying...");
            // Implement retry logic
            break;
        default:
            System.err.println("Unexpected error: " + e.getMessage());
    }
    throw e;
} catch (SdkException e) {
    System.err.println("SDK error: " + e.getMessage());
    throw e;
}
```

## Scenario Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** include descriptive comment block at top explaining scenario steps from specification
- ✅ **ALWAYS** use Scanner for user interaction
- ✅ **ALWAYS** use service actions classes for all AWS operations
- ✅ **ALWAYS** implement proper cleanup in finally block
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification
- ✅ **ALWAYS** handle edge cases (no resources found, etc.) as specified

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