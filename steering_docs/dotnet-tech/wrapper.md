# .NET Service Wrapper Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "dotnet-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("dotnet-premium-KB", ".NET implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate service wrapper classes that encapsulate AWS service functionality with proper error handling and logging.

## Requirements
- **MANDATORY**: Every service MUST have a wrapper class
- **Error Handling**: Handle service-specific errors appropriately
- **Logging**: Include comprehensive logging for all operations
- **Async Methods**: Use async/await pattern for all AWS operations

## File Structure
```
dotnetv4/{Service}/Actions/
├── {Service}Wrapper.cs         # Service wrapper class
├── {Service}Wrapper.csproj     # Project file
```

## Wrapper Class Pattern
```csharp
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// <summary>
/// Purpose
/// 
/// Shows how to use the AWS SDK for .NET with {AWS Service} to
/// {service description and main use cases}.
/// </summary>

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.{Service};
using Amazon.{Service}.Model;
using Microsoft.Extensions.Logging;

namespace Amazon.DocSamples.{Service}
{
    // snippet-start:[dotnetv4.example_code.{service}.{Service}Wrapper]
    /// <summary>
    /// Encapsulates {AWS Service} functionality.
    /// </summary>
    public class {Service}Wrapper
    {
        private readonly IAmazon{Service} _{service}Client;
        private readonly ILogger<{Service}Wrapper> _logger;

        /// <summary>
        /// Initializes a new instance of the {Service}Wrapper class.
        /// </summary>
        /// <param name="{service}Client">The {AWS Service} client.</param>
        /// <param name="logger">The logger instance.</param>
        public {Service}Wrapper(IAmazon{Service} {service}Client, ILogger<{Service}Wrapper> logger)
        {
            _{service}Client = {service}Client;
            _logger = logger;
        }

    }
    // snippet-end:[dotnetv4.example_code.{service}.{Service}Wrapper]

    // Individual action methods follow...
}
```

## Action Method Pattern
```csharp
        // snippet-start:[dotnetv4.example_code.{service}.{ActionName}]
        /// <summary>
        /// {Action description}.
        /// </summary>
        /// <param name="param">Parameter description.</param>
        /// <returns>Response description.</returns>
        public async Task<{ActionName}Response> {ActionMethod}Async(string param)
        {
            try
            {
                var request = new {ActionName}Request
                {
                    Parameter = param
                };
                
                var response = await _{service}Client.{ActionName}Async(request);
                _logger.LogInformation("{Action} completed successfully", "{ActionName}");
                return response;
            }
            catch (Amazon{Service}Exception ex)
            {
                var errorCode = ex.ErrorCode;
                if (errorCode == "SpecificError")
                {
                    _logger.LogError("Specific error handling message");
                }
                else if (errorCode == "AnotherSpecificError")
                {
                    _logger.LogError("Another specific error handling message");
                }
                else
                {
                    _logger.LogError("Error in {ActionName}: {Message}", ex.Message);
                }
                throw;
            }
        }
        // snippet-end:[dotnetv4.example_code.{service}.{ActionName}]
```

## Paginator Pattern for List Operations
```csharp
        // snippet-start:[dotnetv4.example_code.{service}.List{Resources}]
        /// <summary>
        /// Lists all {resources} using pagination to retrieve complete results.
        /// </summary>
        /// <returns>A list of all {resources}.</returns>
        public async Task<List<{Resource}>> List{Resources}Async()
        {
            try
            {
                var {resources} = new List<{Resource}>();
                var {resources}Paginator = _{service}Client.Paginators.List{Resources}(new List{Resources}Request());
                
                await foreach (var response in {resources}Paginator.Responses)
                {
                    {resources}.AddRange(response.{Resources});
                }
                
                _logger.LogInformation("Retrieved {Count} {resources}", {resources}.Count);
                return {resources};
            }
            catch (Amazon{Service}Exception ex)
            {
                var errorCode = ex.ErrorCode;
                if (errorCode == "BadRequestException")
                {
                    _logger.LogError("Invalid request parameters for listing {resources}");
                }
                else
                {
                    _logger.LogError("Error listing {resources}: {Message}", ex.Message);
                }
                throw;
            }
        }
        // snippet-end:[dotnetv4.example_code.{service}.List{Resources}]
```

