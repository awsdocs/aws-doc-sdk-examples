# Java Service Actions Generation

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
Generate service actions classes that encapsulate AWS service functionality with proper error handling and logging using AWS SDK for Java V2.

## Requirements
- **MANDATORY**: Every service MUST have an actions class
- **Error Handling**: Handle service-specific errors appropriately
- **Logging**: Include comprehensive logging for all operations
- **Static Methods**: Use static methods for reusable operations
- **Complete Responses**: Return complete AWS response objects

## File Structure
```
javav2/example_code/{service}/
├── src/main/java/com/example/{service}/
│   └── {Service}Actions.java       # Service actions class
```

## Actions Class Pattern
```java
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.{service};

import software.amazon.awssdk.services.{service}.{Service}Client;
import software.amazon.awssdk.services.{service}.model.*;
import software.amazon.awssdk.services.{service}.paginators.*;
import software.amazon.awssdk.core.exception.SdkException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java code example shows how to perform {AWS Service} operations.
 */

// snippet-start:[{service}.java2.{service}_actions.main]
public class {Service}Actions {

    private static final Logger logger = LoggerFactory.getLogger({Service}Actions.class);

    // Individual action methods follow...
}
// snippet-end:[{service}.java2.{service}_actions.main]
```

## Action Method Pattern
```java
    /**
     * {Action description}.
     *
     * @param {service}Client the {AWS Service} client to use for the operation
     * @param param parameter description
     * @return the {ActionName}Response object containing the result
     * @throws {Service}Exception if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    // snippet-start:[{service}.java2.{action_name}.main]
    public static {ActionName}Response {actionMethod}({Service}Client {service}Client, String param) {
        try {
            {ActionName}Request request = {ActionName}Request.builder()
                    .parameter(param)
                    .build();

            {ActionName}Response response = {service}Client.{actionName}(request);
            logger.info("{Action} completed successfully");
            return response;

        } catch ({Service}Exception e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "BadRequestException":
                    logger.error("Invalid request parameters for {action}: {}", e.getMessage());
                    break;
                case "ResourceNotFoundException":
                    logger.error("Resource not found for {action}: {}", e.getMessage());
                    break;
                case "InternalServerErrorException":
                    logger.error("Internal server error during {action}: {}", e.getMessage());
                    break;
                default:
                    logger.error("Unexpected error during {action}: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error during {action}: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[{service}.java2.{action_name}.main]
```

## Paginator Pattern for List Operations
```java
    /**
     * Lists all {resources} using pagination to retrieve complete results.
     *
     * @param {service}Client the {AWS Service} client to use for the operation
     * @return a list of all {resources}
     * @throws {Service}Exception if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    // snippet-start:[{service}.java2.list_{resources}.main]
    public static List<{Resource}> list{Resources}({Service}Client {service}Client) {
        try {
            List<{Resource}> {resources} = new ArrayList<>();
            
            // Use paginator to retrieve all results
            List{Resources}Iterable listIterable = {service}Client.list{Resources}Paginator();
            
            listIterable.stream()
                    .flatMap(response -> response.{resources}().stream())
                    .forEach({resources}::add);
            
            logger.info("Retrieved {} {resources}", {resources}.size());
            return {resources};

        } catch ({Service}Exception e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "BadRequestException":
                    logger.error("Invalid request parameters for listing {resources}: {}", e.getMessage());
                    break;
                case "AccessDeniedException":
                    logger.error("Access denied when listing {resources}: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error listing {resources}: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error listing {resources}: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[{service}.java2.list_{resources}.main]
```

## Paginator with Parameters Pattern
```java
    /**
     * Lists {resources} with optional filtering, using pagination.
     *
     * @param {service}Client the {AWS Service} client to use for the operation
     * @param filterCriteria optional criteria to filter results
     * @return a list of filtered {resources}
     * @throws {Service}Exception if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    // snippet-start:[{service}.java2.list_{resources}_with_filter.main]
    public static List<{Resource}> list{Resources}WithFilter({Service}Client {service}Client, 
                                                             {FilterCriteria} filterCriteria) {
        try {
            List<{Resource}> {resources} = new ArrayList<>();
            
            List{Resources}Request.Builder requestBuilder = List{Resources}Request.builder();
            if (filterCriteria != null) {
                requestBuilder.filterCriteria(filterCriteria);
            }
            
            List{Resources}Iterable listIterable = {service}Client.list{Resources}Paginator(requestBuilder.build());
            
            listIterable.stream()
                    .flatMap(response -> response.{resources}().stream())
                    .forEach({resources}::add);
            
            logger.info("Retrieved {} filtered {resources}", {resources}.size());
            return {resources};

        } catch ({Service}Exception e) {
            logger.error("Error listing {resources} with filter: {}", e.getMessage());
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error listing {resources} with filter: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[{service}.java2.list_{resources}_with_filter.main]
```

