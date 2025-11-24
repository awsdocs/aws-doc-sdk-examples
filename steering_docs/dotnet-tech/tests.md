# .NET Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "dotnet-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("DotNet-premium-KB", ".NET implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive test suites including unit tests and integration tests using xUnit framework with proper mocking and AWS data structures.

## Requirements
- **Complete Data**: Use complete AWS data structures in tests
- **Proper Attributes**: Use xUnit attributes for test categorization
- **Error Coverage**: Test all error conditions from specification
- **Async Testing**: Use async Task for async test methods

## File Structure
```
dotnetv4/{Service}/Tests/
├── {Service}Tests.csproj           # Test project file
├── {Service}Tests.cs               # Unit and integration tests
```

## Test Project Setup

### Step 1: Create Test Project File
```xml
<!-- dotnetv4/{Service}/Tests/{Service}Tests.csproj -->
<Project Sdk="Microsoft.NET.Sdk">

  <PropertyGroup>
    <TargetFramework>net8.0</TargetFramework>
    <ImplicitUsings>enable</ImplicitUsings>
    <Nullable>enable</Nullable>
    <IsPackable>false</IsPackable>
    <IsTestProject>true</IsTestProject>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="Microsoft.NET.Test.Sdk" Version="17.6.0" />
    <PackageReference Include="xunit" Version="2.4.2" />
    <PackageReference Include="xunit.runner.visualstudio" Version="2.4.5" />
    <PackageReference Include="coverlet.collector" Version="6.0.0" />
    <PackageReference Include="Moq" Version="4.20.69" />
    <PackageReference Include="AWSSDK.{Service}" Version="3.7.0" />
  </ItemGroup>

  <ItemGroup>
    <ProjectReference Include="..\Actions\{Service}Wrapper.csproj" />
    <ProjectReference Include="..\Scenarios\{Service}_Basics\{Service}Basics.csproj" />
  </ItemGroup>

</Project>
```

### Step 2: Create Test Class Structure
```csharp
// dotnetv4/{Service}/Tests/{Service}Tests.cs
using System;
using System.Threading.Tasks;
using Amazon.{Service};
using Amazon.{Service}.Model;
using Moq;
using Xunit;
using Xunit.Abstractions;

namespace Amazon.DocSamples.{Service}.Tests
{
    public class {Service}Tests
    {
        private readonly ITestOutputHelper _output;
        private readonly Mock<IAmazon{Service}> _mock{Service}Client;
        private readonly {Service}Wrapper _wrapper;

        public {Service}Tests(ITestOutputHelper output)
        {
            _output = output;
            _mock{Service}Client = new Mock<IAmazon{Service}>();
            _wrapper = new {Service}Wrapper(_mock{Service}Client.Object);
        }
    }
}
```

## Unit Test Pattern
```csharp
[Theory]
[InlineData(null)]
[InlineData("BadRequestException")]
[InlineData("InternalServerErrorException")]
public async Task Test{ActionName}Async_WithVariousConditions_ReturnsExpectedResult(string? errorCode)
{
    // Arrange
    var paramValue = "test-value";
    var expectedResponse = new {ActionName}Response
    {
        ResponseKey = "response-value"
    };
    
    if (errorCode == null)
    {
        _mock{Service}Client
            .Setup(x => x.{ActionName}Async(It.IsAny<{ActionName}Request>(), default))
            .ReturnsAsync(expectedResponse);
    }
    else
    {
        _mock{Service}Client
            .Setup(x => x.{ActionName}Async(It.IsAny<{ActionName}Request>(), default))
            .ThrowsAsync(new Amazon{Service}Exception(errorCode));
    }
    
    // Act & Assert
    if (errorCode == null)
    {
        var result = await _wrapper.{ActionName}Async(paramValue);
        Assert.Equal("response-value", result.ResponseKey);
    }
    else
    {
        var exception = await Assert.ThrowsAsync<Amazon{Service}Exception>(
            () => _wrapper.{ActionName}Async(paramValue));
        Assert.Equal(errorCode, exception.ErrorCode);
    }
}
```

## Complete AWS Data Structures

