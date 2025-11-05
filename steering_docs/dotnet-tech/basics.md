# .NET Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "DotNet-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("DotNet-premium-KB", ".NET implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: Use Console.WriteLine and Console.ReadLine for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Wrapper Classes**: MUST use service wrapper classes for all operations
- **Namespaces**: MUST use file-level namespaces that match the project names
- **Using Statements**: MUST cleanup unused using statements

## File Structure
```
dotnetv4/{Service}/Scenarios/{Service}_Basics/
├── {Service}Basics.cs              # Main scenario file
├── {Service}Basics.csproj          # Project file
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
```csharp
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/// <summary>
/// Purpose
/// 
/// Shows how to use {AWS Service} to {scenario description}. This scenario demonstrates:
/// 
/// 1. {Phase 1 description}
/// 2. {Phase 2 description}
/// 3. {Phase 3 description}
/// 4. {Phase 4 description}
/// 
/// This example uses the AWS SDK for .NET v4 to interact with {AWS Service}.
/// </summary>

using System;
using System.Threading.Tasks;
using Amazon.{Service};
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace Amazon.DocSamples.{Service}
{
    public class {Service}Basics
    {
        private static ILogger logger = null!;
        private static {Service}Wrapper wrapper = null!;
        private static string? resourceId = null;

        /// <summary>
        /// Main entry point for the {AWS Service} basics scenario.
        /// </summary>
        /// <param name="args">Command line arguments.</param>
        public static async Task Main(string[] args)
        {
            using var host = Host.CreateDefaultBuilder(args)
                .ConfigureServices((_, services) =>
                    services.AddAWSService<IAmazon{Service}>()
                    .AddTransient<{Service}Wrapper>()
                )
                .Build();

            logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
                .CreateLogger<{Service}Basics>();

            wrapper = host.Services.GetRequiredService<{Service}Wrapper>();

            await RunScenario();
        }

        /// <summary>
        /// Runs the {AWS Service} basics scenario.
        /// </summary>
        public static async Task RunScenario()
        {
            Console.WriteLine(new string('-', 88));
            Console.WriteLine("Welcome to the {AWS Service} basics scenario!");
            Console.WriteLine(new string('-', 88));
            Console.WriteLine("{Service description and what users will learn}");
            Console.WriteLine();

            try
            {
                await SetupPhase();
                await DemonstrationPhase();
                await ExaminationPhase();
            }
            catch (Exception ex)
            {
                logger.LogError("Scenario failed: {Message}", ex.Message);
                Console.WriteLine($"The scenario encountered an error: {ex.Message}");
            }
            finally
            {
                await CleanupPhase();
            }
        }

        /// <summary>
        /// Setup phase: Implement based on specification's Setup section.
        /// </summary>
        private static async Task SetupPhase()
        {
            Console.WriteLine("Setting up {AWS Service}...");
            Console.WriteLine();
            
            // Example: Check for existing resources (from specification)
            var existingResources = await wrapper.ListResourcesAsync();
            if (existingResources.Count > 0)
            {
                Console.WriteLine($"Found {existingResources.Count} existing resource(s):");
                foreach (var resource in existingResources)
                {
                    Console.WriteLine($"  - {resource}");
                }
                
                Console.Write("Would you like to use an existing resource? (y/n): ");
                var useExisting = Console.ReadLine()?.ToLower() == "y";
                if (useExisting)
                {
                    resourceId = existingResources[0];
                    return;
                }
            }
        }

        /// <summary>
        /// Demonstration phase: Implement operations from specification.
        /// </summary>
        private static async Task DemonstrationPhase()
        {
            Console.WriteLine("Demonstrating {AWS Service} capabilities...");
            Console.WriteLine();
            
            // Implement specific operations from specification
            // Example: Generate sample data if specified
            await wrapper.CreateSampleDataAsync(resourceId);
            Console.WriteLine("✓ Sample data created successfully");
            
            // Wait if specified in the specification
            Console.WriteLine("Waiting for data to be processed...");
            await Task.Delay(5000);
        }

        /// <summary>
        /// Examination phase: Implement data analysis from specification.
        /// </summary>
        private static async Task ExaminationPhase()
        {
            Console.WriteLine("Examining {AWS Service} data...");
            Console.WriteLine();
            
            // List and examine data as specified
            var dataItems = await wrapper.ListDataAsync(resourceId);
            if (dataItems.Count == 0)
            {
                Console.WriteLine("No data found. Data may take a few minutes to appear.");
                return;
            }
            
            Console.WriteLine($"Found {dataItems.Count} data item(s)");
            
            // Get detailed information as specified
            var detailedData = await wrapper.GetDataDetailsAsync(resourceId, dataItems.Take(5).ToList());
            DisplayDataSummary(detailedData);
            
            // Show detailed view if specified
            if (detailedData.Count > 0)
            {
                Console.Write("Would you like to see detailed information? (y/n): ");
                var showDetails = Console.ReadLine()?.ToLower() == "y";
                if (showDetails)
                {
                    DisplayDataDetails(detailedData[0]);
                }
            }
            
            // Filter data as specified
            FilterDataByCriteria(dataItems);
        }

        /// <summary>
        /// Cleanup phase: Implement cleanup options from specification.
        /// </summary>
        private static async Task CleanupPhase()
        {
            if (string.IsNullOrEmpty(resourceId))
                return;
                
            Console.WriteLine("Cleanup options:");
            Console.WriteLine("Note: Deleting the resource will stop all monitoring/processing.");
            
            Console.Write("Would you like to delete the resource? (y/n): ");
            var deleteResource = Console.ReadLine()?.ToLower() == "y";
            
            if (deleteResource)
            {
                try
                {
                    await wrapper.DeleteResourceAsync(resourceId);
                    Console.WriteLine($"✓ Deleted resource: {resourceId}");
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error deleting resource: {ex.Message}");
                }
            }
            else
            {
                Console.WriteLine($"Resource {resourceId} will continue running.");
                Console.WriteLine("You can manage it through the AWS Console or delete it later.");
            }
        }
    }
}
```

## Scenario Phase Structure (Based on Specification)

### Setup Phase
- **Read specification Setup section** for exact requirements
- Check for existing resources as specified
- Create necessary resources using wrapper methods
- Configure service settings per specification
- Verify setup completion as described

### Demonstration Phase  
- **Follow specification Demonstration section** exactly
- Perform core service operations using wrapper methods
- Generate sample data if specified in the specification
- Show service capabilities as outlined
- Provide educational context from specification

### Examination Phase
- **Implement specification Examination section** requirements
- List and examine results using wrapper methods
- Filter and analyze data as specified
- Display detailed information per specification format
- Allow user interaction as described in specification

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Offer cleanup options with warnings from specification
- Handle cleanup errors gracefully using wrapper methods
- Provide alternative management options as specified
- Confirm completion per specification

## User Interaction Patterns

### Question Types
```python
# Yes/No questions
use_existing = q.ask("Use existing resource? (y/n): ", q.is_yesno)

