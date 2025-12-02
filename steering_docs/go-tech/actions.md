# Go Actions/Wrapper Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Go-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Go-premium-KB", "Go implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate service wrapper structs that encapsulate AWS SDK operations with enhanced error handling, logging, and simplified interfaces for use in scenarios and examples.

## Requirements
- **Encapsulation**: Wrap AWS SDK client operations in service-specific structs
- **Error Handling**: Provide enhanced error handling with user-friendly messages
- **Context Support**: ALWAYS use context.Context for AWS operations
- **Testability**: Design for easy unit testing with interfaces
- **Documentation**: Comprehensive documentation for all exported methods

## File Structure
```
gov2/{service}/actions/
├── {service}_basics.go             # Actions struct and methods
├── {service}_basics_test.go        # Unit tests
└── {service}_basics_integ_test.go  # Integration tests
```

## Actions Struct Pattern
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package actions contains wrapper functions for {AWS Service} operations.
package actions

import (
    "context"
    "errors"
    "fmt"
    "log"

    "github.com/aws/aws-sdk-go-v2/service/{service}"
    "github.com/aws/aws-sdk-go-v2/service/{service}/types"
    "github.com/aws/smithy-go"
)

// {Service}Actions encapsulates {AWS Service} operations.
type {Service}Actions struct {
    {Service}Client *{service}.Client
}

// ListResources lists all resources in the {AWS Service}.
func (actions *{Service}Actions) ListResources(ctx context.Context) ([]types.{ResourceType}, error) {
    var resources []types.{ResourceType}
    
    result, err := actions.{Service}Client.List{Resources}(ctx, &{service}.List{Resources}Input{})
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "AccessDenied":
                return nil, fmt.Errorf("you don't have permission to list {resources}: %w", err)
            case "InternalServerError":
                return nil, fmt.Errorf("service temporarily unavailable, please retry: %w", err)
            default:
                return nil, fmt.Errorf("couldn't list {resources}: %w", err)
            }
        }
        return nil, fmt.Errorf("couldn't list {resources}: %w", err)
    }
    
    resources = result.{Resources}
    log.Printf("Found %d {resource}(s)", len(resources))
    
    return resources, nil
}

// CreateResource creates a new resource in {AWS Service}.
func (actions *{Service}Actions) CreateResource(ctx context.Context, resourceName string) (string, error) {
    input := &{service}.Create{Resource}Input{
        {ResourceName}: &resourceName,
        // Add other required parameters
    }
    
    result, err := actions.{Service}Client.Create{Resource}(ctx, input)
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "BadRequestException":
                return "", fmt.Errorf("invalid resource configuration: %w", err)
            case "ResourceAlreadyExistsException":
                return "", fmt.Errorf("resource with name '%s' already exists: %w", resourceName, err)
            case "LimitExceededException":
                return "", fmt.Errorf("resource limit exceeded: %w", err)
            default:
                return "", fmt.Errorf("couldn't create resource: %w", err)
            }
        }
        return "", fmt.Errorf("couldn't create resource: %w", err)
    }
    
    resourceId := *result.{ResourceId}
    log.Printf("Created resource: %s", resourceId)
    
    return resourceId, nil
}

// GetResource retrieves detailed information about a specific resource.
func (actions *{Service}Actions) GetResource(ctx context.Context, resourceId string) (*types.{ResourceType}, error) {
    input := &{service}.Get{Resource}Input{
        {ResourceId}: &resourceId,
    }
    
    result, err := actions.{Service}Client.Get{Resource}(ctx, input)
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "ResourceNotFoundException":
                return nil, fmt.Errorf("resource '%s' not found: %w", resourceId, err)
            case "AccessDenied":
                return nil, fmt.Errorf("you don't have permission to access resource '%s': %w", resourceId, err)
            default:
                return nil, fmt.Errorf("couldn't get resource '%s': %w", resourceId, err)
            }
        }
        return nil, fmt.Errorf("couldn't get resource '%s': %w", resourceId, err)
    }
    
    return result.{Resource}, nil
}

// UpdateResource updates an existing resource.
func (actions *{Service}Actions) UpdateResource(ctx context.Context, resourceId string, updates map[string]interface{}) error {
    input := &{service}.Update{Resource}Input{
        {ResourceId}: &resourceId,
        // Map updates to appropriate input fields
    }
    
    _, err := actions.{Service}Client.Update{Resource}(ctx, input)
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "ResourceNotFoundException":
                return fmt.Errorf("resource '%s' not found: %w", resourceId, err)
            case "BadRequestException":
                return fmt.Errorf("invalid update parameters: %w", err)
            case "ConflictException":
                return fmt.Errorf("resource '%s' is in a state that cannot be updated: %w", resourceId, err)
            default:
                return fmt.Errorf("couldn't update resource '%s': %w", resourceId, err)
            }
        }
        return fmt.Errorf("couldn't update resource '%s': %w", resourceId, err)
    }
    
    log.Printf("Updated resource: %s", resourceId)
    return nil
}

