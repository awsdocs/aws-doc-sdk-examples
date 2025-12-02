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
├── Hello{Service}.cs           # Hello example file (with Main method)
├── {Service}Wrapper.cs         # Service wrapper class
└── {Service}Actions.csproj     # Actions project file (OutputType=Exe)
```

**CRITICAL:** 
- ✅ Hello example MUST be in the Actions project
- ✅ Actions project MUST have `<OutputType>Exe</OutputType>` to support Main method
- ✅ NO separate Hello project should be created
- ✅ Actions project MUST include Microsoft.Extensions packages for dependency injection

**Required NuGet Packages for Actions Project:**
```xml
<ItemGroup>
  <PackageReference Include="AWSSDK.Extensions.NETCore.Setup" Version="3.7.301" />
  <PackageReference Include="AWSSDK.{Service}" Version="3.7.500" />
  <PackageReference Include="Microsoft.Extensions.DependencyInjection" Version="9.0.0" />
  <PackageReference Include="Microsoft.Extensions.Hosting" Version="9.0.0" />
  <PackageReference Include="Microsoft.Extensions.Logging" Version="9.0.0" />
  <PackageReference Include="Microsoft.Extensions.Logging.Console" Version="9.0.0" />
  <PackageReference Include="Microsoft.Extensions.Logging.Debug" Version="9.0.0" />
</ItemGroup>
```

## Hello Example Pattern
```csharp
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.{Service};
using Amazon.{Service}.Model;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace {Service}Actions;

/// <summary>
/// Hello Amazon {Service} example.
/// </summary>
public class Hello{Service}
{
    private static ILogger logger = null!;

    // snippet-start:[{Service}.dotnetv4.Hello]
    /// <summary>
    /// Main method to run the Hello Amazon {Service} example.
    /// </summary>
    /// <param name="args">Command line arguments (not used).</param>
    public static async Task Main(string[] args)
    {
        // Set up dependency injection for Amazon {Service}.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<Amazon{Service}Client>())
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<Hello{Service}>();

        var {service}Client = host.Services.GetRequiredService<Amazon{Service}Client>();

        Console.Clear();
        Console.WriteLine("Hello, Amazon {Service}! Let's list available {resources}:");
        Console.WriteLine();

        var {resources} = new List<{Resource}>();

        try
        {
            // Use pagination to retrieve all {resources}
            var {resources}Paginator = {service}Client.Paginators.List{Resources}(new List{Resources}Request());
            
            await foreach (var response in {resources}Paginator.Responses)
            {
                {resources}.AddRange(response.{Resources});
            }

            Console.WriteLine($"{{{resources}.Count}} {resource}(s) retrieved.");
            
            foreach (var {resource} in {resources})
            {
                Console.WriteLine($"\t{{{resource}.Name}}");
            }
        }
        catch (Amazon{Service}Exception ex)
        {
            logger.LogError("Error listing {resources}: {Message}", ex.Message);
            Console.WriteLine($"Couldn't list {resources}. Error: {ex.Message}");
        }
        catch (Exception ex)
        {
            logger.LogError("An error occurred: {Message}", ex.Message);
            Console.WriteLine($"An error occurred: {ex.Message}");
        }
    }
    // snippet-end:[{Service}.dotnetv4.Hello]
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
- ✅ **Must use dependency injection with Host.CreateDefaultBuilder**
- ✅ **Must use pagination for list operations**
- ✅ **Must include proper XML documentation**
- ✅ **Must use correct snippet tag format**: `[{Service}.dotnetv4.Hello]`

## Common Patterns
- Use dependency injection with Host.CreateDefaultBuilder
- Use pagination to retrieve all results
- Include comprehensive error handling with try-catch blocks
- Provide user-friendly output messages using Console.WriteLine
- Handle both service-specific and general exceptions
- Keep it as simple as possible - all logic in Main method
- Use async/await pattern for all AWS operations

## Snippet Tag Format
**CRITICAL**: Use the correct snippet tag format:

```csharp
// ✅ CORRECT
// snippet-start:[{Service}.dotnetv4.HelloSteering]
public static async Task Main(string[] args)
{
    // Implementation
}
// snippet-end:[{Service}.dotnetv4.HelloSteering]

// ❌ WRONG - Old format
// snippet-start:[dotnetv4.example_code.{Service}.HelloSteering]
// snippet-end:[dotnetv4.example_code.{Service}.HelloSteering]
```

**Format**: `[{Service}.dotnetv4.Hello]`
- Service name in PascalCase (e.g., Redshift, S3, DynamoDB)
- Always use `.dotnetv4.Hello` for Hello examples