# Text input
resource_name = q.ask("Enter resource name: ")

# Numeric input  
count = q.ask("How many items? ", q.is_int)
```

### Information Display
```python
# Progress indicators
print("✓ Operation completed successfully")
print("⚠ Warning message")
print("✗ Error occurred")

# Formatted output
print("-" * 60)
print(f"Found {len(items)} items:")
for item in items:
    print(f"  • {item['name']}")
```

## Specification-Based Error Handling

### Error Handling from Specification
The specification includes an "Errors" section with specific error codes and handling:

```csharp
// Example error handling based on specification
try
{
    var response = await wrapper.CreateResourceAsync();
    return response;
}
catch (Amazon{Service}Exception ex)
{
    var errorCode = ex.ErrorCode;
    if (errorCode == "BadRequestException")
    {
        // Handle as specified: "Validate input parameters and notify user"
        Console.WriteLine("Invalid configuration. Please check your parameters.");
    }
    else if (errorCode == "InternalServerErrorException")
    {
        // Handle as specified: "Retry operation with exponential backoff"
        Console.WriteLine("Service temporarily unavailable. Retrying...");
        // Implement retry logic
    }
    else
    {
        Console.WriteLine($"Unexpected error: {ex.Message}");
    }
    throw;
}
```

## Scenario Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** include descriptive XML documentation at top explaining scenario steps from specification
- ✅ **ALWAYS** use Console.WriteLine and Console.ReadLine for user interaction
- ✅ **ALWAYS** use service wrapper classes for all AWS operations
- ✅ **ALWAYS** implement proper cleanup in finally block
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification
- ✅ **ALWAYS** handle edge cases (no resources found, etc.) as specified

## Implementation Workflow

### Step-by-Step Implementation Process
1. **Read Specification**: Study `scenarios/basics/{service}/SPECIFICATION.md` thoroughly
2. **Extract API Actions**: Note all API actions listed in "API Actions Used" section
3. **Map to Wrapper Methods**: Ensure wrapper class has methods for all required actions
4. **Implement Phases**: Follow the "Proposed example structure" section exactly
5. **Add Error Handling**: Implement error handling per the "Errors" section
6. **Test Against Specification**: Verify implementation matches specification requirements

### Specification Sections to Implement
- **API Actions Used**: All operations must be available in wrapper class
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