### CRITICAL: Use Complete AWS Response Data
```csharp
// ❌ WRONG - Minimal data that fails validation
var findings = new List<Finding>
{
    new Finding { Id = "finding-1", Type = "SomeType", Severity = 8.0 }
};

// ✅ CORRECT - Complete AWS data structure
var findings = new List<Finding>
{
    new Finding
    {
        Id = "finding-1",
        AccountId = "123456789012",
        Arn = "arn:aws:service:region:account:resource/id",
        Type = "SomeType",
        Severity = 8.0,
        CreatedAt = DateTime.Parse("2023-01-01T00:00:00.000Z"),
        UpdatedAt = DateTime.Parse("2023-01-01T00:00:00.000Z"),
        Region = "us-east-1",
        SchemaVersion = "2.0",
        Resource = new Resource { ResourceType = "Instance" }
    }
};
```

## Integration Test Pattern
Create a single integration test for the scenario by setting IsInteractive to false:

```csharp
using {Service}Basics;
using Microsoft.Extensions.Logging;
using Moq;
using Xunit;

namespace {Service}Tests;

/// <summary>
/// Integration tests for the Amazon {Service} Basics scenario.
/// </summary>
public class {Service}BasicsTest
{
    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>A task representing the asynchronous test operation.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenario()
    {
        // Arrange.
        {Service}Basics.IsInteractive = false;
        var loggerMock = new Mock<ILogger<{Service}Basics>>();
        loggerMock.Setup(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()));

        // Act.
        await {Service}Basics.Main(new string[] { "" });

        // Assert no exceptions or errors logged.
        loggerMock.Verify(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }
}
```

## Additional Integration Test Patterns (Optional)
If needed, you can add specific wrapper method tests:

```csharp
[Fact]
[Trait("Category", "Integration")]
public async Task Test{ActionName}Integration()
{
    // Arrange
    var wrapper = new {Service}Wrapper(new Amazon{Service}Client());
    
    // Act - This should not raise an exception
    var result = await wrapper.{ActionName}Async();
    
    // Assert - Verify result structure
    Assert.NotNull(result);
}

[Fact]
[Trait("Category", "Integration")]
public async Task TestResourceLifecycleIntegration()
{
    // Arrange
    var wrapper = new {Service}Wrapper(new Amazon{Service}Client());
    string? resourceId = null;
    
    try
    {
        // Act - Create resource
        resourceId = await wrapper.CreateResourceAsync();
        Assert.NotNull(resourceId);
        
        // Use resource
        var result = await wrapper.GetResourceAsync(resourceId);
        Assert.NotNull(result);
    }
    finally
    {
        // Clean up
        if (!string.IsNullOrEmpty(resourceId))
        {
            try
            {
                await wrapper.DeleteResourceAsync(resourceId);
            }
            catch
            {
                // Ignore cleanup errors
            }
        }
    }
}
```

## Test Execution Commands

### Unit Tests
```bash
dotnet test dotnetv4/{Service}/Tests/{Service}Tests.csproj --filter "Category!=Integration"
```

### Integration Tests (Runs Complete Scenario)
```bash
dotnet test dotnetv4/{Service}/Tests/{Service}Tests.csproj --filter Category=Integration
```

## Test Requirements Checklist
- ✅ **Test project file created** with proper dependencies
- ✅ **Mock framework setup** (Moq for mocking AWS clients and loggers)
- ✅ **Complete AWS data structures** in all tests
- ✅ **Proper xUnit attributes** (`[Trait("Category", "Integration")]`)
- ✅ **Error condition coverage** per specification
- ✅ **Integration test sets IsInteractive to false** for automated testing
- ✅ **Logger verification** to ensure no errors are logged
- ✅ **Async test methods** using `async Task`

## Integration Test Focus

### Primary Test: Complete Scenario Integration
The main integration test should:
- ✅ **Run the entire scenario** from start to finish
- ✅ **Set IsInteractive to false** to automate the interactive parts
- ✅ **Test against real AWS** services (not mocks)
- ✅ **Verify no errors are logged** using mock logger verification
- ✅ **Handle cleanup automatically** through scenario logic

### Test Structure Priority
1. **Integration Test** - Most important, tests complete workflow
2. **Unit Tests** - Test individual wrapper methods
3. **Additional Integration Tests** - Optional, for specific edge cases

## Common Test Failures to Avoid
- ❌ **Not setting IsInteractive to false** in scenario integration tests
- ❌ **Using incomplete AWS data structures** in unit tests
- ❌ **Missing xUnit attributes** for integration tests
- ❌ **Not verifying logger mock** to ensure no errors are logged
- ❌ **Forgetting to set AWS region** in test clients
- ❌ **Not testing all error conditions** from specification
- ❌ **Not calling Main method properly** in scenario integration tests
- ❌ **Missing proper async/await patterns** in test methods