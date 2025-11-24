# Java Hello Examples Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Java-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Java-premium-KB", "Java implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct AWS SDK for Java V2 client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use AWS SDK client directly, no actions classes needed

## File Structure
```
javav2/example_code/{service}/
├── src/main/java/com/example/{service}/
│   └── Hello{Service}.java         # Hello example file
```

## Hello Example Pattern
```java
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.{service};

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.{service}.{Service}Client;
import software.amazon.awssdk.services.{service}.model.*;
import software.amazon.awssdk.core.exception.SdkException;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This example shows how to get started with {AWS Service} by {basic operation description}.
 */

// snippet-start:[{service}.java2.hello.main]
public class Hello{Service} {

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

        hello{Service}({service}Client);
        {service}Client.close();
    }

    /**
     * Invokes a {AWS Service} operation to {basic operation description}.
     *
     * @param {service}Client the {AWS Service} client to use for the operation
     */
    public static void hello{Service}({Service}Client {service}Client) {
        try {
            // Perform the most basic operation for this service
            {BasicOperation}Request request = {BasicOperation}Request.builder()
                    .build();

            {BasicOperation}Response response = {service}Client.{basicOperation}(request);

            System.out.println("Hello, {AWS Service}!");
            
            // Display appropriate result information
            if (response.has{ResultField}()) {
                System.out.println("Found " + response.{resultField}().size() + " {resources}.");
                response.{resultField}().forEach(resource -> 
                    System.out.println("  - " + resource.{resourceName}())
                );
            } else {
                System.out.println("{AWS Service} is available and ready to use.");
            }

        } catch ({Service}Exception e) {
            System.err.println("{AWS Service} error occurred: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (SdkException e) {
            System.err.println("SDK error occurred: " + e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[{service}.java2.hello.main]
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
```java
public static void hello{Service}({Service}Client {service}Client) {
    try {
        List{Resources}Request request = List{Resources}Request.builder().build();
        List{Resources}Response response = {service}Client.list{Resources}(request);

        System.out.println("Hello, {AWS Service}!");
        System.out.println("Found " + response.{resources}().size() + " {resources}.");
        
        response.{resources}().forEach(resource -> 
            System.out.println("  - " + resource.{resourceName}())
        );

    } catch ({Service}Exception e) {
        System.err.println("{AWS Service} error: " + e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
}
```

### For Services with Status Operations
```java
public static void hello{Service}({Service}Client {service}Client) {
    try {
        Get{Service}StatusRequest request = Get{Service}StatusRequest.builder().build();
        Get{Service}StatusResponse response = {service}Client.get{Service}Status(request);

        System.out.println("Hello, {AWS Service}!");
        System.out.println("Service status: " + response.status());
        System.out.println("{AWS Service} is ready to use.");

    } catch ({Service}Exception e) {
        System.err.println("{AWS Service} error: " + e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
}
```

### For Services with Describe Operations
```java
public static void hello{Service}({Service}Client {service}Client) {
    try {
        Describe{Resources}Request request = Describe{Resources}Request.builder()
                .maxResults(10)
                .build();
        Describe{Resources}Response response = {service}Client.describe{Resources}(request);

        System.out.println("Hello, {AWS Service}!");
        System.out.println("Found " + response.{resources}().size() + " {resources}.");
        
        response.{resources}().forEach(resource -> 
            System.out.println("  - " + resource.{resourceId}() + " (" + resource.state() + ")")
        );

    } catch ({Service}Exception e) {
        System.err.println("{AWS Service} error: " + e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
}
```

## Error Handling Requirements

### Standard Error Handling Pattern
```java
try {
    // AWS operation
    response = {service}Client.operation(request);
    
} catch ({Service}Exception e) {
    // Service-specific errors
    String errorCode = e.awsErrorDetails().errorCode();
    switch (errorCode) {
        case "UnauthorizedOperation":
            System.err.println("You don't have permission to access {AWS Service}.");
            break;
        case "InvalidParameterValue":
            System.err.println("Invalid parameter provided to {AWS Service}.");
            break;
        default:
            System.err.println("{AWS Service} error: " + e.awsErrorDetails().errorMessage());
    }
    System.exit(1);
    
} catch (SdkException e) {
    // General SDK errors (network, credentials, etc.)
    System.err.println("SDK error: " + e.getMessage());
    System.exit(1);
}
```

## Validation Requirements
- ✅ **Must run without errors** (with proper credentials)
- ✅ **Must handle credential issues gracefully**
- ✅ **Must display meaningful output**
- ✅ **Must use direct AWS SDK client calls**
- ✅ **Must include proper snippet tags**
- ✅ **Must accept region as command line argument**
- ✅ **Must close client resources properly**

## Common Patterns
- Always use `{Service}Client.builder().region(awsRegion).build()` for client creation
- Include comprehensive error handling for both service and SDK exceptions
- Provide user-friendly output messages
- Handle command line arguments properly
- Close client resources in main method
- Keep it as simple as possible - no additional classes or complexity
- Use proper Java naming conventions (PascalCase for classes, camelCase for methods)

## Build Configuration
Ensure the service dependency is included in `pom.xml`:

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>{service}</artifactId>
    <version>${aws.java.sdk.version}</version>
</dependency>
```

## Testing Requirements
- ✅ **Must compile without errors**
- ✅ **Must run with valid AWS credentials**
- ✅ **Must handle invalid regions gracefully**
- ✅ **Must display appropriate output for empty results**
- ✅ **Must exit cleanly on errors**