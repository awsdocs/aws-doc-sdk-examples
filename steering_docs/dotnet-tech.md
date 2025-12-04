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
- **Namespace naming**: Use file-scoped namespaces (e.g., `namespace RedshiftActions;`)
- **Class structure**: One public class per file matching filename
- **Method naming**: Use PascalCase for method names
- **Properties**: Use PascalCase for property names
- **Constants**: Use PascalCase for constants
- **Async methods**: Suffix with `Async` (e.g., `ListBucketsAsync`)
- **Indentation**: Use 4 spaces (no tabs), proper indentation after file-scoped namespace
- **Target Framework**: .NET 8.0 (`<TargetFramework>net8.0</TargetFramework>`)
- **Language Version**: Latest (`<LangVersion>latest</LangVersion>`)
- **Snippet tags**: Use format `[{Service}.dotnetv4.{ActionName}]` (e.g., `[Redshift.dotnetv4.CreateCluster]`)

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
dotnetv4/{Service}/
├── Actions/
│   ├── Hello{Service}.cs          # Hello example (with Main method)
│   ├── {Service}Wrapper.cs        # Service wrapper class
│   └── {Service}Actions.csproj    # Actions project file
├── Scenarios/
│   ├── {Service}Basics.cs         # Basics scenario
│   └── {Service}Basics.csproj     # Scenarios project file
├── Tests/
│   ├── {Service}IntegrationTests.cs  # Integration tests only
│   └── {Service}Tests.csproj      # Test project file
└── {Service}Examples.sln          # Service-specific solution file
```

**CRITICAL Project Organization Rules:**
- ✅ **Hello examples MUST be in the Actions project** with a Main method
- ✅ **Integration tests ONLY** - no separate unit tests for wrapper methods
- ✅ **No separate Hello project** - Hello is part of Actions
- ✅ **No separate IntegrationTests project** - all tests in Tests project
- ✅ **Create a service-specific solution file** named {Service}Examples.sln
- ✅ **Solution must include**: Actions, Scenarios, and Tests projects only

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
- ✅ **ALWAYS use file-scoped namespaces** (namespace Name; instead of namespace Name { })
- ✅ **ALWAYS use using statements for AWS client management**
- ✅ **ALWAYS include proper exception handling for AWS service calls**
- ✅ **ALWAYS test AWS credentials before assuming credential issues**
- ✅ **ALWAYS include comprehensive XML documentation**
- ✅ **ALWAYS use async/await patterns for AWS operations**
- ✅ **ALWAYS use dependency injection for AWS services**
- ✅ **ALWAYS create Hello example in the Actions project** with a Main method
- ✅ **ALWAYS create a service-specific solution file** (e.g., Redshift.sln)
- ✅ **ALWAYS target .NET 8.0** with latest language version
- ✅ **ALWAYS put integration tests in the Tests project** (no separate IntegrationTests project)
- ✅ **ALWAYS put print statements in the action methods if possible**
- ✅ **ALWAYS update package versions** to avoid NU1603 warnings

### Project Configuration Requirements
- **Target Framework**: .NET 8.0 (`<TargetFramework>net8.0</TargetFramework>`)
- **Language Version**: Latest (`<LangVersion>latest</LangVersion>`)
- **AWS SDK packages**: Include specific AWS service NuGet packages (use latest versions)
- **Test packages**: Include MSTest and test runner packages
- **Configuration**: Support for appsettings.json and environment variables
- **Nullable**: Enable nullable reference types (`<Nullable>enable</Nullable>`)

### Solution File Management
Each service should have its own solution file in the service directory:

```bash
# Create solution file
dotnet new sln -n {Service}Examples -o dotnetv4/{Service}

# Add projects to solution
dotnet sln dotnetv4/{Service}/{Service}Examples.sln add dotnetv4/{Service}/Actions/{Service}Actions.csproj
dotnet sln dotnetv4/{Service}/{Service}Examples.sln add dotnetv4/{Service}/Scenarios/{Service}Basics.csproj
dotnet sln dotnetv4/{Service}/{Service}Examples.sln add dotnetv4/{Service}/Tests/{Service}Tests.csproj

# Build solution
dotnet build dotnetv4/{Service}/{Service}Examples.sln
```

**Solution Structure:**
- ✅ **3 projects only**: Actions, Scenarios, Tests
- ✅ **No solution folders** - flat structure
- ✅ **Service-specific naming**: {Service}Examples.sln (e.g., RedshiftExamples.sln)
- ✅ **Located in service directory**: dotnetv4/{Service}/{Service}Examples.sln

### Integration with Knowledge Base
Before creating .NET code examples:
1. Query `coding-standards-KB` for "DotNet-code-example-standards"
2. Query `DotNet-premium-KB` for "DotNet implementation patterns"
3. Follow KB-documented patterns for project structure and class organization
4. Validate against existing .NET examples only after KB consultation