## Paginator with Parameters Pattern
```csharp
        // snippet-start:[dotnetv4.example_code.{service}.List{Resources}WithFilter]
        /// <summary>
        /// Lists {resources} with optional filtering, using pagination.
        /// </summary>
        /// <param name="filterCriteria">Optional criteria to filter results.</param>
        /// <returns>A list of filtered {resources}.</returns>
        public async Task<List<{Resource}>> List{Resources}WithFilterAsync(FilterCriteria? filterCriteria = null)
        {
            try
            {
                var request = new List{Resources}Request();
                if (filterCriteria != null)
                {
                    request.FilterCriteria = filterCriteria;
                }
                
                var {resources} = new List<{Resource}>();
                var {resources}Paginator = _{service}Client.Paginators.List{Resources}(request);
                
                await foreach (var response in {resources}Paginator.Responses)
                {
                    {resources}.AddRange(response.{Resources});
                }
                
                _logger.LogInformation("Retrieved {Count} filtered {resources}", {resources}.Count);
                return {resources};
            }
            catch (Amazon{Service}Exception ex)
            {
                _logger.LogError("Error listing {resources} with filter: {Message}", ex.Message);
                throw;
            }
        }
        // snippet-end:[dotnetv4.example_code.{service}.List{Resources}WithFilter]
```

## Error Handling Requirements

### Service-Specific Errors
Based on the service specification, handle these error types:
- **BadRequestException**: Validate input parameters
- **InternalServerErrorException**: Retry with exponential backoff
- **ResourceNotFoundException**: Handle missing resources gracefully
- **AccessDeniedException**: Provide clear permission guidance

### Error Handling Pattern
```csharp
try
{
    var response = await _{service}Client.OperationAsync();
    return response;
}
catch (Amazon{Service}Exception ex)
{
    var errorCode = ex.ErrorCode;
    if (errorCode == "BadRequestException")
    {
        _logger.LogError("Validate input parameters and notify user");
    }
    else if (errorCode == "InternalServerErrorException")
    {
        _logger.LogError("Retry operation with exponential backoff");
    }
    else
    {
        _logger.LogError("Error in operation: {Message}", ex.Message);
    }
    throw;
}
```

## Wrapper Class Requirements
- ✅ **ALWAYS** include proper logging for all operations
- ✅ **ALWAYS** provide `FromClient()` static method
- ✅ **ALWAYS** handle service-specific errors per specification
- ✅ **ALWAYS** include comprehensive XML documentation
- ✅ **ALWAYS** use async/await pattern for all AWS operations
- ✅ **ALWAYS** follow the established naming conventions
- ✅ **ALWAYS** use pagination for list operations when available
- ✅ **ALWAYS** retrieve complete results, not just first page

## Method Naming Conventions
- Use PascalCase for method names
- Match AWS API operation names with Async suffix (e.g., `ListBucketsAsync` for `ListBuckets`)
- Use descriptive parameter names
- Return appropriate data types (Task<Response>, Task<List<T>>, etc.)

## Pagination Requirements
- **MANDATORY**: Use AWS SDK for .NET paginators for list operations when available
- **Complete Results**: Ensure all pages are retrieved, not just first page
- **Efficient**: Use built-in paginator pattern with `await foreach`

## Pagination Usage Guidelines

### When to Use Pagination
- ✅ **List Operations**: Always use for operations that return lists of resources
- ✅ **Large Result Sets**: Essential for services that may return many items
- ✅ **Complete Data**: Ensures all results are retrieved, not just first page

### How to Implement Pagination
```csharp
var allItems = new List<Item>();
var itemsPaginator = _client.Paginators.ListItems(new ListItemsRequest());

await foreach (var response in itemsPaginator.Responses)
{
    allItems.AddRange(response.Items);
}
```

### Common Paginated Operations
- `ListBuckets` → Use pagination if available
- `ListObjects` → Always use pagination (can return thousands of objects)
- `ListTables` → Use pagination for complete results
- `DescribeInstances` → Use pagination for large environments
- `ListFunctions` → Use pagination for complete function list

## Documentation Requirements
- Include class-level XML documentation explaining service purpose
- Document all parameters with XML tags and descriptions
- Document return values with XML tags and descriptions
- Include usage examples in XML documentation where helpful
- Use proper snippet tags for documentation generation
- Document pagination behavior in list method XML documentation