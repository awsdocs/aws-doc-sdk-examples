# .NET Feature Scenario Generation

## Purpose
Generate feature scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Target Directory
**IMPORTANT**: All new feature scenarios MUST be created in the `dotnetv4` directory, NOT `dotnetv3`.

- **New scenarios**: `dotnetv4/{Service}/`
- **Legacy examples**: `dotnetv3/{Service}/` (Must NOT add new examples here)

## Requirements
- **Specification-Driven**: MUST read the `scenarios/features/{service_feature}/SPECIFICATION.md`
- **Interactive**: Use Console.WriteLine and Console.ReadLine for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Wrapper Classes**: MUST use service wrapper classes for all operations
- **CloudFormation**: Deploy resources using CloudFormation stacks when specified
- **Namespaces**: MUST use file-level namespaces that match the project names
- **Using Statements**: MUST cleanup unused using statements

## Project Structure

Feature scenarios use a multi-project structure with separate projects for actions, scenarios, and tests:

```
dotnetv4/{Service}/
├── {Service}.sln                           # Solution file
├── Actions/
│   ├── {Service}Wrapper.cs                 # Wrapper class for service operations
│   ├── Hello{Service}.cs                   # Hello world example (optional)
│   └── {Service}Actions.csproj             # Actions project file
├── Scenarios/
│   ├── {Service}Workflow.cs                # Main workflow/scenario file
│   ├── README.md                           # Scenario documentation
│   └── {Service}Scenario.csproj            # Scenario project file (references Actions)
└── Tests/
    ├── {Service}WorkflowTests.cs           # Unit tests for workflow
    ├── Usings.cs                           # Global usings for tests
    └── {Service}Tests.csproj               # Test project file (references Scenarios)
```

**Note**: Use `dotnetv4` for all new feature scenarios. The `dotnetv3` directory is for legacy examples only.

## MANDATORY Pre-Implementation Steps

### Step 1: Read Scenario Specification
**CRITICAL**: Always read `scenarios/features/{servicefeature}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-step scenario description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Workflow Class Pattern

### Implementation Pattern Based on SPECIFICATION.md

```csharp
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[{Service}.dotnetv4.{Service}Workflow]
using Amazon.{Service};
using Amazon.CloudFormation;
using Amazon.CloudFormation.Model;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using {Service}Actions;

namespace {Service}Scenario;

public class {Service}Workflow
{
    /*
    Before running this .NET code example, set up your development environment, including your credentials.
    This .NET code example performs the following tasks for the {AWS Service} workflow:

    1. Prepare the Application:
       - {Setup step 1 from specification}
       - {Setup step 2 from specification}
       - Deploy the Cloud Formation template for resource creation.
       - Store the outputs of the stack into variables for use in the scenario.

    2. {Phase 2 Name}:
       - {Phase 2 description from specification}

    3. {Phase 3 Name}:
       - {Phase 3 description from specification}

    4. Clean up:
       - Prompt the user for y/n answer if they want to destroy the stack and clean up all resources.
       - Delete resources created during the workflow.
       - Destroy the Cloud Formation stack and wait until the stack has been removed.
    */

    public static ILogger<{Service}Workflow> _logger = null!;
    public static {Service}Wrapper _wrapper = null!;
    public static IAmazonCloudFormation _amazonCloudFormation = null!;

    private static string _roleArn = null!;
    private static string _targetArn = null!;

    public static bool _interactive = true;
    private static string _stackName = "default-{service}-scenario-stack-name";
    private static string _stackResourcePath = "../../../../../../scenarios/features/{service_feature}/resources/cfn_template.yaml";

    public static async Task Main(string[] args)
    {
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazon{Service}>()
                    .AddAWSService<IAmazonCloudFormation>()
                    .AddTransient<{Service}Wrapper>()
            )
            .Build();

        if (_interactive)
        {
            _logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
                .CreateLogger<{Service}Workflow>();

            _wrapper = host.Services.GetRequiredService<{Service}Wrapper>();
            _amazonCloudFormation = host.Services.GetRequiredService<IAmazonCloudFormation>();
        }

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the {AWS Service Feature} Scenario.");
        Console.WriteLine(new string('-', 80));