## Resource Creation Pattern
```java
    /**
     * Creates a new {resource}.
     *
     * @param {service}Client the {AWS Service} client to use for the operation
     * @param {resourceName} the name for the new {resource}
     * @return the ID of the created {resource}
     * @throws {Service}Exception if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    // snippet-start:[{service}.java2.create_{resource}.main]
    public static String create{Resource}({Service}Client {service}Client, String {resourceName}) {
        try {
            Create{Resource}Request request = Create{Resource}Request.builder()
                    .{resourceName}({resourceName})
                    .build();

            Create{Resource}Response response = {service}Client.create{Resource}(request);
            String {resourceId} = response.{resourceId}();
            
            logger.info("Created {resource} with ID: {}", {resourceId});
            return {resourceId};

        } catch ({Service}Exception e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "BadRequestException":
                    logger.error("Invalid parameters for creating {resource}: {}", e.getMessage());
                    break;
                case "ResourceAlreadyExistsException":
                    logger.error("{Resource} already exists: {}", e.getMessage());
                    break;
                case "LimitExceededException":
                    logger.error("Limit exceeded when creating {resource}: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error creating {resource}: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error creating {resource}: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[{service}.java2.create_{resource}.main]
```

## Resource Deletion Pattern
```java
    /**
     * Deletes a {resource}.
     *
     * @param {service}Client the {AWS Service} client to use for the operation
     * @param {resourceId} the ID of the {resource} to delete
     * @throws {Service}Exception if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    // snippet-start:[{service}.java2.delete_{resource}.main]
    public static void delete{Resource}({Service}Client {service}Client, String {resourceId}) {
        try {
            Delete{Resource}Request request = Delete{Resource}Request.builder()
                    .{resourceId}({resourceId})
                    .build();

            {service}Client.delete{Resource}(request);
            logger.info("Deleted {resource} with ID: {}", {resourceId});

        } catch ({Service}Exception e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ResourceNotFoundException":
                    logger.error("{Resource} not found for deletion: {}", e.getMessage());
                    break;
                case "BadRequestException":
                    logger.error("Invalid request for deleting {resource}: {}", e.getMessage());
                    break;
                case "ConflictException":
                    logger.error("Cannot delete {resource} due to conflict: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error deleting {resource}: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error deleting {resource}: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[{service}.java2.delete_{resource}.main]
```

## Error Handling Requirements

### Service-Specific Errors
Based on the service specification, handle these error types:
- **BadRequestException**: Validate input parameters
- **InternalServerErrorException**: Retry with exponential backoff
- **ResourceNotFoundException**: Handle missing resources gracefully
- **AccessDeniedException**: Provide clear permission guidance

### Error Handling Pattern
```java
try {
    // AWS operation
    response = {service}Client.operation(request);
    return response;
} catch ({Service}Exception e) {
    String errorCode = e.awsErrorDetails().errorCode();
    switch (errorCode) {
        case "BadRequestException":
            logger.error("Validate input parameters and notify user");
            break;
        case "InternalServerErrorException":
            logger.error("Retry operation with exponential backoff");
            break;
        default:
            logger.error("Error in operation: {}", e.getMessage());
    }
    throw e;
} catch (SdkException e) {
    logger.error("SDK error in operation: {}", e.getMessage());
    throw e;
}
```

## Actions Class Requirements
- ✅ **ALWAYS** include proper logging for all operations
- ✅ **ALWAYS** use static methods for reusability
- ✅ **ALWAYS** handle service-specific errors per specification
- ✅ **ALWAYS** include comprehensive JavaDoc documentation
- ✅ **ALWAYS** return complete AWS response objects
- ✅ **ALWAYS** follow established naming conventions
- ✅ **ALWAYS** use paginators for list operations when available
- ✅ **ALWAYS** retrieve complete results, not just first page

## Method Naming Conventions
- Use camelCase for method names
- Match AWS API operation names (e.g., `listBuckets` for `ListBuckets`)
- Use descriptive parameter names
- Return appropriate data types (Response objects, String IDs, Lists, etc.)

## Pagination Requirements
- **MANDATORY**: Use paginators when available for list operations
- **Complete Results**: Ensure all pages are retrieved, not just first page
- **Efficient**: Use AWS SDK paginators instead of manual pagination

### Paginator Usage Guidelines

#### When to Use Paginators
- ✅ **List Operations**: Always use for operations that return lists of resources
- ✅ **Large Result Sets**: Essential for services that may return many items
- ✅ **Complete Data**: Ensures all results are retrieved, not just first page

#### How to Use Paginators
```java
// Use paginator for complete results
List{Resources}Iterable listIterable = {service}Client.list{Resources}Paginator();

// Stream through all pages
List<{Resource}> allResources = listIterable.stream()
        .flatMap(response -> response.{resources}().stream())
        .collect(Collectors.toList());
```

## Documentation Requirements
- Include class-level JavaDoc explaining service purpose
- Document all parameters with @param tags
- Document return values with @return tags
- Document exceptions with @throws tags
- Include usage examples in JavaDoc where helpful
- Use proper snippet tags for documentation generation
- Document pagination behavior in list method JavaDoc

## Build Dependencies
Ensure proper dependencies in `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>{service}</artifactId>
        <version>${aws.java.sdk.version}</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.7</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.8</version>
    </dependency>
</dependencies>
```