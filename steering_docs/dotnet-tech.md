# .NET Technology Stack & Build System

## .NET 8+ Development Environment

### Build Tools & Dependencies
- **Build System**: dotnet CLI
- **Package Manager**: NuGet
- **Testing Framework**: xUnit
- **Code Formatting**: dotnet-format
- **SDK Version**: AWS SDK for .NET v4
- **.NET Version**: .NET 8+

### Common Build Commands

```bash
# Build and Package
dotnet build SOLUTION.sln         # Build solution
dotnet build PROJECT.csproj       # Build specific project
dotnet clean                       # Clean build artifacts

# Testing
dotnet test                        # Run all tests
dotnet test --filter Category=Integration  # Run integration tests
dotnet test --logger trx           # Run tests with detailed output

# Execution
dotnet run                         # Run project
dotnet run --project PROJECT.csproj  # Run specific project

# Code Quality
dotnet format                      # Format code
```

### .NET-Specific Pattern Requirements

#### File Naming Conventions
- Use PascalCase for class names and file names
- Service prefix pattern: `{Service}Actions.cs` (e.g., `S3Actions.cs`)
- Hello scenarios: `Hello{Service}.cs` (e.g., `HelloS3.cs`)
- Test files: `{Service}Tests.cs`

#### Hello Scenario Structure
- **Class naming**: `Hello{Service}.cs` class with main method
- **Method structure**: Static Main method as entry point
- **Documentation**: Include XML documentation explaining the hello example purpose

#### Code Structure Standards
- **Namespace naming**: Use reverse domain notation (e.g., `Amazon.DocSamples.S3`)
- **Class structure**: One public class per file matching filename
- **Method naming**: Use PascalCase for method names
- **Properties**: Use PascalCase for property names
- **Constants**: Use PascalCase for constants
- **Async methods**: Suffix with `Async` (e.g., `ListBucketsAsync`)

#### Dependency Injection Patterns
```csharp
    /// <summary>
    /// Main entry point for the AWS Control Tower basics scenario.
    /// </summary>
    /// <param name="args">Command line arguments.</param>
    public static async Task Main(string[] args)
    {
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonControlTower>()
                .AddAWSService<IAmazonControlCatalog>()
                .AddAWSService<IAmazonOrganizations>()
                .AddAWSService<IAmazonSecurityTokenService>()
                .AddTransient<ControlTowerWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<ControlTowerBasics>();

        wrapper = host.Services.GetRequiredService<ControlTowerWrapper>();
        orgClient = host.Services.GetRequiredService<IAmazonOrganizations>();
        stsClient = host.Services.GetRequiredService<IAmazonSecurityTokenService>();

        await RunScenario();
    }
```

#### Error Handling Patterns
```csharp
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Threading.Tasks;

public class ExampleClass
{
    public async Task ExampleMethodAsync()
    {
        var s3Client = new AmazonS3Client();
        
        try
        {
            var response = await s3Client.ListBucketsAsync();
            // Process response
            Console.WriteLine($"Found {response.Buckets.Count} buckets");
        }
        catch (AmazonS3Exception e)
        {
            // Handle S3-specific exceptions
            Console.WriteLine($"S3 Error: {e.Message}");
            Console.WriteLine($"Error Code: {e.ErrorCode}");
            throw;
        }
        catch (Exception e)
        {
            // Handle general exceptions
            Console.WriteLine($"Error: {e.Message}");
            throw;
        }
        finally
        {
            s3Client?.Dispose();
        }
    }
}
```

#### Testing Standards
- **Test framework**: Use xUnit attributes (`[Fact]`, `[Theory]`)
- **Integration tests**: Mark with `[Trait("Category", "Integration")]`
- **Async testing**: Use `async Task` for async test methods
- **Resource management**: Use `using` statements for AWS clients
- **Test naming**: Use descriptive method names explaining test purpose

#### Project Structure
```
src/
├── {Service}Examples/
│   ├── Hello{Service}.cs
│   ├── {Service}Actions.cs
│   ├── {Service}Scenarios.cs
│   └── {Service}Examples.csproj
└── {Service}Examples.Tests/
    ├── {Service}Tests.cs
    └── {Service}Examples.Tests.csproj
```

#### Documentation Requirements
- **XML documentation**: Use `///` for class and method documentation
- **Parameter documentation**: Document all parameters with `<param>`
- **Return documentation**: Document return values with `<returns>`
- **Exception documentation**: Document exceptions with `<exception>`
- **README sections**: Include dotnet setup and execution instructions

### AWS Credentials Handling

#### Critical Credential Testing Protocol
- **CRITICAL**: Before assuming AWS credential issues, always test credentials first with `aws sts get-caller-identity`
- **NEVER** assume credentials are incorrect without verification
- If credentials test passes but .NET SDK fails, investigate SDK-specific credential chain issues
- Common .NET SDK credential issues: EC2 instance metadata service conflicts, credential provider chain order

### Build Troubleshooting

#### DotNetV4 Build Troubleshooting
- **CRITICAL**: When you get a response that the project file does not exist, use `listDirectory` to find the correct project/solution file path before trying to build again
- **NEVER** repeatedly attempt the same build command without first locating the actual file structure
- Always verify file existence with directory listing before executing build commands

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER create examples for dotnetv3 UNLESS explicitly instructed to by the user**
- ❌ **NEVER use camelCase for .NET class or method names**
- ❌ **NEVER forget to dispose AWS clients (use using statements)**
- ❌ **NEVER ignore proper exception handling for AWS operations**
- ❌ **NEVER skip NuGet package management**
- ❌ **NEVER assume credentials without testing first**
- ❌ **NEVER use other language folders for patterns**

### Best Practices
- ✅ **ALWAYS create examples in the dotnetv4 directory unless instructed otherwise**
- ✅ **ALWAYS follow the established .NET project structure**
- ✅ **ALWAYS use PascalCase for .NET identifiers**
- ✅ **ALWAYS use using statements for AWS client management**
- ✅ **ALWAYS include proper exception handling for AWS service calls**
- ✅ **ALWAYS test AWS credentials before assuming credential issues**
- ✅ **ALWAYS include comprehensive XML documentation**
- ✅ **ALWAYS use async/await patterns for AWS operations**
- ✅ **ALWAYS use dependency injection for AWS services**
- ✅ **ALWAYS create a separate class in the Actions project for the Hello example**
- ✅ **ALWAYS add project files to the main solution file DotNetV4Examples.sln**
- ✅ **ALWAYS put print statements in the action methods if possible**

### Project Configuration Requirements
- **Target Framework**: Specify appropriate .NET version in .csproj
- **AWS SDK packages**: Include specific AWS service NuGet packages
- **Test packages**: Include xUnit and test runner packages
- **Configuration**: Support for appsettings.json and environment variables

### Integration with Knowledge Base
Before creating .NET code examples:
1. Query `coding-standards-KB` for "DotNet-code-example-standards"
2. Query `DotNet-premium-KB` for "DotNet implementation patterns"
3. Follow KB-documented patterns for project structure and class organization
4. Validate against existing .NET examples only after KB consultation