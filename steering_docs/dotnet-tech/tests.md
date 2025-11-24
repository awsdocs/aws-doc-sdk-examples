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
Generate integration test suites using MSTest framework to validate complete scenario workflows against real AWS services.

## Requirements
- **Integration Tests Only**: Focus on end-to-end scenario testing, not unit tests
- **Real AWS Services**: Tests run against actual AWS infrastructure
- **Proper Attributes**: Use MSTest attributes for test categorization
- **Async Testing**: Use async Task for async test methods
- **Resource Cleanup**: Ensure proper cleanup of AWS resources after tests

## File Structure
```
dotnetv4/{Service}/Tests/
├── {Service}Tests.csproj              # Test project file
├── {Service}IntegrationTests.cs       # Integration tests
```

**CRITICAL:**
- ✅ **Integration tests ONLY** - no separate unit tests for wrapper methods
- ✅ **All tests in one project** - no separate IntegrationTests project
- ✅ **Use MSTest framework** - not xUnit
- ✅ **Test against real AWS** - not mocks

## Test Project Setup

### Step 1: Create Test Project File
```xml
<!-- dotnetv4/{Service}/Tests/{Service}Tests.csproj -->
<?xml version="1.0" encoding="utf-8"?>
<Project Sdk="Microsoft.NET.Sdk">
  <PropertyGroup>
    <TargetFramework>net8.0</TargetFramework>
    <IsPackable>false</IsPackable>
    <Nullable>enable</Nullable>
    <LangVersion>latest</LangVersion>
    <AssemblyName>{Service}Tests</AssemblyName>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="Microsoft.NET.Test.Sdk" Version="17.8.0" />
    <PackageReference Include="MSTest.TestAdapter" Version="3.1.1" />
    <PackageReference Include="MSTest.TestFramework" Version="3.1.1" />
    <PackageReference Include="coverlet.collector" Version="6.0.0" />
    <PackageReference Include="AWSSDK.{Service}" Version="3.7.401" />
    <PackageReference Include="AWSSDK.{ServiceDataAPI}" Version="3.7.401" />
  </ItemGroup>

  <ItemGroup>
    <ProjectReference Include="..\Actions\{Service}Actions.csproj" />
  </ItemGroup>

  <ItemGroup>
    <Content Include="settings.*.json">
      <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>
      <DependentUpon>settings.json</DependentUpon>
    </Content>
  </ItemGroup>
</Project>
```

**Key Changes:**
- ✅ Use MSTest packages instead of xUnit
- ✅ Target .NET 8.0 with latest language version
- ✅ Use latest AWS SDK package versions
- ✅ Reference Actions project only (no Scenarios reference needed)

### Step 2: Create Integration Test Class Structure
```csharp
// dotnetv4/{Service}/Tests/{Service}IntegrationTests.cs
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Threading.Tasks;
using Amazon.{Service};
using Amazon.{ServiceDataAPI};
using Microsoft.VisualStudio.TestTools.UnitTesting;
using {Service}Actions;

namespace {Service}Tests;

/// <summary>
/// Integration tests for Amazon {Service} operations.
/// These tests require actual AWS credentials and will create real AWS resources.
/// </summary>
[TestClass]
public class {Service}IntegrationTests
{
    private static {Service}Wrapper? _{service}Wrapper;
    private static string? _testResourceIdentifier;
    private const string TestDatabaseName = "dev";
    private const string TestUsername = "testuser";
    private const string TestPassword = "TestPassword123!";

    [ClassInitialize]
    public static void ClassInitialize(TestContext context)
    {
        // Initialize clients
        var {service}Client = new Amazon{Service}Client();
        var {service}DataClient = new Amazon{ServiceDataAPI}Client();
        _{service}Wrapper = new {Service}Wrapper({service}Client, {service}DataClient);
        
        // Generate unique resource identifier
        _testResourceIdentifier = $"test-resource-{DateTime.Now:yyyyMMddHHmmss}";
        
        Console.WriteLine($"Integration tests will use resource: {_testResourceIdentifier}");
    }

    [ClassCleanup]
    public static async Task ClassCleanup()
    {
        // Clean up any remaining test resources
        if (_{service}Wrapper != null && !string.IsNullOrEmpty(_testResourceIdentifier))
        {
            try
            {
                Console.WriteLine($"Cleaning up test resource: {_testResourceIdentifier}");
                await _{service}Wrapper.DeleteResourceAsync(_testResourceIdentifier);
                Console.WriteLine("Test resource cleanup initiated.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Warning: Failed to cleanup test resource: {ex.Message}");
            }
        }
    }
}
```

**Key Changes:**
- ✅ Use file-scoped namespaces
- ✅ Use MSTest attributes ([TestClass], [TestMethod], [ClassInitialize], [ClassCleanup])
- ✅ No mocking - test against real AWS services
- ✅ Include proper resource cleanup in ClassCleanup

## Integration Test Patterns

### Basic Integration Test
```csharp
[TestMethod]
[TestCategory("Integration")]
public async Task DescribeResources_Integration_ReturnsResourceList()
{
    // Act
    var resources = await _{service}Wrapper!.DescribeResourcesAsync();

    // Assert
    Assert.IsNotNull(resources);
    // Note: We don't assert specific count since other resources might exist
    Console.WriteLine($"Found {resources.Count} existing resources.");
}
```

