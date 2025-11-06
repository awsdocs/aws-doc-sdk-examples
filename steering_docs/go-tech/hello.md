# Go Hello Examples Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Go-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Go-premium-KB", "Go implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct AWS SDK for Go v2 client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use AWS SDK for Go v2 client directly, no wrapper structs needed
- **Context**: ALWAYS use context.Context for AWS operations

## File Structure
```
gov2/{service}/hello/
├── hello.go                    # MANDATORY: Hello example file
```

## Hello Example Pattern
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package main demonstrates basic {AWS Service} connectivity.
package main

import (
    "context"
    "errors"
    "fmt"
    "log"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/{service}"
    "github.com/aws/smithy-go"
)

// main demonstrates basic {AWS Service} connectivity by {basic operation description}.
func main() {
    ctx := context.Background()

    // Load AWS configuration
    sdkConfig, err := config.LoadDefaultConfig(ctx)
    if err != nil {
        log.Fatalf("Couldn't load default configuration: %v", err)
    }

    // Create service client
    {service}Client := {service}.NewFromConfig(sdkConfig)

    // Perform the most basic operation for this service
    result, err := {service}Client.{BasicOperation}(ctx, &{service}.{BasicOperationInput}{
        // Add minimal required parameters if any
    })
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "UnauthorizedOperation", "AccessDenied":
                fmt.Println("You don't have permission to access {AWS Service}.")
            default:
                fmt.Printf("Couldn't access {AWS Service}. Error: %v\n", err)
            }
        } else {
            fmt.Printf("Couldn't access {AWS Service}. Error: %v\n", err)
        }
        return
    }

    fmt.Println("Hello, {AWS Service}!")
    // Display appropriate result information based on service type
    
}
```

## Hello Examples by Service Type

### List-Based Services (S3, DynamoDB, etc.)
- **Operation**: List primary resources (buckets, tables, etc.)
- **Message**: Show count and names of resources
- **Example**:
```go
result, err := s3Client.ListBuckets(ctx, &s3.ListBucketsInput{})
if err != nil {
    // Handle error
}

fmt.Printf("Found %d bucket(s):\n", len(result.Buckets))
for _, bucket := range result.Buckets {
    fmt.Printf("  %s\n", *bucket.Name)
}
```

### Status-Based Services (GuardDuty, Config, etc.)  
- **Operation**: Check service status or list detectors/configurations
- **Message**: Show service availability and basic status
- **Example**:
```go
result, err := guarddutyClient.ListDetectors(ctx, &guardduty.ListDetectorsInput{})
if err != nil {
    // Handle error
}

fmt.Printf("Found %d detector(s)\n", len(result.DetectorIds))
```

### Compute Services (EC2, Lambda, etc.)
- **Operation**: List instances/functions or describe regions
- **Message**: Show available resources or regions
- **Example**:
```go
result, err := ec2Client.DescribeRegions(ctx, &ec2.DescribeRegionsInput{})
if err != nil {
    // Handle error
}

fmt.Printf("Found %d region(s):\n", len(result.Regions))
for _, region := range result.Regions {
    fmt.Printf("  %s\n", *region.RegionName)
}
```

## Error Handling Patterns
```go
// Standard error handling for hello examples
if err != nil {
    var ae smithy.APIError
    if errors.As(err, &ae) {
        switch ae.ErrorCode() {
        case "UnauthorizedOperation", "AccessDenied":
            fmt.Println("You don't have permission to access {AWS Service}.")
        case "InvalidUserID.NotFound":
            fmt.Println("User credentials not found or invalid.")
        default:
            fmt.Printf("Couldn't access {AWS Service}. Error: %v\n", err)
        }
    } else {
        fmt.Printf("Couldn't access {AWS Service}. Error: %v\n", err)
    }
    return
}
```

## Context Usage
```go
// Always use context for AWS operations
ctx := context.Background()

// For operations with timeout (if needed)
ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
defer cancel()

result, err := client.Operation(ctx, &service.OperationInput{
    // Parameters
})
```

## Validation Requirements
- ✅ **Must run without errors** (with proper credentials)
- ✅ **Must handle credential issues gracefully**
- ✅ **Must display meaningful output**
- ✅ **Must use direct AWS SDK for Go v2 client calls**
- ✅ **Must include proper package and function documentation**
- ✅ **Must use context.Context for all AWS operations**
- ✅ **Must follow Go naming conventions**

## Common Patterns
- Always use `{service}.NewFromConfig(sdkConfig)` to create clients
- Include comprehensive error handling with smithy.APIError checking
- Provide user-friendly output messages using fmt.Printf
- Handle both service-specific and general exceptions
- Keep it as simple as possible - no additional structs or complexity
- Use context.Background() for basic operations
- Follow Go naming conventions (camelCase for unexported, PascalCase for exported)

## File Naming and Structure
- **File location**: `gov2/{service}/hello/hello.go`
- **Package**: `package main`
- **Function structure**: `main()` function as entry point
- **Documentation**: Include package-level and function-level comments
- **Imports**: Only import necessary packages, group standard library, AWS SDK, and third-party imports