// DeleteResource deletes a resource from {AWS Service}.
func (actions *{Service}Actions) DeleteResource(ctx context.Context, resourceId string) error {
    input := &{service}.Delete{Resource}Input{
        {ResourceId}: &resourceId,
    }
    
    _, err := actions.{Service}Client.Delete{Resource}(ctx, input)
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "ResourceNotFoundException":
                return fmt.Errorf("resource '%s' not found: %w", resourceId, err)
            case "ConflictException":
                return fmt.Errorf("resource '%s' cannot be deleted in its current state: %w", resourceId, err)
            case "AccessDenied":
                return fmt.Errorf("you don't have permission to delete resource '%s': %w", resourceId, err)
            default:
                return fmt.Errorf("couldn't delete resource '%s': %w", resourceId, err)
            }
        }
        return fmt.Errorf("couldn't delete resource '%s': %w", resourceId, err)
    }
    
    log.Printf("Deleted resource: %s", resourceId)
    return nil
}

// ProcessData performs data processing operations on the resource.
func (actions *{Service}Actions) ProcessData(ctx context.Context, resourceId string, data []byte) error {
    input := &{service}.Process{Data}Input{
        {ResourceId}: &resourceId,
        Data:         data,
    }
    
    _, err := actions.{Service}Client.Process{Data}(ctx, input)
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "ResourceNotFoundException":
                return fmt.Errorf("resource '%s' not found: %w", resourceId, err)
            case "InvalidDataException":
                return fmt.Errorf("invalid data format: %w", err)
            case "ThrottlingException":
                return fmt.Errorf("request throttled, please retry: %w", err)
            default:
                return fmt.Errorf("couldn't process data for resource '%s': %w", resourceId, err)
            }
        }
        return fmt.Errorf("couldn't process data for resource '%s': %w", resourceId, err)
    }
    
    log.Printf("Processed data for resource: %s", resourceId)
    return nil
}

// ListDataItems lists data items associated with a resource.
func (actions *{Service}Actions) ListDataItems(ctx context.Context, resourceId string) ([]types.{DataItemType}, error) {
    var allItems []types.{DataItemType}
    var nextToken *string
    
    for {
        input := &{service}.List{DataItems}Input{
            {ResourceId}: &resourceId,
            NextToken:    nextToken,
            MaxResults:   aws.Int32(100), // Reasonable page size
        }
        
        result, err := actions.{Service}Client.List{DataItems}(ctx, input)
        if err != nil {
            var ae smithy.APIError
            if errors.As(err, &ae) {
                switch ae.ErrorCode() {
                case "ResourceNotFoundException":
                    return nil, fmt.Errorf("resource '%s' not found: %w", resourceId, err)
                case "AccessDenied":
                    return nil, fmt.Errorf("you don't have permission to list data items: %w", err)
                default:
                    return nil, fmt.Errorf("couldn't list data items: %w", err)
                }
            }
            return nil, fmt.Errorf("couldn't list data items: %w", err)
        }
        
        allItems = append(allItems, result.{DataItems}...)
        
        nextToken = result.NextToken
        if nextToken == nil {
            break
        }
    }
    
    log.Printf("Found %d data item(s) for resource %s", len(allItems), resourceId)
    return allItems, nil
}