        try
        {
            Console.WriteLine(new string('-', 80));
            var prepareSuccess = await PrepareApplication();
            Console.WriteLine(new string('-', 80));

            if (prepareSuccess)
            {
                Console.WriteLine(new string('-', 80));
                await Phase2();
                Console.WriteLine(new string('-', 80));

                Console.WriteLine(new string('-', 80));
                await Phase3();
                Console.WriteLine(new string('-', 80));
            }

            Console.WriteLine(new string('-', 80));
            await Cleanup();
            Console.WriteLine(new string('-', 80));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "There was a problem with the scenario, initiating cleanup...");
            _interactive = false;
            await Cleanup();
        }

        Console.WriteLine("{AWS Service} scenario completed.");
    }

    /// <summary>
    /// Prepares the application by creating the necessary resources.
    /// </summary>
    /// <returns>True if the application was prepared successfully.</returns>
    public static async Task<bool> PrepareApplication()
    {
        Console.WriteLine("Preparing the application...");
        try
        {
            // Prompt the user for required input (e.g., email, parameters)
            Console.WriteLine("\nThis example creates resources in a CloudFormation stack.");
            
            var userInput = PromptUserForInput();

            // Prompt the user for a name for the CloudFormation stack
            _stackName = PromptUserForStackName();

            // Deploy the CloudFormation stack
            var deploySuccess = await DeployCloudFormationStack(_stackName, userInput);

            if (deploySuccess)
            {
                // Create additional resources if needed
                Console.WriteLine("Application preparation complete.");
                return true;
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "An error occurred while preparing the application.");
        }
        Console.WriteLine("Application preparation failed.");
        return false;
    }

    /// <summary>
    /// Deploys the CloudFormation stack with the necessary resources.
    /// </summary>
    /// <param name="stackName">The name of the CloudFormation stack.</param>
    /// <param name="parameter">Parameter value for the stack.</param>
    /// <returns>True if the stack was deployed successfully.</returns>
    private static async Task<bool> DeployCloudFormationStack(string stackName, string parameter)
    {
        Console.WriteLine($"\nDeploying CloudFormation stack: {stackName}");

        try
        {
            var request = new CreateStackRequest
            {
                StackName = stackName,
                TemplateBody = await File.ReadAllTextAsync(_stackResourcePath),
                Capabilities = { Capability.CAPABILITY_NAMED_IAM }
            };

            // If parameters are provided, set them
            if (!string.IsNullOrWhiteSpace(parameter))
            {
                request.Parameters = new List<Parameter>()
                {
                    new() { ParameterKey = "parameterName", ParameterValue = parameter }
                };
            }

            var response = await _amazonCloudFormation.CreateStackAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"CloudFormation stack creation started: {stackName}");

                // Wait for the stack to be in CREATE_COMPLETE state
                bool stackCreated = await WaitForStackCompletion(response.StackId);

                if (stackCreated)
                {
                    // Retrieve the output values
                    var success = await GetStackOutputs(response.StackId);
                    return success;
                }
                else
                {
                    _logger.LogError($"CloudFormation stack creation failed: {stackName}");
                    return false;
                }
            }
            else
            {
                _logger.LogError($"Failed to create CloudFormation stack: {stackName}");
                return false;
            }
        }
        catch (AlreadyExistsException)
        {
            _logger.LogWarning($"CloudFormation stack '{stackName}' already exists. Please provide a unique name.");
            var newStackName = PromptUserForStackName();
            return await DeployCloudFormationStack(newStackName, parameter);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"An error occurred while deploying the CloudFormation stack: {stackName}");
            return false;
        }
    }

    /// <summary>
    /// Waits for the CloudFormation stack to be in the CREATE_COMPLETE state.
    /// </summary>
    /// <param name="stackId">The ID of the CloudFormation stack.</param>
    /// <returns>True if the stack was created successfully.</returns>
    private static async Task<bool> WaitForStackCompletion(string stackId)
    {
        int retryCount = 0;
        const int maxRetries = 10;
        const int retryDelay = 30000; // 30 seconds.

        while (retryCount < maxRetries)
        {
            var describeStacksRequest = new DescribeStacksRequest
            {
                StackName = stackId
            };

            var describeStacksResponse = await _amazonCloudFormation.DescribeStacksAsync(describeStacksRequest);

            if (describeStacksResponse.Stacks.Count > 0)
            {
                if (describeStacksResponse.Stacks[0].StackStatus == StackStatus.CREATE_COMPLETE)
                {
                    Console.WriteLine("CloudFormation stack creation complete.");
                    return true;
                }
                if (describeStacksResponse.Stacks[0].StackStatus == StackStatus.CREATE_FAILED ||
                         describeStacksResponse.Stacks[0].StackStatus == StackStatus.ROLLBACK_COMPLETE)
                {
                    Console.WriteLine("CloudFormation stack creation failed.");
                    return false;
                }
            }

            Console.WriteLine("Waiting for CloudFormation stack creation to complete...");
            await Task.Delay(retryDelay);
            retryCount++;
        }

        _logger.LogError("Timed out waiting for CloudFormation stack creation to complete.");
        return false;
    }

    /// <summary>
    /// Retrieves the output values from the CloudFormation stack.
    /// </summary>
    /// <param name="stackId">The ID of the CloudFormation stack.</param>
    private static async Task<bool> GetStackOutputs(string stackId)
    {
        try
        {
            var describeStacksRequest = new DescribeStacksRequest { StackName = stackId };

            var describeStacksResponse =
                await _amazonCloudFormation.DescribeStacksAsync(describeStacksRequest);

            if (describeStacksResponse.Stacks.Count > 0)
            {
                var stack = describeStacksResponse.Stacks[0];
                _roleArn = GetStackOutputValue(stack, "RoleARN");
                _targetArn = GetStackOutputValue(stack, "TargetARN");
                return true;
            }
            else
            {
                _logger.LogError($"No stack found for stack outputs: {stackId}");
                return false;
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(
                ex, $"Failed to retrieve CloudFormation stack outputs: {stackId}");
            return false;
        }
    }

    /// <summary>
    /// Get an output value by key from a CloudFormation stack.
    /// </summary>
    /// <param name="stack">The CloudFormation stack.</param>
    /// <param name="outputKey">The key of the output.</param>
    /// <returns>The value as a string.</returns>
    private static string GetStackOutputValue(Stack stack, string outputKey)
    {
        var output = stack.Outputs.First(o => o.OutputKey == outputKey);
        var outputValue = output.OutputValue;
        Console.WriteLine($"Stack output {outputKey}: {outputValue}");
        return outputValue;
    }

    /// <summary>
    /// Cleans up the resources created during the scenario.
    /// </summary>
    /// <returns>True if the cleanup was successful.</returns>
    public static async Task<bool> Cleanup()
    {
        // Prompt the user to confirm cleanup.
        var cleanup = !_interactive || GetYesNoResponse(
            "Do you want to delete all resources created by this scenario? (y/n) ");
        if (cleanup)
        {
            try
            {
                // Delete scenario-specific resources first
                
                // Destroy the CloudFormation stack and wait for it to be removed.
                var stackDeleteSuccess = await DeleteCloudFormationStack(_stackName, false);

                return stackDeleteSuccess;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex,
                    "An error occurred while cleaning up the resources.");
                return false;
            }
        }
        _logger.LogInformation("{Service} scenario is complete.");
        return true;
    }

    /// <summary>
    /// Delete the resources in the stack and wait for confirmation.
    /// </summary>
    /// <param name="stackName">The name of the stack.</param>
    /// <param name="forceDelete">True to force delete the stack.</param>
    /// <returns>True if successful.</returns>
    private static async Task<bool> DeleteCloudFormationStack(string stackName, bool forceDelete)
    {
        var request = new DeleteStackRequest
        {
            StackName = stackName,
        };

        if (forceDelete)
        {
            request.DeletionMode = DeletionMode.FORCE_DELETE_STACK;
        }

        await _amazonCloudFormation.DeleteStackAsync(request);
        Console.WriteLine($"CloudFormation stack '{_stackName}' is being deleted. This may take a few minutes.");

        bool stackDeleted = await WaitForStackDeletion(_stackName, forceDelete);

        if (stackDeleted)
        {
            Console.WriteLine($"CloudFormation stack '{_stackName}' has been deleted.");
            return true;
        }
        else
        {
            _logger.LogError($"Failed to delete CloudFormation stack '{_stackName}'.");
            return false;
        }
    }

    /// <summary>
    /// Wait for the stack to be deleted.
    /// </summary>
    /// <param name="stackName">The name of the stack.</param>
    /// <param name="forceDelete">True to force delete the stack.</param>
    /// <returns>True if successful.</returns>
    private static async Task<bool> WaitForStackDeletion(string stackName, bool forceDelete)
    {
        int retryCount = 0;
        const int maxRetries = 10;
        const int retryDelay = 30000; // 30 seconds

        while (retryCount < maxRetries)
        {
            var describeStacksRequest = new DescribeStacksRequest
            {
                StackName = stackName
            };

            try
            {
                var describeStacksResponse = await _amazonCloudFormation.DescribeStacksAsync(describeStacksRequest);

                if (describeStacksResponse.Stacks.Count == 0 || describeStacksResponse.Stacks[0].StackStatus == StackStatus.DELETE_COMPLETE)
                {
                    return true;
                }
                if (!forceDelete && describeStacksResponse.Stacks[0].StackStatus == StackStatus.DELETE_FAILED)
                {
                    // Try one time to force delete.
                    return await DeleteCloudFormationStack(stackName, true);
                }
            }
            catch (AmazonCloudFormationException ex) when (ex.ErrorCode == "ValidationError")
            {
                // Stack does not exist, so it has been successfully deleted.
                return true;
            }

            Console.WriteLine($"Waiting for CloudFormation stack '{stackName}' to be deleted...");
            await Task.Delay(retryDelay);
            retryCount++;
        }

        _logger.LogError($"Timed out waiting for CloudFormation stack '{stackName}' to be deleted.");
        return false;
    }

    /// <summary>
    /// Helper method to get a yes or no response from the user.
    /// </summary>
    /// <param name="question">The question string to print on the console.</param>
    /// <returns>True if the user responds with a yes.</returns>
    private static bool GetYesNoResponse(string question)
    {
        Console.WriteLine(question);
        var ynResponse = Console.ReadLine();
        var response = ynResponse != null && ynResponse.Equals("y", StringComparison.InvariantCultureIgnoreCase);
        return response;
    }

    /// <summary>
    /// Prompt the user for a non-empty stack name.
    /// </summary>
    /// <returns>The valid stack name</returns>
    private static string PromptUserForStackName()
    {
        Console.WriteLine("Enter a name for the AWS Cloud Formation Stack: ");
        if (_interactive)
        {
            string stackName = Console.ReadLine()!;
            var regex = "[a-zA-Z][-a-zA-Z0-9]|arn:[-a-zA-Z0-9:/._+]";
            if (!Regex.IsMatch(stackName, regex))
            {
                Console.WriteLine(
                    $"Invalid stack name. Please use a name that matches the pattern {regex}.");
                return PromptUserForStackName();
            }

            return stackName;
        }
        // Used when running without user prompts.
        return _stackName;
    }

    /// <summary>
    /// Prompt the user for required input.
    /// </summary>
    /// <returns>The user input value</returns>
    private static string PromptUserForInput()
    {
        if (_interactive)
        {
            Console.WriteLine("Enter required input: ");
            string input = Console.ReadLine()!;
            // Add validation as needed
            return input;
        }
        // Used when running without user prompts.
        return "";
    }
}
// snippet-end:[{Service}.dotnetv4.{Service}Workflow]
```

## Project Files

### Actions Project (.csproj)

```xml
<Project Sdk="Microsoft.NET.Sdk">

  <PropertyGroup>
    <OutputType>Exe</OutputType>
    <TargetFramework>net8.0</TargetFramework>
    <ImplicitUsings>enable</ImplicitUsings>
    <Nullable>enable</Nullable>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="AWSSDK.{Service}" Version="3.7.*" />
    <PackageReference Include="AWSSDK.Extensions.NETCore.Setup" Version="3.7.*" />
    <PackageReference Include="Microsoft.Extensions.DependencyInjection" Version="8.0.*" />
    <PackageReference Include="Microsoft.Extensions.Hosting" Version="8.0.*" />
  </ItemGroup>

