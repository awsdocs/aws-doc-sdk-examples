# .NET Hello Examples Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "DotNet-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("DotNet-premium-KB", ".NET implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct AWS SDK for .NET client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use AWS SDK for .NET client directly, no wrapper classes needed

## File Structure
```
dotnetv4/{Service}/Actions/
├── Hello{Service}.cs           # Hello example file
```

## Hello Example Pattern
```csharp
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// <summary>
/// Purpose
/// 
/// Shows how to get started with {AWS Service} by {basic operation description}.
/// </summary>

using System;
using System.Threading.Tasks;
using Amazon.{Service};
using Amazon.{Service}.Model;

namespace Amazon.DocSamples.{Service}
{
    public class Hello{Service}
    {
        /// <summary>
        /// Use the AWS SDK for .NET to create an {AWS Service} client and 
        /// {basic operation description}.
        /// This example uses the default settings specified in your shared credentials
        /// and config files.
        /// </summary>
        /// <param name="args">Command line arguments.</param>
        public static async Task Main(string[] args)
        {
            try
            {
                // Create service client
                using var {service}Client = new Amazon{Service}Client();
                
                // Perform the most basic operation for this service
                var response = await {service}Client.{BasicOperation}Async();
                
                Console.WriteLine("Hello, {AWS Service}!");
                // Display appropriate result information
                
            }
            catch (Amazon{Service}Exception ex)
            {
                if (ex.ErrorCode == "UnauthorizedOperation")
                {
                    Console.WriteLine("You don't have permission to access {AWS Service}.");
                }
                else
                {
                    Console.WriteLine($"Couldn't access {AWS Service}. Error: {ex.Message}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"An unexpected error occurred: {ex.Message}");
            }
        }
    }
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
- ✅ **Must use direct AWS SDK for .NET client calls**
- ✅ **Must include proper XML documentation**

## Common Patterns
- Always use `new Amazon{Service}Client()` directly
- Include comprehensive error handling with try-catch blocks
- Provide user-friendly output messages using Console.WriteLine
- Handle both service-specific and general exceptions
- Keep it as simple as possible - no additional classes or complexity
- Use async/await pattern for all AWS operations