// GetDataItemDetails retrieves detailed information about specific data items.
func (actions *{Service}Actions) GetDataItemDetails(ctx context.Context, resourceId string, itemIds []string) ([]types.{DetailedDataType}, error) {
    var detailedItems []types.{DetailedDataType}
    
    // Process items in batches if the service supports batch operations
    for _, itemId := range itemIds {
        input := &{service}.Get{DataItem}Input{
            {ResourceId}: &resourceId,
            {ItemId}:     &itemId,
        }
        
        result, err := actions.{Service}Client.Get{DataItem}(ctx, input)
        if err != nil {
            var ae smithy.APIError
            if errors.As(err, &ae) {
                switch ae.ErrorCode() {
                case "ItemNotFoundException":
                    log.Printf("Data item '%s' not found, skipping", itemId)
                    continue
                case "AccessDenied":
                    return nil, fmt.Errorf("you don't have permission to access data item '%s': %w", itemId, err)
                default:
                    return nil, fmt.Errorf("couldn't get data item '%s': %w", itemId, err)
                }
            }
            return nil, fmt.Errorf("couldn't get data item '%s': %w", itemId, err)
        }
        
        detailedItems = append(detailedItems, *result.{DataItem})
    }
    
    log.Printf("Retrieved details for %d data item(s)", len(detailedItems))
    return detailedItems, nil
}
```

## Error Handling Patterns

### Service-Specific Error Handling
```go
// Handle common AWS service errors with user-friendly messages
func handleServiceError(err error, operation string) error {
    var ae smithy.APIError
    if errors.As(err, &ae) {
        switch ae.ErrorCode() {
        case "AccessDenied", "UnauthorizedOperation":
            return fmt.Errorf("you don't have permission to %s: %w", operation, err)
        case "ResourceNotFoundException", "NoSuchResource":
            return fmt.Errorf("resource not found: %w", err)
        case "BadRequestException", "InvalidParameterValue":
            return fmt.Errorf("invalid request parameters: %w", err)
        case "LimitExceededException", "ThrottlingException":
            return fmt.Errorf("request limit exceeded, please retry: %w", err)
        case "InternalServerError", "ServiceUnavailable":
            return fmt.Errorf("service temporarily unavailable, please retry: %w", err)
        default:
            return fmt.Errorf("couldn't %s: %w", operation, err)
        }
    }
    return fmt.Errorf("couldn't %s: %w", operation, err)
}
```

### Pagination Handling
```go
// Handle paginated responses properly
func (actions *{Service}Actions) ListAllResources(ctx context.Context) ([]types.{ResourceType}, error) {
    var allResources []types.{ResourceType}
    var nextToken *string
    
    for {
        input := &{service}.List{Resources}Input{
            NextToken:  nextToken,
            MaxResults: aws.Int32(100),
        }
        
        result, err := actions.{Service}Client.List{Resources}(ctx, input)
        if err != nil {
            return nil, handleServiceError(err, "list resources")
        }
        
        allResources = append(allResources, result.{Resources}...)
        
        nextToken = result.NextToken
        if nextToken == nil {
            break
        }
    }
    
    return allResources, nil
}
```

## Context Usage Patterns
```go
// Always accept context as first parameter
func (actions *{Service}Actions) Operation(ctx context.Context, params ...interface{}) error {
    // Use context in all AWS SDK calls
    result, err := actions.{Service}Client.SomeOperation(ctx, input)
    if err != nil {
        return err
    }
    return nil
}

// For operations that might take time, check context cancellation
func (actions *{Service}Actions) LongRunningOperation(ctx context.Context) error {
    for {
        select {
        case <-ctx.Done():
            return ctx.Err()
        default:
            // Continue operation
        }
        
        // Perform work
        time.Sleep(time.Second)
    }
}
```

## Testing Support
```go
// Define interfaces for testability
type {Service}ClientAPI interface {
    List{Resources}(ctx context.Context, params *{service}.List{Resources}Input, optFns ...func(*{service}.Options)) (*{service}.List{Resources}Output, error)
    Create{Resource}(ctx context.Context, params *{service}.Create{Resource}Input, optFns ...func(*{service}.Options)) (*{service}.Create{Resource}Output, error)
    // Add other methods as needed
}

// Update actions struct to use interface
type {Service}Actions struct {
    {Service}Client {Service}ClientAPI
}
```

## Documentation Requirements
- **Package documentation**: Describe the purpose and usage of the actions package
- **Struct documentation**: Explain what the actions struct encapsulates
- **Method documentation**: Document parameters, return values, and error conditions
- **Error documentation**: Describe possible error conditions and their meanings
- **Example usage**: Include usage examples in documentation

## Actions Requirements
- ✅ **ALWAYS** use context.Context as the first parameter for all methods
- ✅ **ALWAYS** provide enhanced error handling with user-friendly messages
- ✅ **ALWAYS** include comprehensive logging for operations
- ✅ **ALWAYS** handle pagination properly for list operations
- ✅ **ALWAYS** follow Go naming conventions (PascalCase for exported, camelCase for unexported)
- ✅ **ALWAYS** include proper documentation for all exported methods
- ✅ **ALWAYS** design for testability with interfaces
- ✅ **ALWAYS** validate input parameters where appropriate
- ✅ **ALWAYS** use smithy.APIError for AWS-specific error handling

## Common Patterns
- Encapsulate AWS SDK clients in service-specific structs
- Provide simplified method signatures for common operations
- Handle errors gracefully with informative messages
- Support pagination automatically in list operations
- Include logging for debugging and monitoring
- Design methods to be easily testable
- Follow consistent naming patterns across all actions
- Use context for cancellation and timeouts