</Project>
```

### Scenarios Project (.csproj)

```xml
<Project Sdk="Microsoft.NET.Sdk">

  <PropertyGroup>
    <OutputType>Exe</OutputType>
    <TargetFramework>net6.0</TargetFramework>
    <ImplicitUsings>enable</ImplicitUsings>
    <Nullable>enable</Nullable>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="AWSSDK.CloudFormation" Version="3.7.*" />
    <PackageReference Include="AWSSDK.Extensions.NETCore.Setup" Version="3.7.*" />
    <PackageReference Include="Microsoft.Extensions.DependencyInjection" Version="8.0.*" />
    <PackageReference Include="Microsoft.Extensions.Hosting" Version="8.0.*" />
  </ItemGroup>

  <ItemGroup>
    <ProjectReference Include="..\Actions\{Service}Actions.csproj" />
  </ItemGroup>

</Project>
```

### Tests Project (.csproj)

```xml
<Project Sdk="Microsoft.NET.Sdk">

  <PropertyGroup>
    <TargetFramework>net8.0</TargetFramework>
    <ImplicitUsings>enable</ImplicitUsings>
    <Nullable>enable</Nullable>
    <IsPackable>false</IsPackable>
    <IsTestProject>true</IsTestProject>
    <NoWarn>$(NoWarn);NETSDK1206</NoWarn>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="Microsoft.Extensions.Configuration.Json" Version="8.0.*" />
    <PackageReference Include="Microsoft.NET.Test.Sdk" Version="17.*" />
    <PackageReference Include="Moq" Version="4.*" />
    <PackageReference Include="xunit" Version="2.*" />
    <PackageReference Include="Xunit.Extensions.Ordering" Version="1.*" />
    <PackageReference Include="xunit.runner.visualstudio" Version="2.*">
      <IncludeAssets>runtime; build; native; contentfiles; analyzers; buildtransitive</IncludeAssets>
      <PrivateAssets>all</PrivateAssets>
    </PackageReference>
    <PackageReference Include="coverlet.collector" Version="6.*">
      <IncludeAssets>runtime; build; native; contentfiles; analyzers; buildtransitive</IncludeAssets>
      <PrivateAssets>all</PrivateAssets>
    </PackageReference>
  </ItemGroup>

  <ItemGroup>
    <Content Include="testsettings.*.json">
      <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>
      <DependentUpon>testsettings.json</DependentUpon>
    </Content>
  </ItemGroup>

  <ItemGroup>
    <ProjectReference Include="..\Scenarios\{Service}Scenario.csproj" />
  </ItemGroup>