### Full Workflow Integration Test
```csharp
[TestMethod]
[TestCategory("Integration")]
[TestCategory("LongRunning")]
public async Task {Service}FullWorkflow_Integration_CompletesSuccessfully()
{
    // This test runs the complete {Service} workflow
    // Note: This test can take 10-15 minutes to complete
    
    try
    {
        Console.WriteLine("Starting {Service} full workflow integration test...");
        
        // Step 1: Create resource
        Console.WriteLine($"Creating resource: {_testResourceIdentifier}");
        var createdResource = await _{service}Wrapper!.CreateResourceAsync(
            _testResourceIdentifier!, 
            TestDatabaseName, 
            TestUsername, 
            TestPassword);
        
        Assert.IsNotNull(createdResource);
        Assert.AreEqual(_testResourceIdentifier, createdResource.Identifier);
        Console.WriteLine("Resource creation initiated successfully.");

        // Step 2: Wait for resource to become available
        Console.WriteLine("Waiting for resource to become available...");
        await WaitForResourceAvailable(_testResourceIdentifier!, TimeSpan.FromMinutes(20));

        // Step 3: Perform operations
        Console.WriteLine("Performing operations...");
        var result = await _{service}Wrapper.PerformOperationAsync(_testResourceIdentifier!);
        Assert.IsNotNull(result);
        Console.WriteLine("Operations completed successfully.");

        Console.WriteLine("Full workflow integration test completed successfully!");
    }
    finally
    {
        // Clean up - Delete resource
        if (!string.IsNullOrEmpty(_testResourceIdentifier))
        {
            Console.WriteLine($"Deleting test resource: {_testResourceIdentifier}");
            try
            {
                var deletedResource = await _{service}Wrapper!.DeleteResourceAsync(_testResourceIdentifier);
                Assert.IsNotNull(deletedResource);
                Console.WriteLine("Resource deletion initiated successfully.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to delete resource: {ex.Message}");
                throw;
            }
        }
    }
}

/// <summary>
/// Wait for a resource to become available with timeout.
/// </summary>
/// <param name="resourceIdentifier">The resource identifier.</param>
/// <param name="timeout">Maximum time to wait.</param>
private async Task WaitForResourceAvailable(string resourceIdentifier, TimeSpan timeout)
{
    var startTime = DateTime.UtcNow;
    var endTime = startTime.Add(timeout);

    while (DateTime.UtcNow < endTime)
    {
        var resources = await _{service}Wrapper!.DescribeResourcesAsync(resourceIdentifier);
        
        if (resources.Count > 0 && resources[0].Status == "available")
        {
            Console.WriteLine($"Resource {resourceIdentifier} is now available!");
            return;
        }

        var elapsed = DateTime.UtcNow - startTime;
        Console.WriteLine($"Waiting for resource... Elapsed time: {elapsed:mm\\:ss}");
        
        await Task.Delay(TimeSpan.FromSeconds(30)); // Wait 30 seconds between checks
    }

    throw new TimeoutException($"Resource {resourceIdentifier} did not become available within {timeout.TotalMinutes} minutes.");
}
```

## Test Execution Commands

### All Tests
```bash
dotnet test dotnetv4/{Service}/{Service}Examples.sln
```

### Integration Tests Only
```bash
dotnet test dotnetv4/{Service}/Tests/{Service}Tests.csproj --filter TestCategory=Integration
```

### Exclude Long-Running Tests
```bash
dotnet test dotnetv4/{Service}/Tests/{Service}Tests.csproj --filter "TestCategory=Integration&TestCategory!=LongRunning"
```

## Test Requirements Checklist
- ✅ **Test project file created** with proper dependencies
- ✅ **MSTest framework** (not xUnit)
- ✅ **Integration tests only** (no unit tests with mocks)
- ✅ **Proper MSTest attributes** (`[TestCategory("Integration")]`)
- ✅ **Test against real AWS services** (not mocks)
- ✅ **Proper resource cleanup** in ClassCleanup method
- ✅ **Async test methods** using `async Task`
- ✅ **File-scoped namespaces** for modern C# style

## Integration Test Focus

### Primary Test: Complete Workflow Integration
The main integration test should:
- ✅ **Run the entire workflow** from start to finish
- ✅ **Test against real AWS services** (not mocks)
- ✅ **Create and clean up resources** properly
- ✅ **Handle long-running operations** with appropriate timeouts
- ✅ **Use TestCategory attributes** for test organization

### Test Structure Priority
1. **Full Workflow Integration Test** - Most important, tests complete workflow
2. **Basic Operation Tests** - Test individual operations against real AWS
3. **Resource Lifecycle Tests** - Test create, read, update, delete operations

## Common Test Failures to Avoid
- ❌ **Using mocks instead of real AWS services** in integration tests
- ❌ **Missing MSTest attributes** for integration tests
- ❌ **Not cleaning up resources** in ClassCleanup
- ❌ **Forgetting to set AWS region** in test clients
- ❌ **Not handling long-running operations** with timeouts
- ❌ **Missing proper async/await patterns** in test methods
- ❌ **Using xUnit instead of MSTest** framework
- ❌ **Creating separate IntegrationTests project** instead of using Tests project