</Project>
```

## Workflow Phase Structure (Based on Specification)

### Prepare Application Phase
- **Read specification Setup section** for exact requirements
- Prompt user for required input (email, parameters, etc.)
- Prompt user for CloudFormation stack name
- Deploy CloudFormation stack with resources
- Wait for stack creation to complete
- Retrieve stack outputs (ARNs, IDs, etc.)
- Create additional resources if needed (schedule groups, etc.)
- Verify setup completion

### Demonstration Phases
- **Follow specification phases** exactly
- Implement each phase as a separate method
- Use wrapper methods for all service operations
- Prompt user for input as specified
- Display progress and results
- Handle errors gracefully
- Allow user to proceed at their own pace

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Prompt user to confirm cleanup
- Delete scenario-specific resources first
- Delete CloudFormation stack
- Wait for stack deletion to complete
- Handle deletion errors (retry with force delete if needed)
- Confirm completion

## CloudFormation Integration

### Stack Deployment
- Store CloudFormation template path in a constant
- Template should be in `scenarios/features/{service_feature}/resources/cfn_template.yaml`
- Use relative path from Scenarios project: `"../../../../../../scenarios/features/{service_feature}/resources/cfn_template.yaml"`
- Deploy stack with `CAPABILITY_NAMED_IAM` capability
- Pass user input as stack parameters
- Handle `AlreadyExistsException` by prompting for new stack name

### Stack Output Retrieval
- Retrieve outputs after stack creation completes
- Store output values in static fields for use throughout workflow
- Common outputs: Role ARNs, Topic ARNs, Resource IDs
- Display output values to console for user visibility

### Stack Deletion
- Delete stack during cleanup phase
- Wait for deletion to complete
- Handle `DELETE_FAILED` status by retrying with force delete
- Catch `ValidationError` exception (indicates stack already deleted)

## User Interaction Patterns

### Question Types
```csharp
// Yes/No questions
private static bool GetYesNoResponse(string question)
{
    Console.WriteLine(question);
    var ynResponse = Console.ReadLine();
    var response = ynResponse != null && ynResponse.Equals("y", StringComparison.InvariantCultureIgnoreCase);
    return response;
}

// Text input with validation
private static string PromptUserForResourceName(string prompt)
{
    if (_interactive)
    {
        Console.WriteLine(prompt);
        string resourceName = Console.ReadLine()!;
        var regex = "[0-9a-zA-Z-_.]+";
        if (!Regex.IsMatch(resourceName, regex))
        {
            Console.WriteLine($"Invalid resource name. Please use a name that matches the pattern {regex}.");
            return PromptUserForResourceName(prompt);
        }
        return resourceName!;
    }
    // Used when running without user prompts.
    return "resource-" + Guid.NewGuid();
}

// Numeric input
private static int PromptUserForInteger(string prompt)
{
    if (_interactive)
    {
        Console.WriteLine(prompt);
        string stringResponse = Console.ReadLine()!;
        if (string.IsNullOrWhiteSpace(stringResponse) ||
            !Int32.TryParse(stringResponse, out var intResponse))
        {
            Console.WriteLine($"Invalid integer. ");
            return PromptUserForInteger(prompt);
        }
        return intResponse!;
    }
    // Used when running without user prompts.
    return 1;
}
```

### Information Display
```csharp
// Section separators
Console.WriteLine(new string('-', 80));

// Progress indicators
Console.WriteLine($"✓ Operation completed successfully");
Console.WriteLine($"Waiting for operation to complete...");

// Formatted output
Console.WriteLine($"Found {count} items:");
foreach (var item in items)
{
    Console.WriteLine($"  - {item}");
}
```

## Wrapper Class Pattern

### Wrapper Class Structure
```csharp
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[{Service}.dotnetv4.{Service}Wrapper]
using Amazon.{Service};
using Amazon.{Service}.Model;
using Microsoft.Extensions.Logging;

namespace {Service}Actions;

/// <summary>
/// Wrapper class for {AWS Service} operations.
/// </summary>
public class {Service}Wrapper
{
    private readonly IAmazon{Service} _amazon{Service};
    private readonly ILogger<{Service}Wrapper> _logger;

    /// <summary>
    /// Constructor for the {Service}Wrapper class.
    /// </summary>
    /// <param name="amazon{Service}">The injected {Service} client.</param>
    /// <param name="logger">The injected logger.</param>
    public {Service}Wrapper(IAmazon{Service} amazon{Service}, ILogger<{Service}Wrapper> logger)
    {
        _amazon{Service} = amazon{Service};
        _logger = logger;
    }

    // snippet-start:[{Service}.dotnetv4.OperationName]
    /// <summary>
    /// Description of what this operation does.
    /// </summary>
    /// <param name="paramName">Description of parameter.</param>
    /// <returns>Description of return value.</returns>
    public async Task<bool> OperationAsync(string paramName)
    {
        try
        {
            var request = new OperationRequest
            {
                Parameter = paramName
            };

            var response = await _amazon{Service}.OperationAsync(request);

            Console.WriteLine($"Successfully performed operation.");
            return true;
        }
        catch (ConflictException ex)
        {
            _logger.LogError($"Failed to perform operation due to a conflict. {ex.Message}");
            return false;
        }
        catch (ResourceNotFoundException ex)
        {
            _logger.LogError($"Resource not found: {ex.Message}");
            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred: {ex.Message}");
            return false;
        }
    }
    // snippet-end:[{Service}.dotnetv4.OperationName]
}
// snippet-end:[{Service}.dotnetv4.{Service}Wrapper]
```

### Wrapper Method Guidelines
- Return `bool` for success/failure operations
- Return specific types for data retrieval operations
- Log errors using injected logger
- Display success messages to console
- Catch specific exceptions first, then general exceptions
- Include XML documentation for all public methods
- Use snippet tags for documentation extraction

## Error Handling

### Specification-Based Error Handling
The specification includes an "Errors" section with specific error codes and handling:

```csharp
// Example error handling based on specification
try
{
    var response = await _wrapper.CreateResourceAsync();
    return response;
}
catch (ConflictException ex)
{
    // Handle as specified: Resource already exists
    _logger.LogError($"Failed to create resource due to a conflict. {ex.Message}");
    return false;
}
catch (ResourceNotFoundException ex)
{
    // Handle as specified: Resource not found
    _logger.LogError($"Resource not found: {ex.Message}");
    return true; // May return true if deletion was the goal
}
catch (Exception ex)
{
    _logger.LogError($"An error occurred: {ex.Message}");
    return false;
}
```

### Workflow Error Handling
- Wrap main workflow in try-catch block
- Log errors and initiate cleanup on failure
- Set `_interactive = false` to skip prompts during error cleanup
- Ensure cleanup runs in finally block or after error

## Feature Scenario Requirements

### MUST HAVE
- ✅ Read and implement based on `scenarios/features/{service_feature}/SPECIFICATION.md`
- ✅ Use multi-project structure (Actions, Scenarios, Tests)
- ✅ Deploy CloudFormation stack for resource creation
- ✅ Retrieve and use stack outputs
- ✅ Use wrapper classes for all AWS operations
- ✅ Implement proper cleanup with stack deletion
- ✅ Break workflow into logical phases per specification
- ✅ Include error handling per specification
- ✅ Support non-interactive mode for testing
- ✅ Use file-level namespaces
- ✅ Include snippet tags for documentation

### Implementation Workflow

1. **Read Specification**: Study `scenarios/features/{service_feature}/SPECIFICATION.md`
2. **Create Project Structure**: Set up Actions, Scenarios, and Tests projects
3. **Implement Wrapper**: Create wrapper class with all required operations
4. **Implement Workflow**: Create workflow class with phases from specification
5. **Add CloudFormation**: Integrate stack deployment and deletion
6. **Add User Interaction**: Implement prompts and validation
7. **Test**: Create integration tests for workflow methods
8. **Document**: Add README.md with scenario description

## Integration Tests

### Single Integration Test Pattern

Integration tests should use a single test method that verifies no errors are logged:

```csharp
/// <summary>
/// Verifies the scenario with an integration test. No errors should be logged.
/// </summary>
/// <returns>Async task.</returns>
[Fact]
[Trait("Category", "Integration")]
public async Task TestScenarioIntegration()
{
    // Arrange
    {Service}Workflow._interactive = false;

    var loggerScenarioMock = new Mock<ILogger<{Service}Workflow>>();
    loggerScenarioMock.Setup(logger => logger.Log(
        It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
        It.IsAny<EventId>(),
        It.Is<It.IsAnyType>((@object, @type) => true),
        It.IsAny<Exception>(),
        It.IsAny<Func<It.IsAnyType, Exception?, string>>()));

    // Act
    {Service}Workflow._logger = loggerScenarioMock.Object;
    {Service}Workflow._wrapper = new {Service}Wrapper(
        new Amazon{Service}Client(),
        new Mock<ILogger<{Service}Wrapper>>().Object);
    {Service}Workflow._amazonCloudFormation = new AmazonCloudFormationClient();

    await {Service}Workflow.RunScenario();

    // Assert no errors logged
    loggerScenarioMock.Verify(logger => logger.Log(
        It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
        It.IsAny<EventId>(),
        It.Is<It.IsAnyType>((@object, @type) => true),
        It.IsAny<Exception>(),
        It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
        Times.Never);
}
```

### RunScenario Method

The workflow must include a public RunScenario method for testing:

```csharp
/// <summary>
/// Runs the scenario workflow. Used for testing.
/// </summary>
public static async Task RunScenario()
{
    Console.WriteLine(new string('-', 80));
    Console.WriteLine("Welcome to the {Service} Scenario.");
    Console.WriteLine(new string('-', 80));

    try
    {
        var prepareSuccess = await PrepareApplication();
        
        if (prepareSuccess)
        {
            await ExecutePhase2();
        }

        await Cleanup();
    }
    catch (Exception ex)
    {
        _logger.LogError(ex, "There was a problem with the scenario, initiating cleanup...");
        _interactive = false;
        await Cleanup();
    }

    Console.WriteLine("Scenario completed.");
}
```

### Specification Sections to Implement
- **API Actions Used**: All operations must be in wrapper class
- **Proposed example structure**: Maps to workflow phases
- **Setup**: CloudFormation deployment and resource creation
- **Demonstration**: Core service operations
- **Examination**: Data analysis and display
- **Cleanup**: Resource and stack deletion
- **Errors**: Specific error handling strategies